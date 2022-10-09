package com.java110.gateway.video.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.RedisCacheFactory;
import com.java110.core.util.*;
import com.java110.entity.response.ResultDto;
import com.java110.entity.sip.Device;
import com.java110.entity.sip.DeviceChannel;
import com.java110.gateway.sip.*;
import com.java110.gateway.sip.callback.OnProcessListener;
import com.java110.gateway.sip.message.config.ConfigProperties;
import com.java110.gateway.sip.message.session.MessageManager;
import com.java110.gateway.sip.message.session.SyncFuture;
import com.java110.gateway.sip.remux.Observer;
import com.java110.gateway.sip.remux.RtmpPusher;
import com.java110.gateway.sip.session.PushStreamDeviceManager;
import com.java110.intf.inner.IVideoInnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sip.Dialog;
import javax.sip.SipException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class VideoInnerServiceImpl implements IVideoInnerService, OnProcessListener {

    private static Logger logger = LoggerFactory.getLogger(VideoInnerServiceImpl.class);


    @Autowired
    private SipLayer mSipLayer;

    @Autowired
    private ConfigProperties configProperties;

    private static final Map<String, SipPushStreamServer> pushStreamServer = new HashMap<>();


    private MessageManager mMessageManager = MessageManager.getInstance();

    private PushStreamDeviceManager mPushStreamDeviceManager = PushStreamDeviceManager.getInstance();


    @Override
    public ResponseEntity<String> doPlay(JSONObject paramIn) {
        ///play/{deviceId}/{channelId}/{mediaProtocol}
        Assert.hasKeyAndValue(paramIn, "deviceId", "未包含deviceId");
        Assert.hasKeyAndValue(paramIn, "channelId", "未包含channelId");
        Assert.hasKeyAndValue(paramIn, "port", "未包含port");
        Assert.hasKeyAndValue(paramIn, "mediaProtocol", "未包含mediaProtocol");

        logger.debug("收到播放视频请求，{}", paramIn.toJSONString());

        String deviceId = paramIn.getString("deviceId");
        String channelId = paramIn.getString("channelId");
        String mediaProtocol = paramIn.getString("mediaProtocol");

        JSONObject result = new JSONObject();
        try {
            //1.从redis查找设备，如果不存在，返回离线
            String deviceStr = RedisUtil.get(deviceId);
            if (StringUtils.isEmpty(deviceStr)) {
                //throw new IllegalArgumentException("设备离线");
                return ResultDto.error("设备离线");
            }
            //2.设备在线，先检查是否正在推流
            //如果正在推流，直接返回rtmp地址
            String streamName = StreamNameUtils.play(deviceId, channelId);
            PushStreamDevice pushStreamDevice = mPushStreamDeviceManager.get(streamName);
            if (pushStreamDevice != null) {
                String callId = RedisCacheFactory.getValue(pushStreamDevice.getCallId() + "_pushStream");

                if (!StringUtil.isEmpty(callId)) {
                    result.put("address", configProperties.getPullRtmpAddress().concat(streamName));
                    result.put("callId", pushStreamDevice.getCallId());
                    prolongedSurvival(pushStreamDevice.getCallId());
                    return ResultDto.createResponseEntity(result);
                } else {
                    try {
                        mSipLayer.sendBye(callId);
                    } catch (SipException e) {
                        e.printStackTrace();
                    }
                }
            }
            //检查通道是否存在
            Device device = JSONObject.parseObject(deviceStr, Device.class);
            Map<String, DeviceChannel> channelMap = device.getChannelMap();
            if (channelMap == null || !channelMap.containsKey(channelId)) {
                //throw new IllegalArgumentException("通道不存在");
                return ResultDto.error("通道不存在");
            }
            boolean isTcp = mediaProtocol.toUpperCase().equals(SipLayer.TCP);
            //3.下发指令
            String callId = IDUtils.id();
            //getPort可能耗时，在外面调用。
            //int port = mSipLayer.getPort(isTcp);
            int port = paramIn.getIntValue("port");
            String ssrc = mSipLayer.getSsrc(true);


            //启动服务 =================================
            String existsPushStreamServerKey = deviceId + "_" + channelId + "_" + mediaProtocol;
            //停掉老服务
            stopExistsPushStreamServer(existsPushStreamServerKey);

            String address = configProperties.getPushRtmpAddress().concat(streamName);
            Server server = isTcp ? new TCPServer() : new UDPServer();
            Observer observer = new RtmpPusher(address, callId);
            server.subscribe(observer);
            pushStreamDevice = new PushStreamDevice(deviceId, Integer.valueOf(ssrc), callId, streamName, port, isTcp, server,
                    observer, address);
            server.startServer(pushStreamDevice.getFrameDeque(), Integer.valueOf(ssrc), port, false);
            observer.startRemux();
            observer.setOnProcessListener(this);
            mPushStreamDeviceManager.put(streamName, callId, Integer.valueOf(ssrc), pushStreamDevice);

            //保存起来 如果 服务没有停止时手工停止一下服务，以免端口死掉
            pushStreamServer.put(existsPushStreamServerKey, new SipPushStreamServer(observer, server));
            //启动完成=====================================

            mSipLayer.sendInvite(device, SipLayer.SESSION_NAME_PLAY, callId, channelId, port, ssrc, isTcp);
            //4.等待指令响应
            SyncFuture<?> receive = mMessageManager.receive(callId);
            Dialog response = (Dialog) receive.get(3, TimeUnit.SECONDS);

            //4.1响应成功，创建推流session
            if (response != null) {
                pushStreamDevice.setDialog(response);
                //result = GBResult.ok(new MediaData(configProperties.getPullRtmpAddress().concat(streamName), pushStreamDevice.getCallId()));
                result.put("address", configProperties.getPullRtmpAddress().concat(streamName));
                result.put("callId", pushStreamDevice.getCallId());
                RedisCacheFactory.setValue(port + "_port", callId);

                prolongedSurvival(pushStreamDevice.getCallId());
            } else {
                server.stopServer();
                observer.stopRemux();
                //3.2响应失败，删除推流session
                mMessageManager.remove(callId);
                //throw new IllegalArgumentException("摄像头 指令未响应");
                return ResultDto.error("摄像头 指令未响应");
            }
        } catch (Exception e) {
            logger.error("系统异常", e);
            throw new IllegalArgumentException("系统异常");
        }
        return ResultDto.createResponseEntity(result);
    }

    /**
     * 停止已经存在的 接受流服务
     *
     * @param existsPushStreamServerKey
     */
    private void stopExistsPushStreamServer(String existsPushStreamServerKey) {

        if (!pushStreamServer.containsKey(existsPushStreamServerKey)) {
            return;

        }
        SipPushStreamServer sipPushStreamServer = pushStreamServer.get(existsPushStreamServerKey);
        if (sipPushStreamServer == null) {
            return;
        }
        if (sipPushStreamServer.getObserver() != null) {
            try {
                sipPushStreamServer.getObserver().stopRemux();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (sipPushStreamServer.getServer() != null) {
            try {
                sipPushStreamServer.getServer().stopServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        pushStreamServer.remove(existsPushStreamServerKey);
    }

    @Override
    public ResponseEntity<String> bye(JSONObject reqJson) {
        ///play/{deviceId}/{channelId}/{mediaProtocol}
        Assert.hasKeyAndValue(reqJson, "callId", "未包含callId");
        String callId = reqJson.getString("callId");
        try {
            mSipLayer.sendBye(callId);
        } catch (SipException e) {
            e.printStackTrace();
            return ResultDto.error(e.getLocalizedMessage());
        }
        return ResultDto.success();
    }

    @Override
    public ResponseEntity<String> heartbeatVideo(JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "callIds", "未包含callIds");
        String callIds = reqJson.getString("callIds");

        for (String callId : callIds.split(",")) {
            if (StringUtil.isEmpty(callId)) {
                continue;
            }
            prolongedSurvival(callId);
            String tmpCallId = RedisCacheFactory.getValue(callId + "_pushStream");
            if (StringUtil.isEmpty(tmpCallId)) {
                return ResultDto.createResponseEntity(new ResultDto(ResultDto.NO_PUSH_STREAM, "设备未推流"));
            }
        }


        return ResultDto.success();
    }

    @Override
    public void onError(String callId) {
        try {
            mSipLayer.sendBye(callId);
        } catch (SipException e) {
            e.printStackTrace();
        }
    }

    /**
     * 延长存活时间
     *
     * @param callId
     */
    private void prolongedSurvival(String callId) {
        RedisCacheFactory.setValue(callId + "_callId", callId, 60);

        String calls = RedisCacheFactory.getValue("VEDIO_CALLS");
        JSONArray callIds = null;
        if (!StringUtil.isEmpty(calls)) {
            callIds = JSONArray.parseArray(calls);
        } else {
            callIds = new JSONArray();
            callIds.add(callId);
        }

        if (!hasCallIn(callIds, callId)) {
            callIds.add(callId);
        }

        RedisCacheFactory.setValue("VEDIO_CALLS", callIds.toJSONString());
    }


    private boolean hasCallIn(JSONArray callIds, String callId) {
        String tmpCallId = "";
        for (int callIndex = 0; callIndex < callIds.size(); callIndex++) {
            tmpCallId = callIds.getString(callIndex);
            if (tmpCallId.equals(callId)) {
                return true;
            }
        }
        return false;
    }

}

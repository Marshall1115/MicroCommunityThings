package com.java110.gateway.api;


import com.alibaba.fastjson.JSONObject;
import com.java110.entity.response.ResultDto;
import com.java110.entity.sip.Device;
import com.java110.entity.sip.DeviceChannel;
import com.java110.gateway.sip.*;
import com.java110.gateway.sip.callback.OnProcessListener;

import com.java110.core.util.IDUtils;
import com.java110.core.util.RedisUtil;
import com.java110.core.util.StreamNameUtils;
import com.java110.gateway.sip.message.config.ConfigProperties;
import com.java110.gateway.sip.message.result.GBResult;
import com.java110.gateway.sip.message.result.MediaData;
import com.java110.gateway.sip.message.session.MessageManager;
import com.java110.gateway.sip.message.session.SyncFuture;
import com.java110.gateway.sip.remux.Observer;
import com.java110.gateway.sip.remux.RtmpPusher;
import com.java110.gateway.sip.session.PushStreamDeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.sip.Dialog;
import javax.sip.SipException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/api/video")
public class VideoController implements OnProcessListener {


    private static Logger logger = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private SipLayer mSipLayer;

    @Autowired
    private ConfigProperties configProperties;

    private MessageManager mMessageManager = MessageManager.getInstance();

    private PushStreamDeviceManager mPushStreamDeviceManager = PushStreamDeviceManager.getInstance();

    //String pushRtmpAddress
    @GetMapping("/play/{deviceId}/{channelId}/{mediaProtocol}")
    public GBResult doPlay(@PathVariable String deviceId,
                           @PathVariable String channelId,
                           @PathVariable String mediaProtocol) {
        GBResult result = null;
        try {
            //1.从redis查找设备，如果不存在，返回离线
            String deviceStr = RedisUtil.get(deviceId);
            if (StringUtils.isEmpty(deviceStr)) {
                //throw new IllegalArgumentException("设备离线");
                return GBResult.build(ResultDto.ERROR,"设备离线");
            }
            //2.设备在线，先检查是否正在推流
            //如果正在推流，直接返回rtmp地址
            String streamName = StreamNameUtils.play(deviceId, channelId);
            PushStreamDevice pushStreamDevice = mPushStreamDeviceManager.get(streamName);
            if (pushStreamDevice != null) {
                return GBResult.ok(new MediaData(configProperties.getPullRtmpAddress().concat(streamName), pushStreamDevice.getCallId()));
            }
            //检查通道是否存在
            Device device = JSONObject.parseObject(deviceStr, Device.class);
            Map<String, DeviceChannel> channelMap = device.getChannelMap();
            if (channelMap == null || !channelMap.containsKey(channelId)) {
                //throw new IllegalArgumentException("通道不存在");
                return GBResult.build(ResultDto.ERROR,"通道不存在");
            }
            boolean isTcp = mediaProtocol.toUpperCase().equals(SipLayer.TCP);
            //3.下发指令
            String callId = IDUtils.id();
            //getPort可能耗时，在外面调用。
            int port = mSipLayer.getPort();
            String ssrc = mSipLayer.getSsrc(true);
            mSipLayer.sendInvite(device, SipLayer.SESSION_NAME_PLAY, callId, channelId, port, ssrc, isTcp);
            //4.等待指令响应
            SyncFuture<?> receive = mMessageManager.receive(callId);
            Dialog response = (Dialog) receive.get(3, TimeUnit.SECONDS);

            //4.1响应成功，创建推流session
            if (response != null) {
                String address = configProperties.getPushRtmpAddress().concat(streamName);
                Server server = isTcp ? new TCPServer() : new UDPServer();
                Observer observer = new RtmpPusher(address, callId);

                server.subscribe(observer);
                pushStreamDevice = new PushStreamDevice(deviceId, Integer.valueOf(ssrc), callId, streamName, port, isTcp, server,
                        observer, address);

                pushStreamDevice.setDialog(response);
                server.startServer(pushStreamDevice.getFrameDeque(), Integer.valueOf(ssrc), port, false);
                observer.startRemux();

                observer.setOnProcessListener(this);
                mPushStreamDeviceManager.put(streamName, callId, Integer.valueOf(ssrc), pushStreamDevice);
                result = GBResult.ok(new MediaData(configProperties.getPullRtmpAddress().concat(streamName), pushStreamDevice.getCallId()));
            } else {
                //3.2响应失败，删除推流session
                mMessageManager.remove(callId);
                //throw new IllegalArgumentException("摄像头 指令未响应");
                return GBResult.build(ResultDto.ERROR,"摄像头 指令未响应");

            }

        } catch (Exception e) {
            logger.error("系统异常", e);
            throw new IllegalArgumentException("系统异常");
        }
        return result;
    }

    @RequestMapping("bye")
    @ResponseBody
    public GBResult bye(@RequestParam("callId") String callId) {
        try {
            mSipLayer.sendBye(callId);
        } catch (SipException e) {
            e.printStackTrace();
        }
        return GBResult.ok();
    }

    @Override
    public void onError(String callId) {
        try {
            mSipLayer.sendBye(callId);
        } catch (SipException e) {
            e.printStackTrace();
        }
    }
}

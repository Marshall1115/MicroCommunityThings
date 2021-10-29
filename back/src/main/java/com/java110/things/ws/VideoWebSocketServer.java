package com.java110.things.ws;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.service.video.IVideoService;
import com.java110.things.sip.SipLayer;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName WebSocketServer
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/25 12:13
 * @Version 1.0
 * add by wuxw 2020/5/25
 **/
@ServerEndpoint("/videoWebSocket/{clientId}/{deviceId}")
@Component
public class VideoWebSocketServer {

    private static Logger logger = LoggerFactory.getLogger(VideoWebSocketServer.class);

    @Autowired
    private IVideoService videoServiceImpl;

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, VideoWebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, String> clientMachineMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收clientId
     */
    private String clientId = "";

    private String deviceId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("clientId") String clientId, @PathParam("deviceId") String deviceId) {
        this.session = session;
        this.clientId = clientId;
        this.deviceId = deviceId;
        try {
            if (webSocketMap.containsKey(clientId)) {
                webSocketMap.remove(clientId);
                webSocketMap.put(clientId, this);
                //加入set中
            } else {
                webSocketMap.put(clientId, this);
                //加入set中
                addOnlineCount();
                //在线数加1
            }
            logger.debug("用户连接:" + clientId + ",当前在线人数为:" + getOnlineCount());
            //调用推流
            videoServiceImpl = ApplicationContextFactory.getBean("videoServiceImpl", IVideoService.class);
            ResponseEntity<String> responseEntity = videoServiceImpl.doPlay(deviceId, "34020000001320000010", SipLayer.TCP);
           // sendMessage(responseEntity.getBody());
        } catch (Exception e) {
            logger.error("用户:" + clientId + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(clientId)) {
            webSocketMap.remove(clientId);
            //从set中删除
            subOnlineCount();
        }
        logger.info("用户退出:" + clientId + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("用户消息:" + clientId + ",报文:" + message);
        //可以群发消息
        //消息保存到数据库、redis
        if (!StringUtil.isEmpty(message)) {
            try {
                //解析发送的报文
                JSONObject jsonObject = JSONObject.parseObject(message);
                //追加发送人(防止串改)
                jsonObject.put("fromUserId", this.clientId);
                String toUserId = jsonObject.getString("toUserId");
                //传送给对应toUserId用户的websocket
                if (!StringUtil.isEmpty(toUserId) && webSocketMap.containsKey(toUserId)) {
                    webSocketMap.get(toUserId).sendMessage(jsonObject.toJSONString());
                } else {
                    logger.error("请求的clientId:" + toUserId + "不在该服务器上");
                    //否则不在这个服务器上，发送到mysql或者redis
                }
            } catch (Exception e) {
                logger.error("接收客户端消息失败", e);
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("用户错误:" + this.clientId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(ByteBuffer byteBuffer) throws IOException {
        this.session.getBasicRemote().sendBinary(byteBuffer);
    }


    /**
     * 发送设备监控信息
     */
    public static void sendInfo(String message, String deviceId) throws IOException {
        logger.info("发送消息到:" + deviceId + "，报文:" + message);
        for (VideoWebSocketServer server : webSocketMap.values()) {
            if (deviceId.equals(server.deviceId)) {
                webSocketMap.get(server.clientId).sendMessage(message);
            }
        }
    }


    /**
     * 发送设备监控信息
     */
    public static void sendInfo(ByteBuffer byteBuffer, String deviceId) throws IOException {
        logger.info("发送消息到:" + deviceId + "，报文:" + byteBuffer);
        for (VideoWebSocketServer server : webSocketMap.values()) {
            if (deviceId.equals(server.deviceId)) {
                webSocketMap.get(server.clientId).sendMessage(byteBuffer);
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        VideoWebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        VideoWebSocketServer.onlineCount--;
    }
}

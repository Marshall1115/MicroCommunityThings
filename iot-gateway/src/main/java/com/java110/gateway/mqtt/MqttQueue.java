package com.java110.gateway.mqtt;

import com.java110.entity.mqtt.MqttMsgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MqttQueue {

    private static final Logger log = LoggerFactory.getLogger(MqttQueue.class);


    private static final BlockingQueue<MqttMsgDto> msgs = new LinkedBlockingQueue<MqttMsgDto>(100);

    /**
     * 添加消息到队列
     *
     * @param topic
     * @param msg
     */
    public static void addMsg(String topic, String msg) {
        try {
            MqttMsgDto mqttMsgDto = new MqttMsgDto();
            mqttMsgDto.setTopic(topic);
            mqttMsgDto.setMsg(msg);
            msgs.offer(mqttMsgDto,3, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("写入队列失败", e);
            e.printStackTrace();
        }
    }

    public static MqttMsgDto getMqttMsg() {
        try {
            return msgs.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

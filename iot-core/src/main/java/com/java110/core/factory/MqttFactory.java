package com.java110.core.factory;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName MqttFactory
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/20 20:42
 * @Version 1.0
 * add by wuxw 2020/5/20
 **/
public class MqttFactory {

    private static Logger logger = LoggerFactory.getLogger(MqttFactory.class);

    /**
     * 获取客户端
     *
     * @return
     */
    public static final MqttClient getMqttClient() {
        return ApplicationContextFactory.getBean("mqttClient", MqttClient.class);
    }


    public static final void distoryMqttConnect(){
        MqttClient mqttClient = getMqttClient();
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        try {
            mqttClient.close(true);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    /**
     * 订阅某个主题 qos默认为1
     *
     * @param topic
     */
    public static void subscribe(String topic) {
        subscribe(topic, 1);
    }

    /**
     * 订阅某个主题
     *
     * @param topic
     * @param qos
     */
    public static void subscribe(String topic, int qos) {
        MqttClient mqttClient = null;
        try {
            mqttClient = getMqttClient();
            if(mqttClient == null || !mqttClient.isConnected()){
                return ;
            }

            mqttClient.subscribe(topic, qos);
        } catch (Exception e) {
            logger.error("订阅失败", e);
        }
    }

    /**
     * 发布主题，用于通知<br>
     * 默认qos为1 非持久化
     *
     * @param topic
     * @param data
     */
    public static void publish(String topic, String data) {
        publish(topic, data, 1, false);
    }

    /**
     * 发布
     *
     * @param topic
     * @param data
     * @param qos
     * @param retained
     */
    public static void publish(String topic, String data, int qos, boolean retained) {

        logger.debug("推送信息 topic=" + topic + ",data=" + data);
        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setRetained(retained);
        message.setPayload(data.getBytes());
        MqttTopic mqttTopic = getMqttClient().getTopic(topic);
        if (null == mqttTopic) {
            logger.error("Topic Not Exist");
        }
        MqttDeliveryToken token;
        try {
            token = mqttTopic.publish(message);
           // token.waitForCompletion(5000);
            if(token.isComplete()) {
                logger.debug("消息发布成功");
            }
        } catch (Exception e) {
            logger.error("发布失败", e);
        }
    }
}

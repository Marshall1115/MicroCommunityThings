package com.java110.gateway.job;

import com.alibaba.fastjson.JSONArray;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.LocalCacheFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.factory.RedisCacheFactory;
import com.java110.core.util.StringUtil;
import com.java110.gateway.mqtt.MqttClientSubscribeFactory;
import com.java110.gateway.sip.SipLayer;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.SipException;

/**
 * @ClassName ClearExpireJwtThread
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 22:07
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public class CheckMqttConnetThread implements Runnable {
    Logger logger = LoggerFactory.getLogger(CheckMqttConnetThread.class);

    public static final long DEFAULT_WAIT_SECOND = 3 * 10 * 1000; // 默认5分钟执行一次


    @Override
    public void run() {
        while (true) {
            try {
                executeTask();
                Thread.sleep(DEFAULT_WAIT_SECOND);
            } catch (Throwable e) {
                logger.error("连接mqtt 失败", e);
            }
        }
    }

    private void executeTask() throws Exception{

        MqttClient mqttClient = MqttFactory.getMqttClient();


        if(mqttClient.isConnected()){
            logger.debug("mqtt 连接正常");
            return ;
        }

        mqttClient.reconnect();

        MqttClientSubscribeFactory.subscribe();

    }
}
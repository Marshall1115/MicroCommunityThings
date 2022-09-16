package com.java110.gateway.mqtt;

import com.java110.core.util.SeqUtil;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * @ClassName MqttReceiveConfig
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/20 16:10
 * @Version 1.0
 * @IntegrationComponentScan add by wuxw 2020/5/20
 **/
@Configuration
@AutoConfigureAfter(name = "manufacturerServiceImpl")
public class MqttConfig {

    @Value("${spring.mqtt.username}")
    private String username;

    @Value("${spring.mqtt.password}")
    private String password;

    @Value("${spring.mqtt.url}")
    private String hostUrl;

    @Value("${spring.mqtt.client.id}")
    private String clientId;

    @Value("${spring.mqtt.default.topic}")
    private String defaultTopic;

    @Value("${spring.mqtt.completionTimeout}")
    private int completionTimeout;   //连接超时

    @Value("${spring.mqtt.keepalive}")
    private int keepalive;

    @Value("${spring.mqtt.poolSize}")
    private int poolSize;


    @Bean
    @ConditionalOnBean(name = "manufacturerServiceImpl")
    public MqttClient mqttClient() {
        MqttClient client = null;
        //clientId = SeqUtil.getId();
        try {
            client = new MqttClient(hostUrl, clientId, new MemoryPersistence());
            //client = new MqttClient(hostUrl, clientId, new MemoryPersistence(), Executors.newScheduledThreadPool(poolSize));
            MqttConnectOptions option = new MqttConnectOptions();
            option.setCleanSession(true);
            option.setUserName(username);
            option.setPassword(password.toCharArray());
            option.setConnectionTimeout(completionTimeout);
            option.setKeepAliveInterval(keepalive);
            option.setAutomaticReconnect(true);

            client.setCallback(new MqttPushCallback(client, option));
            client.setTimeToWait(5000);
            client.connect(option);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }

    /**
     * 重新构造 MQTT 客户端
     * @return
     */
    public MqttClient createNewMqttClient(){
        return mqttClient();
    }


}

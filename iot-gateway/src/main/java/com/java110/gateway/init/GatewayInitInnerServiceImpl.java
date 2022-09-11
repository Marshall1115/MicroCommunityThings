package com.java110.gateway.init;

import com.java110.gateway.job.CheckMqttConnetThread;
import com.java110.gateway.job.ClearExpireJwtThread;
import com.java110.gateway.mqtt.MqttClientSubscribeFactory;
import com.java110.intf.inner.IGatewayInitInnerService;
import org.springframework.stereotype.Service;

@Service
public class GatewayInitInnerServiceImpl implements IGatewayInitInnerService {
    @Override
    public void init() {
        ClearExpireJwtThread clearExpireJwtThread = new ClearExpireJwtThread(true);
        Thread clearThread = new Thread(clearExpireJwtThread, "ClearExpireJwtThread");
        clearThread.start();

        CheckMqttConnetThread checkMqttConnetThread = new CheckMqttConnetThread();
        Thread mqttThread = new Thread(checkMqttConnetThread, "CheckMqttConnetThread");
        mqttThread.start();
    }

    @Override
    public void subscribeMqtt() {
        MqttClientSubscribeFactory.subscribe();
    }
}

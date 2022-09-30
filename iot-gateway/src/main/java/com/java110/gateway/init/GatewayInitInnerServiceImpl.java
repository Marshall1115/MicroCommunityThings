package com.java110.gateway.init;

import com.java110.gateway.job.CheckMqttConnectThread;
import com.java110.gateway.job.ClearExpireJwtThread;
import com.java110.gateway.job.CustomMqttMsgThread;
import com.java110.gateway.mqtt.MqttClientSubscribeFactory;
import com.java110.intf.inner.IGatewayInitInnerService;
import org.springframework.stereotype.Service;

import java.lang.reflect.Executable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class GatewayInitInnerServiceImpl implements IGatewayInitInnerService {
    @Override
    public void init() {


        //mqtt 消息消费线程
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.execute(new CustomMqttMsgThread());

        ClearExpireJwtThread clearExpireJwtThread = new ClearExpireJwtThread(true);
        Thread clearThread = new Thread(clearExpireJwtThread, "ClearExpireJwtThread");
        clearThread.start();

        CheckMqttConnectThread checkMqttConnectThread = new CheckMqttConnectThread();
        Thread mqttThread = new Thread(checkMqttConnectThread, "CheckMqttConnectThread");
        mqttThread.start();
    }

    @Override
    public void subscribeMqtt() {
        MqttClientSubscribeFactory.subscribe();
    }
}

package com.java110.gateway.job;

import com.alibaba.fastjson.JSONArray;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.LocalCacheFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.factory.RedisCacheFactory;
import com.java110.core.util.StringUtil;
import com.java110.gateway.mqtt.MqttClientSubscribeFactory;
import com.java110.gateway.mqtt.MqttConfig;
import com.java110.gateway.sip.SipLayer;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import javax.sip.SipException;

/**
 * @ClassName ClearExpireJwtThread
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 22:07
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public class CheckMqttConnectThread implements Runnable {
    Logger logger = LoggerFactory.getLogger(CheckMqttConnectThread.class);

    public static final long DEFAULT_WAIT_SECOND = 3 * 10 * 1000; // 默认5分钟执行一次

    private static final int DEFAULT_CONNECT_ERROR_NUM = 10;

    private static int errorNum = 0;

    @Override
    public void run() {
        while (true) {
            try {
                executeTask();
                Thread.sleep(DEFAULT_WAIT_SECOND);
            } catch (Throwable e) {
                logger.error("连接mqtt 失败", e);
                errorNum  +=1;

                if(errorNum < DEFAULT_CONNECT_ERROR_NUM){
                    continue;
                }
                //触发 重新构造实例
                recreateMqttClient();
            }
        }
    }

    public void recreateMqttClient(){
        try {
            MqttFactory.distoryMqttConnect();
            MqttConfig mqttConfig = ApplicationContextFactory.getBean("mqttConfig", MqttConfig.class);
            MqttClient mqttClient = mqttConfig.createNewMqttClient();
            DefaultListableBeanFactory defaultListableBeanFactory =
                    (DefaultListableBeanFactory) ApplicationContextFactory.getApplicationContext().getAutowireCapableBeanFactory();
            //销毁指定实例 execute是上文注解过的实例名称 name="execute"
            defaultListableBeanFactory.destroySingleton("mqttClient");
            //按照旧有的逻辑重新获取实例,Excute是我自己逻辑中的类
            //重新注册同名实例，这样在其他地方注入的实例还是同一个名称，但是实例内容已经重新加载
            defaultListableBeanFactory.registerSingleton("mqttClient", mqttClient);
            MqttClientSubscribeFactory.subscribe();

            errorNum = 0;
        }catch (Throwable e){
            logger.error("重新初始化mqtt 失败", e);

        }
    }


    private void executeTask() throws Exception{

        MqttClient mqttClient = MqttFactory.getMqttClient();


        if(mqttClient.isConnected()){
            logger.debug("mqtt 连接正常");
            errorNum = 0;
            return ;
        }

        logger.debug("mqtt 连接异常 重新连接");

        mqttClient.reconnect();

        MqttClientSubscribeFactory.subscribe();

    }
}
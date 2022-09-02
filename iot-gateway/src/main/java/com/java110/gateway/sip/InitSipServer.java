package com.java110.gateway.sip;


import com.java110.gateway.sip.message.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ConfigProperties.class)
public class InitSipServer {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Value("${sip.listenIp}")
    private String listenIp;

    @Value("${sip.listenPort}")
    private int listenPort;

    @Value("${sip.sipId}")
    private String sipId;

    @Value("${sip.sipRealm}")
    private String sipRealm;

    @Value("${sip.password}")
    private String password;

    @Value("${sip.streamMediaIp}")
    private String streamMediaIp;

    @Value("${sip.streamMediaPort}")
    private int streamMediaPort;

    /**
     * 初始化 层
     * @return
     */
    @Bean
    public SipLayer sipLayer() {
        SipLayer sipLayer = new SipLayer(sipId, sipRealm, password, listenIp, listenPort, streamMediaIp,streamMediaPort);
        boolean startStatus = sipLayer.startServer();
        if (startStatus) {
            logger.info("Sip Server 启动成功 port {}", listenPort);
        } else {
            logger.info("Sip Server 启动失败");
        }
        return sipLayer;
    }
}

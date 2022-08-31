package com.java110.gateway.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wuxw on 2018/4/15.
 */
@Configuration
public class IotServiceBean {
    @Bean
    public IotServiceKafka listener() {
        return new IotServiceKafka();
    }


}

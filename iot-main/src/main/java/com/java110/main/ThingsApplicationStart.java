/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.main;

import com.java110.core.config.Java110Properties;
import com.java110.core.init.ServiceStartInit;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;

/**
 *
 * HC小区管理系统 附属项目 物联网系统启动类
 *
 * @ClassName ThingsApplicationStart
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/10 17:02
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
@MapperScan("com.java110.core.dao")
@SpringBootApplication(scanBasePackages = "com.java110")
@EnableScheduling
@EnableAsync
public class ThingsApplicationStart implements WebMvcConfigurer {

    private static Logger logger = LoggerFactory.getLogger(ThingsApplicationStart.class);


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/face/**").addResourceLocations(
                "file:" + Java110Properties.getCloudFacePath());
        logger.debug("自定义静态资源目录、此处功能用于文件映射" + Java110Properties.getCloudFacePath());
    }

    /**
     * 实例化RestTemplate，通过@LoadBalanced注解开启均衡负载能力.
     *
     * @return restTemplate
     */
    @Bean
    //@LoadBalanced
    public RestTemplate restTemplate() {
        StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build();
//        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
//        httpRequestFactory.setConnectionRequestTimeout(3000);
//        httpRequestFactory.setConnectTimeout(3000);
//        httpRequestFactory.setReadTimeout(3000);
//        restTemplate.setRequestFactory(httpRequestFactory);
        return restTemplate;


    }

    @Bean
    //@LoadBalanced
    public RestTemplate outRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(3000);
        httpRequestFactory.setConnectTimeout(3000);
        httpRequestFactory.setReadTimeout(3000);
        restTemplate.setRequestFactory(httpRequestFactory);
        return restTemplate;
    }


    public static void main(String[] args) {
        try {
            ApplicationContext context = SpringApplication.run(ThingsApplicationStart.class, args);
            ServiceStartInit.initSystemConfig(context);
        }catch (Throwable e){
            logger.error("系统启动失败",e);
        }
    }
}

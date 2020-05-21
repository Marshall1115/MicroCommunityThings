package com.java110.things;

import com.java110.things.config.Java110Properties;
import com.java110.things.init.ServiceStartInit;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;

/**
 * @ClassName ThingsApplicationStart
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/10 17:02
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
@MapperScan("com.java110.things.dao")
@SpringBootApplication
public class ThingsApplicationStart implements WebMvcConfigurer {

    private static Logger logger = LoggerFactory.getLogger(ThingsApplicationStart.class);


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/face/**").addResourceLocations(
                "file:" + Java110Properties.getCloudFacePath());
        logger.debug("自定义静态资源目录、此处功能用于文件映射");
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

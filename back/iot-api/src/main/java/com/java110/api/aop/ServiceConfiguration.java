package com.java110.api.aop;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wuxw on 2018/5/2.
 */
@Configuration
public class ServiceConfiguration {
    @Bean
    public FilterRegistrationBean jwtFilter() {
        StringBuffer exclusions = new StringBuffer();
        exclusions.append("/api/user/login,");
        exclusions.append("/api/data/*,");
        exclusions.append("/api/car/*,");
        exclusions.append("/api/video/*,");
        exclusions.append("/api/accessControl/faceResult,");
        exclusions.append("/api/accessControl/faceResultBisen,");
        exclusions.append("/api/accessControl/faceResult/*,");
        exclusions.append("/api/accessControl/heartBeat,");
        exclusions.append("/api/accessControl/heartBeat/*");
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtFilter());
        //registrationBean.addUrlPatterns("/");
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.addInitParameter("excludedUri", exclusions.toString());

        return registrationBean;
    }

}

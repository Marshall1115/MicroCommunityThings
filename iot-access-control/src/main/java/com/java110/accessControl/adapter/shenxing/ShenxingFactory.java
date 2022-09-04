package com.java110.accessControl.adapter.shenxing;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.LocalCacheFactory;
import com.java110.core.factory.MappingCacheFactory;
import com.java110.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class ShenxingFactory {

    static Logger logger = LoggerFactory.getLogger(ShenxingFactory.class);

    public static final String token = "7VOarATI4IfbqFWLF38VdWoAbHUYlpAY";

    public static HttpHeaders getHeader(String sxdmSn, RestTemplate outRestTemplate) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Cookie", getToken(sxdmSn, outRestTemplate));
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
        httpHeaders.add("sxdmToken", token);
        httpHeaders.add("sxdmSn", sxdmSn);
       
        return httpHeaders;
    }

    public static String getToken(String sxdmSn, RestTemplate outRestTemplate) {

        String token = LocalCacheFactory.getValue(sxdmSn + "sxdmsn_token");
        if (!StringUtil.isEmpty(token)) {
            return token;
        }

        // 查询sign
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("sxdmToken", MappingCacheFactory.getValue("sxdmToken"));
        httpHeaders.add("sxdmSn", sxdmSn);
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("Shenxing_URL") + "/api/auth/login/challenge?username=admin", HttpMethod.GET, httpEntity, String.class);
        } catch (HttpStatusCodeException e) { //这里spring 框架 在4XX 或 5XX 时抛出 HttpServerErrorException 异常，需要重新封装一下
            logger.error("请求下游服务【" + MappingCacheFactory.getValue("Shenxing_URL") + "/api/auth/login/challenge?username=admin" + "】异常，参数为" + httpEntity + e.getResponseBodyAsString(), e);
            throw e;
        }
        logger.debug("请求地址：" + MappingCacheFactory.getValue("Shenxing_URL") + "/api/auth/login/challenge?username=admin" + "请求信息：" + httpEntity + ",返回参数：" + responseEntity);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求sign失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (!paramOut.containsKey("session_id")) {
            throw new IllegalStateException("请求sign失败" + responseEntity);

        }

        LocalCacheFactory.setValue(sxdmSn + "sxdmsn_token", paramOut.getString("session_id"), 25);
        return token;
    }
}

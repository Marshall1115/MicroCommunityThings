package com.java110.intf.inner;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;

public interface IVideoInnerService {

    /**
     * 请求播放视频
     *
     * @param reqJson
     * @return
     */
    ResponseEntity<String> doPlay(JSONObject reqJson);


    /**
     * 暂停推流
     *
     * @param reqJson
     * @return
     */
    ResponseEntity<String> bye(JSONObject reqJson);
    ResponseEntity<String> heartbeatVideo(JSONObject reqJson);
}

package com.java110.entity.mqtt;

import java.io.Serializable;

public class MqttMsgDto implements Serializable{

    private String topic;
    private String msg;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

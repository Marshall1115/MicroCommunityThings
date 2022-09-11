package com.java110.entity.mqtt;

import java.io.Serializable;

public class MqttLogDto implements Serializable {

    private String mqttId;
    private String businessId;
    private String topic;
    private String context;
    private String remark;
    private String businessKey;

    public String getMqttId() {
        return mqttId;
    }

    public void setMqttId(String mqttId) {
        this.mqttId = mqttId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
}

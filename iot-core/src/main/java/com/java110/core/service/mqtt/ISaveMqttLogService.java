package com.java110.core.service.mqtt;

import com.java110.entity.mqtt.MqttLogDto;

public interface ISaveMqttLogService {
    void saveMqttLog(MqttLogDto mqttLogDto);
}

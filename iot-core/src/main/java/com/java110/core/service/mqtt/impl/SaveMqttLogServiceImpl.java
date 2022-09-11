package com.java110.core.service.mqtt.impl;

import com.java110.core.dao.IMqttLogDao;
import com.java110.core.service.mqtt.ISaveMqttLogService;
import com.java110.entity.mqtt.MqttLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SaveMqttLogServiceImpl implements ISaveMqttLogService {

    @Autowired
    private IMqttLogDao mqttLogDao;

    @Async
    @Override
    public void saveMqttLog(MqttLogDto mqttLogDto) {

        mqttLogDao.saveMqttLog(mqttLogDto);

    }
}

package com.java110.core.dao;

import com.java110.entity.mqtt.MqttLogDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IMqttLogDao {

    /**
     * 保存mqtt 日志
     *
     * @param mqttLogDto 设备信息
     * @return 返回影响记录数
     */
    int saveMqttLog(MqttLogDto mqttLogDto);
}

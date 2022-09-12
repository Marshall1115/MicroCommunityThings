package com.java110.core.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.service.mqtt.ISaveMqttLogService;
import com.java110.core.util.SeqUtil;
import com.java110.core.util.StringUtil;
import com.java110.entity.mqtt.MqttLogDto;

public class MqttLogFactory {

    public static void saveReceiveLog(String taskId, String topic, String param) {

        String mqttLogSwitch = MappingCacheFactory.getValue("MQTT_LOG_SWITCH");

        if(!"ON".equals(mqttLogSwitch)){
            return ;
        }

        ISaveMqttLogService saveMqttLogServiceImpl = ApplicationContextFactory.getBean("saveMqttLogServiceImpl", ISaveMqttLogService.class);

        MqttLogDto mqttLogDto = new MqttLogDto();
        mqttLogDto.setMqttId(SeqUtil.getId());
        mqttLogDto.setBusinessId(taskId);
        mqttLogDto.setBusinessKey(getBusinessKey(topic, param));
        mqttLogDto.setContext(param);
        mqttLogDto.setTopic(topic);
        mqttLogDto.setRemark("接受");
        saveMqttLogServiceImpl.saveMqttLog(mqttLogDto);

    }

    /**
     * {
     * 	"AlarmInfoPlate": {
     * 		"serialno": "69ee31dd-462720e8",
     * 		"result": {
     * 			"PlateResult": {
     * 				"isoffline": 0,
     * 				"plateid": 1624,
     * 				"license": "粤S7Y12C",
     * 					"type": 0
     *                },
     * 				"imagePath": "picture/0/69ee31dd-462720e8/20220912/04/20220912_041503_361.jpg"* 			}
     *        }* 	}
     * }
     * @param topic
     * @param param
     * @return
     */
    private static String getBusinessKey(String topic, String param) {

        if (!"/device/push/result".equals(topic)) {
            return "其他业务";
        }

        JSONObject paramIn = JSONObject.parseObject(param);
        if (!paramIn.containsKey("AlarmInfoPlate")) {
            return "其他业务";
        }

        JSONObject alarmInfoPlateObj = paramIn.getJSONObject("AlarmInfoPlate");

        if (alarmInfoPlateObj.containsKey("heartbeat")) {
            return "心跳";
        }

        if(!alarmInfoPlateObj.containsKey("result")){
            return "其他业务";
        }

        JSONObject result = alarmInfoPlateObj.getJSONObject("result");

        if(!result.containsKey("PlateResult")){
            return "其他业务";
        }

        JSONObject plateResult = result.getJSONObject("PlateResult");

        String serialno = plateResult.getString("license");
        if (StringUtil.isEmpty(serialno)) {
            return "其他业务";
        }



        return serialno;
    }

    /**
     * 保存发送数据
     * @param taskId
     * @param carNum
     * @param topic
     * @param param
     */
    public static void saveSendLog(String taskId, String carNum, String topic, String param) {

        String mqttLogSwitch = MappingCacheFactory.getValue("MQTT_LOG_SWITCH");

        if(!"ON".equals(mqttLogSwitch)){
            return ;
        }

        ISaveMqttLogService saveMqttLogServiceImpl = ApplicationContextFactory.getBean("saveMqttLogServiceImpl", ISaveMqttLogService.class);

        MqttLogDto mqttLogDto = new MqttLogDto();
        mqttLogDto.setMqttId(SeqUtil.getId());
        mqttLogDto.setBusinessId(taskId);
        mqttLogDto.setBusinessKey(carNum);
        mqttLogDto.setContext(param);
        mqttLogDto.setTopic(topic);
        mqttLogDto.setRemark("发送");
        saveMqttLogServiceImpl.saveMqttLog(mqttLogDto);

    }
}

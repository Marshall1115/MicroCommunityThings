package com.java110.gateway.job;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.*;
import com.java110.core.service.machine.IMachineService;
import com.java110.core.service.manufacturer.IManufacturerService;
import com.java110.core.util.Assert;
import com.java110.core.util.SeqUtil;
import com.java110.core.util.StringUtil;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.manufacturer.ManufacturerAttrDto;
import com.java110.entity.manufacturer.ManufacturerDto;
import com.java110.entity.mqtt.MqttMsgDto;
import com.java110.gateway.mqtt.MqttQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 消耗消息队列
 */
public class CustomMqttMsgThread implements Runnable{

    private static final Logger log = LoggerFactory.getLogger(CustomMqttMsgThread.class);


    @Override
    public void run() {
        try {
            doDealMqttMsg();
        }catch (Exception e){
            log.error("处理消息异常",e);
            e.printStackTrace();
        }
    }

    private void doDealMqttMsg() throws Exception {

       MqttMsgDto mqttMsgDto =  MqttQueue.getMqttMsg();

       if(mqttMsgDto == null){
           return;
       }

        String taskId = SeqUtil.getId();
        MqttLogFactory.saveReceiveLog(taskId, mqttMsgDto.getTopic(), mqttMsgDto.getMsg());

        IManufacturerService manufacturerServiceImpl = ApplicationContextFactory.getBean("manufacturerServiceImpl", IManufacturerService.class);
        ManufacturerAttrDto tmpManufacturerDto = new ManufacturerAttrDto();
        tmpManufacturerDto.setSpecCd(ManufacturerAttrDto.SPEC_TOPIC);
        tmpManufacturerDto.setValue(mqttMsgDto.getTopic());
        List<ManufacturerAttrDto> manufacturerAttrDtos = manufacturerServiceImpl.getManufacturerAttr(tmpManufacturerDto);

        if (manufacturerAttrDtos == null || manufacturerAttrDtos.size() < 1) {
            if ("/device/push/result".equals(mqttMsgDto.getTopic())) { //臻识的摄像头
                CarMachineProcessFactory.getCarImpl("17").mqttMessageArrived(taskId,mqttMsgDto.getTopic(), mqttMsgDto.getMsg());
            } else {
                String hmId = getHmId(mqttMsgDto.getTopic(), mqttMsgDto.getMsg());
                if ("18".contains(hmId)) {
                    AttendanceProcessFactory.getAttendanceProcessImpl(hmId).mqttMessageArrived(taskId,mqttMsgDto.getTopic(), mqttMsgDto.getMsg());
                } else {
                    AccessControlProcessFactory.getAssessControlProcessImpl(getHmId(mqttMsgDto.getTopic(), mqttMsgDto.getMsg())).mqttMessageArrived(taskId,mqttMsgDto.getTopic(), mqttMsgDto.getMsg());
                }
            }
            return;
        }

        for (ManufacturerAttrDto manufacturerAttrDto : manufacturerAttrDtos) {
            if (ManufacturerDto.HM_TYPE_ACCESS_CONTROL.equals(manufacturerAttrDto.getHmType())) {
                AccessControlProcessFactory.getAssessControlProcessImpl(manufacturerAttrDto.getHmId()).mqttMessageArrived(taskId,mqttMsgDto.getTopic(), mqttMsgDto.getMsg());
            } else if (ManufacturerDto.HM_TYPE_CAR.equals(manufacturerAttrDto.getHmType())) {
                CarMachineProcessFactory.getCarImpl(manufacturerAttrDto.getHmId()).mqttMessageArrived(taskId,mqttMsgDto.getTopic(), mqttMsgDto.getMsg());
            } else if (ManufacturerDto.HM_TYPE_ATTENDANCE.equals(manufacturerAttrDto.getHmType())) {
                AttendanceProcessFactory.getAttendanceProcessImpl(manufacturerAttrDto.getHmId()).mqttMessageArrived(taskId,mqttMsgDto.getTopic(), mqttMsgDto.getMsg());
            }
        }
    }


    /**
     * 获取协议
     *
     * @param topic
     * @param message
     * @return
     */
    private String getHmId(String topic, String message) {

        if (topic.contains("hiot")) {
            return "18";
        }

        //德安中获取
        String hmId = getHmIdByDean(topic);
        if (!StringUtil.isEmpty(hmId)) {
            return hmId;
        }

        //伊兰度中获取协议
        hmId = getHmIdByYld(topic, message);


        return hmId;

    }

    /**
     * 伊兰度中获取设备ID
     *
     * @param topic   face.{sn}.response
     * @param msg
     * @return
     */
    private String getHmIdByYld(String topic, String msg) {

        if (!Assert.isJsonObject(msg)) {
            return "";
        }

        String hmId = "";

        JSONObject msgObj = JSONObject.parseObject(msg);
        String machineCode = "";
        if (msgObj.containsKey("sn")) {
            machineCode = msgObj.getString("sn");
        }

        if (msgObj.containsKey("body")) {
            machineCode = msgObj.getJSONObject("body").getString("sn");
        }

        IMachineService machineService = ApplicationContextFactory.getBean("machineServiceImpl", IMachineService.class);

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineCode);
        try {
            List<MachineDto> machineDtos = machineService.queryMachines(machineDto);

            Assert.listOnlyOne(machineDtos, "未找到设备信息");

            hmId = machineDtos.get(0).getHmId();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        return hmId;
    }

    /**
     * 德安门禁获取
     *
     * @param topic mqtt/face/ID/Rec mqtt/face/ID/Snap mqtt/face/ID/QRCode mqtt/face/ID/Ack
     * @return
     */
    private String getHmIdByDean(String topic) {
        String hmId = "";
        //判断是否为德安心跳
        if ("mqtt/face/heartbeat".equals(topic)) {
            return "6";//德安协议 非常抱歉 topic中不带设备编号 神也没办法用设备编码去查协议了，只能写死了 尴尬的一B
        }
        if (!topic.endsWith("Rec") && !topic.endsWith("Snap") && !topic.endsWith("QRCode") && !topic.endsWith("Ack")) {
            return hmId;
        }

        if (topic.length() < 10) {
            return hmId;
        }

        String machineCode = topic.substring(10, topic.lastIndexOf("/"));

        IMachineService machineService = ApplicationContextFactory.getBean("machineServiceImpl", IMachineService.class);

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineCode);
        try {
            List<MachineDto> machineDtos = machineService.queryMachines(machineDto);

            Assert.listOnlyOne(machineDtos, "未找到设备信息");

            hmId = machineDtos.get(0).getHmId();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        return hmId;
    }
}

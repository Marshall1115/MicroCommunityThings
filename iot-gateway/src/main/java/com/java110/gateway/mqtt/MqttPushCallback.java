package com.java110.gateway.mqtt;


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
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * MQTT 推送回调
 *
 * @author wuxw
 * @date 2020-05-20
 */
public class MqttPushCallback implements MqttCallbackExtended {

    private static final Logger log = LoggerFactory.getLogger(MqttPushCallback.class);
    private MqttClient client;

    private MqttConnectOptions option;

    public MqttPushCallback() {

    }

    public MqttPushCallback(MqttClient client, MqttConnectOptions option) {
        this.client = client;
        this.option = option;
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.debug("断开连接，建议重连" + this);
        log.error("连接断开", cause);
        while (true) {
            try {

                if (client.isConnected()) {
                    log.debug("MqttPushCallback:==========================>mqtt connect success!!!<=============================================");
                    break;
                }
                log.debug("MqttPushCallback:==========================>mqtt connect error, try reconnect<=============================================");
                // 重新连接
                client.reconnect();
                //client.reconnect();
                //重新订阅消息
                MqttClientSubscribeFactory.subscribe();
                Thread.sleep(3000);
            } catch (Throwable e) {
                e.printStackTrace();
                //continue;
                try {
                    Thread.sleep(3000);
                } catch (Throwable ex) {
                    e.printStackTrace();
                }
            }

        }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //log.info(token.isComplete() + "");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        try {
            log.debug("====================================>Topic: " + topic);
            log.debug("=====================================>Message: " + new String(message.getPayload()));
//            MqttQueue.addMsg(topic,new String(message.getPayload()));
            String taskId = SeqUtil.getId();
            MqttLogFactory.saveReceiveLog(taskId, topic, new String(message.getPayload()));

            //先去 topic 表中查询
            IManufacturerService manufacturerServiceImpl = ApplicationContextFactory.getBean("manufacturerServiceImpl", IManufacturerService.class);
            ManufacturerAttrDto tmpManufacturerDto = new ManufacturerAttrDto();
            tmpManufacturerDto.setSpecCd(ManufacturerAttrDto.SPEC_TOPIC);
            tmpManufacturerDto.setValue(topic);
            List<ManufacturerAttrDto> manufacturerAttrDtos = manufacturerServiceImpl.getManufacturerAttr(tmpManufacturerDto);

            if (manufacturerAttrDtos == null || manufacturerAttrDtos.size() < 1) {
                if ("/device/push/result".equals(topic)) { //臻识的摄像头
                    CarMachineProcessFactory.getCarImpl("17").mqttMessageArrived(taskId,topic, new String(message.getPayload()));
                } else {
                    String hmId = getHmId(topic, message);
                    if ("18".contains(hmId)) {
                        AttendanceProcessFactory.getAttendanceProcessImpl(hmId).mqttMessageArrived(taskId,topic, new String(message.getPayload()));
                    } else {
                        AccessControlProcessFactory.getAssessControlProcessImpl(getHmId(topic, message)).mqttMessageArrived(taskId,topic, new String(message.getPayload()));
                    }
                }
                return;
            }

            for (ManufacturerAttrDto manufacturerAttrDto : manufacturerAttrDtos) {
                if (ManufacturerDto.HM_TYPE_ACCESS_CONTROL.equals(manufacturerAttrDto.getHmType())) {
                    AccessControlProcessFactory.getAssessControlProcessImpl(manufacturerAttrDto.getHmId()).mqttMessageArrived(taskId,topic, new String(message.getPayload()));
                } else if (ManufacturerDto.HM_TYPE_CAR.equals(manufacturerAttrDto.getHmType())) {
                    CarMachineProcessFactory.getCarImpl(manufacturerAttrDto.getHmId()).mqttMessageArrived(taskId,topic, new String(message.getPayload()));
                } else if (ManufacturerDto.HM_TYPE_ATTENDANCE.equals(manufacturerAttrDto.getHmType())) {
                    AttendanceProcessFactory.getAttendanceProcessImpl(manufacturerAttrDto.getHmId()).mqttMessageArrived(taskId,topic, new String(message.getPayload()));
                }
            }

        } catch (Exception e) {
            log.error("处理订阅消息失败", e);
        }
    }

    /**
     * 获取协议
     *
     * @param topic
     * @param message
     * @return
     */
    private String getHmId(String topic, MqttMessage message) {

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
     * @param message
     * @return
     */
    private String getHmIdByYld(String topic, MqttMessage message) {
        String msg = new String(message.getPayload());

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

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        try {
            IManufacturerService manufacturerServiceImpl = ApplicationContextFactory.getBean("manufacturerServiceImpl", IManufacturerService.class);
            ManufacturerAttrDto tmpManufacturerDto = new ManufacturerAttrDto();
            tmpManufacturerDto.setSpecCd(ManufacturerAttrDto.SPEC_TOPIC);
            List<ManufacturerAttrDto> manufacturerAttrDtos = manufacturerServiceImpl.getManufacturerAttr(tmpManufacturerDto);

            if (manufacturerAttrDtos == null || manufacturerAttrDtos.size() < 1) {
                return;
            }

            //批量订阅
            for (ManufacturerAttrDto manufacturerAttrDto : manufacturerAttrDtos) {
                MqttFactory.subscribe(manufacturerAttrDto.getValue());
            }
        } catch (Exception e) {
            // 第一次启动会 异常 不关注
            e.printStackTrace();
        }
    }
}
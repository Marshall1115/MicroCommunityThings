package com.java110.barrier.adapter.zhenshiMqtt;

import com.java110.core.factory.MqttFactory;
import com.java110.core.factory.MqttLogFactory;
import com.java110.entity.machine.MachineDto;

/**
 * 臻识 道闸摄像头 字节和 字符串转换处理类
 * <p>
 * add by 吴学文 2021-01-04
 */
public class ZhenshiMqttSend {

    public static final String PUBLISH_TOPIC = "/device/SN/command";
    public static final String SN = "SN";

    /**
     * 发送指令
     *
     * @param machineDto
     * @param cmd
     * @return
     */
    public static boolean sendCmd(String taskId,String carNum, MachineDto machineDto, String cmd) {
        try {
            MqttFactory.publish(PUBLISH_TOPIC.replace(SN, machineDto.getMachineCode()), cmd);

            MqttLogFactory.saveSendLog(taskId,carNum,PUBLISH_TOPIC.replace(SN, machineDto.getMachineCode()),cmd);
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
            return false;
        }
        return true;
    }

}

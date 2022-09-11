package com.java110.core.adapt.attendance;

import com.java110.entity.machine.MachineDto;
import com.java110.entity.response.ResultDto;
import com.java110.entity.user.StaffDto;

/**
 * @ClassName IDealQunyingGetService
 * @Description TODO 考勤机心跳接口类
 * @Author wuxw
 * @Date 2020/5/26 17:38
 * @Version 1.0
 * add by wuxw 2020/5/26
 **/
public interface IAttendanceMachineProcess {

    /**
     * 查询设备是否存在
     *
     * @param machineDto 设备信息
     * @return
     */
    void initMachine(MachineDto machineDto);


    /**
     * 重启设备
     *
     * @param staffDto 设备信息
     */
    void restartAttendanceMachine(MachineDto machineDto, StaffDto staffDto);


    /**
     * 添加人脸
     *
     * @param machineDto 云端获取任务结果
     * @param staffDto   返回结果
     */
    ResultDto addFace(MachineDto machineDto, StaffDto staffDto);


    /**
     * 更新人脸
     *
     * @param machineDto 硬件信息
     * @param staffDto   返回结果
     */
    ResultDto updateFace(MachineDto machineDto, StaffDto staffDto);


    /**
     * 删除人脸
     *
     * @param machineDto 硬件信息
     * @param staffDto   返回结果
     */
    ResultDto deleteFace(MachineDto machineDto, StaffDto staffDto);


    /**
     * 清空人脸
     *
     * @param machineDto 硬件信息
     * @param staffDto   返回结果
     */
    ResultDto clearFace(MachineDto machineDto, StaffDto staffDto);

    /**
     * 返回默认结果值，在没有指令的情况下返回设备的 结果值
     *
     * @return
     */
    String getDefaultResult();

    void mqttMessageArrived(String taskId,String topic, String data);

}

package com.java110.core.adapt.barrier;

import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.ParkingAreaTextDto;

/**
 * 道闸对接设备 适配器类
 * add by wuxw
 * <p>
 * 2020-05-11
 */
public interface ICarMachineProcess {

    /**
     * 初始化方法
     */
    void initCar();

    /**
     * 初始化设备
     */
    void initCar(MachineDto machineDto);


    /**
     * 设备同步的字节信息
     *
     * @param machineDto 设备信息
     * @param bytes      字节信息
     */
    void readByte(MachineDto machineDto, byte[] bytes) throws Exception;


    /**
     * 重启设备
     *
     * @param machineDto 硬件信息
     */
    void restartMachine(MachineDto machineDto);

    /**
     * 道闸开门
     *
     * @param machineDto 硬件信息
     */
    void openDoor(MachineDto machineDto, ParkingAreaTextDto parkingAreaTextDto);
    /**
     * 道闸开门
     *
     * @param machineDto 硬件信息
     */
    void closeDoor(MachineDto machineDto, ParkingAreaTextDto parkingAreaTextDto);

    void sendKeepAlive(MachineDto machineDto);


    void mqttMessageArrived(String topic, String s);

    /**
     * 手动触发 识别
     * @param machineDto
     */
    void manualTrigger(MachineDto machineDto);
}

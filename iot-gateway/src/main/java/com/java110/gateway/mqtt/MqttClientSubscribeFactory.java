package com.java110.gateway.mqtt;

import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.service.machine.IMachineService;
import com.java110.core.service.manufacturer.IManufacturerService;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.manufacturer.ManufacturerAttrDto;

import java.util.List;

public class MqttClientSubscribeFactory {

    public static void subscribe(){

        // 臻识mqtt订阅
        MqttFactory.subscribe("/device/push/result");
        //注册伊兰度设备
        IMachineService machineService = ApplicationContextFactory.getBean("machineServiceImpl", IMachineService.class);

        MachineDto machineDto = new MachineDto();
        machineDto.setHmId("3");
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            return;
        }

        for (MachineDto machineDto1 : machineDtos) {
            MqttFactory.subscribe("face." + machineDto1.getMachineCode() + ".response");
        }


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
    }
}

package com.java110.barrier.engine;

import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import com.java110.entity.parkingArea.ResultParkingAreaTextDto;

import java.util.List;

public interface IOutCarEngine {

    /**
     * 出场
     * @param type
     * @param carNum
     * @param machineDto
     * @param parkingAreaDtos
     * @return
     * @throws Exception
     */
    ResultParkingAreaTextDto outParkingArea(String type, String carNum, MachineDto machineDto, List<ParkingAreaDto> parkingAreaDtos) throws Exception;
}

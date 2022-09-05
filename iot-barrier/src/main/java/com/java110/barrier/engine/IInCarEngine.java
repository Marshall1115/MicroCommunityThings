package com.java110.barrier.engine;

import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import com.java110.entity.parkingArea.ResultParkingAreaTextDto;

import java.util.List;

public interface IInCarEngine {

    ResultParkingAreaTextDto enterParkingArea(String type, String carNum, MachineDto machineDto, List<ParkingAreaDto> parkingAreaDtos,IInOutCarTextEngine inOutCarTextEngine) throws Exception;
}

package com.java110.barrier.engine;

import com.java110.core.service.car.ICarService;
import com.java110.core.util.DateUtil;
import com.java110.entity.car.CarDayDto;
import com.java110.entity.car.CarDto;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JudgeOwnerCarEngine {


    @Autowired
    private ICarService carServiceImpl;


    public CarDayDto judgeOwnerCar(MachineDto machineDto, String carNum, List<ParkingAreaDto> parkingAreaDtos) throws Exception{
        List<String> paIds = new ArrayList<>();
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtos) {
            paIds.add(parkingAreaDto.getPaId());
        }
        CarDto carDto = new CarDto();
        carDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        carDto.setCarNum(carNum);
        List<CarDto> carDtos = carServiceImpl.queryCars(carDto);

        if (carDtos == null || carDtos.size() < 1) {
            return new CarDayDto(carNum,CarDto.LEASE_TYPE_TEMP,-1);
        }
        int day = DateUtil.differentDays(DateUtil.getCurrentDate(), carDtos.get(0).getEndTime());
        if (day <= 0) {

            return new CarDayDto(carNum,carDtos.get(0).getLeaseType(),-2);

        }
        return new CarDayDto(carNum,carDtos.get(0).getLeaseType(),day);
    }
}

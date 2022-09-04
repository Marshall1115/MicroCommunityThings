package com.java110.barrier.engine;

import com.java110.core.service.car.ICarBlackWhiteService;
import com.java110.entity.car.CarBlackWhiteDto;
import com.java110.entity.car.CarInoutDto;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JudgeWhiteCarEngine {

    @Autowired
    private ICarBlackWhiteService carBlackWhiteServiceImpl;
    /**
     * 白名单车辆
     */
    public Boolean judgeWhiteCar(MachineDto machineDto, String carNum,
                                  List<ParkingAreaDto> parkingAreaDtos,
                                  String type, List<CarInoutDto> carInoutDtos) throws Exception {
        List<String> paIds = new ArrayList<>();
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtos) {
            paIds.add(parkingAreaDto.getPaId());
        }
        //1.0 判断是否为黑名单
        CarBlackWhiteDto carBlackWhiteDto = new CarBlackWhiteDto();
        carBlackWhiteDto.setCommunityId(machineDto.getCommunityId());
        carBlackWhiteDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        carBlackWhiteDto.setCarNum(carNum);
        carBlackWhiteDto.setBlackWhite(CarBlackWhiteDto.BLACK_WHITE_WHITE);
        carBlackWhiteDto.setHasValid("Y");
        List<CarBlackWhiteDto> blackWhiteDtos = carBlackWhiteServiceImpl.queryCarBlackWhites(carBlackWhiteDto);

        //白名单直接出场
        CarInoutDto carInoutDto = null;
        if (blackWhiteDtos != null && blackWhiteDtos.size() > 0) {
            return true;
        }

        return false;
    }
}

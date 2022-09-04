package com.java110.barrier.engine.impl;

import com.java110.barrier.engine.*;
import com.java110.core.entity.car.BarrierGateControlDto;
import com.java110.core.service.car.ICarBlackWhiteService;
import com.java110.core.service.car.ICarInoutService;
import com.java110.entity.car.CarBlackWhiteDto;
import com.java110.entity.car.CarDayDto;
import com.java110.entity.car.CarDto;
import com.java110.entity.car.CarInoutDto;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.InOutCarTextDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import com.java110.entity.parkingArea.ResultParkingAreaTextDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InCarEngine extends CarEngine implements IInCarEngine {

    @Autowired
    private ICarBlackWhiteService carBlackWhiteServiceImpl;


    @Autowired
    private ICarInoutService carInoutServiceImpl;

    @Autowired
    private SendInfoEngine sendInfoEngine;

    @Autowired
    private CarInLogEngine carInLogEngine;

    @Autowired
    private JudgeOwnerCarEngine judgeOwnerCarEngine;


    /**
     * 车辆进场
     *
     * @param type       车牌类型
     * @param carNum     车牌号
     * @param machineDto 设备信息
     * @return
     */
    public ResultParkingAreaTextDto enterParkingArea(String type, String carNum, MachineDto machineDto, List<ParkingAreaDto> parkingAreaDtos, IInOutCarTextEngine inOutCarTextEngine) throws Exception {

        InOutCarTextDto inOutCarTextDto = null;
        //1.0 判断是否为黑名单
        List<String> paIds = new ArrayList<>();
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtos) {
            paIds.add(parkingAreaDto.getPaId());
        }

        CarBlackWhiteDto carBlackWhiteDto = new CarBlackWhiteDto();
        carBlackWhiteDto.setCommunityId(machineDto.getCommunityId());
        carBlackWhiteDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        carBlackWhiteDto.setCarNum(carNum);
        carBlackWhiteDto.setBlackWhite(CarBlackWhiteDto.BLACK_WHITE_BLACK);
        carBlackWhiteDto.setHasValid("Y");
        List<CarBlackWhiteDto> blackWhiteDtos = carBlackWhiteServiceImpl.queryCarBlackWhites(carBlackWhiteDto);

        //黑名单车辆不能进入
        if (blackWhiteDtos != null && blackWhiteDtos.size() > 0) {
            inOutCarTextDto = inOutCarTextEngine.blackCarCannotIn(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门失败", type, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_BLACK, inOutCarTextDto, carNum);
        }

        //判断车辆是否为月租车
        CarDayDto carDayDto = judgeOwnerCarEngine.judgeOwnerCar(machineDto, carNum, parkingAreaDtos);
        //判断车辆是否在 场内
        CarInoutDto inoutDto = new CarInoutDto();
        inoutDto.setCarNum(carNum);
        inoutDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        inoutDto.setState("1");
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(inoutDto);
        // 临时车再场内 不让进 需要工作人员处理 手工出场
        if (carInoutDtos != null && carInoutDtos.size() > 0) {
            inOutCarTextDto = inOutCarTextEngine.carInParkingArea(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门失败", type, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_INED, inOutCarTextDto, carNum);
        }

        // 判断是否为出售车辆
        if(CarDto.LEASE_TYPE_SALE.equals(carDayDto.getLeaseType())){
            inOutCarTextDto = inOutCarTextEngine.carInSaleCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        // 判断是否为出售车辆
        if(CarDto.LEASE_TYPE_INNER.equals(carDayDto.getLeaseType())){
            inOutCarTextDto = inOutCarTextEngine.carInInnerCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        // 判断是否为出售车辆
        if(CarDto.LEASE_TYPE_NO_MONEY.equals(carDayDto.getLeaseType())){
            inOutCarTextDto = inOutCarTextEngine.carInInnerNoMoney(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        // 说明是月租车
        if (carDayDto.getDay() > 0) {
            //小于6天时的回复
            if (carDayDto.getDay() < 6) {
                inOutCarTextDto = inOutCarTextEngine.carInLastFiveDay(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
                saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
            }
            inOutCarTextDto = inOutCarTextEngine.carInMonthCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        //月租车 已过期时 可以进场 只是提示未 浙CS8417，月租车，已过期
        if (carDayDto.getDay() == -2) {
            inOutCarTextDto = inOutCarTextEngine.carInMonthExpire(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }
        inOutCarTextDto = inOutCarTextEngine.carInTempCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
        saveCarInInfo(carNum, machineDto, inOutCarTextDto, "开门成功", type, parkingAreaDtos, CarInoutDto.STATE_IN);
        return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
    }

    private void saveCarInInfo(String carNum, MachineDto machineDto, InOutCarTextDto inOutCarTextDto, String openStats, String type, List<ParkingAreaDto> parkingAreaDtos, String stateInFail) throws Exception {
        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, inOutCarTextDto.getRemark(), openStats);
        sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
        //保存 进场记录
        carInLogEngine.saveCarInLog(carNum, type, machineDto, parkingAreaDtos, stateInFail, inOutCarTextDto.getRemark());
    }
}

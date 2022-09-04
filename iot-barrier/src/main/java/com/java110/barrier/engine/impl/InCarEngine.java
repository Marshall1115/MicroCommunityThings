package com.java110.barrier.engine.impl;

import com.java110.barrier.engine.*;
import com.java110.core.entity.car.BarrierGateControlDto;
import com.java110.core.factory.ParkingAreaTextFactory;
import com.java110.core.service.car.ICarBlackWhiteService;
import com.java110.core.service.car.ICarInoutService;
import com.java110.entity.car.CarBlackWhiteDto;
import com.java110.entity.car.CarDayDto;
import com.java110.entity.car.CarInoutDto;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import com.java110.entity.parkingArea.ParkingAreaTextCacheDto;
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
    public ResultParkingAreaTextDto enterParkingArea(String type, String carNum, MachineDto machineDto, List<ParkingAreaDto> parkingAreaDtos) throws Exception {


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
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, "此车为黑名单车辆" + carNum + ",禁止通行", "开门失败");
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            //保存 进场记录
            carInLogEngine.saveCarInLog(carNum, type, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL, "此车为黑名单车辆" + carNum + ",禁止通行");
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_BLACK,  carNum, "禁止通行","", "",  carNum + ",禁止通行", carNum);
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
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, carNum + ",车已在场", "开门失败");
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            carInLogEngine.saveCarInLog(carNum, type, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL, carNum + ",车已在场");
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_INED, carNum, "车已在场", "", "", carNum + ",车已在场", carNum);
        }

        ParkingAreaTextCacheDto parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos, ParkingAreaTextFactory.TYPE_CD_MONTH_CAR_IN);

        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, "", "", "", carDayDto.getDay() + "");
        // 说明是月租车
        if (carDayDto.getDay() > 0) {
            if (parkingAreaTextCacheDto != null) { //配置了缓存
                BarrierGateControlDto barrierGateControlDto
                        = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, "月租车," + carNum + ",欢迎光临", "开门成功");
                sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
                carInLogEngine.saveCarInLog(carNum, type, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN, "月租车," + carNum + ",欢迎光临");
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_MONTH_CAR_SUCCESS, parkingAreaTextCacheDto, carNum);
            }

            //小于6天时的回复
            if(carDayDto.getDay() < 6){
                BarrierGateControlDto barrierGateControlDto
                        = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, carNum + ",欢迎光临", "开门成功");
                sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
                carInLogEngine.saveCarInLog(carNum, type, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN, carNum+"月租车,剩余" + carDayDto.getDay() + "天,请尽快延期");
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, carNum,"月租车,剩余" + carDayDto.getDay() + "天,请尽快延期", "",  "", carNum+"月租车,剩余" + carDayDto.getDay() + "天,请尽快延期", carNum);
            }


            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, carNum + ",欢迎光临", "开门成功");
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            carInLogEngine.saveCarInLog(carNum, type, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN, carNum + ",欢迎光临");
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, "月租车",carNum, "欢迎光临",  "", carNum + ",欢迎光临", carNum);
        }

        //月租车 已过期时 可以进场 只是提示未 浙CS8417，月租车，已过期
        if(carDayDto.getDay() == -2){
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, carNum + ",已过期", "开门成功");
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            carInLogEngine.saveCarInLog(carNum, type, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN, carNum + ",已过期");
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, "月租车",carNum, "已过期",  "", "月租车,"+carNum + ",已过期", carNum);
        }

        // 说明是临时车
        if (parkingAreaTextCacheDto != null) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, "临时车," + carNum + ",欢迎光临", "开门成功");
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            carInLogEngine.saveCarInLog(carNum, type, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN, "临时车," + carNum + ",欢迎光临");
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_TEMP_CAR_SUCCESS, parkingAreaTextCacheDto, carNum);
        }

        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, "临时车," + carNum + ",欢迎光临", "开门成功");
        sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
        carInLogEngine.saveCarInLog(carNum, type, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN, "临时车," + carNum + ",欢迎光临");
        return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, "临时车", carNum, "欢迎光临", "", "临时车," + carNum + ",欢迎光临", carNum);
    }
}

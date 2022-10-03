package com.java110.barrier.engine.impl;

import com.java110.barrier.engine.*;
import com.java110.core.adapt.IComputeTempCarFee;
import com.java110.core.entity.car.BarrierGateControlDto;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.ParkingAreaTextFactory;
import com.java110.core.factory.TempCarFeeFactory;
import com.java110.core.service.car.ICarInoutService;
import com.java110.core.service.fee.ITempCarFeeConfigService;
import com.java110.core.service.parkingBox.IParkingBoxService;
import com.java110.core.util.Assert;
import com.java110.entity.car.*;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.InOutCarTextDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import com.java110.entity.parkingArea.ParkingBoxDto;
import com.java110.entity.parkingArea.ResultParkingAreaTextDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutCarEngine extends CarEngine implements IOutCarEngine {

    @Autowired
    JudgeOwnerCarEngine judgeOwnerCarEngine;

    @Autowired
    private ICarInoutService carInoutServiceImpl;

    @Autowired
    private SendInfoEngine sendInfoEngine;

    @Autowired
    private IParkingBoxService parkingBoxServiceImpl;

    @Autowired
    private JudgeWhiteCarEngine judgeWhiteCarEngine;

    @Autowired
    private CarOutLogEngine carOutLogEngine;


    @Autowired
    private ITempCarFeeConfigService tempCarFeeConfigServiceImpl;


    @Override
    public ResultParkingAreaTextDto outParkingArea(String type, String carNum, MachineDto machineDto, List<ParkingAreaDto> parkingAreaDtos, IInOutCarTextEngine inOutCarTextEngine) throws Exception {

        InOutCarTextDto inOutCarTextDto = null;
        //查询进场记录
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setPaId(getDefaultPaId(parkingAreaDtos));
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(carInoutDto);
        CarDayDto carDayDto = judgeOwnerCarEngine.judgeOwnerCar(machineDto, carNum, parkingAreaDtos);

        if (carInoutDtos == null || carInoutDtos.size() < 1) {
            if("N".equals(parkingAreaDtos.get(0).getYelowCarIn())) {
                inOutCarTextDto = inOutCarTextEngine.carNotInParkingArea(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
                saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门失败", null, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_NO_IN, inOutCarTextDto, carNum);
            }else{
                inOutCarTextDto = inOutCarTextEngine.carNotInParkingAreaCanOut(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
                saveCarOutInfo(carNum, machineDto, inOutCarTextDto, 0, "开门成功", null, parkingAreaDtos, CarInoutDto.STATE_OUT);
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_FREE_CAR_OUT_SUCCESS, inOutCarTextDto, carNum);
            }
        }

        //判断是否为黑名单
        if (judgeWhiteCarEngine.judgeBlackCar(machineDto, carNum, parkingAreaDtos, type, carInoutDtos)) {
            inOutCarTextDto = inOutCarTextEngine.blackCarOut(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
            saveCarOutInfo(carNum,machineDto,inOutCarTextDto,0,"开门失败",carInoutDtos.get(0),parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);

            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR, inOutCarTextDto, carNum);
        }
        //判断是否为白名单
        if (judgeWhiteCarEngine.judgeWhiteCar(machineDto, carNum, parkingAreaDtos, type, carInoutDtos)) {
            inOutCarTextDto = inOutCarTextEngine.whiteCarOut(carNum, machineDto, getDefaultPaId(parkingAreaDtos));
            saveCarOutInfo(carNum,machineDto,inOutCarTextDto,0,"开门成功",carInoutDtos.get(0),parkingAreaDtos, CarInoutDto.STATE_OUT);

            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_FREE_CAR_OUT_SUCCESS, inOutCarTextDto, carNum);
        }



        // 判断是否为出售车辆
        if (CarDto.LEASE_TYPE_SALE.equals(carDayDto.getLeaseType())) {
            inOutCarTextDto = inOutCarTextEngine.carOutSaleCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum,machineDto,inOutCarTextDto,0,"开门成功",carInoutDtos.get(0),parkingAreaDtos, CarInoutDto.STATE_OUT);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        // 判断是否为内部车辆
        if (CarDto.LEASE_TYPE_INNER.equals(carDayDto.getLeaseType())) {
            inOutCarTextDto = inOutCarTextEngine.carOutInnerCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum,machineDto,inOutCarTextDto,0,"开门成功",carInoutDtos.get(0),parkingAreaDtos, CarInoutDto.STATE_OUT);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        // 判断是否为免费车辆
        if (CarDto.LEASE_TYPE_NO_MONEY.equals(carDayDto.getLeaseType())) {
            inOutCarTextDto = inOutCarTextEngine.carOutInnerNoMoney(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum,machineDto,inOutCarTextDto,0,"开门成功",carInoutDtos.get(0),parkingAreaDtos, CarInoutDto.STATE_OUT);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, inOutCarTextDto, carNum);
        }

        //说明是--------------------------------------------------------------------月租车---------------------------------------------------------------
        if (carDayDto.getDay() > 0) {
            inOutCarTextDto = inOutCarTextEngine.carOutMonthCar(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum,machineDto,inOutCarTextDto,0,"开门成功",carInoutDtos.get(0),parkingAreaDtos, CarInoutDto.STATE_OUT);
            ResultParkingAreaTextDto resultParkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_MONTH_CAR_OUT_SUCCESS, inOutCarTextDto, carNum);
            resultParkingAreaTextDto.setDay(carDayDto.getDay());
            return resultParkingAreaTextDto;
        }

        if (carDayDto.getDay() == -2) {
            inOutCarTextDto = inOutCarTextEngine.carOutMonthCarExpire(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum,machineDto,inOutCarTextDto,0,"开门失败",carInoutDtos.get(0),parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);
            ResultParkingAreaTextDto resultParkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR, inOutCarTextDto, carNum);
            resultParkingAreaTextDto.setDay(0);
            return resultParkingAreaTextDto;
        }

        //说明是--------------------------------------------------------------------临时车---------------------------------------------------------------

        //判断岗亭是否收费
        ParkingBoxDto parkingBoxDto = new ParkingBoxDto();
        parkingBoxDto.setExtBoxId(machineDto.getLocationObjId());
        parkingBoxDto.setCommunityId(machineDto.getCommunityId());
        List<ParkingBoxDto> parkingBoxDtos = parkingBoxServiceImpl.queryParkingBoxs(parkingBoxDto);

        Assert.listOnlyOne(parkingBoxDtos, "设备不存在 岗亭");

        if (!"Y".equals(parkingBoxDtos.get(0).getFee())) {
            inOutCarTextDto = inOutCarTextEngine.carOutConfigError(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum,machineDto,inOutCarTextDto,0,"开门成功",carInoutDtos.get(0),parkingAreaDtos, CarInoutDto.STATE_OUT);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, inOutCarTextDto, carNum);
        }


        // 说明完成支付
        if (TempCarFeeFactory.judgeFinishPayTempCarFee(carInoutDtos.get(0))) {
            inOutCarTextDto = inOutCarTextEngine.carOutFinishPayFee(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum,machineDto,inOutCarTextDto,0,"开门成功",carInoutDtos.get(0),parkingAreaDtos, CarInoutDto.STATE_OUT);

            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_TEMP_CAR_OUT_SUCCESS, inOutCarTextDto, carNum);
        }

        //未缴费
        TempCarFeeConfigDto tempCarFeeConfigDto = new TempCarFeeConfigDto();
        tempCarFeeConfigDto.setPaId(getDefaultPaId(parkingAreaDtos));
        tempCarFeeConfigDto.setCommunityId(carInoutDtos.get(0).getCommunityId());
        List<TempCarFeeConfigDto> tempCarFeeConfigDtos = tempCarFeeConfigServiceImpl.queryTempCarFeeConfigs(tempCarFeeConfigDto);

        if (tempCarFeeConfigDtos == null || tempCarFeeConfigDtos.size() < 1) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, carInoutDtos.get(0), "未配置临时车收费规则", "开门失败");
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL, "未配置临时车收费规则");
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_NO_PRI, "临时车无权限");
        }

        IComputeTempCarFee computeTempCarFee = ApplicationContextFactory.getBean(tempCarFeeConfigDtos.get(0).getRuleId(), IComputeTempCarFee.class);
        TempCarFeeResult result = computeTempCarFee.computeTempCarFee(carInoutDtos.get(0), tempCarFeeConfigDtos.get(0));

        //不收费，直接出场
        if (result.getPayCharge() == 0) {
            inOutCarTextDto = inOutCarTextEngine.carOutFinishPayFee(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto);
            saveCarOutInfo(carNum,machineDto,inOutCarTextDto,0,"开门成功",carInoutDtos.get(0),parkingAreaDtos, CarInoutDto.STATE_OUT);
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, inOutCarTextDto, carNum);
        }
        inOutCarTextDto = inOutCarTextEngine.carOutNeedPayFee(carNum, machineDto, getDefaultPaId(parkingAreaDtos), carDayDto,result);
        saveCarOutInfo(carNum,machineDto,inOutCarTextDto, result.getPayCharge(),"开门失败",carInoutDtos.get(0),parkingAreaDtos, CarInoutDto.STATE_IN_FAIL);
        //分钟
        ResultParkingAreaTextDto resultParkingAreaTextDto = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR,
                inOutCarTextDto, carNum);
        resultParkingAreaTextDto.setHours(result.getHours());
        resultParkingAreaTextDto.setMin(result.getMin());
        resultParkingAreaTextDto.setPayCharge(result.getPayCharge());
        return resultParkingAreaTextDto;
    }

    private void saveCarOutInfo(String carNum, MachineDto machineDto, InOutCarTextDto inOutCarTextDto,double payCharge, String openStats, CarInoutDto carInoutDto, List<ParkingAreaDto> parkingAreaDtos, String stateInFail) throws Exception {
        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, payCharge, carInoutDto, inOutCarTextDto.getRemark(), openStats);
        sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
        carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, stateInFail, inOutCarTextDto.getRemark());
    }
}

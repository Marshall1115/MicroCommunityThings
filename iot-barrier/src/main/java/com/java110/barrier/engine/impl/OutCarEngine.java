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
import com.java110.entity.car.CarDayDto;
import com.java110.entity.car.CarInoutDto;
import com.java110.entity.car.TempCarFeeConfigDto;
import com.java110.entity.car.TempCarFeeResult;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import com.java110.entity.parkingArea.ParkingAreaTextCacheDto;
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
    public ResultParkingAreaTextDto outParkingArea(String type, String carNum, MachineDto machineDto, List<ParkingAreaDto> parkingAreaDtos) throws Exception {

        //查询进场记录
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setPaId(getDefaultPaId(parkingAreaDtos));
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(carInoutDto);
        CarDayDto carDayDto = judgeOwnerCarEngine.judgeOwnerCar(machineDto, carNum, parkingAreaDtos);

        if (carInoutDtos == null || carInoutDtos.size() < 1) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, null, carNum + ",车未入场", "开门失败");
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL, carNum + ",车未入场");
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_NO_IN, carNum, "车未入场", "", "", carNum + ",车未入场", carNum);
        }

        //判断是否为白名单
        if (judgeWhiteCarEngine.judgeWhiteCar(machineDto, carNum, parkingAreaDtos, type, carInoutDtos)) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, carInoutDtos.get(0), carNum + ",内部车,一路平安", "开门成功");
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, CarInoutDto.STATE_OUT, carNum + ",内部车,一路平安");
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_FREE_CAR_OUT_SUCCESS,  carNum,"内部车,一路平安","",  "", carNum + ",内部车,一路平安", carNum);
        }

        //判断车辆是否为月租车
        ParkingAreaTextCacheDto parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos, ParkingAreaTextFactory.TYPE_CD_MONTH_CAR_OUT);
        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, "", "", "", carDayDto.getDay() + "");

        //说明是--------------------------------------------------------------------月租车---------------------------------------------------------------
        if (carDayDto.getDay() > 0) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, carInoutDtos.get(0), carNum + ",月租车剩余" + carDayDto.getDay() + "天", "开门成功");
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            if (parkingAreaTextCacheDto != null) {
                carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, CarInoutDto.STATE_OUT, carNum + ",月租车剩余" + carDayDto.getDay() + "天");
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_MONTH_CAR_OUT_SUCCESS, parkingAreaTextCacheDto, carNum);
            }

            ResultParkingAreaTextDto resultParkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_MONTH_CAR_OUT_SUCCESS,carNum, "月租车剩余" + carDayDto.getDay() + "天,一路平安", "","", "月租车,"+carNum + ",剩余" + carDayDto.getDay() + "天", carNum);
            resultParkingAreaTextDto.setDay(carDayDto.getDay());
            carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, CarInoutDto.STATE_OUT, carNum + ",月租车剩余" + carDayDto.getDay() + "天");
            return resultParkingAreaTextDto;
        }

        if(carDayDto.getDay() == -2){
            ResultParkingAreaTextDto resultParkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR,carNum, "月租车,已过期,请缴费", "","", carNum+",月租车,已过期,请缴费", carNum);
            resultParkingAreaTextDto.setDay(0);
            carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL, carNum+",月租车,已过期,请缴费");
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
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, carInoutDtos.get(0), carNum + ",一路平安,该岗亭不收费", "开门成功");
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, CarInoutDto.STATE_OUT, carNum + ",一路平安");
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, carNum, ",一路平安", "", "", carNum + ",一路平安", carNum);
        }

        //检查是否支付完成
        parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos, ParkingAreaTextFactory.TYPE_CD_TEMP_CAR_OUT);
        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, "", "", "", "");

        // 说明完成支付
        if (TempCarFeeFactory.judgeFinishPayTempCarFee(carInoutDtos.get(0))) {
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, carInoutDtos.get(0), "临时车,"+carNum + ",一路平安", "开门成功");
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            if (parkingAreaTextCacheDto != null) {
                carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, CarInoutDto.STATE_OUT, "临时车,"+carNum + ",一路平安");
                return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_TEMP_CAR_OUT_SUCCESS, parkingAreaTextCacheDto, carNum);
            }
            carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, CarInoutDto.STATE_OUT, "临时车,"+carNum + ",一路平安");
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_TEMP_CAR_OUT_SUCCESS, "临时车",carNum,  "一路平安", "", "临时车,"+carNum + ",一路平安", carNum);
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
            BarrierGateControlDto barrierGateControlDto
                    = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, 0, carInoutDtos.get(0), carNum + "停车" + result.getHours() + "时" + result.getMin() + "分", "开门成功");
            sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
            carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, CarInoutDto.STATE_OUT, carNum + "停车" + result.getHours() + "小时" + result.getMin() + "分钟");
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, "临时车,"+carNum + ",一路平安",
                    "", "", "", "临时车,"+carNum + ",一路平安", carNum);
        }

        parkingAreaTextCacheDto = ParkingAreaTextFactory.getText(parkingAreaDtos, ParkingAreaTextFactory.TYPE_CD_TEMP_CAR_NO_PAY);

        //替换脚本中信息
        replaceParkingAreaTextCache(parkingAreaTextCacheDto, carNum, result.getHours() + "", result.getMin() + "", result.getPayCharge() + "", "");

        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, carNum, machineDto, result.getPayCharge(), carInoutDtos.get(0),
                carNum + "停车" + result.getHours() + "小时" + result.getMin() + "分钟,请缴费" + result.getPayCharge() + "元", "开门失败");
        sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);

        if (parkingAreaTextCacheDto != null) {
            carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL, carNum + "停车" + result.getHours() + "小时" + result.getMin() + "分钟,请缴费" + result.getPayCharge() + "元");
            return new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR, parkingAreaTextCacheDto, carNum);
        }
        //分钟
        ResultParkingAreaTextDto resultParkingAreaTextDto = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR,
                carNum,"停车" + result.getHours() + "小时"+result.getMin() + "分钟,请交费"+ result.getPayCharge() + "元","","",
                carNum + ",停车" + result.getHours() + "小时" + result.getMin() + "分钟,请交费" + result.getPayCharge() + "元", carNum);

        resultParkingAreaTextDto.setHours(result.getHours());
        resultParkingAreaTextDto.setMin(result.getMin());
        resultParkingAreaTextDto.setPayCharge(result.getPayCharge());
        carOutLogEngine.saveCarOutLog(carNum, machineDto, parkingAreaDtos, CarInoutDto.STATE_IN_FAIL, carNum + "停车" + result.getHours() + "时" + result.getMin() + "分请交费" + result.getPayCharge() + "元");
        return resultParkingAreaTextDto;
    }
}

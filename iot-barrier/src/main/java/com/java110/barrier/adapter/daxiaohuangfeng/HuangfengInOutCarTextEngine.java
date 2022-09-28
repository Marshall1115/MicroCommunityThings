package com.java110.barrier.adapter.daxiaohuangfeng;

import com.java110.barrier.engine.IInOutCarTextEngine;
import com.java110.entity.car.CarDayDto;
import com.java110.entity.car.TempCarFeeResult;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.InOutCarTextDto;
import org.springframework.stereotype.Service;

@Service
public class HuangfengInOutCarTextEngine implements IInOutCarTextEngine {
    /**
     * 1.0入场：黑名单禁止入场
     * @param carNum
     * @param machineDto
     * @param paId
     * @return
     */
    @Override
    public InOutCarTextDto blackCarCannotIn(String carNum, MachineDto machineDto, String paId) {
        return new InOutCarTextDto(carNum, "禁止通行", carNum + ",禁止通行", "此车为黑名单车辆" + carNum + ",禁止通行");
    }

    /**
     * 1.0入场：白名单入场
     * @param carNum
     * @param machineDto
     * @param paId
     * @return
     */
    @Override
    public InOutCarTextDto whiteCarCanIn(String carNum, MachineDto machineDto, String paId) {
        return new InOutCarTextDto("内部车",carNum, "欢迎光临","", "内部车,"+carNum + ",欢迎光临");
    }


    /**
     * 2.0入场：车辆在场
     * @param carNum
     * @param machineDto
     * @param paId
     * @return
     */
    @Override
    public InOutCarTextDto carInParkingArea(String carNum, MachineDto machineDto, String paId) {
        return new InOutCarTextDto(carNum, "车已在场", carNum + ",车已在场");
    }

    /**
     * 3.0入场：出售车辆
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carInSaleCar(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto) {
        return new InOutCarTextDto("固定车",carNum, "欢迎光临","", "固定车,"+carNum + ",欢迎光临");
    }

    /**
     * 4.0入场：内部车
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carInInnerCar(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto) {
        return new InOutCarTextDto("内部车",carNum, "欢迎光临","", "内部车,"+carNum + ",欢迎光临");
    }

    /**
     * 5.0入场： 免费车
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carInInnerNoMoney(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto) {
        return new InOutCarTextDto("免费车辆",carNum, "欢迎光临","", "免费车辆,"+carNum + ",欢迎光临");
    }

    /**
     * 6.0入场： 月租车 5天后到期
     * @param carNum
     * @param machineDto
     * @param paId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carInLastFiveDay(String carNum, MachineDto machineDto, String paId, CarDayDto carDayDto) {
        return new InOutCarTextDto(carNum, "月租车,剩余" + carDayDto.getDay() + "天,请尽快延期", carNum + "月租车,剩余" + carDayDto.getDay() + "天,请尽快延期");
    }

    /**
     * 7.0入场：月租车
     * @param carNum
     * @param machineDto
     * @param paId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carInMonthCar(String carNum, MachineDto machineDto, String paId, CarDayDto carDayDto) {
        return new InOutCarTextDto("月租车", carNum, "欢迎光临", "", carNum + ",欢迎光临");
    }

    /**
     * 8.0入场：月租车辆已过期
     * @param carNum
     * @param machineDto
     * @param paId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carInMonthExpire(String carNum, MachineDto machineDto, String paId, CarDayDto carDayDto) {
        return new InOutCarTextDto("月租车", carNum, "已过期", "", "月租车," + carNum + ",已过期");
    }

    @Override
    public InOutCarTextDto tempCarCannotIn(String carNum, MachineDto machineDto, String paId) {
        return new InOutCarTextDto("临时车", carNum, "禁止通行", "", "临时车," + carNum + ",禁止通行");
    }

    /**
     * 9.0入场：临时车进场
     * @param carNum
     * @param machineDto
     * @param paId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carInTempCar(String carNum, MachineDto machineDto, String paId, CarDayDto carDayDto) {
        return new InOutCarTextDto("临时车", carNum, "欢迎光临", "", "临时车," + carNum + ",欢迎光临");
    }

    /**
     * 10.0出场：车辆未入场
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @return
     */
    @Override
    public InOutCarTextDto carNotInParkingArea(String carNum, MachineDto machineDto, String defaultPaId) {
        return new InOutCarTextDto(carNum, "车未入场", carNum + ",车未入场");
    }
    /**
     * 10.0出场：车辆未入场(可以出场)
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @return
     */
    @Override
    public InOutCarTextDto carNotInParkingAreaCanOut(String carNum, MachineDto machineDto, String defaultPaId){
        return new InOutCarTextDto(carNum, ",一路平安", carNum + ",一路平安");
    }

    public InOutCarTextDto blackCarOut(String carNum, MachineDto machineDto, String defaultPaId){
        return new InOutCarTextDto(carNum, "禁止通行", carNum + ",禁止通行", "此车为黑名单车辆" + carNum + ",禁止通行");
    }

    /**
     * 11.0出场：白名单出场
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @return
     */
    @Override
    public InOutCarTextDto whiteCarOut(String carNum, MachineDto machineDto, String defaultPaId) {
        return new InOutCarTextDto( carNum, "内部车,一路平安", carNum + ",内部车,一路平安");
    }

    /**
     * 12.0出场：出售车辆
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carOutSaleCar(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto) {
        return new InOutCarTextDto( carNum, "固定车,一路平安", carNum + ",固定车,一路平安");
    }

    /**
     * 13.0出场：内部车
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carOutInnerCar(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto) {
        return new InOutCarTextDto( carNum, "内部车,一路平安", carNum + ",内部车,一路平安");
    }

    /**
     * 14.0出场：免费车辆
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carOutInnerNoMoney(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto) {
        return new InOutCarTextDto( carNum, "免费车辆,一路平安", carNum + ",免费车辆,一路平安");
    }

    /**
     * 15.0出场：月租车
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carOutMonthCar(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto) {
        return new InOutCarTextDto( carNum, "月租车剩余" + carDayDto.getDay() + "天,一路平安", "月租车," + carNum + ",剩余" + carDayDto.getDay() + "天");
    }

    /**
     * 16.0出场：月租车过期
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carOutMonthCarExpire(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto) {
        return new InOutCarTextDto(carNum, "月租车,已过期,请缴费", carNum + ",月租车,已过期,请缴费");
    }

    /**
     * 17.0出场：配置错误
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carOutConfigError(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto) {
        return new InOutCarTextDto(carNum, ",一路平安", carNum + ",一路平安");
    }

    /**
     * 18.0出场：临时车支付完成
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    @Override
    public InOutCarTextDto carOutFinishPayFee(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto) {
        return new InOutCarTextDto("临时车", carNum, "一路平安", "", "临时车," + carNum + ",一路平安");
    }

    /**
     * 19.0临时车出场未付费
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @param result
     * @return
     */
    @Override
    public InOutCarTextDto carOutNeedPayFee(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto, TempCarFeeResult result) {
        return new InOutCarTextDto(carNum, "停车" + result.getHours() + "小时" + result.getMin() + "分钟,请交费" + result.getPayCharge() + "元", "", "",
                carNum + ",停车" + result.getHours() + "小时" + result.getMin() + "分钟,请交费" + result.getPayCharge() + "元");
    }
}

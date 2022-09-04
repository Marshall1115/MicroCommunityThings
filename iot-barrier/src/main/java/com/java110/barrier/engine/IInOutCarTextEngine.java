package com.java110.barrier.engine;


import com.java110.entity.car.CarDayDto;
import com.java110.entity.car.TempCarFeeResult;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.InOutCarTextDto;

public interface IInOutCarTextEngine {


    /**
     * 1.0 黑名单车辆不能入场
     *
     * @param carNum
     * @param machineDto
     * @param paId
     * @return
     */
    InOutCarTextDto blackCarCannotIn(String carNum, MachineDto machineDto, String paId);


    /**
     * 白名单车辆进入
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @return
     */
    InOutCarTextDto whiteCarCanIn(String carNum, MachineDto machineDto, String defaultPaId);

    /**
     * 2.0 车辆已经在场
     * @param carNum
     * @param machineDto
     * @param paId
     * @return
     */
    InOutCarTextDto carInParkingArea(String carNum, MachineDto machineDto, String paId);


    /**
     * 出售车辆入场
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carInSaleCar(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto);

    /**
     * 内部车辆入场
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carInInnerCar(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto);
    /**
     * 免费车辆入场
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carInInnerNoMoney(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto);

    /**
     * 车辆进入 月租车剩余5天
     * @param carNum
     * @param machineDto
     * @param paId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carInLastFiveDay(String carNum, MachineDto machineDto, String paId, CarDayDto carDayDto);
    /**
     * 车辆进入 月租车剩余5天
     * @param carNum
     * @param machineDto
     * @param paId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carInMonthCar(String carNum, MachineDto machineDto, String paId, CarDayDto carDayDto);


    /**
     * 车辆过期
     * @param carNum
     * @param machineDto
     * @param paId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carInMonthExpire(String carNum, MachineDto machineDto, String paId, CarDayDto carDayDto);
    /**
     * 临时车进场
     * @param carNum
     * @param machineDto
     * @param paId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carInTempCar(String carNum, MachineDto machineDto, String paId, CarDayDto carDayDto);

    /******************************************************************* 车辆出场 配置****************************************/
    /**
     * 车辆未进场
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @return
     */
    InOutCarTextDto carNotInParkingArea(String carNum, MachineDto machineDto, String defaultPaId);

    /**
     * 白名单 车辆出场
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @return
     */
    InOutCarTextDto whiteCarOut(String carNum, MachineDto machineDto, String defaultPaId);


    /**
     * 出售车辆出场
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carOutSaleCar(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto);

    /**
     * 内部车辆出场
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carOutInnerCar(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto);
    /**
     * 免费车辆出场
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carOutInnerNoMoney(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto);

    /**
     * 月租车出场
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carOutMonthCar(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto);

    /**
     * 月租车已过期
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carOutMonthCarExpire(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto);

    /**
     * 车辆出场 临时车配置错误
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carOutConfigError(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto);


    /**
     * 临时车支付完成
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @return
     */
    InOutCarTextDto carOutFinishPayFee(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto);

    /**
     * 临时车出场 未付费
     * @param carNum
     * @param machineDto
     * @param defaultPaId
     * @param carDayDto
     * @param result
     * @return
     */
    InOutCarTextDto carOutNeedPayFee(String carNum, MachineDto machineDto, String defaultPaId, CarDayDto carDayDto, TempCarFeeResult result);

}

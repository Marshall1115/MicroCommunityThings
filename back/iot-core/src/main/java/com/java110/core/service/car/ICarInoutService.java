package com.java110.core.service.car;

import com.java110.entity.car.CarInoutDto;
import com.java110.entity.response.ResultDto;

import java.util.List;

/**
 * @ClassName ICarInoutService
 * @Description TODO 进出场服务类
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface ICarInoutService {

    /**
     * 保存车辆信息
     *
     * @param carInoutDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto saveCarInout(CarInoutDto carInoutDto) throws Exception;

    /**
     * 获取车辆信息
     *
     * @param carInoutDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto getCarInout(CarInoutDto carInoutDto) throws Exception;

    /**
     * 获取车辆信息
     *
     * @param carInoutDto 车辆信息
     * @return
     * @throws Exception
     */
    List<CarInoutDto> queryCarInout(CarInoutDto carInoutDto) throws Exception;

    /**
     * 修改车辆信息
     *
     * @param carInoutDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto updateCarInout(CarInoutDto carInoutDto) throws Exception;

    /**
     * 删除设备
     *
     * @param carInoutDto 车辆信息
     * @return
     * @throws Exception
     */
    ResultDto deleteCarInout(CarInoutDto carInoutDto) throws Exception;
}

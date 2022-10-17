package com.java110.core.service.parkingCouponCar;

import com.java110.entity.parkingCouponCar.ParkingCouponCarDto;
import com.java110.entity.response.ResultDto;

import java.util.List;

/**
 * @ClassName IParkingCouponCarService
 * @Description TODO 小区服务接口类
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IParkingCouponCarService {

    /**
     * 保存小区信息
     *
     * @param ParkingCouponCarDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto saveParkingCouponCar(ParkingCouponCarDto ParkingCouponCarDto) throws Exception;
    /**
     * 修改小区信息
     *
     * @param ParkingCouponCarDto 小区信息
     * @return
     * @throws Exception
     */
    ResultDto updateParkingCouponCar(ParkingCouponCarDto ParkingCouponCarDto) throws Exception;

    /**
     * 获取设备信息
     *
     * @param ParkingCouponCarDto 小区信息
     * @return
     * @throws Exception
     */
    ResultDto getParkingCouponCar(ParkingCouponCarDto ParkingCouponCarDto) throws Exception;

    /**
     * 获取设备信息
     *
     * @param ParkingCouponCarDto 小区信息
     * @return
     * @throws Exception
     */
    List<ParkingCouponCarDto> queryParkingCouponCars(ParkingCouponCarDto ParkingCouponCarDto) ;

    /**
     * 删除设备
     *
     * @param ParkingCouponCarDto 小区信息
     * @return
     * @throws Exception
     */
    ResultDto deleteParkingCouponCar(ParkingCouponCarDto ParkingCouponCarDto) throws Exception;

    double dealParkingCouponCar(double payChare,List<ParkingCouponCarDto> parkingCouponCarDtos);
}

package com.java110.core.dao;

import com.java110.entity.parkingCouponCar.ParkingCouponCarDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IParkingCouponCarServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IParkingCouponCarServiceDao {

    /**
     * 保存设备信息
     *
     * @param parkingCouponCarDto 设备信息
     * @return 返回影响记录数
     */
    int saveParkingCouponCar(ParkingCouponCarDto parkingCouponCarDto);

    /**
     * 查询设备信息
     * @param parkingCouponCarDto 设备信息
     * @return
     */
    List<ParkingCouponCarDto> getParkingCouponCars(ParkingCouponCarDto parkingCouponCarDto);


    /**
     * 修改设备信息
     *
     * @param parkingCouponCarDto 设备信息
     * @return 返回影响记录数
     */
    int updateParkingCouponCar(ParkingCouponCarDto parkingCouponCarDto);

    long getParkingCouponCarCount(ParkingCouponCarDto parkingCouponCarDto);
}

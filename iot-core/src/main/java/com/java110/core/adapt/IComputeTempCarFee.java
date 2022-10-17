package com.java110.core.adapt;

import com.java110.entity.car.CarInoutDto;
import com.java110.entity.car.TempCarFeeConfigDto;
import com.java110.entity.car.TempCarFeeResult;
import com.java110.entity.parkingCouponCar.ParkingCouponCarDto;

import java.util.List;

/**
 * 计算 临时车 停车费
 */
public interface IComputeTempCarFee {


    /**
     * 临时车停车费计算
     *
     * @param carInoutDto
     * @param tempCarFeeConfigDto
     * @return
     */
    TempCarFeeResult computeTempCarFee(CarInoutDto carInoutDto, TempCarFeeConfigDto tempCarFeeConfigDto,List<ParkingCouponCarDto> parkingCouponCarDtos) throws Exception;
}

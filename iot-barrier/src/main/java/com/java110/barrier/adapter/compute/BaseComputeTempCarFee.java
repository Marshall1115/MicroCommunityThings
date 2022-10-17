package com.java110.barrier.adapter.compute;

import com.java110.core.adapt.IComputeTempCarFee;
import com.java110.core.service.fee.ITempCarFeeConfigService;
import com.java110.entity.car.CarInoutDto;
import com.java110.entity.car.TempCarFeeConfigAttrDto;
import com.java110.entity.car.TempCarFeeConfigDto;
import com.java110.entity.car.TempCarFeeResult;
import com.java110.core.factory.TempCarFeeFactory;
import com.java110.core.service.fee.ITempCarFeeConfigService;
import com.java110.entity.parkingCouponCar.ParkingCouponCarDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class BaseComputeTempCarFee implements IComputeTempCarFee {
    @Autowired
    private ITempCarFeeConfigService tempCarFeeConfigServiceImpl;


    @Override
    public TempCarFeeResult computeTempCarFee(CarInoutDto carInoutDto,
                                              TempCarFeeConfigDto tempCarFeeConfigDto,
                                              List<ParkingCouponCarDto> parkingCouponCarDtos) throws Exception {
        TempCarFeeConfigAttrDto tempCarFeeConfigAttrDto = new TempCarFeeConfigAttrDto();
        tempCarFeeConfigAttrDto.setConfigId(tempCarFeeConfigDto.getConfigId());
        tempCarFeeConfigAttrDto.setCommunityId(tempCarFeeConfigDto.getCommunityId());

        List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos = tempCarFeeConfigServiceImpl.queryTempCarFeeConfigAttrs(tempCarFeeConfigAttrDto);
        //long couponMin = getCouponMin(parkingCouponCarDtos);
        TempCarFeeResult result = doCompute(carInoutDto, tempCarFeeConfigDto, tempCarFeeConfigAttrDtos,parkingCouponCarDtos);
        //获取停车时间
        long min = TempCarFeeFactory.getTempCarMin(carInoutDto);
        long hours = min / 60; //因为两者都是整数，你得到一个int
        long minutes = min%60;
        result.setMin(minutes);
        result.setHours(hours);
        return result;
    }

    /**
     * 计算 费用
     *
     * @param carInoutDto
     * @param tempCarFeeConfigDto
     * @param tempCarFeeConfigAttrDtos
     * @return
     */
    public abstract TempCarFeeResult doCompute(CarInoutDto carInoutDto,
                                               TempCarFeeConfigDto tempCarFeeConfigDto,
                                               List<TempCarFeeConfigAttrDto> tempCarFeeConfigAttrDtos,
                                               List<ParkingCouponCarDto> parkingCouponCarDtos);

    /**
     * 查询 停车分钟
     * @param parkingCouponCarDtos
     * @return
     */
    private long getCouponMin(List<ParkingCouponCarDto> parkingCouponCarDtos){

        long couponMin = 0;
        if(parkingCouponCarDtos == null || parkingCouponCarDtos.size()<1){
            return couponMin;
        }

        for(ParkingCouponCarDto parkingCouponCarDto : parkingCouponCarDtos){
            if(!ParkingCouponCarDto.TYPE_CD_HOURS.equals(parkingCouponCarDto.getTypeCd())){
                continue;
            }

            couponMin += Long.parseLong(parkingCouponCarDto.getValue());
        }

        return couponMin;
    }


}

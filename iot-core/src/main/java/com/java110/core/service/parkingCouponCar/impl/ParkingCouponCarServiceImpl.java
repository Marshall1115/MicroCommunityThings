package com.java110.core.service.parkingCouponCar.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.constant.SystemConstant;
import com.java110.core.dao.IParkingCouponCarServiceDao;
import com.java110.core.service.parkingCouponCar.IParkingCouponCarService;
import com.java110.core.util.StringUtil;
import com.java110.entity.PageDto;
import com.java110.entity.car.CarDto;
import com.java110.entity.parkingCouponCar.ParkingCouponCarDto;
import com.java110.entity.response.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ParkingCouponCarServiceImpl
 * @Description TODO 小区管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("parkingCouponCarServiceImpl")
public class ParkingCouponCarServiceImpl implements IParkingCouponCarService {

    @Autowired
    private IParkingCouponCarServiceDao parkingCouponCarServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 添加小区信息
     *
     * @param parkingCouponCarDto 小区对象
     * @return
     */
    @Override
    public ResultDto saveParkingCouponCar(ParkingCouponCarDto parkingCouponCarDto) throws Exception {
        int count = parkingCouponCarServiceDao.saveParkingCouponCar(parkingCouponCarDto);

        ResultDto resultDto = null;
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(parkingCouponCarDto.getTaskId())) {
            data.put("taskId", parkingCouponCarDto.getTaskId());
        }
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
        }
        return resultDto;
    }

    @Override
    public ResultDto updateParkingCouponCar(ParkingCouponCarDto parkingCouponCarDto) throws Exception {
        int count = parkingCouponCarServiceDao.updateParkingCouponCar(parkingCouponCarDto);
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(parkingCouponCarDto.getTaskId())) {
            data.put("taskId", parkingCouponCarDto.getTaskId());
        }
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
        }
        return resultDto;
    }

    /**
     * 查询小区信息
     *
     * @param parkingCouponCarDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getParkingCouponCar(ParkingCouponCarDto parkingCouponCarDto) throws Exception {

        if (parkingCouponCarDto.getPage() != PageDto.DEFAULT_PAGE) {
            parkingCouponCarDto.setPage((parkingCouponCarDto.getPage() - 1) * parkingCouponCarDto.getRow());
        }
        List<ParkingCouponCarDto> parkingCouponCarDtoList = null;

        long count = parkingCouponCarServiceDao.getParkingCouponCarCount(parkingCouponCarDto);
        List<CarDto> carDtoList = null;
        if (count > 0) {
            parkingCouponCarDtoList = parkingCouponCarServiceDao.getParkingCouponCars(parkingCouponCarDto);
        }else{
            parkingCouponCarDtoList = new ArrayList<>();
        }
        int totalPage = (int) Math.ceil((double) count / (double) parkingCouponCarDto.getRow());


        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, parkingCouponCarDtoList);
        return resultDto;
    }

    /**
     * 查询小区
     *
     * @param parkingCouponCarDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public List<ParkingCouponCarDto> queryParkingCouponCars(ParkingCouponCarDto parkingCouponCarDto)  {
        List<ParkingCouponCarDto> parkingCouponCarDtoList = parkingCouponCarServiceDao.getParkingCouponCars(parkingCouponCarDto);
        return parkingCouponCarDtoList;
    }

    @Override
    public ResultDto deleteParkingCouponCar(ParkingCouponCarDto parkingCouponCarDto) throws Exception {
        parkingCouponCarDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = parkingCouponCarServiceDao.updateParkingCouponCar(parkingCouponCarDto);
        JSONObject data = new JSONObject();
        if (StringUtil.isEmpty(parkingCouponCarDto.getTaskId())) {
            data.put("taskId", parkingCouponCarDto.getTaskId());
        }
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, data);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, data);
        }
        return resultDto;
    }


}

/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.api.extApi.parkingCouponCar;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.service.community.ICommunityService;
import com.java110.core.service.parkingArea.IParkingAreaService;
import com.java110.core.service.parkingCouponCar.IParkingCouponCarService;
import com.java110.core.util.Assert;
import com.java110.core.util.BeanConvertUtil;
import com.java110.core.util.SeqUtil;
import com.java110.entity.community.CommunityDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import com.java110.entity.parkingCouponCar.ParkingCouponCarDto;
import com.java110.entity.response.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 停车卷同步 接口类
 * add by 吴学文 2021-01-17
 * <p>
 * 该接口类为 需要将临时车费用等信息 同步时需要调用
 */

@RestController
@RequestMapping(path = "/extApi/parkingCouponCar")
public class ParkingCouponCarExtController {

    @Autowired
    ICommunityService communityServiceImpl;

    @Autowired
    IParkingAreaService parkingAreaServiceImpl;

    @Autowired
    private IParkingCouponCarService parkingCouponCarServiceImpl;

    /**
     * 添加或修改 停车卷
     * <p>
     *
     * @param reqParam
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addOrUpdateParkingCouponCar", method = RequestMethod.POST)
    public ResponseEntity<String> addOrUpdateParkingCouponCar(@RequestBody String reqParam) throws Exception {
        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "couponName", "未包含优惠券名称");
        Assert.hasKeyAndValue(reqJson, "shopName", "未包含商铺名称");
        Assert.hasKeyAndValue(reqJson, "extCommunityId", "未包含外部小区ID");
        Assert.hasKeyAndValue(reqJson, "extPaId", "未包含停车场ID");
        Assert.hasKeyAndValue(reqJson, "carNum", "未包含车牌号");
        Assert.hasKeyAndValue(reqJson, "giveWay", "未包含赠送方式 赠送方式 1001 扫码获取 2002 商家添加 3003 购物自动赠送");
        Assert.hasKeyAndValue(reqJson, "typeCd", "未包含减免方式 时长减免 1001 金额减免 2002 折扣减免 3003 全免 4004");
        Assert.hasKeyAndValue(reqJson, "value", "未包含面值");
        Assert.hasKeyAndValue(reqJson, "startTime", "未包含开始时间");
        Assert.hasKeyAndValue(reqJson, "endTime", "未包含结束时间");
        Assert.hasKeyAndValue(reqJson, "extPccId", "未包含外部ID");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        CommunityDto communityDto = new CommunityDto();
        communityDto.setExtCommunityId(reqJson.getString("extCommunityId"));
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        Assert.listOnlyOne(communityDtos, "未找到小区信息");

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setExtPaId(reqJson.getString("extPaId"));
        parkingAreaDto.setCommunityId(communityDtos.get(0).getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreas(parkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "未找到停车场信息");


        ParkingCouponCarDto parkingCouponCarDto = new ParkingCouponCarDto();
        parkingCouponCarDto.setExtPccId(reqJson.getString("extPccId"));
        List<ParkingCouponCarDto> parkingCouponCarDtos = parkingCouponCarServiceImpl.queryParkingCouponCars(parkingCouponCarDto);

        if (parkingCouponCarDtos == null || parkingCouponCarDtos.size() < 1) {
            parkingCouponCarDto = BeanConvertUtil.covertBean(reqJson, ParkingCouponCarDto.class);
            parkingCouponCarDto.setPccId(SeqUtil.getId());
            parkingCouponCarDto.setState(ParkingCouponCarDto.STATE_W);
            parkingCouponCarDto.setRemark("未使用");
            parkingCouponCarDto.setCommunityId(communityDtos.get(0).getCommunityId());
            parkingCouponCarDto.setPaId(parkingAreaDtos.get(0).getPaId());
            resultDto = parkingCouponCarServiceImpl.saveParkingCouponCar(parkingCouponCarDto);
        } else {
            parkingCouponCarDto = BeanConvertUtil.covertBean(reqJson, ParkingCouponCarDto.class);
            parkingCouponCarDto.setPccId(parkingCouponCarDtos.get(0).getPccId());
            parkingCouponCarDto.setState(ParkingCouponCarDto.STATE_W);
            parkingCouponCarDto.setRemark("未使用");
            parkingCouponCarDto.setCommunityId(communityDtos.get(0).getCommunityId());
            parkingCouponCarDto.setPaId(parkingAreaDtos.get(0).getPaId());
            resultDto = parkingCouponCarServiceImpl.updateParkingCouponCar(parkingCouponCarDto);
        }

        return ResultDto.createResponseEntity(resultDto);
    }


    /**
     * 删除 停车劵
     * <p>
     *
     * @param reqParam
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteParkingCouponCar", method = RequestMethod.POST)
    public ResponseEntity<String> deleteParkingCouponCar(@RequestBody String reqParam) throws Exception {
        JSONObject reqJson = JSONObject.parseObject(reqParam);
        Assert.hasKeyAndValue(reqJson, "extPccId", "未包含外部停车劵ID");
        Assert.hasKeyAndValue(reqJson, "extPaId", "未包含外部停车场ID");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");


        ParkingCouponCarDto parkingCouponCarDto = new ParkingCouponCarDto();
        parkingCouponCarDto.setExtPccId(reqJson.getString("extPccId"));
        List<ParkingCouponCarDto> parkingCouponCarDtos = parkingCouponCarServiceImpl.queryParkingCouponCars(parkingCouponCarDto);

        if (parkingCouponCarDtos == null || parkingCouponCarDtos.size() < 1) {
            return ResultDto.success();
        }
        parkingCouponCarDto = new ParkingCouponCarDto();
        parkingCouponCarDto.setPccId(parkingCouponCarDtos.get(0).getPccId());

        ResultDto resultDto = parkingCouponCarServiceImpl.deleteParkingCouponCar(parkingCouponCarDto);

        return ResultDto.createResponseEntity(resultDto);
    }
}

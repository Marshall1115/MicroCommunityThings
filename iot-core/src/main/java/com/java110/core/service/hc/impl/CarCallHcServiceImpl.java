package com.java110.core.service.hc.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.entity.car.BarrierGateControlDto;
import com.java110.core.service.app.IAppService;
import com.java110.core.service.hc.ICarCallHcService;
import com.java110.core.constant.SystemConstant;
import com.java110.entity.app.AppAttrDto;
import com.java110.entity.app.AppDto;
import com.java110.entity.car.CarInoutDto;
import com.java110.entity.community.CommunityDto;
import com.java110.entity.machine.MachineDto;
import com.java110.core.exception.Result;
import com.java110.core.exception.ServiceException;
import com.java110.core.factory.HttpFactory;
import com.java110.core.service.community.ICommunityService;
import com.java110.core.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用HC小区管理系统实现类
 * <p>
 * 演示地址：demo.homecommunity.cn
 * 代码：https://gitee.com/wuxw7/MicroCommunity
 *
 * @ClassName CarCallHcServiceImpl
 * @Description TODO
 * @Author wuxw
 * @Date 2021/1/18 20:54
 * @Version 1.0
 * add by wuxw 2021/1/18
 **/
@Service
public class CarCallHcServiceImpl implements ICarCallHcService {
    Logger logger = LoggerFactory.getLogger(CarCallHcServiceImpl.class);
    @Autowired
    private ICommunityService communityServiceImpl;

    @Autowired
    private IAppService appServiceImpl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Async
    public void carInout(CarInoutDto carInoutDto) throws Exception {
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(carInoutDto.getCommunityId());
        communityDto.setStatusCd("0");
        List<CommunityDto> communityDtos = communityServiceImpl.queryCommunitys(communityDto);

        Assert.listOnlyOne(communityDtos, "未包含小区信息");

        AppDto appDto = new AppDto();
        appDto.setAppId(communityDtos.get(0).getAppId());
        List<AppDto> appDtos = appServiceImpl.getApp(appDto);

        Assert.listOnlyOne(appDtos, "未找到应用信息");
        AppAttrDto appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_CAR_INOUT);

        if (appAttrDto == null) {
            return;
        }

        String value = appAttrDto.getValue();
        String securityCode = "";
        appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_SECURITY_CODE);
        if (appAttrDto != null) {
            securityCode = appAttrDto.getValue();
        }

        String url = value;
        Map<String, String> headers = new HashMap<>();
        headers.put("machineCode", carInoutDto.getMachineCode());
        headers.put("communityId", communityDtos.get(0).getExtCommunityId());

        JSONObject data = new JSONObject();
        data.put("carNum", carInoutDto.getCarNum());
        data.put("machineCode", carInoutDto.getMachineCode());
        data.put("communityId", communityDtos.get(0).getExtCommunityId());
        data.put("state",carInoutDto.getState());
        data.put("remark",carInoutDto.getRemark());
        if (CarInoutDto.INOUT_TYPE_IN.equals(carInoutDto.getInoutType())) {
            data.put("inTime", CarInoutDto.INOUT_TYPE_IN.equals(carInoutDto.getInoutType()) ? carInoutDto.getOpenTime() : "");
        } else {
            data.put("outTime", CarInoutDto.INOUT_TYPE_OUT.equals(carInoutDto.getInoutType()) ? carInoutDto.getOpenTime() : "");
            data.put("payCharge", carInoutDto.getPayCharge());
            data.put("realCharge", carInoutDto.getRealCharge());
            data.put("payType", carInoutDto.getPayType());
        }
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, url, data.toString(), headers, HttpMethod.POST, securityCode);

        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(Result.SYS_ERROR, "上传车辆失败" + tmpResponseEntity.getBody());
        }

    }

    @Override
    @Async
    public void notifyTempCarFeeOrder(CarInoutDto carInoutDto) throws Exception {
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(carInoutDto.getCommunityId());
        communityDto.setStatusCd("0");
        List<CommunityDto> communityDtos = communityServiceImpl.queryCommunitys(communityDto);

        Assert.listOnlyOne(communityDtos, "未包含小区信息");

        AppDto appDto = new AppDto();
        appDto.setAppId(communityDtos.get(0).getAppId());
        List<AppDto> appDtos = appServiceImpl.getApp(appDto);

        Assert.listOnlyOne(appDtos, "未找到应用信息");
        AppAttrDto appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_CAR_INOUT_PAY_FINISH);

        if (appAttrDto == null) {
            return;
        }

        String value = appAttrDto.getValue();
        String securityCode = "";
        appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_SECURITY_CODE);
        if (appAttrDto != null) {
            securityCode = appAttrDto.getValue();
        }

        String url = value;
        Map<String, String> headers = new HashMap<>();
        headers.put("machineCode", carInoutDto.getMachineCode());
        headers.put("communityId", communityDtos.get(0).getExtCommunityId());

        JSONObject data = new JSONObject();
        data.put("carNum", carInoutDto.getCarNum());
        data.put("machineCode", carInoutDto.getMachineCode());
        data.put("communityId", communityDtos.get(0).getExtCommunityId());
        data.put("payCharge", carInoutDto.getPayCharge());
        data.put("realCharge", carInoutDto.getRealCharge());
        data.put("payType", carInoutDto.getPayType());

        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, url, data.toString(), headers, HttpMethod.POST, securityCode);

        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(Result.SYS_ERROR, "上传车辆失败" + tmpResponseEntity.getBody());
        }
    }

    @Override
    @Async
    public void carInoutPageInfo(BarrierGateControlDto barrierGateControlDto, String extBoxId, MachineDto machineDto) throws Exception{
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDto.getCommunityId());
        List<CommunityDto> communityDtos = communityServiceImpl.queryCommunitys(communityDto);
        Assert.listOnlyOne(communityDtos, "小区不存在");

        barrierGateControlDto.setExtCommunityId(communityDtos.get(0).getExtCommunityId());
        barrierGateControlDto.setExtBoxId(extBoxId);
        barrierGateControlDto.setExtMachineId(machineDto.getExtMachineId());
        //上报第三方系统
        AppDto appDto = new AppDto();
        appDto.setAppId(communityDtos.get(0).getAppId());
        List<AppDto> appDtos = appServiceImpl.getApp(appDto);

        Assert.listOnlyOne(appDtos, "未找到应用信息");
        AppAttrDto appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_OPEN_PARKING_AREA_DOOR_CONTROL_LOG);

        if (appAttrDto == null) {
            return;
        }
        String value = appAttrDto.getValue();
        String upLoadAppId = "";
        String securityCode = "";
        appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_APP_ID);

        if (appAttrDto != null) {
            upLoadAppId = appAttrDto.getValue();
        }

        appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_SECURITY_CODE);
        if (appAttrDto != null) {
            securityCode = appAttrDto.getValue();
        }


        Map<String, String> headers = new HashMap<>();
        headers.put(SystemConstant.HTTP_APP_ID, upLoadAppId);
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, value, JSONObject.toJSONString(barrierGateControlDto), headers, HttpMethod.POST, securityCode);
        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            logger.error("执行结果失败" + tmpResponseEntity.getBody());
        }
    }
}

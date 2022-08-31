package com.java110.accessControl.adapter.sxoa;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.adapt.ICallAccessControlService;
import com.java110.core.util.Assert;
import com.java110.core.util.DateUtil;
import com.java110.core.util.SeqUtil;
import com.java110.core.util.StringUtil;
import com.java110.entity.accessControl.UserFaceDto;
import com.java110.entity.fee.FeeDto;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.machine.MachineFaceDto;
import com.java110.entity.openDoor.OpenDoorDto;
import com.java110.entity.room.RoomDto;
import com.java110.core.factory.LocalCacheFactory;
import com.java110.core.factory.MappingCacheFactory;
import com.java110.core.factory.NotifyAccessControlFactory;
import com.java110.core.service.machine.IMachineService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class SxoaQueryAccessControlInoutLogAdapt {
    private static Logger logger = LoggerFactory.getLogger(SxoaQueryAccessControlInoutLogAdapt.class);

    public static final String GET_TOKEN = "/oauth/token";
    private static final String QUERY_ACCESS_CONTROL_LOG = "/v1.0/openrecord/page";
    private static final String MSG_TYPE_EVENT_ACCESS = "community_event_access";

    @Autowired
    private ICallAccessControlService callAccessControlServiceImpl;

    private static String consumerId = "";

    public static final String OPEN_TYPE_FACE = "1000"; // 人脸开门

    @Autowired
    private RestTemplate outRestTemplate;


    @Autowired
    private IMachineService machineServiceImpl;


    public void query() {


//        JSONObject postParameters = new JSONObject();
//        postParameters.put("consumerId", consumerId);
//        postParameters.put("autoCommit", true);
//
//        String paramOutString = HttpClient.doPost(url, postParameters.toJSONString(), "Bearer " + getToken(), "POST");

        //添加设备
        JSONObject paramIn = new JSONObject();
        paramIn.put("pageNo", 1);
        paramIn.put("pageSize", 20);
        paramIn.put("orTime", DateUtil.getFormatTimeString(new Date(),DateUtil.DATE_FORMATE_STRING_B));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,-1);
        paramIn.put("startTime", DateUtil.getFormatTimeString(calendar.getTime(),DateUtil.DATE_FORMATE_STRING_A));
        paramIn.put("endTime", DateUtil.getFormatTimeString(new Date(),DateUtil.DATE_FORMATE_STRING_A));

        logger.debug("------请求报文：{}" , paramIn.toJSONString());

        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), SxCommomFactory.getHeader(outRestTemplate));
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + QUERY_ACCESS_CONTROL_LOG, HttpMethod.POST, httpEntity, String.class);

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        logger.debug("------返回信息：{}" + paramOut);

        if (paramOut.getIntValue("code") != 0) {
            return;
        }

        JSONArray data = paramOut.getJSONObject("data").getJSONArray("resultList");

        if (data == null || data.size() < 1) {
            return;
        }

        JSONObject dataObj = null;
        for (int dataIndex = 0; dataIndex < data.size(); dataIndex++) {
            dataObj = data.getJSONObject(dataIndex);
            httpFaceResult(dataObj);

        }

    }


    public String httpFaceResult(JSONObject content) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        JSONObject resultParam = new JSONObject();
        List<MachineDto> machineDtos = null;
        try {
            JSONObject body = content;

            MachineDto machineDto = new MachineDto();
            machineDto.setMachineName(body.getString("orDeviceName"));
            machineDtos = callAccessControlServiceImpl.queryMachines(machineDto);

            Assert.listOnlyOne(machineDtos, "设备不存在");

            String userId = body.containsKey("orResidentId") ? body.getString("orResidentId") : "";
            String userName = "";
            String createTime = "";
            if (!StringUtils.isEmpty(userId)) {
                MachineFaceDto machineFaceDto = new MachineFaceDto();
                machineFaceDto.setExtUserId(userId);
                machineFaceDto.setMachineId(machineDtos.get(0).getMachineId());
                List<MachineFaceDto> machineFaceDtos = notifyAccessControlService.queryMachineFaces(machineFaceDto);
                if (machineFaceDtos != null && machineFaceDtos.size() > 0) {
                    userName = machineFaceDtos.get(0).getName();
                    userId = machineFaceDtos.get(0).getUserId();
                }
            } else {
                userName = body.getString("orResidentName");
            }
            if (StringUtil.isEmpty(userId)) {
                userId = "-1";
            }
            if(StringUtil.isEmpty(userName)){
                userName = body.getString("orResidentName");
            }
            if (StringUtil.isEmpty(userName)) {
                userName = "门禁未上报";
            }

            createTime = body.getString("orTime");

            OpenDoorDto openDoorDto = new OpenDoorDto();
            //openDoorDto.setFace(ImageFactory.encodeImageToBase64(body.getString("orPrintscreen")));
            openDoorDto.setFace("");
            openDoorDto.setUserName(userName);
            openDoorDto.setHat("3");
            openDoorDto.setMachineCode(machineDtos.get(0).getMachineCode());
            openDoorDto.setUserId(userId);
            openDoorDto.setOpenId(SeqUtil.getId());
            openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);
            openDoorDto.setSimilarity("1");
            openDoorDto.setCreateTime(createTime);
            freshOwnerFee(openDoorDto);

            notifyAccessControlService.saveFaceResult(openDoorDto);

        } catch (Exception e) {
            logger.error("推送人脸失败", e);
        }
        resultParam.put("result", 1);
        resultParam.put("success", true);
        return resultParam.toJSONString();//未找到设备

    }


    /**
     * 获取accessToken
     *
     * @return
     */
    private String getToken() {

        String token = LocalCacheFactory.getValue("hik_token");
        if (!StringUtil.isEmpty(token)) {
            return token;
        }
        String url = MappingCacheFactory.getValue("HIK_URL") + GET_TOKEN;
        String appId = MappingCacheFactory.getValue("client_id");
        String appKey = MappingCacheFactory.getValue("client_secret");

        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("client_id", appId);
        postParameters.add("client_secret", appKey);
        postParameters.add("grant_type", "client_credentials");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(postParameters, httpHeaders);
        logger.debug("------请求信息：", httpEntity.toString());
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求百胜获取token失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        if (!paramOut.containsKey("access_token")) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }

        token = paramOut.getString("access_token");
        LocalCacheFactory.setValue("hik_token", token, 10 * 24 * 60);
        return token;
    }


    /**
     * 查询费用信息
     *
     * @param openDoorDto
     */
    protected void freshOwnerFee(OpenDoorDto openDoorDto) {

        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        List<FeeDto> feeDtos = new ArrayList<>();
        try {
            //查询业主房屋信息
            UserFaceDto userFaceDto = new UserFaceDto();
            userFaceDto.setUserId(openDoorDto.getUserId());
            List<RoomDto> roomDtos = notifyAccessControlService.getRooms(userFaceDto);

            if (roomDtos == null || roomDtos.size() < 1) {
                return;
            }

            for (RoomDto roomDto : roomDtos) {
                List<FeeDto> tmpFeeDtos = notifyAccessControlService.getFees(roomDto);
                if (tmpFeeDtos == null || tmpFeeDtos.size() < 1) {
                    continue;
                }
                feeDtos.addAll(tmpFeeDtos);
            }
        } catch (Exception e) {
            logger.error("云端查询物业费失败", e);
        }

        if (feeDtos.size() < 1) {
            openDoorDto.setAmountOwed("0");
            return;
        }
        double own = 0.00;
        for (FeeDto feeDto : feeDtos) {
            logger.debug("查询费用信息" + JSONObject.toJSONString(feeDto));
            own += feeDto.getAmountOwed();
        }

        openDoorDto.setAmountOwed(own + "");
    }
}

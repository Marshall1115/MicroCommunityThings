package com.java110.things.adapt.accessControl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.accessControl.OpenDoorMonitorWebSocketServer;
import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IMachineFaceServiceDao;
import com.java110.things.dao.IMachineServiceDao;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.app.AppAttrDto;
import com.java110.things.entity.app.AppDto;
import com.java110.things.entity.cloud.MachineCmdResultDto;
import com.java110.things.entity.cloud.MachineHeartbeatDto;
import com.java110.things.entity.cloud.MachineUploadFaceDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.fee.FeeDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.machine.OperateLogDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.room.RoomDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ServiceException;
import com.java110.things.exception.ThreadException;
import com.java110.things.factory.HttpFactory;
import com.java110.things.factory.ImageFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.app.IAppService;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineFaceService;
import com.java110.things.service.machine.IOperateLogService;
import com.java110.things.service.openDoor.IOpenDoorService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.DateUtil;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName NotifyAccessControlServcieImpl
 * @Description TODO ?????? ???????????????????????????
 * @Author wuxw
 * @Date 2020/5/15 19:11
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Service("callAccessControlServiceImpl")
public class CallAccessControlServiceImpl implements ICallAccessControlService {
    Logger logger = LoggerFactory.getLogger(CallAccessControlServiceImpl.class);

    public static final String FACE_RESULT = "-result";

    @Autowired
    IMachineServiceDao machineServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ICommunityService communityServiceImpl;

    @Autowired
    private IOperateLogService operateLogServiceImpl;

    @Autowired
    private IOpenDoorService openDoorServiceImpl;

    @Autowired
    private IMachineFaceServiceDao machineFaceServiceDaoImpl;

    @Autowired
    private IAppService appServiceImpl;

    @Autowired
    private IMachineFaceService machineFaceService;

    /**
     * ??????????????????
     *
     * @return
     */
    public List<MachineDto> queryMachines() throws Exception {

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ACCESS_CONTROL);
        List<MachineDto> machineDtos = machineServiceDao.getMachines(machineDto);

        return machineDtos;

    }

    /**
     * ??????????????????
     *
     * @return
     */
    public List<MachineDto> queryMachines(MachineDto machineDto) throws Exception {

        machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ACCESS_CONTROL);
        List<MachineDto> machineDtos = machineServiceDao.getMachines(machineDto);

        return machineDtos;

    }

    /**
     * ??????????????????
     *
     * @return
     */
    public List<MachineFaceDto> queryMachineFaces(MachineFaceDto machineFaceDto)  {

        machineFaceDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ACCESS_CONTROL);
        List<MachineFaceDto> machineFaceDtos = machineFaceServiceDaoImpl.getMachineFaces(machineFaceDto);

        return machineFaceDtos;

    }


    @Override
    public void uploadMachine(MachineDto machineDto) {

        if (machineDto == null) {
            throw new ServiceException(Result.SYS_ERROR, "????????????????????????");
        }

        logger.debug("machineDto", machineDto.toString());
        //????????????ID???????????????
        if (StringUtil.isEmpty(machineDto.getMachineId())) {
            machineDto.setMachineId(UUID.randomUUID().toString());
        }
        //????????????
        if (StringUtil.isEmpty(machineDto.getMachineCode())) {
            throw new ServiceException(Result.SYS_ERROR, "?????????????????????????????????????????????????????????mac??????ip,????????????????????????");
        }
        //??????IP
        if (StringUtil.isEmpty(machineDto.getMachineIp())) {
            throw new ServiceException(Result.SYS_ERROR, "???????????????ip");
        }
        //??????mac
        if (StringUtil.isEmpty(machineDto.getMachineMac())) {
            machineDto.setMachineMac(machineDto.getMachineIp());
        }
        // ????????????
        if (StringUtil.isEmpty(machineDto.getMachineName())) {
            machineDto.setMachineName(machineDto.getMachineCode());
        }
        machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ACCESS_CONTROL);

        MachineDto tmpMachineDto = new MachineDto();
        tmpMachineDto.setMachineVersion(machineDto.getMachineVersion());
        tmpMachineDto.setMachineCode(machineDto.getMachineCode());
        tmpMachineDto.setMachineIp(machineDto.getMachineIp());
        tmpMachineDto.setMachineMac(machineDto.getMachineMac());

        long machineCnt = machineServiceDao.getMachineCount(tmpMachineDto);
        if (machineCnt > 0) {
            logger.debug("????????????????????????????????????" + tmpMachineDto.toString());
            return;
        }

        machineServiceDao.saveMachine(machineDto);

        try {
            //????????????
            uploadCloud(machineDto);
        } catch (Exception e) {
            logger.error("??????????????????", e);
            throw new ServiceException(Result.SYS_ERROR, "??????????????????" + e);

        }
    }

    /**
     * ??????????????????
     *
     * @param operateLogDto ??????????????????logId ???????????????????????????????????????????????? ?????????
     */
    @Override
    public void saveOrUpdateOperateLog(OperateLogDto operateLogDto) {
        if (StringUtil.isEmpty(operateLogDto.getLogId())) {
            throw new ServiceException(Result.SYS_ERROR, "???????????????ID???????????? ?????????????????????????????????????????? ?????????");
        }

//        if (StringUtil.isEmpty(operateLogDto.getMachineId())) {
//            throw new ServiceException(Result.SYS_ERROR, "??????ID????????????machineDto ???????????????????????????????????????machineCode ????????????");
//        }
//        if(StringUtil.isEmpty(operateLogDto.getState())){
//            throw new ServiceException(Result.SYS_ERROR, "??????????????????????????? 10001,????????????10002,???????????? 10003");
//        }

//        if (StringUtil.isEmpty(operateLogDto.getOperateType())) {
//            throw new ServiceException(Result.SYS_ERROR, "???????????????????????????t_dict ???");
//        }

        if (StringUtil.isEmpty(operateLogDto.getState())) {
            operateLogDto.setState("10001"); // ?????????????????????
        }
        if (StringUtil.isEmpty(operateLogDto.getMachineTypeCd())) {
            operateLogDto.setMachineTypeCd("9999");
        }


        operateLogServiceImpl.saveOperateLog(operateLogDto);
    }

    @Override
    public void saveFaceResult(OpenDoorDto openDoorDto) throws Exception {

        String faceBase = openDoorDto.getFace();
        String facePath = "/" + openDoorDto.getMachineCode() + FACE_RESULT + "/" + openDoorDto.getUserId() + "-" + openDoorDto.getOpenId() + ".jpg";

        ImageFactory.GenerateImage(faceBase, facePath);

        openDoorDto.setFace(facePath);
        MachineDto tmpMachineDto = new MachineDto();
        tmpMachineDto.setMachineCode(openDoorDto.getMachineCode());

        List<MachineDto> machineDtos = machineServiceDao.getMachines(tmpMachineDto);

        if (machineDtos == null || machineDtos.size() < 1) {
            throw new ServiceException(Result.SYS_ERROR, "??????????????????");
        }

        openDoorDto.setMachineId(machineDtos.get(0).getMachineId());
        openDoorDto.setMachineName(machineDtos.get(0).getMachineName());
        openDoorDto.setMachineIp(machineDtos.get(0).getMachineIp());

        String modelFacePath = "/" + machineDtos.get(0).getCommunityId() + "/" + openDoorDto.getUserId() + ".jpg";
        openDoorDto.setModelFace(modelFacePath);
        openDoorDto.setCommunityId(machineDtos.get(0).getCommunityId());
        //?????? ????????????
        ResultDto resultDto = openDoorServiceImpl.saveOpenDoor(openDoorDto);

        if (ResponseConstant.SUCCESS != resultDto.getCode()) {
            throw new ServiceException(Result.SYS_ERROR, resultDto.getMsg());
        }

        String faceUrl = MappingCacheFactory.getValue("ACCESS_CONTROL_FACE_URL");
        openDoorDto.setFace(faceUrl + "/" + openDoorDto.getFace());
        openDoorDto.setModelFace(faceUrl + "/" + openDoorDto.getModelFace());
        openDoorDto.setCreateTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));

        OpenDoorMonitorWebSocketServer.sendInfo(JSONObject.toJSONString(openDoorDto), machineDtos.get(0).getMachineId());

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDtos.get(0).getCommunityId());
        communityDto.setStatusCd("0");
        List<CommunityDto> communityDtos = communityServiceImpl.queryCommunitys(communityDto);

        Assert.listOnlyOne(communityDtos, "?????????????????????");

        AppDto appDto = new AppDto();
        appDto.setAppId(communityDtos.get(0).getAppId());
        List<AppDto> appDtos = appServiceImpl.getApp(appDto);

        Assert.listOnlyOne(appDtos, "?????????????????????");
        AppAttrDto appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_UPLOAD_FACE_URL);

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


        //????????????
        MachineUploadFaceDto machineUploadFaceDto = new MachineUploadFaceDto();
        machineUploadFaceDto.setDateTime(DateUtil.getFormatTimeString(new Date(), DateUtil.DATE_FORMATE_STRING_A));
        machineUploadFaceDto.setMachineCode(machineDtos.get(0).getMachineCode());
        machineUploadFaceDto.setOpenTypeCd("1000");
        machineUploadFaceDto.setPhoto(faceBase);
        machineUploadFaceDto.setSimilar(openDoorDto.getSimilarity());
        machineUploadFaceDto.setUserId(openDoorDto.getUserId());
        machineUploadFaceDto.setUserName(openDoorDto.getUserName());
        machineUploadFaceDto.setRecordTypeCd(StringUtil.isEmpty(openDoorDto.getUserId()) ? "6666" : "8888");
        machineUploadFaceDto.setExtCommunityId(communityDtos.get(0).getExtCommunityId());
        machineUploadFaceDto.setIdNumber(openDoorDto.getIdNumber());
        machineUploadFace(machineUploadFaceDto, value, upLoadAppId, securityCode);

    }

    /**
     * ??????????????????
     *
     * @param userFaceDto ????????????????????????
     * @return
     */
    @Override
    public List<RoomDto> getRooms(UserFaceDto userFaceDto) throws Exception {

        //?????? ????????????
        CommunityDto communityDto = new CommunityDto();
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "????????????????????????");
        }

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        if (communityDtos == null || communityDtos.size() < 1) {
            throw new ThreadException(Result.SYS_ERROR, "????????????????????????????????????????????????");
        }

        String url = MappingCacheFactory.getValue("CLOUD_API") + "/api/room.queryRoomsByOwner";
        JSONObject paramIn = new JSONObject();
        paramIn.put("communityId", communityDtos.get(0).getCommunityId());
        paramIn.put("ownerId", userFaceDto.getUserId());
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, url + HttpFactory.mapToUrlParam(paramIn), paramIn.toJSONString(), HttpMethod.GET);

        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            logger.error("??????????????????" + tmpResponseEntity.getBody());

            return null;
        }

        JSONObject paramOut = JSONObject.parseObject(tmpResponseEntity.getBody());
        if (!paramOut.containsKey("rooms") || paramOut.getJSONArray("rooms").size() < 1) {
            return null;
        }

        JSONArray rooms = paramOut.getJSONArray("rooms");
        List<RoomDto> roomDtos = new ArrayList<>();
        for (int roomIndex = 0; roomIndex < rooms.size(); roomIndex++) {
            roomDtos.add(BeanConvertUtil.covertBean(rooms.get(roomIndex), RoomDto.class));
        }
        return roomDtos;
    }

    @Override
    public List<FeeDto> getFees(RoomDto roomDto) throws Exception {

        //?????? ????????????
        CommunityDto communityDto = new CommunityDto();
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "????????????????????????");
        }

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        if (communityDtos == null || communityDtos.size() < 1) {
            throw new ThreadException(Result.SYS_ERROR, "????????????????????????????????????????????????");
        }

        String url = MappingCacheFactory.getValue("CLOUD_API") + "/api/fee.listFee";
        JSONObject paramIn = new JSONObject();
        paramIn.put("communityId", communityDtos.get(0).getCommunityId());
        paramIn.put("page", 1);
        paramIn.put("row", 100);
        paramIn.put("payerObjId", roomDto.getRoomId());
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, url + HttpFactory.mapToUrlParam(paramIn), paramIn.toJSONString(), HttpMethod.GET);

        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            logger.error("??????????????????" + tmpResponseEntity.getBody());

            return null;
        }

        JSONObject paramOut = JSONObject.parseObject(tmpResponseEntity.getBody());
        if (!paramOut.containsKey("fees") || paramOut.getJSONArray("fees").size() < 1) {
            return null;
        }

        JSONArray fees = paramOut.getJSONArray("fees");
        List<FeeDto> feeDtos = new ArrayList<>();
        for (int feeIndex = 0; feeIndex < fees.size(); feeIndex++) {
            feeDtos.add(BeanConvertUtil.covertBean(fees.get(feeIndex), FeeDto.class));
        }
        Calendar now = Calendar.getInstance();
        for (FeeDto feeDto : feeDtos) {
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(feeDtos.get(0).getEndTime());
            int surplus = now.get(Calendar.DATE) - endDate.get(Calendar.DATE);
            int result = now.get(Calendar.MONTH) - endDate.get(Calendar.MONTH);
            int month = (now.get(Calendar.YEAR) - endDate.get(Calendar.YEAR)) * 12;
            int monthSub = (Math.abs(month + result) + surplus);
            if (monthSub < 0) {
                feeDto.setAmountOwed(0.00);
            }
            BigDecimal additionalAmount = new BigDecimal(feeDto.getFeePrice());
            BigDecimal monthDec = new BigDecimal(monthSub);
            feeDto.setAmountOwed(monthDec.multiply(additionalAmount).doubleValue());
        }


        return feeDtos;
    }

    /**
     * ??????????????????
     *
     * @param machineUploadFaceDto ????????????
     * @throws Exception
     */
    @Override
    public void machineUploadFace(MachineUploadFaceDto machineUploadFaceDto, String url) throws Exception {
        machineUploadFace(machineUploadFaceDto, url, "", "");

    }


    /**
     * ??????????????????
     *
     * @param machineUploadFaceDto ????????????
     * @throws Exception
     */
    public void machineUploadFace(MachineUploadFaceDto machineUploadFaceDto, String url, String appId, String securityCode) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put(SystemConstant.HTTP_APP_ID, appId);
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, url, machineUploadFaceDto.toString(), headers, HttpMethod.POST, securityCode);
        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(Result.SYS_ERROR, "??????????????????" + tmpResponseEntity.getBody());
        }

    }


    @Override
    public void machineCmdResult(MachineCmdResultDto machineCmdResultDto) throws Exception {

        OperateLogDto operateLogDto = new OperateLogDto();
        operateLogDto.setLogId(machineCmdResultDto.getTaskId());
        List<OperateLogDto> operateLogDtos = operateLogServiceImpl.queryOperateLogs(operateLogDto);

        Assert.listOnlyOne(operateLogDtos, "???????????????????????????");

        //??? ??????????????????
        OperateLogDto tmpOperateLogDto = new OperateLogDto();
        tmpOperateLogDto.setLogId(machineCmdResultDto.getTaskId());
        tmpOperateLogDto.setState(machineCmdResultDto.getCode() == 0 ? "10002" : "10003");
        tmpOperateLogDto.setResParam(machineCmdResultDto.getResJson());

        //??????????????????
        operateLogServiceImpl.saveOperateLog(tmpOperateLogDto);
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineId(operateLogDtos.get(0).getMachineId());
        List<MachineDto> machineDtos = queryMachines(machineDto);

        Assert.listOnlyOne(machineDtos, "???????????????");

        //??????????????????
        if (!StringUtil.isEmpty(operateLogDtos.get(0).getUserId())) {
            MachineFaceDto machineFaceDto = new MachineFaceDto();
            machineFaceDto.setUserId(operateLogDtos.get(0).getUserId());
            machineFaceDto.setMachineId(machineDtos.get(0).getMachineId());
            machineFaceDto.setState(machineCmdResultDto.getCode() == 0 ? "10002" : "10003");
            machineFaceDto.setMessage(machineCmdResultDto.getMsg());
            machineFaceService.updateMachineFace(machineFaceDto);
        }

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDtos.get(0).getCommunityId());
        communityDto.setStatusCd("0");
        List<CommunityDto> communityDtos = communityServiceImpl.queryCommunitys(communityDto);

        Assert.listOnlyOne(communityDtos, "?????????????????????");

        AppDto appDto = new AppDto();
        appDto.setAppId(communityDtos.get(0).getAppId());
        List<AppDto> appDtos = appServiceImpl.getApp(appDto);

        Assert.listOnlyOne(appDtos, "?????????????????????");
        AppAttrDto appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_UPLOAD_CMD_URL);

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

        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, value, machineCmdResultDto.toString(), headers, HttpMethod.POST, securityCode);
        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            logger.error("??????????????????" + tmpResponseEntity.getBody());
        }
    }

    @Override
    public void machineHeartbeat(MachineHeartbeatDto machineHeartbeatDto) throws Exception {

        Assert.hasLength(machineHeartbeatDto.getMachineCode(), "?????????????????????");
        if (machineHeartbeatDto.getHeartbeatTime() == null) {
            machineHeartbeatDto.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        }

        //????????????????????????
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineHeartbeatDto.getMachineCode());
        List<MachineDto> machineDtos = machineServiceDao.getMachines(machineDto);

        Assert.listOnlyOne(machineDtos, "???????????????");
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDtos.get(0).getCommunityId());
        List<CommunityDto> communityDtos = communityServiceImpl.queryCommunitys(communityDto);
        Assert.listOnlyOne(communityDtos, "???????????????");

        machineHeartbeatDto.setExtCommunityId(communityDtos.get(0).getExtCommunityId());

        machineHeartbeatDto.setTaskId(SeqUtil.getId());

        //?????? ????????????
        MachineDto tmpMachineDto = new MachineDto();
        tmpMachineDto.setMachineId(machineDtos.get(0).getMachineId());
        tmpMachineDto.setHeartbeatTime(machineHeartbeatDto.getHeartbeatTime());
        machineServiceDao.updateMachine(tmpMachineDto);

        //?????????????????????

        AppDto appDto = new AppDto();
        appDto.setAppId(communityDtos.get(0).getAppId());
        List<AppDto> appDtos = appServiceImpl.getApp(appDto);

        Assert.listOnlyOne(appDtos, "?????????????????????");
        AppAttrDto appAttrDto = appDtos.get(0).getAppAttr(AppAttrDto.SPEC_CD_UPLOAD_HEARTBEAT);

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
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, value, JSONObject.toJSONString(machineHeartbeatDto), headers, HttpMethod.POST, securityCode);
        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            logger.error("??????????????????" + tmpResponseEntity.getBody());
        }
    }

    /**
     * ????????????
     *
     * @param machineDto
     */
    private void uploadCloud(MachineDto machineDto) throws Exception {
        //?????? ????????????
        CommunityDto communityDto = new CommunityDto();
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "????????????????????????");
        }

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        if (communityDtos == null || communityDtos.size() < 1) {
            throw new ThreadException(Result.SYS_ERROR, "????????????????????????????????????????????????");
        }
        String url = MappingCacheFactory.getValue("CLOUD_API") + "/api/machine.listMachines?page=1&row=1&communityId="
                + communityDtos.get(0).getCommunityId() + "&machineCode=" + machineDto.getMachineCode();
        //?????????????????????????????????
        ResponseEntity<String> responseEntity = HttpFactory.exchange(restTemplate, url, "", HttpMethod.GET);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(Result.SYS_ERROR, responseEntity.getBody());
        }
        JSONObject result = JSONObject.parseObject(responseEntity.getBody());

        int total = result.getInteger("total");
        //??????????????????
        if (total > 0) {
            return;
        }
        url = MappingCacheFactory.getValue("CLOUD_API") + "/api/machine.saveMachine";
        JSONObject paramIn = new JSONObject();
        paramIn.put("machineCode", machineDto.getMachineCode());
        paramIn.put("machineVersion", machineDto.getMachineVersion());
        paramIn.put("machineName", machineDto.getMachineName());
        paramIn.put("machineIp", machineDto.getMachineIp());
        paramIn.put("machineMac", machineDto.getMachineMac());
        paramIn.put("machineTypeCd", machineDto.getMachineTypeCd());
        paramIn.put("direction", "3306");//?????????????????????
        paramIn.put("authCode", "123");
        paramIn.put("locationTypeCd", "4000");
        paramIn.put("locationObjId", "-1");
        paramIn.put("communityId", communityDtos.get(0).getCommunityId());
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, url, paramIn.toJSONString(), HttpMethod.POST);

        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            logger.error("????????????????????????" + tmpResponseEntity.getBody());
        }


    }
}

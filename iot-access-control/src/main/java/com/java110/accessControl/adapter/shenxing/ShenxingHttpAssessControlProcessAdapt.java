package com.java110.accessControl.adapter.shenxing;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.adapter.DefaultAbstractAccessControlAdapt;
import com.java110.core.adapt.ICallAccessControlService;
import com.java110.core.factory.MappingCacheFactory;
import com.java110.core.factory.NotifyAccessControlFactory;
import com.java110.core.service.machine.IMachineService;
import com.java110.core.thread.AddUpdateFace;
import com.java110.core.util.DateUtil;
import com.java110.core.util.SeqUtil;
import com.java110.core.util.StringUtil;
import com.java110.entity.accessControl.HeartbeatTaskDto;
import com.java110.entity.accessControl.UserFaceDto;
import com.java110.entity.cloud.MachineHeartbeatDto;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.machine.MachineFaceDto;
import com.java110.entity.openDoor.OpenDoorDto;
import com.java110.entity.response.ResultDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

/**
 * 伊兰度 门禁设备 Mqtt 方式
 */
@Service("shenxingHttpAssessControlProcessAdapt")
public class ShenxingHttpAssessControlProcessAdapt extends DefaultAbstractAccessControlAdapt {

    private static Logger logger = LoggerFactory.getLogger(ShenxingHttpAssessControlProcessAdapt.class);
    //public static Function fun=new Function();

    private static final String DEFAULT_PORT = "8090"; //端口

    @Autowired
    private IMachineService machineServiceImpl;

    @Autowired
    private RestTemplate restTemplate;

    public static final long START_TIME = new Date().getTime() - 1000 * 60 * 60;
    public static final long END_TIME = new Date().getTime() + 1000 * 60 * 60 * 24 * 365;

    public static final String OPEN_TYPE_FACE = "1000"; // 人脸开门

    //平台名称
    public static final String MANUFACTURER = "CJ_";

    public static final String VERSION = "0.2";

    public static final String CMD_ADD_FACE = "/face"; // 创建人脸
    public static final String CMD_ADD_FACE_FIND = "/face/find"; // 创建人脸

    public static final String CMD_OPEN_DOOR = "/api/devices/io"; // 开门

    public static final String CMD_REBOOT = "/api/devices/reboot";// 重启设备

    public static final String CMD_ADD_USER = "/api/persons/item"; // 添加人员
    public static final String CMD_UPDATE_USER = "/api/persons/item/"; // 修改人员


    public static final String CMD_DELETE_FACE = "/api/persons/item/"; //删除人脸
    public static final String CMD_RESET = "/device/reset"; //设备重置


    public static final String SN = "{sn}";

    public static final String FACE_URL = "ACCESS_CONTROL_FACE_URL";

    public static final String FACE_RESULT = "face_result";

    //图片后缀
    public static final String IMAGE_SUFFIX = ".jpg";

    public static final String ADD_MACHINE = "/api/schedules/item";
    //delete machien
    public static final String DELETE_MACHINE = "/api/schedules/item/";

    public static final String ADD_GROUP = "/api/groups/item";

    public static final String DELETE_GROUP = "/api/groups/item/";

    @Override
    public void initAssessControlProcess() {
        logger.debug("初始化是配置器");

    }

    @Override
    public ResultDto addMachine(MachineDto machineDto) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("schedule_name", machineDto.getMachineName());
        paramIn.put("schedule_list_type", "json");
        JSONObject scheduleObj = new JSONObject();
        scheduleObj.put("type", "workday");
        JSONObject rule = new JSONObject();
        rule.put("1", "0:00-23:59");
        rule.put("2", "0:00-23:59");
        rule.put("3", "0:00-23:59");
        rule.put("4", "0:00-23:59");
        rule.put("5", "0:00-23:59");
        rule.put("6", "0:00-23:59");
        rule.put("7", "0:00-23:59");
        scheduleObj.put("rule", rule);
        JSONArray schedules = new JSONArray();
        schedules.add(scheduleObj);
        paramIn.put("schedule_list", schedules);
        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), ShenxingFactory.getHeader(machineDto.getMachineCode(), restTemplate));
        ResponseEntity<String> responseEntity = restTemplate.exchange(MappingCacheFactory.getValue("Shenxing_URL") + ADD_MACHINE, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_FACE_FIND, paramIn.toJSONString(), responseEntity.getBody());
        if (responseEntity.getStatusCode().value()  >= 400) {
            throw new IllegalStateException("请求添加权限组失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (!paramOut.containsKey("id")) {
            throw new IllegalStateException("添加权限组失败" + responseEntity);
        }

        String schedule_id = paramOut.getString("id");

        // /api/groups/item
        paramIn = new JSONObject();
        paramIn.put("group_name", machineDto.getMachineName());
        paramIn.put("schedule_id", paramOut.getString("id"));
        //paramIn.put("request_id","");
        httpEntity = new HttpEntity(paramIn.toJSONString(), ShenxingFactory.getHeader(machineDto.getMachineCode(), restTemplate));
        responseEntity = restTemplate.exchange(MappingCacheFactory.getValue("Shenxing_URL") + ADD_GROUP, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        if (responseEntity.getStatusCode().value()  >= 400) {
            throw new IllegalStateException("请求添加权限组失败" + responseEntity);
        }

        paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (!paramOut.containsKey("id")) {
            throw new IllegalStateException("添加权限组失败" + responseEntity);
        }

        machineDto.setThirdMachineId(schedule_id + "::" + paramOut.getString("id"));
        return new ResultDto(ResultDto.SUCCESS, "成功");
    }

    @Override
    public ResultDto deleteMachine(MachineDto machineDto) {

        String[] extMachineIds = machineDto.getExtMachineId().split("::");


        HttpEntity httpEntity = new HttpEntity("", ShenxingFactory.getHeader(machineDto.getMachineCode(), restTemplate));
        ResponseEntity<String> responseEntity = restTemplate.exchange(MappingCacheFactory.getValue("Shenxing_URL") + DELETE_GROUP + extMachineIds[1], HttpMethod.DELETE, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), DELETE_GROUP, machineDto.getExtMachineId(), responseEntity.getBody());
        if (responseEntity.getStatusCode().value()  >= 400) {
            throw new IllegalStateException("请求添加权限组失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (!paramOut.containsKey("id")) {
            throw new IllegalStateException("删除权限组失败" + responseEntity);
        }

        httpEntity = new HttpEntity("", ShenxingFactory.getHeader(machineDto.getMachineCode(), restTemplate));
        responseEntity = restTemplate.exchange(MappingCacheFactory.getValue("Shenxing_URL") + DELETE_MACHINE + extMachineIds[0], HttpMethod.DELETE, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), DELETE_MACHINE, machineDto.getExtMachineId(), responseEntity.getBody());
        if (responseEntity.getStatusCode().value()  >= 400) {
            throw new IllegalStateException("请求添加权限组失败" + responseEntity);
        }

        paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (!paramOut.containsKey("id")) {
            throw new IllegalStateException("删除权限组失败" + responseEntity);
        }
        return new ResultDto(ResultDto.SUCCESS, "成功");
    }


    @Override
    public ResultDto initAssessControlProcess(MachineDto machineDto) {
        return new ResultDto(ResultDto.SUCCESS, "成功");
    }

    @Override
    public String getFace(MachineDto machineDto, UserFaceDto userFaceDto) {



        return null;
    }

    @Override
    public ResultDto addFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        // /api/groups/item
        JSONObject paramIn = new JSONObject();
        paramIn.put("recognition_type", "staff");
        paramIn.put("is_admin", false);
        paramIn.put("person_name", userFaceDto.getName());
        paramIn.put("id", userFaceDto.getUserId());
        paramIn.put("password", "");
        paramIn.put("card_number", userFaceDto.getIdNumber());
        paramIn.put("person_code", userFaceDto.getUserId() + "1");

        JSONArray groupList = new JSONArray();
        groupList.add(machineDto.getThirdMachineId().split("::")[1]);
        paramIn.put("group_list", groupList);

        JSONArray faceList = new JSONArray();

        JSONObject face = new JSONObject();
        face.put("idx", 1);
        face.put("data", userFaceDto.getFaceBase64());
        faceList.add(face);
        paramIn.put("face_list", faceList);
        //paramIn.put("request_id","");
        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), ShenxingFactory.getHeader(machineDto.getMachineCode(), restTemplate));
        ResponseEntity<String> responseEntity = restTemplate.exchange(MappingCacheFactory.getValue("Shenxing_URL") + CMD_ADD_USER, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        if (responseEntity.getStatusCode().value()  >= 400) {
            throw new IllegalStateException("请求添加权限组失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (!paramOut.containsKey("id")) {
            throw new IllegalStateException("添加人员失败" + responseEntity);
        }

        return new ResultDto(ResultDto.SUCCESS, "成功");
    }

    @Override
    public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {

        JSONObject paramIn = new JSONObject();
        paramIn.put("recognition_type", "staff");
        paramIn.put("is_admin", false);
        paramIn.put("person_name", userFaceDto.getName());
        paramIn.put("card_number", userFaceDto.getIdNumber());
        paramIn.put("person_code", userFaceDto.getUserId() + "1");

        JSONArray groupList = new JSONArray();
        groupList.add(machineDto.getThirdMachineId().split("::")[1]);
        paramIn.put("group_list", groupList);

        JSONArray faceList = new JSONArray();

        JSONObject face = new JSONObject();
        face.put("idx", 1);
        face.put("data", userFaceDto.getFaceBase64());
        faceList.add(face);
        paramIn.put("face_list", faceList);
        //paramIn.put("request_id","");
        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), ShenxingFactory.getHeader(machineDto.getMachineCode(), restTemplate));
        logger.debug("请求信息 ： " + httpEntity);
        ResponseEntity<String> responseEntity = restTemplate.exchange(MappingCacheFactory.getValue("Shenxing_URL") + CMD_UPDATE_USER + userFaceDto.getUserId(), HttpMethod.PUT, httpEntity, String.class);
        logger.debug( "返回信息 ：" + responseEntity);
        if (responseEntity.getStatusCode().value()  >= 400) {
            throw new IllegalStateException("请求添加权限组失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (!paramOut.containsKey("id")) {
            throw new IllegalStateException("修改人员失败" + responseEntity);
        }

        return new ResultDto(ResultDto.SUCCESS, "成功");
    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        HttpEntity httpEntity = new HttpEntity("", ShenxingFactory.getHeader(machineDto.getMachineCode(), restTemplate));
        ResponseEntity<String> responseEntity = restTemplate.exchange(MappingCacheFactory.getValue("Shenxing_URL") + CMD_DELETE_FACE + heartbeatTaskDto.getTaskinfo(), HttpMethod.DELETE, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        if (responseEntity.getStatusCode().value()  >= 400) {
            throw new IllegalStateException("请求删除人脸失败" + responseEntity);
        }

        return new ResultDto(ResultDto.SUCCESS, "成功");
    }

    @Override
    public ResultDto clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String password = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "ASSESS_PASSWORD");
        String url = "http://" + machineDto.getMachineIp() + CMD_RESET;
        JSONObject param = new JSONObject();
        param.put("delete", false);
        param.put("pass", password);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_RESET, param.toJSONString(), responseEntity.getBody());
        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        return new ResultDto(paramOut.getBoolean("success") ? ResultDto.SUCCESS : ResultDto.ERROR, paramOut.getString("code") + paramOut.getString("msg"));

    }


    /**
     * 扫描设备
     *
     * @return
     */
    @Override
    public List<MachineDto> scanMachine() throws Exception {


        return null;
    }


    @Override
    public void restartMachine(MachineDto machineDto) {
        JSONObject param = new JSONObject();
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), ShenxingFactory.getHeader(machineDto.getMachineCode(), restTemplate));
        ResponseEntity<String> responseEntity = restTemplate.exchange(MappingCacheFactory.getValue("Shenxing_URL") + CMD_REBOOT , HttpMethod.GET, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        if (responseEntity.getStatusCode().value()  >= 400) {
            throw new IllegalStateException("请求重启设备失败" + responseEntity);
        }

    }

    @Override
    public void openDoor(MachineDto machineDto) {
        JSONObject param = new JSONObject();
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), ShenxingFactory.getHeader(machineDto.getMachineCode(), restTemplate));
        logger.debug("请求信息 ： " + httpEntity );

        ResponseEntity<String> responseEntity = restTemplate.exchange(MappingCacheFactory.getValue("Shenxing_URL") + CMD_OPEN_DOOR , HttpMethod.POST, httpEntity, String.class);
        logger.debug("，返回信息:" + responseEntity);
        if (responseEntity.getStatusCode().value()  >= 400) {
            throw new IllegalStateException("请求开门失败" + responseEntity);
        }
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_OPEN_DOOR, "", responseEntity.getBody());
    }

    @Override
    public String httpFaceResult(MachineDto machineDto, String data) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        JSONObject resultParam = new JSONObject();
        try {
            JSONObject body = JSONObject.parseObject(data);

            String userId = body.containsKey("person_id") ? body.getString("person_id") : "";
            String userName = "";
            if (!StringUtils.isEmpty(userId)) {
                MachineFaceDto machineFaceDto = new MachineFaceDto();
                machineFaceDto.setUserId(userId);
                machineFaceDto.setMachineId(machineDto.getMachineId());
                List<MachineFaceDto> machineFaceDtos = notifyAccessControlService.queryMachineFaces(machineFaceDto);
                if (machineFaceDtos != null && machineFaceDtos.size() > 0) {
                    userName = machineFaceDtos.get(0).getName();
                }
            }

            if(StringUtil.isEmpty(userName)){
                userName = body.getString("person_name");
            }


            OpenDoorDto openDoorDto = new OpenDoorDto();
            openDoorDto.setFace(body.getString("photo"));
            openDoorDto.setUserName(userName);
            openDoorDto.setHat("3");
            openDoorDto.setMachineCode(machineDto.getMachineCode());
            openDoorDto.setUserId(userId);
            openDoorDto.setOpenId(SeqUtil.getId());
            openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);
            openDoorDto.setSimilarity(body.containsKey("identifyType") && "1".equals(body.getString("identifyType")) ? "100" : "0");


            freshOwnerFee(openDoorDto);

            notifyAccessControlService.saveFaceResult(openDoorDto);

        } catch (Exception e) {
            logger.error("推送人脸失败", e);
        }
        resultParam.put("result", 1);
        resultParam.put("success", true);
        return resultParam.toJSONString();//未找到设备


    }

    @Override
    public String heartbeat(String data, String machineCode) throws Exception {
        JSONObject info = JSONObject.parseObject(data);

        //设备ID
        //String machineCode = info.getString("deviceKey");
        String heartBeatTime = null;
        //heartBeatTime = info.getString("time");
        heartBeatTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A);
        MachineHeartbeatDto machineHeartbeatDto = new MachineHeartbeatDto(machineCode, heartBeatTime);
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        notifyAccessControlService.machineHeartbeat(machineHeartbeatDto);
        JSONObject resultParam = new JSONObject();
        resultParam.put("result", 1);
        resultParam.put("success", true);
        return resultParam.toJSONString();//未找到设备
    }


}

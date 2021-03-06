package com.java110.things.adapt.accessControl.smt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.DefaultAbstractAccessControlAdapt;
import com.java110.things.adapt.accessControl.IAssessControlProcess;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.cloud.MachineHeartbeatDto;
import com.java110.things.entity.fee.FeeDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.machine.OperateLogDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.room.RoomDto;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.factory.MqttFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.quartz.accessControl.AddUpdateFace;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import com.tendcent.face.sdk.client.TdxSdkClient;
import com.tendcent.face.sdk.client.dto.FaceDto;
import com.tendcent.face.sdk.client.dto.HostInfoDto;
import com.tendcent.face.sdk.client.dto.PersonDto;
import com.tendcent.face.sdk.client.resp.TdxSdkResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sun.applet.Main;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ?????????sdk
 */
@Service("smtHttpAssessControlProcessAdapt")
public class SmtHttpAssessControlProcessAdapt extends DefaultAbstractAccessControlAdapt {

    private static Logger logger = LoggerFactory.getLogger(SmtHttpAssessControlProcessAdapt.class);

    private static final String DEFAULT_PORT = "8090"; //??????

    @Autowired
    private IMachineService machineServiceImpl;

    public static final long START_TIME = new Date().getTime() - 1000 * 60 * 60;
    public static final long END_TIME = new Date().getTime() + 1000 * 60 * 60 * 24 * 365;

    public static final String OPEN_TYPE_FACE = "1000"; // ????????????

    public static final String CMD_ADD_FACE = "/face/create"; // ????????????


    public static final String CMD_OPEN_DOOR = "/device/openDoorControl"; // ??????


    public static final String CMD_ADD_USER = "/person/create"; // ????????????


    public static final String CMD_DELETE_FACE = "/person/delete"; //????????????

    public static final String SN = "{sn}";

    @Override
    public void initAssessControlProcess() {
        logger.debug("?????????????????????");
    }

    @Override
    public ResultDto initAssessControlProcess(MachineDto machineDto) {
        //??????????????????
        String secret =MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SECRET");
        String deviceKey=machineDto.getMachineMac();//?????????
        String host=MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_HOST");
        String url =MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "IOT_URL") +
                "/api/accessControl/faceResult/" + machineDto.getMachineMac();
//                MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SET_IDENTIFY_CALLBACK"); //????????????????????????
        int port=Integer.parseInt(MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_PORT"));
        int timeout=5000;
        HostInfoDto hostInfo=new HostInfoDto();
        hostInfo.setHost(host);
        hostInfo.setPort(port);
        hostInfo.setTimeout(timeout);

        TdxSdkResponse tdxSdkResponse=TdxSdkClient.setIdentifyCallback(hostInfo,deviceKey, secret,url);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, deviceKey, tdxSdkResponse==null?"":tdxSdkResponse.toString());

        //??????????????????
        url = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "IOT_URL")
                + "/api/accessControl/heartBeat/" + machineDto.getMachineMac();
                //MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SET_HEARTBEAT_CALLBACK");
        tdxSdkResponse=TdxSdkClient.setHeartbeatUrl(hostInfo,deviceKey, secret,url);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, deviceKey, tdxSdkResponse==null?"":tdxSdkResponse.toString());

        if (tdxSdkResponse==null||!"000".equals(tdxSdkResponse.getCode())) {
            return new ResultDto(ResultDto.ERROR, "?????????????????????");
        }else{
            return new ResultDto(ResultDto.SUCCESS , tdxSdkResponse.getMsg());
        }
    }

    @Override
    public int getFaceNum(MachineDto machineDto) {
        return 0;
    }

    @Override
    public String getFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        String secret =MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SECRET");
        String deviceKey=machineDto.getMachineMac();//?????????
        String host=MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_HOST");
        int port=Integer.parseInt(MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_PORT"));
        int timeout=5000;
        HostInfoDto hostInfo=new HostInfoDto();
        hostInfo.setHost(host);
        hostInfo.setPort(port);
        hostInfo.setTimeout(timeout);
        String personId=userFaceDto.getUserId();

        TdxSdkResponse tdxSdkResponse=TdxSdkClient.findPerson(hostInfo,deviceKey, secret,personId);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, deviceKey, tdxSdkResponse==null?"":tdxSdkResponse.toString());

        if (tdxSdkResponse==null||!"000".equals(tdxSdkResponse.getCode())) {
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }
        return personId;
    }

   /*
     * @param machineDto  ????????????
     * @param userFaceDto ??????????????????
     * @return
     */
    @Override
    public ResultDto addFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        String secret =MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SECRET");
        String deviceKey=machineDto.getMachineMac();//?????????
        String host=MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_HOST");
        int port=Integer.parseInt(MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_PORT"));
        int timeout=5000;
        HostInfoDto hostInfo=new HostInfoDto();
        hostInfo.setHost(host);
        hostInfo.setPort(port);
        hostInfo.setTimeout(timeout);
        PersonDto personDto=new PersonDto();
//        personDto.setExpireTime(0l);
        personDto.setId(userFaceDto.getUserId());
        personDto.setIdcardNum(userFaceDto.getIdNumber());
        personDto.setName(userFaceDto.getName());

        TdxSdkResponse tdxSdkResponse=TdxSdkClient.addPerson(hostInfo,deviceKey, secret,personDto);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, deviceKey, tdxSdkResponse==null?"":tdxSdkResponse.toString());

        FaceDto faceDto=new FaceDto();
        faceDto.setFaceId(userFaceDto.getUserId());
        faceDto.setPersonId(userFaceDto.getUserId());
        faceDto.setImgBase64(userFaceDto.getFaceBase64());
        tdxSdkResponse=TdxSdkClient.addFace(hostInfo,deviceKey, secret,faceDto);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_FACE, deviceKey,tdxSdkResponse==null?"":tdxSdkResponse.toString());

        return new ResultDto(tdxSdkResponse.getCode()=="200" ? ResultDto.SUCCESS : ResultDto.ERROR, tdxSdkResponse==null?"":tdxSdkResponse.getMsg());
    }

    @Override
    public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {

       return null;
//        return new ResultDto(paramOut.getBoolean("success") ? ResultDto.SUCCESS : ResultDto.ERROR, paramOut.getString("msg"));
    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String secret =MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SECRET");
        String deviceKey=machineDto.getMachineMac();//?????????
        String host=MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_HOST");
        int port=Integer.parseInt(MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_PORT"));
        int timeout=5000;
        HostInfoDto hostInfo=new HostInfoDto();
        hostInfo.setHost(host);
        hostInfo.setPort(port);
        hostInfo.setTimeout(timeout);
        String personId=heartbeatTaskDto.getTaskid();

        TdxSdkResponse tdxSdkResponse=TdxSdkClient.delFace(hostInfo,deviceKey, secret,personId);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, deviceKey, tdxSdkResponse==null?"":tdxSdkResponse.toString());
        if (tdxSdkResponse==null||!"000".equals(tdxSdkResponse.getCode())) {
            return new ResultDto( ResultDto.ERROR , tdxSdkResponse==null?"":tdxSdkResponse.getMsg());
        }else{
            return new ResultDto( ResultDto.SUCCESS , tdxSdkResponse==null?"":tdxSdkResponse.getMsg());
        }

    }

    @Override
    public ResultDto clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {


        return null;
    }


    /**
     * ????????????
     *
     * @return
     */
    @Override
    public List<MachineDto> scanMachine() throws Exception {


        return null;
    }

    @Override
    public void mqttMessageArrived(String topic, String data) {


    }


    @Override
    public void restartMachine(MachineDto machineDto) {
        String secret =MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SECRET");
        String deviceKey=machineDto.getMachineMac();//?????????
        String host=MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_HOST");
        int port=Integer.parseInt(MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_PORT"));
        int timeout=5000;
        HostInfoDto hostInfo=new HostInfoDto();
        hostInfo.setHost(host);
        hostInfo.setPort(port);
        hostInfo.setTimeout(timeout);
        TdxSdkResponse tdxSdkResponse=TdxSdkClient.rebootDevice(hostInfo,deviceKey,secret);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_OPEN_DOOR, deviceKey, tdxSdkResponse==null?"":tdxSdkResponse.toString());
    }

    @Override
    public void openDoor(MachineDto machineDto) {
        String secret =MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_SECRET");
        String deviceKey=machineDto.getMachineMac();//?????????
        String host=MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_HOST");
        int port=Integer.parseInt(MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "SMT_PORT"));
        int timeout=5000;
        HostInfoDto hostInfo=new HostInfoDto();
        hostInfo.setHost(host);
        hostInfo.setPort(port);
        hostInfo.setTimeout(timeout);
        JSONObject param = new JSONObject();
        param.put("secret", secret);
        param.put("deviceKey", deviceKey);
        param.put("hostInfo", hostInfo);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        TdxSdkResponse tdxSdkResponse=TdxSdkClient.openDoor(hostInfo,deviceKey,secret);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_OPEN_DOOR, deviceKey, tdxSdkResponse==null?"":tdxSdkResponse.toString());
    }



//    public static void main(String[] args) throws UnsupportedEncodingException {
//        String data="standard=37.3&searchScore=87.88905&ip=192.168.204.167&" +
//                "deviceKey=8DA5A59CF625F562&type=face_0&token=2D2ACE27A6BAF35DAB48DFA2151A89BF" +
//                "&personName=%E5%BC%A0%E9%BE%99" +
//                "&path=http%3A%2F%2F192.168.204.167%3A8090%2Fsnapshot%2F20210308%2F1615192075813.jpg" +
//                "&temperature=-1.0&personId=772021022532740148&time=1615192075944&temperatureState=1" +
//                "&livenessScore=93.72917&mask=-1";
//        data=java.net.URLDecoder.decode(data, "utf-8");
//        String[] bb=data.split("&");
//        JSONObject body=new JSONObject();
//        for(int i=0;i<bb.length;i++){
//            String[] cc=bb[i].split("=");
//            if(cc.length==2){
//                body.put(cc[0],cc[1]);
//            }else{
//                body.put(cc[0],"");
//            }
//        }
//        System.out.println(body.toJSONString());
//        System.out.println(java.net.URLDecoder.decode(body.getString("personName"),"utf-8"));
//    }
    /*
     * @param machineDto
     * @param data ???????????????????????????????????????????????????????????????
     * @return
     */

    @Override
    public String httpFaceResult(MachineDto machineDto,String data) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        JSONObject resultParam = new JSONObject();
        try {
//            JSONObject body = JSONObject.parseObject(data);
            data=java.net.URLDecoder.decode(data, "utf-8");
            String[] bb=data.split("&");
            JSONObject body=new JSONObject();
            for(int i=0;i<bb.length;i++){
                String[] cc=bb[i].split("=");
                if(cc.length==2){
                    body.put(cc[0],cc[1]);
                }else{
                    body.put(cc[0],"");
                }
            }
            String type=body.getString("type");
            String userId = body.containsKey("personId") ? body.getString("personId") : "";
            String userName =java.net.URLDecoder.decode(body.getString("personName"),"utf-8");
//            if("face_0".equals(type)){
                OpenDoorDto openDoorDto = new OpenDoorDto();
                openDoorDto.setFace(body.getString("imgBase64"));
                openDoorDto.setUserName(userName);
                openDoorDto.setHat("3");
                openDoorDto.setMachineCode(body.getString("deviceKey"));//machineDto.getMachineCode());
                openDoorDto.setMachineCode(body.getString("deviceKey"));//machineDto.getMachineCode());
//                openDoorDto.
                openDoorDto.setUserId(userId);
                openDoorDto.setOpenId(SeqUtil.getId());
                openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);
                openDoorDto.setSimilarity(body.getString("searchScore"));
                freshOwnerFee(openDoorDto);
                notifyAccessControlService.saveFaceResult(openDoorDto);
//            }
//            if (!StringUtils.isEmpty(userId)) {
//                MachineFaceDto machineFaceDto = new MachineFaceDto();
//                machineFaceDto.setUserId(userId);
//                machineFaceDto.setMachineId(machineDto.getMachineId());
//                List<MachineFaceDto> machineFaceDtos = notifyAccessControlService.queryMachineFaces(machineFaceDto);
//                if (machineFaceDtos != null && machineFaceDtos.size() > 0) {
//                    userName = machineFaceDtos.get(0).getName();
//                }
//
//            }
        } catch (Exception e) {
            logger.error("??????????????????", e);
            resultParam.put("code", 404);
            resultParam.put("desc", "??????");
            return resultParam.toJSONString();//???????????????
        }
        resultParam.put("result", 1);
        resultParam.put("code","000");
        resultParam.put("success", "ture");
        return resultParam.toJSONString();
    }

    @Override
    public String heartbeat(String data, String machineCode) throws Exception {
//        JSONObject info = JSONObject.parseObject(data);
        //ip=192.168.204.167&deviceKey=8DA5A59CF625F562&time=1615174935065&version=1.41.4.8&faceCount=0&personCount=0
        String[] bb=data.split("&");
        JSONObject info=new JSONObject();
        for(int i=0;i<bb.length;i++){
            String[] cc=bb[i].split("=");
            if(cc.length==2){
                info.put(cc[0],cc[1]);
            }else{
                info.put(cc[0],"");
            }
        }
        //??????ID  8DA5A59CF625F562   192.168.204.167   ????????????????????????1
        //String machineCode = info.getString("deviceKey");
        String heartBeatTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        heartBeatTime =sdf.format(new Date(Long.parseLong(info.getString("time")))) ;
        MachineHeartbeatDto machineHeartbeatDto = new MachineHeartbeatDto(machineCode, heartBeatTime);
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        notifyAccessControlService.machineHeartbeat(machineHeartbeatDto);
        JSONObject resultParam = new JSONObject();
        resultParam.put("result", 1);
        resultParam.put("code","000");
        resultParam.put("success", "ture");
        return resultParam.toJSONString();
    }



}

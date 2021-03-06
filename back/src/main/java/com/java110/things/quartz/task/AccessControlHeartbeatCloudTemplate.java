package com.java110.things.quartz.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.quartz.accessControl.AddUpdateFace;
import com.java110.things.quartz.accessControl.ClearAllFace;
import com.java110.things.quartz.accessControl.DeleteFace;
import com.java110.things.config.Java110Properties;
import com.java110.things.constant.AccessControlConstant;
import com.java110.things.constant.ExceptionConstant;
import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.SystemExceptionDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.task.TaskDto;
import com.java110.things.exception.HeartbeatCloudException;
import com.java110.things.exception.Result;
import com.java110.things.exception.ThreadException;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.factory.HttpFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.quartz.TaskSystemQuartz;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.service.machine.ISystemExceptionService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.DateUtil;
import com.java110.things.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName AccessControlHeartbeatCloudTemplate
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/8 16:53
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
@Component
public class AccessControlHeartbeatCloudTemplate extends TaskSystemQuartz {

    /**
     * ????????? ????????????
     */
    public static boolean INIT_MACHINE_STATE = false;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Java110Properties java110Properties;


    @Autowired
    private AddUpdateFace addUpdateFace;

    @Autowired
    private DeleteFace deleteFace;
    @Autowired
    private ICommunityService communityService;

    @Autowired
    private IMachineService machineService;

    @Autowired
    private ClearAllFace clearAllFace;

    @Autowired
    private ISystemExceptionService systemExceptionService;


    @Override
    protected void process(TaskDto taskDto) throws Exception {

        if (INIT_MACHINE_STATE) {
            AccessControlProcessFactory.getAssessControlProcessImpl("").initAssessControlProcess();
            INIT_MACHINE_STATE = false;
        }
        //??????????????????
        List<MachineDto> machineDtos = queryMachines();

        //?????? ????????????
        CommunityDto communityDto = new CommunityDto();
        ResultDto resultDto = communityService.getCommunity(communityDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "????????????????????????");
        }

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        if (communityDtos == null || communityDtos.size() < 1) {
            throw new ThreadException(Result.SYS_ERROR, "????????????????????????????????????????????????");
        }


        //??????????????????????????????
        heartbeatCloud(machineDtos, communityDtos.get(0));
    }

    /**
     * ???????????? ????????????
     *
     * @param machineDtos ????????????
     */
    private void heartbeatCloud(List<MachineDto> machineDtos, CommunityDto communityDto) {

        if (machineDtos == null || machineDtos.size() == 0) {
            return;
        }

        for (MachineDto machineDto : machineDtos) {
            try {
                doHeartbeatCloud(machineDto, communityDto);
            } catch (Exception e) {
                logger.error(machineDto.getMachineCode() + "????????????", e);

            }
        }

    }

    /**
     * ???????????? ????????????
     *
     * @param machineDto
     */
    private void doHeartbeatCloud(MachineDto machineDto, CommunityDto communityDto) throws Exception {

        String url = MappingCacheFactory.getValue("CLOUD_API") + AccessControlConstant.MACHINE_HEARTBEART;

        Map<String, String> headers = new HashMap<>();
        headers.put("command", "gettask");
        headers.put("machinecode", machineDto.getMachineCode());
        headers.put("communityId", communityDto.getCommunityId());

        JSONObject paramIn = new JSONObject();
        paramIn.put("machineCode", machineDto.getMachineCode());
        paramIn.put("devGroup", "default");
        paramIn.put("communityId", communityDto.getCommunityId());
        paramIn.put("name", machineDto.getMachineName());
        paramIn.put("authCode", machineDto.getAuthCode());
        paramIn.put("ip", machineDto.getMachineIp());
        paramIn.put("mac", machineDto.getMachineMac());
        paramIn.put("remarks", "");
        paramIn.put("faceNum", AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).getFaceNum(machineDto));
        paramIn.put("lastOnTime", DateUtil.getTime());
        paramIn.put("statCode", "");
        paramIn.put("deviceType", machineDto.getMachineTypeCd());
        paramIn.put("versionCode", machineDto.getMachineVersion());

        ResponseEntity<String> responseEntity = HttpFactory.exchange(restTemplate, url, paramIn.toJSONString(), headers, HttpMethod.POST);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new HeartbeatCloudException(ExceptionConstant.ERROR, responseEntity.getBody());
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (!paramOut.containsKey("code") || ResponseConstant.SUCCESS != paramOut.getInteger("code")) {
            String msg = paramOut.containsKey("message") ? paramOut.getString("message") : ResponseConstant.NO_RESULT;
            throw new HeartbeatCloudException(ExceptionConstant.ERROR, msg);
        }

        JSONArray data = paramOut.getJSONArray("data");

        for (int dataIndex = 0; dataIndex < data.size(); dataIndex++) {
            doHeartbeatCloudResult(machineDto, data.getJSONObject(dataIndex), communityDto);
        }

    }

    /**
     * ??????????????????
     *
     * @param commandInfo ???????????? {
     *                    "taskinfo": "772019121580420009", ????????????ID
     *                    "taskcmd": 101, ????????????
     *                    "taskId": "ed06d2329c774474a05475ac6f3d623d"  ??????ID
     *                    }
     */
    private void doHeartbeatCloudResult(MachineDto machineDto, JSONObject commandInfo, CommunityDto communityDto) throws Exception {

        Assert.hasKeyAndValue(commandInfo, "taskcmd", "?????????????????????????????? ??? ?????????????????? taskcmd" + commandInfo.toJSONString());
        Assert.hasKeyAndValue(commandInfo, "taskinfo", "?????????????????????????????? ??? ?????????????????? taskinfo" + commandInfo.toJSONString());
        Assert.hasKeyAndValue(commandInfo, "taskid", "?????????????????????????????? ??? ????????????ID taskid" + commandInfo.toJSONString());

        HeartbeatTaskDto heartbeatTaskDto = BeanConvertUtil.covertBean(commandInfo, HeartbeatTaskDto.class);
        try {
            switch (commandInfo.getInteger("taskcmd")) {
                case AccessControlConstant.CMD_ADD_UPDATE_FACE:
                    addUpdateFace.addUpdateFace(machineDto, heartbeatTaskDto, communityDto);
                    break;
                case AccessControlConstant.CMD_DELETE_FACE:
                    deleteFace.deleteFace(machineDto, heartbeatTaskDto, communityDto);
                    break;
                case AccessControlConstant.CMD_CLEAR_ALL_FACE:
                    clearAllFace.clearFace(machineDto, heartbeatTaskDto, communityDto);
                    break;
                default:
                    logger.error("??????????????????", commandInfo.getInteger("taskcmd"));
            }
        } catch (Exception e) {
            //????????????????????????????????????
            systemExceptionService.save(SystemExceptionDto.EXCEPTION_TYPE_ACCESS_CONTROL, commandInfo.getString("taskId"),
                    machineDto.getMachineId(), ExceptionUtil.getStackTrace(e));
            throw e;
        }


    }

    /**
     * ??????????????????
     *
     * @return
     */
    private List<MachineDto> queryMachines() throws Exception {
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ACCESS_CONTROL);
        ResultDto resultDto = machineService.getMachine(machineDto);
        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "??????????????????");
        }
        List<MachineDto> machineDtos = (List<MachineDto>) resultDto.getData();
        return machineDtos;

    }
}

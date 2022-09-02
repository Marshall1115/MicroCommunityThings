package com.java110.attendance.zhongkong;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.adapt.ICallAccessControlService;
import com.java110.core.adapt.attendance.IAttendanceMachineProcess;
import com.java110.core.adapt.attendance.ICallAttendanceService;
import com.java110.core.constant.ResponseConstant;
import com.java110.entity.attendance.ClockInDto;
import com.java110.entity.attendance.ClockInResultDto;
import com.java110.entity.cloud.MachineCmdResultDto;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.machine.OperateLogDto;
import com.java110.entity.response.ResultDto;
import com.java110.entity.user.StaffDto;
import com.java110.core.factory.CallAttendanceFactory;
import com.java110.core.factory.MappingCacheFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.factory.NotifyAccessControlFactory;
import com.java110.core.service.community.ICommunityService;
import com.java110.core.service.machine.IMachineService;
import com.java110.core.util.DateUtil;
import com.java110.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("zkAttendanceMachineProcessAdapt")
public class ZkAttendanceMachineProcessAdapt implements IAttendanceMachineProcess {

    private static Logger logger = LoggerFactory.getLogger(ZkAttendanceMachineProcessAdapt.class);

    public static final String FACE_URL = "ACCESS_CONTROL_FACE_URL";

    @Autowired
    private IMachineService machineServiceImpl;

    @Autowired
    private ICommunityService communityServiceImpl;

    //图片后缀
    public static final String IMAGE_SUFFIX = ".jpg";

    @Override
    public void initMachine(MachineDto machineDto) {

    }

    @Override
    public void restartAttendanceMachine(MachineDto machineDto, StaffDto staffDto) {

        JSONObject param = JSONObject.parseObject("{\n" +
                "\"confirmation_topic\" : \"/hio/device_cmd_reply\", \"data\" : {\n" +
                "\"cmd_type\" : \"reboot_device\"\n" +
                "},\n" +
                "\"message_id\" : 0,\n" +
                "\"message_uuid\" : \"227206768f454443\"," +
                " \"request_type\" : \"device_cmd\"\n" +
                "}");

        param.put("message_uuid", staffDto.getTaskId());
        MqttFactory.publish("/hiot/" + machineDto.getMachineCode() + "/request_setting", param.toJSONString());
        saveLog(staffDto.getTaskId(), machineDto.getMachineId(), "/hiot/" + machineDto.getMachineCode() + "/request_setting", param.toJSONString(), "", "", staffDto.getStaffId(), staffDto.getStaffName());

    }

    @Override
    public ResultDto addFace(MachineDto machineDto, StaffDto staffDto) {

        JSONObject param = JSONObject.parseObject("{\"confirmation_topic\": \"/hiot/people_send_reply\", \"data\": []}");
        JSONArray datas = param.getJSONArray("data");
        JSONObject data = new JSONObject();
        data.put("age", 0);
        data.put("card", "");
        data.put("gender", 1);
        //data.put("image_base64", staffDto.getFaceBase64());
        data.put("image_url", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + staffDto.getExtStaffId() + IMAGE_SUFFIX);
        data.put("name", staffDto.getStaffName());
        data.put("person_uuid", staffDto.getStaffId());
        data.put("phone", "");
        data.put("product_key", "");
        datas.add(data);
        param.put("message_uuid", staffDto.getTaskId());
        param.put("message_id", 0);
        MqttFactory.publish("/hiot/" + machineDto.getMachineCode() + "/add_data", param.toJSONString());
        saveLog(staffDto.getTaskId(), machineDto.getMachineId(), "/hiot/" + machineDto.getMachineCode() + "/add_data", param.toJSONString(), "", "", staffDto.getStaffId(), staffDto.getStaffName());
        return new ResultDto(ResultDto.SUCCESS, "成功");
    }

    @Override
    public ResultDto updateFace(MachineDto machineDto, StaffDto staffDto) {
        deleteFace(machineDto, staffDto);
        addFace(machineDto, staffDto);
        return new ResultDto(ResultDto.SUCCESS, "成功");
    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, StaffDto staffDto) {
        JSONObject param = JSONObject.parseObject("{\"confirmation_topic\": \"/hiot/people_send_reply\", \"data\": []}");
        JSONArray datas = param.getJSONArray("data");
        JSONObject data = new JSONObject();
        data.put("clear_all", false);
        data.put("person_uuids", new String[]{staffDto.getStaffId()});
        datas.add(data);
        param.put("message_uuid", staffDto.getTaskId()+"01");
        param.put("message_id", 0);
        MqttFactory.publish("/hiot/" + machineDto.getMachineCode() + "/del_data", param.toJSONString());
        saveLog(staffDto.getTaskId()+"01", machineDto.getMachineId(), "/hiot/" + machineDto.getMachineCode() + "/del_data", param.toJSONString(), "", "", staffDto.getStaffId(), staffDto.getStaffName());
        return new ResultDto(ResultDto.SUCCESS, "成功");
    }

    @Override
    public void mqttMessageArrived(String topic, String data) {

        JSONObject param = JSONObject.parseObject(data);

        switch (topic) {
            case "/hiot/people_send_reply":
                doCmdResultCloud(param);
                break;
            case "/hiot/record_message": //硬件上线
                clockIn(param);
                break;
            default:
                doCmdResultCloud(param);
                break;
        }

        if (!param.containsKey("cmd_id")) {
            return;
        }
        String state = param.containsKey("code") && "0".equals(param.getString("code")) ? "10002" : "10003";
        String marchineId = "-1";
        if (param.containsKey("sn")) {
            ResultDto resultDto = null;
            MachineDto machineDto = new MachineDto();
            machineDto.setMachineCode(param.getString("sn"));
            machineDto.setMachineTypeCd("9999"); // 标识门禁 以防万一和道闸之类冲突
            try {
                resultDto = machineServiceImpl.getMachine(machineDto);
                if (resultDto != null && resultDto.getCode() == ResponseConstant.SUCCESS) {
                    List<MachineDto> machineDtos = (List<MachineDto>) resultDto.getData();
                    if (machineDtos.size() > 0) {
                        marchineId = machineDtos.get(0).getMachineId();
                    }
                }
            } catch (Exception e) {

            }
        }
        saveLog(param.getString("message_uuid"), marchineId, topic, "", data, state, "", "");
    }

    @Override
    public ResultDto clearFace(MachineDto machineDto, StaffDto staffDto) {
        return null;
    }

    @Override
    public String getDefaultResult() {
        return null;
    }

    /**
     * 存储日志
     *
     * @param logId     日志ID
     * @param machineId 设备ID
     * @param cmd       操作命令
     * @param reqParam  请求报文
     * @param resParam  返回报文
     * @param state     状态
     * @param userId    业主ID
     * @param userName  业主名称
     */
    protected void saveLog(String logId, String machineId, String cmd, String reqParam, String resParam, String state, String userId, String userName) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        OperateLogDto operateLogDto = new OperateLogDto();
        operateLogDto.setLogId(logId);
        operateLogDto.setMachineId(machineId);
        operateLogDto.setOperateType(cmd);
        operateLogDto.setReqParam(reqParam);
        operateLogDto.setResParam(resParam);
        operateLogDto.setState(state);
        operateLogDto.setUserId(userId);
        operateLogDto.setUserName(userName);
        notifyAccessControlService.saveOrUpdateOperateLog(operateLogDto);
    }


    private void doCmdResultCloud(JSONObject resultCmd) {
        try {
            String taskId = resultCmd.getString("message_uuid");
            int code = -1;
            if (!resultCmd.containsKey("status")) {
                code = -1;
            } else {
                String status = resultCmd.getString("status");
                if ("ok".equals(status)) {
                    code = 0;
                } else {
                    code = -1;
                }
            }
            String msg = resultCmd.getString("message_info");
            ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
            MachineCmdResultDto machineCmdResultDto = new MachineCmdResultDto(code, msg, taskId, "", resultCmd.toJSONString());
            notifyAccessControlService.machineCmdResult(machineCmdResultDto);
        } catch (Exception e) {
            logger.error("上报执行命令失败", e);
        }
    }

    /**
     * 打卡记录
     *
     * @param dataObj
     */
    private void clockIn(JSONObject dataObj) {
        logger.debug("考勤结果,{}", dataObj.toJSONString());
        dataObj = dataObj.getJSONObject("data");
        if (!dataObj.containsKey("person_uuid") || StringUtil.isEmpty(dataObj.getString("person_uuid"))) {
            return;
        }
        try {

            String staffId = dataObj.getString("person_uuid");
            ICallAttendanceService callAttendanceService = CallAttendanceFactory.getCallAttendanceService();
            ClockInDto clockInDto = new ClockInDto();
            clockInDto.setStaffId(staffId);
            Date date = new Date();
            //date.setTime(dataObj.getLong("recog_time"));
            clockInDto.setClockInTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
            clockInDto.setClockInTime(DateUtil.getFormatTimeString(date, DateUtil.DATE_FORMATE_STRING_A));
            clockInDto.setPic(dataObj.getString("snap_image"));
            ClockInResultDto resultDto = callAttendanceService.clockIn(clockInDto);
            logger.debug("考勤结果,{}", JSONObject.toJSONString(resultDto));
        } catch (Exception e) {
            logger.error("考勤失败", e);
        }
    }

     public static void main(String[] args) {
        Date time = new Date();
         System.out.println(time.getTime());
         Date date = new Date();
         date.setTime(1659577546);
         System.out.println(date);
    }
}

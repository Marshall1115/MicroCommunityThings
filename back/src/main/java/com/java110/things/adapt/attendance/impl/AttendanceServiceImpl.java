package com.java110.things.adapt.attendance.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.attendance.IAttendanceProcess;
import com.java110.things.adapt.attendance.IAttendanceService;
import com.java110.things.adapt.attendance.ICallAttendanceService;
import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.dao.IAttendanceClassesServiceDao;
import com.java110.things.dao.IStaffServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.accessControl.SyncGetTaskResultDto;
import com.java110.things.entity.attendance.*;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.StaffDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ThreadException;
import com.java110.things.factory.AttendanceProcessFactory;
import com.java110.things.factory.CallAttendanceFactory;
import com.java110.things.factory.GetCloudFaceFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineCmdService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.Assert;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AttendanceServiceImpl
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/29 18:35
 * @Version 1.0
 * add by wuxw 2020/5/29
 **/

@Service("attendanceServiceImpl")
public class AttendanceServiceImpl implements IAttendanceService {
    private static Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);


    @Autowired
    private IMachineCmdService machineCmdServiceImpl;

    @Autowired
    private IMachineService machineServiceImpl;

    @Autowired
    private ICommunityService communityServiceImpl;

    @Autowired
    private IStaffServiceDao staffServiceDao;

    @Autowired
    private IAttendanceClassesServiceDao attendanceClassesServiceDao;

    /**
     * ?????? ??????????????????
     *
     * @return
     */
    private IAttendanceProcess getAttendanceProcess() throws Exception {
        return AttendanceProcessFactory.getAttendanceProcessImpl();
    }

    @Override
    public ResultDto heartbeat(MachineDto machineDto) {
        String result = null;
        try {
            //???????????????????????????
            getAttendanceProcess().initMachine(machineDto);

            MachineCmdDto machineCmdDto = new MachineCmdDto();
            machineCmdDto.setMachineCode(machineDto.getMachineCode());
            machineCmdDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ATTENDANCE);
            machineCmdDto.setState(MachineConstant.MACHINE_CMD_STATE_NO_DEAL);
            machineCmdDto.setPage(1);
            machineCmdDto.setRow(5);
            //????????????????????????
            List<MachineCmdDto> machineCmdDtos = getMachineCmd(machineCmdDto);

            if (machineCmdDtos != null && machineCmdDtos.size() > 0) {
                JSONObject paramOut = new JSONObject();
                for (MachineCmdDto tmpMachineCmdDto : machineCmdDtos) {
                    parseCmd(tmpMachineCmdDto, paramOut);
                }
                result = paramOut.toJSONString();
            }

//            if (StringUtil.isEmpty(result)) {
//                //????????????????????? ??????
//                result = getCmdFromCloud(machineDto);
//            }
        } catch (Exception e) {
            logger.error("????????????????????????", e);
        } finally {
            if (StringUtil.isEmpty(result)) {
                try {
                    result = getAttendanceProcess().getDefaultResult();
                } catch (Exception e) {
                    logger.error("????????????????????????", e);
                    result = "??????????????????????????????";
                }
            }
        }

        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, result);
    }

    private String getCmdFromCloud(MachineDto machineDto) throws Exception {

        machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ATTENDANCE);
        ResultDto resultDto = machineServiceImpl.getMachine(machineDto);
        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "??????????????????");
        }
        List<MachineDto> machineDtos = (List<MachineDto>) resultDto.getData();

        Assert.listOnlyOne(machineDtos, "???????????????");
        //?????? ????????????
        CommunityDto communityDto = new CommunityDto();
        resultDto = communityServiceImpl.getCommunity(communityDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "????????????????????????");
        }
        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();
        Assert.listOnlyOne(communityDtos, "????????????????????????????????????????????????");

        List<SyncGetTaskResultDto> syncGetTaskResultDtos = GetCloudFaceFactory.doHeartbeatCloud(machineDtos.get(0), communityDtos.get(0));

        if (syncGetTaskResultDtos == null || syncGetTaskResultDtos.size() < 1) {
            return null;
        }
        JSONObject paramOut = new JSONObject();
        for (SyncGetTaskResultDto syncGetTaskResultDto : syncGetTaskResultDtos) {
            parseCloudCmd(syncGetTaskResultDto, paramOut);
        }
        return paramOut.toJSONString();
    }

    /**
     * ???????????? ??????
     *
     * @param syncGetTaskResultDto
     * @param paramOut
     */
    private void parseCloudCmd(SyncGetTaskResultDto syncGetTaskResultDto, JSONObject paramOut) {

        JSONObject resultJson = null;
        try {
            switch (syncGetTaskResultDto.getCmd()) {
                case MachineConstant
                        .CMD_CREATE_FACE:
                    getAttendanceProcess().addFace(syncGetTaskResultDto, paramOut);
                    break;
                case MachineConstant
                        .CMD_DELETE_FACE:
                    // getAttendanceProcess().deleteFace(syncGetTaskResultDto, paramOut);
                    break;
                case MachineConstant
                        .CMD_CLEAR_FACE:
                    getAttendanceProcess().clearFace(syncGetTaskResultDto, paramOut);
                    break;
            }

            //??????????????????
            MachineCmdDto machineCmdDto = new MachineCmdDto();
            machineCmdDto.setCmdId(SeqUtil.getId());
            machineCmdDto.setState(MachineConstant.MACHINE_CMD_STATE_DEALING);
            machineCmdDto.setMachineCode(syncGetTaskResultDto.getMachineDto().getMachineCode());
            machineCmdDto.setMachineId(syncGetTaskResultDto.getMachineDto().getMachineId());
            machineCmdDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ATTENDANCE);
            machineCmdDto.setCommunityId(syncGetTaskResultDto.getCommunityDto().getCommunityId());
            machineCmdDto.setCmdCode(syncGetTaskResultDto.getCmd());
            machineCmdDto.setCmdName("");
            machineCmdServiceImpl.saveMachineCmd(machineCmdDto);

        } catch (Exception e) {
            logger.error("??????????????????", e);
        }
    }


    /**
     * ????????????
     *
     * @param machineCmdDto ?????????
     * @return
     */
    private void parseCmd(MachineCmdDto machineCmdDto, JSONObject paramOut) {
        JSONObject resultJson = null;
        try {
            switch (machineCmdDto.getCmdCode()) {
                case MachineConstant
                        .CMD_RESTART:
                    getAttendanceProcess().restartAttendanceMachine(machineCmdDto, paramOut);
                    break;
                case MachineConstant.CMD_CREATE_FACE:
                    getAttendanceProcess().addFace(machineCmdDto, paramOut);
                    break;
                case MachineConstant.CMD_DELETE_FACE:
                    getAttendanceProcess().deleteFace(machineCmdDto, paramOut);
                    break;
            }

            //??????????????????
            MachineCmdDto tmpMachineCmdDto = new MachineCmdDto();
            tmpMachineCmdDto.setCmdId(machineCmdDto.getCmdId());
            tmpMachineCmdDto.setState(MachineConstant.MACHINE_CMD_STATE_DEALING);
            machineCmdServiceImpl.updateMachineCmd(tmpMachineCmdDto);

        } catch (Exception e) {
            logger.error("??????????????????", e);
        }
    }


    @Override
    public ResultDto attendanceUploadData(AttendanceUploadDto attendanceUploadDto) {
        try {
            return getAttendanceProcess().attendanceUploadData(attendanceUploadDto);
        } catch (Exception e) {
            logger.error("??????????????????", e);
        }
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, "????????????????????????");

    }

    @Override
    public ResultDto getClasses(AttendanceClassesDto attendanceClassesDto) {
        int page = attendanceClassesDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            attendanceClassesDto.setPage((page - 1) * attendanceClassesDto.getRow());
        }
        long count = attendanceClassesServiceDao.getAttendanceClassesCount(attendanceClassesDto);
        int totalPage = (int) Math.ceil((double) count / (double) attendanceClassesDto.getRow());
        List<AttendanceClassesDto> attendanceClassesDtos = null;
        if (count > 0) {
            attendanceClassesDtos = attendanceClassesServiceDao.getAttendanceClassess(attendanceClassesDto);
        } else {
            attendanceClassesDtos = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, attendanceClassesDtos);
        return resultDto;
    }


    @Override
    public List<AttendanceClassesDto> getAttendanceClasses(AttendanceClassesDto attendanceClassesDto) {
        int page = attendanceClassesDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            attendanceClassesDto.setPage((page - 1) * attendanceClassesDto.getRow());
        }

        List<AttendanceClassesDto> attendanceClassesDtos = null;

        attendanceClassesDtos = attendanceClassesServiceDao.getAttendanceClassess(attendanceClassesDto);

        return attendanceClassesDtos;
    }

    @Override
    public ResultDto getDepartments(StaffDto staffDto) {
        List<StaffDto> staffDtos = staffServiceDao.getDepartments(staffDto);
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, staffDtos);
        return resultDto;
    }

    @Override
    public ResultDto getStaffs(StaffDto staffDto) {
        int page = staffDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            staffDto.setPage((page - 1) * staffDto.getRow());
        }
        long count = staffServiceDao.getStaffCount(staffDto);
        int totalPage = (int) Math.ceil((double) count / (double) staffDto.getRow());
        List<StaffDto> staffDtos = null;
        if (count > 0) {
            staffDtos = staffServiceDao.getStaffs(staffDto);
        } else {
            staffDtos = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, staffDtos);
        return resultDto;
    }

    @Override
    public List<StaffDto> queryStaffs(StaffDto staffDto) {
        int page = staffDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            staffDto.setPage((page - 1) * staffDto.getRow());
        }

        List<StaffDto> staffDtos = null;

        staffDtos = staffServiceDao.getStaffs(staffDto);

        return staffDtos;
    }


    @Override
    public ResultDto saveClassStaff(AttendanceClassesStaffDto attendanceClassesStaffDto) throws Exception {


        long flag = attendanceClassesServiceDao.saveAttendanceClassesStaff(attendanceClassesStaffDto);
        if (flag > 0) {
            return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
    }


    @Override
    public ResultDto deleteClassStaff(AttendanceClassesStaffDto attendanceClassesStaffDto) {
        AttendanceClassesStaffDto attendanceClassesStaffDto1 = new AttendanceClassesStaffDto();
        attendanceClassesStaffDto1.setCsId(attendanceClassesStaffDto.getCsId());
        attendanceClassesStaffDto1.setStatusCd("1");
        long flag = attendanceClassesServiceDao.updateAttendanceClassesStaffDto(attendanceClassesStaffDto1);
        if (flag > 0) {
            return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
    }

    /**
     * ??????????????????
     *
     * @param machineCmdDto ????????????
     * @return
     */
    private List<MachineCmdDto> getMachineCmd(MachineCmdDto machineCmdDto) throws Exception {
        ICallAttendanceService callAttendanceService = CallAttendanceFactory.getCallAttendanceService();
        List<MachineCmdDto> machineCmdDtos = callAttendanceService.getMachineCmds(machineCmdDto);
        return machineCmdDtos;
    }


    @Override
    public ResultDto getClassStaffs(AttendanceClassesStaffDto attendanceClassesStaffDto) {
        int page = attendanceClassesStaffDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            attendanceClassesStaffDto.setPage((page - 1) * attendanceClassesStaffDto.getRow());
        }
        long count = attendanceClassesServiceDao.getAttendanceClassesStaffCount(attendanceClassesStaffDto);
        int totalPage = (int) Math.ceil((double) count / (double) attendanceClassesStaffDto.getRow());
        List<AttendanceClassesStaffDto> attendanceClassesStaffDtos = null;
        if (count > 0) {
            attendanceClassesStaffDtos = attendanceClassesServiceDao.getAttendanceClassesStaffs(attendanceClassesStaffDto);
        } else {
            attendanceClassesStaffDtos = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, attendanceClassesStaffDtos);
        return resultDto;
    }

    @Override
    public List<AttendanceClassesStaffDto> queryClassStaffs(AttendanceClassesStaffDto attendanceClassesStaffDto) {
        int page = attendanceClassesStaffDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            attendanceClassesStaffDto.setPage((page - 1) * attendanceClassesStaffDto.getRow());
        }

        List<AttendanceClassesStaffDto> attendanceClassesStaffDtos = null;

        attendanceClassesStaffDtos = attendanceClassesServiceDao.getAttendanceClassesStaffs(attendanceClassesStaffDto);

        return attendanceClassesStaffDtos;
    }

    @Override
    public ResultDto getAttendanceTasks(AttendanceClassesTaskDto attendanceClassesTaskDto) {
        int page = attendanceClassesTaskDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            attendanceClassesTaskDto.setPage((page - 1) * attendanceClassesTaskDto.getRow());
        }
        long count = attendanceClassesServiceDao.getAttendanceClassesTaskCount(attendanceClassesTaskDto);
        int totalPage = (int) Math.ceil((double) count / (double) attendanceClassesTaskDto.getRow());
        List<AttendanceClassesTaskDto> attendanceClassesTaskDtos = null;
        if (count > 0) {
            attendanceClassesTaskDtos = attendanceClassesServiceDao.getAttendanceClassesTasks(attendanceClassesTaskDto);
            for (AttendanceClassesTaskDto tmpAttendanceClassesTaskDto : attendanceClassesTaskDtos) {
                AttendanceClassesTaskDetailDto attendanceClassesTaskDetailDto = new AttendanceClassesTaskDetailDto();
                attendanceClassesTaskDetailDto.setTaskId(tmpAttendanceClassesTaskDto.getTaskId());
                attendanceClassesTaskDetailDto.setStatusCd("0");
                List<AttendanceClassesTaskDetailDto> attendanceClassesTaskDetailDtos
                        = attendanceClassesServiceDao.getAttendanceClassesTaskDetails(attendanceClassesTaskDetailDto);

                if (attendanceClassesTaskDetailDtos != null && attendanceClassesTaskDetailDtos.size() > 0) {
                    freshUserFace(attendanceClassesTaskDetailDtos);
                    tmpAttendanceClassesTaskDto.setAttendanceClassesTaskDetails(attendanceClassesTaskDetailDtos);
                }


            }
        } else {
            attendanceClassesTaskDtos = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, attendanceClassesTaskDtos);
        return resultDto;
    }

    @Override
    public ResultDto getMonthAttendance(AttendanceClassesTaskDto attendanceClassesTaskDto) {
        int page = attendanceClassesTaskDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            attendanceClassesTaskDto.setPage((page - 1) * attendanceClassesTaskDto.getRow());
        }
        long count = attendanceClassesServiceDao.getMonthAttendanceCount(attendanceClassesTaskDto);
        int totalPage = (int) Math.ceil((double) count / (double) attendanceClassesTaskDto.getRow());
        List<AttendanceClassesTaskDto> attendanceClassesTaskDtos = null;
        if (count > 0) {
            attendanceClassesTaskDtos = attendanceClassesServiceDao.getMonthAttendance(attendanceClassesTaskDto);
        } else {
            attendanceClassesTaskDtos = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, attendanceClassesTaskDtos);
        return resultDto;
    }

    @Override
    public ResultDto getStaffAttendanceLog(StaffAttendanceLogDto staffAttendanceLogDto) {
        int page = staffAttendanceLogDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            staffAttendanceLogDto.setPage((page - 1) * staffAttendanceLogDto.getRow());
        }
        long count = attendanceClassesServiceDao.getStaffAttendanceLogCount(staffAttendanceLogDto);
        int totalPage = (int) Math.ceil((double) count / (double) staffAttendanceLogDto.getRow());
        List<StaffAttendanceLogDto> staffAttendanceLogDtos = null;
        if (count > 0) {
            staffAttendanceLogDtos = attendanceClassesServiceDao.getStaffAttendanceLogs(staffAttendanceLogDto);
        } else {
            staffAttendanceLogDtos = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, staffAttendanceLogDtos);
        return resultDto;
    }


    /**
     * ??????????????????
     *
     * @param attendanceClassesTaskDetailDtos
     */
    private void freshUserFace(List<AttendanceClassesTaskDetailDto> attendanceClassesTaskDetailDtos) {
        String faceUrl = MappingCacheFactory.getValue("ACCESS_CONTROL_FACE_URL");

        for (AttendanceClassesTaskDetailDto tmpAttendanceClassesTaskDetailDto : attendanceClassesTaskDetailDtos) {
            if (StringUtil.isEmpty(tmpAttendanceClassesTaskDetailDto.getFacePath())) {
                continue;
            }
            tmpAttendanceClassesTaskDetailDto.setFacePath(faceUrl + tmpAttendanceClassesTaskDetailDto.getFacePath());
        }
    }


    /**
     * ????????????
     *
     * @param attendanceClassesDto
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto deleteAttendanceClassesDto(AttendanceClassesDto attendanceClassesDto) {
        int count = attendanceClassesServiceDao.deleteAttendanceClasses(attendanceClassesDto.getClassesId());
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * ??????????????????
     *
     * @param attendanceClassesAttrDto
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto deleteAttendanceClassesAttrDto(AttendanceClassesAttrDto attendanceClassesAttrDto) {
        int count = attendanceClassesServiceDao.deleteAttendanceClassesAttr(attendanceClassesAttrDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


    /**
     * ??????????????????
     *
     * @param attendanceClassesDto ????????????
     * @return
     */
    @Override
    public ResultDto updateAttendanceClasses(AttendanceClassesDto attendanceClassesDto) {
        int count = attendanceClassesServiceDao.updateAttendanceClasses(attendanceClassesDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * ??????????????????
     *
     * @param attendanceClassesDto ????????????
     * @return
     */
    @Override
    @Transactional
    public ResultDto insertAttendanceClassesDto(AttendanceClassesDto attendanceClassesDto, List<AttendanceClassesAttrDto> attrDtos) {
        int count = attendanceClassesServiceDao.saveAttendanceClasses(attendanceClassesDto);
        for (AttendanceClassesAttrDto attrDto : attrDtos) {
            attrDto.setAttrId(SeqUtil.getId());
            attrDto.setClassesId(attendanceClassesDto.getClassesId());
            attrDto.setStatusCd("0");
            attendanceClassesServiceDao.saveAttendanceClassesAttr(attrDto);
        }
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * ??????????????????
     *
     * @param attrDto ????????????
     * @return
     */
    @Override
    @Transactional
    public ResultDto saveAttendanceClassesAttrDto(AttendanceClassesAttrDto attrDto) {

        int count = attendanceClassesServiceDao.saveAttendanceClassesAttr(attrDto);

        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * ??????????????????????????????
     *
     * @param attrDto ????????????
     * @return
     */
    public ResultDto getAttendanceClassesAttrs(AttendanceClassesAttrDto attrDto) {
        List<AttendanceClassesAttrDto> attrDtoList = attendanceClassesServiceDao.getAttendanceClassesAttrs(attrDto);
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, attrDtoList);
        return resultDto;
    }
}

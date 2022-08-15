package com.java110.things.service.staff.impl;

import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.dao.IMachineServiceDao;
import com.java110.things.dao.IStaffServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.StaffDto;
import com.java110.things.factory.AttendanceProcessFactory;
import com.java110.things.factory.ImageFactory;
import com.java110.things.service.machine.IMachineCmdService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.service.staff.IStaffService;
import com.java110.things.util.Assert;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName StaffServiceImpl
 * @Description TODO 小区管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("staffServiceImpl")
public class StaffServiceImpl implements IStaffService {

    @Autowired
    private IStaffServiceDao staffServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IMachineService machineService;

    @Autowired
    private IMachineCmdService machineCmdServiceImpl;

    @Autowired
    private IMachineServiceDao machineServiceDaoImpl;

    public static final String MACHINE_HAS_NOT_FACE = "-1"; // 设备没有人脸

    /**
     * 添加小区信息
     *
     * @param staffDto 小区对象
     * @return
     */
    @Override
    public ResultDto saveStaff(StaffDto staffDto) throws Exception {
        ResultDto resultDto = null;

        //如果有 照片
        if (!StringUtil.isEmpty(staffDto.getFaceBase64())) {
            MachineDto machineDto = new MachineDto();
            machineDto.setMachineCode(staffDto.getMachineCode());
            List<MachineDto> machineDtos = machineServiceDaoImpl.getMachines(machineDto);
            //Assert.listOnlyOne(machineDtos, "设备编码错误，不存在该设备");

            if (machineDtos == null || machineDtos.size() < 1) {
                //Assert.listOnlyOne(machineDtos, "设备编码错误，不存在该设备");
                throw new IllegalArgumentException("设备编码错误，不存在该设备");
            }
            machineDto = machineDtos.get(0);
            boolean exists = ImageFactory.existsImage(staffDto.getExtCommunityId() + File.separatorChar + staffDto.getExtStaffId() + ".jpg");
            String faceId = exists ? staffDto.getExtStaffId() : null;
            //调用新增人脸接口
            if (StringUtil.isEmpty(faceId) || MACHINE_HAS_NOT_FACE.equals(faceId)) {
                //存储人脸
                String faceBase = staffDto.getFaceBase64();
                if (faceBase.contains("base64,")) {
                    faceBase = faceBase.substring(faceBase.indexOf("base64,") + 7);
                }
                String img = ImageFactory.GenerateImage(faceBase, staffDto.getExtCommunityId() + File.separatorChar + staffDto.getExtStaffId() + ".jpg");
                resultDto = AttendanceProcessFactory.getAttendanceProcessImpl(machineDto.getHmId()).addFace(machineDto, staffDto);
            } else { //调用更新人脸接口
                ImageFactory.deleteImage(machineDto.getMachineCode() + File.separatorChar + staffDto.getExtStaffId() + ".jpg");
                String faceBase = staffDto.getFaceBase64();
                if (faceBase.contains("base64,")) {
                    faceBase = faceBase.substring(faceBase.indexOf("base64,") + 7);
                }
                String img = ImageFactory.GenerateImage(faceBase, machineDto.getCommunityId() + File.separatorChar + staffDto.getExtStaffId() + ".jpg");
                resultDto = AttendanceProcessFactory.getAttendanceProcessImpl(machineDto.getHmId()).updateFace(machineDto, staffDto);
            }
            if (resultDto == null) {
                return resultDto;
            }
        }
        //设备写值
        addStaffMachineCmd(staffDto);

        int count = staffServiceDao.saveStaff(staffDto);

        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    private void addStaffMachineCmd(StaffDto staffDto) throws Exception {


        //根据部门查询设备
        MachineDto machineDto = new MachineDto();

        machineDto.setMachineTypeCd(MachineDto.MACHINE_TYPE_ATTENDANCE);
        machineDto.setLocationType("5000");
        machineDto.setLocationObjId(staffDto.getDepartmentId());
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);

        if (machineDtos == null || machineDtos.size() < 1) {
            return;
        }

        for (MachineDto machineDto1 : machineDtos) {
            MachineCmdDto machineCmdDto = new MachineCmdDto();
            machineCmdDto.setMachineCode(machineDto1.getMachineCode());
            machineCmdDto.setState("1000");
            machineCmdDto.setCmdId(SeqUtil.getId());
            machineCmdDto.setCmdName("同步人员");
            machineCmdDto.setMachineTypeCd(machineDto1.getMachineTypeCd());
            machineCmdDto.setObjType("002");
            machineCmdDto.setObjTypeValue(staffDto.getStaffId());
            machineCmdDto.setCmdCode("101");
            machineCmdDto.setCommunityId(machineDto1.getCommunityId());
            machineCmdDto.setMachineId(machineDto1.getMachineId());
            machineCmdServiceImpl.saveMachineCmd(machineCmdDto);
        }
    }


    /**
     * 查询小区信息
     *
     * @param staffDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getStaff(StaffDto staffDto) throws Exception {

        if (staffDto.getPage() != PageDto.DEFAULT_PAGE) {
            staffDto.setPage((staffDto.getPage() - 1) * staffDto.getRow());
        }
        long count = staffServiceDao.getStaffCount(staffDto);
        int totalPage = (int) Math.ceil((double) count / (double) staffDto.getRow());
        List<StaffDto> staffDtoList = null;
        if (count > 0) {
            staffDtoList = staffServiceDao.getStaffs(staffDto);
            //刷新人脸地
        } else {
            staffDtoList = new ArrayList<>();
        }

        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, staffDtoList);
        return resultDto;
    }

    @Override
    public List<StaffDto> queryStaffs(StaffDto staffDto) throws Exception {
        if (staffDto.getPage() != PageDto.DEFAULT_PAGE) {
            staffDto.setPage((staffDto.getPage() - 1) * staffDto.getRow());
        }
        List<StaffDto> staffDtoList = null;

        staffDtoList = staffServiceDao.getStaffs(staffDto);
        //刷新人脸地
        return staffDtoList;

    }


    @Override
    public ResultDto updateStaff(StaffDto staffDto) throws Exception {
        //修改传送第三方平台
        ResultDto resultDto = null;

        if (!StringUtil.isEmpty(staffDto.getFaceBase64()) && !StringUtil.isEmpty(staffDto.getMachineCode())) {
            MachineDto machineDto = new MachineDto();
            machineDto.setMachineCode(staffDto.getMachineCode());
            List<MachineDto> machineDtos = machineServiceDaoImpl.getMachines(machineDto);
            //Assert.listOnlyOne(machineDtos, "设备编码错误，不存在该设备");

            if (machineDtos == null || machineDtos.size() < 1) {
                //Assert.listOnlyOne(machineDtos, "设备编码错误，不存在该设备");
                throw new IllegalArgumentException("设备编码错误，不存在该设备");
            }
            machineDto = machineDtos.get(0);
            ImageFactory.deleteImage(machineDto.getMachineCode() + File.separatorChar + staffDto.getExtStaffId() + ".jpg");
            String faceBase = staffDto.getFaceBase64();
            if (faceBase.contains("base64,")) {
                faceBase = faceBase.substring(faceBase.indexOf("base64,") + 7);
            }
            String img = ImageFactory.GenerateImage(faceBase, machineDto.getCommunityId() + File.separatorChar + staffDto.getExtStaffId() + ".jpg");
            resultDto = AttendanceProcessFactory.getAttendanceProcessImpl(machineDto.getHmId()).updateFace(machineDto, staffDto);
        }

        if (resultDto != null && resultDto.getCode() != ResultDto.SUCCESS) {
            return resultDto;
        }

        int count = staffServiceDao.updateStaff(staffDto);

        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


    @Override
    public ResultDto deleteStaff(StaffDto staffDto) throws Exception {

        ResultDto resultDto = null;

        if (!StringUtil.isEmpty(staffDto.getMachineCode())) {
            MachineDto machineDto = new MachineDto();
            machineDto.setMachineCode(staffDto.getMachineCode());
            List<MachineDto> machineDtos = machineServiceDaoImpl.getMachines(machineDto);
            Assert.listOnlyOne(machineDtos, "设备编码错误，不存在该设备");
            machineDto = machineDtos.get(0);
            resultDto = AttendanceProcessFactory.getAttendanceProcessImpl(machineDto.getHmId()).deleteFace(machineDto, staffDto);
        }
        if (resultDto != null && resultDto.getCode() != ResultDto.SUCCESS) {
            return resultDto;
        }
        deleteStaffMachineCmd(staffDto);
        staffDto.setStatusCd("1");
        int count = staffServiceDao.updateStaff(staffDto);
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


    private void deleteStaffMachineCmd(StaffDto staffDto) throws Exception {


        //根据部门查询设备
        MachineDto machineDto = new MachineDto();

        machineDto.setMachineTypeCd(MachineDto.MACHINE_TYPE_ATTENDANCE);
        machineDto.setLocationType("5000");
        machineDto.setLocationObjId(staffDto.getDepartmentId());
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);

        if (machineDtos == null || machineDtos.size() < 1) {
            return;
        }

        for (MachineDto machineDto1 : machineDtos) {
            MachineCmdDto machineCmdDto = new MachineCmdDto();
            machineCmdDto.setMachineCode(machineDto1.getMachineCode());
            machineCmdDto.setState("1000");
            machineCmdDto.setCmdId(SeqUtil.getId());
            machineCmdDto.setCmdName("同步人员");
            machineCmdDto.setMachineTypeCd(machineDto1.getMachineTypeCd());
            machineCmdDto.setObjType("002");
            machineCmdDto.setObjTypeValue(staffDto.getStaffId());
            machineCmdDto.setCmdCode(MachineConstant.CMD_DELETE_FACE);
            machineCmdDto.setCommunityId(machineDto1.getCommunityId());
            machineCmdDto.setMachineId(machineDto1.getMachineId());
            machineCmdServiceImpl.saveMachineCmd(machineCmdDto);
        }
    }


}

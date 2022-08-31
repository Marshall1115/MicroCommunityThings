package com.java110.core.factory;

import com.java110.core.adapt.attendance.IAttendanceMachineProcess;
import com.java110.core.constant.ResponseConstant;
import com.java110.entity.manufacturer.ManufacturerDto;
import com.java110.entity.response.ResultDto;
import com.java110.core.exception.Result;
import com.java110.core.exception.ThreadException;
import com.java110.core.adapt.attendance.IAttendanceProcess;
import com.java110.core.service.manufacturer.IManufacturerService;
import com.java110.core.util.Assert;

import java.util.List;

/**
 * @ClassName AssessControlProcessFactory
 * @Description TODO 云考勤工厂类
 * @Author wuxw
 * @Date 2020/5/20 21:00
 * @Version 1.0
 * add by wuxw 2020/5/20
 **/
public class AttendanceProcessFactory {

    /**
     * 访问硬件接口
     */
    private static IAttendanceProcess attendanceProcessImpl;


    /**
     * 访问硬件接口
     */
    private static IAttendanceMachineProcess attendanceMachineProcessImpl;

    /**
     * 获取硬件接口对象
     *
     * @return
     */
    public static IAttendanceMachineProcess getAttendanceProcessImpl(String hmId) throws Exception {
        if (attendanceMachineProcessImpl != null) {
            return attendanceMachineProcessImpl;
        }
        IManufacturerService manufacturerServiceImpl = ApplicationContextFactory.getBean("manufacturerServiceImpl", IManufacturerService.class);
        ManufacturerDto tmpManufacturerDto = new ManufacturerDto();
        tmpManufacturerDto.setHmType("4004");
        tmpManufacturerDto.setHmId(hmId);
        ResultDto resultDto = manufacturerServiceImpl.getManufacturer(tmpManufacturerDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, resultDto.getMsg());
        }

        List<ManufacturerDto> manufacturerDtos = (List<ManufacturerDto>) resultDto.getData();

        Assert.listOnlyOne(manufacturerDtos, "当前有多个默认协议或者一个都没有");
        attendanceMachineProcessImpl = ApplicationContextFactory.getBean(manufacturerDtos.get(0).getProtocolImpl(), IAttendanceMachineProcess.class);
        return attendanceMachineProcessImpl;
    }

    /**
     * 获取硬件接口对象
     *
     * @return
     */
    public static IAttendanceProcess getAttendanceProcessImpl() throws Exception {
        if (attendanceProcessImpl != null) {
            return attendanceProcessImpl;
        }
        IManufacturerService manufacturerServiceImpl = ApplicationContextFactory.getBean("manufacturerServiceImpl", IManufacturerService.class);
        ManufacturerDto tmpManufacturerDto = new ManufacturerDto();
        tmpManufacturerDto.setHmType("4004");
        tmpManufacturerDto.setDefaultProtocol("T");
        ResultDto resultDto = manufacturerServiceImpl.getManufacturer(tmpManufacturerDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, resultDto.getMsg());
        }

        List<ManufacturerDto> manufacturerDtos = (List<ManufacturerDto>) resultDto.getData();

        Assert.listOnlyOne(manufacturerDtos, "当前有多个默认协议或者一个都没有");
        attendanceProcessImpl = ApplicationContextFactory.getBean(manufacturerDtos.get(0).getProtocolImpl(), IAttendanceProcess.class);
        return attendanceProcessImpl;
    }

}

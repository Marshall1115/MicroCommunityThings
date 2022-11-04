package com.java110.core.service.hc;

import com.alibaba.fastjson.JSONObject;
import com.java110.entity.attendance.AttendanceClassesTaskDetailDto;
import com.java110.entity.attendance.StaffAttendanceLogDto;

/**
 * 调用HC小区管理系统
 * 这里设计的目的是为了解决异步调用问题
 *
 * @ClassName IAttendanceCallHcService
 * @Description TODO
 * @Author wuxw
 * @Date 2021/1/18 20:50
 * @Version 1.0
 * add by wuxw 2021/1/18
 **/
public interface IAttendanceCallHcService {

    /**
     * 考勤任务生成上报
     *
     * @param taskId
     * @return
     */
    public void upload(String taskId) throws Exception;

    /**
     * 考勤
     *
     * @throws Exception
     */
    void checkIn(AttendanceClassesTaskDetailDto attendanceClassesTaskDetailDto, boolean finishAllTaskDetail) throws Exception;

    /**
     * 考勤记录同步
     *
     * @param paramIn
     */
    void checkInTime(JSONObject paramIn) throws Exception;
}

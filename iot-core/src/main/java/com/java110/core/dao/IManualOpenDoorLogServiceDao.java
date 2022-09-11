package com.java110.core.dao;

import com.java110.entity.machine.SystemExceptionDto;
import com.java110.entity.openDoor.ManualOpenDoorLogDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IManualOpenDoorLogServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/22 14:54
 * @Version 1.0
 * add by wuxw 2020/5/22
 **/
@Mapper
public interface IManualOpenDoorLogServiceDao {
    /**
     * 保存操作日志信息
     *
     * @param ManualOpenDoorLogDto 操作日志信息
     * @return 返回影响记录数
     */
    int saveManualOpenDoorLog(ManualOpenDoorLogDto ManualOpenDoorLogDto);

    /**
     * 修改操作日志信息
     *
     * @param ManualOpenDoorLogDto 操作日志信息
     * @return 返回影响记录数
     */
    int updateManualOpenDoorLog(ManualOpenDoorLogDto ManualOpenDoorLogDto);

    /**
     * 查询操作日志信息
     *
     * @param ManualOpenDoorLogDto 操作日志信息
     * @return
     */
    List<ManualOpenDoorLogDto> getManualOpenDoorLogs(ManualOpenDoorLogDto ManualOpenDoorLogDto);

    /**
     * 查询操作日志总记录数
     *
     * @param ManualOpenDoorLogDto 操作日志信息
     * @return
     */
    long getManualOpenDoorLogCount(ManualOpenDoorLogDto ManualOpenDoorLogDto);

}

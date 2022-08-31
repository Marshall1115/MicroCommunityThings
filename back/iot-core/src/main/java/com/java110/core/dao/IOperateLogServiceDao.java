package com.java110.core.dao;

import com.java110.entity.machine.MachineDto;
import com.java110.entity.machine.OperateLogDto;
import com.java110.entity.machine.SystemExceptionDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IOperateLogServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/22 14:54
 * @Version 1.0
 * add by wuxw 2020/5/22
 **/
@Mapper
public interface IOperateLogServiceDao {
    /**
     * 保存操作日志信息
     *
     * @param operateLogDto 操作日志信息
     * @return 返回影响记录数
     */
    int saveOperateLog(OperateLogDto operateLogDto);

    /**
     * 修改操作日志信息
     *
     * @param operateLogDto 操作日志信息
     * @return 返回影响记录数
     */
    int updateOperateLog(OperateLogDto operateLogDto);

    /**
     * 查询操作日志信息
     * @param operateLogDto 操作日志信息
     * @return
     */
    List<OperateLogDto> getOperateLogs(OperateLogDto operateLogDto);

    /**
     * 查询操作日志总记录数
     * @param operateLogDto 操作日志信息
     * @return
     */
    long getOperateLogCount(OperateLogDto operateLogDto);


    /**
     * 保存系统异常
     * @param systemExceptionDto
     * @return
     */
    int saveSystemException(SystemExceptionDto systemExceptionDto);
}

package com.java110.core.service.machine;

import com.java110.entity.machine.OperateLogDto;
import com.java110.entity.response.ResultDto;

import java.util.List;

/**
 * @ClassName IOperateLogService
 * @Description TODO 硬件操作日志记录类
 * @Author wuxw
 * @Date 2020/5/22 14:50
 * @Version 1.0
 * add by wuxw 2020/5/22
 **/
public interface IOperateLogService {

    /**
     * 保存操作日志
     * @param operateLogDto 操作日志对象
     * @return
     */
    ResultDto saveOperateLog(OperateLogDto operateLogDto);

    /**
     * 查询操作日志
     * @param operateLogDto 操作日志对象
     * @return
     */
    ResultDto getOperateLogs(OperateLogDto operateLogDto);

    /**
     * 查询操作日志
     * @param operateLogDto 操作日志对象
     * @return
     */
    List<OperateLogDto> queryOperateLogs(OperateLogDto operateLogDto);
}

package com.java110.core.service.machine.impl;

import com.java110.core.dao.IOperateLogServiceDao;
import com.java110.entity.machine.SystemExceptionDto;
import com.java110.core.service.machine.ISystemExceptionService;
import com.java110.core.util.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName OperateLogServiceImpl
 * @Description TODO 操作日志服务类
 * @Author wuxw
 * @Date 2020/5/22 14:54
 * @Version 1.0
 * add by wuxw 2020/5/22
 **/
@Service("SystemExceptionServiceImpl")
public class SystemExceptionServiceImpl implements ISystemExceptionService {

    @Autowired
    IOperateLogServiceDao operateLogServiceDao;

    @Override
    public void save(SystemExceptionDto systemExceptionDto) {
        operateLogServiceDao.saveSystemException(systemExceptionDto);
    }

    @Override
    public void save(String exceptionType, String objId, String machineId, String errMsg) {
        SystemExceptionDto systemExceptionDto = new SystemExceptionDto();
        systemExceptionDto.setExceptionId(SeqUtil.getId());
        systemExceptionDto.setErrMsg(errMsg);
        systemExceptionDto.setExceptionType(exceptionType);
        systemExceptionDto.setObjId(objId);
        systemExceptionDto.setMachineId(machineId);
        operateLogServiceDao.saveSystemException(systemExceptionDto);
    }

    @Override
    public void save(String exceptionType, String errMsg) {
        SystemExceptionDto systemExceptionDto = new SystemExceptionDto();
        systemExceptionDto.setExceptionId(SeqUtil.getId());
        systemExceptionDto.setErrMsg(errMsg);
        systemExceptionDto.setExceptionType(exceptionType);
        operateLogServiceDao.saveSystemException(systemExceptionDto);
    }

}

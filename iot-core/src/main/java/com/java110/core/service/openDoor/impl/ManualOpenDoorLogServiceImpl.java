package com.java110.core.service.openDoor.impl;

import com.java110.core.constant.ResponseConstant;
import com.java110.core.dao.IManualOpenDoorLogServiceDao;
import com.java110.core.service.openDoor.IManualOpenDoorLogService;
import com.java110.entity.PageDto;
import com.java110.entity.openDoor.ManualOpenDoorLogDto;
import com.java110.entity.response.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OpenDoorServiceImpl
 * @Description TODO 设备管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("manualOpenDoorLogServiceImpl")
public class ManualOpenDoorLogServiceImpl implements IManualOpenDoorLogService {

    @Autowired
    private IManualOpenDoorLogServiceDao manualOpenDoorLogServiceDao;

    /**
     * 添加设备信息
     *
     * @param openDoorDto 设备对象
     * @return
     */
    @Override
    public ResultDto saveManualOpenDoorLog(ManualOpenDoorLogDto openDoorDto) throws Exception {
        int count = manualOpenDoorLogServiceDao.saveManualOpenDoorLog(openDoorDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * 修改图片
     *
     * @param openDoorDto 设备对象
     * @return
     */
    @Override
    public ResultDto updateManualOpenDoorLog(ManualOpenDoorLogDto openDoorDto)  {
        int count = manualOpenDoorLogServiceDao.updateManualOpenDoorLog(openDoorDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


    /**
     * 查询设备信息
     *
     * @param openDoorDto 设备信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getManualOpenDoorLog(ManualOpenDoorLogDto openDoorDto) throws Exception {
        int page = openDoorDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            openDoorDto.setPage((page - 1) * openDoorDto.getRow());
        }
        long count = manualOpenDoorLogServiceDao.getManualOpenDoorLogCount(openDoorDto);
        int totalPage = (int) Math.ceil((double) count / (double) openDoorDto.getRow());
        List<ManualOpenDoorLogDto> openDoorDtoList = null;
        if (count > 0) {
            openDoorDtoList = manualOpenDoorLogServiceDao.getManualOpenDoorLogs(openDoorDto);
        } else {
            openDoorDtoList = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, openDoorDtoList);
        return resultDto;
    }


}

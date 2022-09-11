package com.java110.core.service.openDoor;

import com.java110.entity.openDoor.ManualOpenDoorLogDto;
import com.java110.entity.response.ResultDto;

/**
 * @ClassName IUserService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IManualOpenDoorLogService {

    /**
     * 保存开门信息
     *
     * @param ManualOpenDoorLogDto 开门信息
     * @return
     * @throws Exception
     */
    ResultDto saveManualOpenDoorLog(ManualOpenDoorLogDto ManualOpenDoorLogDto) throws Exception;

    /**
     * 修改 开门图片
     *
     * @param openDoorDto
     * @return
     * @throws Exception
     */
    ResultDto updateManualOpenDoorLog(ManualOpenDoorLogDto openDoorDto) ;

    /**
     * 获取开门信息
     *
     * @param ManualOpenDoorLogDto 开门信息
     * @return
     * @throws Exception
     */
    ResultDto getManualOpenDoorLog(ManualOpenDoorLogDto ManualOpenDoorLogDto) throws Exception;


}

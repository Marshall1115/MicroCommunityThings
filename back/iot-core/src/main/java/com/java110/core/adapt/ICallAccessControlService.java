package com.java110.core.adapt;

import com.java110.entity.accessControl.UserFaceDto;
import com.java110.entity.cloud.MachineCmdResultDto;
import com.java110.entity.cloud.MachineHeartbeatDto;
import com.java110.entity.cloud.MachineUploadFaceDto;
import com.java110.entity.fee.FeeDto;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.machine.MachineFaceDto;
import com.java110.entity.machine.OperateLogDto;
import com.java110.entity.openDoor.OpenDoorDto;
import com.java110.entity.room.RoomDto;

import java.util.List;

/**
 * @ClassName INotifyAccessControlServcie
 * @Description TODO 门禁硬件回调 接口类
 * @Author wuxw
 * @Date 2020/5/15 19:12
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
public interface ICallAccessControlService {

    /**
     * 查询设备信息
     *
     * @return
     */
    public List<MachineDto> queryMachines() throws Exception;

    /**
     * 查询设备信息
     *
     * @return
     */
    public List<MachineDto> queryMachines(MachineDto machineDto) throws Exception;

    /**
     * 查询设备人脸
     *
     * @return
     */
    public List<MachineFaceDto> queryMachineFaces(MachineFaceDto machineFaceDto) ;

    /**
     * 门禁 上报,当门禁上线时 建议将门禁自动上报，系统管理页面和云端可以自动注册设备
     *
     * @param machineDto 设备对象
     */
    void uploadMachine(MachineDto machineDto);

    /**
     * 记录操作日志
     *
     * @param operateLogDto 日志对象，当logId 在数据库中不存在是做添加，存在时 做修改
     */
    void saveOrUpdateOperateLog(OperateLogDto operateLogDto);

    /**
     * 人脸推送接口
     * 这里只处理 抓拍图片记录，模板图片本身就存在 不做记录
     *
     * @param openDoorDto 必填信息
     */
    void saveFaceResult(OpenDoorDto openDoorDto) throws Exception;

    /**
     * 获取用户房屋信息
     *
     * @param userFaceDto 根据用户人脸信息
     * @return 返回房屋列表
     */
    List<RoomDto> getRooms(UserFaceDto userFaceDto) throws Exception;

    /**
     * 查询费用信息
     *
     * @param roomDto 房屋信息
     * @return 费用列表
     */
    List<FeeDto> getFees(RoomDto roomDto) throws Exception;

    /**
     * 设备上报人脸
     *
     * @param machineUploadFaceDto 要求信息
     * @throws Exception
     */
    void machineUploadFace(MachineUploadFaceDto machineUploadFaceDto, String path) throws Exception;

    void machineCmdResult(MachineCmdResultDto machineCmdResultDto) throws Exception;

    /**
     * 设备心跳上报
     *
     * @param machineHeartbeatDto 心跳信息
     * @throws Exception
     */
    void machineHeartbeat(MachineHeartbeatDto machineHeartbeatDto) throws Exception;


}

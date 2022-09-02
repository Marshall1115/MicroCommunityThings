package com.java110.core.thread;

import com.java110.core.adapt.accessControl.BaseAccessControl;
import com.java110.entity.accessControl.HeartbeatTaskDto;
import com.java110.entity.community.CommunityDto;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.machine.MachineFaceDto;
import com.java110.core.factory.AccessControlProcessFactory;
import com.java110.core.factory.ImageFactory;
import com.java110.core.service.machine.IMachineFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 添加更新人脸
 */
@Component
public class ClearAllFace extends BaseAccessControl {
    @Autowired
    private IMachineFaceService machineFaceService;

    /**
     * 添加 更新人脸 方法
     *
     * @param heartbeatTaskDto 心跳下发任务指令
     */
    public void clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto, CommunityDto communityDto) throws Exception {

        //清空硬件下的人脸
        ImageFactory.clearImage(machineDto.getMachineCode());

        AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).clearFace(machineDto, heartbeatTaskDto);

        MachineFaceDto machineFaceDto = new MachineFaceDto();
        //machineFaceDto.setUserId(heartbeatTaskDto.getTaskinfo());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setTaskId(heartbeatTaskDto.getTaskid());
        //machineFaceDto.set
        machineFaceService.deleteMachineFace(machineFaceDto);
    }
}

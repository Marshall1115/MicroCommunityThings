package com.java110.accessControl.task;

import com.java110.core.config.Java110Properties;
import com.java110.core.service.task.TaskSystemQuartz;
import com.java110.core.thread.AddUpdateFace;
import com.java110.core.thread.DeleteFace;
import com.java110.core.thread.ScanMachine;
import com.java110.entity.task.TaskDto;
import com.java110.core.service.community.ICommunityService;
import com.java110.core.service.machine.IMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName ScanAccessControlMachineTemplate
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/8 16:53
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
@Component
public class ScanAccessControlMachineTemplate extends TaskSystemQuartz {

    /**
     * 初始化 硬件状态
     */
    public static boolean INIT_MACHINE_STATE = false;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Java110Properties java110Properties;


    @Autowired
    private AddUpdateFace addUpdateFace;

    @Autowired
    private DeleteFace deleteFace;
    @Autowired
    private ICommunityService communityService;

    @Autowired
    private IMachineService machineService;

    @Autowired
    private ScanMachine scanMachine;
    @Override
    protected void process(TaskDto taskDto) throws Exception {
        scanMachine.scan();

    }


}

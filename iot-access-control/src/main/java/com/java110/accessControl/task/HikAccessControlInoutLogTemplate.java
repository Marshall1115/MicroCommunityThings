package com.java110.accessControl.task;

import com.java110.accessControl.adapter.hik.HikQueryAccessControlInoutLogAdapt;
import com.java110.core.service.task.TaskSystemQuartz;
import com.java110.entity.task.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName ScanAccessControlMachineTemplate
 * @Description TODO 海康设备 门禁进出记录拉去
 * @Author wuxw
 * @Date 2020/6/8 16:53
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
@Component
public class HikAccessControlInoutLogTemplate extends TaskSystemQuartz {

    /**
     * 初始化 硬件状态
     */
    public static boolean INIT_MACHINE_STATE = false;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HikQueryAccessControlInoutLogAdapt hikQueryAccessControlInoutLogAdapt;

    /**
     * 执行定时任务
     * @param taskDto
     * @throws Exception
     */
    @Override
    protected void process(TaskDto taskDto) throws Exception {
        hikQueryAccessControlInoutLogAdapt.query();
    }


}

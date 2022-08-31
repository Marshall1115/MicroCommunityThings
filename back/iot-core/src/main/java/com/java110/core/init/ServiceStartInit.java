package com.java110.core.init;

import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.MappingCacheFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.service.machine.IMachineService;
import com.java110.core.service.manufacturer.IManufacturerService;
import com.java110.core.thread.HeartbeatCloudApiThread;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.manufacturer.ManufacturerAttrDto;
import com.java110.intf.inner.IAccessControlInitInnerService;
import com.java110.intf.inner.IGatewayInitInnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * 服务启动加载类
 * Created by wuxw on 2018/5/7.
 */
public class ServiceStartInit {

    private final static Logger logger = LoggerFactory.getLogger(ServiceStartInit.class);


    public static void initSystemConfig(ApplicationContext context) {
        //加载配置文件，注册订单处理侦听
        try {
            ApplicationContextFactory.setApplicationContext(context);
            //启动心跳
            //startHeartbeatCloudApiThread();

            //扫描门禁
            //startScanAccessCrontrolMachine();

            //清理会话
            startClearJwtThread();

            //mqtt 注册
            initMqttClientSubscribe();

            //刷入缓存
            freshCache();

        } catch (Exception ex) {
            logger.error("系统初始化失败", ex);
            throw new IllegalStateException("系统初始化失败", ex);
        }
    }

    private static void initMqttClientSubscribe() {
        //注册设备上线 topic
//        MqttFactory.subscribe("online.response");
//        //推送人脸识别结果
//        MqttFactory.subscribe("face.response");

        // 臻识mqtt订阅
        MqttFactory.subscribe("/device/push/result");


        //注册伊兰度设备
        IMachineService machineService = ApplicationContextFactory.getBean("machineServiceImpl", IMachineService.class);

        MachineDto machineDto = new MachineDto();
        machineDto.setHmId("3");
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            return;
        }

        for (MachineDto machineDto1 : machineDtos) {
            MqttFactory.subscribe("face." + machineDto1.getMachineCode() + ".response");
        }


        IManufacturerService manufacturerServiceImpl = ApplicationContextFactory.getBean("manufacturerServiceImpl", IManufacturerService.class);
        ManufacturerAttrDto tmpManufacturerDto = new ManufacturerAttrDto();
        tmpManufacturerDto.setSpecCd(ManufacturerAttrDto.SPEC_TOPIC);
        List<ManufacturerAttrDto> manufacturerAttrDtos = manufacturerServiceImpl.getManufacturerAttr(tmpManufacturerDto);

        if (manufacturerAttrDtos == null || manufacturerAttrDtos.size() < 1) {
            return;
        }

        //批量订阅
        for (ManufacturerAttrDto manufacturerAttrDto : manufacturerAttrDtos) {
            MqttFactory.subscribe(manufacturerAttrDto.getValue());
        }

    }

    /**
     * 刷新缓存
     */
    private static void freshCache() {
        //刷新映射缓存
        MappingCacheFactory.flushCacheMappings();
    }

    private static void startScanAccessCrontrolMachine() {
        IAccessControlInitInnerService accessControlInitInnerService = ApplicationContextFactory.getBean("accessControlInitInnerServiceImpl",IAccessControlInitInnerService.class);
        accessControlInitInnerService.init();
    }

    /**
     * 清理jwt 会话
     */
    private static void startClearJwtThread() {
        IGatewayInitInnerService accessControlInitInnerService = ApplicationContextFactory.getBean("gatewayInitInnerServiceImpl",IGatewayInitInnerService.class);
        accessControlInitInnerService.init();
    }

    /**
     * 启动心跳 线程
     */
    private static void startHeartbeatCloudApiThread() {
        //启动业主信息同步 线程 变更业主的场景
        HeartbeatCloudApiThread heartbeatCloudApiThread = new HeartbeatCloudApiThread(true);
        Thread ownerToMachineThread = new Thread(heartbeatCloudApiThread, "HeartbeatCloudApiThread");
        ownerToMachineThread.start();
    }


}

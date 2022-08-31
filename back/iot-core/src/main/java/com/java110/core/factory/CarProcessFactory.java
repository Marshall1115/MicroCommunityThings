package com.java110.core.factory;

import com.java110.core.adapt.barrier.ICarMachineProcess;
import com.java110.core.constant.ResponseConstant;
import com.java110.entity.manufacturer.ManufacturerDto;
import com.java110.entity.response.ResultDto;
import com.java110.core.exception.Result;
import com.java110.core.exception.ThreadException;
import com.java110.core.adapt.barrier.ICarProcess;
import com.java110.core.service.manufacturer.IManufacturerService;
import com.java110.core.util.Assert;

import java.util.List;

/**
 * @ClassName AssessControlProcessFactory
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/20 21:00
 * @Version 1.0
 * add by wuxw 2020/5/20
 **/
public class CarProcessFactory {

    /**
     * 访问硬件接口
     */
    private static ICarProcess carProcessImpl;
    /**
     * 获取硬件接口对象
     *
     * @return
     */
    public static ICarProcess getCarImpl(String hmId) throws Exception {

        IManufacturerService manufacturerServiceImpl = ApplicationContextFactory.getBean("manufacturerServiceImpl", IManufacturerService.class);
        ManufacturerDto tmpManufacturerDto = new ManufacturerDto();
        tmpManufacturerDto.setHmType("2002");
        tmpManufacturerDto.setHmId(hmId);
        ResultDto resultDto = manufacturerServiceImpl.getManufacturer(tmpManufacturerDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, resultDto.getMsg());
        }

        List<ManufacturerDto> manufacturerDtos = (List<ManufacturerDto>) resultDto.getData();

        Assert.listOnlyOne(manufacturerDtos, "当前有多个默认协议或者一个都没有");
        carProcessImpl = ApplicationContextFactory.getBean(manufacturerDtos.get(0).getProtocolImpl(), ICarProcess.class);
        return carProcessImpl;
    }

}

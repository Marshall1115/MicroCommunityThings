package com.java110.barrier.engine;

import com.java110.core.entity.car.BarrierGateControlDto;
import com.java110.core.service.hc.ICarCallHcService;
import com.java110.entity.machine.MachineDto;
import com.java110.intf.inner.IApiInnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendInfoEngine {

    @Autowired
    private IApiInnerService apiInnerServiceImpl;


    @Autowired
    private ICarCallHcService carCallHcServiceImpl;

    public void sendInfo(BarrierGateControlDto barrierGateControlDto, String extBoxId, MachineDto machineDto) throws Exception {

        apiInnerServiceImpl.barrierGateControlWebSocketServerSendInfo(barrierGateControlDto.toString(), extBoxId);


        carCallHcServiceImpl.carInoutPageInfo(barrierGateControlDto, extBoxId, machineDto);


    }
}

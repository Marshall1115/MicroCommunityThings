package com.java110.api.smo.impl;

import com.java110.api.controller.accessControl.OpenDoorMonitorWebSocketServer;
import com.java110.api.ws.BarrierGateControlWebSocketServer;
import com.java110.intf.inner.IApiInnerService;
import org.springframework.stereotype.Service;

@Service
public class ApiInnerServiceImpl implements IApiInnerService {
    @Override
    public void sendInfo(String msg, String machineId) throws Exception{
        OpenDoorMonitorWebSocketServer.sendInfo(msg, machineId);

    }

    @Override
    public void barrierGateControlWebSocketServerSendInfo(String message, String boxId) throws Exception {
        BarrierGateControlWebSocketServer.sendInfo(message,boxId);
    }
}

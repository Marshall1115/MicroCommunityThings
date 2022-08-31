package com.java110.gateway.smo;

import com.alibaba.fastjson.JSONObject;
import com.java110.entity.car.Java110CarProtocol;
import com.java110.entity.machine.MachineDto;
import com.java110.gateway.netty.NettySocketHolder;
import com.java110.gateway.netty.client.CarNettyClient;
import com.java110.intf.inner.IGatewayNettySocketHandle;
import org.springframework.stereotype.Service;

@Service
public class GatewayNettySocketHandleImpl implements IGatewayNettySocketHandle {
    @Override
    public JSONObject sendMsgSync(Java110CarProtocol java110CarProtocol, String queryId) {
        JSONObject resultJson = NettySocketHolder.sendMsgSync(java110CarProtocol, queryId);
        return resultJson;
    }

    @Override
    public boolean sendMsg(MachineDto machineDto, byte[] headerBytes, byte[] bytes) throws Exception {
        return CarNettyClient.sendMsg(machineDto, headerBytes, bytes);
    }

    public boolean sendMsg(MachineDto machineDto, byte[] bytes) throws Exception{
        return CarNettyClient.sendMsg(machineDto, bytes);

    }

    @Override
    public void twoGetChannel(MachineDto machineDto) {
        CarNettyClient.twoGetChannel(machineDto);
    }
}

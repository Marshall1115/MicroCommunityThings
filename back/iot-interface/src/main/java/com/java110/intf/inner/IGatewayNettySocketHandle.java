package com.java110.intf.inner;

import com.alibaba.fastjson.JSONObject;
import com.java110.entity.car.Java110CarProtocol;
import com.java110.entity.machine.MachineDto;

public interface IGatewayNettySocketHandle {
    JSONObject sendMsgSync(Java110CarProtocol java110CarProtocol, String queryId);

    boolean sendMsg(MachineDto machineDto, byte[] headerBytes, byte[] bytes) throws Exception;

    boolean sendMsg(MachineDto machineDto, byte[] bytes) throws Exception;

      void twoGetChannel(MachineDto machineDto);
}

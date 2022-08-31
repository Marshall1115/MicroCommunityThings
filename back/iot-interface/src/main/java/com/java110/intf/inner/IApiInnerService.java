package com.java110.intf.inner;

public interface IApiInnerService {

    void sendInfo(String msg,String machineId) throws Exception;
    void barrierGateControlWebSocketServerSendInfo(String message, String boxId) throws Exception;
}

package com.java110.gateway.init;

import com.java110.gateway.job.ClearExpireJwtThread;
import com.java110.intf.inner.IGatewayInitInnerService;
import org.springframework.stereotype.Service;

@Service
public class GatewayInitInnerServiceImpl implements IGatewayInitInnerService {
    @Override
    public void init() {
        ClearExpireJwtThread clearExpireJwtThread = new ClearExpireJwtThread(true);
        Thread clearThread = new Thread(clearExpireJwtThread, "ClearExpireJwtThread");
        clearThread.start();
    }
}

package com.java110.accessControl.init;

import com.java110.accessControl.job.ScanAccessControlThread;
import com.java110.intf.inner.IAccessControlInitInnerService;
import org.springframework.stereotype.Service;

@Service
public class AccessControlInitInnerServiceImpl implements IAccessControlInitInnerService {
    @Override
    public void init() {
        ScanAccessControlThread scanAccessControlThread = new ScanAccessControlThread(true);
        Thread sThread = new Thread(scanAccessControlThread, "ScanAccessControlThread");
        sThread.start();
    }
}

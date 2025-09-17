/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : BookingConferenceTask.java
 * Package     : com.paradisecloud.fcm.fme.conference.task
 * @author lilinhai
 * @since 2021-05-20 18:47
 * @version  V1.0
 */
package com.paradisecloud.fcm.license.monitor;


import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.license.LicenseExecutor;
import com.paradisecloud.fcm.license.LicenseManagerHolder;
import com.paradisecloud.fcm.terminal.fs.util.SpringContextUtil;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ConferencePartiantsCountMonitor extends Thread implements InitializingBean {


    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void run() {
        logger.info("ConferencePartiantsCountMonitor 启动成功！");

        while (true) {
            try {
                List<FmeBridge> fmeBridges = FmeBridgeCache.getInstance().getFmeBridges();
                if (CollectionUtils.isNotEmpty(fmeBridges)) {
                    for (FmeBridge fmeBridge : fmeBridges) {
                        if (fmeBridge.isAvailable()) {
                            doTask();
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Threads.sleep(1000*60);
            }
        }

    }

    private void doTask() {
        LicenseContent licenseContent = null;
        try {
            LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
            licenseContent = licenseManager.verify();

        } catch (Exception e) {
            Environment environment = SpringContextUtil.getApplicationContext().getEnvironment();
            String activeProfile = environment.getActiveProfiles()[0];
            if(!Objects.equals("prod",activeProfile)){
                return ;
            }
            LicenseExecutor.execParticipantLimit(0);

        }
        if (licenseContent != null) {
            try {
                logger.info("++++++++LicenseExecutor  exec  start ++++++++");
                LicenseExecutor.limitScheduledExecutorService(licenseContent, 0);
                Map extra = (Map) licenseContent.getExtra();
                LicenseExecutor.streamerAndRecorderSet(extra);
            } catch (Exception e) {
                logger.info("++++++++LicenseExecutor  exec  error ++++++++" + e.getMessage());
            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}

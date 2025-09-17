package com.paradisecloud.fcm.smc2.monitor;

import com.paradisecloud.com.fcm.smc.modle.SmcBridgeStatus;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2Dept;
import com.paradisecloud.fcm.smc2.cache.DeptSmc2MappingCache;
import com.paradisecloud.fcm.smc2.cache.Smc2BridgeCache;
import com.paradisecloud.fcm.smc2.cache.Smc2Bridge;
import com.suntek.smc.esdk.service.client.AuthorizeServiceEx;
import com.suntek.smc.esdk.service.client.SubscribeServiceEx;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author nj
 * @date 2023/6/28 14:11
 */
@Component
public class Smc2LoginStatusMonitor implements ApplicationRunner {

    private Logger logger= LoggerFactory.getLogger(Smc2LoginStatusMonitor.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {

       logger.info("Smc2Login-keep-task....start.........................");

        while (true){
            try {
                Map<Long, Smc2Bridge> smc2BridgeMap = Smc2BridgeCache.getInstance().getSmc2BridgeMap();
                if(smc2BridgeMap!=null){
                    for (Smc2Bridge smc2Bridge : smc2BridgeMap.values()) {
                        boolean available = smc2Bridge.isAvailable();
                        AuthorizeServiceEx authorizeService = smc2Bridge.getAuthorizeService();
                        if(!available){
                            Integer loginAg = authorizeService.login(smc2Bridge.getBusiSmc2().getUsername(), smc2Bridge.getBusiSmc2().getPassword());
                            if(loginAg==0){
                                authorizeService.keepAlive();
                                try {
                                    SubscribeServiceEx subscribeServiceEx = smc2Bridge.getSubscribeServiceEx();
                                    subscribeServiceEx.enablePushEx(1, "");
                                    smc2Bridge.setBridgeStatus(SmcBridgeStatus.AVAILABLE);
                                } catch (Exception e) {
                                    logger.error("Smc2Login-keep-task_enable_PushExerror.:"+e.getMessage());
                                }
                            }else {
                                logger.error("Smc2Login-keep-task_ ag login failed");
                            }
                        }else {
                            authorizeService.keepAlive();
                        }

                    }
                }
            } catch (Exception e) {
                logger.error("Smc2Login-keep-task_ error [Smc2LoginStatusMonitor]");
            }finally {
                Threads.sleep(20*1000);
            }

        }


    }
}

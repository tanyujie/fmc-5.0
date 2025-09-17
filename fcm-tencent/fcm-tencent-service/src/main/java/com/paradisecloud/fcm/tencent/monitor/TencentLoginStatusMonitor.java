package com.paradisecloud.fcm.tencent.monitor;


import com.paradisecloud.fcm.tencent.cache.TencentBridge;
import com.paradisecloud.fcm.tencent.cache.TencentBridgeCache;
import com.paradisecloud.fcm.tencent.model.BridgeStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author nj
 * @date 2023/6/28 14:11
 */
@Component
@Slf4j
public class TencentLoginStatusMonitor implements ApplicationRunner {


    public static final ScheduledThreadPoolExecutor scheduler= new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(Thread.currentThread().getThreadGroup(), r, "TencentBridge-keep-task");
            return thread;
        }
    });


    @Override
    public void run(ApplicationArguments args) throws Exception {

        scheduler.scheduleWithFixedDelay(()->{
            try {
                Map<Long, TencentBridge> TencentBridgeMap = TencentBridgeCache.getInstance().getTencentBridgeMap();
                if(TencentBridgeMap!=null){
                    for (TencentBridge value : TencentBridgeMap.values()) {
                        if(!value.isAvailable()){
                            value.getUsers(1);
                        }
                    }
                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        },0,30, TimeUnit.SECONDS);

    }
}

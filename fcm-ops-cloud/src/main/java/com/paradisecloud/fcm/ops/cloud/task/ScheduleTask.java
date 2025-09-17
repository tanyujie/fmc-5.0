package com.paradisecloud.fcm.ops.cloud.task;

import com.paradisecloud.fcm.dao.model.BusiClient;
import com.paradisecloud.fcm.ops.cloud.cache.ClientCache;
import com.paradisecloud.fcm.ops.cloud.interfaces.IBusiClientService;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务类
 */
@Component
public class ScheduleTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 每天2:03启动删除长时间未登录的非固定终端
     */
    @Scheduled(cron = "0 3 2 * * ?")
    public void checkCmGetChangesThread() {
        logger.info("删除长时间未登录的非固定终端定时任务启动");
        IBusiClientService busiClientService = BeanFactory.getBean(IBusiClientService.class);
        long currentTime = System.currentTimeMillis();
        for (BusiClient busiClient : ClientCache.getInstance().values()) {
            if (currentTime - busiClient.getLastOnlineTime().getTime() > 1000 * 60 * 60 * 24 * 30) {
                String sn = busiClient.getSn();
                if (sn != null && sn.length() > 20) {
                    String timeStr = sn.substring(sn.length() - 18);
                    if (NumberUtils.isParsable(timeStr)) {
                        busiClientService.deleteBusiClientById(busiClient.getId());
                    }
                }
            }
        }
    }
}

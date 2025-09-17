package com.paradisecloud.fcm.smc2.core;

import com.paradisecloud.fcm.smc2.cache.Smc2Bridge;
import com.paradisecloud.fcm.smc2.model.notice.OngoingConfNotificationExMessage;
import com.paradisecloud.fcm.smc2.model.notice.OngoingConfNotificationProcessorMessageQueue;
import com.sinhy.utils.ThreadUtils;
import com.suntek.smc.esdk.pojo.local.*;
import com.suntek.smc.esdk.service.client.SubscribeServiceEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author nj
 * @date 2023/4/26 9:51
 */

public class Smc2SubscribleTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private  SubscribeServiceEx subscribeServiceEx;

    private Smc2Bridge smc2Bridge;

    public Smc2SubscribleTask(SubscribeServiceEx subscribeServiceEx,Smc2Bridge smc2Bridge) {
        this.subscribeServiceEx = subscribeServiceEx;
        this.smc2Bridge=smc2Bridge;
    }

    @Override
    public void run() {
        logger.info("Smc2SubscribleThread start successfully!");

        while (true) {
            try {
                        //推送通知机制设为Pull
                    TPSDKResponseEx<List<NotificationEx>> result = subscribeServiceEx.queryNotificationEx();
                    if (0 == result.getResultCode()) {
                        logger.info("Smc2SubscribleThread 查询成功，返回通知消息");
                        //查询成功，返回通知消息
                        List<NotificationEx> notificationExs = result.getResult();
                        if (!CollectionUtils.isEmpty(notificationExs)) {
                            for (NotificationEx notificationEx : notificationExs) {
                                if (notificationEx instanceof OverflowNotificationEx) {
                                    logger.error("通知队列溢出");
                                }
                                if (notificationEx instanceof ScheduleConfNotificationEx) {
                                    logger.info("已预约会议事件通知");
                                }
                                if (notificationEx instanceof OngoingConfNotificationEx) {
                                    logger.info("活动会议事件通知");
                                    OngoingConfNotificationEx ongoingConfNotificationEx = (OngoingConfNotificationEx) notificationEx;
                                    OngoingConfNotificationProcessorMessageQueue.getInstance().put(new OngoingConfNotificationExMessage(ongoingConfNotificationEx,smc2Bridge));
                                }
                            }
                        }

                    }


            } catch (Exception e) {
                logger.error("ConferenceMonitoringThread error", e);
            } finally {
                ThreadUtils.sleep(1000);
            }

        }
    }

}

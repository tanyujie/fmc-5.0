package com.paradisecloud.fcm.ding.event;

import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.app.api.GenericEventListener;
import com.dingtalk.open.app.api.OpenDingTalkStreamClientBuilder;
import com.dingtalk.open.app.api.message.GenericOpenDingTalkEvent;
import com.dingtalk.open.app.api.security.AuthClientCredential;
import com.dingtalk.open.app.stream.protocol.event.EventAckStatus;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.ding.cache.DingBridge;
import com.paradisecloud.fcm.ding.cache.DingBridgeCache;
import com.paradisecloud.fcm.ding.core.DingDingModuleInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collection;

/**
 * @author nj
 * @date 2024/2/20 9:49
 */
//@Component
public class OpenDingTalkStream extends Thread implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DingDingModuleInitializer.class);

    @Override
    public void run() {

        while (true) {
            Collection<DingBridge> values = DingBridgeCache.getInstance().getDingBridgeMap().values();
            for (DingBridge value : values) {
                if (value.isAvailable() && !value.getStream()) {
                    try {
                        OpenDingTalkStreamClientBuilder
                                .custom()
                                .credential(new AuthClientCredential(value.getBusiDing().getSecretId(), value.getBusiDing().getSecretKey()))
                                //注册事件监听
                                .registerAllEventListener(new GenericEventListener() {
                                    @Override
                                    public EventAckStatus onEvent(GenericOpenDingTalkEvent event) {
                                        try {
                                            //事件唯一Id
                                            String eventId = event.getEventId();
                                            //事件类型
                                            String eventType = event.getEventType();
                                            //事件产生时间
                                            Long bornTime = event.getEventBornTime();
                                            //获取事件体
                                            JSONObject bizData = event.getData();
                                            //处理事件
                                            DingMeetingProcessorMessageQueue.getInstance().put(new DingMeetingMessage(bizData, eventId + bornTime));
                                            //消费成功
                                            return EventAckStatus.SUCCESS;
                                        } catch (Exception e) {
                                            //消费失败
                                            return EventAckStatus.LATER;
                                        }
                                    }
                                })
                                .build().start();
                        value.setStream(true);
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                }
            }
            Threads.sleep(1000);
        }


    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}

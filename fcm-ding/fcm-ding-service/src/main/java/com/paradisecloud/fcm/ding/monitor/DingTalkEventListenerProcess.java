package com.paradisecloud.fcm.ding.monitor;

import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.app.api.GenericEventListener;
import com.dingtalk.open.app.api.OpenDingTalkClient;
import com.dingtalk.open.app.api.OpenDingTalkStreamClientBuilder;
import com.dingtalk.open.app.api.message.GenericOpenDingTalkEvent;
import com.dingtalk.open.app.api.security.AuthClientCredential;
import com.dingtalk.open.app.stream.protocol.event.EventAckStatus;
import com.paradisecloud.fcm.ding.event.DingMeetingMessage;
import com.paradisecloud.fcm.ding.event.DingMeetingProcessorMessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;


/**
 * @author nj
 * @date 2024/2/21 11:19
 */
//@Component
public class DingTalkEventListenerProcess extends Thread implements InitializingBean {

    private Logger LOGGER= LoggerFactory.getLogger(getClass());

    private OpenDingTalkClient clientBuilder;

    @Override
    public void run() {
        try {
            OpenDingTalkClient openDingTalkClient = OpenDingTalkStreamClientBuilder
                    .custom()
                    .credential(new AuthClientCredential("dingmtbh4rmay5w8g2ry", "7R_CtIOTmuyk_R9RAWNC7w86Xa9tv8ZHJRDvtZwK98j86jdY2hkNMJx5uxvHRpFZ"))
                    .registerAllEventListener(new GenericEventListener() {
                        @Override
                        public EventAckStatus onEvent(GenericOpenDingTalkEvent event) {
                            try {
                                LOGGER.info("receive eventMessage......................");
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
                    }).build();
            openDingTalkClient.start();
        } catch (Exception e) {

        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }




}

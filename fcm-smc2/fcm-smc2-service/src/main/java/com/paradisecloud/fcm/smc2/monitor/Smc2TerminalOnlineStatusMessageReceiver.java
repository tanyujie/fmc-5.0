/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebsocketMessageThreadPoolExecutor.java
 * Package     : com.paradisecloud.fcm.service.websocket
 * @author lilinhai
 * @since 2021-02-04 16:41
 * @version  V1.0
 */
package com.paradisecloud.fcm.smc2.monitor;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.message.terminal.Smc2TerminalOnlineStatusMessageQueue;
import com.paradisecloud.fcm.common.message.terminal.TerminalOnlineStatusMessage;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.cache.Smc2WebsocketMessageType;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeCountingStatistics;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.sinhy.model.AsyncBlockingMessageProcessor;
import com.sinhy.utils.ReflectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <pre>终端在线状态消息接收器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-04 16:41
 */
@Component
public class Smc2TerminalOnlineStatusMessageReceiver extends AsyncBlockingMessageProcessor<TerminalOnlineStatusMessage> implements InitializingBean {

    /**
     *
     */
    public Smc2TerminalOnlineStatusMessageReceiver() {
        super("Smc2TerminalOnlineStatusMessageReceiver", (Smc2TerminalOnlineStatusMessageQueue) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(Smc2TerminalOnlineStatusMessageQueue.class, "getInstance"), null));
    }

    @Override
    public void process(TerminalOnlineStatusMessage message) {
        for (Iterator<Smc2ConferenceContext> iterator = Smc2ConferenceContextCache.getInstance().values().iterator(); iterator.hasNext(); ) {
            Smc2ConferenceContext conferenceContext = iterator.next();

            try {
                if (conferenceContext!=null&&!conferenceContext.isEnd()) {
                    AttendeeSmc2 attendee = conferenceContext.getAttendeeByTerminalId(message.getTerminalId());
                    if(attendee!=null){
                        attendee.setOnlineStatus(message.getOnlineStatus().getValue());
                        if(attendee.getUpdateMap().size()>1){

                            // 在线消息
                            if (attendee.containsUpdateField("onlineStatus")) {
                                TerminalOnlineStatus onlineStatus = TerminalOnlineStatus.convert((int) attendee.getUpdateMap().get("onlineStatus"));
                                StringBuilder messageTip = new StringBuilder();
                                messageTip.append("【").append(attendee.getName()).append("】").append(onlineStatus.getName());

                                // 消息和参会者信息同步到主级会议
                                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                                Map<String, Object> updateMap = new HashMap<>(attendee.getUpdateMap());
                                updateMap.put("ip", attendee.getIp());
                                updateMap.put("ipNew", attendee.getIpNew());
                                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, updateMap);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);

                            }

                        }

                    }

                }
            } catch (Throwable e) {
                logger.error("终端状态处理出错: " + conferenceContext.getCoSpaceId() + ", message: " + message, e);
            }

        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}

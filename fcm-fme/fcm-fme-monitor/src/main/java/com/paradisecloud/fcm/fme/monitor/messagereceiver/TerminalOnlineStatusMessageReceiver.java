/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebsocketMessageThreadPoolExecutor.java
 * Package     : com.paradisecloud.fcm.service.websocket
 * @author lilinhai 
 * @since 2021-02-04 16:41
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.monitor.messagereceiver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.model.busi.attendee.AttendeeCountingStatistics;
import com.sinhy.spring.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.message.terminal.TerminalOnlineStatusMessage;
import com.paradisecloud.fcm.common.message.terminal.TerminalOnlineStatusMessageQueue;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.AttendeeMessageQueue;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.CallFailedAttendeeMessage;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.fme.model.response.participant.ParticipantInfoResponse;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.model.AsyncBlockingMessageProcessor;
import com.sinhy.utils.ReflectionUtils;

/**  
 * <pre>终端在线状态消息接收器</pre>
 * @author lilinhai
 * @since 2021-02-04 16:41
 * @version V1.0  
 */
@Component
public class TerminalOnlineStatusMessageReceiver extends AsyncBlockingMessageProcessor<TerminalOnlineStatusMessage> implements InitializingBean
{
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 17:00
     */
    public TerminalOnlineStatusMessageReceiver()
    {
        super("TerminalOnlineStatusMessageReceiver", (TerminalOnlineStatusMessageQueue)ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(TerminalOnlineStatusMessageQueue.class, "getInstance"), null));
    }

    @Override
    public void process(TerminalOnlineStatusMessage message)
    {
        for (Iterator<ConferenceContext> iterator = ConferenceContextCache.getInstance().values().iterator(); iterator.hasNext();)
        {
            ConferenceContext conferenceContext = iterator.next();
            try
            {
                if (!conferenceContext.isEnd())
                {
                    TerminalAttendee terminalAttendee = conferenceContext.getTerminalAttendeeMap().get(message.getTerminalId());
                    
                    // 只能同步部门ID相等的会议上下文
                    if (terminalAttendee != null && terminalAttendee.getDeptId() == conferenceContext.getDeptId().longValue())
                    {
                        synchronized (terminalAttendee)
                        {
                            terminalAttendee.resetUpdateMap();
                            
                            // 同步终端在线状态
                            terminalAttendee.setOnlineStatus(message.getOnlineStatus().getValue());
                            
                            logger.info("------------收到终端在线状态变更消息: {}, {}", terminalAttendee);
                            
                            // 在线消息
                            if (terminalAttendee.containsUpdateField("onlineStatus"))
                            {
                                TerminalOnlineStatus onlineStatus = TerminalOnlineStatus.convert((int) terminalAttendee.getUpdateMap().get("onlineStatus"));
                                StringBuilder messageTip = new StringBuilder();
                                messageTip.append("【").append(terminalAttendee.getName()).append("】").append(onlineStatus.getName());
                                if (onlineStatus == TerminalOnlineStatus.OFFLINE)
                                {
                                    FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceNumber(conferenceContext.getDeptId(), conferenceContext.getConferenceNumber(), false);
                                    if (fmeBridge != null)
                                    {
                                        ParticipantInfoResponse participantInfoResponse = null;
                                        try
                                        {
                                            participantInfoResponse = fmeBridge.getParticipantInvoker().getParticipant(terminalAttendee.getParticipantUuid());
                                        }
                                        catch (Exception e)
                                        {
                                        }
                                        if (participantInfoResponse != null && participantInfoResponse.getParticipant() != null)
                                        {
                                            terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(message.getTerminalId());
                                            if (busiTerminal.getOnlineStatus() == TerminalOnlineStatus.OFFLINE.getValue()) {
                                                busiTerminal.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                                BusiTerminalMapper busiTerminalMapper = BeanFactory.getBean(BusiTerminalMapper.class);
                                                busiTerminalMapper.updateBusiTerminal(busiTerminal);
                                            }
                                        }
                                        else
                                        {
                                            // 消息和参会者信息同步到主级会议
                                            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
                                            Map<String, Object> updateMap = new HashMap<>(terminalAttendee.getUpdateMap());
                                            updateMap.put("ip", terminalAttendee.getIp());
                                            updateMap.put("ipNew", terminalAttendee.getIpNew());
                                            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, updateMap);
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                                            if (!terminalAttendee.isHangUp())
                                            {
                                                AttendeeMessageQueue.getInstance().put(new CallFailedAttendeeMessage(terminalAttendee));
                                            }
                                        }
                                    }
                                    else
                                    {
                                        // 消息和参会者信息同步到主级会议
                                        WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
                                        Map<String, Object> updateMap = new HashMap<>(terminalAttendee.getUpdateMap());
                                        updateMap.put("ip", terminalAttendee.getIp());
                                        updateMap.put("ipNew", terminalAttendee.getIpNew());
                                        WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, updateMap);
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                                        /*                                        if (!terminalAttendee.isHangUp())
                                        {
                                            AttendeeMessageQueue.getInstance().put(new CallFailedAttendeeMessage(terminalAttendee));
                                        }*/
                                    }
                                }
                                else
                                {
                                    // 消息和参会者信息同步到主级会议
                                    WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                                    Map<String, Object> updateMap = new HashMap<>(terminalAttendee.getUpdateMap());
                                    updateMap.put("ip", terminalAttendee.getIp());
                                    updateMap.put("ipNew", terminalAttendee.getIpNew());
                                    WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, updateMap);
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                                }
                            }
                        }
                    }
                }
            }
            catch (Throwable e)
            {
                logger.error("终端状态处理出错: " + conferenceContext.getConferenceNumber() + ", message: " + message, e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}

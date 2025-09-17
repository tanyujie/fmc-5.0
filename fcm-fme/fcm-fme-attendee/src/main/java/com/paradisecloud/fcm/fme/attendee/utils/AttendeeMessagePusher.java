/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeMessagePusher.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author lilinhai 
 * @since 2021-03-04 17:37
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.utils;

import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;

/**  
 * <pre>参会者消息推送器</pre>
 * @author lilinhai
 * @since 2021-03-04 17:37
 * @version V1.0  
 */
public class AttendeeMessagePusher
{
    
    private static final AttendeeMessagePusher INSTANCE = new AttendeeMessagePusher();
    
    private AttendeeMessagePusher()
    {
        
    }
    
    /**
     * 推送入会消息
     * @author lilinhai
     * @param participant 
     * @since 2021-03-04 17:40  void
     */
    public void pushMeetingJoinedMessage(Attendee attendee, Participant participant, ConferenceContext conferenceContext)
    {
        if (attendee.containsUpdateField("meetingStatus"))
        {
            AttendeeMeetingStatus meetingStatus = AttendeeMeetingStatus.convert((int) attendee.getUpdateMap().get("meetingStatus"));
            StringBuilder messageTip = new StringBuilder();
            messageTip.append("【").append(attendee.getName()).append("】").append(meetingStatus.getName());
            
            if (meetingStatus == AttendeeMeetingStatus.OUT)
            {
                if (participant.getRosterUpdate() != null && !ObjectUtils.isEmpty(participant.getRosterUpdate().getString("reason")))
                {
                    messageTip.append("：").append(participant.getRosterUpdate().getString("reason"));
                }
            }
            else
            {
                // 入会，标记锁定状态
                Call call = FmeDataCache.getCallByUuid(participant.getCall());
                if (call.getLocked() != null && call.getLocked())
                {
                    if (participant.getCallLeg().getStatus() == null || participant.getCallLeg().getStatus().getDurationSeconds() <= 2)
                    {
                        attendee.setLocked(true);
                    }
                }
            }
            
            // 消息和参会者信息同步到主级会议
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        }
    }
    
    /**
     * 推送离会消息
     * @author lilinhai
     * @since 2021-03-04 17:40  void
     */
    public void pushMeetingLeftMessage(Attendee attendee, ConferenceContext conferenceContext)
    {
        
    }
    
    /**
     * 推送混音消息
     * @author lilinhai
     * @since 2021-03-04 17:40  void
     */
    public void pushMixingStatusMessage(Attendee attendee, ConferenceContext conferenceContext)
    {
        // 混音消息
        if (attendee.containsUpdateField("mixingStatus"))
        {
            AttendeeMixingStatus onlineStatus = AttendeeMixingStatus.convert((int) attendee.getUpdateMap().get("mixingStatus"));
            StringBuilder messageTip = new StringBuilder();
            messageTip.append("【").append(attendee.getName()).append("】").append(onlineStatus.getName());
            
            // 消息和参会者信息同步到主级会议
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        }
    }
    
    
    /**
     * 推送在线消息
     * @author lilinhai
     * @since 2021-03-04 17:40  void
     */
    public void pushOnlineMessage(Attendee attendee, ConferenceContext conferenceContext)
    {
        // 在线消息
        if (attendee.containsUpdateField("onlineStatus"))
        {
            TerminalOnlineStatus onlineStatus = TerminalOnlineStatus.convert((int) attendee.getUpdateMap().get("onlineStatus"));
            StringBuilder messageTip = new StringBuilder();
            messageTip.append("【").append(attendee.getName()).append("】").append(onlineStatus.getName());
            if (onlineStatus == TerminalOnlineStatus.OFFLINE)
            {
                // 消息和参会者信息同步到主级会议
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
            }
            else
            {
                // 消息和参会者信息同步到主级会议
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
            }
        }
    }

    /**
     * <p>Get Method   :   INSTANCE AttendeeMessagePusher</p>
     * @return instance
     */
    public static AttendeeMessagePusher getInstance()
    {
        return INSTANCE;
    }
}

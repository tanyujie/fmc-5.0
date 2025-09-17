/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeMessagePusher.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author lilinhai 
 * @since 2021-02-04 17:27
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smc2.conference.updateprocess;

import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;

/**  
 * <pre>参会者消息推送器</pre>
 * @author lilinhai
 * @since 2021-02-04 17:27
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
     * @since 2021-02-04 17:40  void
     */
    public void pushMeetingJoinedMessage(AttendeeSmc2 attendee, SmcParitipantsStateRep.ContentDTO participant, Smc2ConferenceContext conferenceContext)
    {
        if (attendee.containsUpdateField("meetingStatus"))
        {
            AttendeeMeetingStatus meetingStatus = AttendeeMeetingStatus.convert((int) attendee.getUpdateMap().get("meetingStatus"));
            StringBuilder messageTip = new StringBuilder();
            messageTip.append("【").append(attendee.getName()).append("】").append(meetingStatus.getName());
            
            if (meetingStatus == AttendeeMeetingStatus.IN)
            {
                // 入会，标记锁定状态
                if (conferenceContext.isLocked())
                {
                    attendee.setLocked(true);
                }
            }
            // 消息和参会者信息同步到主级会议
            Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        }
    }
    

    
    /**
     * 推送混音消息
     * @author lilinhai
     * @since 2021-02-04 17:40  void
     */
    public void pushMixingStatusMessage(AttendeeSmc2 attendee, Smc2ConferenceContext conferenceContext)
    {
        // 混音消息
        if (attendee.containsUpdateField("mixingStatus"))
        {
            AttendeeMixingStatus onlineStatus = AttendeeMixingStatus.convert((int) attendee.getUpdateMap().get("mixingStatus"));
            StringBuilder messageTip = new StringBuilder();
            messageTip.append("【").append(attendee.getName()).append("】").append(onlineStatus.getName());
            // 消息和参会者信息同步到主级会议
            Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        }
    }
    
    
    /**
     * 推送在线消息
     * @author lilinhai
     * @since 2021-02-04 17:40  void
     */
    public void pushOnlineMessage(AttendeeSmc2 attendee, Smc2ConferenceContext conferenceContext)
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
                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
            }
            else
            {
                // 消息和参会者信息同步到主级会议
                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
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

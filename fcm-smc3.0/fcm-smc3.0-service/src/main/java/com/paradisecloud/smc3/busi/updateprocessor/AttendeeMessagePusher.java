/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeMessagePusher.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author lilinhai 
 * @since 2021-03-04 17:37
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.updateprocessor;

import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;

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
    public void pushMeetingJoinedMessage(AttendeeSmc3 attendee, SmcParitipantsStateRep.ContentDTO participant, Smc3ConferenceContext conferenceContext)
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
            Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        }
    }
    

    
    /**
     * 推送混音消息
     * @author lilinhai
     * @since 2021-03-04 17:40  void
     */
    public void pushMixingStatusMessage(AttendeeSmc3 attendee, Smc3ConferenceContext conferenceContext)
    {
        // 混音消息
        if (attendee.containsUpdateField("mixingStatus"))
        {
            AttendeeMixingStatus onlineStatus = AttendeeMixingStatus.convert((int) attendee.getUpdateMap().get("mixingStatus"));
            StringBuilder messageTip = new StringBuilder();
            messageTip.append("【").append(attendee.getName()).append("】").append(onlineStatus.getName());
            // 消息和参会者信息同步到主级会议
            Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        }
    }
    
    
    /**
     * 推送在线消息
     * @author lilinhai
     * @since 2021-03-04 17:40  void
     */
    public void pushOnlineMessage(AttendeeSmc3 attendee, Smc3ConferenceContext conferenceContext)
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
                Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
            }
            else
            {
                // 消息和参会者信息同步到主级会议
                Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
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

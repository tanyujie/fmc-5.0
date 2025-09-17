/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : OtherAttendeeUpdateProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:12
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.tencent.templateConference.updateprocess;

import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentWebSocketMessagePusher;
import com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.tencent.model.reponse.RealTimeParticipantsResponse;
import com.sinhy.spring.BeanFactory;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**  
 * <pre>其它参会者更新处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:12
 * @version V1.0  
 */
public class OtherAttendeeUpdateProcessor extends AttendeeUpdateProcessor
{

    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-19 15:16 
     * @param participant
     * @param a
     * @param conferenceContext
     */
    public OtherAttendeeUpdateProcessor(SmcParitipantsStateRep.ContentDTO participant, AttendeeTencent a, TencentConferenceContext conferenceContext)
    {
        super(participant, a, conferenceContext);
    }

    @Override
    public void process()
    {
        attendee.setParticipantUuid(participant.getGeneralParam().getId());
        attendee.setSmcParticipant(participant);
        if (participant.getState().getOnline() != null) {
            if (!participant.getState().getOnline() && participant.getState().getCallFailReason() != 0) {

            } else {

               if(!attendee.isMeetingJoined()){
                   attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                }

                conferenceContext.getParticipantAttendeeAllMap().put(attendee.getParticipantUuid(), attendee);
                attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                attendee.setCallRequestSentTime(null);
                AttendeeMessagePusher.getInstance().pushMixingStatusMessage(attendee, conferenceContext);
                AttendeeMessagePusher.getInstance().pushMeetingJoinedMessage(attendee, participant, conferenceContext);
                AttendeeMessagePusher.getInstance().pushOnlineMessage(attendee, conferenceContext);
            }
        }
        BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
    }
    

    
}

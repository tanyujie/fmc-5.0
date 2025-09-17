/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : OtherAttendeeUpdateProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:12
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smc2.conference.updateprocess;

import com.paradisecloud.com.fcm.smc.modle.ConstAPI;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.SelfCallAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.CallTheRollAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.operation.ChooseToSeeAttendeeOperation;
import com.sinhy.spring.BeanFactory;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;


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
    public OtherAttendeeUpdateProcessor(SmcParitipantsStateRep.ContentDTO participant, AttendeeSmc2 a, Smc2ConferenceContext conferenceContext)
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
                if ((participant.getChangeType()!=null&&participant.getChangeType() == ConstAPI.DELETE_TYPE )) {
                    attendee.leaveMeeting();
                    // 从缓存中移除
                    conferenceContext.removeAttendeeById(attendee.getId());
                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendee.getId());
                    updateMap.put("deptId", attendee.getDeptId());
                    updateMap.put("mcuAttendee", attendee.isMcuAttendee());
                    Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    String reason = "【" + attendee.getName() + "】离会";
                    Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                    if (attendee == conferenceContext.getMasterAttendee()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("oldMasterAttendee", attendee);
                        data.put("newMasterAttendee", null);
                        Smc2WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);

                        StringBuilder messageTip = new StringBuilder();
                        messageTip.append("主会场已离会【").append(attendee.getName()).append("】");
                        Smc2WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                        conferenceContext.clearMasterAttendee();
                    }
                } else {
                    attendee.hangup();
                }

            } else {

               if(!attendee.isMeetingJoined()){
                   attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                   if(conferenceContext.getMasterAttendee()!=null){
                       if(Objects.equals(attendee.getId(),conferenceContext.getMasterAttendee().getId())){
                           AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                           if(attendeeOperation instanceof ChangeMasterAttendeeOperation){
                               ChangeMasterAttendeeOperation changeMasterAttendeeOperation = new ChangeMasterAttendeeOperation(conferenceContext, conferenceContext.getMasterAttendee());
                               changeMasterAttendeeOperation.operate();
                           } else  if(attendeeOperation instanceof ChooseToSeeAttendeeOperation||attendeeOperation instanceof CallTheRollAttendeeOperation){
                               ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                               conferenceServiceEx.requestConfChairEx(conferenceContext.getSmc2conferenceId(), conferenceContext.getMasterAttendee().getRemoteParty());
                               attendeeOperation.operate();
                           }
                       }
                   }
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

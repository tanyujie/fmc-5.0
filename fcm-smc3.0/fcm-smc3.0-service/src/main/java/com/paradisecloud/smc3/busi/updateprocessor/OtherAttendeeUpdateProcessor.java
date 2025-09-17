/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : OtherAttendeeUpdateProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai
 * @since 2021-02-19 15:13
 * @version  V1.0
 */
package com.paradisecloud.smc3.busi.updateprocessor;

import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.MinutesAttendeeSmc3;
import com.paradisecloud.smc3.busi.operation.AttendeeOperation;
import com.paradisecloud.smc3.busi.operation.TalkPrivateAttendeeOperation;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import com.paradisecloud.smc3.monitor.ConferenceSmc3AttendeeOperationThread;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <pre>其它参会者更新处理器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-19 15:13
 */
public class OtherAttendeeUpdateProcessor extends AttendeeUpdateProcessor {


    /**
     * <pre>构造方法</pre>
     *
     * @param participant
     * @param a
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-19 15:16
     */
    public OtherAttendeeUpdateProcessor(SmcParitipantsStateRep.ContentDTO participant, AttendeeSmc3 a, Smc3ConferenceContext conferenceContext) {
        super(participant, a, conferenceContext);
    }

    @Override
    public void process() {
        attendee.setParticipantUuid(participant.getGeneralParam().getId());
        attendee.setSmcParticipant(participant);
        if (participant.getState().getOnline() != null) {
            if (!participant.getState().getOnline() && participant.getState().getCallFailReason() != 0) {
              //  if (participant.getChangeType() == ConstAPI.DELETE_TYPE || (attendee instanceof SelfCallAttendeeSmc3 && participant.getState().getCallFailReason() != 0)) {
                if (participant.getChangeType() == ConstAPI.DELETE_TYPE ) {
                    if (attendee instanceof MinutesAttendeeSmc3) {
                        MinutesAttendeeSmc3 minutesAttendee = (MinutesAttendeeSmc3) attendee;
                        if (StringUtils.hasText(minutesAttendee.getRemoteParty())) {
                            if (conferenceContext.isMinutes())
                            {
                                conferenceContext.setMinutes(false);
                                conferenceContext.setMinutesAttendee(null);
                                // 向所有客户端通知会议的录制状态
                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MINUTES, false);
                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭纪要");
                                return;
                            }
                        }
                    }
                    attendee.leaveMeeting();
                    // 从缓存中移除
                    conferenceContext.removeAttendeeById(attendee.getId());
                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendee.getId());
                    updateMap.put("deptId", attendee.getDeptId());
                    updateMap.put("mcuAttendee", attendee.isMcuAttendee());
                    Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    String reason = "【" + attendee.getName() + "】离会";
                    Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                    if (attendee == conferenceContext.getMasterAttendee()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("oldMasterAttendee", attendee);
                        data.put("newMasterAttendee", null);
                        Smc3WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);

                        StringBuilder messageTip = new StringBuilder();
                        messageTip.append("主会场已离会【").append(attendee.getName()).append("】");
                        Smc3WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                        conferenceContext.clearMasterAttendee();
                    }

                } else {
                    attendee.hangup();
                }

            } else {
                if (attendee instanceof MinutesAttendeeSmc3) {
                    MinutesAttendeeSmc3 minutesAttendee = (MinutesAttendeeSmc3) attendee;
                    if (StringUtils.hasText(minutesAttendee.getRemoteParty())) {
                        if (!conferenceContext.isMinutes())
                        {
                            conferenceContext.setMinutes(true);
                            conferenceContext.setMinutesAttendee(attendee);

                            // 向所有客户端通知会议的录制状态
                            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MINUTES, true);
                            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启纪要");

                            attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                            return;
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

                AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                if(attendeeOperation!=null){
                    if(attendeeOperation instanceof TalkPrivateAttendeeOperation){
                        ConferenceSmc3AttendeeOperationThread.add(attendee);
                    }
                }
            }
        }

        //BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
    }


}

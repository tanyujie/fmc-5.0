/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUpdateProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai
 * @since 2021-02-19 15:05
 * @version  V1.0
 */
package com.paradisecloud.smc3.busi.updateprocessor;

import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.ViewTemplateConference;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.model.busi.attendee.McuAttendee;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.McuAttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.TerminalAttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.UpMcuAttendeeSmc3;
import com.paradisecloud.smc3.busi.operation.AttendeeOperation;
import com.paradisecloud.smc3.busi.operation.TalkPrivateAttendeeOperation;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import com.paradisecloud.smc3.monitor.ConferenceSmc3AttendeeOperationThread;
import com.sinhy.spring.BeanFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>参会者更新处理器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-19 15:05
 */
public class RegisteredAttendeeUpdateProcessor extends AttendeeUpdateProcessor {

    /**
     * <pre>构造方法</pre>
     *
     * @param participant
     * @param a
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-19 15:11
     */
    public RegisteredAttendeeUpdateProcessor(SmcParitipantsStateRep.ContentDTO participant, AttendeeSmc3 a, Smc3ConferenceContext conferenceContext) {
        super(participant, a, conferenceContext);
    }

    @Override
    public void process() {
        attendee.setSmcParticipant(participant);
        attendee.setParticipantUuid(participant.getGeneralParam().getId());
        if(participant.getState().getOnline()!=null){
            if (participant.getState().getOnline()) {

                int meetingStatus = attendee.getMeetingStatus();
                if (meetingStatus == AttendeeMeetingStatus.OUT.getValue()) {
                    attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                    // 呼入成功后，重置主动挂断
                    attendee.setHangUp(false);
                    attendee.setCallRequestSentTime(null);
                    attendee.setParticipantUuid(participant.getGeneralParam().getId());
                    attendee.setOnlineStatus(AttendeeMeetingStatus.IN.getValue());
                    conferenceContext.getParticipantAttendeeAllMap().put(participant.getGeneralParam().getId(),attendee);
                }
                AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                if(attendeeOperation!=null){
                    if(attendeeOperation instanceof TalkPrivateAttendeeOperation){
                        ConferenceSmc3AttendeeOperationThread.add(attendee);
                    }
                }
            } else {
                if(participant.getState().getCallFailReason()!=0){
                    int meetingStatus = attendee.getMeetingStatus();

                    if (meetingStatus == AttendeeMeetingStatus.IN.getValue()) {
                        if(participant.getChangeType()== ConstAPI.DELETE_TYPE){
                            attendee.leaveMeeting();
                        }
                        attendee.hangup();
                    }
                }
            }
        }

        // 已配置的终端与会者更新
        if (attendee instanceof TerminalAttendeeSmc3) {
            processTerminalAttendeeUpdate();
        }
        // FME类型的与会者
        else if (attendee instanceof McuAttendeeSmc3) {
            processMcuAttendee();
        }
    }



    /**
     * <pre>处理上级级联FME参会</pre>
     *
     * @author Administrator
     * @since 2021-03-06 23:16  void
     */
    private void processMcuAttendee() {
        McuAttendeeSmc3 mcuAttendee = (McuAttendeeSmc3) attendee;

        String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendee.getId());
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext != null)
        {
            String remoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
            if (baseConferenceContext.getMcuCallPort() != null && baseConferenceContext.getMcuCallPort() != 5060) {
                remoteParty += ":" + baseConferenceContext.getMcuCallPort();
            }
            mcuAttendee.setRemoteParty(remoteParty);
            mcuAttendee.setIp(baseConferenceContext.getMcuCallIp());
            mcuAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
        }
        else
        {
            mcuAttendee.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
        }
        if (baseConferenceContext == null) {
            ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
            ViewTemplateConference viewTemplateConferenceConCascade = new ViewTemplateConference();
            viewTemplateConferenceConCascade.setId(mcuAttendee.getCascadeTemplateId());
            viewTemplateConferenceConCascade.setMcuType(mcuAttendee.getCascadeMcuType());
            viewTemplateConferenceConCascade.setUpCascadeId(conferenceContext.getTemplateConferenceId());
            viewTemplateConferenceConCascade.setUpCascadeMcuType(conferenceContext.getMcuType());
            List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceConCascade);
            if (viewTemplateConferenceList == null || viewTemplateConferenceList.size() == 0) {
                conferenceContext.removeMcuAttendee(mcuAttendee);
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("id", mcuAttendee.getId());
                updateMap.put("deptId", mcuAttendee.getDeptId());
                updateMap.put("mcuAttendee", mcuAttendee.isMcuAttendee());
               Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                return;
            }
        }

        // 消息推送
        AttendeeMessagePusher.getInstance().pushMixingStatusMessage(attendee, conferenceContext);
        AttendeeMessagePusher.getInstance().pushMeetingJoinedMessage(attendee, participant, conferenceContext);
        AttendeeMessagePusher.getInstance().pushOnlineMessage(attendee, conferenceContext);

    }

    /**
     * <pre> 已配置的终端与会者更新</pre>
     *
     * @author lilinhai
     * @since 2021-03-03 15:51  void
     */
    private void processTerminalAttendeeUpdate() {
        TerminalAttendeeSmc3 terminalAttendee = (TerminalAttendeeSmc3) attendee;
        terminalAttendee.setParticipantUuid(participant.getGeneralParam().getId());
        BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
        if(bt==null){
            BusiTerminalMapper terminalMapper = BeanFactory.getBean(BusiTerminalMapper.class);
            bt = terminalMapper.selectBusiTerminalById(terminalAttendee.getTerminalId());
        }
        if(bt==null){
            return;
        }
        terminalAttendee.setTerminalType(bt.getType());
        terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
        if (attendee.isMeetingJoined()) {
            terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
        } else {
            terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
            terminalAttendee.setName(bt.getName());
        }
        //BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
        // 消息推送
        AttendeeMessagePusher.getInstance().pushMixingStatusMessage(attendee, conferenceContext);
        AttendeeMessagePusher.getInstance().pushMeetingJoinedMessage(attendee, participant, conferenceContext);
        AttendeeMessagePusher.getInstance().pushOnlineMessage(attendee, conferenceContext);

        conferenceContext.getParticipantAttendeeAllMap().put(participant.getGeneralParam().getId(),terminalAttendee);
    }
}

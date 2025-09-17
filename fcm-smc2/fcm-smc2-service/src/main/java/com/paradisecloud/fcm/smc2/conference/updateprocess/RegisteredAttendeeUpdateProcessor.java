/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUpdateProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai
 * @since 2021-02-19 15:05
 * @version  V1.0
 */
package com.paradisecloud.fcm.smc2.conference.updateprocess;

import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.ViewTemplateConference;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.McuAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.TerminalAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.CallTheRollAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.operation.ChooseToSeeAttendeeOperation;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.spring.BeanFactory;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


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
    public RegisteredAttendeeUpdateProcessor(SmcParitipantsStateRep.ContentDTO participant, AttendeeSmc2 a, Smc2ConferenceContext conferenceContext) {
        super(participant, a, conferenceContext);
    }

    @Override
    public void process() {


        if (participant.getState().getOnline()) {
            int onlineStatus = attendee.getOnlineStatus();
            if(onlineStatus== TerminalOnlineStatus.OFFLINE.getValue()){
                attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
            }

            int meetingStatus = attendee.getMeetingStatus();
            if (meetingStatus == AttendeeMeetingStatus.OUT.getValue()) {
                attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                // 呼入成功后，重置主动挂断
                attendee.setHangUp(false);
                attendee.setCallRequestSentTime(null);
                attendee.setParticipantUuid(participant.getGeneralParam().getId());


                if(conferenceContext.getMasterAttendee()!=null){
                    if(Objects.equals(attendee.getId(),conferenceContext.getMasterAttendee().getId())){
                        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                        if(attendeeOperation instanceof ChangeMasterAttendeeOperation){
                            ChangeMasterAttendeeOperation changeMasterAttendeeOperation = new ChangeMasterAttendeeOperation(conferenceContext, conferenceContext.getMasterAttendee());
                            changeMasterAttendeeOperation.operate();
                        }
                        else  if(attendeeOperation instanceof ChooseToSeeAttendeeOperation||attendeeOperation instanceof CallTheRollAttendeeOperation){
                            ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                            conferenceServiceEx.requestConfChairEx(conferenceContext.getSmc2conferenceId(), conferenceContext.getMasterAttendee().getRemoteParty());
                            attendeeOperation.operate();
                        }
                    }
                }
            }



        } else {

            int meetingStatus = attendee.getMeetingStatus();
            if (meetingStatus == AttendeeMeetingStatus.IN.getValue()) {
                attendee.leaveMeeting();
            }

        }

        // 已配置的终端与会者更新
        if (attendee instanceof TerminalAttendeeSmc2) {
            processTerminalAttendeeUpdate();
        }
        // FME类型的与会者
        else if (attendee instanceof McuAttendeeSmc2) {
            processFmeAttendee();
        }
    }

    /**
     * <pre>处理FME参会者</pre>
     *
     * @author Administrator
     * @since 2021-02-06 22:16  void
     */
    private void processFmeAttendee() {
        if (attendee instanceof McuAttendeeSmc2) {
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
        McuAttendeeSmc2 mcuAttendee = (McuAttendeeSmc2) attendee;

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
                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
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
     * @since 2021-02-02 15:51  void
     */
    private void processTerminalAttendeeUpdate() {
        TerminalAttendeeSmc2 terminalAttendee = (TerminalAttendeeSmc2) attendee;
        BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
        terminalAttendee.setTerminalType(bt.getType());
        terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
        if (attendee.isMeetingJoined()) {
            terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
        } else {
            terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
            terminalAttendee.setName(bt.getName());
        }
        // 消息推送
        AttendeeMessagePusher.getInstance().pushMixingStatusMessage(attendee, conferenceContext);
        AttendeeMessagePusher.getInstance().pushMeetingJoinedMessage(attendee, participant, conferenceContext);
        AttendeeMessagePusher.getInstance().pushOnlineMessage(attendee, conferenceContext);

        conferenceContext.getParticipantAttendeeAllMap().put(participant.getGeneralParam().getId(),terminalAttendee);
        BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
    }
}

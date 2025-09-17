/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallTheRollOperation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-20 16:42
 * @version  V1.0
 */
package com.paradisecloud.smc3.busi.operation;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.AttendeeCallTheRollStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.enumer.AttendeeImportance;
import com.paradisecloud.smc3.busi.layout.SplitScreen;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.ParticipantStatusDto;
import com.paradisecloud.smc3.model.request.ConferenceStatusRequest;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <pre>点名与会者操作</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-20 16:42
 */
public class CallTheRollAttendeeOperation extends AttendeeOperation {
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-22 12:48
     */
    private static final long serialVersionUID = 1L;
    private AttendeeSmc3 callTheRollAttendee;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @param conferenceContext
     * @param splitScreen
     * @param attendees
     * @author lilinhai
     * @since 2021-02-22 13:38
     */
    public CallTheRollAttendeeOperation(Smc3ConferenceContext conferenceContext, SplitScreen splitScreen, List<AttendeeSmc3> attendees) {
        super(conferenceContext, splitScreen, attendees);
    }

    public CallTheRollAttendeeOperation(Smc3ConferenceContext conferenceContext,  AttendeeSmc3 attendees) {
        super(conferenceContext);
        this.callTheRollAttendee=attendees;
    }

    @Override
    public void operate() {
        AttendeeSmc3 masterAttendee = conferenceContext.getMasterAttendee();
        if (masterAttendee == null) {
            throw new CustomException("没有主会场,无法选看");
        }

        if(this.callTheRollAttendee.getCallTheRollStatus()== AttendeeCallTheRollStatus.YES.getValue()){
            return;
        }


        SmcParitipantsStateRep.ContentDTO smc3Participant = callTheRollAttendee.getSmcParticipant();
        Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
        String conferenceId = conferenceContext.getSmc3conferenceId();
        String participantId = smc3Participant.getGeneralParam().getId();
        String chairmanId = masterAttendee.getSmcParticipant().getGeneralParam().getId();

        ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
        conferenceStatusRequest.setSpokesman(participantId);
        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            bridgesByDept.getSmcConferencesInvoker().conferencesStatusControlCascade(conferenceId, conferenceStatusRequest, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            bridgesByDept.getSmcConferencesInvoker().conferencesControlChooseCascade(conferenceId, participantId,chairmanId, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            bridgesByDept.getSmcConferencesInvoker().conferencesStatusControl(conferenceId, conferenceStatusRequest, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            bridgesByDept.getSmcConferencesInvoker().conferencesControlChoose(conferenceId, participantId,chairmanId, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
        muteSite(conferenceId,chairmanId,bridgesByDept,false);


        List<ParticipantStatusDto> participantStatusList=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(conferenceContext.getAttendees())) {
            for (AttendeeSmc3 attendee : conferenceContext.getAttendees()) {
                otherSiteMute(attendee,participantStatusList);
            }
        }
        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeSmc3> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeSmc3 attendee : attendees) {
                    otherSiteMute(attendee,participantStatusList);
                }
            }
        }

        for (AttendeeSmc3 attendee : conferenceContext.getMasterAttendees()) {
            otherSiteMute(attendee,participantStatusList);
        }

        for (AttendeeSmc3 attendee : conferenceContext.getCascadeAttendees()) {
            otherSiteMute(attendee,participantStatusList);
        }
        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

        }else {
            bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

        }

        if (callTheRollAttendee != null) {
            AttendeeImportance.POINT.processAttendeeWebsocketMessage(callTheRollAttendee);
            callTheRollAttendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, callTheRollAttendee);
        }

    }

    private void otherSiteMute(AttendeeSmc3 attendee,List<ParticipantStatusDto> participantStatusList) {

        if(Objects.equals(attendee.getParticipantUuid(), this.callTheRollAttendee.getParticipantUuid())){
            return;
        }
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setIsMute(true);
        participantStatusDto.setId(attendee.getParticipantUuid());
        participantStatusList.add(participantStatusDto);
    }

    private void muteSite(String conferenceId, String participantId, Smc3Bridge bridge,Boolean mute) {
        List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(participantId);
        participantStatusDto.setIsMute(mute);
        participantStatusList.add(participantStatusDto);
        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
    }



    @Override
    public void cancel() {

        if(callTheRollAttendee!=null){
            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(callTheRollAttendee);
            callTheRollAttendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, callTheRollAttendee);
            //取消混音
            SmcParitipantsStateRep.ContentDTO smcParticipant = callTheRollAttendee.getSmcParticipant();
            if(smcParticipant!=null){
                List<ParticipantStatusDto> participantStatusList=new ArrayList<>();
                ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
                participantStatusDto.setIsMute(true);
                participantStatusDto.setId(smcParticipant.getGeneralParam().getId());
                participantStatusList.add(participantStatusDto);
                Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
                String conferenceId = conferenceContext.getSmc3conferenceId();
                if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                    bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }else {
                    bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
            }

        }

    }


}

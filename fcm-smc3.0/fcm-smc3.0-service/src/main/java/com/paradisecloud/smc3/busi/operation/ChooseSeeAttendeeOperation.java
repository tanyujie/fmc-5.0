package com.paradisecloud.smc3.busi.operation;



import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.AttendeeChooseSeeStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.enumer.AttendeeImportance;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.ParticipantStatus;
import com.paradisecloud.smc3.model.ParticipantStatusDto;
import com.paradisecloud.smc3.model.request.ConferenceStatusRequest;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import org.apache.commons.collections.CollectionUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <pre>请加上该类的描述</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-26 15:55
 */
public class ChooseSeeAttendeeOperation extends AttendeeOperation {


    private AttendeeSmc3 chooseSeeAttendee;
    private AttendeeSmc3 oldChooseSeeAttendee;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @param chooseSeeAttendee
     * @author lilinhai
     * @since 2021-02-22 13:47
     */
    public ChooseSeeAttendeeOperation(Smc3ConferenceContext conferenceContext, AttendeeSmc3 chooseSeeAttendee) {
        super(conferenceContext);
        this.chooseSeeAttendee = chooseSeeAttendee;
    }

    /**
     * 操作方法
     *
     * @author lilinhai
     * @since 2021-02-20 16:39  void
     */
    @Override
    public void operate() {
        operateScreen();
    }

    private void operateScreen() {

        if(chooseSeeAttendee.getChooseSeeStatus()==AttendeeChooseSeeStatus.YES.getValue()){
            return;
        }

        AttendeeOperation lastAttendeeOperation = conferenceContext.getLastAttendeeOperation();

        if(lastAttendeeOperation instanceof  ChangeMasterAttendeeOperation){
            ChangeMasterAttendeeOperation old= (ChangeMasterAttendeeOperation) lastAttendeeOperation;
            oldChooseSeeAttendee=old.getDefaultChooseSeeAttendee();
        }


        AttendeeSmc3 masterAttendee = conferenceContext.getMasterAttendee();

        if(masterAttendee==null){
            throw new CustomException("没有主会场,无法选看");
        }
        if (chooseSeeAttendee == masterAttendee)
        {
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "主会场不能被选看！");
            return;
        }


        if(chooseSeeAttendee.isMeetingJoined()){

            SmcParitipantsStateRep.ContentDTO masterParticipant = masterAttendee.getSmcParticipant();
            if(masterParticipant==null){
                return;
            }
            String chairmanId = masterParticipant.getGeneralParam().getId();
            SmcParitipantsStateRep.ContentDTO smcParticipantChoose= chooseSeeAttendee.getSmcParticipant();
            if(smcParticipantChoose==null){
                return;
            }
            String conferenceId = conferenceContext.getSmc3conferenceId();
            String participantId = smcParticipantChoose.getGeneralParam().getId();
            Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
            //锁定视频源
            ParticipantStatus participantStatus = new ParticipantStatus();
            participantStatus.setVideoSwitchAttribute("CUSTOMIZED");
            if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                bridgesByDept.getSmcParticipantsInvoker().conferencesParticipantStatusOnlyCascade(conferenceId,chairmanId,participantStatus,bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }else {
                bridgesByDept.getSmcParticipantsInvoker().conferencesParticipantStatusOnly(conferenceId,chairmanId,participantStatus,bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }

            if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                bridgesByDept.getSmcConferencesInvoker().conferencesControlChooseCascade(conferenceId, chairmanId, participantId, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }else {
                bridgesByDept.getSmcConferencesInvoker().conferencesControlChoose(conferenceId, chairmanId, participantId, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }

            participantStatus.setVideoSwitchAttribute("AUTO");
            if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                bridgesByDept.getSmcParticipantsInvoker().conferencesParticipantStatusOnlyCascade(conferenceId,chairmanId,participantStatus,bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }else {
                bridgesByDept.getSmcParticipantsInvoker().conferencesParticipantStatusOnly(conferenceId,chairmanId,participantStatus,bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
            chooseSeeAttendee.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());

            //广播主席
            ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
            conferenceStatusRequest.setBroadcaster(chairmanId);
            if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                bridgesByDept.getSmcConferencesInvoker().conferencesStatusControlCascade(conferenceId, conferenceStatusRequest, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }else {
                bridgesByDept.getSmcConferencesInvoker().conferencesStatusControl(conferenceId, conferenceStatusRequest, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }



            //其它会场
            List<ParticipantStatusDto> participantStatusList=new ArrayList<>();

            if (CollectionUtils.isNotEmpty(conferenceContext.getAttendees())) {
                for (AttendeeSmc3 attendee : conferenceContext.getAttendees()) {
                    othersiteList(participantStatusList, attendee);
                }
            }
            for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                List<AttendeeSmc3> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
                if (attendees != null) {
                    for (AttendeeSmc3 attendee : attendees) {
                        othersiteList(participantStatusList, attendee);
                    }
                }
            }

            for (AttendeeSmc3 attendee : conferenceContext.getMasterAttendees()) {
                othersiteList(participantStatusList, attendee);
            }

            if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }else {
                bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }



            if (isUpCascadeRollCall()) {
                AttendeeImportance.CHOOSE_SEE.processAttendeeWebsocketMessage(chooseSeeAttendee);
            } else if (isUpCascadePolling()) {
                if (isUpCascadeBroadcast()) {
                    AttendeeImportance.ROUND_BROADCAST.processAttendeeWebsocketMessage(chooseSeeAttendee);
                } else {
                    AttendeeImportance.ROUND.processAttendeeWebsocketMessage(chooseSeeAttendee);
                }
            } else {
                AttendeeImportance.CHOOSE_SEE.processAttendeeWebsocketMessage(chooseSeeAttendee);
            }
            if (oldChooseSeeAttendee != null&&!Objects.equals(oldChooseSeeAttendee.getId(),chooseSeeAttendee.getId())) {

                AttendeeImportance.COMMON.processAttendeeWebsocketMessage(oldChooseSeeAttendee);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, oldChooseSeeAttendee);

            }
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, chooseSeeAttendee);

        }

    }

    private void othersiteList(List<ParticipantStatusDto> participantStatusList, AttendeeSmc3 attendee) {
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setIsMute(true);
        participantStatusDto.setId(attendee.getParticipantUuid());
        participantStatusList.add(participantStatusDto);
    }

    @Override
    public void cancel() {
        if(chooseSeeAttendee!=null){

            //取消混音
            SmcParitipantsStateRep.ContentDTO smcParticipant = chooseSeeAttendee.getSmcParticipant();
            if(smcParticipant!=null){
                List<ParticipantStatusDto> participantStatusList=new ArrayList<>();
                ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
                participantStatusDto.setIsMute(true);
                participantStatusDto.setId(smcParticipant.getGeneralParam().getId());
                Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
                String conferenceId = conferenceContext.getSmc3conferenceId();

                if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                    bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }else {
                    bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }

            }

            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(chooseSeeAttendee);
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, chooseSeeAttendee);


            if (CollectionUtils.isNotEmpty(conferenceContext.getAttendees())) {
                for (AttendeeSmc3 attendee : conferenceContext.getAttendees()) {
                    pushMessage(attendee);
                }
            }
            for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                List<AttendeeSmc3> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
                if (attendees != null) {
                    for (AttendeeSmc3 attendee : attendees) {
                        pushMessage(attendee);
                    }
                }
            }

            for (AttendeeSmc3 attendee : conferenceContext.getMasterAttendees()) {
                pushMessage(attendee);
            }

        }
    }

    private void pushMessage(AttendeeSmc3 attendee) {
        AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendee);
        if (attendee.getUpdateMap().size() > 1) {
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee);
        }
    }
}

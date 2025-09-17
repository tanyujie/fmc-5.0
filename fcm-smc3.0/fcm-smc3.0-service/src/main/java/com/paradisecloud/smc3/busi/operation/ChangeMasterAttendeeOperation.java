package com.paradisecloud.smc3.busi.operation;


import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.enumer.AttendeeImportance;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.ParticipantStatus;
import com.paradisecloud.smc3.model.ParticipantStatusDto;
import com.paradisecloud.smc3.model.request.ConferenceStatusRequest;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Administrator
 */
public class ChangeMasterAttendeeOperation extends AttendeeOperation {

    private Logger logger= LoggerFactory.getLogger(getClass());

    public static final int WAIT_TIME = 10000;
    private volatile AttendeeSmc3 defaultChooseSeeAttendee = null;
    private AttendeeSmc3 targetAttendee;
    private AttendeeSmc3 oldMasterAttendee;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-22 13:47
     */
    public ChangeMasterAttendeeOperation(Smc3ConferenceContext conferenceContext) {
        super(conferenceContext);
        this.oldMasterAttendee = conferenceContext.getMasterAttendee();
    }

    public ChangeMasterAttendeeOperation(Smc3ConferenceContext conferenceContext, AttendeeSmc3 attendeeTele) {
        super(conferenceContext);
        this.oldMasterAttendee = conferenceContext.getMasterAttendee();
        this.targetAttendee = attendeeTele;
    }

    public AttendeeSmc3 getDefaultChooseSeeAttendee() {
        return defaultChooseSeeAttendee;
    }

    public void setDefaultChooseSeeAttendee(AttendeeSmc3 defaultChooseSeeAttendee) {
        this.defaultChooseSeeAttendee = defaultChooseSeeAttendee;
    }

    /**
     * 操作方法
     *
     * @author lilinhai
     * @since 2021-02-20 16:39  void
     */
    @Override
    public synchronized void operate() {
        initTargetAttendees();
        operateScreen();
    }

    private void initTargetAttendees() {

        if(targetAttendee==null){
            return;
        }
        for (AttendeeSmc3 attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (attendee.isMeetingJoined() && !Objects.equals(attendee.getId(), targetAttendee.getId())
                        && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
                    defaultChooseSeeAttendee = attendee;
                    return;
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeSmc3> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeSmc3 attendee : attendees) {
                    if (attendee.isMeetingJoined() && !Objects.equals(attendee.getId(), targetAttendee.getId())
                            && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
                        defaultChooseSeeAttendee = attendee;
                        return;
                    }
                }
            }
        }

        for (AttendeeSmc3 attendee : conferenceContext.getMasterAttendees()) {
            if (attendee.isMeetingJoined() && !Objects.equals(attendee.getId(), targetAttendee.getId())
                    && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
                defaultChooseSeeAttendee = attendee;
                return;
            }
        }

        AttendeeSmc3 attendee = conferenceContext.getMasterAttendee();
        if (attendee != null && !Objects.equals(attendee.getId(), targetAttendee.getId())
                && attendee.isMeetingJoined() && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
            defaultChooseSeeAttendee = attendee;
            return;
        }
    }

    private void operateScreen() {
        if (targetAttendee == null) {
            return;
        }
        Date startTime = conferenceContext.getStartTime();
        long currentTimeMillis = System.currentTimeMillis();
        if(currentTimeMillis-startTime.getTime()<= WAIT_TIME){
            Threads.sleep(1500);
        }

        if(Strings.isNotBlank(conferenceContext.getParentConferenceId())){
            return;
        }
        if (targetAttendee == conferenceContext.getMasterAttendee()) {
            oldMasterAttendee = conferenceContext.getMasterAttendee();
        }

        try {

            if (targetAttendee.isMeetingJoined()) {

                SmcParitipantsStateRep.ContentDTO smc3Participant = targetAttendee.getSmcParticipant();
                if (smc3Participant == null) {
                    return;
                }
                String conferenceId = conferenceContext.getSmc3conferenceId();
                String participantId = smc3Participant.getGeneralParam().getId();
                Smc3Bridge bridgesByDept = conferenceContext.getSmc3Bridge();
                AttendeeSmc3 masterAttendee = conferenceContext.getMasterAttendee();
                if (masterAttendee != null) {
                    muteTrue(conferenceContext.getSmc3conferenceId(), masterAttendee.getParticipantUuid(), bridgesByDept);
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("isMute", true);

                if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                    bridgesByDept.getSmcConferencesInvoker().conferencesControl(conferenceId, jsonObject.toJSONString(), bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }else {
                    bridgesByDept.getSmcConferencesInvoker().conferencesControl(conferenceId, jsonObject.toJSONString(), bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }

                conferenceContext.setMasterAttendee(targetAttendee);

                //锁定视频源
                ParticipantStatus participantStatus = new ParticipantStatus();
                participantStatus.setVideoSwitchAttribute("CUSTOMIZED");
                if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                    bridgesByDept.getSmcParticipantsInvoker().conferencesParticipantStatusOnlyCascade(conferenceId, participantId, participantStatus, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }else {
                    bridgesByDept.getSmcParticipantsInvoker().conferencesParticipantStatusOnly(conferenceId, participantId, participantStatus, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }

                ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
                conferenceStatusRequest.setChairman(participantId);
                if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                    bridgesByDept.getSmcConferencesInvoker().conferencesStatusControlCascade(conferenceId, conferenceStatusRequest, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }else {
                    bridgesByDept.getSmcConferencesInvoker().conferencesStatusControl(conferenceId, conferenceStatusRequest, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
                broadcaster(conferenceId, participantId, bridgesByDept);


                if (oldMasterAttendee != null && oldMasterAttendee != targetAttendee) {
                    AttendeeImportance.COMMON.processAttendeeWebsocketMessage(oldMasterAttendee);
                }


                if (oldMasterAttendee != targetAttendee) {
                    Map<String, Object> data = new HashMap<>(2);
                    data.put("oldMasterAttendee", oldMasterAttendee);
                    data.put("newMasterAttendee", targetAttendee);
                    Smc3WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
                    if (defaultChooseSeeAttendee != null) {
                        Smc3WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, defaultChooseSeeAttendee);
                    }

                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("主会场已切换至【").append(targetAttendee.getName()).append("】");
                    Smc3WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                }

                if (defaultChooseSeeAttendee != null) {
                    //主会场选看
                    SmcParitipantsStateRep.ContentDTO choose = defaultChooseSeeAttendee.getSmcParticipant();
                    if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                        bridgesByDept.getSmcConferencesInvoker().conferencesControlChooseCascade(conferenceId, participantId, choose.getGeneralParam().getId(), bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    }else {
                        bridgesByDept.getSmcConferencesInvoker().conferencesControlChoose(conferenceId, participantId, choose.getGeneralParam().getId(), bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    }
                    AttendeeImportance.CHOOSE_SEE.processAttendeeWebsocketMessage(defaultChooseSeeAttendee);
                }

                //锁定视频源
                participantStatus.setVideoSwitchAttribute("AUTO");
                if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                    bridgesByDept.getSmcParticipantsInvoker().conferencesParticipantStatusOnlyCascade(conferenceId, participantId, participantStatus, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

                }else {
                    bridgesByDept.getSmcParticipantsInvoker().conferencesParticipantStatusOnly(conferenceId, participantId, participantStatus, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

                }

                mutesetting(conferenceId, bridgesByDept);


            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }


    }

    private void mutesetting(String conferenceId, Smc3Bridge bridgesByDept) {
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
    }


    @Override
    public void cancel() {
        if (targetAttendee != null) {
            Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();
//            ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
//            conferenceStatusRequest.setChairman("");
//            smc3Bridge.getSmcConferencesInvoker().conferencesStatusControl(conferenceContext.getSmc3conferenceId(),conferenceStatusRequest , smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            AttendeeSmc3 masterAttendee = conferenceContext.getMasterAttendee();
//            if (masterAttendee != null) {
//                muteTrue(conferenceContext.getSmc3conferenceId(), masterAttendee.getParticipantUuid(), smc3Bridge);
//            }
//            muteTrue(conferenceContext.getSmc3conferenceId(), targetAttendee.getParticipantUuid(), smc3Bridge);
            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(targetAttendee);
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, targetAttendee);
            if (defaultChooseSeeAttendee != null) {
                AttendeeImportance.COMMON.processAttendeeWebsocketMessage(defaultChooseSeeAttendee);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, defaultChooseSeeAttendee);
            }
        }
    }


    public void cancelChooseStatus(){
        if (defaultChooseSeeAttendee != null) {
            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(defaultChooseSeeAttendee);
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, defaultChooseSeeAttendee);
        }
    }

    private void broadcaster(String conferenceId, String participantId, Smc3Bridge bridge) {
        ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
        conferenceStatusRequest.setBroadcaster(participantId);
        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            bridge.getSmcConferencesInvoker().conferencesStatusControlCascade(conferenceId, conferenceStatusRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            bridge.getSmcConferencesInvoker().conferencesStatusControl(conferenceId, conferenceStatusRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
    }

    private void muteTrue(String conferenceId, String participantId, Smc3Bridge bridge) {
        List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(participantId);
        participantStatusDto.setIsMute(true);
        participantStatusList.add(participantStatusDto);
        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            bridge.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceId, participantStatusList, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
    }


    private void otherSiteMute(AttendeeSmc3 attendee,List<ParticipantStatusDto> participantStatusList) {

        if(Objects.equals(attendee.getParticipantUuid(), this.targetAttendee.getParticipantUuid())){
            return;
        }
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setIsMute(true);
        participantStatusDto.setId(attendee.getParticipantUuid());
        participantStatusList.add(participantStatusDto);
    }

}

package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation;


import com.huaweicloud.sdk.meeting.v1.model.RestSubscriberInPic;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.AttendeeImportance;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudMeetingBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudWebSocketMessagePusher;

import java.util.*;


/**
 * @author nj
 * @date 2023/5/16 14:30
 */
public class ChangeMasterAttendeeOperation extends AttendeeOperation {


    private volatile AttendeeHwcloud defaultChooseSeeAttendee = null;
    private AttendeeHwcloud targetAttendee;
    private AttendeeHwcloud oldMasterAttendee;

    protected ChangeMasterAttendeeOperation(HwcloudConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    public ChangeMasterAttendeeOperation(HwcloudConferenceContext conferenceContext, AttendeeHwcloud master) {
        super(conferenceContext);
        this.targetAttendee = master;
        this.oldMasterAttendee = conferenceContext.getMasterAttendee();
    }


    @Override
    public void operate() {
        initTargetAttendees();

        changMasterProcess();
    }

    private void initTargetAttendees() {
        if (targetAttendee == null) {
            return;
        }
        for (AttendeeHwcloud attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (attendee.isMeetingJoined() && !Objects.equals(attendee.getId(), targetAttendee.getId())
                        && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
                    defaultChooseSeeAttendee = attendee;
                    return;
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeHwcloud> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeHwcloud attendee : attendees) {
                    if (attendee.isMeetingJoined() && !Objects.equals(attendee.getId(), targetAttendee.getId())
                            && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
                        defaultChooseSeeAttendee = attendee;
                        return;
                    }
                }
            }
        }

        for (AttendeeHwcloud attendee : conferenceContext.getMasterAttendees()) {
            if (attendee.isMeetingJoined() && !Objects.equals(attendee.getId(), targetAttendee.getId())
                    && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
                defaultChooseSeeAttendee = attendee;
                return;
            }
        }

        AttendeeHwcloud attendee = conferenceContext.getMasterAttendee();
        if (attendee != null && !Objects.equals(attendee.getId(), targetAttendee.getId())
                && attendee.isMeetingJoined() && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
            defaultChooseSeeAttendee = attendee;
            return;
        }
    }

    /**
     * 会议中同一时段只能存在一个主席会场。
     * 若被指定会场已是主席，重复设置会失败。
     * 若会议中已经设置了其他会场为主席会场，必须先通过releaseConfChairEx接口释放原来的主席，才能设置新的主席。
     * 如果是智真三屏会场，只能设置中屏为主席。
     */
    private void changMasterProcess() {
        if (conferenceContext == null || conferenceContext.isEnd() || targetAttendee == null) {
            return;
        }
        HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();

        if (targetAttendee.isMeetingJoined()) {
            String participantUuid = targetAttendee.getParticipantUuid();

            hwcloudMeetingBridge.getMeetingControl().applyChair(hwcloudMeetingBridge.getTokenInfo().getToken(), hwcloudMeetingBridge.getConfID(), participantUuid, 1, hwcloudMeetingBridge.getHostPassword());
            targetAttendee.setUserRole(2);
            hwcloudMeetingBridge.getMeetingControl().muteMeeting(hwcloudMeetingBridge.getTokenInfo().getToken(), hwcloudMeetingBridge.getConfID(), 1, 1);
            conferenceContext.setMasterAttendee(targetAttendee);


            hwcloudMeetingBridge.getMeetingControl().muteParticipant(hwcloudMeetingBridge.getTokenInfo().getToken(), hwcloudMeetingBridge.getConfID(), participantUuid, 0);

            if (oldMasterAttendee != null && oldMasterAttendee != targetAttendee) {
                AttendeeImportance.COMMON.processAttendeeWebsocketMessage(oldMasterAttendee);
            }


            if (oldMasterAttendee != null && oldMasterAttendee != targetAttendee) {
                Map<String, Object> data = new HashMap<>(2);
                data.put("oldMasterAttendee", oldMasterAttendee);
                data.put("newMasterAttendee", targetAttendee);
                HwcloudWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
                if (defaultChooseSeeAttendee != null) {
                    HwcloudWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, defaultChooseSeeAttendee);
                }

                StringBuilder messageTip = new StringBuilder();
                messageTip.append("主会场已切换至【").append(targetAttendee.getName()).append("】");
                HwcloudWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
            }else {

                StringBuilder messageTip = new StringBuilder();
                messageTip.append("主会场已切换至【").append(targetAttendee.getName()).append("】");
                HwcloudWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                Map<String, Object> data = new HashMap<>(1);
                data.put("newMasterAttendee", targetAttendee);
                HwcloudWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);

            }

            if (defaultChooseSeeAttendee != null) {
                //主会场选看
//                String participantUuid1 = defaultChooseSeeAttendee.getParticipantUuid();
//                hwcloudMeetingBridge.getMeetingControl().partView(hwcloudMeetingBridge.getTokenInfo().getToken(),hwcloudMeetingBridge.getConfID(), targetAttendee.getParticipantUuid(), participantUuid1);

                //新建多画面为一分屏

                List<RestSubscriberInPic> subscriberInPics = new ArrayList<>();
                RestSubscriberInPic restSubscriberInPic = new RestSubscriberInPic();
                restSubscriberInPic.setIndex(1);
                restSubscriberInPic.setIsAssistStream(0);
                List<String> ids = new ArrayList<>();
                ids.add(defaultChooseSeeAttendee.getNumber());
                restSubscriberInPic.setSubscriber(ids);
                subscriberInPics.add(restSubscriberInPic);

                hwcloudMeetingBridge.getMeetingControl().setCustomMultiPicture(hwcloudMeetingBridge.getTokenInfo().getToken(), hwcloudMeetingBridge.getConfID(), 10, null, 1, "Single", subscriberInPics, true);
                hwcloudMeetingBridge.getMeetingControl().chairView(hwcloudMeetingBridge.getTokenInfo().getToken(), hwcloudMeetingBridge.getConfID(), 1, null, null, null);

                AttendeeImportance.CHOOSE_SEE.processAttendeeWebsocketMessage(defaultChooseSeeAttendee);

            }

            //广播主席
            hwcloudMeetingBridge.getMeetingControl().broadcast(hwcloudMeetingBridge.getTokenInfo().getToken(), hwcloudMeetingBridge.getConfID(), participantUuid);
        }


    }


    @Override
    public void cancel() {
        if (defaultChooseSeeAttendee != null) {
            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(defaultChooseSeeAttendee);
            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, defaultChooseSeeAttendee);
        }
        HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
        //取消主持人
//        hwcloudMeetingBridge.getMeetingControl().applyChair(hwcloudMeetingBridge.getTokenInfo().getToken(),hwcloudMeetingBridge.getConfID(),targetAttendee.getParticipantUuid(),0, hwcloudMeetingBridge.getHostPassword());
//        hwcloudMeetingBridge.getMeetingControl().muteParticipant(hwcloudMeetingBridge.getTokenInfo().getToken(),hwcloudMeetingBridge.getConfID(),targetAttendee.getParticipantUuid(),1);
//        targetAttendee.setUserRole(0);
    }

    public AttendeeHwcloud getDefaultChooseSeeAttendee() {
        return defaultChooseSeeAttendee;
    }

    public void setDefaultChooseSeeAttendee(AttendeeHwcloud defaultChooseSeeAttendee) {
        this.defaultChooseSeeAttendee = defaultChooseSeeAttendee;
    }

    public void cancelChooseStatus(){
        if (defaultChooseSeeAttendee != null) {
            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(defaultChooseSeeAttendee);
            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, defaultChooseSeeAttendee);
        }
    }
}

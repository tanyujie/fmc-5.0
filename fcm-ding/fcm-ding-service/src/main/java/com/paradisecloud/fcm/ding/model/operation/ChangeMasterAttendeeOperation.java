package com.paradisecloud.fcm.ding.model.operation;


import com.paradisecloud.fcm.ding.busi.AttendeeImportance;
import com.paradisecloud.fcm.ding.busi.attende.AttendeeDing;
import com.paradisecloud.fcm.ding.cache.DingConferenceContext;
import com.paradisecloud.fcm.ding.cache.DingWebSocketMessagePusher;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * @author nj
 * @date 2023/5/16 14:30
 */
public class ChangeMasterAttendeeOperation extends AttendeeOperation {

    private final Logger logger = LoggerFactory.getLogger(ChangeMasterAttendeeOperation.class);

    private volatile AttendeeDing defaultChooseSeeAttendee = null;
    private AttendeeDing targetAttendee;
    private AttendeeDing oldMasterAttendee;

    protected ChangeMasterAttendeeOperation(DingConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    public ChangeMasterAttendeeOperation(DingConferenceContext conferenceContext, AttendeeDing master) {
        super(conferenceContext);
        this.targetAttendee = master;
        this.oldMasterAttendee=conferenceContext.getMasterAttendee();
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
        for (AttendeeDing attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (attendee.isMeetingJoined() && attendee.getInstanceid() == 9 && !Objects.equals(attendee.getId(), targetAttendee.getId())
                        && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
                    defaultChooseSeeAttendee = attendee;
                    return;
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeDing> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeDing attendee : attendees) {
                    if (attendee.isMeetingJoined() && attendee.getInstanceid() == 9 && !Objects.equals(attendee.getId(), targetAttendee.getId())
                            && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
                        defaultChooseSeeAttendee = attendee;
                        return;
                    }
                }
            }
        }

        for (AttendeeDing attendee : conferenceContext.getMasterAttendees()) {
            if (attendee.isMeetingJoined() && attendee.getInstanceid() == 9 && !Objects.equals(attendee.getId(), targetAttendee.getId())
                    && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
                defaultChooseSeeAttendee = attendee;
                return;
            }
        }

        AttendeeDing attendee = conferenceContext.getMasterAttendee();
        if (attendee != null && !Objects.equals(attendee.getId(), targetAttendee.getId())
                && attendee.isMeetingJoined() && attendee.getInstanceid() == 9 && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
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
        if (conferenceContext == null || conferenceContext.isEnd()||targetAttendee==null) {
            return;
        }
        Integer instanceid = targetAttendee.getInstanceid();
        if(instanceid==null||instanceid!=9){
            throw new CustomException("暂不支持的终端类型");
        }


    }




    @Override
    public void cancel() {
        if (defaultChooseSeeAttendee != null) {
            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(defaultChooseSeeAttendee);
            DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, defaultChooseSeeAttendee);
        }
    }

    public AttendeeDing getDefaultChooseSeeAttendee() {
        return defaultChooseSeeAttendee;
    }

    public void setDefaultChooseSeeAttendee(AttendeeDing defaultChooseSeeAttendee) {
        this.defaultChooseSeeAttendee = defaultChooseSeeAttendee;
    }
}

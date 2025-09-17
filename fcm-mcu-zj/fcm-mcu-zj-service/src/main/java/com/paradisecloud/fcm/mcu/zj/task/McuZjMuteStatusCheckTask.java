package com.paradisecloud.fcm.mcu.zj.task;

import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.sinhy.spring.BeanFactory;

import java.util.ArrayList;
import java.util.List;

public class McuZjMuteStatusCheckTask extends DelayTask {

    private McuZjConferenceContext conferenceContext;

    public McuZjMuteStatusCheckTask(String id, long delayInMilliseconds, McuZjConferenceContext conferenceContext) {
        super("mute_check_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        if (conferenceContext != null) {

            boolean masterMixing = false;
            boolean guestMixing = false;
            if (conferenceContext.getMasterAttendee() != null) {
                AttendeeForMcuZj attendee = conferenceContext.getMasterAttendee();
                if (attendee.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                    masterMixing = true;
                }
            }

            for (AttendeeForMcuZj attendee : new ArrayList<>(conferenceContext.getAttendees())) {
                if (attendee != null) {
                    if (attendee.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                        guestMixing = true;
                    }
                }
            }

            if (!guestMixing) {
                if (conferenceContext.getMasterAttendees() != null) {
                    for (AttendeeForMcuZj attendee : conferenceContext.getMasterAttendees()) {
                        if (attendee.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                            guestMixing = true;
                        }
                    }
                }
            }

            if (!guestMixing) {
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    List<AttendeeForMcuZj> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
                    if (attendees != null) {
                        for (AttendeeForMcuZj attendee : attendees) {
                            if (attendee.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                                guestMixing = true;
                            }
                        }
                    }
                }
            }
            Integer muteStatusOld = conferenceContext.getMuteStatus();
            if (!masterMixing && !guestMixing) {
                conferenceContext.setMuteStatus(2);
            } else if (masterMixing && !guestMixing) {
                conferenceContext.setMuteStatus(1);
            } else {
                conferenceContext.setMuteStatus(0);
            }
            if (muteStatusOld.intValue() != conferenceContext.getMuteStatus()) {
                BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
            }
        }
    }
}

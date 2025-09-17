package com.paradisecloud.fcm.mcu.kdc.task;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.CcAddMixingTerminalRequest;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.CcRemoveMixingTerminalRequest;
import com.paradisecloud.fcm.mcu.kdc.model.response.cc.CcAddMixingTerminalResponse;
import com.paradisecloud.fcm.mcu.kdc.model.response.cc.CcRemoveMixingTerminalResponse;

import java.util.ArrayList;
import java.util.List;

public class McuKdcAttendeeSetMuteTask extends DelayTask {

    private McuKdcConferenceContext conferenceContext;
    private List<AttendeeForMcuKdc> attendees;
    private boolean mute;

    public McuKdcAttendeeSetMuteTask(String id, long delayInMilliseconds, McuKdcConferenceContext conferenceContext, AttendeeForMcuKdc attendee, boolean mute) {
        super(id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        attendees = new ArrayList<>();
        this.attendees.add(attendee);
        this.mute = mute;
    }

    public McuKdcAttendeeSetMuteTask(String id, long delayInMilliseconds, McuKdcConferenceContext conferenceContext, List<AttendeeForMcuKdc> attendees, boolean mute) {
        super(id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = attendees;
        this.mute = mute;
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
            List<CcAddMixingTerminalRequest.Mt> members = new ArrayList<>();
            for (AttendeeForMcuKdc attendee : attendees) {
                if (attendee != null && attendee.isMeetingJoined()) {
                    if (StringUtils.isNotEmpty(attendee.getParticipantUuid())) {
                        CcAddMixingTerminalRequest.Mt mt = new CcAddMixingTerminalRequest.Mt();
                        mt.setMt_id(attendee.getParticipantUuid());
                        members.add(mt);
                    }
                }
            }
            if (members.size() > 0) {
                if (mute) {
                    CcRemoveMixingTerminalRequest ccRemoveMixingTerminalRequest = new CcRemoveMixingTerminalRequest();
                    ccRemoveMixingTerminalRequest.setConf_id(conferenceContext.getConfId());
                    ccRemoveMixingTerminalRequest.setMix_id("1");
                    ccRemoveMixingTerminalRequest.setMembers(members);
                    CcRemoveMixingTerminalResponse ccRemoveMixingTerminalResponse = conferenceContext.getConferenceControlApi().removeMixingTerminal(ccRemoveMixingTerminalRequest);
                    if (ccRemoveMixingTerminalResponse != null && ccRemoveMixingTerminalResponse.isSuccess()) {
                    }
                } else {
                    CcAddMixingTerminalRequest ccAddMixingTerminalRequest = new CcAddMixingTerminalRequest();
                    ccAddMixingTerminalRequest.setConf_id(conferenceContext.getConfId());
                    ccAddMixingTerminalRequest.setMix_id("1");
                    ccAddMixingTerminalRequest.setMembers(members);
                    CcAddMixingTerminalResponse ccAddMixingTerminalResponse = conferenceContext.getConferenceControlApi().addMixingTerminal(ccAddMixingTerminalRequest);
                    if (ccAddMixingTerminalResponse != null && ccAddMixingTerminalResponse.isSuccess()) {
                    }
                }
            }
        }
    }
}

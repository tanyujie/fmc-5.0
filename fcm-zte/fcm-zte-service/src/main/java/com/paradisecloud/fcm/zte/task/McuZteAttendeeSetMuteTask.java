package com.paradisecloud.fcm.zte.task;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import com.zte.m900.request.CancelMuteParticipantRequest;
import com.zte.m900.request.MuteParticipantRequest;


public class McuZteAttendeeSetMuteTask extends DelayTask {

    private McuZteConferenceContext conferenceContext;
    private AttendeeForMcuZte attendee;
    private boolean mute;

    public McuZteAttendeeSetMuteTask(String id, long delayInMilliseconds, McuZteConferenceContext conferenceContext, AttendeeForMcuZte attendee, boolean mute) {
        super(id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendee = attendee;
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
            if (attendee != null && attendee.isMeetingJoined()) {
                if (StringUtils.isNotEmpty(attendee.getParticipantUuid())) {
                    if(mute){
                        MuteParticipantRequest ccMuteParticipantRequest = new MuteParticipantRequest();
                        ccMuteParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                        ccMuteParticipantRequest.setTerminalIdentifier(attendee.getParticipantUuid());
                        conferenceContext.getConferenceControlApi().muteParticipant(ccMuteParticipantRequest);
                    }else {
                        CancelMuteParticipantRequest ccCancelMuteParticipantRquest = new CancelMuteParticipantRequest();
                        ccCancelMuteParticipantRquest.setConferenceIdentifier(conferenceContext.getConfId());
                        ccCancelMuteParticipantRquest.setTerminalIdentifier(attendee.getParticipantUuid());
                        conferenceContext.getConferenceControlApi().cancelMuteParticipant(ccCancelMuteParticipantRquest);
                    }

                }
            }
        }
    }
}

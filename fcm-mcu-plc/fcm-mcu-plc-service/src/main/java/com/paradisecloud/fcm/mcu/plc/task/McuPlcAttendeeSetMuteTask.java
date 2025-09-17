package com.paradisecloud.fcm.mcu.plc.task;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeVideoStatus;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.request.cc.CcUpdateTerminalAudioAndVideoRequest;
import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cc.CcUpdateTerminalAudioAndVideoResponse;

public class McuPlcAttendeeSetMuteTask extends DelayTask {

    private McuPlcConferenceContext conferenceContext;
    private AttendeeForMcuPlc attendee;
    private boolean mute;

    public McuPlcAttendeeSetMuteTask(String id, long delayInMilliseconds, McuPlcConferenceContext conferenceContext, AttendeeForMcuPlc attendee, boolean mute) {
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
                    CcUpdateTerminalAudioAndVideoRequest ccUpdateTerminalAudioAndVideoRequest = new CcUpdateTerminalAudioAndVideoRequest();
                    ccUpdateTerminalAudioAndVideoRequest.setId(conferenceContext.getConfId());
                    ccUpdateTerminalAudioAndVideoRequest.setParty_id(attendee.getParticipantUuid());
                    ccUpdateTerminalAudioAndVideoRequest.setAudio_mute(mute);
                    if (attendee.getVideoStatus() == AttendeeVideoStatus.YES.getValue()) {
                        ccUpdateTerminalAudioAndVideoRequest.setVideo_mute(false);
                    } else {
                        ccUpdateTerminalAudioAndVideoRequest.setVideo_mute(false);
                    }
                    CcUpdateTerminalAudioAndVideoResponse ccUpdateTerminalAudioAndVideoResponse = conferenceContext.getConferenceControlApi().updateTerminalAudioAndVideo(ccUpdateTerminalAudioAndVideoRequest);
                    if (ccUpdateTerminalAudioAndVideoResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateTerminalAudioAndVideoResponse.getStatus())) {
                    }
                }
            }
        }
    }
}

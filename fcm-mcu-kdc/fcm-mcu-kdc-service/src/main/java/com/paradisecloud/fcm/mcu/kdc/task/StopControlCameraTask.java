package com.paradisecloud.fcm.mcu.kdc.task;

import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.CcCameraControlRequest;
import org.springframework.util.StringUtils;

public class StopControlCameraTask extends DelayTask {

    private McuKdcConferenceContext conferenceContext;
    private AttendeeForMcuKdc attendee;
    private String movement;

    public StopControlCameraTask(String id, long delayInMilliseconds, McuKdcConferenceContext conferenceContext, AttendeeForMcuKdc attendee, String movement) {
        super(id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendee = attendee;
        this.movement = movement;
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
            if (attendee != null && StringUtils.hasText(attendee.getParticipantUuid())) {
                if (System.currentTimeMillis() - attendee.getLastControlCameraTime() > 500 && movement.equals(attendee.getLastControlCameraMove())) {
                    CcCameraControlRequest ccCameraControlRequest = new CcCameraControlRequest();
                    ccCameraControlRequest.setConf_id(conferenceContext.getConfId());
                    ccCameraControlRequest.setState(CcCameraControlRequest.state_stop);
                    ccCameraControlRequest.setMt_id(attendee.getParticipantUuid());
                    ccCameraControlRequest.setType(CcCameraControlRequest.type_down);
                    conferenceContext.getConferenceControlApi().controlCamera(ccCameraControlRequest);
                    attendee.setLastControlCameraMove(null);
                    attendee.setLastControlCameraTime(0);
                }
            }
        }
    }
}

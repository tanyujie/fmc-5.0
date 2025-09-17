package com.paradisecloud.fcm.mcu.zj.task;

import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcCameraControlRequest;
import org.springframework.util.StringUtils;

import java.util.Collection;

public class StopControlCameraTask extends DelayTask {

    private McuZjConferenceContext conferenceContext;
    private AttendeeForMcuZj attendee;
    private String movement;

    public StopControlCameraTask(String id, long delayInMilliseconds, McuZjConferenceContext conferenceContext, AttendeeForMcuZj attendee, String movement) {
        super("stop_control_" + id, delayInMilliseconds);
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
            if (attendee != null && StringUtils.hasText(attendee.getEpUserId())) {
                if (System.currentTimeMillis() - attendee.getLastControlCameraTime() > 500 && movement.equals(attendee.getLastControlCameraMove())) {
                    CcCameraControlRequest ccCameraControlRequest = new CcCameraControlRequest();
                    ccCameraControlRequest.setAction(CcCameraControlRequest.action_stop);
                    ccCameraControlRequest.setUsr_id(attendee.getEpUserId());
                    ccCameraControlRequest.setSpeed(4);
                    ccCameraControlRequest.setTimeout(100);
                    conferenceContext.getConferenceControlApi().cameraControl(ccCameraControlRequest);
                    attendee.setLastControlCameraMove(null);
                    attendee.setLastControlCameraTime(0);
                }
            }
        }
    }
}

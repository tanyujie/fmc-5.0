package com.paradisecloud.fcm.mcu.zj.task;

import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;

import java.util.List;

public class UnlockAttendeesTask extends DelayTask {
    private McuZjConferenceContext conferenceContext;

    public UnlockAttendeesTask(String id, long delayInMilliseconds, McuZjConferenceContext conferenceContext) {
        super("unlock_c_" + id, delayInMilliseconds);
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
            {
                AttendeeForMcuZj masterAttendee = conferenceContext.getMasterAttendee();
                if (masterAttendee != null) {
                    if (masterAttendee.isLocked()) {
                        masterAttendee.resetUpdateMap();
                        masterAttendee.setLocked(false);
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, masterAttendee.getUpdateMap());
                    }
                }
            }
            {
                List<AttendeeForMcuZj> attendees = conferenceContext.getAttendees();
                for (AttendeeForMcuZj attendee : attendees) {
                    if (attendee.isLocked()) {
                        attendee.resetUpdateMap();
                        attendee.setLocked(false);
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                    }
                }
            }
            {
                List<AttendeeForMcuZj> masterAttendees = conferenceContext.getMasterAttendees();
                for (AttendeeForMcuZj attendee : masterAttendees) {
                    if (attendee.isLocked()) {
                        attendee.resetUpdateMap();
                        attendee.setLocked(false);
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                    }
                }
            }
            {
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    List<AttendeeForMcuZj> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
                    if (attendees != null) {
                        for (AttendeeForMcuZj attendee : attendees) {
                            if (attendee.isLocked()) {
                                attendee.resetUpdateMap();
                                attendee.setLocked(false);
                                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                            }
                        }
                    }
                }
            }
        }
    }
}

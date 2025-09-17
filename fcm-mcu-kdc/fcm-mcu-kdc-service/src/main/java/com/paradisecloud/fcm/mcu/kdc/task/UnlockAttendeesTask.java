package com.paradisecloud.fcm.mcu.kdc.task;

import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;

import java.util.List;

public class UnlockAttendeesTask extends DelayTask {
    private McuKdcConferenceContext conferenceContext;

    public UnlockAttendeesTask(String id, long delayInMilliseconds, McuKdcConferenceContext conferenceContext) {
        super(id, delayInMilliseconds);
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
                AttendeeForMcuKdc masterAttendee = conferenceContext.getMasterAttendee();
                if (masterAttendee.isLocked()) {
                    masterAttendee.resetUpdateMap();
                    masterAttendee.setLocked(false);
                    McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, masterAttendee.getUpdateMap());
                }
            }
            {
                List<AttendeeForMcuKdc> attendees = conferenceContext.getAttendees();
                for (AttendeeForMcuKdc attendee : attendees) {
                    if (attendee.isLocked()) {
                        attendee.resetUpdateMap();
                        attendee.setLocked(false);
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                    }
                }
            }
            {
                List<AttendeeForMcuKdc> masterAttendees = conferenceContext.getMasterAttendees();
                for (AttendeeForMcuKdc attendee : masterAttendees) {
                    if (attendee.isLocked()) {
                        attendee.resetUpdateMap();
                        attendee.setLocked(false);
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                    }
                }
            }
            {
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    List<AttendeeForMcuKdc> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
                    if (attendees != null) {
                        for (AttendeeForMcuKdc attendee : attendees) {
                            if (attendee.isLocked()) {
                                attendee.resetUpdateMap();
                                attendee.setLocked(false);
                                McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                            }
                        }
                    }
                }
            }
        }
    }
}

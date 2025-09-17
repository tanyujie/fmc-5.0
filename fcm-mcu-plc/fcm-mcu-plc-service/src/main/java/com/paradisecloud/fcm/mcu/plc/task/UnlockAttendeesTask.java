package com.paradisecloud.fcm.mcu.plc.task;

import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;

import java.util.List;

public class UnlockAttendeesTask extends DelayTask {
    private McuPlcConferenceContext conferenceContext;

    public UnlockAttendeesTask(String id, long delayInMilliseconds, McuPlcConferenceContext conferenceContext) {
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
                AttendeeForMcuPlc masterAttendee = conferenceContext.getMasterAttendee();
                if (masterAttendee.isLocked()) {
                    masterAttendee.resetUpdateMap();
                    masterAttendee.setLocked(false);
                    McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, masterAttendee.getUpdateMap());
                }
            }
            {
                List<AttendeeForMcuPlc> attendees = conferenceContext.getAttendees();
                for (AttendeeForMcuPlc attendee : attendees) {
                    if (attendee.isLocked()) {
                        attendee.resetUpdateMap();
                        attendee.setLocked(false);
                        McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                    }
                }
            }
            {
                List<AttendeeForMcuPlc> masterAttendees = conferenceContext.getMasterAttendees();
                for (AttendeeForMcuPlc attendee : masterAttendees) {
                    if (attendee.isLocked()) {
                        attendee.resetUpdateMap();
                        attendee.setLocked(false);
                        McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                    }
                }
            }
            {
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    List<AttendeeForMcuPlc> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
                    if (attendees != null) {
                        for (AttendeeForMcuPlc attendee : attendees) {
                            if (attendee.isLocked()) {
                                attendee.resetUpdateMap();
                                attendee.setLocked(false);
                                McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                            }
                        }
                    }
                }
            }
        }
    }
}

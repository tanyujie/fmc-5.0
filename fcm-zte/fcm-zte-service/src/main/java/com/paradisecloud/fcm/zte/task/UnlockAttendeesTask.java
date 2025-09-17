package com.paradisecloud.fcm.zte.task;

import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.zte.cache.McuZteWebSocketMessagePusher;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;


import java.util.List;

public class UnlockAttendeesTask extends DelayTask {
    private McuZteConferenceContext conferenceContext;

    public UnlockAttendeesTask(String id, long delayInMilliseconds, McuZteConferenceContext conferenceContext) {
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
                AttendeeForMcuZte masterAttendee = conferenceContext.getMasterAttendee();
                if (masterAttendee.isLocked()) {
                    masterAttendee.resetUpdateMap();
                    masterAttendee.setLocked(false);
                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, masterAttendee.getUpdateMap());
                }
            }
            {
                List<AttendeeForMcuZte> attendees = conferenceContext.getAttendees();
                for (AttendeeForMcuZte attendee : attendees) {
                    if (attendee.isLocked()) {
                        attendee.resetUpdateMap();
                        attendee.setLocked(false);
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                    }
                }
            }
            {
                List<AttendeeForMcuZte> masterAttendees = conferenceContext.getMasterAttendees();
                for (AttendeeForMcuZte attendee : masterAttendees) {
                    if (attendee.isLocked()) {
                        attendee.resetUpdateMap();
                        attendee.setLocked(false);
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                    }
                }
            }
            {
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    List<AttendeeForMcuZte> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
                    if (attendees != null) {
                        for (AttendeeForMcuZte attendee : attendees) {
                            if (attendee.isLocked()) {
                                attendee.resetUpdateMap();
                                attendee.setLocked(false);
                                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.request;

/**
 * @author admin
 */
public class DelMeetingRoomRequest extends CommonRequest {

    private int meetingroom_id;

    public int getMeetingroom_id() {
        return meetingroom_id;
    }

    public void setMeetingroom_id(int meetingroom_id) {
        this.meetingroom_id = meetingroom_id;
    }
}

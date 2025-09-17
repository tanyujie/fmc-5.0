package com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.request;

public class AddMeetingRoomBookByMeetingRequest extends CommonRequest {

    private int meetingroom_id;
    private String meetingid;
    private String booker;

    public int getMeetingroom_id() {
        return meetingroom_id;
    }

    public void setMeetingroom_id(int meetingroom_id) {
        this.meetingroom_id = meetingroom_id;
    }

    public String getMeetingid() {
        return meetingid;
    }

    public void setMeetingid(String meetingid) {
        this.meetingid = meetingid;
    }

    public String getBooker() {
        return booker;
    }

    public void setBooker(String booker) {
        this.booker = booker;
    }
}

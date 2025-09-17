package com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.request;

public class AddMeetingRoomBookByScheduleRequest extends CommonRequest {

    private int meetingroom_id;
    private String schedule_id;
    private String booker;

    public int getMeetingroom_id() {
        return meetingroom_id;
    }

    public void setMeetingroom_id(int meetingroom_id) {
        this.meetingroom_id = meetingroom_id;
    }

    public String getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(String schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getBooker() {
        return booker;
    }

    public void setBooker(String booker) {
        this.booker = booker;
    }
}

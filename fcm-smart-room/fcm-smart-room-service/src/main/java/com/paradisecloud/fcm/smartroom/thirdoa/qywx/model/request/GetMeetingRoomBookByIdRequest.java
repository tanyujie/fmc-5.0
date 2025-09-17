package com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.request;

public class GetMeetingRoomBookByIdRequest extends CommonRequest{
    private String meetingroom_id;
    private String booking_id;

    public String getMeetingroom_id() {
        return meetingroom_id;
    }

    public void setMeetingroom_id(String meetingroom_id) {
        this.meetingroom_id = meetingroom_id;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }
}

package com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.response;

public class AddMeetingRoomBookResponse extends CommonResponse {
    private String booking_id;
    private String schedule_id;

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(String schedule_id) {
        this.schedule_id = schedule_id;
    }
}

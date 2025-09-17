package com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.request;

public class CancelMeetingRoomBookRequest extends CommonRequest {

    private String booking_id;
    private int keep_schedule;
    private int cancel_date;

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public int getKeep_schedule() {
        return keep_schedule;
    }

    public void setKeep_schedule(int keep_schedule) {
        this.keep_schedule = keep_schedule;
    }

    public int getCancel_date() {
        return cancel_date;
    }

    public void setCancel_date(int cancel_date) {
        this.cancel_date = cancel_date;
    }
}

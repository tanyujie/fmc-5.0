package com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.response;

import java.util.List;

public class AddMeetingRoomBookByScheduleResponse extends CommonResponse {

    private String booking_id;
    private List<Integer> conflict_date;

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public List<Integer> getConflict_date() {
        return conflict_date;
    }

    public void setConflict_date(List<Integer> conflict_date) {
        this.conflict_date = conflict_date;
    }
}

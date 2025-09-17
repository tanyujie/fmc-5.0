package com.paradisecloud.fcm.web.cloud;

import com.paradisecloud.fcm.service.conference.BaseConferenceContext;

public class CountBasedBookingStrategy implements BookingStrategy {
    private CloudMeetingResourceRoom room;

    public CountBasedBookingStrategy(CloudMeetingResourceRoom room) {
        this.room = room;
    }

    @Override
    public BaseConferenceContext bookMeeting(String name,String mcuType,int duration) {
        return room.bookMeetingByCount(name,mcuType,duration);
    }
}

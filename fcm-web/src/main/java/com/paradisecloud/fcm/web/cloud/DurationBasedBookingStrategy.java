package com.paradisecloud.fcm.web.cloud;

import com.paradisecloud.fcm.service.conference.BaseConferenceContext;

public class DurationBasedBookingStrategy implements BookingStrategy {

    private CloudMeetingResourceRoom room;

    public DurationBasedBookingStrategy(CloudMeetingResourceRoom room) {
        this.room = room;
    }


    @Override
    public BaseConferenceContext bookMeeting(String name, String cloudMcuType, int duration) {

        return  room.bookMeeting(name,cloudMcuType,duration);
    }
}

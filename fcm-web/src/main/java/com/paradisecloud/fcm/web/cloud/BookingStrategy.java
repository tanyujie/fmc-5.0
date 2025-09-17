package com.paradisecloud.fcm.web.cloud;

import com.paradisecloud.fcm.service.conference.BaseConferenceContext;

public interface BookingStrategy {
    BaseConferenceContext bookMeeting(String name,String cloudMcuType, int duration);
}

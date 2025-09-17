package com.paradisecloud.fcm.web.service.interfaces;

import com.paradisecloud.fcm.service.conference.BaseConferenceContext;

public interface CloudConferenceService {
    BaseConferenceContext startCloudConference(String conferenceName, String mcuType);
    BaseConferenceContext startCloudConference(String conferenceName,  String mcuType,String sn);
}

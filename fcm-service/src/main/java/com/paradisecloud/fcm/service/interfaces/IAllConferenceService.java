package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.fcm.service.conference.BaseConferenceContext;

public interface IAllConferenceService {

    void processAfterEndConference(BaseConferenceContext baseConferenceContext);
}

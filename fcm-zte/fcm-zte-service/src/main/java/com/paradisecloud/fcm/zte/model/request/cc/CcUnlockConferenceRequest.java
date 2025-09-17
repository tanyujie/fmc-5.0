package com.paradisecloud.fcm.zte.model.request.cc;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

/**
 * @author nj
 * @date 2024/4/10 10:39
 */
public class CcUnlockConferenceRequest extends CommonRequest {

    private String ConferenceIdentifier;

    public String getConferenceIdentifier() {
        return ConferenceIdentifier;
    }

    public void setConferenceIdentifier(String conferenceIdentifier) {
        ConferenceIdentifier = conferenceIdentifier;
    }


    @Override
    public String buildToXml() {
        return super.buildToXml();
    }
}

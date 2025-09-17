package com.paradisecloud.fcm.zte.model.request.cc;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

/**
 * @author nj
 * @date 2024/4/10 10:37
 */
public class CcLockConferenceRequest extends CommonRequest {


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

package com.paradisecloud.fcm.zte.model.request.cc;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

/**
 * @author nj
 * @date 2024/4/10 9:23
 */
public class CcDisconnectParticipantRequest extends CommonRequest {
    private String ConferenceIdentifier;
    private String TerminalIdentifier;


    public String getConferenceIdentifier() {
        return ConferenceIdentifier;
    }

    public void setConferenceIdentifier(String conferenceIdentifier) {
        ConferenceIdentifier = conferenceIdentifier;
    }

    public String getTerminalIdentifier() {
        return TerminalIdentifier;
    }

    public void setTerminalIdentifier(String terminalIdentifier) {
        TerminalIdentifier = terminalIdentifier;
    }

    @Override
    public String buildToXml() {
        String xml = "";
        return xml;
    }
}

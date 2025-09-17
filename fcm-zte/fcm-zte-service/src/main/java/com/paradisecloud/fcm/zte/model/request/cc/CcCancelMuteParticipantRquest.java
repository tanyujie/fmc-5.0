package com.paradisecloud.fcm.zte.model.request.cc;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

/**
 * @author nj
 * @date 2024/4/9 9:40
 */
public class CcCancelMuteParticipantRquest extends CommonRequest {

    private String conferenceIdentifier;
    private String terminalIdentifier;

    private int conferenceIdOption;

    private int terminalIdOption;

    public String getConferenceIdentifier() {
        return conferenceIdentifier;
    }

    public void setConferenceIdentifier(String conferenceIdentifier) {
        this.conferenceIdentifier = conferenceIdentifier;
    }

    public String getTerminalIdentifier() {
        return terminalIdentifier;
    }

    public void setTerminalIdentifier(String terminalIdentifier) {
        this.terminalIdentifier = terminalIdentifier;
    }

    public int getConferenceIdOption() {
        return conferenceIdOption;
    }

    public void setConferenceIdOption(int conferenceIdOption) {
        this.conferenceIdOption = conferenceIdOption;
    }

    public int getTerminalIdOption() {
        return terminalIdOption;
    }

    public void setTerminalIdOption(int terminalIdOption) {
        this.terminalIdOption = terminalIdOption;
    }
}

package com.paradisecloud.fcm.zte.model.request.cc;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

public class CcDeleteMrTerminalRequest extends CommonRequest {
    private String ConferenceIdentifier;
    private String TerminalIdentifier;
    private int TerminalIdOption;
    private int ConferenceIdOption;


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

    public int getTerminalIdOption() {
        return TerminalIdOption;
    }

    public void setTerminalIdOption(int terminalIdOption) {
        TerminalIdOption = terminalIdOption;
    }

    public int getConferenceIdOption() {
        return ConferenceIdOption;
    }

    public void setConferenceIdOption(int conferenceIdOption) {
        ConferenceIdOption = conferenceIdOption;
    }

    @Override
    public String buildToXml() {
        String xml = "";
        return xml;
    }
}

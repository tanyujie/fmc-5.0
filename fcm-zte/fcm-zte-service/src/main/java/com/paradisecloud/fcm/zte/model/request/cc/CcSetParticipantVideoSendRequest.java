package com.paradisecloud.fcm.zte.model.request.cc;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

/**
 * 是否允许会场发送主视频
 * @author nj
 * @date 2024/4/10 9:43
 */
public class CcSetParticipantVideoSendRequest extends CommonRequest {

    private String ConferenceIdentifier;
    private String TerminalIdentifier;
    private int Value;

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

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }
}

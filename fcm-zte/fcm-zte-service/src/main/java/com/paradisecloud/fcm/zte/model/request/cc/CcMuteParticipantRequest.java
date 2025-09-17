package com.paradisecloud.fcm.zte.model.request.cc;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

public class CcMuteParticipantRequest extends CommonRequest {

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

    /**
     *
     * <TRANS_CONF_2>
     *     <TRANS_COMMON_PARAMS>
     *         <MCU_TOKEN>{{mcu_token}}</MCU_TOKEN>
     *         <MCU_USER_TOKEN>{{mcu_user_token}}</MCU_USER_TOKEN>
     *         <ASYNC>
     *             <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *             <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         </ASYNC>
     *         <MESSAGE_ID>2</MESSAGE_ID>
     *     </TRANS_COMMON_PARAMS>
     *     <ACTION>
     *         <SET_AUDIO_VIDEO_MUTE>
     *             <ID>107</ID>
     *             <AUDIO_MUTE>true</AUDIO_MUTE>
     *             <VIDEO_MUTE>true</VIDEO_MUTE>
     *             <PARTY_ID>3</PARTY_ID>
     *         </SET_AUDIO_VIDEO_MUTE>
     *     </ACTION>
     * </TRANS_CONF_2>
     *
     * @return
     */

    @Override
    public String buildToXml() {
        String xml = "" ;

        return xml;
    }
}

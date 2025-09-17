package com.paradisecloud.fcm.mcu.plc.model.request.cc;

import com.paradisecloud.fcm.mcu.plc.model.request.CommonRequest;

public class CcUpdateTerminalAudioAndVideoRequest extends CommonRequest {
    private String id;
    private String party_id;
    private boolean audio_mute;
    private boolean video_mute;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParty_id() {
        return party_id;
    }

    public void setParty_id(String party_id) {
        this.party_id = party_id;
    }

    public boolean isAudio_mute() {
        return audio_mute;
    }

    public void setAudio_mute(boolean audio_mute) {
        this.audio_mute = audio_mute;
    }

    public boolean isVideo_mute() {
        return video_mute;
    }

    public void setVideo_mute(boolean video_mute) {
        this.video_mute = video_mute;
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
        String xml = "" +
                "<TRANS_CONF_2>" +
                "<TRANS_COMMON_PARAMS>" +
                "<MCU_TOKEN>" + mcuToken + "</MCU_TOKEN>" +
                "<MCU_USER_TOKEN>" + mcuUserToken + "</MCU_USER_TOKEN>" +
                "<ASYNC>" +
                "<YOUR_TOKEN1>" + yourToken1 + "</YOUR_TOKEN1>" +
                "<YOUR_TOKEN2>" + yourToken2 + "</YOUR_TOKEN2>" +
                "</ASYNC>" +
                "<MESSAGE_ID>" + messageId + "</MESSAGE_ID>" +
                "</TRANS_COMMON_PARAMS>" +
                "<ACTION>" +
                "<SET_AUDIO_VIDEO_MUTE>" +
                "<ID>" + id + "</ID>" +
                "<AUDIO_MUTE>" + audio_mute + "</AUDIO_MUTE>" +
                "<VIDEO_MUTE>" + video_mute + "</VIDEO_MUTE>" +
                "<PARTY_ID>" + party_id + "</PARTY_ID>" +
                "</SET_AUDIO_VIDEO_MUTE>" +
                "</ACTION>" +
                "</TRANS_CONF_2>";
        return xml;
    }
}

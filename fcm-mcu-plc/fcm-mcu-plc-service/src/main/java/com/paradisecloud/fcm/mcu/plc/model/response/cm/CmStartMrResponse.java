package com.paradisecloud.fcm.mcu.plc.model.response.cm;

import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import org.json.JSONObject;
import org.json.XML;

public class CmStartMrResponse extends CommonResponse {

    private String id;
    private String conferenceNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConferenceNum() {
        return conferenceNum;
    }

    public void setConferenceNum(String conferenceNum) {
        this.conferenceNum = conferenceNum;
    }

    /**
     * <RESPONSE_TRANS_RES>
     *     <RETURN_STATUS>
     *         <ID>0</ID>
     *         <DESCRIPTION>Status OK</DESCRIPTION>
     *         <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *         <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         <MESSAGE_ID>2</MESSAGE_ID>
     *         <DESCRIPTION_EX></DESCRIPTION_EX>
     *     </RETURN_STATUS>
     *     <ACTION>
     *         <START>
     *             <RESERVATION>
     *                 <OBJ_TOKEN>22</OBJ_TOKEN>
     *                 <CHANGED>true</CHANGED>
     *                 <NAME>tty_2</NAME>
     *                 <ID>98</ID>
     *                 <CORRELATION_ID>测试会议2-2023-07-06T00:55:09.0Z-00:E0:DB:30:E8:04</CORRELATION_ID>
     *                 <NETWORK>h323</NETWORK>
     *                 <MEDIA>video_audio</MEDIA>
     *                 <PASSWORD>22222</PASSWORD>
     *                 <VIDEO_SESSION>continuous_presence</VIDEO_SESSION>
     *                 <VIDEO_PROTOCOL>auto</VIDEO_PROTOCOL>
     *                 <TRANSFER_RATE>1920</TRANSFER_RATE>
     *                 <AUDIO_RATE>auto</AUDIO_RATE>
     *                 <VIDEO_FORMAT>auto</VIDEO_FORMAT>
     *                 <FRAME_RATE>auto</FRAME_RATE>
     *                 <EXTERNAL_IVR_CONTROL>false</EXTERNAL_IVR_CONTROL>
     *                 <ATTENDED_MODE>ivr</ATTENDED_MODE>
     *                 <AV_MSG>Conference IVR Service</AV_MSG>
     *                 <STAND_BY>false</STAND_BY>
     *                 <OPERATOR_CONF>false</OPERATOR_CONF>
     *                 <SAME_LAYOUT>false</SAME_LAYOUT>
     *                 <AUTO_TERMINATE>
     *                     <ON>true</ON>
     *                     <TIME_BEFORE_FIRST_JOIN>10</TIME_BEFORE_FIRST_JOIN>
     *                     <TIME_AFTER_LAST_QUIT>1</TIME_AFTER_LAST_QUIT>
     *                     <LAST_QUIT_TYPE>after_last_quit</LAST_QUIT_TYPE>
     *                 </AUTO_TERMINATE>
     *                 <AUDIO_MIX_DEPTH>5</AUDIO_MIX_DEPTH>
     *                 <TALK_HOLD_TIME>150</TALK_HOLD_TIME>
     *                 <MAX_PARTIES>automatic</MAX_PARTIES>
     *                 <MEET_ME_PER_CONF>
     *                     <ON>true</ON>
     *                     <AUTO_ADD>true</AUTO_ADD>
     *                     <MIN_NUM_OF_PARTIES>0</MIN_NUM_OF_PARTIES>
     *                     <MIN_NUM_OF_AUDIO_PARTIES>0</MIN_NUM_OF_AUDIO_PARTIES>
     *                     <SERVICE_NAME_FOR_MIN_PARTIES></SERVICE_NAME_FOR_MIN_PARTIES>
     *                 </MEET_ME_PER_CONF>
     *                 <H323_BIT_RATE>384</H323_BIT_RATE>
     *                 <LEADER_PASSWORD>22222</LEADER_PASSWORD>
     *                 <START_CONF_LEADER>false</START_CONF_LEADER>
     *                 <TERMINATE_AFTER_LEADER_EXIT>false</TERMINATE_AFTER_LEADER_EXIT>
     *                 <REPEATED_ID>0</REPEATED_ID>
     *                 <MEETING_ROOM>
     *                     <ON>false</ON>
     *                     <LIMITED_SEQ>off</LIMITED_SEQ>
     *                 </MEETING_ROOM>
     *                 <OVERRIDE_PROFILE_LAYOUT>false</OVERRIDE_PROFILE_LAYOUT>
     *                 <START_TIME>2023-07-06T00:55:09</START_TIME>
     *                 <DURATION>
     *                     <HOUR>1</HOUR>
     *                     <MINUTE>0</MINUTE>
     *                     <SECOND>0</SECOND>
     *                 </DURATION>
     *                 <CASCADE>
     *                     <CASCADE_ROLE>none</CASCADE_ROLE>
     *                     <CASCADED_LINKS_NUMBER>0</CASCADED_LINKS_NUMBER>
     *                 </CASCADE>
     *                 <MUTE_PARTIES_IN_LECTURE>false</MUTE_PARTIES_IN_LECTURE>
     *                 <MUTE_ALL_PARTIES_AUDIO_EXCEPT_LEADER>false</MUTE_ALL_PARTIES_AUDIO_EXCEPT_LEADER>
     *                 <MUTE_ALL_PARTIES_VIDEO_EXCEPT_LEADER>false</MUTE_ALL_PARTIES_VIDEO_EXCEPT_LEADER>
     *                 <LECTURE_MODE>
     *                     <ON>false</ON>
     *                     <TIMER>false</TIMER>
     *                     <INTERVAL>15</INTERVAL>
     *                     <AUDIO_ACTIVATED>false</AUDIO_ACTIVATED>
     *                     <LECTURE_NAME></LECTURE_NAME>
     *                     <LECTURE_MODE_TYPE>lecture_none</LECTURE_MODE_TYPE>
     *                     <LECTURE_ID>-1</LECTURE_ID>
     *                 </LECTURE_MODE>
     *                 <LAYOUT>1x1</LAYOUT>
     *                 <FORCE_LIST>
     *                     <FORCE>
     *                         <LAYOUT>1x1</LAYOUT>
     *                         <CELL>
     *                             <ID>1</ID>
     *                             <FORCE_STATE>auto</FORCE_STATE>
     *                             <SOURCE_ID>-1</SOURCE_ID>
     *                         </CELL>
     *                     </FORCE>
     *                 </FORCE_LIST>
     *                 <ENTRY_QUEUE>false</ENTRY_QUEUE>
     *                 <MEET_ME_PER_ENTRY_QUEUE>true</MEET_ME_PER_ENTRY_QUEUE>
     *                 <ENTRY_PASSWORD></ENTRY_PASSWORD>
     *                 <VIDEO_PLUS>true</VIDEO_PLUS>
     *                 <VISUAL_EFFECTS>
     *                     <BACKGROUND_COLOR>
     *                         <RED>0</RED>
     *                         <GREEN>0</GREEN>
     *                         <BLUE>0</BLUE>
     *                         <Y>0</Y>
     *                         <U>0</U>
     *                         <V>0</V>
     *                     </BACKGROUND_COLOR>
     *                     <LAYOUT_BORDER>true</LAYOUT_BORDER>
     *                     <LAYOUT_BORDER_COLOR>
     *                         <RED>0</RED>
     *                         <GREEN>0</GREEN>
     *                         <BLUE>0</BLUE>
     *                         <Y>48</Y>
     *                         <U>159</U>
     *                         <V>121</V>
     *                     </LAYOUT_BORDER_COLOR>
     *                     <SPEAKER_NOTATION>true</SPEAKER_NOTATION>
     *                     <SPEAKER_NOTATION_COLOR>
     *                         <RED>0</RED>
     *                         <GREEN>0</GREEN>
     *                         <BLUE>0</BLUE>
     *                         <Y>125</Y>
     *                         <U>83</U>
     *                         <V>196</V>
     *                     </SPEAKER_NOTATION_COLOR>
     *                     <IMAGE_ID>1</IMAGE_ID>
     *                     <USE_YUV>true</USE_YUV>
     *                 </VISUAL_EFFECTS>
     *                 <MESSAGE_OVERLAY>
     *                     <ON>false</ON>
     *                     <MESSAGE_TEXT></MESSAGE_TEXT>
     *                     <MESSAGE_FONT_SIZE>small</MESSAGE_FONT_SIZE>
     *                     <MESSAGE_FONT_SIZE_INT>12</MESSAGE_FONT_SIZE_INT>
     *                     <MESSAGE_COLOR>white_font_on_light_blue_background</MESSAGE_COLOR>
     *                     <NUM_OF_REPETITIONS>3</NUM_OF_REPETITIONS>
     *                     <MESSAGE_DISPLAY_SPEED>slow</MESSAGE_DISPLAY_SPEED>
     *                     <MESSAGE_DISPLAY_POSITION>bottom</MESSAGE_DISPLAY_POSITION>
     *                     <MESSAGE_DISPLAY_POSITION_INT>90</MESSAGE_DISPLAY_POSITION_INT>
     *                     <MESSAGE_TRANSPARENCE>50</MESSAGE_TRANSPARENCE>
     *                 </MESSAGE_OVERLAY>
     *                 <SITE_NAME>
     *                     <SITE_NAME_DISPLAY_MODE>Off</SITE_NAME_DISPLAY_MODE>
     *                     <SITE_NAME_FONT_SIZE>12</SITE_NAME_FONT_SIZE>
     *                     <SITE_NAME_COLOR>white_font_on_red_background</SITE_NAME_COLOR>
     *                     <SITE_NAME_DISPLAY_POSITION>Left Top</SITE_NAME_DISPLAY_POSITION>
     *                     <SITE_NAME_TRANSPARENCE>50</SITE_NAME_TRANSPARENCE>
     *                     <SITE_NAME_HORIZONTAL_POSITION>0</SITE_NAME_HORIZONTAL_POSITION>
     *                     <SITE_NAME_VERTICAL_POSITION>0</SITE_NAME_VERTICAL_POSITION>
     *                     <SITE_NAME_TEXT_COLOR>white_font</SITE_NAME_TEXT_COLOR>
     *                 </SITE_NAME>
     *                 <AUTO_SCAN_ORDER/>
     *                 <COP>false</COP>
     *                 <PROFILE>false</PROFILE>
     *                 <AD_HOC>false</AD_HOC>
     *                 <AD_HOC_PROFILE_ID>-1</AD_HOC_PROFILE_ID>
     *                 <BASE_PROFILE_NAME></BASE_PROFILE_NAME>
     *                 <AUTO_LAYOUT>true</AUTO_LAYOUT>
     *                 <LAYOUT_MODE>normal</LAYOUT_MODE>
     *                 <BILLING_DATA></BILLING_DATA>
     *                 <NUMERIC_ID>40409</NUMERIC_ID>
     *                 <CONTACT_INFO_LIST/>
     *                 <ENCRYPTION>false</ENCRYPTION>
     *                 <ENCRYPTION_TYPE>all_non_encrypted</ENCRYPTION_TYPE>
     *                 <VIDEO>true</VIDEO>
     *                 <QCIF_FRAME_RATE>auto</QCIF_FRAME_RATE>
     *                 <CONFERENCE_TYPE>standard</CONFERENCE_TYPE>
     *                 <ENABLE_RECORDING>false</ENABLE_RECORDING>
     *                 <ENABLE_RECORDING_INDICATION>false</ENABLE_RECORDING_INDICATION>
     *                 <START_REC_POLICY>immediately</START_REC_POLICY>
     *                 <REC_LINK_NAME></REC_LINK_NAME>
     *                 <VIDEO_QUALITY>sharpness</VIDEO_QUALITY>
     *                 <MAX_RESOLUTION>auto</MAX_RESOLUTION>
     *                 <ENTRY_QUEUE_TYPE>normal</ENTRY_QUEUE_TYPE>
     *                 <CASCADE_EQ>false</CASCADE_EQ>
     *                 <SIP_FACTORY>false</SIP_FACTORY>
     *                 <SIP_FACTORY_AUTO_CONNECT>false</SIP_FACTORY_AUTO_CONNECT>
     *                 <ENTERPRISE_MODE>Graphics</ENTERPRISE_MODE>
     *                 <ENTERPRISE_PROTOCOL>up_to_h.264</ENTERPRISE_PROTOCOL>
     *                 <HD>false</HD>
     *                 <TIP_COMPATIBILITY>none</TIP_COMPATIBILITY>
     *                 <NAT_KEEP_ALIVE_PERIOD>0</NAT_KEEP_ALIVE_PERIOD>
     *                 <NAT_KEEP_ALIVE_PERIOD>0</NAT_KEEP_ALIVE_PERIOD>
     *                 <AV_MCU_FOCUS_URI></AV_MCU_FOCUS_URI>
     *                 <MSFT_REAL_CONNECT>
     *                     <AV_MCU>
     *                         <AV_MCU_CONFERENCE_TYPE>ad_hoc</AV_MCU_CONFERENCE_TYPE>
     *                         <ORGANIZER_ORIGINAL_TO></ORGANIZER_ORIGINAL_TO>
     *                         <RPP_CONTACT></RPP_CONTACT>
     *                     </AV_MCU>
     *                     <CAA>
     *                         <URI></URI>
     *                         <CONFERENCE_ID></CONFERENCE_ID>
     *                     </CAA>
     *                 </MSFT_REAL_CONNECT>
     *                 <AV_MCU_CASCADE_MODE>resource_optimized</AV_MCU_CASCADE_MODE>
     *                 <SEND_RECORDING_ANNOUNCEMENT>true</SEND_RECORDING_ANNOUNCEMENT>
     *                 <RECORDING_SERVER_PLAYBACK_LAYOUT_MODE >Auto</RECORDING_SERVER_PLAYBACK_LAYOUT_MODE >
     *                 <IS_TELEPRESENCE_MODE>false</IS_TELEPRESENCE_MODE>
     *                 <DISPLAY_NAME>测试会议2</DISPLAY_NAME>
     *                 <AUDIO_ONLY_RECORDING>false</AUDIO_ONLY_RECORDING>
     *                 <LPR>false</LPR>
     *                 <CONFERENCE_TEMPLATE>
     *                     <ON>false</ON>
     *                 </CONFERENCE_TEMPLATE>
     *                 <HD_RESOLUTION>hd_720</HD_RESOLUTION>
     *                 <H264_VSW_HIGH_PROFILE_PREFERENCE>base_line_only</H264_VSW_HIGH_PROFILE_PREFERENCE>
     *                 <VIDEO_CLARITY>false</VIDEO_CLARITY>
     *                 <AUTO_REDIAL>false</AUTO_REDIAL>
     *                 <EXCLUSIVE_CONTENT_MODE>false</EXCLUSIVE_CONTENT_MODE>
     *                 <ENABLE_FECC>true</ENABLE_FECC>
     *                 <PERMANENT>false</PERMANENT>
     *                 <ECHO_SUPPRESSION>false</ECHO_SUPPRESSION>
     *                 <KEYBOARD_SUPPRESSION>false</KEYBOARD_SUPPRESSION>
     *                 <GATEWAY>false</GATEWAY>
     *                 <GW_DIAL_OUT_PROTOCOLS>
     *                     <H323>false</H323>
     *                     <SIP>false</SIP>
     *                     <H320>false</H320>
     *                     <PSTN>false</PSTN>
     *                 </GW_DIAL_OUT_PROTOCOLS>
     *                 <CONTENT_TO_LEGACY_EPS>true</CONTENT_TO_LEGACY_EPS>
     *                 <COP_CONFIGURATION_LIST>
     *                     <ASPECT_RATIO>16_9</ASPECT_RATIO>
     *                     <COP_CONFIGURATION>
     *                         <VIDEO_PROTOCOL>h264</VIDEO_PROTOCOL>
     *                         <COP_VIDEO_FORMAT>1080p</COP_VIDEO_FORMAT>
     *                         <FRAME_RATE>25</FRAME_RATE>
     *                         <TRANSFER_RATE>1728</TRANSFER_RATE>
     *                     </COP_CONFIGURATION>
     *                     <COP_CONFIGURATION>
     *                         <VIDEO_PROTOCOL>h264</VIDEO_PROTOCOL>
     *                         <COP_VIDEO_FORMAT>720p</COP_VIDEO_FORMAT>
     *                         <FRAME_RATE>25</FRAME_RATE>
     *                         <TRANSFER_RATE>832</TRANSFER_RATE>
     *                     </COP_CONFIGURATION>
     *                     <COP_CONFIGURATION>
     *                         <VIDEO_PROTOCOL>h264</VIDEO_PROTOCOL>
     *                         <COP_VIDEO_FORMAT>cif_sif</COP_VIDEO_FORMAT>
     *                         <FRAME_RATE>25</FRAME_RATE>
     *                         <TRANSFER_RATE>512</TRANSFER_RATE>
     *                     </COP_CONFIGURATION>
     *                     <COP_CONFIGURATION>
     *                         <VIDEO_PROTOCOL>h263</VIDEO_PROTOCOL>
     *                         <COP_VIDEO_FORMAT>cif_sif</COP_VIDEO_FORMAT>
     *                         <FRAME_RATE>25</FRAME_RATE>
     *                         <TRANSFER_RATE>384</TRANSFER_RATE>
     *                     </COP_CONFIGURATION>
     *                 </COP_CONFIGURATION_LIST>
     *                 <APPOINTMENT_ID></APPOINTMENT_ID>
     *                 <SITE_NAMES>true</SITE_NAMES>
     *                 <AUTO_SCAN_INTERVAL>10</AUTO_SCAN_INTERVAL>
     *                 <GATHERING>
     *                     <ENABLE_GATHERING>false</ENABLE_GATHERING>
     *                     <LANGUAGE>english</LANGUAGE>
     *                     <IP_NUMBER_ACCESS></IP_NUMBER_ACCESS>
     *                     <ACCESS_NUMBER_1></ACCESS_NUMBER_1>
     *                     <ACCESS_NUMBER_2></ACCESS_NUMBER_2>
     *                     <FREE_TEXT_1></FREE_TEXT_1>
     *                     <FREE_TEXT_2></FREE_TEXT_2>
     *                     <FREE_TEXT_3></FREE_TEXT_3>
     *                 </GATHERING>
     *                 <TELEPRESENCE_MODE_CONFIGURATION>no</TELEPRESENCE_MODE_CONFIGURATION>
     *                 <TELEPRESENCE_LAYOUT_MODE>manual</TELEPRESENCE_LAYOUT_MODE>
     *                 <CROPPING>true</CROPPING>
     *                 <RES_STATUS>ok</RES_STATUS>
     *                 <AUDIO_CLARITY>false</AUDIO_CLARITY>
     *                 <SPEAKER_CHANGE_THRESHOLD>Auto</SPEAKER_CHANGE_THRESHOLD>
     *                 <AUTO_BRIGHTNESS>false</AUTO_BRIGHTNESS>
     *                 <IVR_PROVIDER_EQ>false</IVR_PROVIDER_EQ>
     *                 <CHINESE_FONT>Default</CHINESE_FONT>
     *                 <SERVICE_REGISTRATION_LIST/>
     *                 <SIP_REGISTRATIONS_STATUS>not_configured</SIP_REGISTRATIONS_STATUS>
     *                 <CONF_MEDIA_TYPE>mix_avc_media_relay</CONF_MEDIA_TYPE>
     *                 <OPERATION_POINTS_PRESET>cif_optimized</OPERATION_POINTS_PRESET>
     *                 <MRC_MCU_ID>1</MRC_MCU_ID>
     *                 <MULTIPLE_RESOLUTION>false</MULTIPLE_RESOLUTION>
     *                 <CONTENT_TRANSCODING_H264>false</CONTENT_TRANSCODING_H264>
     *                 <CONTENT_TRANSCODING_H263>false</CONTENT_TRANSCODING_H263>
     *                 <H264_CASCADE_OPTIMIZED>false</H264_CASCADE_OPTIMIZED>
     *                 <AS_SIP_CONTENT>false</AS_SIP_CONTENT>
     *                 <AUTO_MUTE_NOISY_PARTIES>false</AUTO_MUTE_NOISY_PARTIES>
     *                 <HIGH_PROFILE_CONTENT>false</HIGH_PROFILE_CONTENT>
     *                 <LAYOUT_INDICATIONS>
     *                     <LAYOUT_INDICATIONS_POSITION>top_left</LAYOUT_INDICATIONS_POSITION>
     *                     <LAYOUT_INDICATIONS_AUDIO_PARTICIPANTS>true</LAYOUT_INDICATIONS_AUDIO_PARTICIPANTS>
     *                     <LAYOUT_INDICATIONS_VIDEO_PARTICIPANTS>true</LAYOUT_INDICATIONS_VIDEO_PARTICIPANTS>
     *                     <LAYOUT_INDICATIONS_AUDIO_PARTICIPANTS_DISPLAY_MODE>on_audio_participants_change</LAYOUT_INDICATIONS_AUDIO_PARTICIPANTS_DISPLAY_MODE>
     *                     <LAYOUT_INDICATIONS_AUDIO_PARTICIPANTS_DISPLAY_DURATION>10</LAYOUT_INDICATIONS_AUDIO_PARTICIPANTS_DISPLAY_DURATION>
     *                     <LAYOUT_SELF_INDICATIONS_NETWORK_QUALITY>true</LAYOUT_SELF_INDICATIONS_NETWORK_QUALITY>
     *                     <LAYOUT_INDICATIONS_PARTICIPANTS_JOINING_DISPLAY_NAME>true</LAYOUT_INDICATIONS_PARTICIPANTS_JOINING_DISPLAY_NAME>
     *                     <LAYOUT_INDICATIONS_PARTICIPANTS_LEAVING_DISPLAY_NAME>true</LAYOUT_INDICATIONS_PARTICIPANTS_LEAVING_DISPLAY_NAME>
     *                     <LAYOUT_INDICATIONS_PARTICIPANTS_DISPLAY_NAME_DURATION>10</LAYOUT_INDICATIONS_PARTICIPANTS_DISPLAY_NAME_DURATION>
     *                 </LAYOUT_INDICATIONS>
     *                 <CREATOR_URI></CREATOR_URI>
     *                 <PARTY_LIST/>
     *             </RESERVATION>
     *         </START>
     *     </ACTION>
     * </RESPONSE_TRANS_RES>
     *
     * @param xml
     */
    public CmStartMrResponse(String xml) {
        JSONObject jsonObject = XML.toJSONObject(xml);
        if (jsonObject != null) {
            try {
                JSONObject RESPONSE_TRANS_RES = jsonObject.getJSONObject("RESPONSE_TRANS_RES");
                JSONObject RETURN_STATUS = RESPONSE_TRANS_RES.getJSONObject("RETURN_STATUS");
                String DESCRIPTION = RETURN_STATUS.getString("DESCRIPTION");
                status = DESCRIPTION;
                if (STATUS_OK.equals(status)) {
                    JSONObject ACTION = RESPONSE_TRANS_RES.getJSONObject("ACTION");
                    JSONObject START = ACTION.getJSONObject("START");
                    JSONObject RESERVATION = START.getJSONObject("RESERVATION");
                    id = String.valueOf(RESERVATION.get("ID"));
                    conferenceNum = String.valueOf(RESERVATION.get("NUMERIC_ID"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

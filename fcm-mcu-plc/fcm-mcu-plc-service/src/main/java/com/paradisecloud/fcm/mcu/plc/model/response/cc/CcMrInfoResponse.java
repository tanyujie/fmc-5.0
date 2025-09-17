package com.paradisecloud.fcm.mcu.plc.model.response.cc;

import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.time.ZoneId;
import java.util.*;

public class CcMrInfoResponse extends CommonResponse {
    private String id;
    private String conferenceNum;
    private String audioSourceId;
    private List<TerminalInfo> connectedTerminalInfoList = new ArrayList<>();
    private List<TerminalInfo> disconnectedTerminalInfoList = new ArrayList<>();
    private Set<String> deletedTerminalIdSet = new HashSet<>();
    private boolean muteParties;

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

    public String getAudioSourceId() {
        return audioSourceId;
    }

    public void setAudioSourceId(String audioSourceId) {
        this.audioSourceId = audioSourceId;
    }

    public List<TerminalInfo> getConnectedTerminalInfoList() {
        return connectedTerminalInfoList;
    }

    public void setConnectedTerminalInfoList(List<TerminalInfo> connectedTerminalInfoList) {
        this.connectedTerminalInfoList = connectedTerminalInfoList;
    }

    public List<TerminalInfo> getDisconnectedTerminalInfoList() {
        return disconnectedTerminalInfoList;
    }

    public void setDisconnectedTerminalInfoList(List<TerminalInfo> disconnectedTerminalInfoList) {
        this.disconnectedTerminalInfoList = disconnectedTerminalInfoList;
    }

    public Set<String> getDeletedTerminalIdSet() {
        return deletedTerminalIdSet;
    }

    public void setDeletedTerminalIdSet(Set<String> deletedTerminalIdSet) {
        this.deletedTerminalIdSet = deletedTerminalIdSet;
    }

    public boolean isMuteParties() {
        return muteParties;
    }

    public void setMuteParties(boolean muteParties) {
        this.muteParties = muteParties;
    }

    public static class TerminalInfo {
        private String id;
        private String name;
        private Integer status;
        private String protocol;
        // dial_out:呼出     dial_int:呼入
        private String callingMode;
        private String remoteAddress;
        private String ip;
        private String layoutType;
        private boolean audioMute;
        private boolean videoMute;
        private boolean contentProvider;
        private Date connectTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getCallingMode() {
            return callingMode;
        }

        public void setCallingMode(String callingMode) {
            this.callingMode = callingMode;
        }

        public String getRemoteAddress() {
            return remoteAddress;
        }

        public void setRemoteAddress(String remoteAddress) {
            this.remoteAddress = remoteAddress;
        }

        public String getLayoutType() {
            return layoutType;
        }

        public void setLayoutType(String layoutType) {
            this.layoutType = layoutType;
        }

        public boolean isAudioMute() {
            return audioMute;
        }

        public void setAudioMute(boolean audioMute) {
            this.audioMute = audioMute;
        }

        public boolean isVideoMute() {
            return videoMute;
        }

        public void setVideoMute(boolean videoMute) {
            this.videoMute = videoMute;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public boolean isContentProvider() {
            return contentProvider;
        }

        public void setContentProvider(boolean contentProvider) {
            this.contentProvider = contentProvider;
        }

        public Date getConnectTime() {
            return connectTime;
        }

        public void setConnectTime(Date connectTime) {
            this.connectTime = connectTime;
        }
    }


    /**
     * <RESPONSE_TRANS_CONF>
     *     <RETURN_STATUS>
     *         <ID>0</ID>
     *         <DESCRIPTION>Status OK</DESCRIPTION>
     *         <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *         <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         <MESSAGE_ID>2</MESSAGE_ID>
     *         <DESCRIPTION_EX></DESCRIPTION_EX>
     *     </RETURN_STATUS>
     *     <ACTION>
     *         <GET>
     *             <CONFERENCE>
     *                 <OBJ_TOKEN>12513</OBJ_TOKEN>
     *                 <CHANGED>true</CHANGED>
     *                 <RESERVATION>
     *                     <NAME>lxl_874449881</NAME>
     *                     <ID>100</ID>
     *                     <CORRELATION_ID>lxl_874449881-2023-07-06T01:00:58.0Z-00:E0:DB:30:E8:04</CORRELATION_ID>
     *                     <PASSWORD>847111</PASSWORD>
     *                     <START_TIME>2023-07-06T01:00:58</START_TIME>
     *                     <DURATION>
     *                         <HOUR>1</HOUR>
     *                         <MINUTE>10</MINUTE>
     *                         <SECOND>0</SECOND>
     *                     </DURATION>
     *                     <CASCADE>
     *                         <CASCADE_ROLE>none</CASCADE_ROLE>
     *                         <CASCADED_LINKS_NUMBER>0</CASCADED_LINKS_NUMBER>
     *                     </CASCADE>
     *                     <MUTE_PARTIES_IN_LECTURE>false</MUTE_PARTIES_IN_LECTURE>
     *                     <MUTE_ALL_PARTIES_AUDIO_EXCEPT_LEADER>false</MUTE_ALL_PARTIES_AUDIO_EXCEPT_LEADER>
     *                     <MUTE_ALL_PARTIES_VIDEO_EXCEPT_LEADER>false</MUTE_ALL_PARTIES_VIDEO_EXCEPT_LEADER>
     *                     <LECTURE_MODE>
     *                         <ON>false</ON>
     *                         <TIMER>false</TIMER>
     *                         <INTERVAL>15</INTERVAL>
     *                         <AUDIO_ACTIVATED>false</AUDIO_ACTIVATED>
     *                         <LECTURE_NAME></LECTURE_NAME>
     *                         <LECTURE_MODE_TYPE>lecture_none</LECTURE_MODE_TYPE>
     *                         <LECTURE_ID>-1</LECTURE_ID>
     *                     </LECTURE_MODE>
     *                     <LAYOUT>1x1</LAYOUT>
     *                     <FORCE_LIST>
     *                         <FORCE>
     *                             <LAYOUT>1x1</LAYOUT>
     *                             <CELL>
     *                                 <ID>1</ID>
     *                                 <FORCE_STATE>auto</FORCE_STATE>
     *                             </CELL>
     *                         </FORCE>
     *                     </FORCE_LIST>
     *                     <ENTRY_PASSWORD></ENTRY_PASSWORD>
     *                     <VISUAL_EFFECTS>
     *                         <BACKGROUND_COLOR>
     *                             <RED>0</RED>
     *                             <GREEN>0</GREEN>
     *                             <BLUE>0</BLUE>
     *                             <Y>0</Y>
     *                             <U>0</U>
     *                             <V>0</V>
     *                         </BACKGROUND_COLOR>
     *                         <LAYOUT_BORDER>true</LAYOUT_BORDER>
     *                         <LAYOUT_BORDER_COLOR>
     *                             <RED>0</RED>
     *                             <GREEN>0</GREEN>
     *                             <BLUE>0</BLUE>
     *                             <Y>48</Y>
     *                             <U>159</U>
     *                             <V>121</V>
     *                         </LAYOUT_BORDER_COLOR>
     *                         <SPEAKER_NOTATION>true</SPEAKER_NOTATION>
     *                         <SPEAKER_NOTATION_COLOR>
     *                             <RED>0</RED>
     *                             <GREEN>0</GREEN>
     *                             <BLUE>0</BLUE>
     *                             <Y>125</Y>
     *                             <U>83</U>
     *                             <V>196</V>
     *                         </SPEAKER_NOTATION_COLOR>
     *                         <IMAGE_ID>1</IMAGE_ID>
     *                         <USE_YUV>true</USE_YUV>
     *                     </VISUAL_EFFECTS>
     *                     <MESSAGE_OVERLAY>
     *                         <ON>false</ON>
     *                         <MESSAGE_TEXT></MESSAGE_TEXT>
     *                         <MESSAGE_FONT_SIZE>small</MESSAGE_FONT_SIZE>
     *                         <MESSAGE_FONT_SIZE_INT>12</MESSAGE_FONT_SIZE_INT>
     *                         <MESSAGE_COLOR>white_font_on_light_blue_background</MESSAGE_COLOR>
     *                         <NUM_OF_REPETITIONS>3</NUM_OF_REPETITIONS>
     *                         <MESSAGE_DISPLAY_SPEED>slow</MESSAGE_DISPLAY_SPEED>
     *                         <MESSAGE_DISPLAY_POSITION>bottom</MESSAGE_DISPLAY_POSITION>
     *                         <MESSAGE_DISPLAY_POSITION_INT>90</MESSAGE_DISPLAY_POSITION_INT>
     *                         <MESSAGE_TRANSPARENCE>50</MESSAGE_TRANSPARENCE>
     *                     </MESSAGE_OVERLAY>
     *                     <SITE_NAME>
     *                         <SITE_NAME_DISPLAY_MODE>Off</SITE_NAME_DISPLAY_MODE>
     *                         <SITE_NAME_FONT_SIZE>12</SITE_NAME_FONT_SIZE>
     *                         <SITE_NAME_COLOR>white_font_on_red_background</SITE_NAME_COLOR>
     *                         <SITE_NAME_DISPLAY_POSITION>Left Top</SITE_NAME_DISPLAY_POSITION>
     *                         <SITE_NAME_TRANSPARENCE>50</SITE_NAME_TRANSPARENCE>
     *                         <SITE_NAME_HORIZONTAL_POSITION>0</SITE_NAME_HORIZONTAL_POSITION>
     *                         <SITE_NAME_VERTICAL_POSITION>0</SITE_NAME_VERTICAL_POSITION>
     *                         <SITE_NAME_TEXT_COLOR>white_font</SITE_NAME_TEXT_COLOR>
     *                     </SITE_NAME>
     *                     <AUTO_SCAN_ORDER/>
     *                     <AD_HOC>false</AD_HOC>
     *                     <AD_HOC_PROFILE_ID>1</AD_HOC_PROFILE_ID>
     *                     <BASE_PROFILE_NAME></BASE_PROFILE_NAME>
     *                     <AUTO_LAYOUT>true</AUTO_LAYOUT>
     *                     <LAYOUT_MODE>normal</LAYOUT_MODE>
     *                     <BILLING_DATA></BILLING_DATA>
     *                     <NUMERIC_ID>85533</NUMERIC_ID>
     *                     <CONTACT_INFO_LIST/>
     *                     <IS_TELEPRESENCE_MODE>false</IS_TELEPRESENCE_MODE>
     *                     <VIDEO_CLARITY>false</VIDEO_CLARITY>
     *                     <AUTO_REDIAL>false</AUTO_REDIAL>
     *                     <EXCLUSIVE_CONTENT_MODE>false</EXCLUSIVE_CONTENT_MODE>
     *                     <ENABLE_FECC>true</ENABLE_FECC>
     *                     <AUTO_SCAN_INTERVAL>10</AUTO_SCAN_INTERVAL>
     *                     <CHINESE_FONT>Default</CHINESE_FONT>
     *                     <SERVICE_REGISTRATION_LIST/>
     *                     <SIP_REGISTRATIONS_STATUS>not_configured</SIP_REGISTRATIONS_STATUS>
     *                     <MULTIPLE_RESOLUTION>false</MULTIPLE_RESOLUTION>
     *                     <CONTENT_TRANSCODING_H264>false</CONTENT_TRANSCODING_H264>
     *                     <CONTENT_TRANSCODING_H263>false</CONTENT_TRANSCODING_H263>
     *                     <H264_CASCADE_OPTIMIZED>false</H264_CASCADE_OPTIMIZED>
     *                     <AS_SIP_CONTENT>false</AS_SIP_CONTENT>
     *                 </RESERVATION>
     *                 <CONF_STATUS>
     *                     <CONF_OK>true</CONF_OK>
     *                     <CONF_EMPTY>false</CONF_EMPTY>
     *                     <SINGLE_PARTY>false</SINGLE_PARTY>
     *                     <NOT_FULL>false</NOT_FULL>
     *                     <RESOURCES_DEFICIENCY>false</RESOURCES_DEFICIENCY>
     *                     <BAD_RESOURCES>false</BAD_RESOURCES>
     *                     <PROBLEM_PARTY>false</PROBLEM_PARTY>
     *                     <PARTY_REQUIRES_OPERATOR_ASSIST>false</PARTY_REQUIRES_OPERATOR_ASSIST>
     *                     <CONTENT_RESOURCES_DEFICIENCY>false</CONTENT_RESOURCES_DEFICIENCY>
     *                 </CONF_STATUS>
     *                 <AUDIO_SOURCE_ID>5</AUDIO_SOURCE_ID>
     *                 <LSD_SOURCE_ID>-1</LSD_SOURCE_ID>
     *                 <NUM_CONNECTED_PARTIES>2</NUM_CONNECTED_PARTIES>
     *                 <SECURE>false</SECURE>
     *                 <EPC_CONTENT_SOURCE_ID>-1</EPC_CONTENT_SOURCE_ID>
     *                 <RECORDING_STATUS>stop</RECORDING_STATUS>
     *                 <SIP_REGISTRATIONS_STATUS>not_configured</SIP_REGISTRATIONS_STATUS>
     *                 <MANAGE_TELEPRESENCE_LAYOUTS_INTERNALLY>false</MANAGE_TELEPRESENCE_LAYOUTS_INTERNALLY>
     *                 <ONGOING_PARTY_LIST>
     *                     <ONGOING_PARTY>
     *                         <ONGOING_PARTY_CHANGE>new</ONGOING_PARTY_CHANGE>
     *                         <PARTY>
     *                             <NAME>lxl_874449881_(001)</NAME>
     *                             <ID>2</ID>
     *                             <PHONE_LIST/>
     *                             <INTERFACE>sip</INTERFACE>
     *                             <CONNECTION>dial_in</CONNECTION>
     *                             <MEET_ME_METHOD>party</MEET_ME_METHOD>
     *                             <NUM_TYPE>taken_from_service</NUM_TYPE>
     *                             <BONDING>disabled</BONDING>
     *                             <MULTI_RATE>auto</MULTI_RATE>
     *                             <NET_CHANNEL_NUMBER>auto</NET_CHANNEL_NUMBER>
     *                             <VIDEO_PROTOCOL>auto</VIDEO_PROTOCOL>
     *                             <CALL_CONTENT>framed</CALL_CONTENT>
     *                             <ALIAS>
     *                                 <NAME></NAME>
     *                                 <ALIAS_TYPE>323_id</ALIAS_TYPE>
     *                             </ALIAS>
     *                             <IP>0.0.0.0</IP>
     *                             <SIGNALING_PORT>5060</SIGNALING_PORT>
     *                             <VOLUME>5</VOLUME>
     *                             <MCU_PHONE_LIST/>
     *                             <BONDING_PHONE></BONDING_PHONE>
     *                             <SERVICE_NAME>IP Network Service</SERVICE_NAME>
     *                             <AUTO_DETECT>false</AUTO_DETECT>
     *                             <RESTRICT>false</RESTRICT>
     *                             <VIDEO_BIT_RATE>automatic</VIDEO_BIT_RATE>
     *                             <LAYOUT_TYPE>personal</LAYOUT_TYPE>
     *                             <PERSONAL_LAYOUT>2x2</PERSONAL_LAYOUT>
     *                             <PERSONAL_FORCE_LIST>
     *                                 <FORCE>
     *                                     <LAYOUT>2x2</LAYOUT>
     *                                     <CELL>
     *                                         <ID>1</ID>
     *                                         <FORCE_STATE>auto</FORCE_STATE>
     *                                         <SOURCE_ID>5</SOURCE_ID>
     *                                     </CELL>
     *                                     <CELL>
     *                                         <ID>2</ID>
     *                                         <FORCE_STATE>auto</FORCE_STATE>
     *                                         <SOURCE_ID>-1</SOURCE_ID>
     *                                     </CELL>
     *                                     <CELL>
     *                                         <ID>3</ID>
     *                                         <FORCE_STATE>auto</FORCE_STATE>
     *                                         <SOURCE_ID>-1</SOURCE_ID>
     *                                     </CELL>
     *                                     <CELL>
     *                                         <ID>4</ID>
     *                                         <FORCE_STATE>auto</FORCE_STATE>
     *                                         <SOURCE_ID>-1</SOURCE_ID>
     *                                     </CELL>
     *                                 </FORCE>
     *                             </PERSONAL_FORCE_LIST>
     *                             <FORCE>
     *                                 <LAYOUT>1x1</LAYOUT>
     *                             </FORCE>
     *                             <VIP>false</VIP>
     *                             <CONTACT_INFO_LIST>
     *                                 <ADDITIONAL_INFO>172.16.100.232</ADDITIONAL_INFO>
     *                             </CONTACT_INFO_LIST>
     *                             <LISTEN_VOLUME>5</LISTEN_VOLUME>
     *                             <AGC>true</AGC>
     *                             <SIP_ADDRESS>40031@172.16.100.232</SIP_ADDRESS>
     *                             <SIP_ADDRESS_TYPE>uri_type</SIP_ADDRESS_TYPE>
     *                             <UNDEFINED>true</UNDEFINED>
     *                             <NODE_TYPE>terminal</NODE_TYPE>
     *                             <ENCRYPTION_EX>no</ENCRYPTION_EX>
     *                             <IS_RECORDING_LINK_PARTY>false</IS_RECORDING_LINK_PARTY>
     *                             <USER_IDENTIFIER_STRING></USER_IDENTIFIER_STRING>
     *                             <IDENTIFICATION_METHOD>called_phone_number</IDENTIFICATION_METHOD>
     *                             <MAX_RESOLUTION>auto</MAX_RESOLUTION>
     *                             <CASCADE>
     *                                 <CASCADE_ROLE>none</CASCADE_ROLE>
     *                                 <CASCADED_LINKS_NUMBER>0</CASCADED_LINKS_NUMBER>
     *                                 <LINK_TYPE>regular</LINK_TYPE>
     *                             </CASCADE>
     *                             <IP_V6>::</IP_V6>
     *                             <TELEPRESENCE_MODE>none</TELEPRESENCE_MODE>
     *                             <SUB_IP_SERVICE>primary</SUB_IP_SERVICE>
     *                             <ENDPOINT_MEDIA_TYPE>avc</ENDPOINT_MEDIA_TYPE>
     *                         </PARTY>
     *                         <L_SYNC_LOSS>0</L_SYNC_LOSS>
     *                         <R_SYNC_LOSS>0</R_SYNC_LOSS>
     *                         <L_VIDEO_SYNC_LOSS>1</L_VIDEO_SYNC_LOSS>
     *                         <R_VIDEO_SYNC_LOSS>0</R_VIDEO_SYNC_LOSS>
     *                         <H323_VIDEO_INTRA_SYNC>false</H323_VIDEO_INTRA_SYNC>
     *                         <H323_SYNC>0</H323_SYNC>
     *                         <ONGOING_PARTY_STATUS>
     *                             <ID>1</ID>
     *                             <DESCRIPTION>connected</DESCRIPTION>
     *                         </ONGOING_PARTY_STATUS>
     *                         <DISCONNECTION_CAUSE>
     *                             <ID>0</ID>
     *                             <DESCRIPTION>None</DESCRIPTION>
     *                             <DESCRIPTION_EX>0</DESCRIPTION_EX>
     *                         </DISCONNECTION_CAUSE>
     *                         <DISCONNECTING_OPERATOR></DISCONNECTING_OPERATOR>
     *                         <CONNECT_RETRY>0</CONNECT_RETRY>
     *                         <CONNECT_TIME>2023-07-06T01:07:08</CONNECT_TIME>
     *                         <AUDIO_MUTE>true</AUDIO_MUTE>
     *                         <AUDIO_SELF_MUTE>false</AUDIO_SELF_MUTE>
     *                         <AUDIO_MCU_MUTE>false</AUDIO_MCU_MUTE>
     *                         <AUDIO_BLOCK>false</AUDIO_BLOCK>
     *                         <VIDEO_MUTE>true</VIDEO_MUTE>
     *                         <VIDEO_SELF_MUTE>false</VIDEO_SELF_MUTE>
     *                         <VIDEO_MCU_MUTE>false</VIDEO_MCU_MUTE>
     *                         <ATTENDING_STATE>inconf</ATTENDING_STATE>
     *                         <OPERATOR_PARTY>false</OPERATOR_PARTY>
     *                         <AUTO_ADD>true</AUTO_ADD>
     *                         <CHANNELS/>
     *                         <ACTUAL_MCU_PHONES/>
     *                         <ACTUAL_PARTY_PHONES/>
     *                         <AUDIO_MEMBER>true</AUDIO_MEMBER>
     *                         <VIDEO_MEMBER>true</VIDEO_MEMBER>
     *                         <LEADER>false</LEADER>
     *                         <WAIT_FOR_ASSISTANCE>assitance_type_none</WAIT_FOR_ASSISTANCE>
     *                         <SRC_TYPE_IS_EQ>false</SRC_TYPE_IS_EQ>
     *                         <FORCE>
     *                             <LAYOUT>2x2</LAYOUT>
     *                             <CELL>
     *                                 <ID>1</ID>
     *                                 <FORCE_STATE>auto</FORCE_STATE>
     *                                 <SOURCE_ID>5</SOURCE_ID>
     *                             </CELL>
     *                             <CELL>
     *                                 <ID>2</ID>
     *                                 <FORCE_STATE>auto</FORCE_STATE>
     *                                 <SOURCE_ID>-1</SOURCE_ID>
     *                             </CELL>
     *                             <CELL>
     *                                 <ID>3</ID>
     *                                 <FORCE_STATE>auto</FORCE_STATE>
     *                                 <SOURCE_ID>-1</SOURCE_ID>
     *                             </CELL>
     *                             <CELL>
     *                                 <ID>4</ID>
     *                                 <FORCE_STATE>auto</FORCE_STATE>
     *                                 <SOURCE_ID>-1</SOURCE_ID>
     *                             </CELL>
     *                         </FORCE>
     *                         <GK_STATUS>
     *                             <GK_STATE>arq</GK_STATE>
     *                             <ALLOC_BANDWIDTH>0</ALLOC_BANDWIDTH>
     *                             <REQ_BANDWIDTH>0</REQ_BANDWIDTH>
     *                             <REQ_INTERVAL>0</REQ_INTERVAL>
     *                             <GK_ROUTED>false</GK_ROUTED>
     *                         </GK_STATUS>
     *                         <CAC_STATUS>
     *                             <ALLOC_BANDWIDTH>-1</ALLOC_BANDWIDTH>
     *                             <REQ_BANDWIDTH>-1</REQ_BANDWIDTH>
     *                         </CAC_STATUS>
     *                         <TX_BAUD_RATE>1920000</TX_BAUD_RATE>
     *                         <RX_BAUD_RATE>1920000</RX_BAUD_RATE>
     *                         <NOISY>false</NOISY>
     *                         <CONTENT_MEMBER>false</CONTENT_MEMBER>
     *                         <CONTENT_PROVIDER>false</CONTENT_PROVIDER>
     *                         <VISUAL_NAME>话机</VISUAL_NAME>
     *                         <SECONDARY_CAUSE_PARAMS>
     *                             <CAP_CODE>g711Alaw64k</CAP_CODE>
     *                             <FRAME_RATE_VALUE>0</FRAME_RATE_VALUE>
     *                             <LINE_RATE_VALUE>0</LINE_RATE_VALUE>
     *                             <VIDEO_RESOLUTION>qcif</VIDEO_RESOLUTION>
     *                             <SECONDARY_PROBLEM>unknown</SECONDARY_PROBLEM>
     *                             <SECONDARY_PROBLEM_VALUE>0</SECONDARY_PROBLEM_VALUE>
     *                             <REMOTE_SECONDARY_PROBLEM_VALUE>0</REMOTE_SECONDARY_PROBLEM_VALUE>
     *                             <SIP_CONFERENCING_LIMITATION>unknown</SIP_CONFERENCING_LIMITATION>
     *                         </SECONDARY_CAUSE_PARAMS>
     *                         <PARTY_CHANGE_TYPE>party_new_info</PARTY_CHANGE_TYPE>
     *                         <IS_RV_SYNC_LOSS>false</IS_RV_SYNC_LOSS>
     *                         <IS_LV_SYNC_LOSS>false</IS_LV_SYNC_LOSS>
     *                         <PARTY_CONF_FORCE>
     *                             <LAYOUT>1x1</LAYOUT>
     *                             <CELL>
     *                                 <ID>1</ID>
     *                                 <FORCE_STATE>auto</FORCE_STATE>
     *                                 <SOURCE_ID>1</SOURCE_ID>
     *                             </CELL>
     *                         </PARTY_CONF_FORCE>
     *                         <IS_CURRENTLY_ENCRYPTED>false</IS_CURRENTLY_ENCRYPTED>
     *                         <R_LPR_ACTIVATION>false</R_LPR_ACTIVATION>
     *                         <L_LPR_ACTIVATION>false</L_LPR_ACTIVATION>
     *                         <IS_VALID_HOME_CONF>true</IS_VALID_HOME_CONF>
     *                         <IS_EXCLUSIVE_CONTENT>false</IS_EXCLUSIVE_CONTENT>
     *                         <REQUEST_TO_SPEAK>false</REQUEST_TO_SPEAK>
     *                         <IS_EVENT_MODE_INTRA_SUPPRESSED>false</IS_EVENT_MODE_INTRA_SUPPRESSED>
     *                         <EVENT_MODE_LEVEL>0</EVENT_MODE_LEVEL>
     *                         <LPR_HEADERS_ACTIVATION>false</LPR_HEADERS_ACTIVATION>
     *                         <TIP_MODE>none</TIP_MODE>
     *                     </ONGOING_PARTY>
     *                     <ONGOING_PARTY>
     *                         <ONGOING_PARTY_CHANGE>new</ONGOING_PARTY_CHANGE>
     *                         <PARTY>
     *                             <NAME>lxl_874449881_(002)</NAME>
     *                             <ID>5</ID>
     *                             <PHONE_LIST/>
     *                             <INTERFACE>sip</INTERFACE>
     *                             <CONNECTION>dial_in</CONNECTION>
     *                             <MEET_ME_METHOD>party</MEET_ME_METHOD>
     *                             <NUM_TYPE>taken_from_service</NUM_TYPE>
     *                             <BONDING>disabled</BONDING>
     *                             <MULTI_RATE>auto</MULTI_RATE>
     *                             <NET_CHANNEL_NUMBER>auto</NET_CHANNEL_NUMBER>
     *                             <VIDEO_PROTOCOL>auto</VIDEO_PROTOCOL>
     *                             <CALL_CONTENT>framed</CALL_CONTENT>
     *                             <ALIAS>
     *                                 <NAME></NAME>
     *                                 <ALIAS_TYPE>323_id</ALIAS_TYPE>
     *                             </ALIAS>
     *                             <IP>0.0.0.0</IP>
     *                             <SIGNALING_PORT>5060</SIGNALING_PORT>
     *                             <VOLUME>5</VOLUME>
     *                             <MCU_PHONE_LIST/>
     *                             <BONDING_PHONE></BONDING_PHONE>
     *                             <SERVICE_NAME>IP Network Service</SERVICE_NAME>
     *                             <AUTO_DETECT>false</AUTO_DETECT>
     *                             <RESTRICT>false</RESTRICT>
     *                             <VIDEO_BIT_RATE>automatic</VIDEO_BIT_RATE>
     *                             <LAYOUT_TYPE>conference</LAYOUT_TYPE>
     *                             <PERSONAL_LAYOUT>1x1</PERSONAL_LAYOUT>
     *                             <PERSONAL_FORCE_LIST/>
     *                             <FORCE>
     *                                 <LAYOUT>1x1</LAYOUT>
     *                             </FORCE>
     *                             <VIP>false</VIP>
     *                             <CONTACT_INFO_LIST/>
     *                             <LISTEN_VOLUME>5</LISTEN_VOLUME>
     *                             <AGC>true</AGC>
     *                             <SIP_ADDRESS>172.16.100.66</SIP_ADDRESS>
     *                             <SIP_ADDRESS_TYPE>uri_type</SIP_ADDRESS_TYPE>
     *                             <UNDEFINED>true</UNDEFINED>
     *                             <NODE_TYPE>terminal</NODE_TYPE>
     *                             <ENCRYPTION_EX>no</ENCRYPTION_EX>
     *                             <IS_RECORDING_LINK_PARTY>false</IS_RECORDING_LINK_PARTY>
     *                             <USER_IDENTIFIER_STRING></USER_IDENTIFIER_STRING>
     *                             <IDENTIFICATION_METHOD>called_phone_number</IDENTIFICATION_METHOD>
     *                             <MAX_RESOLUTION>auto</MAX_RESOLUTION>
     *                             <CASCADE>
     *                                 <CASCADE_ROLE>none</CASCADE_ROLE>
     *                                 <CASCADED_LINKS_NUMBER>0</CASCADED_LINKS_NUMBER>
     *                                 <LINK_TYPE>regular</LINK_TYPE>
     *                             </CASCADE>
     *                             <IP_V6>::</IP_V6>
     *                             <TELEPRESENCE_MODE>none</TELEPRESENCE_MODE>
     *                             <SUB_IP_SERVICE>primary</SUB_IP_SERVICE>
     *                             <ENDPOINT_MEDIA_TYPE>avc</ENDPOINT_MEDIA_TYPE>
     *                         </PARTY>
     *                         <L_SYNC_LOSS>0</L_SYNC_LOSS>
     *                         <R_SYNC_LOSS>0</R_SYNC_LOSS>
     *                         <L_VIDEO_SYNC_LOSS>1</L_VIDEO_SYNC_LOSS>
     *                         <R_VIDEO_SYNC_LOSS>0</R_VIDEO_SYNC_LOSS>
     *                         <H323_VIDEO_INTRA_SYNC>false</H323_VIDEO_INTRA_SYNC>
     *                         <H323_SYNC>0</H323_SYNC>
     *                         <ONGOING_PARTY_STATUS>
     *                             <ID>1</ID>
     *                             <DESCRIPTION>connected</DESCRIPTION>
     *                         </ONGOING_PARTY_STATUS>
     *                         <DISCONNECTION_CAUSE>
     *                             <ID>0</ID>
     *                             <DESCRIPTION>None</DESCRIPTION>
     *                             <DESCRIPTION_EX>0</DESCRIPTION_EX>
     *                         </DISCONNECTION_CAUSE>
     *                         <DISCONNECTING_OPERATOR></DISCONNECTING_OPERATOR>
     *                         <CONNECT_RETRY>0</CONNECT_RETRY>
     *                         <CONNECT_TIME>2023-07-06T01:41:13</CONNECT_TIME>
     *                         <AUDIO_MUTE>false</AUDIO_MUTE>
     *                         <AUDIO_SELF_MUTE>false</AUDIO_SELF_MUTE>
     *                         <AUDIO_MCU_MUTE>false</AUDIO_MCU_MUTE>
     *                         <AUDIO_BLOCK>false</AUDIO_BLOCK>
     *                         <VIDEO_MUTE>false</VIDEO_MUTE>
     *                         <VIDEO_SELF_MUTE>false</VIDEO_SELF_MUTE>
     *                         <VIDEO_MCU_MUTE>false</VIDEO_MCU_MUTE>
     *                         <ATTENDING_STATE>inconf</ATTENDING_STATE>
     *                         <OPERATOR_PARTY>false</OPERATOR_PARTY>
     *                         <AUTO_ADD>true</AUTO_ADD>
     *                         <CHANNELS/>
     *                         <ACTUAL_MCU_PHONES/>
     *                         <ACTUAL_PARTY_PHONES/>
     *                         <AUDIO_MEMBER>true</AUDIO_MEMBER>
     *                         <VIDEO_MEMBER>true</VIDEO_MEMBER>
     *                         <LEADER>false</LEADER>
     *                         <WAIT_FOR_ASSISTANCE>assitance_type_none</WAIT_FOR_ASSISTANCE>
     *                         <SRC_TYPE_IS_EQ>false</SRC_TYPE_IS_EQ>
     *                         <FORCE>
     *                             <LAYOUT>1x1</LAYOUT>
     *                             <CELL>
     *                                 <ID>1</ID>
     *                                 <FORCE_STATE>auto</FORCE_STATE>
     *                                 <SOURCE_ID>5</SOURCE_ID>
     *                             </CELL>
     *                         </FORCE>
     *                         <GK_STATUS>
     *                             <GK_STATE>arq</GK_STATE>
     *                             <ALLOC_BANDWIDTH>0</ALLOC_BANDWIDTH>
     *                             <REQ_BANDWIDTH>0</REQ_BANDWIDTH>
     *                             <REQ_INTERVAL>0</REQ_INTERVAL>
     *                             <GK_ROUTED>false</GK_ROUTED>
     *                         </GK_STATUS>
     *                         <CAC_STATUS>
     *                             <ALLOC_BANDWIDTH>-1</ALLOC_BANDWIDTH>
     *                             <REQ_BANDWIDTH>-1</REQ_BANDWIDTH>
     *                         </CAC_STATUS>
     *                         <TX_BAUD_RATE>1048000</TX_BAUD_RATE>
     *                         <RX_BAUD_RATE>1048000</RX_BAUD_RATE>
     *                         <NOISY>false</NOISY>
     *                         <CONTENT_MEMBER>true</CONTENT_MEMBER>
     *                         <CONTENT_PROVIDER>false</CONTENT_PROVIDER>
     *                         <VISUAL_NAME>Yealink VC Desktop</VISUAL_NAME>
     *                         <SECONDARY_CAUSE_PARAMS>
     *                             <CAP_CODE>g711Alaw64k</CAP_CODE>
     *                             <FRAME_RATE_VALUE>0</FRAME_RATE_VALUE>
     *                             <LINE_RATE_VALUE>0</LINE_RATE_VALUE>
     *                             <VIDEO_RESOLUTION>qcif</VIDEO_RESOLUTION>
     *                             <SECONDARY_PROBLEM>unknown</SECONDARY_PROBLEM>
     *                             <SECONDARY_PROBLEM_VALUE>0</SECONDARY_PROBLEM_VALUE>
     *                             <REMOTE_SECONDARY_PROBLEM_VALUE>0</REMOTE_SECONDARY_PROBLEM_VALUE>
     *                             <SIP_CONFERENCING_LIMITATION>unknown</SIP_CONFERENCING_LIMITATION>
     *                         </SECONDARY_CAUSE_PARAMS>
     *                         <PARTY_CHANGE_TYPE>party_new_info</PARTY_CHANGE_TYPE>
     *                         <IS_RV_SYNC_LOSS>false</IS_RV_SYNC_LOSS>
     *                         <IS_LV_SYNC_LOSS>false</IS_LV_SYNC_LOSS>
     *                         <PARTY_CONF_FORCE>
     *                             <LAYOUT>1x1</LAYOUT>
     *                             <CELL>
     *                                 <ID>1</ID>
     *                                 <FORCE_STATE>auto</FORCE_STATE>
     *                                 <SOURCE_ID>5</SOURCE_ID>
     *                             </CELL>
     *                         </PARTY_CONF_FORCE>
     *                         <IS_CURRENTLY_ENCRYPTED>false</IS_CURRENTLY_ENCRYPTED>
     *                         <R_LPR_ACTIVATION>false</R_LPR_ACTIVATION>
     *                         <L_LPR_ACTIVATION>false</L_LPR_ACTIVATION>
     *                         <IS_VALID_HOME_CONF>true</IS_VALID_HOME_CONF>
     *                         <IS_EXCLUSIVE_CONTENT>false</IS_EXCLUSIVE_CONTENT>
     *                         <REQUEST_TO_SPEAK>false</REQUEST_TO_SPEAK>
     *                         <IS_EVENT_MODE_INTRA_SUPPRESSED>false</IS_EVENT_MODE_INTRA_SUPPRESSED>
     *                         <EVENT_MODE_LEVEL>0</EVENT_MODE_LEVEL>
     *                         <LPR_HEADERS_ACTIVATION>false</LPR_HEADERS_ACTIVATION>
     *                         <TIP_MODE>none</TIP_MODE>
     *                     </ONGOING_PARTY>
     *                 </ONGOING_PARTY_LIST>
     *                 <DELETED_PARTIES>
     *                     <PARTY_ID>1</PARTY_ID>
     *                     <PARTY_ID>3</PARTY_ID>
     *                     <PARTY_ID>4</PARTY_ID>
     *                 </DELETED_PARTIES>
     *             </CONFERENCE>
     *         </GET>
     *     </ACTION>
     * </RESPONSE_TRANS_CONF>
     * @param xml
     */

    public CcMrInfoResponse(String xml) {
        JSONObject jsonObject = XML.toJSONObject(xml);
        if (jsonObject != null) {
            try {
                JSONObject RESPONSE_TRANS_CONF = jsonObject.getJSONObject("RESPONSE_TRANS_CONF");
                JSONObject RETURN_STATUS = RESPONSE_TRANS_CONF.getJSONObject("RETURN_STATUS");
                String DESCRIPTION = RETURN_STATUS.getString("DESCRIPTION");
                status = DESCRIPTION;
                if (STATUS_OK.equals(status)) {
                    JSONObject ACTION = RESPONSE_TRANS_CONF.getJSONObject("ACTION");
                    JSONObject get = ACTION.getJSONObject("GET");
                    JSONObject conference = get.getJSONObject("CONFERENCE");
                    if (conference.has("AUDIO_SOURCE_ID")) {
                        Object audioSourceIdObj = conference.get("AUDIO_SOURCE_ID");
                        if (audioSourceIdObj != null) {
                            audioSourceId = String.valueOf(audioSourceIdObj);
                        }
                    }
                    JSONObject reservation = conference.getJSONObject("RESERVATION");
                    id = String.valueOf(reservation.getInt("ID"));
                    conferenceNum = String.valueOf(reservation.get("NUMERIC_ID"));
                    muteParties = reservation.getBoolean("MUTE_PARTIES_IN_LECTURE");
                    if (conference.has("ONGOING_PARTY_LIST")) {
                        Object ongoing_party_list_obj = conference.get("ONGOING_PARTY_LIST");
                        if (ongoing_party_list_obj instanceof JSONObject) {
                            JSONObject ongoing_party_list = (JSONObject) ongoing_party_list_obj;
                            if (ongoing_party_list != null) {
                                Object ongoing_party_obj = ongoing_party_list.get("ONGOING_PARTY");
                                if (ongoing_party_obj != null) {
                                    JSONArray ongoing_party_for = null;
                                    if (ongoing_party_obj instanceof JSONArray) {
                                        JSONArray ongoing_party_array = (JSONArray) ongoing_party_obj;
                                        ongoing_party_for = ongoing_party_array;
                                    } else if (ongoing_party_obj instanceof JSONObject) {
                                        ongoing_party_for = new JSONArray();
                                        ongoing_party_for.put(ongoing_party_obj);
                                    }
                                    if (ongoing_party_for != null) {
                                        for (Object ongoing_party_temp : ongoing_party_for) {
                                            JSONObject ongoing_party = (JSONObject) ongoing_party_temp;
                                            TerminalInfo terminalInfo = new TerminalInfo();
                                            terminalInfo.name = String.valueOf(ongoing_party.get("VISUAL_NAME"));
                                            JSONObject ongoing_party_status = ongoing_party.getJSONObject("ONGOING_PARTY_STATUS");
                                            terminalInfo.status = ongoing_party_status.getInt("ID");
                                            terminalInfo.audioMute = ongoing_party.getBoolean("AUDIO_MUTE");
                                            terminalInfo.videoMute = ongoing_party.getBoolean("VIDEO_MUTE");
                                            terminalInfo.contentProvider = ongoing_party.getBoolean("CONTENT_PROVIDER");
                                            JSONObject party = ongoing_party.getJSONObject("PARTY");
                                            terminalInfo.id = String.valueOf(party.get("ID"));
                                            terminalInfo.protocol = party.getString("INTERFACE");
                                            terminalInfo.callingMode = party.getString("CONNECTION");
                                            terminalInfo.remoteAddress = party.getString("SIP_ADDRESS");
                                            if ("null".equalsIgnoreCase(terminalInfo.remoteAddress)) {
                                                terminalInfo.remoteAddress = null;
                                            }
                                            terminalInfo.ip = party.getString("IP");
                                            terminalInfo.layoutType = party.getString("LAYOUT_TYPE");
                                            if (ongoing_party.has("CONNECT_TIME")) {
                                                String connectTimeStr = ongoing_party.getString("CONNECT_TIME");
                                                terminalInfo.connectTime = DateUtil.convertDateByString(connectTimeStr, "yyyy-MM-dd'T'HH:mm:ss", ZoneId.of("UTC"));
                                            }
                                            if (terminalInfo.status == 1 || terminalInfo.status == 10) {
                                                connectedTerminalInfoList.add(terminalInfo);
                                            } else {
                                                disconnectedTerminalInfoList.add(terminalInfo);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (conference.has("DELETED_PARTIES")) {
                        Object deleted_parties_obj = conference.get("DELETED_PARTIES");
                        if (deleted_parties_obj instanceof JSONObject) {
                            JSONObject deleted_parties = (JSONObject) deleted_parties_obj;
                            if (deleted_parties != null) {
                                Object deleted_party_id_obj = deleted_parties.get("PARTY_ID");
                                JSONArray deleted_party_id_for = null;
                                if (deleted_party_id_obj instanceof JSONArray) {
                                    JSONArray deleted_party_id_array = (JSONArray) deleted_party_id_obj;
                                    deleted_party_id_for = deleted_party_id_array;
                                } else if (deleted_party_id_obj instanceof Integer) {
                                    deleted_party_id_for = new JSONArray();
                                    deleted_party_id_for.put(deleted_party_id_obj);
                                }
                                if (deleted_party_id_for != null) {
                                    for (Object party_id_obj : deleted_party_id_for) {
                                        String party_id = String.valueOf(party_id_obj);
                                        deletedTerminalIdSet.add(party_id);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

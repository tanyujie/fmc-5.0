package com.paradisecloud.fcm.zte.model.response.cm;

import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import org.json.JSONObject;
import org.json.XML;

public class CmGetMrCdrResponse extends CommonResponse {

    private String id;
    private Integer endReasonType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getEndReasonType() {
        return endReasonType;
    }

    public void setEndReasonType(Integer endReasonType) {
        this.endReasonType = endReasonType;
    }

    /**
     * <RESPONSE_TRANS_CDR_FULL>
     *     <RETURN_STATUS>
     *         <ID>0</ID>
     *         <DESCRIPTION>Status OK</DESCRIPTION>
     *         <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *         <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         <MESSAGE_ID>0</MESSAGE_ID>
     *         <DESCRIPTION_EX></DESCRIPTION_EX>
     *     </RETURN_STATUS>
     *     <ACTION>
     *         <GET>
     *             <CDR_FULL>
     *                 <CDR_SUMMARY>
     *                     <FILE_VERSION>675</FILE_VERSION>
     *                     <NAME>tty_10229</NAME>
     *                     <ID>134</ID>
     *                     <STATUS_STR>cause_operator_terminate</STATUS_STR>
     *                     <STATUS>2</STATUS>
     *                     <GMT_OFFSET>0</GMT_OFFSET>
     *                     <START_TIME>2023-07-12T06:39:30</START_TIME>
     *                     <DURATION>
     *                         <HOUR>0</HOUR>
     *                         <MINUTE>0</MINUTE>
     *                         <SECOND>25</SECOND>
     *                     </DURATION>
     *                     <RESERVE_START_TIME>2023-07-12T06:39:30</RESERVE_START_TIME>
     *                     <RESERVE_DURATION>
     *                         <HOUR>1</HOUR>
     *                         <MINUTE>0</MINUTE>
     *                         <SECOND>0</SECOND>
     *                     </RESERVE_DURATION>
     *                     <MCU_FILE_NAME>c111</MCU_FILE_NAME>
     *                     <FILE_SAVED>true</FILE_SAVED>
     *                     <GMT_OFFSET_MINUTE>0</GMT_OFFSET_MINUTE>
     *                     <DISPLAY_NAME>test2</DISPLAY_NAME>
     *                     <RESERVED_AUDIO_PARTIES>0</RESERVED_AUDIO_PARTIES>
     *                     <RESERVED_VIDEO_PARTIES>0</RESERVED_VIDEO_PARTIES>
     *                 </CDR_SUMMARY>
     *                 <CDR_EVENT>
     *                     <CONF_START>
     *                         <STAND_BY>false</STAND_BY>
     *                         <TRANSFER_RATE>1024</TRANSFER_RATE>
     *                         <AUDIO_RATE>auto</AUDIO_RATE>
     *                         <VIDEO_SESSION>continuous_presence</VIDEO_SESSION>
     *                         <VIDEO_FORMAT>auto</VIDEO_FORMAT>
     *                         <FRAME_RATE>auto</FRAME_RATE>
     *                         <LSD_RATE>none</LSD_RATE>
     *                         <T120_RATE>none</T120_RATE>
     *                         <AUTO_TERMINATE>
     *                             <ON>true</ON>
     *                         </AUTO_TERMINATE>
     *                         <QCIF_FRAME_RATE>auto</QCIF_FRAME_RATE>
     *                     </CONF_START>
     *                     <TIME_STAMP>2023-07-12T06:39:30</TIME_STAMP>
     *                 </CDR_EVENT>
     *                 <CDR_EVENT>
     *                     <CONF_START_1>
     *                         <ENTRY_TONE>false</ENTRY_TONE>
     *                         <EXIT_TONE>false</EXIT_TONE>
     *                         <TALK_HOLD_TIME>300</TALK_HOLD_TIME>
     *                         <AUDIO_MIX_DEPTH>5</AUDIO_MIX_DEPTH>
     *                         <OPERATOR_CONF>false</OPERATOR_CONF>
     *                         <VIDEO_PROTOCOL>auto</VIDEO_PROTOCOL>
     *                         <MEET_ME_PER_CONF>
     *                             <ON>true</ON>
     *                             <AUTO_ADD>true</AUTO_ADD>
     *                             <MIN_NUM_OF_PARTIES>0</MIN_NUM_OF_PARTIES>
     *                         </MEET_ME_PER_CONF>
     *                         <PASSWORD>0076</PASSWORD>
     *                         <CHAIR_MODE>none</CHAIR_MODE>
     *                         <CASCADE>
     *                             <CASCADE_ROLE>none</CASCADE_ROLE>
     *                             <MASTER_NAME></MASTER_NAME>
     *                         </CASCADE>
     *                         <LOCK>false</LOCK>
     *                         <MAX_PARTIES>automatic</MAX_PARTIES>
     *                         <RESOURCE_FORCE/>
     *                         <ATTENDED_MODE>ivr</ATTENDED_MODE>
     *                         <AV_MSG>Conference IVR Servi</AV_MSG>
     *                         <LECTURE_MODE>
     *                             <ON>false</ON>
     *                             <TIMER>false</TIMER>
     *                             <INTERVAL>15</INTERVAL>
     *                             <AUDIO_ACTIVATED>false</AUDIO_ACTIVATED>
     *                             <LECTURE_NAME></LECTURE_NAME>
     *                             <LECTURE_MODE_TYPE>lecture_none</LECTURE_MODE_TYPE>
     *                             <LECTURE_ID>-1</LECTURE_ID>
     *                         </LECTURE_MODE>
     *                         <TIME_BEFORE_FIRST_JOIN>10</TIME_BEFORE_FIRST_JOIN>
     *                         <TIME_AFTER_LAST_QUIT>1</TIME_AFTER_LAST_QUIT>
     *                         <END_TIME_ALERT_TONE_EX>
     *                             <ON>false</ON>
     *                             <TIME>0</TIME>
     *                         </END_TIME_ALERT_TONE_EX>
     *                     </CONF_START_1>
     *                     <TIME_STAMP>2023-07-12T06:39:30</TIME_STAMP>
     *                 </CDR_EVENT>
     *                 <CDR_EVENT>
     *                     <CONF_START_4>
     *                         <NUMERIC_ID>10229</NUMERIC_ID>
     *                         <ENTRY_PASSWORD></ENTRY_PASSWORD>
     *                         <LEADER_PASSWORD>0076</LEADER_PASSWORD>
     *                         <CONTACT_INFO_LIST/>
     *                         <BILLING_DATA></BILLING_DATA>
     *                     </CONF_START_4>
     *                     <TIME_STAMP>2023-07-12T06:39:30</TIME_STAMP>
     *                 </CDR_EVENT>
     *                 <CDR_EVENT>
     *                     <CONF_START_5>
     *                         <ENCRYPTION>0</ENCRYPTION>
     *                     </CONF_START_5>
     *                     <TIME_STAMP>2023-07-12T06:39:30</TIME_STAMP>
     *                 </CDR_EVENT>
     *                 <CDR_EVENT>
     *                     <CONF_START_10>
     *                         <DISPLAY_NAME>test2</DISPLAY_NAME>
     *                         <CONF_MEDIA_TYPE>mix_avc_media_relay</CONF_MEDIA_TYPE>
     *                     </CONF_START_10>
     *                     <TIME_STAMP>2023-07-12T06:39:30</TIME_STAMP>
     *                 </CDR_EVENT>
     *                 <CDR_EVENT>
     *                     <CONF_CORRELATION_DATA>
     *                         <CONF_UUID>test2-2023-07-12T06:39:30.0Z-00:E0:DB:30:E8:04;</CONF_UUID>
     *                     </CONF_CORRELATION_DATA>
     *                     <TIME_STAMP>2023-07-12T06:39:30</TIME_STAMP>
     *                 </CDR_EVENT>
     *                 <CDR_EVENT>
     *                     <OPERATOR_TERMINATE>
     *                         <OPERATOR_NAME>ttadmin</OPERATOR_NAME>
     *                     </OPERATOR_TERMINATE>
     *                     <TIME_STAMP>2023-07-12T06:39:55</TIME_STAMP>
     *                 </CDR_EVENT>
     *                 <CDR_EVENT>
     *                     <CONF_END>
     *                         <CONF_END_CAUSE>cause_operator_terminate</CONF_END_CAUSE>
     *                     </CONF_END>
     *                     <TIME_STAMP>2023-07-12T06:39:55</TIME_STAMP>
     *                 </CDR_EVENT>
     *             </CDR_FULL>
     *         </GET>
     *     </ACTION>
     * </RESPONSE_TRANS_CDR_FULL>
     *
     * @param xml
     */
    public CmGetMrCdrResponse(String xml) {
        JSONObject jsonObject = XML.toJSONObject(xml);
        if (jsonObject != null) {
            try {
                JSONObject RESPONSE_TRANS_CDR_FULL = jsonObject.getJSONObject("RESPONSE_TRANS_CDR_FULL");
                JSONObject RETURN_STATUS = RESPONSE_TRANS_CDR_FULL.getJSONObject("RETURN_STATUS");
                String DESCRIPTION = RETURN_STATUS.getString("DESCRIPTION");
                status = DESCRIPTION;
                if (STATUS_OK.equals(status)) {
                    JSONObject ACTION = RESPONSE_TRANS_CDR_FULL.getJSONObject("ACTION");
                    JSONObject GET = ACTION.getJSONObject("GET");
                    JSONObject CDR_FULL = GET.getJSONObject("CDR_FULL");
                    JSONObject CDR_SUMMARY = CDR_FULL.getJSONObject("CDR_SUMMARY");
                    int endStatus = CDR_SUMMARY.getInt("STATUS");
                    if (endStatus == 3) {
                        endReasonType = EndReasonsType.AUTO_END;
                    } else if (endStatus == 4) {
                        endReasonType = EndReasonsType.IDLE_TOO_LONG;
                    } else {
                        endReasonType = EndReasonsType.ADMINISTRATOR_HANGS_UP;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

package com.paradisecloud.fcm.zte.model.response.cm;

import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.util.HashMap;
import java.util.Map;

public class CmSearchMrResponse extends CommonResponse {

    /**
     * key:会议号
     */
    private Map<String, ConfInfo> confInfoMap = new HashMap<>();

    public Map<String, ConfInfo> getConfInfoMap() {
        return confInfoMap;
    }

    public void setConfInfoMap(Map<String, ConfInfo> confInfoMap) {
        this.confInfoMap = confInfoMap;
    }

    /**
     * <RESPONSE_TRANS_CONF_LIST>
     *     <RETURN_STATUS>
     *         <ID>0</ID>
     *         <DESCRIPTION>Status OK</DESCRIPTION>
     *         <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *         <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         <MESSAGE_ID>1</MESSAGE_ID>
     *         <DESCRIPTION_EX></DESCRIPTION_EX>
     *     </RETURN_STATUS>
     *     <ACTION>
     *         <GET_LS>
     *             <CONF_SUMMARY_LS>
     *                 <OBJ_TOKEN>1268</OBJ_TOKEN>
     *                 <CHANGED>true</CHANGED>
     *                 <CONF_SUMMARY>
     *                     <NAME>lxl_1601192784</NAME>
     *                     <ID>96</ID>
     *                     <CONF_CHANGE>new</CONF_CHANGE>
     *                     <CONF_STATUS>
     *                         <CONF_OK>true</CONF_OK>
     *                         <CONF_EMPTY>false</CONF_EMPTY>
     *                         <SINGLE_PARTY>false</SINGLE_PARTY>
     *                         <NOT_FULL>false</NOT_FULL>
     *                         <RESOURCES_DEFICIENCY>false</RESOURCES_DEFICIENCY>
     *                         <BAD_RESOURCES>false</BAD_RESOURCES>
     *                         <PROBLEM_PARTY>false</PROBLEM_PARTY>
     *                         <PARTY_REQUIRES_OPERATOR_ASSIST>false</PARTY_REQUIRES_OPERATOR_ASSIST>
     *                         <CONTENT_RESOURCES_DEFICIENCY>false</CONTENT_RESOURCES_DEFICIENCY>
     *                     </CONF_STATUS>
     *                     <NUM_LEADERS>0</NUM_LEADERS>
     *                     <START_TIME>2023-07-05T01:32:57</START_TIME>
     *                     <END_TIME>2023-07-05T08:32:57</END_TIME>
     *                     <OPERATOR_CONF>false</OPERATOR_CONF>
     *                     <LOCK>false</LOCK>
     *                     <LECTURE_CONF>false</LECTURE_CONF>
     *                     <VIDEO_SESSION>continuous_presence</VIDEO_SESSION>
     *                     <NUM_PARTIES>2</NUM_PARTIES>
     *                     <NUM_CONNECTED_PARTIES>2</NUM_CONNECTED_PARTIES>
     *                     <INVITE_PARTY>false</INVITE_PARTY>
     *                     <ENTRY_QUEUE>false</ENTRY_QUEUE>
     *                     <ROLL_CALL>false</ROLL_CALL>
     *                     <SECURE>false</SECURE>
     *                     <ENTRY_PASSWORD></ENTRY_PASSWORD>
     *                     <PASSWORD>221411</PASSWORD>
     *                     <MEET_ME_PHONE></MEET_ME_PHONE>
     *                     <NUMERIC_ID>85533</NUMERIC_ID>
     *                     <CONTACT_INFO_LIST/>
     *                     <AUTO_LAYOUT>false</AUTO_LAYOUT>
     *                     <DURATION>
     *                         <HOUR>7</HOUR>
     *                         <MINUTE>0</MINUTE>
     *                         <SECOND>0</SECOND>
     *                     </DURATION>
     *                     <NUM_UNDEFINED_PARTIES>0</NUM_UNDEFINED_PARTIES>
     *                     <ENCRYPTION>false</ENCRYPTION>
     *                     <ENCRYPTION_TYPE>all_non_encrypted</ENCRYPTION_TYPE>
     *                     <GATEWAY>false</GATEWAY>
     *                     <RECORDING_STATUS>stop</RECORDING_STATUS>
     *                     <TRANSFER_RATE>1920</TRANSFER_RATE>
     *                     <HD>false</HD>
     *                     <DISPLAY_NAME>lxl_1601192784</DISPLAY_NAME>
     *                     <EPC_CONTENT_SOURCE_ID>-1</EPC_CONTENT_SOURCE_ID>
     *                     <IS_TELEPRESENCE_MODE>false</IS_TELEPRESENCE_MODE>
     *                     <SIP_REGISTRATIONS_STATUS>not_configured</SIP_REGISTRATIONS_STATUS>
     *                     <CONF_MEDIA_TYPE>avc_only</CONF_MEDIA_TYPE>
     *                 </CONF_SUMMARY>
     *                 <CONF_SUMMARY>
     *                     <NAME>tty_2</NAME>
     *                     <ID>97</ID>
     *                     <CONF_CHANGE>new</CONF_CHANGE>
     *                     <CONF_STATUS>
     *                         <CONF_OK>false</CONF_OK>
     *                         <CONF_EMPTY>true</CONF_EMPTY>
     *                         <SINGLE_PARTY>false</SINGLE_PARTY>
     *                         <NOT_FULL>false</NOT_FULL>
     *                         <RESOURCES_DEFICIENCY>false</RESOURCES_DEFICIENCY>
     *                         <BAD_RESOURCES>false</BAD_RESOURCES>
     *                         <PROBLEM_PARTY>false</PROBLEM_PARTY>
     *                         <PARTY_REQUIRES_OPERATOR_ASSIST>false</PARTY_REQUIRES_OPERATOR_ASSIST>
     *                         <CONTENT_RESOURCES_DEFICIENCY>false</CONTENT_RESOURCES_DEFICIENCY>
     *                     </CONF_STATUS>
     *                     <NUM_LEADERS>0</NUM_LEADERS>
     *                     <START_TIME>2023-07-05T08:20:49</START_TIME>
     *                     <END_TIME>2023-07-05T09:20:49</END_TIME>
     *                     <OPERATOR_CONF>false</OPERATOR_CONF>
     *                     <LOCK>false</LOCK>
     *                     <LECTURE_CONF>false</LECTURE_CONF>
     *                     <VIDEO_SESSION>continuous_presence</VIDEO_SESSION>
     *                     <NUM_PARTIES>0</NUM_PARTIES>
     *                     <NUM_CONNECTED_PARTIES>0</NUM_CONNECTED_PARTIES>
     *                     <INVITE_PARTY>false</INVITE_PARTY>
     *                     <ENTRY_QUEUE>false</ENTRY_QUEUE>
     *                     <ROLL_CALL>false</ROLL_CALL>
     *                     <SECURE>false</SECURE>
     *                     <ENTRY_PASSWORD></ENTRY_PASSWORD>
     *                     <PASSWORD>22222</PASSWORD>
     *                     <MEET_ME_PHONE></MEET_ME_PHONE>
     *                     <NUMERIC_ID>52011</NUMERIC_ID>
     *                     <CONTACT_INFO_LIST/>
     *                     <AUTO_LAYOUT>true</AUTO_LAYOUT>
     *                     <DURATION>
     *                         <HOUR>1</HOUR>
     *                         <MINUTE>0</MINUTE>
     *                         <SECOND>0</SECOND>
     *                     </DURATION>
     *                     <NUM_UNDEFINED_PARTIES>0</NUM_UNDEFINED_PARTIES>
     *                     <ENCRYPTION>false</ENCRYPTION>
     *                     <ENCRYPTION_TYPE>all_non_encrypted</ENCRYPTION_TYPE>
     *                     <GATEWAY>false</GATEWAY>
     *                     <RECORDING_STATUS>stop</RECORDING_STATUS>
     *                     <TRANSFER_RATE>1920</TRANSFER_RATE>
     *                     <HD>false</HD>
     *                     <DISPLAY_NAME>测试会议2</DISPLAY_NAME>
     *                     <EPC_CONTENT_SOURCE_ID>-1</EPC_CONTENT_SOURCE_ID>
     *                     <IS_TELEPRESENCE_MODE>false</IS_TELEPRESENCE_MODE>
     *                     <SIP_REGISTRATIONS_STATUS>not_configured</SIP_REGISTRATIONS_STATUS>
     *                     <CONF_MEDIA_TYPE>mix_avc_media_relay</CONF_MEDIA_TYPE>
     *                 </CONF_SUMMARY>
     *                 <DELETED_CONF_LIST/>
     *             </CONF_SUMMARY_LS>
     *         </GET_LS>
     *     </ACTION>
     * </RESPONSE_TRANS_CONF_LIST>
     *
     * @param xml
     */
    public CmSearchMrResponse(String xml) {
        JSONObject jsonObject = XML.toJSONObject(xml);
        if (jsonObject != null) {
            try {
                JSONObject RESPONSE_TRANS_CONF_LIST = jsonObject.getJSONObject("RESPONSE_TRANS_CONF_LIST");
                JSONObject RETURN_STATUS = RESPONSE_TRANS_CONF_LIST.getJSONObject("RETURN_STATUS");
                String DESCRIPTION = RETURN_STATUS.getString("DESCRIPTION");
                status = DESCRIPTION;
                if (STATUS_OK.equals(status)) {
                    JSONObject ACTION = RESPONSE_TRANS_CONF_LIST.getJSONObject("ACTION");
                    JSONObject GET_LS = ACTION.getJSONObject("GET_LS");
                    JSONObject CONF_SUMMARY_LS = GET_LS.getJSONObject("CONF_SUMMARY_LS");
                    if (CONF_SUMMARY_LS.has("CONF_SUMMARY")) {
                        Object CONF_SUMMARY = CONF_SUMMARY_LS.get("CONF_SUMMARY");
                        if (CONF_SUMMARY != null) {
                            if (CONF_SUMMARY instanceof JSONObject) {
                                JSONObject CONF_SUMMARY_OBJECT = (JSONObject) CONF_SUMMARY;
                                ConfInfo confInfo = convertToConfInfo(CONF_SUMMARY_OBJECT);
                                confInfoMap.put(confInfo.getConferenceNum(), confInfo);
                            } else if (CONF_SUMMARY instanceof JSONArray) {
                                JSONArray CONF_SUMMARY_ARRAY = (JSONArray) CONF_SUMMARY;
                                for (Object confObj : CONF_SUMMARY_ARRAY) {
                                    if (confObj instanceof JSONObject) {
                                        JSONObject jsonObjectConf = (JSONObject) confObj;
                                        ConfInfo confInfo = convertToConfInfo(jsonObjectConf);
                                        confInfoMap.put(confInfo.getConferenceNum(), confInfo);
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

    private ConfInfo convertToConfInfo(JSONObject jsonObject) {
        ConfInfo confInfo = new ConfInfo();
        String id = String.valueOf(jsonObject.get("ID"));
        confInfo.setId(id);
        String conferenceNum = String.valueOf(jsonObject.get("NUMERIC_ID"));
        confInfo.setConferenceNum(conferenceNum);
        String displayName = String.valueOf(jsonObject.get("DISPLAY_NAME"));
        confInfo.setName(displayName);
        boolean locked = jsonObject.getBoolean("LOCK");
        confInfo.setLocked(locked);

        return confInfo;
    }

    public class ConfInfo {

        private String id;
        private String conferenceNum;
        private String name;
        private boolean locked;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }
    }
}

package com.paradisecloud.fcm.mcu.plc.model.response.cm;

import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import org.json.JSONObject;
import org.json.XML;

public class CmLoginResponse extends CommonResponse {

    protected String mcuToken;
    protected String mcuUserToken;

    public String getMcuToken() {
        return mcuToken;
    }

    public void setMcuToken(String mcuToken) {
        this.mcuToken = mcuToken;
    }

    public String getMcuUserToken() {
        return mcuUserToken;
    }

    public void setMcuUserToken(String mcuUserToken) {
        this.mcuUserToken = mcuUserToken;
    }

    /**
     * <RESPONSE_TRANS_MCU>
     *     <RETURN_STATUS>
     *         <ID>0</ID>
     *         <DESCRIPTION>Status OK</DESCRIPTION>
     *         <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *         <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         <MESSAGE_ID>0</MESSAGE_ID>
     *         <DESCRIPTION_EX></DESCRIPTION_EX>
     *     </RETURN_STATUS>
     *     <ACTION>
     *         <LOGIN>
     *             <MCU_TOKEN>145</MCU_TOKEN>
     *             <MCU_USER_TOKEN>145</MCU_USER_TOKEN>
     *             <VERSION_LIST>
     *                 <MCU_VERSION>
     *                     <MAIN>8</MAIN>
     *                     <MAJOR>6</MAJOR>
     *                     <MINOR>7</MINOR>
     *                     <INTERNAL>48</INTERNAL>
     *                     <PRIVATE_DESCRIPTION></PRIVATE_DESCRIPTION>
     *                     <DESCRIPTION></DESCRIPTION>
     *                 </MCU_VERSION>
     *                 <MCMS_VERSION>
     *                     <MAIN>8</MAIN>
     *                     <MAJOR>6</MAJOR>
     *                     <MINOR>7</MINOR>
     *                     <INTERNAL>386043</INTERNAL>
     *                     <PRIVATE_DESCRIPTION></PRIVATE_DESCRIPTION>
     *                 </MCMS_VERSION>
     *             </VERSION_LIST>
     *             <AUTHORIZATION_GROUP>administrator</AUTHORIZATION_GROUP>
     *             <API_NUMBER>2000</API_NUMBER>
     *             <PRODUCT_TYPE>Rmx_1800</PRODUCT_TYPE>
     *             <HTTP_PORT>0</HTTP_PORT>
     *             <ENTRY_QUEUE_ROUTING>numeric_id_routing</ENTRY_QUEUE_ROUTING>
     *             <SYSTEM_CARDS_MODE>meridian</SYSTEM_CARDS_MODE>
     *             <SYSTEM_RAM_SIZE>8192_mb</SYSTEM_RAM_SIZE>
     *             <JITC_MODE>false</JITC_MODE>
     *             <SESSION_TIMEOUT_IN_MINUTES>0</SESSION_TIMEOUT_IN_MINUTES>
     *             <PASSWORD_EXPIRATION_WARNING_DAYS>0</PASSWORD_EXPIRATION_WARNING_DAYS>
     *             <HIDE_CONFERENCE_PASSWORD>false</HIDE_CONFERENCE_PASSWORD>
     *             <SEPARATED_MANAGEMENT_NETWORK>false</SEPARATED_MANAGEMENT_NETWORK>
     *             <AUDIBLE_ALARM_ENABLE>false</AUDIBLE_ALARM_ENABLE>
     *             <HOTBACKUP_ACTUAL_TYPE>none</HOTBACKUP_ACTUAL_TYPE>
     *             <TOTAL_NUMBER_OF_PARTICIPANTS>60</TOTAL_NUMBER_OF_PARTICIPANTS>
     *             <TOTAL_NUMBER_OF_EVENT_MODE_PARTICIPANTS>0</TOTAL_NUMBER_OF_EVENT_MODE_PARTICIPANTS>
     *             <MULTIPLE_SERVICES>false</MULTIPLE_SERVICES>
     *             <V35_FOR_JITC>false</V35_FOR_JITC>
     *             <MACHINE_ACCOUNT>false</MACHINE_ACCOUNT>
     *             <VIDEO_PREVIEW_ENABLE>true</VIDEO_PREVIEW_ENABLE>
     *             <MCU_AVC_CP_SUPPORT>true</MCU_AVC_CP_SUPPORT>
     *             <MCU_AVC_VSW_SUPPORT>false</MCU_AVC_VSW_SUPPORT>
     *             <MCU_SVC_SUPPORT>false</MCU_SVC_SUPPORT>
     *             <MCU_MIXED_CP_SUPPORT>false</MCU_MIXED_CP_SUPPORT>
     *             <MCU_MIXED_VSW_SUPPORT>false</MCU_MIXED_VSW_SUPPORT>
     *             <MCU_CASCADE_SVC>false</MCU_CASCADE_SVC>
     *             <MCU_TIP>true</MCU_TIP>
     *             <AVC_CIF_PLUS_ENABLED>true</AVC_CIF_PLUS_ENABLED>
     *             <MCU_ITP>false</MCU_ITP>
     *             <MCU_AUDIO_ONLY_CONF>false</MCU_AUDIO_ONLY_CONF>
     *             <MCU_HIGH_PROFILE_CONTENT>true</MCU_HIGH_PROFILE_CONTENT>
     *             <MCU_PRODUCT_NAME>RPCS 1800</MCU_PRODUCT_NAME>
     *             <MAX_LINE_RATE>6144</MAX_LINE_RATE>
     *             <SIMULATION>false</SIMULATION>
     *             <MCU_WEBRTC>false</MCU_WEBRTC>
     *             <LICENSE_MODE>cfs</LICENSE_MODE>
     *         </LOGIN>
     *     </ACTION>
     * </RESPONSE_TRANS_MCU>
     *
     * @param xml
     */
    public CmLoginResponse(String xml) {
        JSONObject jsonObject = XML.toJSONObject(xml);
        if (jsonObject != null) {
            try {
                JSONObject RESPONSE_TRANS_MCU = jsonObject.getJSONObject("RESPONSE_TRANS_MCU");
                JSONObject RETURN_STATUS = RESPONSE_TRANS_MCU.getJSONObject("RETURN_STATUS");
                String DESCRIPTION = RETURN_STATUS.getString("DESCRIPTION");
                status = DESCRIPTION;
                if (STATUS_OK.equals(status)) {
                    JSONObject ACTION = RESPONSE_TRANS_MCU.getJSONObject("ACTION");
                    JSONObject LOGIN = ACTION.getJSONObject("LOGIN");
                    mcuToken = String.valueOf(LOGIN.get("MCU_TOKEN"));
                    mcuUserToken = String.valueOf(LOGIN.get("MCU_USER_TOKEN"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

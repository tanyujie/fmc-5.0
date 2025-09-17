package com.paradisecloud.fcm.zte.model.response.cm;

import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import org.json.JSONObject;
import org.json.XML;

import java.time.ZoneId;
import java.util.Date;

public class CmGetMcuTimeResponse extends CommonResponse {

    private Date mcuTime;

    public Date getMcuTime() {
        return mcuTime;
    }

    public void setMcuTime(Date mcuTime) {
        this.mcuTime = mcuTime;
    }

    /**
     *
     * <RESPONSE_TRANS_MCU>
     *     <RETURN_STATUS>
     *         <ID>0</ID>
     *         <DESCRIPTION>Status OK</DESCRIPTION>
     *         <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *         <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         <MESSAGE_ID>1</MESSAGE_ID>
     *         <DESCRIPTION_EX></DESCRIPTION_EX>
     *     </RETURN_STATUS>
     *     <ACTION>
     *         <GET_TIME>
     *             <MCU_TIME>
     *                 <MCU_BASE_TIME>2023-07-21T07:21:35</MCU_BASE_TIME>
     *                 <GMT_OFFSET_SIGN>true</GMT_OFFSET_SIGN>
     *                 <GMT_OFFSET>0</GMT_OFFSET>
     *                 <IS_NTP>false</IS_NTP>
     *                 <NTP_IP_ADDRESS></NTP_IP_ADDRESS>
     *                 <NTP_SERVERS_PARAMETERS>
     *                     <NTP_IP_ADDRESS></NTP_IP_ADDRESS>
     *                     <NTP_IPV6_ADDRESS>::</NTP_IPV6_ADDRESS>
     *                     <NTP_SERVER_STATUS>NotConfigured</NTP_SERVER_STATUS>
     *                 </NTP_SERVERS_PARAMETERS>
     *                 <NTP_SERVERS_PARAMETERS>
     *                     <NTP_IP_ADDRESS></NTP_IP_ADDRESS>
     *                     <NTP_IPV6_ADDRESS>::</NTP_IPV6_ADDRESS>
     *                     <NTP_SERVER_STATUS>NotConfigured</NTP_SERVER_STATUS>
     *                 </NTP_SERVERS_PARAMETERS>
     *                 <NTP_SERVERS_PARAMETERS>
     *                     <NTP_IP_ADDRESS></NTP_IP_ADDRESS>
     *                     <NTP_IPV6_ADDRESS>::</NTP_IPV6_ADDRESS>
     *                     <NTP_SERVER_STATUS>NotConfigured</NTP_SERVER_STATUS>
     *                 </NTP_SERVERS_PARAMETERS>
     *             </MCU_TIME>
     *         </GET_TIME>
     *     </ACTION>
     * </RESPONSE_TRANS_MCU>
     *
     * @param xml
     */
    public CmGetMcuTimeResponse(String xml) {
        JSONObject jsonObject = XML.toJSONObject(xml);
        if (jsonObject != null) {
            try {
                JSONObject RESPONSE_TRANS_MCU = jsonObject.getJSONObject("RESPONSE_TRANS_MCU");
                JSONObject RETURN_STATUS = RESPONSE_TRANS_MCU.getJSONObject("RETURN_STATUS");
                String DESCRIPTION = RETURN_STATUS.getString("DESCRIPTION");
                status = DESCRIPTION;
                if (STATUS_OK.equals(status)) {
                    JSONObject ACTION = RESPONSE_TRANS_MCU.getJSONObject("ACTION");
                    JSONObject GET_TIME = ACTION.getJSONObject("GET_TIME");
                    JSONObject MCU_TIME = GET_TIME.getJSONObject("MCU_TIME");
                    String mcuBaseTimeStr = MCU_TIME.getString("MCU_BASE_TIME");
                    mcuTime = DateUtil.convertDateByString(mcuBaseTimeStr, "yyyy-MM-dd'T'HH:mm:ss", ZoneId.of("UTC"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

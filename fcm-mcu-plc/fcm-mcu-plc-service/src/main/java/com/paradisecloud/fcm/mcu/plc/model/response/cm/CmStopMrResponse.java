package com.paradisecloud.fcm.mcu.plc.model.response.cm;

import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import org.json.JSONObject;
import org.json.XML;

public class CmStopMrResponse extends CommonResponse {

    /**
     * <RESPONSE_TRANS_CONF>
     *     <RETURN_STATUS>
     *         <ID>1</ID>
     *         <DESCRIPTION>In progress</DESCRIPTION>
     *         <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *         <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         <MESSAGE_ID>2</MESSAGE_ID>
     *         <DESCRIPTION_EX></DESCRIPTION_EX>
     *     </RETURN_STATUS>
     *     <ACTION>
     *         <TERMINATE_CONF/>
     *     </ACTION>
     * </RESPONSE_TRANS_CONF>
     *
     * @param xml
     */
    public CmStopMrResponse(String xml) {
        JSONObject jsonObject = XML.toJSONObject(xml);
        if (jsonObject != null) {
            try {
                JSONObject RESPONSE_TRANS_CONF = jsonObject.getJSONObject("RESPONSE_TRANS_CONF");
                JSONObject RETURN_STATUS = RESPONSE_TRANS_CONF.getJSONObject("RETURN_STATUS");
                String DESCRIPTION = RETURN_STATUS.getString("DESCRIPTION");
                status = DESCRIPTION;
                if (STATUS_OK.equals(status) || IN_PROGRESS.equals(status)) {
                    JSONObject ACTION = RESPONSE_TRANS_CONF.getJSONObject("ACTION");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

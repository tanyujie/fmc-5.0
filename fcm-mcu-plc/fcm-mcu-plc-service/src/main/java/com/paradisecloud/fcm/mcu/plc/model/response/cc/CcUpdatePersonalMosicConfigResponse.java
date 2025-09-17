package com.paradisecloud.fcm.mcu.plc.model.response.cc;

import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import org.json.JSONObject;
import org.json.XML;

public class CcUpdatePersonalMosicConfigResponse extends CommonResponse {

    /**
     *
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
     *         <SET_PARTY_VIDEO_LAYOUT_EX/>
     *     </ACTION>
     * </RESPONSE_TRANS_CONF>
     *
     * @param xml
     */

    public CcUpdatePersonalMosicConfigResponse(String xml) {
        JSONObject jsonObject = XML.toJSONObject(xml);
        if (jsonObject != null) {
            try {
                JSONObject RESPONSE_TRANS_CONF = jsonObject.getJSONObject("RESPONSE_TRANS_CONF");
                JSONObject RETURN_STATUS = RESPONSE_TRANS_CONF.getJSONObject("RETURN_STATUS");
                String DESCRIPTION = RETURN_STATUS.getString("DESCRIPTION");
                status = DESCRIPTION;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

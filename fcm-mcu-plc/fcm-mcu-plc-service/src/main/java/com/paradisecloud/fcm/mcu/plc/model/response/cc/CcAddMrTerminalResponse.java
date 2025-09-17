package com.paradisecloud.fcm.mcu.plc.model.response.cc;

import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import org.json.JSONObject;
import org.json.XML;

public class CcAddMrTerminalResponse extends CommonResponse {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
     *         <ADD_PARTY>
     *             <ID>12</ID>
     *         </ADD_PARTY>
     *     </ACTION>
     * </RESPONSE_TRANS_CONF>
     *
     * @param xml
     */
    public CcAddMrTerminalResponse(String xml) {
        JSONObject jsonObject = XML.toJSONObject(xml);
        if (jsonObject != null) {
            try {
                JSONObject RESPONSE_TRANS_CONF = jsonObject.getJSONObject("RESPONSE_TRANS_CONF");
                JSONObject RETURN_STATUS = RESPONSE_TRANS_CONF.getJSONObject("RETURN_STATUS");
                String DESCRIPTION = RETURN_STATUS.getString("DESCRIPTION");
                status = DESCRIPTION;
                status = DESCRIPTION;
                if (STATUS_OK.equals(status)) {
                    JSONObject ACTION = RESPONSE_TRANS_CONF.getJSONObject("ACTION");
                    JSONObject ADD_PARTY = ACTION.getJSONObject("ADD_PARTY");
                    id = String.valueOf(ADD_PARTY.get("ID"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

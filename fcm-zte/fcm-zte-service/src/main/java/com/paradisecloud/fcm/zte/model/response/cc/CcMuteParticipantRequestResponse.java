package com.paradisecloud.fcm.zte.model.response.cc;

import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import org.json.JSONObject;
import org.json.XML;

public class CcMuteParticipantRequestResponse extends CommonResponse {



    public CcMuteParticipantRequestResponse(String xml) {
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

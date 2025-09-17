package com.paradisecloud.fcm.zte.model.request.cm;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

public class CmStopMrRequest extends CommonRequest {

    private String ConferenceIdentifier;

    public String getConferenceIdentifier() {
        return ConferenceIdentifier;
    }

    public void setConferenceIdentifier(String conferenceIdentifier) {
        ConferenceIdentifier = conferenceIdentifier;
    }

    /**
     *
     * <TRANS_CONF_2>
     *     <TRANS_COMMON_PARAMS>
     *         <MCU_TOKEN>{{mcu_token}}</MCU_TOKEN>
     *         <MCU_USER_TOKEN>{{mcu_user_token}}</MCU_USER_TOKEN>
     *         <ASYNC>
     *             <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *             <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         </ASYNC>
     *         <MESSAGE_ID>2</MESSAGE_ID>
     *     </TRANS_COMMON_PARAMS>
     *     <ACTION>
     *         <TERMINATE_CONF>
     *             <ID>57</ID>
     *         </TERMINATE_CONF>
     *     </ACTION>
     * </TRANS_CONF_2>
     *
     * @return
     */
    @Override
    public String buildToXml() {
        String xml = "" ;
        return xml;
    }
}

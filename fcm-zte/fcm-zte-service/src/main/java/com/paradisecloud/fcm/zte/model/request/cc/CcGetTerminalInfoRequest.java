package com.paradisecloud.fcm.zte.model.request.cc;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

public class CcGetTerminalInfoRequest extends CommonRequest {
    private String conf_id;
    private String party_id;

    public String getId() {
        return conf_id;
    }

    public void setId(String id) {
        this.conf_id = id;
    }

    public String getParty_id() {
        return party_id;
    }

    public void setParty_id(String party_id) {
        this.party_id = party_id;
    }

    /**
     *
     * <TRANS_PARTY>
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
     *         <GET>
     *             <CONF_ID>104</CONF_ID>
     *             <PARTY_ID>8</PARTY_ID>
     *         </GET>
     *     </ACTION>
     * </TRANS_PARTY>
     *
     * @return
     */

    @Override
    public String buildToXml() {
        String xml = "" +
                "<TRANS_PARTY>" +
                "<TRANS_COMMON_PARAMS>" +
                "<MCU_TOKEN>" + mcuToken + "</MCU_TOKEN>" +
                "<MCU_USER_TOKEN>" + mcuUserToken + "</MCU_USER_TOKEN>" +
                "<ASYNC>" +
                "<YOUR_TOKEN1>" + yourToken1 + "</YOUR_TOKEN1>" +
                "<YOUR_TOKEN2>" + yourToken2 + "</YOUR_TOKEN2>" +
                "</ASYNC>" +
                "<MESSAGE_ID>" + messageId + "</MESSAGE_ID>" +
                "</TRANS_COMMON_PARAMS>" +
                "<ACTION>" +
                "<GET>" +
                "<CONF_ID>" + conf_id + "</CONF_ID>" +
                "<PARTY_ID>" + party_id + "</PARTY_ID>" +
                "</GET>" +
                "</ACTION>" +
                "</TRANS_PARTY>";
        return xml;
    }
}

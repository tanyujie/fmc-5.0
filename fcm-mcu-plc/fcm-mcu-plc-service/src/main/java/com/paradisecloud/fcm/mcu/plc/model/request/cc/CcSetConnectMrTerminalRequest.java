package com.paradisecloud.fcm.mcu.plc.model.request.cc;

import com.paradisecloud.fcm.mcu.plc.model.request.CommonRequest;

public class CcSetConnectMrTerminalRequest extends CommonRequest {
    private String id;
    private boolean connect;
    private String party_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public String getParty_id() {
        return party_id;
    }

    public void setParty_id(String party_id) {
        this.party_id = party_id;
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
     *         <SET_CONNECT>
     *             <ID>106</ID>
     *             <CONNECT>false</CONNECT>
     *             <PARTY_ID>6</PARTY_ID>
     *         </SET_CONNECT>
     *     </ACTION>
     * </TRANS_CONF_2>
     *
     * @return
     */
    @Override
    public String buildToXml() {
        String xml = "" +
                "<TRANS_CONF_2>" +
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
                "<SET_CONNECT>" +
                "<ID>" + id +"</ID>" +
                "<CONNECT>" + connect + "</CONNECT>" +
                "<PARTY_ID>" + party_id + "</PARTY_ID>" +
                "</SET_CONNECT>" +
                "</ACTION>" +
                "</TRANS_CONF_2>";
        return xml;
    }
}

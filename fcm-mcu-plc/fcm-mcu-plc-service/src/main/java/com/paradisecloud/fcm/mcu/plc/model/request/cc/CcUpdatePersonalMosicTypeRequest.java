package com.paradisecloud.fcm.mcu.plc.model.request.cc;

import com.paradisecloud.fcm.mcu.plc.model.request.CommonRequest;

public class CcUpdatePersonalMosicTypeRequest extends CommonRequest {
    private String id;
    private String party_id;
    private String layout_type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParty_id() {
        return party_id;
    }

    public void setParty_id(String party_id) {
        this.party_id = party_id;
    }

    public String getLayout_type() {
        return layout_type;
    }

    public void setLayout_type(String layout_type) {
        this.layout_type = layout_type;
    }

    /**
     *
     * <TRANS_CONF_1>
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
     *         <SET_PARTY_LAYOUT_TYPE>
     *             <ID>104</ID>
     *             <PARTY_ID>2</PARTY_ID>
     *             <LAYOUT_TYPE>conference</LAYOUT_TYPE>
     *         </SET_PARTY_LAYOUT_TYPE>
     *     </ACTION>
     * </TRANS_CONF_1>
     *
     * @return
     */

    @Override
    public String buildToXml() {

        String xml = "" +
                "<TRANS_CONF_1>" +
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
                "<SET_PARTY_LAYOUT_TYPE>" +
                "<ID>" + id +"</ID>" +
                "<PARTY_ID>" + party_id + "</PARTY_ID>" +
                "<LAYOUT_TYPE>" + layout_type + "</LAYOUT_TYPE>" +
                "</SET_PARTY_LAYOUT_TYPE>" +
                "</ACTION>" +
                "</TRANS_CONF_1>";

        return xml;
    }
}

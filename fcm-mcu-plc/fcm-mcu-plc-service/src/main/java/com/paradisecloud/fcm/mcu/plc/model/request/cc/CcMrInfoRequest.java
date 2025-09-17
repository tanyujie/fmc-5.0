package com.paradisecloud.fcm.mcu.plc.model.request.cc;

import com.paradisecloud.fcm.mcu.plc.model.request.CommonRequest;

public class CcMrInfoRequest extends CommonRequest {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
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
     *         <GET>
     *             <ID>96</ID>
     *         </GET>
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
                "<GET>" +
                "<ID>" + id +"</ID>" +
                "</GET>" +
                "</ACTION>" +
                "</TRANS_CONF_2>";
        return xml;
    }
}
package com.paradisecloud.fcm.mcu.plc.model.request.cc;

import com.paradisecloud.fcm.mcu.plc.model.request.CommonRequest;

public class CcUpdateMrAutoMosicConfigRequest extends CommonRequest {
    private String id;
    private Boolean auto_layout;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getAuto_layout() {
        return auto_layout;
    }

    public void setAuto_layout(Boolean auto_layout) {
        this.auto_layout = auto_layout;
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
     *         <SET_AUTO_LAYOUT>
     *             <ID>62</ID>
     *             <AUTO_LAYOUT>false</AUTO_LAYOUT>
     *         </SET_AUTO_LAYOUT>
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
                "<SET_AUTO_LAYOUT>" +
                "<ID>" + id + "</ID>" +
                "<AUTO_LAYOUT>" + auto_layout + "</AUTO_LAYOUT>" +
                "</SET_AUTO_LAYOUT>" +
                "</ACTION>" +
                "</TRANS_CONF_2>";

        return xml;
    }
}

package com.paradisecloud.fcm.mcu.plc.model.request.cm;

import com.paradisecloud.fcm.mcu.plc.model.request.CommonRequest;

public class CmSearchMrRequest extends CommonRequest {

    /**
     *
     * <TRANS_CONF_LIST>
     *     <TRANS_COMMON_PARAMS>
     *         <MCU_TOKEN>{{mcu_token}}</MCU_TOKEN>
     *         <MCU_USER_TOKEN>{{mcu_user_token}}</MCU_USER_TOKEN>
     *         <MESSAGE_ID>1</MESSAGE_ID>
     *     </TRANS_COMMON_PARAMS>
     *     <ACTION>
     *         <GET_LS>
     *             <OBJ_TOKEN>-1</OBJ_TOKEN>
     *         </GET_LS>
     *     </ACTION>
     * </TRANS_CONF_LIST>
     *
     * @return
     */
    @Override
    public String buildToXml() {
        String xml = "" +
                "<TRANS_CONF_LIST>" +
                "<TRANS_COMMON_PARAMS>" +
                "<MCU_TOKEN>" + mcuToken + "</MCU_TOKEN>" +
                "<MCU_USER_TOKEN>" + mcuUserToken + "</MCU_USER_TOKEN>" +
                "<MESSAGE_ID>1</MESSAGE_ID>" +
                "</TRANS_COMMON_PARAMS>" +
                "<ACTION>" +
                "<GET_LS>" +
                "<OBJ_TOKEN>-1</OBJ_TOKEN>" +
                "</GET_LS>" +
                "</ACTION>" +
                "</TRANS_CONF_LIST>";
        return xml;
    }
}

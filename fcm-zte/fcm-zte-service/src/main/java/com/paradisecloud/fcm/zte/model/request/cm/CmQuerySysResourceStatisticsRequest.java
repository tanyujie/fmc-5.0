package com.paradisecloud.fcm.zte.model.request.cm;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

public class CmQuerySysResourceStatisticsRequest extends CommonRequest {

    /**
     * <TRANS_RSRC_REPORT>
     *     <TRANS_COMMON_PARAMS>
     *         <MCU_TOKEN>{{mcu_token}}</MCU_TOKEN>
     *         <MCU_USER_TOKEN>{{mcu_user_token}}</MCU_USER_TOKEN>
     *         <ASYNC>
     *             <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *             <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         </ASYNC>
     *         <MESSAGE_ID>0</MESSAGE_ID>
     *     </TRANS_COMMON_PARAMS>
     *     <ACTION>
     *         <GET_CARMEL_REPORT/>
     *     </ACTION>
     * </TRANS_RSRC_REPORT>
     *
     * @return
     */
    @Override
    public String buildToXml() {
        String xml = "" +
                "<TRANS_RSRC_REPORT>" +
                "<TRANS_COMMON_PARAMS>" +
                "<MCU_TOKEN>" + mcuToken + "</MCU_TOKEN>" +
                "<MCU_USER_TOKEN>" + mcuUserToken + "</MCU_USER_TOKEN>" +
                "<MESSAGE_ID>1</MESSAGE_ID>" +
                "</TRANS_COMMON_PARAMS>" +
                "<ACTION>" +
                "<GET_CARMEL_REPORT/>" +
                "</ACTION>" +
                "</TRANS_RSRC_REPORT>";
        return xml;
    }
}

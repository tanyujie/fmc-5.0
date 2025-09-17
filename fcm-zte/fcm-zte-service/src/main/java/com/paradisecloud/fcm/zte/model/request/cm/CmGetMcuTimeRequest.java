package com.paradisecloud.fcm.zte.model.request.cm;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

public class CmGetMcuTimeRequest extends CommonRequest {

    /**
     * <TRANS_MCU>
     *     <TRANS_COMMON_PARAMS>
     *         <MCU_TOKEN>0</MCU_TOKEN>
     *         <MCU_USER_TOKEN>0</MCU_USER_TOKEN>
     *         <ASYNC>
     *             <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *             <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         </ASYNC>
     *         <MESSAGE_ID>1</MESSAGE_ID>
     *     </TRANS_COMMON_PARAMS>
     *     <ACTION>
     *         <GET_TIME/>
     *     </ACTION>
     * </TRANS_MCU>
     *
     * @return
     */
    @Override
    public String buildToXml() {
        String xml = "" +
                "<TRANS_MCU>" +
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
                "<GET_TIME/>" +
                "</ACTION>" +
                "</TRANS_MCU>";
        return xml;
    }
}

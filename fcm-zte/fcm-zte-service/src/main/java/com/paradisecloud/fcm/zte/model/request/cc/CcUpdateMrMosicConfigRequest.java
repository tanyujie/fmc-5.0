package com.paradisecloud.fcm.zte.model.request.cc;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

import java.util.List;

public class CcUpdateMrMosicConfigRequest extends CommonRequest {

    private String id;
    private String layout;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
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
     *         <SET_VIDEO_LAYOUT>
     *             <ID>62</ID>
     *             <FORCE>
     *                 <LAYOUT>2x2</LAYOUT>
     *                 <CELL>
     *                     <ID>1</ID>
     *                     <FORCE_STATE>forced</FORCE_STATE>
     *                     <FORCE_ID>1</FORCE_ID>
     *                     <FORCE_NAME></FORCE_NAME>
     *                     <SOURCE_ID>1</SOURCE_ID>
     *                 </CELL>
     *                 <CELL>
     *                     <ID>2</ID>
     *                     <FORCE_STATE>forced</FORCE_STATE>
     *                     <FORCE_ID>2</FORCE_ID>
     *                     <FORCE_NAME></FORCE_NAME>
     *                     <SOURCE_ID>2</SOURCE_ID>
     *                 </CELL>
     *                 <CELL>
     *                     <ID>3</ID>
     *                     <FORCE_STATE>forced</FORCE_STATE>
     *                     <FORCE_ID>4</FORCE_ID>
     *                     <FORCE_NAME></FORCE_NAME>
     *                     <SOURCE_ID>4</SOURCE_ID>
     *                 </CELL>
     *                 <CELL>
     *                     <ID>4</ID>
     *                     <FORCE_STATE>auto</FORCE_STATE>
     *                     <SOURCE_ID>-1</SOURCE_ID>
     *                 </CELL>
     *             </FORCE>
     *         </SET_VIDEO_LAYOUT>
     *     </ACTION>
     * </TRANS_CONF_1>
     *
     * @return
     */

    @Override
    public String buildToXml() {
        String cell = "";


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
                " <SET_VIDEO_LAYOUT>" +
                "<ID>" + id +"</ID>" +
                "<FORCE>" +
                "<LAYOUT>" + layout + "</LAYOUT>" +
                cell +
                "</FORCE>" +
                "</SET_VIDEO_LAYOUT>" +
                "</ACTION>" +
                "</TRANS_CONF_1>";
        return xml;
    }
}

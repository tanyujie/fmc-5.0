package com.paradisecloud.fcm.mcu.plc.model.request.cc;

import com.paradisecloud.fcm.mcu.plc.model.request.CommonRequest;

public class CcAddMrTerminalRequest extends CommonRequest {
    private String id;
    private String party_name;
    private String party_interface;
    private String party_ip;
    private String party_remote_address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParty_name() {
        return party_name;
    }

    public void setParty_name(String party_name) {
        this.party_name = party_name;
    }

    public String getParty_interface() {
        return party_interface;
    }

    public void setParty_interface(String party_interface) {
        this.party_interface = party_interface;
    }

    public String getParty_ip() {
        return party_ip;
    }

    public void setParty_ip(String party_ip) {
        this.party_ip = party_ip;
    }

    public String getParty_remote_address() {
        return party_remote_address;
    }

    public void setParty_remote_address(String party_remote_address) {
        this.party_remote_address = party_remote_address;
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
     *         <ADD_PARTY>
     *             <ID>113</ID>
     *             <PARTY>
     *                 <NAME>40031</NAME>
     *                 <ID></ID>
     *                 <INTERFACE>sip</INTERFACE>
     *                 <CONNECTION>dial_out</CONNECTION>
     *                 <VIDEO_PROTOCOL>auto</VIDEO_PROTOCOL>
     *                 <CALL_CONTENT>framed
     *                 </CALL_CONTENT>
     *                 <ALIAS>
     *                     <NAME>40031@172.16.100.232</NAME>
     *                     <ALIAS_TYPE>323_id</ALIAS_TYPE>
     *                 </ALIAS>
     *                 <IP>0.0.0.0</IP>
     *                 <SIGNALING_PORT>5060</SIGNALING_PORT>
     *                 <AUTO_DETECT>false</AUTO_DETECT>
     *                 <RESTRICT>false</RESTRICT>
     *                 <ENHANCED_VIDEO>false
     *                 </ENHANCED_VIDEO>
     *                 <VIDEO_BIT_RATE>automatic</VIDEO_BIT_RATE>
     *                 <IP_QOS>
     *                     <QOS_ACTION>disabled</QOS_ACTION>
     *                     <QOS_DIFF_SERV>diffserv</QOS_DIFF_SERV>
     *                     <QOS_IP_AUDIO>5</QOS_IP_AUDIO>
     *                     <QOS_IP_VIDEO>4</QOS_IP_VIDEO>
     *                     <QOS_TOS>none</QOS_TOS>
     *                 </IP_QOS>
     *                 <ATM_QOS>
     *                     <QOS_ACTION>disabled</QOS_ACTION>
     *                     <QOS_ATM_AUDIO>ubr</QOS_ATM_AUDIO>
     *                     <QOS_ATM_VIDEO>ubr</QOS_ATM_VIDEO>
     *                 </ATM_QOS>
     *                 <RECORDING_PORT>no</RECORDING_PORT>
     *                 <LAYOUT_TYPE>conference</LAYOUT_TYPE>
     *                 <PERSONAL_LAYOUT>1x1</PERSONAL_LAYOUT>
     *                 <PERSONAL_FORCE_LIST/>
     *                 <VIP>false</VIP>
     *                 <CONTACT_INFO_LIST>
     *                     <CONTACT_INFO/>
     *                     <CONTACT_INFO_2/>
     *                     <CONTACT_INFO_3/>
     *                     <CONTACT_INFO_4/>
     *                     <ADDITIONAL_INFO/>
     *                 </CONTACT_INFO_LIST>
     *                 <LISTEN_VOLUME>5</LISTEN_VOLUME>
     *                 <AGC>true</AGC>
     *                 <SIP_ADDRESS>40031@172.16.100.232</SIP_ADDRESS>
     *                 <SIP_ADDRESS_TYPE>uri_type</SIP_ADDRESS_TYPE>
     *                 <WEB_USER_ID>0</WEB_USER_ID>
     *                 <UNDEFINED>false</UNDEFINED>
     *                 <BACKUP_SERVICE_NAME/>
     *                 <BACKUP_SUB_SERVICE_NAME/>
     *                 <DEFAULT_TEMPLATE>false</DEFAULT_TEMPLATE>
     *                 <NODE_TYPE>terminal</NODE_TYPE>
     *                 <ENCRYPTION_EX>auto</ENCRYPTION_EX>
     *                 <H323_PSTN>false</H323_PSTN>
     *                 <EMAIL/>
     *                 <IS_RECORDING_LINK_PARTY>false</IS_RECORDING_LINK_PARTY>
     *                 <USER_IDENTIFIER_STRING/>
     *                 <IDENTIFICATION_METHOD>password</IDENTIFICATION_METHOD>
     *                 <CASCADE>
     *                     <CASCADE_ROLE>none</CASCADE_ROLE>
     *                     <CASCADED_LINKS_NUMBER>1</CASCADED_LINKS_NUMBER>
     *                     <LINK_TYPE>regular</LINK_TYPE>
     *                     <MASTER_NAME/>
     *                 </CASCADE>
     *                 <TELEPRESENCE_MODE>none</TELEPRESENCE_MODE>
     *                 <FORCE>
     *                     <LAYOUT>1x1</LAYOUT>
     *                 </FORCE>
     *                 <IP_V6>::</IP_V6>
     *                 <SUB_IP_SERVICE>primary</SUB_IP_SERVICE>
     *                 <MAX_RESOLUTION>auto</MAX_RESOLUTION>
     *                 <ENDPOINT_MEDIA_TYPE>avc</ENDPOINT_MEDIA_TYPE>
     *                 <PRECEDENCE_DOMAIN_NAME/>
     *                 <PRECEDENCE_LEVEL_TYPE>priority</PRECEDENCE_LEVEL_TYPE>
     *             </PARTY>
     *         </ADD_PARTY>
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
                "<ADD_PARTY>" +
                "<ID>" + id +"</ID>" +
                "<PARTY>" +
                "<NAME>" + party_name + "</NAME>" +
                "<ID></ID>" +
                "<INTERFACE>" + party_interface + "</INTERFACE>" +
                "<CONNECTION>dial_out</CONNECTION>" +
                "<VIDEO_PROTOCOL>auto</VIDEO_PROTOCOL>" +
                "<CALL_CONTENT>framed" +
                "</CALL_CONTENT>" +
                "<ALIAS>" +
                "<NAME>" + party_remote_address + "</NAME>" +
                "<ALIAS_TYPE>323_id</ALIAS_TYPE>" +
                "</ALIAS>" +
                "<IP>" + party_ip + "</IP>" +
                "<SIGNALING_PORT>5060</SIGNALING_PORT>" +
                "<AUTO_DETECT>false</AUTO_DETECT>" +
                "<RESTRICT>false</RESTRICT>" +
                "<ENHANCED_VIDEO>false" +
                "</ENHANCED_VIDEO>" +
                "<VIDEO_BIT_RATE>automatic</VIDEO_BIT_RATE>" +
                "<IP_QOS>" +
                "<QOS_ACTION>disabled</QOS_ACTION>" +
                "<QOS_DIFF_SERV>diffserv</QOS_DIFF_SERV>" +
                "<QOS_IP_AUDIO>5</QOS_IP_AUDIO>" +
                "<QOS_IP_VIDEO>4</QOS_IP_VIDEO>" +
                "<QOS_TOS>none</QOS_TOS>" +
                "</IP_QOS>" +
                "<ATM_QOS>" +
                "<QOS_ACTION>disabled</QOS_ACTION>" +
                "<QOS_ATM_AUDIO>ubr</QOS_ATM_AUDIO>" +
                "<QOS_ATM_VIDEO>ubr</QOS_ATM_VIDEO>" +
                "</ATM_QOS>" +
                "<RECORDING_PORT>no</RECORDING_PORT>" +
                "<LAYOUT_TYPE>conference</LAYOUT_TYPE>" +
                "<PERSONAL_LAYOUT>1x1</PERSONAL_LAYOUT>" +
                "<PERSONAL_FORCE_LIST/>" +
                "<VIP>false</VIP>" +
                "<CONTACT_INFO_LIST>" +
                "<CONTACT_INFO/>" +
                "<CONTACT_INFO_2/>" +
                "<CONTACT_INFO_3/>" +
                "<CONTACT_INFO_4/>" +
                "<ADDITIONAL_INFO/>" +
                "</CONTACT_INFO_LIST>" +
                "<LISTEN_VOLUME>5</LISTEN_VOLUME>" +
                "<AGC>true</AGC>" +
                "<SIP_ADDRESS>" + party_remote_address + "</SIP_ADDRESS>" +
                "<SIP_ADDRESS_TYPE>uri_type</SIP_ADDRESS_TYPE>" +
                "<WEB_USER_ID>0</WEB_USER_ID>" +
                "<UNDEFINED>false</UNDEFINED>" +
                "<BACKUP_SERVICE_NAME/>" +
                "<BACKUP_SUB_SERVICE_NAME/>" +
                "<DEFAULT_TEMPLATE>false</DEFAULT_TEMPLATE>" +
                "<NODE_TYPE>terminal</NODE_TYPE>" +
                "<ENCRYPTION_EX>auto</ENCRYPTION_EX>" +
                "<H323_PSTN>false</H323_PSTN>" +
                "<EMAIL/>" +
                "<IS_RECORDING_LINK_PARTY>false</IS_RECORDING_LINK_PARTY>" +
                "<USER_IDENTIFIER_STRING/>" +
                "<IDENTIFICATION_METHOD>password</IDENTIFICATION_METHOD>" +
                "<CASCADE>" +
                "<CASCADE_ROLE>none</CASCADE_ROLE>" +
                "<CASCADED_LINKS_NUMBER>1</CASCADED_LINKS_NUMBER>" +
                "<LINK_TYPE>regular</LINK_TYPE>" +
                "<MASTER_NAME/>" +
                "</CASCADE>" +
                "<TELEPRESENCE_MODE>none</TELEPRESENCE_MODE>" +
                "<FORCE>" +
                "<LAYOUT>1x1</LAYOUT>" +
                "</FORCE>" +
                "<IP_V6>::</IP_V6>" +
                "<SUB_IP_SERVICE>primary</SUB_IP_SERVICE>" +
                "<MAX_RESOLUTION>auto</MAX_RESOLUTION>" +
                "<ENDPOINT_MEDIA_TYPE>avc</ENDPOINT_MEDIA_TYPE>" +
                "<PRECEDENCE_DOMAIN_NAME/>" +
                "<PRECEDENCE_LEVEL_TYPE>priority</PRECEDENCE_LEVEL_TYPE>" +
                "</PARTY>" +
                "</ADD_PARTY>" +
                "</ACTION>" +
                "</TRANS_CONF_1>";
        return xml;
    }
}

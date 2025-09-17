package com.paradisecloud.fcm.mcu.plc.model.request.cm;

import com.paradisecloud.fcm.mcu.plc.model.request.CommonRequest;

public class CmLoginRequest extends CommonRequest {

    private String ip;
    private Integer port;
    private String userName;
    private String password;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * <TRANS_MCU>
     *     <TRANS_COMMON_PARAMS>
     *         <MCU_TOKEN></MCU_TOKEN>
     *         <MCU_USER_TOKEN></MCU_USER_TOKEN>
     *         <ASYNC>
     *             <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *             <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         </ASYNC>
     *         <MESSAGE_ID>0</MESSAGE_ID>
     *     </TRANS_COMMON_PARAMS>
     *     <ACTION>
     *         <LOGIN>
     *             <MCU_IP>
     *                 <IP>172.16.100.69</IP>
     *                 <LISTEN_PORT>80</LISTEN_PORT>
     *                 <HOST_NAME/>
     *             </MCU_IP>
     *             <USER_NAME>ttadmin</USER_NAME>
     *             <PASSWORD>P@rad1se</PASSWORD>
     *         </LOGIN>
     *     </ACTION>
     * </TRANS_MCU>
     *
     * @return
     */
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
                "<LOGIN>" +
                "<MCU_IP>" +
                "<IP>" + ip + "</IP>" +
                "<LISTEN_PORT>" + port + "</LISTEN_PORT>" +
                "<HOST_NAME/>" +
                "</MCU_IP>" +
                "<USER_NAME>" + userName + "</USER_NAME>" +
                "<PASSWORD>" + password + "</PASSWORD>" +
                "</LOGIN>" +
                "</ACTION>" +
                "</TRANS_MCU>";
        return xml;
    }
}

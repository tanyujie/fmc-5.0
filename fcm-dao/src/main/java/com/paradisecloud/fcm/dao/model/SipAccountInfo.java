package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.utils.bean.BeanUtils;

public class SipAccountInfo extends SipServerInfo {

    /** fs用户id */
    private String sipUserName;

    /** fs用户密码 */
    private String sipPassword;

    /** fs显示名 */
    private String displayName;

    /** fsbc用户id */
    private String userName;

    /** fsbc用户密码 */
    private String password;

    /** fsbc显示名 */
    private String name;

    /** 终端ID */
    private Long terminalId;

    /** 协议 */
    private String protocol;

    public SipAccountInfo() {
    }

    public SipAccountInfo(SipServerInfo sipServerInfo) {
        BeanUtils.copyBeanProp(this, sipServerInfo);
    }

    public String getSipUserName() {
        return sipUserName;
    }

    public void setSipUserName(String sipUserName) {
        this.sipUserName = sipUserName;
    }

    public String getSipPassword() {
        return sipPassword;
    }

    public void setSipPassword(String sipPassword) {
        this.sipPassword = sipPassword;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return super.toString() + "SipAccountInfo{" +
                "sipUserName='" + sipUserName + '\'' +
                ", sipPassword='" + sipPassword + '\'' +
                ", displayName='" + displayName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", terminalId=" + terminalId +
                ", protocol=" + protocol +
                '}';
    }
}

package com.paradisecloud.fcm.dao.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class SipServerInfo {

    /** 服务器ID */
    private Long serverId;

    /** 服务器类型 */
    private Integer type;

    /** sip服务器ip */
    private String sipServer;

    /** sip服务器端口 */
    private Integer sipPort;

    /** turnServer的ip */
    private String turnServer;

    /** turnPort端口 */
    private Integer turnPort;

    /** turnUserName用户名 */
    private String turnUserName;

    /** turnPassword密码 */
    private String turnPassword;

    /** stunServer的ip */
    private String stunServer;

    /** stunPort的端口 */
    private Integer stunPort;

    /** 代理服务器ip */
    private String proxyServer;

    /** 创建时间 */
    private Date createTime;

    /** fsbc授权域名 */
    @JsonProperty("AuthDomain")
    private String AuthDomain;

    /** fsbc端口号 */
    private Integer port;

    /** 域名 */
    private String domainName;

    /** 服务器ip */
    private String serverIp;

    /** 账号域名 */
    private String mcuDomain;

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSipServer() {
        return sipServer;
    }

    public void setSipServer(String sipServer) {
        this.sipServer = sipServer;
    }

    public Integer getSipPort() {
        return sipPort;
    }

    public void setSipPort(Integer sipPort) {
        this.sipPort = sipPort;
    }

    public String getTurnServer() {
        return turnServer;
    }

    public void setTurnServer(String turnServer) {
        this.turnServer = turnServer;
    }

    public Integer getTurnPort() {
        return turnPort;
    }

    public void setTurnPort(Integer turnPort) {
        this.turnPort = turnPort;
    }

    public String getTurnUserName() {
        return turnUserName;
    }

    public void setTurnUserName(String turnUserName) {
        this.turnUserName = turnUserName;
    }

    public String getTurnPassword() {
        return turnPassword;
    }

    public void setTurnPassword(String turnPassword) {
        this.turnPassword = turnPassword;
    }

    public String getStunServer() {
        return stunServer;
    }

    public void setStunServer(String stunServer) {
        this.stunServer = stunServer;
    }

    public Integer getStunPort() {
        return stunPort;
    }

    public void setStunPort(Integer stunPort) {
        this.stunPort = stunPort;
    }

    public String getProxyServer() {
        return proxyServer;
    }

    public void setProxyServer(String proxyServer) {
        this.proxyServer = proxyServer;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JsonIgnore
    public String getAuthDomain() {
        return AuthDomain;
    }

    public void setAuthDomain(String authDomain) {
        AuthDomain = authDomain;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getMcuDomain() {
        return mcuDomain;
    }

    public void setMcuDomain(String mcuDomain) {
        this.mcuDomain = mcuDomain;
    }
}

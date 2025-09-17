package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 *  busi_mqtt
 * 
 * @author zyz
 * @date 2021-07-21
 */
@Schema(description = "mqtt资源配置")
public class BusiMqtt extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @Schema(description = "主键id")
    private Long id;

    /** 终端连接mqtt服务器的用户名 */
    @Schema(description = "终端连接mqtt服务器的用户名")
    @Excel(name = "终端连接mqtt服务器的用户名")
    private String userName;

    /** 终端连接mqtt服务器的密码 */
    @Schema(description = "终端连接mqtt服务器的密码")
    @Excel(name = "终端连接mqtt服务器的密码")
    private String password;

    /** mqtt服务器的名称 */
    @Schema(description = "mqtt服务器的名称")
    @Excel(name = "mqtt服务器的名称")
    private String mqttName;

    /** 设备的ip地址 */
    @Schema(description = "设备的ip地址")
    @Excel(name = "设备的ip地址")
    private String ip;

    /** mqtt服务器端口 */
    @Schema(description = "mqtt服务器端口")
    @Excel(name = "mqtt服务器端口")
    private Integer tcpPort;

    /** mqtt服务器后台管理端口 */
    @Schema(description = "mqtt服务器后台管理端口")
    @Excel(name = "mqtt服务器后台管理端口")
    private Integer dashboardPort;

    /** mqtt服务调用api的端口 */
    @Schema(description = "mqtt服务调用api的端口")
    @Excel(name = "mqtt服务调用api的端口")
    private Integer managementPort;

    /** mqtt在线状态：1在线，2离线，3删除 */
    @Schema(description = "mqtt在线状态：1在线，2离线，3删除")
    @Excel(name = "mqtt在线状态：1在线，2离线，3删除")
    private Integer status;

    /** 服务器端口 */
    @Schema(description = "服务器端口")
    @Excel(name = "服务器端口")
    private Integer serverPort;

    /** mqtt服务启动路径 */
    @Schema(description = "mqtt服务启动路径")
    @Excel(name = "mqtt服务启动路径")
    private String mqttStartupPath;

    /** 服务器的用户名 */
    @Schema(description = "服务器的用户名")
    @Excel(name = "服务器的用户名")
    private String serverUserName;

    /** 服务器的密码 */
    @Schema(description = "服务器的密码")
    @Excel(name = "服务器的密码")
    private String serverPassword;
    
    /** 节点名称 */
    @Schema(description = "节点名称")
    @Excel(name = "节点名称")
    private String nodeName;

    /** 域名 */
    @Schema(description = "域名")
    @Excel(name = "域名")
    private String domainName;

    /** 使用SSL */
    @Schema(description = "使用SSL")
    @Excel(name = "使用SSL")
    private Integer useSsl;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    public void setMqttName(String mqttName) 
    {
        this.mqttName = mqttName;
    }

    public String getMqttName() 
    {
        return mqttName;
    }
    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }
    public void setTcpPort(Integer tcpPort) 
    {
        this.tcpPort = tcpPort;
    }

    public Integer getTcpPort() 
    {
        return tcpPort;
    }
    public void setDashboardPort(Integer dashboardPort) 
    {
        this.dashboardPort = dashboardPort;
    }

    public Integer getDashboardPort() 
    {
        return dashboardPort;
    }
    public void setManagementPort(Integer managementPort) 
    {
        this.managementPort = managementPort;
    }

    public Integer getManagementPort() 
    {
        return managementPort;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setServerPort(Integer serverPort) 
    {
        this.serverPort = serverPort;
    }

    public Integer getServerPort() 
    {
        return serverPort;
    }
    public void setMqttStartupPath(String mqttStartupPath) 
    {
        this.mqttStartupPath = mqttStartupPath;
    }

    public String getMqttStartupPath() 
    {
        return mqttStartupPath;
    }
    public void setServerUserName(String serverUserName) 
    {
        this.serverUserName = serverUserName;
    }

    public String getServerUserName() 
    {
        return serverUserName;
    }
    public void setServerPassword(String serverPassword) 
    {
        this.serverPassword = serverPassword;
    }

    public String getServerPassword() 
    {
        return serverPassword;
    }

    public void setNodeName(String nodeName) 
    {
        this.nodeName = nodeName;
    }

    public String getNodeName() 
    {
        return nodeName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Integer getUseSsl() {
        return useSsl;
    }

    public void setUseSsl(Integer useSsl) {
        this.useSsl = useSsl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("userName", getUserName())
            .append("password", getPassword())
            .append("mqttName", getMqttName())
            .append("ip", getIp())
            .append("tcpPort", getTcpPort())
            .append("dashboardPort", getDashboardPort())
            .append("managementPort", getManagementPort())
            .append("status", getStatus())
            .append("serverPort", getServerPort())
            .append("mqttStartupPath", getMqttStartupPath())
            .append("serverUserName", getServerUserName())
            .append("serverPassword", getServerPassword())
            .append("nodeName", getNodeName())
            .append("domainName", getDomainName())
            .append("useSsl", getUseSsl())
            .toString();
    }
}

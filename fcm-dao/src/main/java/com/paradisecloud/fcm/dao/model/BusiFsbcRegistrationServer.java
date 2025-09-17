package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 终端FSBC注册服务器对象 busi_fsbc_registration_server
 * 
 * @author lilinhai
 * @date 2022-04-01
 */
@Schema(description = "终端FSBC注册服务器")
public class BusiFsbcRegistrationServer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 注册服务器名字 */
    @Schema(description = "注册服务器名字")
    @Excel(name = "注册服务器名字")
    private String name;

    /** 注册服务器呼叫ip */
    @Schema(description = "注册服务器呼叫ip")
    @Excel(name = "注册服务器呼叫ip")
    private String callIp;

    /** 注册服务器数据同步IP */
    @Schema(description = "注册服务器数据同步IP")
    @Excel(name = "注册服务器数据同步IP")
    private String dataSyncIp;

    /** 注册服务器端口 */
    @Schema(description = "注册服务器端口")
    @Excel(name = "注册服务器端口")
    private Integer port;

    /** sip端口 */
    @Schema(description = "sip端口")
    @Excel(name = "sip端口")
    private Integer sipPort;

    /** 注册服务器用户名 */
    @Schema(description = "注册服务器用户名")
    @Excel(name = "注册服务器用户名")
    private String username;

    /** 注册服务器密码 */
    @Schema(description = "注册服务器密码")
    @Excel(name = "注册服务器密码")
    private String password;

    /** 域名 */
    @Schema(description = "域名")
    @Excel(name = "域名")
    private String domainName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setCallIp(String callIp) 
    {
        this.callIp = callIp;
    }

    public String getCallIp() 
    {
        return callIp;
    }
    public void setDataSyncIp(String dataSyncIp) 
    {
        this.dataSyncIp = dataSyncIp;
    }

    public String getDataSyncIp() 
    {
        return dataSyncIp;
    }
    public void setPort(Integer port) 
    {
        this.port = port;
    }

    public Integer getPort() 
    {
        return port;
    }
    public void setSipPort(Integer sipPort) 
    {
        this.sipPort = sipPort;
    }

    public Integer getSipPort() 
    {
        return sipPort;
    }
    public void setUsername(String username) 
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("name", getName())
            .append("callIp", getCallIp())
            .append("dataSyncIp", getDataSyncIp())
            .append("port", getPort())
            .append("sipPort", getSipPort())
            .append("username", getUsername())
            .append("password", getPassword())
            .append("domainName", getDomainName())
            .toString();
    }
}
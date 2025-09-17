package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 服务器资源信息对象 busi_free_switch
 * 
 * @author zyz
 * @date 2021-09-02
 */
@Schema(description = "服务器资源信息")
public class BusiFreeSwitch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @Schema(description = "主键id")
    private Long id;

    /** 服务器用户名 */
    @Schema(description = "服务器用户名")
    @Excel(name = "服务器用户名")
    private String userName;

    /** 服务器密码 */
    @Schema(description = "服务器密码")
    @Excel(name = "服务器密码")
    private String password;

    /** 名称 */
    @Schema(description = "名称")
    @Excel(name = "名称")
    private String name;

    /** 服务器ip */
    @Schema(description = "服务器ip")
    @Excel(name = "服务器ip")
    private String ip;
    
    /** 端口号 */
    @Schema(description = "端口号")
    @Excel(name = "端口号")
    private Integer port;

    /** 呼叫端口号 */
    @Schema(description = "呼叫端口号")
    @Excel(name = "呼叫端口号")
    private Integer callPort;

    /** 能否被叫 */
    @Schema(description = "能否被叫")
    @Excel(name = "能否被叫")
    private Integer outBound;

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
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }

    public void setPort(Integer port) 
    {
        this.port = port;
    }

    public Integer getPort() 
    {
        return port;
    }

    public Integer getCallPort() {
        return callPort;
    }

    public void setCallPort(Integer callPort) {
        this.callPort = callPort;
    }

    public Integer getOutBound() {
        return outBound;
    }

    public void setOutBound(Integer outBound) {
        this.outBound = outBound;
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
            .append("userName", getUserName())
            .append("password", getPassword())
            .append("name", getName())
            .append("ip", getIp())
            .append("port", getPort())
            .append("callPort", getCallPort())
            .append("outBound", getOutBound())
            .append("domainName", getDomainName())
            .toString();
    }
}

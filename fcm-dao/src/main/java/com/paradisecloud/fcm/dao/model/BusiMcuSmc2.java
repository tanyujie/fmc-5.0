package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * SMC3.0MCU终端信息对象 busi_mcu_smc3
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
@Schema(description = "SMC3.0MCU终端信息")
public class BusiMcuSmc2 extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 连接用户名，同一个组下的用户名相同 */
    @Schema(description = "连接用户名，同一个组下的用户名相同")
    @Excel(name = "连接用户名，同一个组下的用户名相同")
    private String username;

    /** 连接密码，同一个组下的密码相同 */
    @Schema(description = "连接密码，同一个组下的密码相同")
    @Excel(name = "连接密码，同一个组下的密码相同")
    private String password;

    /** meeting用户名 */
    @Schema(description = "meeting用户名")
    @Excel(name = "meeting用户名")
    private String meetingUsername;

    /** meeting密码 */
    @Schema(description = "meeting密码")
    @Excel(name = "meeting密码")
    private String meetingPassword;

    /** 显示名字 */
    @Schema(description = "显示名字")
    @Excel(name = "显示名字")
    private String name;

    /** 设备的IP地址 */
    @Schema(description = "设备的IP地址")
    @Excel(name = "设备的IP地址")
    private String ip;

    /** 该IP是增强音视频效果 */
    @Schema(description = "该IP是增强音视频效果")
    @Excel(name = "该IP是增强音视频效果")
    private String cucmIp;

    /** 端口 */
    @Schema(description = "端口")
    @Excel(name = "端口")
    private Integer port;

    /** 在线状态：1在线，2离线，3删除 */
    @Schema(description = "在线状态：1在线，2离线，3删除")
    @Excel(name = "在线状态：1在线，2离线，3删除")
    private Integer status;

    /** 容量 */
    @Schema(description = "容量")
    @Excel(name = "容量")
    private Integer capacity;

    /** 备用（本节点宕机后指向的备用节点） */
    @Schema(description = "备用（本节点宕机后指向的备用节点）")
    @Excel(name = "备用", readConverterExp = "本=节点宕机后指向的备用节点")
    private Long spareMcuId;

    /** MCU域 */
    @Schema(description = "MCU域")
    @Excel(name = "MCU域")
    private String mcuDomain;

    /** 呼叫端口 */
    @Schema(description = "呼叫端口")
    @Excel(name = "呼叫端口")
    private Integer callPort;

    /** 代理服务器地址 */
    @Schema(description = "代理服务器地址")
    @Excel(name = "代理服务器地址")
    private String proxyHost;

    /** 代理服务器端口 */
    @Schema(description = "代理服务器端口")
    @Excel(name = "代理服务器端口")
    private Integer proxyPort;

    /** SC服务器地址 */
    @Schema(description = "SC服务器地址")
    @Excel(name = "SC服务器地址")
    private String scUrl;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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
    public void setMeetingUsername(String meetingUsername) 
    {
        this.meetingUsername = meetingUsername;
    }

    public String getMeetingUsername() 
    {
        return meetingUsername;
    }
    public void setMeetingPassword(String meetingPassword) 
    {
        this.meetingPassword = meetingPassword;
    }

    public String getMeetingPassword() 
    {
        return meetingPassword;
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
    public void setCucmIp(String cucmIp) 
    {
        this.cucmIp = cucmIp;
    }

    public String getCucmIp() 
    {
        return cucmIp;
    }
    public void setPort(Integer port) 
    {
        this.port = port;
    }

    public Integer getPort() 
    {
        return port;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setCapacity(Integer capacity) 
    {
        this.capacity = capacity;
    }

    public Integer getCapacity() 
    {
        return capacity;
    }
    public void setSpareMcuId(Long spareMcuId) 
    {
        this.spareMcuId = spareMcuId;
    }

    public Long getSpareMcuId() 
    {
        return spareMcuId;
    }
    public void setMcuDomain(String mcuDomain) 
    {
        this.mcuDomain = mcuDomain;
    }

    public String getMcuDomain() 
    {
        return mcuDomain;
    }
    public void setCallPort(Integer callPort) 
    {
        this.callPort = callPort;
    }

    public Integer getCallPort() 
    {
        return callPort;
    }
    public void setProxyHost(String proxyHost) 
    {
        this.proxyHost = proxyHost;
    }

    public String getProxyHost() 
    {
        return proxyHost;
    }
    public void setProxyPort(Integer proxyPort) 
    {
        this.proxyPort = proxyPort;
    }

    public Integer getProxyPort() 
    {
        return proxyPort;
    }
    public void setScUrl(String scUrl) 
    {
        this.scUrl = scUrl;
    }

    public String getScUrl() 
    {
        return scUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("username", getUsername())
            .append("password", getPassword())
            .append("meetingUsername", getMeetingUsername())
            .append("meetingPassword", getMeetingPassword())
            .append("name", getName())
            .append("ip", getIp())
            .append("cucmIp", getCucmIp())
            .append("port", getPort())
            .append("status", getStatus())
            .append("capacity", getCapacity())
            .append("spareMcuId", getSpareMcuId())
            .append("mcuDomain", getMcuDomain())
            .append("callPort", getCallPort())
            .append("proxyHost", getProxyHost())
            .append("proxyPort", getProxyPort())
            .append("scUrl", getScUrl())
            .toString();
    }
}

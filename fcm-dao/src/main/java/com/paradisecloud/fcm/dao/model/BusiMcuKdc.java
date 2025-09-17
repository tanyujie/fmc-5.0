package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 宝利通MCU终端信息对象 busi_mcu_kdc
 * 
 * @author lilinhai
 * @date 2022-10-10
 */
@Schema(description = "宝利通MCU终端信息")
public class BusiMcuKdc extends BaseEntity
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

    /** admin用户名 */
    @Schema(description = "admin用户名")
    @Excel(name = "admin用户名")
    private String adminUsername;

    /** admin密码 */
    @Schema(description = "admin密码")
    @Excel(name = "admin密码")
    private String adminPassword;

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

    /** 开发者KEY */
    @Schema(description = "开发者KEY")
    @Excel(name = "开发者KEY")
    private String devKey;

    /** 开发者VALUE */
    @Schema(description = "开发者VALUE")
    @Excel(name = "开发者VALUE")
    private String devValue;

    /** MCU域 */
    @Schema(description = "MCU域")
    @Excel(name = "MCU域")
    private String mcuDomain;

    /** 呼叫端口号 */
    @Schema(description = "呼叫端口号")
    @Excel(name = "呼叫端口号")
    private Integer callPort;

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
    public void setAdminUsername(String adminUsername) 
    {
        this.adminUsername = adminUsername;
    }

    public String getAdminUsername() 
    {
        return adminUsername;
    }
    public void setAdminPassword(String adminPassword) 
    {
        this.adminPassword = adminPassword;
    }

    public String getAdminPassword() 
    {
        return adminPassword;
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

    public String getDevKey() {
        return devKey;
    }

    public void setDevKey(String devKey) {
        this.devKey = devKey;
    }

    public String getDevValue() {
        return devValue;
    }

    public void setDevValue(String devValue) {
        this.devValue = devValue;
    }

    public String getMcuDomain() {
        return mcuDomain;
    }

    public void setMcuDomain(String mcuDomain) {
        this.mcuDomain = mcuDomain;
    }

    public Integer getCallPort() {
        return callPort;
    }

    public void setCallPort(Integer callPort) {
        this.callPort = callPort;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("username", getUsername())
            .append("password", getPassword())
            .append("adminUsername", getAdminUsername())
            .append("adminPassword", getAdminPassword())
            .append("name", getName())
            .append("ip", getIp())
            .append("cucmIp", getCucmIp())
            .append("port", getPort())
            .append("status", getStatus())
            .append("capacity", getCapacity())
            .append("spareMcuId", getSpareMcuId())
            .append("devKey", getDevKey())
            .append("devValue", getDevValue())
            .append("mcuDomain", getMcuDomain())
            .append("callPort", getCallPort())
            .toString();
    }
}

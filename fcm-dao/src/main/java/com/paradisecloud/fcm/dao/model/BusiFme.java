package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * FME终端信息对象 busi_fme
 * 
 * @author lilinhai
 * @date 2022-05-17
 */
@Schema(description = "FME终端信息")
public class BusiFme extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** FME的连接用户名，同一个组下的用户名相同 */
    @Schema(description = "FME的连接用户名，同一个组下的用户名相同")
    @Excel(name = "FME的连接用户名，同一个组下的用户名相同")
    private String username;

    /** fme显示名字 */
    @Schema(description = "fme显示名字")
    @Excel(name = "fme显示名字")
    private String name;

    /** FME的连接密码，同一个组下的密码相同 */
    @Schema(description = "FME的连接密码，同一个组下的密码相同")
    @Excel(name = "FME的连接密码，同一个组下的密码相同")
    private String password;

    /** 设备的IP地址 */
    @Schema(description = "设备的IP地址")
    @Excel(name = "设备的IP地址")
    private String ip;

    /** admin用户名 */
    @Schema(description = "admin用户名")
    @Excel(name = "admin用户名")
    private String adminUsername;

    /** fme端口 */
    @Schema(description = "fme端口")
    @Excel(name = "fme端口")
    private Integer port;

    /** 该IP是增强音视频效果 */
    @Schema(description = "该IP是增强音视频效果")
    @Excel(name = "该IP是增强音视频效果")
    private String cucmIp;

    /** admin密码 */
    @Schema(description = "admin密码")
    @Excel(name = "admin密码")
    private String adminPassword;

    /** FME在线状态：1在线，2离线，3删除 */
    @Schema(description = "FME在线状态：1在线，2离线，3删除")
    @Excel(name = "FME在线状态：1在线，2离线，3删除")
    private Integer status;

    /** FME容量 */
    @Schema(description = "FME容量")
    @Excel(name = "FME容量")
    private Integer capacity;

    /** 备用FME（本节点宕机后指向的备用节点） */
    @Schema(description = "备用FME（本节点宕机后指向的备用节点）")
    @Excel(name = "备用FME", readConverterExp = "本=节点宕机后指向的备用节点")
    private Long spareFmeId;

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
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }
    public void setAdminUsername(String adminUsername) 
    {
        this.adminUsername = adminUsername;
    }

    public String getAdminUsername() 
    {
        return adminUsername;
    }
    public void setPort(Integer port) 
    {
        this.port = port;
    }

    public Integer getPort() 
    {
        return port;
    }
    public void setCucmIp(String cucmIp) 
    {
        this.cucmIp = cucmIp;
    }

    public String getCucmIp() 
    {
        return cucmIp;
    }
    public void setAdminPassword(String adminPassword) 
    {
        this.adminPassword = adminPassword;
    }

    public String getAdminPassword() 
    {
        return adminPassword;
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
    public void setSpareFmeId(Long spareFmeId) 
    {
        this.spareFmeId = spareFmeId;
    }

    public Long getSpareFmeId() 
    {
        return spareFmeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("username", getUsername())
            .append("name", getName())
            .append("password", getPassword())
            .append("ip", getIp())
            .append("adminUsername", getAdminUsername())
            .append("port", getPort())
            .append("cucmIp", getCucmIp())
            .append("adminPassword", getAdminPassword())
            .append("status", getStatus())
            .append("capacity", getCapacity())
            .append("spareFmeId", getSpareFmeId())
            .toString();
    }
}
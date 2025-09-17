package com.paradisecloud.fcm.telep.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * tele终端信息对象 busi_tele
 * 
 * @author lilinhai
 * @date 2022-10-11
 */
@Schema(description = "tele终端信息")
public class BusiTele extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** admin用户名 */
    @Schema(description = "admin用户名")
    @Excel(name = "admin用户名")
    private String adminUsername;

    /** admin密码 */
    @Schema(description = "admin密码")
    @Excel(name = "admin密码")
    private String adminPassword;

    /** tele显示名字 */
    @Schema(description = "tele显示名字")
    @Excel(name = "tele显示名字")
    private String name;

    /** 设备的IP地址 */
    @Schema(description = "设备的IP地址")
    @Excel(name = "设备的IP地址")
    private String ip;

    /** tele端口 */
    @Schema(description = "tele端口")
    @Excel(name = "tele端口")
    private Integer port;

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
    private Long spareTeleId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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
    public void setSpareTeleId(Long spareTeleId) 
    {
        this.spareTeleId = spareTeleId;
    }

    public Long getSpareTeleId() 
    {
        return spareTeleId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("adminUsername", getAdminUsername())
            .append("adminPassword", getAdminPassword())
            .append("name", getName())
            .append("ip", getIp())
            .append("port", getPort())
            .append("status", getStatus())
            .append("capacity", getCapacity())
            .append("spareTeleId", getSpareTeleId())
            .toString();
    }
}

package com.paradisecloud.smc.dao.model;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author nj
 * @date 2022/8/24 10:50
 */
public class BusiSmc extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 系统管理员用户 */
    @Schema(description = "系统管理员用户")
    @Excel(name = "系统管理员用户")
    private String username;

    /** 系统管理员密码 */
    @Schema(description = "系统管理员密码")
    @Excel(name = "系统管理员密码")
    private String password;

    /** 会议管理员用户名 */
    @Schema(description = "会议管理员用户名")
    @Excel(name = "会议管理员用户名")
    private String meetingUsername;

    /** 会议管理员密码 */
    @Schema(description = "会议管理员密码")
    @Excel(name = "会议管理员密码")
    private String meetingPassword;

    /** smc显示名字 */
    @Schema(description = "smc显示名字")
    @Excel(name = "smc显示名字")
    private String name;

    /** 设备的IP地址 */
    @Schema(description = "设备的IP地址")
    @Excel(name = "设备的IP地址")
    private String ip;

    /** SC设备的IP地址 */
    @Schema(description = "SC设备的IP地址")
    @Excel(name = "SC设备的IP地址")
    private String scIp;

    /** smc在线状态：1在线，2离线，3删除 */
    @Schema(description = "smc在线状态：1在线，2离线，3删除")
    @Excel(name = "smc在线状态：1在线，2离线，3删除")
    private Integer status;

    /** FME容量 */
    @Schema(description = "FME容量")
    @Excel(name = "FME容量")
    private Integer capacity;

    /** 备用SMC（本节点宕机后指向的备用节点） */
    @Schema(description = "备用SMC（本节点宕机后指向的备用节点）")
    @Excel(name = "备用SMC", readConverterExp = "本=节点宕机后指向的备用节点")
    private Long spareSmcId;

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
    public void setScIp(String scIp)
    {
        this.scIp = scIp;
    }

    public String getScIp()
    {
        return scIp;
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
    public void setSpareSmcId(Long spareSmcId)
    {
        this.spareSmcId = spareSmcId;
    }

    public Long getSpareSmcId()
    {
        return spareSmcId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("username", getUsername())
                .append("password", getPassword())
                .append("meetingUsername", getMeetingUsername())
                .append("meetingPassword", getMeetingPassword())
                .append("name", getName())
                .append("ip", getIp())
                .append("scIp", getScIp())
                .append("status", getStatus())
                .append("capacity", getCapacity())
                .append("spareSmcId", getSpareSmcId())
                .toString();
    }
}

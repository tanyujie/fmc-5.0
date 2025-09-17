package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 直播服务器信息对象 busi_live
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
@Schema(description = "直播服务器信息")
public class BusiLive extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 显示名字 */
    @Schema(description = "显示名字")
    @Excel(name = "显示名字")
    private String name;

    /** 设备的IP地址 */
    @Schema(description = "设备的IP地址")
    @Excel(name = "设备的IP地址")
    private String ip;

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

    /** 直播路径 */
    @Schema(description = "直播路径")
    @Excel(name = "直播路径")
    private String uriPath;

    /** 协议类型 */
    @Schema(description = "协议类型")
    @Excel(name = "协议类型")
    private String protocolType;

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
    public void setUriPath(String uriPath) 
    {
        this.uriPath = uriPath;
    }

    public String getUriPath() 
    {
        return uriPath;
    }
    public void setProtocolType(String protocolType) 
    {
        this.protocolType = protocolType;
    }

    public String getProtocolType() 
    {
        return protocolType;
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
            .append("ip", getIp())
            .append("port", getPort())
            .append("status", getStatus())
            .append("capacity", getCapacity())
            .append("uriPath", getUriPath())
            .append("protocolType", getProtocolType())
            .append("domainName", getDomainName())
            .toString();
    }
}

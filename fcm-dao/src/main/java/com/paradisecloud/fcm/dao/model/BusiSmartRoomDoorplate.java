package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 智慧办公房间门牌对象 busi_smart_room_doorplate
 *
 * @author lilinhai
 * @date 2024-02-19
 */
@Schema(description = "智慧办公房间门牌")
public class BusiSmartRoomDoorplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 名称 */
    @Schema(description = "名称")
    @Excel(name = "名称")
    private String name;

    /** 序列号 */
    @Schema(description = "序列号")
    @Excel(name = "序列号")
    private String sn;

    /** IP地址 */
    @Schema(description = "IP地址")
    @Excel(name = "IP地址")
    private String ip;

    /** mqtt连接状态：1在线，2离线 */
    @Schema(description = "mqtt连接状态：1在线，2离线")
    @Excel(name = "mqtt连接状态：1在线，2离线")
    private Integer mqttOnlineStatus;

    /** APP版本号 */
    @Schema(description = "APP版本号")
    @Excel(name = "APP版本号")
    private String appVersionCode;

    /** APP版本名 */
    @Schema(description = "APP版本名")
    @Excel(name = "APP版本名")
    private String appVersionName;

    /** 软件类型：与busi_terminal_upgrade中terminal_type等同 */
    @Schema(description = "软件类型：与busi_terminal_upgrade中terminal_type等同")
    @Excel(name = "软件类型：与busi_terminal_upgrade中terminal_type等同")
    private String appType;

    /** 连接IP */
    @Schema(description = "连接IP")
    @Excel(name = "连接IP")
    private String connectIp;

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
    public void setSn(String sn)
    {
        this.sn = sn;
    }

    public String getSn()
    {
        return sn;
    }
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getIp()
    {
        return ip;
    }
    public void setMqttOnlineStatus(Integer mqttOnlineStatus)
    {
        this.mqttOnlineStatus = mqttOnlineStatus;
    }

    public Integer getMqttOnlineStatus()
    {
        return mqttOnlineStatus;
    }
    public void setAppVersionCode(String appVersionCode)
    {
        this.appVersionCode = appVersionCode;
    }

    public String getAppVersionCode()
    {
        return appVersionCode;
    }
    public void setAppVersionName(String appVersionName)
    {
        this.appVersionName = appVersionName;
    }

    public String getAppVersionName()
    {
        return appVersionName;
    }
    public void setAppType(String appType)
    {
        this.appType = appType;
    }

    public String getAppType()
    {
        return appType;
    }
    public void setConnectIp(String connectIp)
    {
        this.connectIp = connectIp;
    }

    public String getConnectIp()
    {
        return connectIp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("sn", getSn())
                .append("ip", getIp())
                .append("mqttOnlineStatus", getMqttOnlineStatus())
                .append("appVersionCode", getAppVersionCode())
                .append("appVersionName", getAppVersionName())
                .append("appType", getAppType())
                .append("connectIp", getConnectIp())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("remark", getRemark())
                .toString();
    }
}
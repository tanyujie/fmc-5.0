package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 客户端对象 busi_client
 * 
 * @author lilinhai
 * @date 2024-07-26
 */
@Schema(description = "客户端")
public class BusiClient extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 序列号 */
    @Schema(description = "序列号")
    @Excel(name = "序列号")
    private String sn;

    /** 名称 */
    @Schema(description = "名称")
    @Excel(name = "名称")
    private String name;

    /** IP */
    @Schema(description = "IP")
    @Excel(name = "IP")
    private String ip;

    /** MQTT在线状态 */
    @Schema(description = "MQTT在线状态")
    @Excel(name = "MQTT在线状态")
    private Integer mqttOnlineStatus;

    /** APP版本号 */
    @Schema(description = "APP版本号")
    @Excel(name = "APP版本号")
    private String appVersionCode;

    /** APP版本名 */
    @Schema(description = "APP版本名")
    @Excel(name = "APP版本名")
    private String appVersionName;

    /** 连接IP */
    @Schema(description = "连接IP")
    @Excel(name = "连接IP")
    private String connectIp;

    /** 过期时间 */
    @Schema(description = "过期时间")
    @Excel(name = "过期时间", width = 30)
    private Date expiredDate;

    /** 创建用户Id */
    @Schema(description = "创建用户Id")
    @Excel(name = "创建用户Id")
    private Long createUserId;

    /** 更新用户ID */
    @Schema(description = "更新用户ID")
    @Excel(name = "更新用户ID")
    private Long updateUserId;

    /** 用户ID */
    @Schema(description = "用户ID")
    @Excel(name = "用户ID")
    private Long userId;

    /** 资源ID */
    @Schema(description = "资源ID")
    @Excel(name = "资源ID")
    private Long sourceId;

    /** 最后在线时间 */
    @Schema(description = "最后在线时间")
    @Excel(name = "最后在线时间", width = 30)
    private Date lastOnlineTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSn(String sn) 
    {
        this.sn = sn;
    }

    public String getSn() 
    {
        return sn;
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

    public String getAppVersionName() {
        return appVersionName;
    }
    public void setConnectIp(String connectIp)
    {
        this.connectIp = connectIp;
    }

    public String getConnectIp() 
    {
        return connectIp;
    }
    public void setExpiredDate(Date expiredDate) 
    {
        this.expiredDate = expiredDate;
    }

    public Date getExpiredDate() 
    {
        return expiredDate;
    }
    public void setCreateUserId(Long createUserId) 
    {
        this.createUserId = createUserId;
    }

    public Long getCreateUserId() 
    {
        return createUserId;
    }
    public void setUpdateUserId(Long updateUserId) 
    {
        this.updateUserId = updateUserId;
    }

    public Long getUpdateUserId() 
    {
        return updateUserId;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setSourceId(Long sourceId) 
    {
        this.sourceId = sourceId;
    }

    public Long getSourceId() 
    {
        return sourceId;
    }

    public Date getLastOnlineTime() {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(Date lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("sn", getSn())
            .append("name", getName())
            .append("ip", getIp())
            .append("mqttOnlineStatus", getMqttOnlineStatus())
            .append("appVersionCode", getAppVersionCode())
            .append("appVersionName", getAppVersionName())
            .append("connectIp", getConnectIp())
            .append("expiredDate", getExpiredDate())
            .append("createTime", getCreateTime())
            .append("createUserId", getCreateUserId())
            .append("updateTime", getUpdateTime())
            .append("updateUserId", getUpdateUserId())
            .append("userId", getUserId())
            .append("sourceId", getSourceId())
            .append("lastOnlineTime", getLastOnlineTime())
            .toString();
    }
}

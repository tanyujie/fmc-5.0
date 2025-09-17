package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 智慧办公房间门牌注册对象 busi_smart_room_doorplate_register
 *
 * @author lilinhai
 * @date 2024-02-19
 */
@Schema(description = "智慧办公房间门牌注册")
public class BusiSmartRoomDoorplateRegister extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 序列号 */
    @Schema(description = "序列号")
    @Excel(name = "序列号")
    private String sn;

    /** IP地址 */
    @Schema(description = "IP地址")
    @Excel(name = "IP地址")
    private String ip;

    /** APP版本号 */
    @Schema(description = "APP版本号")
    @Excel(name = "APP版本号")
    private String appVersionCode;

    /** APP版本号 */
    @Schema(description = "APP版本号")
    @Excel(name = "APP版本号")
    private String appVersionName;

    /** 连接IP */
    @Schema(description = "连接IP")
    @Excel(name = "连接IP")
    private String connectIp;

    /** 软件类型：与busi_terminal_upgrade中terminal_type等同 */
    @Schema(description = "软件类型：与busi_terminal_upgrade中terminal_type等同")
    @Excel(name = "软件类型：与busi_terminal_upgrade中terminal_type等同")
    private String appType;

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
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getIp()
    {
        return ip;
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
    public void setConnectIp(String connectIp)
    {
        this.connectIp = connectIp;
    }

    public String getConnectIp()
    {
        return connectIp;
    }

    public void setAppType(String appType)
    {
        this.appType = appType;
    }

    public String getAppType()
    {
        return appType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("sn", getSn())
                .append("ip", getIp())
                .append("appVersionCode", getAppVersionCode())
                .append("appVersionName", getAppVersionName())
                .append("connectIp", getConnectIp())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("appType", getAppType())
                .toString();
    }
}
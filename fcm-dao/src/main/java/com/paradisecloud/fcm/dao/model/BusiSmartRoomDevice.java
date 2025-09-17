package com.paradisecloud.fcm.dao.model;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 智慧办公房间设备对象 busi_smart_room_device
 *
 * @author lilinhai
 * @date 2024-02-19
 */
@Schema(description = "智慧办公房间设备")
public class BusiSmartRoomDevice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 设备名 */
    @Schema(description = "设备名")
    @Excel(name = "设备名")
    private String deviceName;

    /** 设备类型 0：电子门牌 1：物联网网关 2：物联网设备 999：其它设备 */
    @Schema(description = "设备类型 0：电子门牌 1：物联网网关 2：物联网设备 999：其它设备")
    @Excel(name = "设备类型 0：电子门牌 1：物联网网关 2：物联网设备 999：其它设备")
    private Integer deviceType;

    /** 设备分类 （当设备类型为999：其它设备时，设备分类为设备分类表中id） */
    @Schema(description = "设备分类 （当设备类型为999：其它设备时，设备分类为设备分类表中id）")
    @Excel(name = "设备分类 ", readConverterExp = "设备分类 （当设备类型为999：其它设备时，设备分类为设备分类表中id）")
    private Long deviceClassify;

    /** 状态 */
    @Schema(description = "状态")
    @Excel(name = "状态")
    private Integer status;

    /** 在线状态：1在线，2离线 */
    @Schema(description = "在线状态：1在线，2离线")
    @Excel(name = "在线状态：1在线，2离线")
    private Integer onlineStatus;

    /** 品牌 */
    @Schema(description = "品牌")
    @Excel(name = "品牌")
    private String brand;

    /** 设备型号 */
    @Schema(description = "设备型号")
    @Excel(name = "设备型号")
    private String deviceModel;

    /** 投用时间 */
    @Schema(description = "投用时间")
    @Excel(name = "投用时间", width = 30)
    private Date appliedTime;

    /** 硬件版本 */
    @Schema(description = "硬件版本")
    @Excel(name = "硬件版本")
    private String hardwareVersion;

    /** 软件版本 */
    @Schema(description = "软件版本")
    @Excel(name = "软件版本")
    private String softwareVersion;

    /** 物联网关ID (当日设备类型为物联网设备时) */
    @Schema(description = "物联网关ID (当日设备类型为物联网设备时)")
    @Excel(name = "物联网关ID (当日设备类型为物联网设备时)")
    private Long lotId;

    /** 物联网关通道 (设备连上物联网关的通道号) */
    @Schema(description = "物联网通道 (设备连上物联网关的通道号)")
    @Excel(name = "物联网关通道 (设备连上物联网关的通道号)")
    private Integer lotChannel;

    /** 物联网设备类型 */
    @Schema(description = "物联网设备类型")
    @Excel(name = "物联网设备类型")
    private Integer lotDeviceType;

    /** 详情（更多设备信息） */
    @Schema(description = "详情（更多设备信息）")
    @Excel(name = "详情（更多设备信息）")
    private Map<String, Object> details;

    /** 绑定Id */
    @Schema(description = "绑定Id")
    private String bindId;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public String getDeviceName()
    {
        return deviceName;
    }
    public void setDeviceType(Integer deviceType)
    {
        this.deviceType = deviceType;
    }

    public Integer getDeviceType()
    {
        return deviceType;
    }
    public void setDeviceClassify(Long deviceClassify)
    {
        this.deviceClassify = deviceClassify;
    }

    public Long getDeviceClassify()
    {
        return deviceClassify;
    }
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }
    public void setOnlineStatus(Integer onlineStatus)
    {
        this.onlineStatus = onlineStatus;
    }

    public Integer getOnlineStatus()
    {
        return onlineStatus;
    }
    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public String getBrand()
    {
        return brand;
    }
    public void setDeviceModel(String deviceModel)
    {
        this.deviceModel = deviceModel;
    }

    public String getDeviceModel()
    {
        return deviceModel;
    }
    public void setAppliedTime(Date appliedTime)
    {
        this.appliedTime = appliedTime;
    }

    public Date getAppliedTime()
    {
        return appliedTime;
    }
    public void setHardwareVersion(String hardwareVersion)
    {
        this.hardwareVersion = hardwareVersion;
    }

    public String getHardwareVersion()
    {
        return hardwareVersion;
    }
    public void setSoftwareVersion(String softwareVersion)
    {
        this.softwareVersion = softwareVersion;
    }

    public String getSoftwareVersion()
    {
        return softwareVersion;
    }

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public Integer getLotChannel() {
        return lotChannel;
    }

    public void setLotChannel(Integer lotChannel) {
        this.lotChannel = lotChannel;
    }

    public Integer getLotDeviceType() {
        return lotDeviceType;
    }

    public void setLotDeviceType(Integer lotDeviceType) {
        this.lotDeviceType = lotDeviceType;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public String getBindId() {
        return bindId;
    }

    public void setBindId(String bindId) {
        this.bindId = bindId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("deviceName", getDeviceName())
                .append("deviceType", getDeviceType())
                .append("deviceClassify", getDeviceClassify())
                .append("status", getStatus())
                .append("onlineStatus", getOnlineStatus())
                .append("brand", getBrand())
                .append("deviceModel", getDeviceModel())
                .append("appliedTime", getAppliedTime())
                .append("hardwareVersion", getHardwareVersion())
                .append("softwareVersion", getSoftwareVersion())
                .append("remark", getRemark())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .append("lotId", getLotId())
                .append("lotChannel", getLotChannel())
                .append("lotDeviceType", getLotDeviceType())
                .append("details", getDetails())
                .append("bindId", getBindId())
                .toString();
    }
}

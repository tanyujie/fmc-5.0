package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 智慧办公房间设备分类对象 busi_smart_room_device_classify
 *
 * @author lilinhai
 * @date 2024-02-19
 */
@Schema(description = "智慧办公房间设备分类")
public class BusiSmartRoomDeviceClassify extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 设备类型（1：物联网设备 999：其它设备） */
    @Schema(description = "设备类型（1：物联网设备 999：其它设备）")
    @Excel(name = "设备类型", readConverterExp = "1=：物联网设备,9=99：其它设备")
    private Integer deviceType;

    /** 设备分类名 */
    @Schema(description = "设备分类名")
    @Excel(name = "设备分类名")
    private String deviceClassifyName;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setDeviceType(Integer deviceType)
    {
        this.deviceType = deviceType;
    }

    public Integer getDeviceType()
    {
        return deviceType;
    }
    public void setDeviceClassifyName(String deviceClassifyName)
    {
        this.deviceClassifyName = deviceClassifyName;
    }

    public String getDeviceClassifyName()
    {
        return deviceClassifyName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("deviceType", getDeviceType())
                .append("deviceClassifyName", getDeviceClassifyName())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * 智慧办公房间设备关联对象 busi_smart_room_device_map
 *
 * @author lilinhai
 * @date 2024-02-19
 */
@Schema(description = "智慧办公房间设备关联")
public class BusiSmartRoomDeviceMap extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 房间ID */
    @Schema(description = "房间ID")
    @Excel(name = "房间ID")
    private Long roomId;

    /** 设备ID 当设备类型为0：电子门牌 时，设备ID为电子门牌ID 当设备类型为1：物联网网关时，设备ID为物联网网关ID */
    @Schema(description = "设备ID 当设备类型为0：电子门牌 时，设备ID为电子门牌ID 当设备类型为1：物联网网关时，设备ID为物联网网关ID")
    @Excel(name = "设备ID 当设备类型为0：电子门牌 时，设备ID为电子门牌ID 当设备类型为1：物联网网关时，设备ID为物联网网关ID")
    private Long deviceId;

    /** 设备类型 0：电子门牌 1：物联网网关 2：物联网设备  999：其它设备 */
    @Schema(description = "设备类型 0：电子门牌 1：物联网网关 2：物联网设备  999：其它设备")
    @Excel(name = "设备类型 0：电子门牌 1：物联网网关 2：物联网设备  999：其它设备")
    private Integer deviceType;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setRoomId(Long roomId)
    {
        this.roomId = roomId;
    }

    public Long getRoomId()
    {
        return roomId;
    }
    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId()
    {
        return deviceId;
    }
    public void setDeviceType(Integer deviceType)
    {
        this.deviceType = deviceType;
    }

    public Integer getDeviceType()
    {
        return deviceType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("roomId", getRoomId())
                .append("deviceId", getDeviceId())
                .append("deviceType", getDeviceType())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .toString();
    }
}
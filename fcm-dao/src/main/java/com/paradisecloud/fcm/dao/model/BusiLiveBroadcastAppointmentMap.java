package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 直播会议对应对象 busi_live_broadcast_appointment_map
 *
 * @author lilinhai
 * @date 2024-05-07
 */
@Schema(description = "直播会议对应")
public class BusiLiveBroadcastAppointmentMap extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 直播id */
    @Schema(description = "直播id")
    @Excel(name = "直播id")
    private Long liveBroadcastId;

    /** mcu类型 */
    @Schema(description = "mcu类型")
    @Excel(name = "mcu类型")
    private String mcuType;

    /** 预约会议id */
    @Schema(description = "预约会议id")
    @Excel(name = "预约会议id")
    private Long appointmentId;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setLiveBroadcastId(Long liveBroadcastId)
    {
        this.liveBroadcastId = liveBroadcastId;
    }

    public Long getLiveBroadcastId()
    {
        return liveBroadcastId;
    }
    public void setMcuType(String mcuType)
    {
        this.mcuType = mcuType;
    }

    public String getMcuType()
    {
        return mcuType;
    }
    public void setAppointmentId(Long appointmentId)
    {
        this.appointmentId = appointmentId;
    }

    public Long getAppointmentId()
    {
        return appointmentId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("liveBroadcastId", getLiveBroadcastId())
                .append("mcuType", getMcuType())
                .append("appointmentId", getAppointmentId())
                .toString();
    }
}

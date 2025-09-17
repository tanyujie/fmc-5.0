package com.paradisecloud.fcm.dao.model;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 智慧办公房间预约对象 busi_smart_room_book
 *
 * @author lilinhai
 * @date 2024-02-19
 */
@Schema(description = "智慧办公房间预约")
public class BusiSmartRoomBook extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 房间ID */
    @Schema(description = "房间ID")
    @Excel(name = "房间ID")
    private Long roomId;

    /** 预约名 */
    @Schema(description = "预约名")
    @Excel(name = "预约名")
    private String bookName;

    /** 开始时间 */
    @Schema(description = "开始时间")
    @Excel(name = "开始时间", width = 30)
    private Date startTime;

    /** 结束时间 */
    @Schema(description = "结束时间")
    @Excel(name = "结束时间", width = 30)
    private Date endTime;

    /** 预约状态 0：预约中 1：取消预约 2：结束预约 */
    @Schema(description = "预约状态 0：预约中 1：取消预约 2：结束预约")
    @Excel(name = "预约状态 0：预约中 1：取消预约 2：结束预约")
    private Integer bookStatus;

    /** MCU类型 */
    @Schema(description = "MCU类型")
    @Excel(name = "MCU类型")
    private String mcuType;

    /** 会议模板ID */
    @Schema(description = "会议模板ID")
    @Excel(name = "会议模板ID")
    private Long appointmentConferenceId;

    /** 延长分钟数 */
    @Schema(description = "延长分钟数")
    @Excel(name = "延长分钟数")
    private Integer extendMinutes;

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
    public void setBookName(String bookName)
    {
        this.bookName = bookName;
    }

    public String getBookName()
    {
        return bookName;
    }
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getStartTime()
    {
        return startTime;
    }
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }
    public void setBookStatus(Integer bookStatus)
    {
        this.bookStatus = bookStatus;
    }

    public Integer getBookStatus()
    {
        return bookStatus;
    }

    public String getMcuType() {
        return mcuType;
    }

    public void setMcuType(String mcuType) {
        this.mcuType = mcuType;
    }

    public Long getAppointmentConferenceId() {
        return appointmentConferenceId;
    }

    public void setAppointmentConferenceId(Long appointmentConferenceId) {
        this.appointmentConferenceId = appointmentConferenceId;
    }

    public Integer getExtendMinutes() {
        return extendMinutes;
    }

    public void setExtendMinutes(Integer extendMinutes) {
        this.extendMinutes = extendMinutes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("roomId", getRoomId())
                .append("bookName", getBookName())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("bookStatus", getBookStatus())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("remark", getRemark())
                .append("mcuType", getMcuType())
                .append("appointmentConferenceId", getAppointmentConferenceId())
                .append("extendMinutes", getExtendMinutes())
                .toString();
    }
}
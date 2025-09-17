package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 会议预约记录对象 busi_mcu_plc_conference_appointment
 * 
 * @author lilinhai
 * @date 2021-10-21
 */
@Schema(description = "会议预约记录")
public class BusiMcuPlcConferenceAppointment extends BusiConferenceAppointment
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 归属租户 */
    @Schema(description = "归属租户")
    @Excel(name = "归属租户")
    private Long deptId;

    /** 关联的会议模板ID */
    @Schema(description = "关联的会议模板ID")
    @Excel(name = "关联的会议模板ID")
    private Long templateId;

    /** 会议开始时间 */
    @Schema(description = "会议开始时间")
    @Excel(name = "会议开始时间")
    private String startTime;

    /** 是否自动创建模板 */
    @Schema(description = "是否自动创建模板")
    @Excel(name = "是否自动创建模板")
    private Integer isAutoCreateTemplate;

    /** 会议结束时间 */
    @Schema(description = "会议结束时间")
    @Excel(name = "会议结束时间")
    private String endTime;

    /** 1当天，2每天，3每周，4每月 */
    @Schema(description = "1当天，2每天，3每周，4每月")
    @Excel(name = "1当天，2每天，3每周，4每月")
    private Integer repeatRate;

    /** 预约会议的状态：1启动，2停止 */
    @Schema(description = "预约会议的状态：1启动，2停止")
    @Excel(name = "预约会议的状态：1启动，2停止")
    private Integer status;

    /** 延长分钟数 */
    @Schema(description = "延长分钟数")
    @Excel(name = "延长分钟数")
    private Integer extendMinutes;

    /** repeat_rate为3，范围为[1-7]，repeat_rate为4范围是1-30 */
    @Schema(description = "repeat_rate为3，范围为[1-7]，repeat_rate为4范围是1-30")
    @Excel(name = "repeat_rate为3，范围为[1-7]，repeat_rate为4范围是1-30")
    private Integer repeatDate;

    /** 是否主动挂断 */
    @Schema(description = "是否主动挂断")
    @Excel(name = "是否主动挂断")
    private Integer isHangUp;

    /** 入会密码 */
    @Schema(description = "入会密码")
    @Excel(name = "入会密码")
    private String password;

    /** 参会者上限 */
    @Schema(description = "参会者上限")
    @Excel(name = "参会者上限")
    private Integer attendeeLimit;

    /** 会议是否开始 */
    @Schema(description = "会议是否开始")
    @Excel(name = "会议是否开始")
    private Integer isStart;
    
    /** 启动失败原因 */
    @Schema(description = "启动失败原因")
    @Excel(name = "启动失败原因")
    private String startFailedReason;

    /** 会议类型:1:预约会议;2:即时会议 */
    @Schema(description = "会议类型:1:预约会议;2:即时会议")
    private Integer type;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setTemplateId(Long templateId) 
    {
        this.templateId = templateId;
    }

    public Long getTemplateId() 
    {
        return templateId;
    }
    public void setStartTime(String startTime) 
    {
        this.startTime = startTime;
    }

    public String getStartTime() 
    {
        return startTime;
    }
    public void setIsAutoCreateTemplate(Integer isAutoCreateTemplate) 
    {
        this.isAutoCreateTemplate = isAutoCreateTemplate;
    }

    public Integer getIsAutoCreateTemplate() 
    {
        return isAutoCreateTemplate;
    }
    public void setEndTime(String endTime) 
    {
        this.endTime = endTime;
    }

    public String getEndTime() 
    {
        return endTime;
    }
    public void setRepeatRate(Integer repeatRate) 
    {
        this.repeatRate = repeatRate;
    }

    public Integer getRepeatRate() 
    {
        return repeatRate;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setExtendMinutes(Integer extendMinutes) 
    {
        this.extendMinutes = extendMinutes;
    }

    public Integer getExtendMinutes() 
    {
        return extendMinutes;
    }
    public void setRepeatDate(Integer repeatDate) 
    {
        this.repeatDate = repeatDate;
    }

    public Integer getRepeatDate() 
    {
        return repeatDate;
    }
    public void setIsHangUp(Integer isHangUp) 
    {
        this.isHangUp = isHangUp;
    }

    public Integer getIsHangUp() 
    {
        return isHangUp;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    public void setAttendeeLimit(Integer attendeeLimit) 
    {
        this.attendeeLimit = attendeeLimit;
    }

    public Integer getAttendeeLimit() 
    {
        return attendeeLimit;
    }
    public void setIsStart(Integer isStart) 
    {
        this.isStart = isStart;
    }

    public Integer getIsStart() 
    {
        return isStart;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * <p>Get Method   :   startFailedReason String</p>
     * @return startFailedReason
     */
    public String getStartFailedReason()
    {
        return startFailedReason;
    }

    /**
     * <p>Set Method   :   startFailedReason String</p>
     * @param startFailedReason
     */
    public void setStartFailedReason(String startFailedReason)
    {
        this.startFailedReason = startFailedReason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("templateId", getTemplateId())
            .append("startTime", getStartTime())
            .append("isAutoCreateTemplate", getIsAutoCreateTemplate())
            .append("endTime", getEndTime())
            .append("repeatRate", getRepeatRate())
            .append("status", getStatus())
            .append("extendMinutes", getExtendMinutes())
            .append("repeatDate", getRepeatDate())
            .append("isHangUp", getIsHangUp())
            .append("password", getPassword())
            .append("attendeeLimit", getAttendeeLimit())
            .append("isStart", getIsStart())
            .append("startFailedReason", getStartFailedReason())
            .append("type", getType())
            .toString();
    }
}
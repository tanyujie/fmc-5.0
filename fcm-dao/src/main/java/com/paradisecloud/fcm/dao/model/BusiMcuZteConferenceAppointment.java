package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 中兴MCU会议预约记录对象 busi_mcu_zte_conference_appointment
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
@Schema(description = "中兴MCU会议预约记录")
public class BusiMcuZteConferenceAppointment extends BusiConferenceAppointment
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

    /** 是否自动创建模板 */
    @Schema(description = "是否自动创建模板")
    @Excel(name = "是否自动创建模板")
    private Integer isAutoCreateTemplate;

    /** 会议开始时间 */
    @Schema(description = "会议开始时间")
    @Excel(name = "会议开始时间")
    private String startTime;

    /** 会议结束时间 */
    @Schema(description = "会议结束时间")
    @Excel(name = "会议结束时间")
    private String endTime;

    /** 延长分钟数 */
    @Schema(description = "延长分钟数")
    @Excel(name = "延长分钟数")
    private Integer extendMinutes;

    /** 是否主动挂断 */
    @Schema(description = "是否主动挂断")
    @Excel(name = "是否主动挂断")
    private Integer isHangUp;

    /** 预约会议的状态：1启动，2停止 */
    @Schema(description = "预约会议的状态：1启动，2停止")
    @Excel(name = "预约会议的状态：1启动，2停止")
    private Integer status;

    /** 会议是否开始 */
    @Schema(description = "会议是否开始")
    @Excel(name = "会议是否开始")
    private Integer isStart;

    /** 启动失败原因记录 */
    @Schema(description = "启动失败原因记录")
    @Excel(name = "启动失败原因记录")
    private String startFailedReason;

    /** 1自定义，2每天，3每周，4每月 */
    @Schema(description = "1自定义，2每天，3每周，4每月")
    @Excel(name = "1自定义，2每天，3每周，4每月")
    private Integer repeatRate;

    /** repeat_rate为3，范围为[1-7]，repeat_rate为4范围是1-30 */
    @Schema(description = "repeat_rate为3，范围为[1-7]，repeat_rate为4范围是1-30")
    @Excel(name = "repeat_rate为3，范围为[1-7]，repeat_rate为4范围是1-30")
    private Integer repeatDate;

    /** 入会密码 */
    @Schema(description = "入会密码")
    @Excel(name = "入会密码")
    private String password;

    /** 参会者上限 */
    @Schema(description = "参会者上限")
    @Excel(name = "参会者上限")
    private Integer attendeeLimit;

    /** 会议类型:1:预约会议;2:即时会议 */
    @Schema(description = "会议类型:1:预约会议;2:即时会议")
    @Excel(name = "会议类型:1:预约会议;2:即时会议")
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
    public void setIsAutoCreateTemplate(Integer isAutoCreateTemplate) 
    {
        this.isAutoCreateTemplate = isAutoCreateTemplate;
    }

    public Integer getIsAutoCreateTemplate() 
    {
        return isAutoCreateTemplate;
    }
    public void setStartTime(String startTime) 
    {
        this.startTime = startTime;
    }

    public String getStartTime() 
    {
        return startTime;
    }
    public void setEndTime(String endTime) 
    {
        this.endTime = endTime;
    }

    public String getEndTime() 
    {
        return endTime;
    }
    public void setExtendMinutes(Integer extendMinutes) 
    {
        this.extendMinutes = extendMinutes;
    }

    public Integer getExtendMinutes() 
    {
        return extendMinutes;
    }
    public void setIsHangUp(Integer isHangUp) 
    {
        this.isHangUp = isHangUp;
    }

    public Integer getIsHangUp() 
    {
        return isHangUp;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setIsStart(Integer isStart) 
    {
        this.isStart = isStart;
    }

    public Integer getIsStart() 
    {
        return isStart;
    }
    public void setStartFailedReason(String startFailedReason) 
    {
        this.startFailedReason = startFailedReason;
    }

    public String getStartFailedReason() 
    {
        return startFailedReason;
    }
    public void setRepeatRate(Integer repeatRate) 
    {
        this.repeatRate = repeatRate;
    }

    public Integer getRepeatRate() 
    {
        return repeatRate;
    }
    public void setRepeatDate(Integer repeatDate) 
    {
        this.repeatDate = repeatDate;
    }

    public Integer getRepeatDate() 
    {
        return repeatDate;
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
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("templateId", getTemplateId())
            .append("isAutoCreateTemplate", getIsAutoCreateTemplate())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("extendMinutes", getExtendMinutes())
            .append("isHangUp", getIsHangUp())
            .append("status", getStatus())
            .append("isStart", getIsStart())
            .append("startFailedReason", getStartFailedReason())
            .append("repeatRate", getRepeatRate())
            .append("repeatDate", getRepeatDate())
            .append("password", getPassword())
            .append("attendeeLimit", getAttendeeLimit())
            .append("type", getType())
            .toString();
    }
}

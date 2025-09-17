package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 会议预约记录对象 busi_tencent_conference_appointment
 * 
 * @author lilinhai
 * @date 2023-07-05
 */
@Schema(description = "会议预约记录")
public class BusiTencentConferenceAppointment extends BaseEntity
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
    private String startDate;

    /** 会议结束时间 */
    @Schema(description = "会议结束时间")
    @Excel(name = "会议结束时间")
    private String endDate;

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
    private Integer tencentType;

    /** 周期性会议规则 */
    @Schema(description = "周期性会议规则")
    @Excel(name = "周期性会议规则")
    private String  recurringRule;

    private String type;

    private String accessCode;

    private String subject;
    private Integer duration;
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


    public String getRecurringRule() {
        return recurringRule;
    }

    public void setRecurringRule(String recurringRule) {
        this.recurringRule = recurringRule;
    }


    public Integer getTencentType() {
        return tencentType;
    }

    public void setTencentType(Integer tencentType) {
        this.tencentType = tencentType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getIsAutoCreateTemplate() {
        return isAutoCreateTemplate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    @Override
    public String toString() {
        return "BusiTencentConferenceAppointment{" +
                "id=" + id +
                ", deptId=" + deptId +
                ", templateId=" + templateId +
                ", isAutoCreateTemplate=" + isAutoCreateTemplate +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", extendMinutes=" + extendMinutes +
                ", isHangUp=" + isHangUp +
                ", status=" + status +
                ", isStart=" + isStart +
                ", startFailedReason='" + startFailedReason + '\'' +
                ", repeatRate=" + repeatRate +
                ", repeatDate=" + repeatDate +
                ", password='" + password + '\'' +
                ", attendeeLimit=" + attendeeLimit +
                ", tencentType=" + tencentType +
                ", recurringRule='" + recurringRule + '\'' +
                ", type='" + type + '\'' +
                ", accessCode='" + accessCode + '\'' +
                ", subject='" + subject + '\'' +
                ", duration=" + duration +
                '}';
    }
}

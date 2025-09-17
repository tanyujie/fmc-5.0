package com.paradisecloud.fcm.dao.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 会议审批对象 busi_conference_approval
 * 
 * @author lilinhai
 * @date 2023-11-27
 */
@Schema(description = "会议审批")
public class BusiConferenceApproval extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 预约会议ID */
    @Schema(description = "预约会议ID")
    @Excel(name = "预约会议ID")
    private Long appointmentConferenceId;

    /** MCU类型 */
    @Schema(description = "MCU类型")
    @Excel(name = "MCU类型")
    private String mcuType;

    /** 会议名 */
    @Schema(description = "会议名")
    @Excel(name = "会议名")
    private String conferenceName;

    /** 部门ID */
    @Schema(description = "部门ID")
    @Excel(name = "部门ID")
    private Long deptId;

    /** 审批状态 0：待审批 1：审批通过 2：审批不通过 3：会议删除 */
    @Schema(description = "审批状态 0：待审批 1：审批通过 2：审批不通过 3：会议删除")
    @Excel(name = "审批状态 0：待审批 1：审批通过 2：审批不通过 3：会议删除")
    private Integer approvalStatus;

    /** 审批未通过原因 */
    @Schema(description = "审批未通过原因")
    @Excel(name = "审批未通过原因")
    private String approvalFailReason;

    /** 审批用户ID */
    @Schema(description = "审批用户ID")
    @Excel(name = "审批用户ID")
    private Long approvalUserId;

    /** 审批人 */
    @Schema(description = "审批人")
    @Excel(name = "审批人")
    private String approvalBy;

    /** 审批时间 */
    @Schema(description = "审批时间")
    @Excel(name = "审批时间", width = 30)
    private Date approvalTime;

    /** 创建用户ID */
    @Schema(description = "创建用户ID")
    @Excel(name = "创建用户ID")
    private String createUserId;

    /** 会议详情 */
    @Schema(description = "会议详情")
    @Excel(name = "会议详情")
    private Map<String, Object> conferenceDetail;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setAppointmentConferenceId(Long appointmentConferenceId) 
    {
        this.appointmentConferenceId = appointmentConferenceId;
    }

    public Long getAppointmentConferenceId() 
    {
        return appointmentConferenceId;
    }

    public String getMcuType() {
        return mcuType;
    }

    public void setMcuType(String mcuType) {
        this.mcuType = mcuType;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setApprovalStatus(Integer approvalStatus) 
    {
        this.approvalStatus = approvalStatus;
    }

    public Integer getApprovalStatus() 
    {
        return approvalStatus;
    }
    public void setApprovalFailReason(String approvalFailReason) 
    {
        this.approvalFailReason = approvalFailReason;
    }

    public String getApprovalFailReason() 
    {
        return approvalFailReason;
    }
    public void setApprovalUserId(Long approvalUserId) 
    {
        this.approvalUserId = approvalUserId;
    }

    public Long getApprovalUserId() 
    {
        return approvalUserId;
    }
    public void setApprovalBy(String approvalBy) 
    {
        this.approvalBy = approvalBy;
    }

    public String getApprovalBy() 
    {
        return approvalBy;
    }
    public void setApprovalTime(Date approvalTime) 
    {
        this.approvalTime = approvalTime;
    }

    public Date getApprovalTime() 
    {
        return approvalTime;
    }
    public void setCreateUserId(String createUserId) 
    {
        this.createUserId = createUserId;
    }

    public String getCreateUserId() 
    {
        return createUserId;
    }
    public void setConferenceDetail(Map<String, Object> conferenceDetail)
    {
        this.conferenceDetail = conferenceDetail;
    }

    public Map<String, Object> getConferenceDetail()
    {
        return conferenceDetail;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("appointmentConferenceId", getAppointmentConferenceId())
            .append("mcuType", getMcuType())
            .append("conferenceName", getConferenceName())
            .append("deptId", getDeptId())
            .append("approvalStatus", getApprovalStatus())
            .append("approvalFailReason", getApprovalFailReason())
            .append("approvalUserId", getApprovalUserId())
            .append("approvalBy", getApprovalBy())
            .append("approvalTime", getApprovalTime())
            .append("createUserId", getCreateUserId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("conferenceDetail", getConferenceDetail())
            .toString();
    }
}

package com.paradisecloud.smc.dao.model;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * @author nj
 * @date 2023/3/14 14:33
 */
@Schema(description = "【请填写功能名称】")
public class BusiSmcHistoryConference extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /** 会议id */
    @Schema(description = "会议id")
    @Excel(name = "会议id")
    private String conferenceId;

    /** 部门ID */
    @Schema(description = "部门ID")
    @Excel(name = "部门ID")
    private Long deptId;


    private String subject;

    private String conferenceCode;

    private int endStatus;

    private Date endTime;

    private Long templateId;

    private String  conferenceAvcType;

    private Date startTime;

    private Integer duration;

    private Integer participantNum;

    public Integer getParticipantNum() {
        return participantNum;
    }

    public void setParticipantNum(Integer participantNum) {
        this.participantNum = participantNum;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getConferenceAvcType() {
        return conferenceAvcType;
    }

    public void setConferenceAvcType(String conferenceAvcType) {
        this.conferenceAvcType = conferenceAvcType;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getEndStatus() {
        return endStatus;
    }

    public void setEndStatus(int endStatus) {
        this.endStatus = endStatus;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getConferenceCode() {
        return conferenceCode;
    }

    public void setConferenceCode(String conferenceCode) {
        this.conferenceCode = conferenceCode;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }
    public void setConferenceId(String conferenceId)
    {
        this.conferenceId = conferenceId;
    }

    public String getConferenceId()
    {
        return conferenceId;
    }
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    @Override
    public String toString() {
        return "BusiSmcHistoryConference{" +
                "id=" + id +
                ", conferenceId='" + conferenceId + '\'' +
                ", deptId=" + deptId +
                ", subject='" + subject + '\'' +
                ", conferenceCode='" + conferenceCode + '\'' +
                ", endStatus=" + endStatus +
                ", endTime=" + endTime +
                ", templateId=" + templateId +
                ", conferenceAvcType='" + conferenceAvcType + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}

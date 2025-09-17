package com.paradisecloud.fcm.dao.model;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * tencent会议历史对象 busi_tencent_history_conference
 *
 * @author lilinhai
 * @date 2023-07-07
 */
@Schema(description = "tencent会议历史")
public class BusiTencentHistoryConference extends BaseEntity
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
    private Integer deptId;

    /** 主题 */
    @Schema(description = "主题")
    @Excel(name = "主题")
    private String subject;

    /** 会议数字ID */
    @Schema(description = "会议数字ID")
    @Excel(name = "会议数字ID")
    private String conferenceCode;

    /** 是否结束1结束2否 */
    @Schema(description = "是否结束1结束2否")
    @Excel(name = "是否结束1结束2否")
    private Long endStatus;

    /** 会议结束时间 */
    @Schema(description = "会议结束时间")
    @Excel(name = "会议结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 模板ID */
    @Schema(description = "模板ID")
    @Excel(name = "模板ID")
    private Integer templateId;

    /** 音视频会议类型 */
    @Schema(description = "音视频会议类型")
    @Excel(name = "音视频会议类型")
    private String conferenceAvcType;

    /** 会议开始时间 */
    @Schema(description = "会议开始时间")
    @Excel(name = "会议开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 会议时长 */
    @Schema(description = "会议时长")
    @Excel(name = "会议时长")
    private Integer duration;

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
    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }
    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getSubject()
    {
        return subject;
    }
    public void setConferenceCode(String conferenceCode)
    {
        this.conferenceCode = conferenceCode;
    }

    public String getConferenceCode()
    {
        return conferenceCode;
    }
    public void setEndStatus(Long endStatus)
    {
        this.endStatus = endStatus;
    }

    public Long getEndStatus()
    {
        return endStatus;
    }
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }
    public void setTemplateId(Integer templateId)
    {
        this.templateId = templateId;
    }

    public Integer getTemplateId()
    {
        return templateId;
    }
    public void setConferenceAvcType(String conferenceAvcType)
    {
        this.conferenceAvcType = conferenceAvcType;
    }

    public String getConferenceAvcType()
    {
        return conferenceAvcType;
    }
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getStartTime()
    {
        return startTime;
    }
    public void setDuration(Integer duration)
    {
        this.duration = duration;
    }

    public Integer getDuration()
    {
        return duration;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("conferenceId", getConferenceId())
                .append("deptId", getDeptId())
                .append("createTime", getCreateTime())
                .append("subject", getSubject())
                .append("conferenceCode", getConferenceCode())
                .append("endStatus", getEndStatus())
                .append("endTime", getEndTime())
                .append("templateId", getTemplateId())
                .append("conferenceAvcType", getConferenceAvcType())
                .append("startTime", getStartTime())
                .append("duration", getDuration())
                .toString();
    }
}

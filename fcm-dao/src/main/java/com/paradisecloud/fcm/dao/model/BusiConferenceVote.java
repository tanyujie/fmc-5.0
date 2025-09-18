package com.paradisecloud.fcm.dao.model;


import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 会议投票主对象 busi_conference_vote
 *
 * @author lilinhai
 * @date 2025-09-18
 */
@Schema(description = "会议投票主")
public class BusiConferenceVote extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 投票ID（主键） */
    @Schema(description = "投票ID（主键）")
    private Long voteId;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String mcuType;

    /** 会议id */
    @Schema(description = "会议id")
    @Excel(name = "会议id")
    private Long templateConferenceId;

    /** 投票主题 */
    @Schema(description = "投票主题")
    @Excel(name = "投票主题")
    private String title;

    private long voteCreateTime;

    /** 补充描述 */
    @Schema(description = "补充描述")
    @Excel(name = "补充描述")
    private String description;

    /** 投票状态（0：未开始，1：进行中，2：已结束） */
    @Schema(description = "投票状态（0：未开始，1：进行中，2：已结束）")
    @Excel(name = "投票状态", readConverterExp = "0=：未开始，1：进行中，2：已结束")
    private int status;

    /** 是否匿名（0：否，1：是） */
    @Schema(description = "是否匿名（0：否，1：是）")
    @Excel(name = "是否匿名", readConverterExp = "0=：否，1：是")
    private int isAnonymous;

    /** 创建人ID（外键，关联用户表） */
    @Schema(description = "创建人ID（外键，关联用户表）")
    @Excel(name = "创建人ID", readConverterExp = "外=键，关联用户表")
    private Long creatorId;

    public void setVoteId(Long voteId)
    {
        this.voteId = voteId;
    }

    public Long getVoteId()
    {
        return voteId;
    }
    public void setMcuType(String mcuType)
    {
        this.mcuType = mcuType;
    }

    public String getMcuType()
    {
        return mcuType;
    }
    public void setTemplateConferenceId(Long templateConferenceId)
    {
        this.templateConferenceId = templateConferenceId;
    }

    public Long getTemplateConferenceId()
    {
        return templateConferenceId;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getStatus()
    {
        return status;
    }
    public void setIsAnonymous(int isAnonymous)
    {
        this.isAnonymous = isAnonymous;
    }

    public int getIsAnonymous()
    {
        return isAnonymous;
    }
    public void setCreatorId(Long creatorId)
    {
        this.creatorId = creatorId;
    }

    public Long getCreatorId()
    {
        return creatorId;
    }

    public long getVoteCreateTime() {
        return voteCreateTime;
    }

    public void setVoteCreateTime(long voteCreateTime) {
        this.voteCreateTime = voteCreateTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("voteId", getVoteId())
                .append("mcuType", getMcuType())
                .append("templateConferenceId", getTemplateConferenceId())
                .append("title", getTitle())
                .append("description", getDescription())
                .append("createTime", getCreateTime())
                .append("status", getStatus())
                .append("isAnonymous", getIsAnonymous())
                .append("creatorId", getCreatorId())
                .toString();
    }
}

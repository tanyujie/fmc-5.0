package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 会议投票问题对象 busi_conference_vote_question
 *
 * @author lilinhai
 * @date 2025-09-18
 */
@Schema(description = "会议投票问题")
public class BusiConferenceVoteQuestion extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 问题ID（主键） */
    @Schema(description = "问题ID（主键）")
    private Long questionId;

    /** 关联的投票ID（外键） */
    @Schema(description = "关联的投票ID（外键）")
    @Excel(name = "关联的投票ID", readConverterExp = "外=键")
    private Long voteId;

    /** 问题内容 */
    @Schema(description = "问题内容")
    @Excel(name = "问题内容")
    private String content;

    /** 问题类型（0：单选，1：多选） */
    @Schema(description = "问题类型（0：单选，1：多选）")
    @Excel(name = "问题类型", readConverterExp = "0=：单选，1：多选")
    private Integer type;

    /** 问题排序 */
    @Schema(description = "问题排序")
    @Excel(name = "问题排序")
    private Long sortOrder;

    public void setQuestionId(Long questionId)
    {
        this.questionId = questionId;
    }

    public Long getQuestionId()
    {
        return questionId;
    }
    public void setVoteId(Long voteId)
    {
        this.voteId = voteId;
    }

    public Long getVoteId()
    {
        return voteId;
    }
    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }
    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getType()
    {
        return type;
    }
    public void setSortOrder(Long sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Long getSortOrder()
    {
        return sortOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("questionId", getQuestionId())
                .append("voteId", getVoteId())
                .append("content", getContent())
                .append("type", getType())
                .append("sortOrder", getSortOrder())
                .toString();
    }
}

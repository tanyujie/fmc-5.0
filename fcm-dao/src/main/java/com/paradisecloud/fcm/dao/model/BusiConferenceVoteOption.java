package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 会议投票选项对象 busi_conference_vote_option
 *
 * @author lilinhai
 * @date 2025-09-18
 */
@Schema(description = "会议投票选项")
public class BusiConferenceVoteOption extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 选项ID（主键） */
    @Schema(description = "选项ID（主键）")
    private Long optionId;

    /** 关联的问题ID（外键） */
    @Schema(description = "关联的问题ID（外键）")
    @Excel(name = "关联的问题ID", readConverterExp = "外=键")
    private Long questionId;

    /** 选项内容 */
    @Schema(description = "选项内容")
    @Excel(name = "选项内容")
    private String content;

    /** 该选项的投票数 */
    @Schema(description = "该选项的投票数")
    @Excel(name = "该选项的投票数")
    private Integer voteCount;

    @Schema(description = "选项排序")
    @Excel(name = "选项排序")
    private Integer optionOrder;

    public void setOptionId(Long optionId)
    {
        this.optionId = optionId;
    }

    public Integer getOptionOrder() {
        return optionOrder;
    }

    public void setOptionOrder(Integer optionOrder) {
        this.optionOrder = optionOrder;
    }

    public Long getOptionId()
    {
        return optionId;
    }
    public void setQuestionId(Long questionId)
    {
        this.questionId = questionId;
    }

    public Long getQuestionId()
    {
        return questionId;
    }
    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }
    public void setVoteCount(Integer voteCount)
    {
        this.voteCount = voteCount;
    }

    public Integer getVoteCount()
    {
        return voteCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("optionId", getOptionId())
                .append("questionId", getQuestionId())
                .append("content", getContent())
                .append("voteCount", getVoteCount())
                .toString();
    }
}

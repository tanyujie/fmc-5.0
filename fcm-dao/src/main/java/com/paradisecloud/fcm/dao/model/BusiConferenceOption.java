package com.paradisecloud.fcm.dao.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Schema(description = "会议问卷选项")
public class BusiConferenceOption extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 选项ID（主键） */
    @Schema(description = "选项ID（主键）")
    private Long optionId;

    /** 关联的问题ID（外键） */
    @Schema(description = "关联的问题ID（外键）")
    private Long questionId;

    /** 选项内容 */
    @Schema(description = "选项内容")
    private String content;

    /** 该选项的投票数量 */
    @Schema(description = "该选项的投票数量")
    private Integer voteCount;

    /** 选项排序号（用于控制显示顺序） */
    @Schema(description = "选项排序号（用于控制显示顺序）")
    private Integer sortNum;

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("optionId", getOptionId())
                .append("questionId", getQuestionId())
                .append("content", getContent())
                .append("voteCount", getVoteCount())
                .append("sortNum", getSortNum())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}

package com.paradisecloud.fcm.dao.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Schema(description = "会议问卷问题")
public class BusiConferenceQuestion extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 问题ID（主键） */
    @Schema(description = "问题ID（主键）")
    private Long questionId;

    /** 关联的问卷ID（外键） */
    @Schema(description = "关联的问卷ID（外键）")
    private Long questionnaireId;

    /** 问题内容 */
    @Schema(description = "问题内容")
    private String content;

    /** 问题类型（0：单选，1：多选，2：文本等） */
    @Schema(description = "问题类型（0：单选，1：多选，2：文本等）")
    private Integer type;

    /** 问题排序号（用于控制显示顺序） */
    @Schema(description = "问题排序号（用于控制显示顺序）")
    private Integer sortNum;

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
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
                .append("questionId", getQuestionId())
                .append("questionnaireId", getQuestionnaireId())
                .append("content", getContent())
                .append("type", getType())
                .append("sortNum", getSortNum())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}

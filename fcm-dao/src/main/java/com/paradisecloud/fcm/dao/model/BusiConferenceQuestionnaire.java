package com.paradisecloud.fcm.dao.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Schema(description = "会议问卷主表")
public class BusiConferenceQuestionnaire extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 问卷ID（主键） */
    @Schema(description = "问卷ID（主键）")
    private Long questionnaireId;

    /** mcu type */
    @Schema(description = "mcu type")
    private String mcuType;

    /** 关联的会议ID（外键，关联会议表） */
    @Schema(description = "关联的会议ID（外键，关联会议表）")
    private Long templateConferenceId;

    /** 问卷标题 */
    @Schema(description = "问卷标题")
    private String title;

    /** 有效时间（单位：分钟） */
    @Schema(description = "有效时间（单位：分钟）")
    private Integer effectiveTime;

    /** 问卷状态（0：未开始，1：进行中，2：已结束） */
    @Schema(description = "问卷状态（0：未开始，1：进行中，2：已结束）")
    private Integer status;

    /** 是否匿名（0：否，1：是） */
    @Schema(description = "是否匿名（0：否，1：是）")
    private Integer isAnonymous;

    /** 创建人ID（外键，关联用户表） */
    @Schema(description = "创建人ID（外键，关联用户表）")
    private Long creatorId;

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setMcuType(String mcuType) {
        this.mcuType = mcuType;
    }

    public String getMcuType() {
        return mcuType;
    }

    public void setTemplateConferenceId(Long templateConferenceId) {
        this.templateConferenceId = templateConferenceId;
    }

    public Long getTemplateConferenceId() {
        return templateConferenceId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setEffectiveTime(Integer effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Integer getEffectiveTime() {
        return effectiveTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setIsAnonymous(Integer isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public Integer getIsAnonymous() {
        return isAnonymous;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("questionnaireId", getQuestionnaireId())
                .append("mcuType", getMcuType())
                .append("templateConferenceId", getTemplateConferenceId())
                .append("title", getTitle())
                .append("effectiveTime", getEffectiveTime())
                .append("status", getStatus())
                .append("isAnonymous", getIsAnonymous())
                .append("creatorId", getCreatorId())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}

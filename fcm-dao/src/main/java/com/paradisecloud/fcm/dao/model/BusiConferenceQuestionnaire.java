package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 会议问卷主对象 busi_conference_questionnaire
 *
 * @author lilinhai
 * @date 2025-09-24
 */
@Schema(description = "会议问卷主")
public class BusiConferenceQuestionnaire extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 问卷ID（主键） */
    @Schema(description = "问卷ID（主键）")
    private Long questionnaireId;

    /** mcu type */
    @Schema(description = "mcu type")
    @Excel(name = "mcu type")
    private String mcuType;

    /** 关联的会议ID（外键，关联会议表） */
    @Schema(description = "关联的会议ID（外键，关联会议表）")
    @Excel(name = "关联的会议ID", readConverterExp = "外=键，关联会议表")
    private Long conferenceId;

    /** 问卷标题 */
    @Schema(description = "问卷标题")
    @Excel(name = "问卷标题")
    private String title;

    /** 有效时间（单位：分钟） */
    @Schema(description = "有效时间（单位：分钟）")
    @Excel(name = "有效时间", readConverterExp = "单=位：分钟")
    private Long effectiveTime;

    /** 创建时间 */
    @Schema(description = "创建时间")
    @Excel(name = "创建时间")
    private Long createQuestionnaireTime;

    /** 问卷状态（0：未开始，1：进行中，2：已结束） */
    @Schema(description = "问卷状态（0：未开始，1：进行中，2：已结束）")
    @Excel(name = "问卷状态", readConverterExp = "0=：未开始，1：进行中，2：已结束")
    private Integer status;

    /** 是否匿名（0：否，1：是） */
    @Schema(description = "是否匿名（0：否，1：是）")
    @Excel(name = "是否匿名", readConverterExp = "0=：否，1：是")
    private Integer isAnonymous;

    /** 创建人ID（外键，关联用户表） */
    @Schema(description = "创建人ID（外键，关联用户表）")
    @Excel(name = "创建人ID", readConverterExp = "外=键，关联用户表")
    private Long creatorId;

    @Schema(description = "创建人")
    private String creatorNickname;
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setQuestionnaireId(Long questionnaireId)
    {
        this.questionnaireId = questionnaireId;
    }

    public Long getQuestionnaireId()
    {
        return questionnaireId;
    }
    public void setMcuType(String mcuType)
    {
        this.mcuType = mcuType;
    }

    public String getMcuType()
    {
        return mcuType;
    }
    public void setConferenceId(Long conferenceId)
    {
        this.conferenceId = conferenceId;
    }

    public Long getConferenceId()
    {
        return conferenceId;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }
    public void setEffectiveTime(Long effectiveTime)
    {
        this.effectiveTime = effectiveTime;
    }

    public Long getEffectiveTime()
    {
        return effectiveTime;
    }
    public void setCreateQuestionnaireTime(Long createQuestionnaireTime)
    {
        this.createQuestionnaireTime = createQuestionnaireTime;
    }

    public Integer getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Integer isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public Long getCreateQuestionnaireTime()
    {
        return createQuestionnaireTime;
    }

    public void setCreatorId(Long creatorId)
    {
        this.creatorId = creatorId;
    }

    public Long getCreatorId()
    {
        return creatorId;
    }

    public String getCreatorNickname() {
        return creatorNickname;
    }

    public void setCreatorNickname(String creatorNickname) {
        this.creatorNickname = creatorNickname;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("questionnaireId", getQuestionnaireId())
                .append("mcuType", getMcuType())
                .append("conferenceId", getConferenceId())
                .append("title", getTitle())
                .append("effectiveTime", getEffectiveTime())
                .append("createQuestionnaireTime", getCreateQuestionnaireTime())
                .append("status", getStatus())
                .append("isAnonymous", getIsAnonymous())
                .append("creatorId", getCreatorId())
                .toString();
    }
}

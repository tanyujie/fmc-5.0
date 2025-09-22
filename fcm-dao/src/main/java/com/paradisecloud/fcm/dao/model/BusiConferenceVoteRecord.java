package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 会议投票记录对象 busi_conference_vote_record
 *
 * @author lilinhai
 * @date 2025-09-19
 */
@Schema(description = "会议投票记录")
public class BusiConferenceVoteRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID（主键） */
    @Schema(description = "记录ID（主键）")
    private Long recordId;

    /** 关联的投票ID（外键） */
    @Schema(description = "关联的投票ID（外键）")
    @Excel(name = "关联的投票ID", readConverterExp = "外=键")
    private Long voteId;

    /** 关联的问题ID（外键） */
    @Schema(description = "关联的问题ID（外键）")
    @Excel(name = "关联的问题ID", readConverterExp = "外=键")
    private Long questionId;

    /** 投票用户ID（匿名时为NULL） */
    @Schema(description = "投票用户ID（匿名时为NULL）")
    @Excel(name = "投票用户ID", readConverterExp = "匿=名时为NULL")
    private String userId;

    /** 投票用户昵称 */
    @Schema(description = "投票用户昵称")
    @Excel(name = "投票用户昵称")
    private String userName;

    /** 选择的选项ID列表（多选时用逗号分隔） */
    @Schema(description = "选择的选项ID列表（多选时用逗号分隔）")
    @Excel(name = "选择的选项ID列表", readConverterExp = "多=选时用逗号分隔")
    private String optionIds;

    /** 投票时间 */
    @Schema(description = "投票时间")
    @Excel(name = "投票时间", width = 30, dateFormat = "yyyy-MM-dd")
    private long voteTime;

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public Long getRecordId()
    {
        return recordId;
    }
    public void setVoteId(Long voteId)
    {
        this.voteId = voteId;
    }

    public Long getVoteId()
    {
        return voteId;
    }
    public void setQuestionId(Long questionId)
    {
        this.questionId = questionId;
    }

    public Long getQuestionId()
    {
        return questionId;
    }
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserId()
    {
        return userId;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }
    public void setOptionIds(String optionIds)
    {
        this.optionIds = optionIds;
    }

    public String getOptionIds()
    {
        return optionIds;
    }
    public void setVoteTime(long voteTime)
    {
        this.voteTime = voteTime;
    }

    public long getVoteTime()
    {
        return voteTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("recordId", getRecordId())
                .append("voteId", getVoteId())
                .append("questionId", getQuestionId())
                .append("userId", getUserId())
                .append("userName", getUserName())
                .append("optionIds", getOptionIds())
                .append("voteTime", getVoteTime())
                .toString();
    }
}

package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Schema(description = "成员签到关联")
public class BusiConferenceUserSignIn extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 签到 ID */
    @Schema(description = "签到 ID")
    @Excel(name = "签到 ID")
    private Long signInId;

    /** 会议ID */
    @Schema(description = "会议ID")
    @Excel(name = "会议ID")
    private Long conferenceId;

    /** 成员 ID */
    @Schema(description = "成员 ID")
    @Excel(name = "成员 ID")
    private Long userId;

    /** 用户昵称 */
    @Schema(description = "用户昵称")
    @Excel(name = "用户昵称")
    private String userNickname;

    /** 成员签到时间 */
    @Schema(description = "成员签到时间")
    @Excel(name = "成员签到时间")
    private Long signInTime;

    /** 成员签到状态1：未签到，2：已签到 */
    @Schema(description = "成员签到状态1：未签到，2：已签到")
    @Excel(name = "成员签到状态1：未签到，2：已签到")
    private Integer signStatus;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setSignInId(Long signInId)
    {
        this.signInId = signInId;
    }

    public Long getSignInId()
    {
        return signInId;
    }
    public void setConferenceId(Long conferenceId)
    {
        this.conferenceId = conferenceId;
    }

    public Long getConferenceId()
    {
        return conferenceId;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setUserNickname(String userNickname)
    {
        this.userNickname = userNickname;
    }

    public String getUserNickname()
    {
        return userNickname;
    }
    public void setSignInTime(Long signInTime)
    {
        this.signInTime = signInTime;
    }

    public Long getSignInTime()
    {
        return signInTime;
    }
    public void setSignStatus(Integer signStatus)
    {
        this.signStatus = signStatus;
    }

    public Integer getSignStatus()
    {
        return signStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("signInId", getSignInId())
                .append("conferenceId", getConferenceId())
                .append("userId", getUserId())
                .append("userNickname", getUserNickname())
                .append("signInTime", getSignInTime())
                .append("signStatus", getSignStatus())
                .toString();
    }
}
package com.paradisecloud.fcm.dao.model;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 智慧办公房间参与人对象 busi_smart_room_participant
 *
 * @author lilinhai
 * @date 2024-04-07
 */
@Schema(description = "智慧办公房间参与人")
public class BusiSmartRoomParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 预约ID */
    @Schema(description = "预约ID")
    @Excel(name = "预约ID")
    private Long bookId;

    /** 用户ID */
    @Schema(description = "用户ID")
    @Excel(name = "用户ID")
    private Long userId;

    /** 用户名称 */
    @Schema(description = "用户名称")
    @Excel(name = "用户名称")
    private String userName;

    /** 签到时间 */
    @Schema(description = "签到时间")
    @Excel(name = "签到时间", width = 30)
    private Date signInTime;

    /** 签到码 */
    @Schema(description = "签到码")
    @Excel(name = "签到码")
    private String signInCode;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setBookId(Long bookId)
    {
        this.bookId = bookId;
    }

    public Long getBookId()
    {
        return bookId;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
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

    public void setSignInTime(Date signInTime)
    {
        this.signInTime = signInTime;
    }

    public Date getSignInTime()
    {
        return signInTime;
    }
    public void setSignInCode(String signInCode)
    {
        this.signInCode = signInCode;
    }

    public String getSignInCode()
    {
        return signInCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("bookId", getBookId())
                .append("userId", getUserId())
                .append("userName", getUserName())
                .append("signInTime", getSignInTime())
                .append("signInCode", getSignInCode())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
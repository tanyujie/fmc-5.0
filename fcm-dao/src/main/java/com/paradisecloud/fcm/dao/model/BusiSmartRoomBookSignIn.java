package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 房间预约签到对象 busi_smart_room_book_sign_in
 *
 * @author lilinhai
 * @date 2024-03-22
 */
@Schema(description = "房间预约签到")
public class BusiSmartRoomBookSignIn extends BaseEntity
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

    /** 用户名 */
    @Schema(description = "用户名")
    @Excel(name = "用户名")
    private String userName;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("bookId", getBookId())
                .append("userId", getUserId())
                .append("userName", getUserName())
                .append("createTime", getCreateTime())
                .toString();
    }
}

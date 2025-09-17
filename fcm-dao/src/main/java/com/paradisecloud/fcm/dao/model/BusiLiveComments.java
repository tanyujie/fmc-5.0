package com.paradisecloud.fcm.dao.model;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 直播回放评论对象 busi_live_comments
 * 
 * @author lilinhai
 * @date 2024-05-21
 */
@Schema(description = "直播回放评论")
public class BusiLiveComments extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 直播ID */
    @Schema(description = "直播ID")
    @Excel(name = "直播ID")
    private Long liveId;

    /** 评论 */
    @Schema(description = "评论")
    @Excel(name = "评论")
    private String commentContent;

    /** 评论用户ID */
    @Schema(description = "评论用户ID")
    @Excel(name = "评论用户ID")
    private Long commentUserId;

    /** 评论用户名 */
    @Schema(description = "评论用户名")
    @Excel(name = "评论用户名")
    private String commentUserName;

    /** 父评论ID */
    @Schema(description = "父评论ID")
    @Excel(name = "父评论ID")
    private Long parentCommentId;

    /** 回复评论ID */
    @Schema(description = "回复评论ID")
    @Excel(name = "回复评论ID")
    private Long replyCommentId;

    /** 删除时间 */
    @Schema(description = "删除时间")
    @Excel(name = "删除时间", width = 30)
    private Date deleteTime;

    /** 删除人 */
    @Schema(description = "删除人")
    @Excel(name = "删除人")
    private String deleteBy;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setLiveId(Long liveId) 
    {
        this.liveId = liveId;
    }

    public Long getLiveId() 
    {
        return liveId;
    }
    public void setCommentContent(String commentContent) 
    {
        this.commentContent = commentContent;
    }

    public String getCommentContent() 
    {
        return commentContent;
    }
    public void setCommentUserId(Long commentUserId) 
    {
        this.commentUserId = commentUserId;
    }

    public Long getCommentUserId() 
    {
        return commentUserId;
    }
    public void setCommentUserName(String commentUserName) 
    {
        this.commentUserName = commentUserName;
    }

    public String getCommentUserName() 
    {
        return commentUserName;
    }
    public void setParentCommentId(Long parentCommentId) 
    {
        this.parentCommentId = parentCommentId;
    }

    public Long getParentCommentId() 
    {
        return parentCommentId;
    }
    public void setReplyCommentId(Long replyCommentId) 
    {
        this.replyCommentId = replyCommentId;
    }

    public Long getReplyCommentId() 
    {
        return replyCommentId;
    }
    public void setDeleteTime(Date deleteTime) 
    {
        this.deleteTime = deleteTime;
    }

    public Date getDeleteTime() 
    {
        return deleteTime;
    }
    public void setDeleteBy(String deleteBy) 
    {
        this.deleteBy = deleteBy;
    }

    public String getDeleteBy() 
    {
        return deleteBy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("liveId", getLiveId())
            .append("commentContent", getCommentContent())
            .append("commentUserId", getCommentUserId())
            .append("commentUserName", getCommentUserName())
            .append("parentCommentId", getParentCommentId())
            .append("replyCommentId", getReplyCommentId())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("deleteTime", getDeleteTime())
            .append("deleteBy", getDeleteBy())
            .toString();
    }
}

package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 会议动作捕捉对象 busi_conference_motion_capture
 * 
 * @author lilinhai
 * @date 2025-06-16
 */
@Schema(description = "会议动作捕捉")
public class BusiConferenceMotionCapture extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 历史会议ID */
    @Schema(description = "历史会议ID")
    @Excel(name = "历史会议ID")
    private Long historyConferenceId;

    /** coSpaceId */
    @Schema(description = "coSpaceId")
    @Excel(name = "coSpaceId")
    private String coSpace;

    /** 动作 */
    @Schema(description = "动作")
    @Excel(name = "动作")
    private String motion;

    /** 图片名 */
    @Schema(description = "图片名")
    @Excel(name = "图片名")
    private String imageName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setHistoryConferenceId(Long historyConferenceId) 
    {
        this.historyConferenceId = historyConferenceId;
    }

    public Long getHistoryConferenceId() 
    {
        return historyConferenceId;
    }
    public void setCoSpace(String coSpace) 
    {
        this.coSpace = coSpace;
    }

    public String getCoSpace() 
    {
        return coSpace;
    }
    public void setMotion(String motion) 
    {
        this.motion = motion;
    }

    public String getMotion() 
    {
        return motion;
    }
    public void setImageName(String imageName) 
    {
        this.imageName = imageName;
    }

    public String getImageName() 
    {
        return imageName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("historyConferenceId", getHistoryConferenceId())
            .append("coSpace", getCoSpace())
            .append("motion", getMotion())
            .append("imageName", getImageName())
            .append("createTime", getCreateTime())
            .toString();
    }
}

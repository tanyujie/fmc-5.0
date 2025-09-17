package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 直播记录对象 busi_live_broadcast
 *
 * @author lilinhai
 * @date 2024-05-07
 */
@Schema(description = "直播记录")
public class BusiLiveBroadcast extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 归属租户 */
    @Schema(description = "归属租户")
    @Excel(name = "归属租户")
    private Long deptId;

    /** 直播开始时间 */
    @Schema(description = "直播开始时间")
    @Excel(name = "直播开始时间")
    private String startTime;

    /** 直播结束时间 */
    @Schema(description = "直播结束时间")
    @Excel(name = "直播结束时间")
    private String endTime;

    /** 延长分钟数 */
    @Schema(description = "延长分钟数")
    @Excel(name = "延长分钟数")
    private Integer extendMinutes;

    /** 直播是否开始 */
    @Schema(description = "直播是否开始")
    @Excel(name = "直播是否开始")
    private Integer isStart;

    /** 直播的状态：1启动，2停止 */
    @Schema(description = "直播的状态：1启动，2停止")
    @Excel(name = "直播的状态：1启动，2停止, 3已结束")
    private Integer status;

    /** 直播类型:1:会议直播;2:普通直播 */
    @Schema(description = "直播类型:1:会议直播;2:普通直播")
    @Excel(name = "直播类型:1:会议直播;2:普通直播")
    private Integer type;

    /** 直播推流地址 */
    @Schema(description = "直播推流地址")
    @Excel(name = "直播推流地址")
    private String streamUrl;

    /** 直播名 */
    @Schema(description = "直播名")
    @Excel(name = "直播名")
    private String name;

    /** 直播介绍 */
    @Schema(description = "直播介绍")
    @Excel(name = "直播介绍")
    private String introduce;

    /** 是否启用回放(1是，2否) */
    @Schema(description = "是否启用回放(1是，2否)")
    @Excel(name = "是否启用回放(1是，2否)")
    private Integer playbackEnabled;

    /** 是否启用评论(1是，2否) */
    @Schema(description = "是否启用评论(1是，2否)")
    @Excel(name = "是否启用评论(1是，2否)")
    private Integer commentsEnabled;

    /** 是否启用送礼(1是，2否) */
    @Schema(description = "是否启用送礼(1是，2否)")
    @Excel(name = "是否启用送礼(1是，2否)")
    private Integer giftsEnabled;

    /** 直播时长 */
    @Schema(description = "直播时长")
    @Excel(name = "直播时长")
    private Integer duration;

    /** 终端总数 */
    @Schema(description = "终端总数")
    @Excel(name = "终端总数")
    private Long deviceNum;

    /** 终端id */
    @Schema(description = "终端id")
    @Excel(name = "终端id")
    private Integer terminalId;

    /** 直播结束原因：1:管理员挂断; 2:到时自动结束; 3:异常结束  */
    @Schema(description = "直播结束原因：1:管理员挂断; 2:到时自动结束; 3:异常结束 ")
    @Excel(name = "直播结束原因：1:管理员挂断; 2:到时自动结束; 3:异常结束 ")
    private Integer endReasonsType;

    /** 历史会议id */
    @Schema(description = "历史会议id")
    @Excel(name = "历史会议id")
    private Integer historyConferenceId;

    /** 文件直播的文件id */
    @Schema(description = "文件直播的文件id")
    @Excel(name = "文件直播的文件id")
    private Integer meetingFileId;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }
    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getStartTime()
    {
        return startTime;
    }
    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public String getEndTime()
    {
        return endTime;
    }
    public void setExtendMinutes(Integer extendMinutes)
    {
        this.extendMinutes = extendMinutes;
    }

    public Integer getExtendMinutes()
    {
        return extendMinutes;
    }
    public void setIsStart(Integer isStart)
    {
        this.isStart = isStart;
    }

    public Integer getIsStart()
    {
        return isStart;
    }
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }
    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getType()
    {
        return type;
    }
    public void setStreamUrl(String streamUrl)
    {
        this.streamUrl = streamUrl;
    }

    public String getStreamUrl()
    {
        return streamUrl;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setIntroduce(String introduce)
    {
        this.introduce = introduce;
    }

    public String getIntroduce()
    {
        return introduce;
    }
    public void setPlaybackEnabled(Integer playbackEnabled)
    {
        this.playbackEnabled = playbackEnabled;
    }

    public Integer getPlaybackEnabled()
    {
        return playbackEnabled;
    }
    public void setCommentsEnabled(Integer commentsEnabled)
    {
        this.commentsEnabled = commentsEnabled;
    }

    public Integer getCommentsEnabled()
    {
        return commentsEnabled;
    }
    public void setGiftsEnabled(Integer giftsEnabled)
    {
        this.giftsEnabled = giftsEnabled;
    }

    public Integer getGiftsEnabled()
    {
        return giftsEnabled;
    }
    public void setDuration(Integer duration)
    {
        this.duration = duration;
    }

    public Integer getDuration()
    {
        return duration;
    }
    public void setDeviceNum(Long deviceNum)
    {
        this.deviceNum = deviceNum;
    }

    public Long getDeviceNum()
    {
        return deviceNum;
    }
    public void setTerminalId(Integer terminalId)
    {
        this.terminalId = terminalId;
    }

    public Integer getTerminalId()
    {
        return terminalId;
    }
    public void setEndReasonsType(Integer endReasonsType)
    {
        this.endReasonsType = endReasonsType;
    }

    public Integer getEndReasonsType()
    {
        return endReasonsType;
    }
    public void setHistoryConferenceId(Integer historyConferenceId)
    {
        this.historyConferenceId = historyConferenceId;
    }

    public Integer getHistoryConferenceId()
    {
        return historyConferenceId;
    }
    public void setMeetingFileId(Integer meetingFileId)
    {
        this.meetingFileId = meetingFileId;
    }

    public Integer getMeetingFileId()
    {
        return meetingFileId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("createBy", getCreateBy())
                .append("updateBy", getUpdateBy())
                .append("deptId", getDeptId())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("extendMinutes", getExtendMinutes())
                .append("isStart", getIsStart())
                .append("status", getStatus())
                .append("type", getType())
                .append("streamUrl", getStreamUrl())
                .append("name", getName())
                .append("introduce", getIntroduce())
                .append("playbackEnabled", getPlaybackEnabled())
                .append("commentsEnabled", getCommentsEnabled())
                .append("giftsEnabled", getGiftsEnabled())
                .append("duration", getDuration())
                .append("deviceNum", getDeviceNum())
                .append("terminalId", getTerminalId())
                .append("endReasonsType", getEndReasonsType())
                .append("historyConferenceId", getHistoryConferenceId())
                .append("meetingFileId", getMeetingFileId())
                .toString();
    }
}
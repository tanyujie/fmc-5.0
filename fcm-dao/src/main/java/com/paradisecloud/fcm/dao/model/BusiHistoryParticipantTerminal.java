package com.paradisecloud.fcm.dao.model;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 历史会议的参会者终端对象 busi_history_participant_terminal
 * 
 * @author lilinhai
 * @date 2022-06-08
 */
@Schema(description = "历史会议的参会者终端")
public class BusiHistoryParticipantTerminal extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 关联的会议ID */
    @Schema(description = "关联的会议ID")
    @Excel(name = "关联的会议ID")
    private Long historyConferenceId;

    /** 关联的终端ID */
    @Schema(description = "关联的终端ID")
    @Excel(name = "关联的终端ID")
    private Long terminalId;

    /** 远程参与方地址 */
    @Schema(description = "远程参与方地址")
    @Excel(name = "远程参与方地址")
    private String remoteParty;

    /** 部门Id */
    @Schema(description = "部门Id")
    @Excel(name = "部门Id")
    private Long deptId;

    /** 终端名称 */
    @Schema(description = "终端名称")
    @Excel(name = "终端名称")
    private String name;

    /** 入会时间 */
    @Schema(description = "入会时间")
    @Excel(name = "入会时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date joinTime;

    /** 离会时间 */
    @Schema(description = "离会时间")
    @Excel(name = "离会时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date outgoingTime;

    /** 处于活动的时长(s) */
    @Schema(description = "处于活动的时长(s)")
    @Excel(name = "处于活动的时长(s)")
    private Integer durationSeconds;

    /** 是否入会 */
    @Schema(description = "是否入会")
    @Excel(name = "是否入会")
    private Boolean joined;

    /** 入会次数 */
    @Schema(description = "入会次数")
    @Excel(name = "入会次数")
    private Integer joinedTimes;

    /** 流媒体信息 */
    @Schema(description = "流媒体信息")
    @Excel(name = "流媒体信息")
    private Map<String, Object>  mediaInfo;

    private CdrCallLegStart cdrCallLegStart;

    private CdrCallLegEnd cdrCallLegEnd;

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
    public void setTerminalId(Long terminalId) 
    {
        this.terminalId = terminalId;
    }

    public Long getTerminalId() 
    {
        return terminalId;
    }
    public void setRemoteParty(String remoteParty) 
    {
        this.remoteParty = remoteParty;
    }

    public String getRemoteParty() 
    {
        return remoteParty;
    }
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setJoinTime(Date joinTime) 
    {
        this.joinTime = joinTime;
    }

    public Date getJoinTime() 
    {
        return joinTime;
    }
    public void setOutgoingTime(Date outgoingTime) 
    {
        this.outgoingTime = outgoingTime;
    }

    public Date getOutgoingTime() 
    {
        return outgoingTime;
    }
    public void setDurationSeconds(Integer durationSeconds) 
    {
        this.durationSeconds = durationSeconds;
    }

    public Integer getDurationSeconds() 
    {
        return durationSeconds;
    }
    public void setJoined(Boolean joined)
    {
        this.joined = joined;
    }

    public Boolean getJoined()
    {
        return joined;
    }
    public void setJoinedTimes(Integer joinedTimes) 
    {
        this.joinedTimes = joinedTimes;
    }

    public Integer getJoinedTimes() 
    {
        return joinedTimes;
    }
    public void setMediaInfo(Map<String, Object> mediaInfo)
    {
        this.mediaInfo = mediaInfo;
    }

    public Map<String, Object>  getMediaInfo()
    {
        return mediaInfo;
    }

    public CdrCallLegStart getCdrCallLegStart() {
        return cdrCallLegStart;
    }

    public void setCdrCallLegStart(CdrCallLegStart cdrCallLegStart) {
        this.cdrCallLegStart = cdrCallLegStart;
    }

    public CdrCallLegEnd getCdrCallLegEnd() {
        return cdrCallLegEnd;
    }

    public void setCdrCallLegEnd(CdrCallLegEnd cdrCallLegEnd) {
        this.cdrCallLegEnd = cdrCallLegEnd;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("historyConferenceId", getHistoryConferenceId())
            .append("terminalId", getTerminalId())
            .append("remoteParty", getRemoteParty())
            .append("deptId", getDeptId())
            .append("name", getName())
            .append("joinTime", getJoinTime())
            .append("outgoingTime", getOutgoingTime())
            .append("durationSeconds", getDurationSeconds())
            .append("joined", getJoined())
            .append("joinedTimes", getJoinedTimes())
            .append("mediaInfo", getMediaInfo())
            .toString();
    }
}

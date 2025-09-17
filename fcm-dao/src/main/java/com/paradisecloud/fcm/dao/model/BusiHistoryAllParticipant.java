package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.Map;

/**
 * 历史全会议的参会者对象 busi_history_all_participant
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@Schema(description = "历史全会议的参会者")
public class BusiHistoryAllParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 关联的会议ID */
    @Schema(description = "关联的会议ID")
    private Long historyConferenceId;

    /** 参会者顺序（权重倒叙排列） */
    @Schema(description = "参会者顺序（权重倒叙排列）")
    private Integer weight;

    /** 会场ID：callLegId */
    @Schema(description = "会场ID：callLegId")
    private String callLegId;

    /** 会议ID：callId */
    @Schema(description = "会议ID：callId")
    private String callId;

    /** callLeg的远程参与方地址 */
    @Schema(description = "callLeg的远程参与方地址")
    @Excel(name = "远程参与方地址")
    private String remoteParty;

    /** 终端名称 */
    @Schema(description = "终端名称")
    @Excel(name = "终端名称")
    private String name;

    /** 入会时间 */
    @Schema(description = "入会时间")
    @Excel(name = "入会时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date joinTime;

    /** 离会时间 */
    @Schema(description = "离会时间")
    @Excel(name = "离会时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date outgoingTime;

    /**
     * call leg处于活动的时长(s)
     */
    @Schema(description = "call leg处于活动的时长(s)")
    @Excel(name = "入会时长(s)")
    private Integer durationSeconds;

    private CdrCallLegStart cdrCallLegStart;

    private CdrCallLegEnd cdrCallLegEnd;

    @Schema(description = "coSpaceId")
    private String coSpace;

    @Schema(description = "joined")
    private Boolean joined;
    
    /** 媒体属性 */
    @Schema(description = "媒体属性")
    @Excel(name = "媒体属性")
    private Map<String, Object> mediaInfo;

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

    public void setWeight(Integer weight) 
    {
        this.weight = weight;
    }

    public Integer getWeight() 
    {
        return weight;
    }

    public String getCallLegId() {
        return callLegId;
    }

    public void setCallLegId(String callLegId) {
        this.callLegId = callLegId;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getRemoteParty() {
        return remoteParty;
    }

    public void setRemoteParty(String remoteParty) {
        this.remoteParty = remoteParty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public Date getOutgoingTime() {
        return outgoingTime;
    }

    public void setOutgoingTime(Date outgoingTime) {
        this.outgoingTime = outgoingTime;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
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

    public String getCoSpace() {
        return coSpace;
    }

    public void setCoSpace(String coSpace) {
        this.coSpace = coSpace;
    }

    public Boolean getJoined() {
        return joined;
    }

    public void setJoined(Boolean joined) {
        this.joined = joined;
    }

    /**
     * <p>Get Method   :   mediaInfo Map<String,Object></p>
     * @return mediaInfo
     */
    public Map<String, Object> getMediaInfo()
    {
        return mediaInfo;
    }

    /**
     * <p>Set Method   :   mediaInfo Map<String,Object></p>
     * @param mediaInfo
     */
    public void setMediaInfo(Map<String, Object> mediaInfo)
    {
        this.mediaInfo = mediaInfo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("historyConferenceId", getHistoryConferenceId())
                .append("weight", getWeight())
                .append("callId", getCallId())
                .append("callLegId", getCallLegId())
                .append("remoteParty", getRemoteParty())
                .append("name", getName())
                .append("joinTime", getJoinTime())
                .append("outgoingTime", getOutgoingTime())
                .toString();
    }
}

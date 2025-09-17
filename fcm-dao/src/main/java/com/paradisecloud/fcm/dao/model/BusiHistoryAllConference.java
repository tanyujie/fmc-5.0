package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;

/**
 * 历史全会议，每次挂断会保存该历史记录对象 busi_history_all_conference
 * 
 * @author lilinhai
 * @date 2021-01-30
 */
@Schema(description = "历史全会议，每次挂断会保存该历史记录")
public class BusiHistoryAllConference extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 模板会议名 */
    @Schema(description = "模板会议名")
    @Excel(name = "模板会议名")
    private String name;

    /** 创建者ID */
    @Schema(description = "创建者ID")
    private Long createUserId;

    /** 会议号码 */
    @Schema(description = "会议号码")
    @Excel(name = "会议号码")
    private String number;

    /** 创建者用户名 */
    @Schema(description = "创建者用户名")
    private String createUserName;

    /** 入会方案配置ID（关联FME里面的入会方案记录ID，会控端不存） */
    @Schema(description = "入会方案配置ID（关联FME里面的入会方案记录ID，会控端不存）")
    private String callLegProfileId;

    /** 带宽1,2,3,4,5,6M */
    @Schema(description = "带宽1,2,3,4,5,6M")
    @Excel(name = "带宽(M)")
    private Integer bandwidth;

    /** coSpaceId */
    @Schema(description = "coSpaceId")
    private String coSpace;

    /** callId */
    @Schema(description = "callId")
    private String callId;

    /** 会议开始时间 */
    @Schema(description = "会议开始时间")
    @Excel(name = "会议开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date conferenceStartTime;

    /** 会议结束时间 */
    @Schema(description = "会议结束时间")
    @Excel(name = "会议结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date conferenceEndTime;

    /** 会议时长 */
    @Schema(description = "会议时长")
    @Excel(name = "会议时长")
    private Integer duration;

    /** 终端总数 */
    @Schema(description = "终端总数")
    private Integer deviceNum;

    /** 会议类型:1、预定会议;0:普通会议 */
    @Schema(description = "会议类型:1、预定会议;0:普通会议")
    private Integer type;
    /** 历史参会者 */
    @Schema(description = "历史参会者")
    private List<BusiHistoryParticipant> participantList;


    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setCreateUserId(Long createUserId) 
    {
        this.createUserId = createUserId;
    }

    public Long getCreateUserId() 
    {
        return createUserId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setCreateUserName(String createUserName)
    {
        this.createUserName = createUserName;
    }

    public String getCreateUserName() 
    {
        return createUserName;
    }
    public void setCallLegProfileId(String callLegProfileId) 
    {
        this.callLegProfileId = callLegProfileId;
    }

    public String getCallLegProfileId() 
    {
        return callLegProfileId;
    }
    public void setBandwidth(Integer bandwidth) 
    {
        this.bandwidth = bandwidth;
    }

    public Integer getBandwidth() 
    {
        return bandwidth;
    }

    public String getCoSpace() {
        return coSpace;
    }

    public void setCoSpace(String coSpace) {
        this.coSpace = coSpace;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public Date getConferenceStartTime() {
        return conferenceStartTime;
    }

    public void setConferenceStartTime(Date conferenceStartTime) {
        this.conferenceStartTime = conferenceStartTime;
    }

    public Date getConferenceEndTime() {
        return conferenceEndTime;
    }

    public void setConferenceEndTime(Date conferenceEndTime) {
        this.conferenceEndTime = conferenceEndTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(Integer deviceNum) {
        this.deviceNum = deviceNum;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<BusiHistoryParticipant> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(List<BusiHistoryParticipant> participantList) {
        this.participantList = participantList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("name", getName())
                .append("createUserId", getCreateUserId())
                .append("number", getNumber())
                .append("createUserName", getCreateUserName())
                .append("callLegProfileId", getCallLegProfileId())
                .append("bandwidth", getBandwidth())
                .append("callId", getCallId())
                .append("coSpace", getCoSpace())
                .append("conferenceStartTime", getConferenceStartTime())
                .append("conferenceEndTime", getConferenceEndTime())
                .append("duration", getDuration())
                .append("deviceNum", getDeviceNum())
                .append("type", getType())
                .toString();
    }
}
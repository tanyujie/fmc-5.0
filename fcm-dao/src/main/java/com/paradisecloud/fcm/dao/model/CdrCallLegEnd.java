package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;

import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * callLegEnd记录对象 cdr_call_leg_end
 *
 * @author lilinhai
 * @date 2021-05-13
 */
@Schema(description = "callLegEnd记录")
public class CdrCallLegEnd extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /**
     * callLegID
     */
    @Schema(description = "callLegID")
    private String cdrId;

    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    @Excel(name = "会话ID")
    private String session;

    /**
     * 会议桥ID
     */
    @Schema(description = "会议桥ID")
    @Excel(name = "会议桥ID")
    private String callBridge;

    /**
     * 记录索引
     */
    @Schema(description = "记录索引")
    @Excel(name = "记录索引")
    private Integer recordIndex;

    /**
     * 记录索引
     */
    @Schema(description = "记录索引")
    @Excel(name = "记录索引")
    private Integer correlatorIndex;

    /**
     * cdr标识
     */
    @Schema(description = "cdr标识")
    @Excel(name = "cdr标识")
    private String cdrTag;

    /**
     * 结束原因
     */
    @Schema(description = "结束原因")
    @Excel(name = "结束原因")
    private CallLegEndReasonEnum reason;

    /**
     * true:由远程参会者终止；false : 由会议服务器发起终止
     */
    @Schema(description = "true:由远程参会者终止；false : 由会议服务器发起终止")
    @Excel(name = "true:由远程参会者终止；false : 由会议服务器发起终止")
    private Boolean remoteTeardown;

    /**
     * 是否存在加密的Media
     */
    @Schema(description = "是否存在加密的Media")
    @Excel(name = "是否存在加密的Media")
    private Boolean encryptedMedia;

    /**
     * 是否存在未加密的Media
     */
    @Schema(description = "是否存在未加密的Media")
    @Excel(name = "是否存在未加密的Media")
    private Boolean unencryptedMedia;

    /**
     * call leg处于活动的时长(s)
     */
    @Schema(description = "call leg处于活动的时长(s)")
    @Excel(name = "call leg处于活动的时长(s)")
    private Integer durationSeconds;

    /**
     * call leg已活动的时长(s)
     */
    @Schema(description = "call leg已活动的时长(s)")
    @Excel(name = "call leg已活动的时长(s)")
    private Integer activatedDuration;

    /**
     * 不同类型媒体活动的百分比信息:主视频查看
     */
    @Schema(description = "不同类型媒体活动的百分比信息:主视频查看")
    @Excel(name = "不同类型媒体活动的百分比信息:主视频查看")
    private BigDecimal mainVideoViewer;

    /**
     * 不同类型媒体活动的百分比信息:主要视频参与者
     */
    @Schema(description = "不同类型媒体活动的百分比信息:主要视频参与者")
    @Excel(name = "不同类型媒体活动的百分比信息:主要视频参与者")
    private BigDecimal mainVideoContributor;

    /**
     * 不同类型媒体活动的百分比信息:演示文稿查看器
     */
    @Schema(description = "不同类型媒体活动的百分比信息:演示文稿查看器")
    @Excel(name = "不同类型媒体活动的百分比信息:演示文稿查看器")
    private BigDecimal presentationViewer;

    /**
     * 不同类型媒体活动的百分比信息:演示文稿的贡献者
     */
    @Schema(description = "不同类型媒体活动的百分比信息:演示文稿的贡献者")
    @Excel(name = "不同类型媒体活动的百分比信息:演示文稿的贡献者")
    private BigDecimal presentationContributor;

    /**
     * 不同类型媒体活动的百分比信息:多流媒体视频
     */
    @Schema(description = "不同类型媒体活动的百分比信息:多流媒体视频")
    @Excel(name = "不同类型媒体活动的百分比信息:多流媒体视频")
    private BigDecimal multistreamVideo;

    /**
     * 最大屏幕数量
     */
    @Schema(description = "最大屏幕数量")
    @Excel(name = "最大屏幕数量")
    private Integer maxScreens;

    /**
     * 远程系统分配的所有者ID
     */
    @Schema(description = "远程系统分配的所有者ID")
    @Excel(name = "远程系统分配的所有者ID")
    private String ownerId;

    /**
     * sip连接的callID
     */
    @Schema(description = "sip连接的callID")
    @Excel(name = "sip连接的callID")
    private String sipCallId;

    /**
     * 记录时间
     */
    @Schema(description = "记录时间")
    @Excel(name = "记录时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date time;

    /** 创建者ID */
    @Schema(description = "创建者ID")
    @Excel(name = "创建者ID")
    private Long createUserId;

    /** 创建者用户名 */
    @Schema(description = "创建者用户名")
    @Excel(name = "创建者用户名")
    private String createUserName;

    private List<CdrCallLegEndMediaInfo> callLegEndMediaInfoList;

    private List<CdrCallLegEndAlarm> cdrCallLegEndAlarmList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCdrId() {
        return cdrId;
    }

    public void setCdrId(String cdrId) {
        this.cdrId = cdrId;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSession() {
        return session;
    }

    public void setCallBridge(String callBridge) {
        this.callBridge = callBridge;
    }

    public String getCallBridge() {
        return callBridge;
    }

    public void setRecordIndex(Integer recordIndex) {
        this.recordIndex = recordIndex;
    }

    public Integer getRecordIndex() {
        return recordIndex;
    }

    public void setCorrelatorIndex(Integer correlatorIndex) {
        this.correlatorIndex = correlatorIndex;
    }

    public Integer getCorrelatorIndex() {
        return correlatorIndex;
    }

    public void setCdrTag(String cdrTag) {
        this.cdrTag = cdrTag;
    }

    public String getCdrTag() {
        return cdrTag;
    }

    public CallLegEndReasonEnum getReason() {
        return reason;
    }

    public void setReason(CallLegEndReasonEnum reason) {
        this.reason = reason;
    }

    public Boolean getRemoteTeardown() {
        return remoteTeardown;
    }

    public void setRemoteTeardown(Boolean remoteTeardown) {
        this.remoteTeardown = remoteTeardown;
    }

    public Boolean getEncryptedMedia() {
        return encryptedMedia;
    }

    public void setEncryptedMedia(Boolean encryptedMedia) {
        this.encryptedMedia = encryptedMedia;
    }

    public Boolean getUnencryptedMedia() {
        return unencryptedMedia;
    }

    public void setUnencryptedMedia(Boolean unencryptedMedia) {
        this.unencryptedMedia = unencryptedMedia;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setActivatedDuration(Integer activatedDuration) {
        this.activatedDuration = activatedDuration;
    }

    public Integer getActivatedDuration() {
        return activatedDuration;
    }

    public BigDecimal getMainVideoViewer() {
        return mainVideoViewer;
    }

    public void setMainVideoViewer(BigDecimal mainVideoViewer) {
        this.mainVideoViewer = mainVideoViewer;
    }

    public BigDecimal getMainVideoContributor() {
        return mainVideoContributor;
    }

    public void setMainVideoContributor(BigDecimal mainVideoContributor) {
        this.mainVideoContributor = mainVideoContributor;
    }

    public BigDecimal getPresentationViewer() {
        return presentationViewer;
    }

    public void setPresentationViewer(BigDecimal presentationViewer) {
        this.presentationViewer = presentationViewer;
    }

    public BigDecimal getPresentationContributor() {
        return presentationContributor;
    }

    public void setPresentationContributor(BigDecimal presentationContributor) {
        this.presentationContributor = presentationContributor;
    }

    public BigDecimal getMultistreamVideo() {
        return multistreamVideo;
    }

    public void setMultistreamVideo(BigDecimal multistreamVideo) {
        this.multistreamVideo = multistreamVideo;
    }

    public void setMaxScreens(Integer maxScreens) {
        this.maxScreens = maxScreens;
    }

    public Integer getMaxScreens() {
        return maxScreens;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setSipCallId(String sipCallId) {
        this.sipCallId = sipCallId;
    }

    public String getSipCallId() {
        return sipCallId;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public List<CdrCallLegEndMediaInfo> getCallLegEndMediaInfoList() {
        return callLegEndMediaInfoList;
    }

    public void setCallLegEndMediaInfoList(List<CdrCallLegEndMediaInfo> callLegEndMediaInfoList) {
        this.callLegEndMediaInfoList = callLegEndMediaInfoList;
    }

    public List<CdrCallLegEndAlarm> getCdrCallLegEndAlarmList() {
        return cdrCallLegEndAlarmList;
    }

    public void setCdrCallLegEndAlarmList(List<CdrCallLegEndAlarm> cdrCallLegEndAlarmList) {
        this.cdrCallLegEndAlarmList = cdrCallLegEndAlarmList;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    @Override
    public String toString() {
        return "CdrCallLegEnd{" +
                "cdrId=" + cdrId +
                ", id='" + id + '\'' +
                ", session='" + session + '\'' +
                ", callBridge='" + callBridge + '\'' +
                ", recordIndex=" + recordIndex +
                ", correlatorIndex=" + correlatorIndex +
                ", cdrTag='" + cdrTag + '\'' +
                ", reason='" + reason + '\'' +
                ", remoteTeardown=" + remoteTeardown +
                ", encryptedMedia=" + encryptedMedia +
                ", unencryptedMedia=" + unencryptedMedia +
                ", durationSeconds=" + durationSeconds +
                ", activatedDuration=" + activatedDuration +
                ", mainVideoViewer=" + mainVideoViewer +
                ", mainVideoContributor=" + mainVideoContributor +
                ", presentationViewer=" + presentationViewer +
                ", presentationContributor=" + presentationContributor +
                ", multistreamVideo=" + multistreamVideo +
                ", maxScreens=" + maxScreens +
                ", ownerId='" + ownerId + '\'' +
                ", sipCallId='" + sipCallId + '\'' +
                ", time=" + time +
                '}';
    }
}

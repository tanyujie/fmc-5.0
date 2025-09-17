package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * callLegStart 记录对象 cdr_call_leg_start
 *
 * @author lilinhai
 * @date 2021-05-13
 */
@Schema(description = "callLegStart 记录")
public class CdrCallLegStart extends BaseEntity {
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
     * 显示名称
     */
    @Schema(description = "显示名称")
    @Excel(name = "显示名称")
    private String displayName;

    /**
     * 传入呼叫的被叫者ID/传出呼叫的呼叫者ID
     */
    @Schema(description = "传入呼叫的被叫者ID/传出呼叫的呼叫者ID")
    @Excel(name = "传入呼叫的被叫者ID/传出呼叫的呼叫者ID")
    private String localAddress;

    /**
     * 呼叫方或被叫者的远程地址，具体看连接是呼入还是呼出
     */
    @Schema(description = "呼叫方或被叫者的远程地址，具体看连接是呼入还是呼出")
    @Excel(name = "呼叫方或被叫者的远程地址，具体看连接是呼入还是呼出")
    private String remoteAddress;

    /**
     * callLeg的远程参与方地址
     */
    @Schema(description = "callLeg的远程参与方地址")
    @Excel(name = "callLeg的远程参与方地址")
    private String remoteParty;

    /**
     * cdr标识
     */
    @Schema(description = "cdr标识")
    @Excel(name = "cdr标识")
    private String cdrTag;

    /**
     * 启用访客连接时为true
     */
    @Schema(description = "启用访客连接时为true")
    @Excel(name = "启用访客连接时为true")
    private Boolean guestConnection;

    /**
     * 是否启用录制
     */
    @Schema(description = "是否启用录制")
    @Excel(name = "是否启用录制")
    private Boolean recording;

    /**
     * 是否创建流媒体连接
     */
    @Schema(description = "是否创建流媒体连接")
    @Excel(name = "是否创建流媒体连接")
    private Boolean streaming;

    /**
     * call leg的类型: sip|acano
     */
    @Schema(description = "call leg的类型: sip|acano")
    @Excel(name = "call leg的类型: sip|acano")
    private String type;

    /**
     * call leg的子类型 :lync | avaya | distributionLink | lyncdistribution | webApp
     */
    @Schema(description = "call leg的子类型 :lync | avaya | distributionLink | lyncdistribution | webApp")
    @Excel(name = "call leg的子类型 :lync | avaya | distributionLink | lyncdistribution | webApp")
    private String subType;

    /**
     * lync子类型的子类型:audioVideo| applicationSharing| instantMessaging
     */
    @Schema(description = "lync子类型的子类型:audioVideo| applicationSharing| instantMessaging")
    @Excel(name = "lync子类型的子类型:audioVideo| applicationSharing| instantMessaging")
    private String lyncSubType;

    /**
     * 呼入incoming/呼出类型outgoing
     */
    @Schema(description = "呼入incoming/呼出类型outgoing")
    @Excel(name = "呼入incoming/呼出类型outgoing")
    private String direction;

    /**
     * call ID
     */
    @Schema(description = "call ID")
    @Excel(name = "call ID")
    private String call;

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
     * Lync模式下的分组ID
     */
    @Schema(description = "Lync模式下的分组ID")
    @Excel(name = "Lync模式下的分组ID")
    private String groupId;

    /**
     * SIP连接替换时的 CallID
     */
    @Schema(description = "SIP连接替换时的 CallID")
    @Excel(name = "SIP连接替换时的 CallID")
    private String replacesSipCallId;

    /**
     * 是否可使用movedParticipant API移除参会者
     */
    @Schema(description = "是否可使用movedParticipant API移除参会者")
    @Excel(name = "是否可使用movedParticipant API移除参会者")
    private Integer canMove;

    /**
     * 移动CallLeg时的 CallLeg GUID
     */
    @Schema(description = "移动CallLeg时的 CallLeg GUID")
    @Excel(name = "移动CallLeg时的 CallLeg GUID")
    private String movedCallLeg;

    /**
     * 移动CallLeg时的CallBridge ID
     */
    @Schema(description = "移动CallLeg时的CallBridge ID")
    @Excel(name = "移动CallLeg时的CallBridge ID")
    private String movedCallLegCallBridge;

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

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteParty(String remoteParty) {
        this.remoteParty = remoteParty;
    }

    public String getRemoteParty() {
        return remoteParty;
    }

    public void setCdrTag(String cdrTag) {
        this.cdrTag = cdrTag;
    }

    public String getCdrTag() {
        return cdrTag;
    }

    public Boolean getGuestConnection() {
        return guestConnection;
    }

    public void setGuestConnection(Boolean guestConnection) {
        this.guestConnection = guestConnection;
    }

    public Boolean getRecording() {
        return recording;
    }

    public void setRecording(Boolean recording) {
        this.recording = recording;
    }

    public Boolean getStreaming() {
        return streaming;
    }

    public void setStreaming(Boolean streaming) {
        this.streaming = streaming;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getSubType() {
        return subType;
    }

    public void setLyncSubType(String lyncSubType) {
        this.lyncSubType = lyncSubType;
    }

    public String getLyncSubType() {
        return lyncSubType;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
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

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setReplacesSipCallId(String replacesSipCallId) {
        this.replacesSipCallId = replacesSipCallId;
    }

    public String getReplacesSipCallId() {
        return replacesSipCallId;
    }

    public void setCanMove(Integer canMove) {
        this.canMove = canMove;
    }

    public Integer getCanMove() {
        return canMove;
    }

    public void setMovedCallLeg(String movedCallLeg) {
        this.movedCallLeg = movedCallLeg;
    }

    public String getMovedCallLeg() {
        return movedCallLeg;
    }

    public void setMovedCallLegCallBridge(String movedCallLegCallBridge) {
        this.movedCallLegCallBridge = movedCallLegCallBridge;
    }

    public String getMovedCallLegCallBridge() {
        return movedCallLegCallBridge;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
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
        return "CdrCallLegStart{" +
                "cdrId=" + cdrId +
                ", id='" + id + '\'' +
                ", session='" + session + '\'' +
                ", callBridge='" + callBridge + '\'' +
                ", recordIndex=" + recordIndex +
                ", correlatorIndex=" + correlatorIndex +
                ", displayName='" + displayName + '\'' +
                ", localAddress='" + localAddress + '\'' +
                ", remoteAddress='" + remoteAddress + '\'' +
                ", remoteParty='" + remoteParty + '\'' +
                ", cdrTag='" + cdrTag + '\'' +
                ", guestConnection=" + guestConnection +
                ", recording=" + recording +
                ", streaming=" + streaming +
                ", type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                ", lyncSubType='" + lyncSubType + '\'' +
                ", direction='" + direction + '\'' +
                ", call='" + call + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", sipCallId='" + sipCallId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", replacesSipCallId='" + replacesSipCallId + '\'' +
                ", canMove=" + canMove +
                ", movedCallLeg='" + movedCallLeg + '\'' +
                ", movedCallLegCallBridge='" + movedCallLegCallBridge + '\'' +
                ", time=" + time +
                '}';
    }
}

package com.paradisecloud.fcm.dao.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * callLegUpdate记录对象 cdr_call_leg_update
 *
 * @author lilinhai
 * @date 2021-05-13
 */
@Schema(description = "callLegUpdate记录")
public class CdrCallLegUpdate extends BaseEntity {
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
     * callLeg状态:connected;如何为空则未连接
     */
    @Schema(description = "callLeg状态:connected;如何为空则未连接")
    @Excel(name = "callLeg状态:connected;如何为空则未连接")
    private String state;

    /**
     * callLeg当前是否已停用
     */
    @Schema(description = "callLeg当前是否已停用")
    @Excel(name = "callLeg当前是否已停用")
    private Integer deactivated;

    /**
     * 呼叫方或被叫者的远程地址，具体看连接是呼入还是呼出
     */
    @Schema(description = "呼叫方或被叫者的远程地址，具体看连接是呼入还是呼出")
    @Excel(name = "呼叫方或被叫者的远程地址，具体看连接是呼入还是呼出")
    private String remoteAddress;

    /**
     * 如何ivr状态时为空,否则为callLeg的callId
     */
    @Schema(description = "如何ivr状态时为空,否则为callLeg的callId")
    @Excel(name = "如何ivr状态时为空,否则为callLeg的callId")
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
     * 显示名称
     */
    @Schema(description = "显示名称")
    @Excel(name = "显示名称")
    private String displayName;

    /**
     * 是否可使用movedParticipant API移除参会者
     */
    @Schema(description = "是否可使用movedParticipant API移除参会者")
    @Excel(name = "是否可使用movedParticipant API移除参会者")
    private Integer canMove;

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

    public void setCdrTag(String cdrTag) {
        this.cdrTag = cdrTag;
    }

    public String getCdrTag() {
        return cdrTag;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setDeactivated(Integer deactivated) {
        this.deactivated = deactivated;
    }

    public Integer getDeactivated() {
        return deactivated;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getRemoteAddress() {
        return remoteAddress;
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

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setCanMove(Integer canMove) {
        this.canMove = canMove;
    }

    public Integer getCanMove() {
        return canMove;
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
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("cdrId", getCdrId())
                .append("id", getId())
                .append("session", getSession())
                .append("callBridge", getCallBridge())
                .append("recordIndex", getRecordIndex())
                .append("correlatorIndex", getCorrelatorIndex())
                .append("cdrTag", getCdrTag())
                .append("state", getState())
                .append("deactivated", getDeactivated())
                .append("remoteAddress", getRemoteAddress())
                .append("callIvr", getCall())
                .append("ownerId", getOwnerId())
                .append("sipCallId", getSipCallId())
                .append("groupId", getGroupId())
                .append("displayName", getDisplayName())
                .append("canMove", getCanMove())
                .append("time", getTime())
                .toString();
    }
}

package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * callstart 和callend记录对象 cdr_call
 *
 * @author lilinhai
 * @date 2021-05-13
 */
@Schema(description = "callstart 和callend记录")
public class CdrCall extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /**
     * callId
     */
    @Schema(description = "callId")
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
     * call名称
     */
    @Schema(description = "call名称")
    @Excel(name = "call名称")
    private String name;

    /**
     * coSpaceId
     */
    @Schema(description = "coSpaceId")
    @Excel(name = "coSpaceId")
    private String coSpace;

    /**
     * 所有者名称
     */
    @Schema(description = "所有者名称")
    @Excel(name = "所有者名称")
    private String ownerName;

    /**
     * 租户标识
     */
    @Schema(description = "租户标识")
    @Excel(name = "租户标识")
    private String tenant;

    /**
     * cdr标识
     */
    @Schema(description = "cdr标识")
    @Excel(name = "cdr标识")
    private String cdrTag;

    /**
     * call类型:coSpace| coSpace实例
     */
    @Schema(description = "call类型:coSpace| coSpace实例")
    @Excel(name = "call类型:coSpace| coSpace实例")
    private String callType;

    /**
     * 一个或多个call bridge上的call leg唯一标识
     */
    @Schema(description = "一个或多个call bridge上的call leg唯一标识")
    @Excel(name = "一个或多个call bridge上的call leg唯一标识")
    private String callCorrelator;

    /**
     * 当前call中已完成的 call leg数量
     */
    @Schema(description = "当前call中已完成的 call leg数量")
    @Excel(name = "当前call中已完成的 call leg数量")
    private Integer callLegsCompleted;

    /**
     * 当前call中同时活动的最大 call leg数量
     */
    @Schema(description = "当前call中同时活动的最大 call leg数量")
    @Excel(name = "当前call中同时活动的最大 call leg数量")
    private Integer callLegsMaxActive;

    /**
     * 当前call 处于活动状态的时间长度（以秒为单位）
     */
    @Schema(description = "当前call 处于活动状态的时间长度（以秒为单位）")
    @Excel(name = "当前call 处于活动状态的时间长度", readConverterExp = "以=秒为单位")
    private Integer durationSeconds;

    /**
     * 0:callEnd;1:callStart
     */
    @Schema(description = "0:callEnd;1:callStart")
    @Excel(name = "0:callEnd;1:callStart")
    private Integer recordType;

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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCoSpace(String coSpace) {
        this.coSpace = coSpace;
    }

    public String getCoSpace() {
        return coSpace;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getTenant() {
        return tenant;
    }

    public void setCdrTag(String cdrTag) {
        this.cdrTag = cdrTag;
    }

    public String getCdrTag() {
        return cdrTag;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallCorrelator(String callCorrelator) {
        this.callCorrelator = callCorrelator;
    }

    public String getCallCorrelator() {
        return callCorrelator;
    }

    public void setCallLegsCompleted(Integer callLegsCompleted) {
        this.callLegsCompleted = callLegsCompleted;
    }

    public Integer getCallLegsCompleted() {
        return callLegsCompleted;
    }

    public void setCallLegsMaxActive(Integer callLegsMaxActive) {
        this.callLegsMaxActive = callLegsMaxActive;
    }

    public Integer getCallLegsMaxActive() {
        return callLegsMaxActive;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setRecordType(Integer recordType) {
        this.recordType = recordType;
    }

    public Integer getRecordType() {
        return recordType;
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
                .append("name", getName())
                .append("coSpace", getCoSpace())
                .append("ownerName", getOwnerName())
                .append("tenant", getTenant())
                .append("cdrTag", getCdrTag())
                .append("callType", getCallType())
                .append("callCorrelator", getCallCorrelator())
                .append("callLegsCompleted", getCallLegsCompleted())
                .append("callLegsMaxActive", getCallLegsMaxActive())
                .append("durationSeconds", getDurationSeconds())
                .append("recordType", getRecordType())
                .append("time", getTime())
                .toString();
    }
}

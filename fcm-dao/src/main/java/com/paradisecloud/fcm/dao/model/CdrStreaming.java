package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * streamingStart和streamingEnd记录对象 cdr_streaming
 *
 * @author lilinhai
 * @date 2021-05-13
 */
@Schema(description = "streamingStart和streamingEnd记录")
public class CdrStreaming extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /**
     * streaming标签ID
     */
    @Schema(description = "streaming标签ID")
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
     * RTMP类型的URL地址
     */
    @Schema(description = "RTMP类型的URL地址")
    @Excel(name = "RTMP类型的URL地址")
    private String streamUrl;

    /**
     * 流媒体设备的Uri
     */
    @Schema(description = "流媒体设备的Uri")
    @Excel(name = "流媒体设备的Uri")
    private String streamerUri;

    /**
     * 正在记录的callId
     */
    @Schema(description = "正在记录的callId")
    @Excel(name = "正在记录的callId")
    private String call;

    /**
     * 正在记录的callLegId
     */
    @Schema(description = "正在记录的callLegId")
    @Excel(name = "正在记录的callLegId")
    private String callleg;

    /**
     * 记录时间
     */
    @Schema(description = "记录时间")
    @Excel(name = "记录时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date time;

    /**
     * 0:streamingEnd;1:streamingStart
     */
    @Schema(description = "0:streamingEnd;1:streamingStart")
    @Excel(name = "0:streamingEnd;1:streamingStart")
    private Integer recordType;

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

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamerUri(String streamerUri) {
        this.streamerUri = streamerUri;
    }

    public String getStreamerUri() {
        return streamerUri;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getCall() {
        return call;
    }

    public void setCallleg(String callleg) {
        this.callleg = callleg;
    }

    public String getCallleg() {
        return callleg;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public void setRecordType(Integer recordType) {
        this.recordType = recordType;
    }

    public Integer getRecordType() {
        return recordType;
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
                .append("streamUrl", getStreamUrl())
                .append("streamerUri", getStreamerUri())
                .append("call", getCall())
                .append("callleg", getCallleg())
                .append("time", getTime())
                .append("recordType", getRecordType())
                .toString();
    }
}

package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 视频流、音频流传输信息对象 cdr_call_leg_end_media_info
 *
 * @author lilinhai
 * @date 2021-05-14
 */
@Schema(description = "视频流、音频流传输信息")
public class CdrCallLegEndMediaInfo extends BaseEntity {
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
     * 传输视频的相关信息:codec类型
     */
    @Schema(description = "传输视频的相关信息:codec类型")
    @Excel(name = "传输视频的相关信息:codec类型")
    private String codec;

    /**
     * 传输视频最大视频分辨率宽度
     */
    @Schema(description = "传输视频最大视频分辨率宽度")
    @Excel(name = "传输视频最大视频分辨率宽度")
    private Integer maxSizeWidth;

    /**
     * 传输视频最大视频分辨率高度
     */
    @Schema(description = "传输视频最大视频分辨率高度")
    @Excel(name = "传输视频最大视频分辨率高度")
    private Integer maxSizeHeight;

    /**
     * txVideo/rxVideo/txAudio/rxAudio
     */
    @Schema(description = "txVideo/rxVideo/txAudio/rxAudio")
    @Excel(name = "txVideo/rxVideo/txAudio/rxAudio")
    private String type;

    /**
     * 丢包间隔
     */
    @Schema(description = "丢包间隔")
    @Excel(name = "丢包间隔")
    private BigDecimal packetLossBurstsDuration;

    /**
     * 丢包频率
     */
    @Schema(description = "丢包频率")
    @Excel(name = "丢包频率")
    private BigDecimal packetLossBurstsDensity;

    /**
     * 数据包间隙
     */
    @Schema(description = "数据包间隙")
    @Excel(name = "数据包间隙")
    private BigDecimal packetGapDuration;

    /**
     * 数据包频率
     */
    @Schema(description = "数据包频率")
    @Excel(name = "数据包频率")
    private BigDecimal packetGapDensity;

    /**
     * 历史与会者
     */
    @Schema(description = "历史与会者")
    @Excel(name = "历史与会者")
    private BusiHistoryParticipant busiHistoryParticipant;

    /**
     * 终端
     */
    @Schema(description = "终端")
    @Excel(name = "终端")
    private BusiTerminal busiTerminal;

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

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getCodec() {
        return codec;
    }

    public void setMaxSizeWidth(Integer maxSizeWidth) {
        this.maxSizeWidth = maxSizeWidth;
    }

    public Integer getMaxSizeWidth() {
        return maxSizeWidth;
    }

    public void setMaxSizeHeight(Integer maxSizeHeight) {
        this.maxSizeHeight = maxSizeHeight;
    }

    public Integer getMaxSizeHeight() {
        return maxSizeHeight;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getPacketLossBurstsDuration() {
        return packetLossBurstsDuration;
    }

    public void setPacketLossBurstsDuration(BigDecimal packetLossBurstsDuration) {
        this.packetLossBurstsDuration = packetLossBurstsDuration;
    }

    public BigDecimal getPacketLossBurstsDensity() {
        return packetLossBurstsDensity;
    }

    public void setPacketLossBurstsDensity(BigDecimal packetLossBurstsDensity) {
        this.packetLossBurstsDensity = packetLossBurstsDensity;
    }

    public BigDecimal getPacketGapDuration() {
        return packetGapDuration;
    }

    public void setPacketGapDuration(BigDecimal packetGapDuration) {
        this.packetGapDuration = packetGapDuration;
    }

    public BigDecimal getPacketGapDensity() {
        return packetGapDensity;
    }

    public void setPacketGapDensity(BigDecimal packetGapDensity) {
        this.packetGapDensity = packetGapDensity;
    }

    public BusiHistoryParticipant getBusiHistoryParticipant() {
        return busiHistoryParticipant;
    }

    public void setBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant) {
        this.busiHistoryParticipant = busiHistoryParticipant;
    }

    public BusiTerminal getBusiTerminal() {
        return busiTerminal;
    }

    public void setBusiTerminal(BusiTerminal busiTerminal) {
        this.busiTerminal = busiTerminal;
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
                .append("codec", getCodec())
                .append("maxSizeWidth", getMaxSizeWidth())
                .append("maxSizeHeight", getMaxSizeHeight())
                .append("type", getType())
                .append("packetLossBurstsDuration", getPacketLossBurstsDuration())
                .append("packetLossBurstsDensity", getPacketLossBurstsDensity())
                .append("packetGapDuration", getPacketGapDuration())
                .append("packetGapDensity", getPacketGapDensity())
                .toString();
    }
}

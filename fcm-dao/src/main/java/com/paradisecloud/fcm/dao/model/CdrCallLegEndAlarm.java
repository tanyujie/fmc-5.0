package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;

import com.paradisecloud.fcm.dao.enums.CallLegEndAlarmTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * callLegEnd报警信息对象 cdr_call_leg_end_alarm
 *
 * @author lilinhai
 * @date 2021-05-13
 */
@Schema(description = "callLegEnd报警信息")
public class CdrCallLegEndAlarm extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /**
     * callLegEnd主键Id
     */
    @Schema(description = "callLegEndId")
    @Excel(name = "callLegEndId")
    private String cdrLegEndId;

    /**
     * 报警类型:packetLoss|excessiveJitter|highRoundTripTime
     */
    @Schema(description = "报警类型:packetLoss|excessiveJitter|highRoundTripTime")
    @Excel(name = "报警类型:packetLoss|excessiveJitter|highRoundTripTime")
    private CallLegEndAlarmTypeEnum type;

    /**
     * 发生该报警条件的呼叫持续时间的百分比
     */
    @Schema(description = "发生该报警条件的呼叫持续时间的百分比")
    @Excel(name = "发生该报警条件的呼叫持续时间的百分比")
    private BigDecimal durationPercentage;

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

    public CdrCallLegEndAlarm() {
    }

    public CdrCallLegEndAlarm(String cdrLegEndId) {
        this.cdrLegEndId = cdrLegEndId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCdrLegEndId(String cdrLegEndId) {
        this.cdrLegEndId = cdrLegEndId;
    }

    public String getCdrLegEndId() {
        return cdrLegEndId;
    }

    public CallLegEndAlarmTypeEnum getType() {
        return type;
    }

    public void setType(CallLegEndAlarmTypeEnum type) {
        this.type = type;
    }

    public BigDecimal getDurationPercentage() {
        return durationPercentage;
    }

    public void setDurationPercentage(BigDecimal durationPercentage) {
        this.durationPercentage = durationPercentage;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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
                .append("cdrId", getId())
                .append("cdrLegEndId", getCdrLegEndId())
                .append("type", getType())
                .append("durationPercentage", getDurationPercentage())
                .toString();
    }
}

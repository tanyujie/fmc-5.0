package com.paradisecloud.fcm.dao.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.validation.constraints.*;

@Schema(description = "会议模板的签到主表")
public class BusiConferenceSignIn extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 签到 ID，主键，自增 */
    @Schema(description = "签到 ID，主键，自增")
    private long signInId;

    /** mcu type */
    @Schema(description = "mcu type")
    private String mcuType;

    /** 会议id */
    @Schema(description = "会议id")
    private long conferenceId;

    /** 发起者ID */
    @Schema(description = "发起者ID")
    private Integer initiatorId;

    /** 发起时间 */
    @Schema(description = "发起时间")
    @NotNull(message = "startTime 不能为空") // 数值非空
    @Future(message = "startTime 必须为未来的时间（不允许过去或当前时间）") // 调整为必须是未来时间
    private Long startTime;

    /** 签到时长（单位：秒） */
    @Schema(description = "签到时长（单位：秒）")
/*    @Min(value = 30, message = "签到时长不能小于 30 秒") // 最小值校验
    @Max(value = 180, message = "签到时长不能大于 180 秒") // 最大值校验*/
    private Integer duration;

    /** 签到说明 */
    @Schema(description = "签到说明")
    private String description;

    /** 签到状态 */
    @Schema(description = "签到状态1进行中2已结束")
    private Integer status;

    public void setSignInId(Integer signInId) {
        this.signInId = signInId;
    }

    public long getSignInId() {
        return signInId;
    }

    public void setSignInId(long signInId) {
        this.signInId = signInId;
    }

    public void setMcuType(String mcuType) {
        this.mcuType = mcuType;
    }

    public String getMcuType() {
        return mcuType;
    }

    public long getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(long conferenceId) {
        this.conferenceId = conferenceId;
    }

    public void setInitiatorId(Integer initiatorId) {
        this.initiatorId = initiatorId;
    }

    public Integer getInitiatorId() {
        return initiatorId;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("signInId", getSignInId())
                .append("mcuType", getMcuType())
                .append("conferenceId", getConferenceId())
                .append("initiatorId", getInitiatorId())
                .append("startTime", getStartTime())
                .append("duration", getDuration())
                .append("description", getDescription())
                .append("status", getStatus())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}

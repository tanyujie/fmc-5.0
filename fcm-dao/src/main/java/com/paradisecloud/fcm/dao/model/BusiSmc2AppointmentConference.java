package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 【请填写功能名称】对象 busi_smc2_appointment_conference
 * 
 * @author lilinhai
 * @date 2023-05-04
 */
@Schema(description = "【请填写功能名称】")
public class BusiSmc2AppointmentConference extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /** 部门id */
    @Schema(description = "部门id")
    @Excel(name = "部门id")
    private Long deptId;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    @Excel(name = "部门id")
    private String chairmanPassword;

    /** 会议类型 */
    @Schema(description = "会议类型")
    @Excel(name = "会议类型")
    private String conferenceTimeType;

    /** 时长 */
    @Schema(description = "时长")
    @Excel(name = "时长")
    private Integer duration;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    @Excel(name = "时长")
    private String guestPassword;

    /** utc时间 */
    @Schema(description = "utc时间")
    @Excel(name = "utc时间")
    private String scheduleStartTime;

    /** 主题 */
    @Schema(description = "主题")
    @Excel(name = "主题")
    private String subject;

    /** 虚拟号码 */
    @Schema(description = "虚拟号码")
    @Excel(name = "虚拟号码")
    private String vmrNumber;

    /** 视频会议，语音会议 */
    @Schema(description = "视频会议，语音会议")
    @Excel(name = "视频会议，语音会议")
    private String type;

    /** 最大入会数量 */
    @Schema(description = "最大入会数量")
    @Excel(name = "最大入会数量")
    private Integer maxParticipantNum;

    /** 静音入会 */
    @Schema(description = "静音入会")
    @Excel(name = "静音入会")
    private Integer voiceActive;

    /** 带宽 */
    @Schema(description = "带宽")
    @Excel(name = "带宽")
    private Integer rate;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    @Excel(name = "带宽")
    private String periodConferenceTime;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    @Excel(name = "带宽")
    private String durationPerPeriodUnit;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    @Excel(name = "带宽")
    private String periodUnitType;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    @Excel(name = "带宽")
    private String startDate;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    @Excel(name = "带宽")
    private String endDate;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    @Excel(name = "带宽")
    private Integer weekIndexInMonthMode;

    /** 会议ID */
    @Schema(description = "会议ID")
    @Excel(name = "会议ID")
    private String conferenceId;

    /** 视频分辨率（MPI_1080P） */
    @Schema(description = "视频分辨率（MPI_1080P）")
    @Excel(name = "视频分辨率", readConverterExp = "M=PI_1080P")
    private String videoResolution;

    /** svc视频分辨率 */
    @Schema(description = "svc视频分辨率")
    @Excel(name = "svc视频分辨率")
    private String svcVideoResolution;

    /** 自动静音 */
    @Schema(description = "自动静音")
    @Excel(name = "自动静音")
    private Integer autoMute;

    /** 直播 */
    @Schema(description = "直播")
    @Excel(name = "直播")
    private Integer supportLive;

    /** 录播 */
    @Schema(description = "录播")
    @Excel(name = "录播")
    private Integer supportRecord;

    /** 数据会议 */
    @Schema(description = "数据会议")
    @Excel(name = "数据会议")
    private Integer amcRecord;

    /** 会议数字ID */
    @Schema(description = "会议数字ID")
    @Excel(name = "会议数字ID")
    private String accessCode;

    /** 创建者 */
    @Schema(description = "创建者")
    @Excel(name = "创建者")
    private String createHyAdmin;

    /** 本地创建者 */
    @Schema(description = "本地创建者")
    @Excel(name = "本地创建者")
    private Integer createUserId;

    /** 会议账号 */
    @Schema(description = "会议账号")
    @Excel(name = "会议账号")
    private String accountName;

    /** 会议用户 */
    @Schema(description = "会议用户")
    @Excel(name = "会议用户")
    private String username;

    /** 会议token */
    @Schema(description = "会议token")
    @Excel(name = "会议token")
    private String token;

    /** 会议状态 */
    @Schema(description = "会议状态")
    @Excel(name = "会议状态")
    private String stage;

    /** 嘉宾链接 */
    @Schema(description = "嘉宾链接")
    @Excel(name = "嘉宾链接")
    private String guestLink;

    /** 主席链接 */
    @Schema(description = "主席链接")
    @Excel(name = "主席链接")
    private String chairmanLink;

    /** 分类 */
    @Schema(description = "分类")
    @Excel(name = "分类")
    private String category;

    /** 会议是否活动 */
    @Schema(description = "会议是否活动")
    @Excel(name = "会议是否活动")
    private Integer active;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    @Excel(name = "会议是否活动")
    private Integer legacyId;

    /** 组织名称 */
    @Schema(description = "组织名称")
    @Excel(name = "组织名称")
    private String organizationName;

    /** 主会场 */
    @Schema(description = "主会场")
    @Excel(name = "主会场")
    private Integer masterTerminalId;

    /** 数据会议 */
    @Schema(description = "数据会议")
    @Excel(name = "数据会议")
    private Integer enableDataConf;

    /** 会议密码 */
    @Schema(description = "会议密码")
    @Excel(name = "会议密码")
    private String password;

    private String createUser;

    private int smc2TemplateId;

    private Integer isHangUp;
    private Integer status;
    private Integer isStart;
    private String startFailedReason;


    public Integer getIsHangUp() {
        return isHangUp;
    }

    public void setIsHangUp(Integer isHangUp) {
        this.isHangUp = isHangUp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsStart() {
        return isStart;
    }

    public void setIsStart(Integer isStart) {
        this.isStart = isStart;
    }

    public String getStartFailedReason() {
        return startFailedReason;
    }

    public void setStartFailedReason(String startFailedReason) {
        this.startFailedReason = startFailedReason;
    }

    public int getSmc2TemplateId() {
        return smc2TemplateId;
    }

    public void setSmc2TemplateId(int smc2TemplateId) {
        this.smc2TemplateId = smc2TemplateId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }
    public void setChairmanPassword(String chairmanPassword) 
    {
        this.chairmanPassword = chairmanPassword;
    }

    public String getChairmanPassword() 
    {
        return chairmanPassword;
    }
    public void setConferenceTimeType(String conferenceTimeType) 
    {
        this.conferenceTimeType = conferenceTimeType;
    }

    public String getConferenceTimeType() 
    {
        return conferenceTimeType;
    }
    public void setDuration(Integer duration) 
    {
        this.duration = duration;
    }

    public Integer getDuration() 
    {
        return duration;
    }
    public void setGuestPassword(String guestPassword) 
    {
        this.guestPassword = guestPassword;
    }

    public String getGuestPassword() 
    {
        return guestPassword;
    }
    public void setScheduleStartTime(String scheduleStartTime) 
    {
        this.scheduleStartTime = scheduleStartTime;
    }

    public String getScheduleStartTime() 
    {
        return scheduleStartTime;
    }
    public void setSubject(String subject) 
    {
        this.subject = subject;
    }

    public String getSubject() 
    {
        return subject;
    }
    public void setVmrNumber(String vmrNumber) 
    {
        this.vmrNumber = vmrNumber;
    }

    public String getVmrNumber() 
    {
        return vmrNumber;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setMaxParticipantNum(Integer maxParticipantNum) 
    {
        this.maxParticipantNum = maxParticipantNum;
    }

    public Integer getMaxParticipantNum() 
    {
        return maxParticipantNum;
    }
    public void setVoiceActive(Integer voiceActive) 
    {
        this.voiceActive = voiceActive;
    }

    public Integer getVoiceActive() 
    {
        return voiceActive;
    }
    public void setRate(Integer rate) 
    {
        this.rate = rate;
    }

    public Integer getRate() 
    {
        return rate;
    }
    public void setPeriodConferenceTime(String periodConferenceTime) 
    {
        this.periodConferenceTime = periodConferenceTime;
    }

    public String getPeriodConferenceTime() 
    {
        return periodConferenceTime;
    }
    public void setDurationPerPeriodUnit(String durationPerPeriodUnit) 
    {
        this.durationPerPeriodUnit = durationPerPeriodUnit;
    }

    public String getDurationPerPeriodUnit() 
    {
        return durationPerPeriodUnit;
    }
    public void setPeriodUnitType(String periodUnitType) 
    {
        this.periodUnitType = periodUnitType;
    }

    public String getPeriodUnitType() 
    {
        return periodUnitType;
    }
    public void setStartDate(String startDate) 
    {
        this.startDate = startDate;
    }

    public String getStartDate() 
    {
        return startDate;
    }
    public void setEndDate(String endDate) 
    {
        this.endDate = endDate;
    }

    public String getEndDate() 
    {
        return endDate;
    }
    public void setWeekIndexInMonthMode(Integer weekIndexInMonthMode) 
    {
        this.weekIndexInMonthMode = weekIndexInMonthMode;
    }

    public Integer getWeekIndexInMonthMode() 
    {
        return weekIndexInMonthMode;
    }
    public void setConferenceId(String conferenceId) 
    {
        this.conferenceId = conferenceId;
    }

    public String getConferenceId() 
    {
        return conferenceId;
    }
    public void setVideoResolution(String videoResolution) 
    {
        this.videoResolution = videoResolution;
    }

    public String getVideoResolution() 
    {
        return videoResolution;
    }
    public void setSvcVideoResolution(String svcVideoResolution) 
    {
        this.svcVideoResolution = svcVideoResolution;
    }

    public String getSvcVideoResolution() 
    {
        return svcVideoResolution;
    }
    public void setAutoMute(Integer autoMute) 
    {
        this.autoMute = autoMute;
    }

    public Integer getAutoMute() 
    {
        return autoMute;
    }
    public void setSupportLive(Integer supportLive) 
    {
        this.supportLive = supportLive;
    }

    public Integer getSupportLive() 
    {
        return supportLive;
    }
    public void setSupportRecord(Integer supportRecord) 
    {
        this.supportRecord = supportRecord;
    }

    public Integer getSupportRecord() 
    {
        return supportRecord;
    }
    public void setAmcRecord(Integer amcRecord) 
    {
        this.amcRecord = amcRecord;
    }

    public Integer getAmcRecord() 
    {
        return amcRecord;
    }
    public void setAccessCode(String accessCode) 
    {
        this.accessCode = accessCode;
    }

    public String getAccessCode() 
    {
        return accessCode;
    }
    public void setCreateHyAdmin(String createHyAdmin) 
    {
        this.createHyAdmin = createHyAdmin;
    }

    public String getCreateHyAdmin() 
    {
        return createHyAdmin;
    }
    public void setCreateUserId(Integer createUserId) 
    {
        this.createUserId = createUserId;
    }

    public Integer getCreateUserId() 
    {
        return createUserId;
    }
    public void setAccountName(String accountName) 
    {
        this.accountName = accountName;
    }

    public String getAccountName() 
    {
        return accountName;
    }
    public void setUsername(String username) 
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }
    public void setToken(String token) 
    {
        this.token = token;
    }

    public String getToken() 
    {
        return token;
    }
    public void setStage(String stage) 
    {
        this.stage = stage;
    }

    public String getStage() 
    {
        return stage;
    }
    public void setGuestLink(String guestLink) 
    {
        this.guestLink = guestLink;
    }

    public String getGuestLink() 
    {
        return guestLink;
    }
    public void setChairmanLink(String chairmanLink) 
    {
        this.chairmanLink = chairmanLink;
    }

    public String getChairmanLink() 
    {
        return chairmanLink;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }
    public void setActive(Integer active) 
    {
        this.active = active;
    }

    public Integer getActive() 
    {
        return active;
    }
    public void setLegacyId(Integer legacyId) 
    {
        this.legacyId = legacyId;
    }

    public Integer getLegacyId() 
    {
        return legacyId;
    }
    public void setOrganizationName(String organizationName) 
    {
        this.organizationName = organizationName;
    }

    public String getOrganizationName() 
    {
        return organizationName;
    }
    public void setMasterTerminalId(Integer masterTerminalId) 
    {
        this.masterTerminalId = masterTerminalId;
    }

    public Integer getMasterTerminalId() 
    {
        return masterTerminalId;
    }
    public void setEnableDataConf(Integer enableDataConf) 
    {
        this.enableDataConf = enableDataConf;
    }

    public Integer getEnableDataConf() 
    {
        return enableDataConf;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("chairmanPassword", getChairmanPassword())
            .append("conferenceTimeType", getConferenceTimeType())
            .append("duration", getDuration())
            .append("guestPassword", getGuestPassword())
            .append("scheduleStartTime", getScheduleStartTime())
            .append("subject", getSubject())
            .append("vmrNumber", getVmrNumber())
            .append("type", getType())
            .append("maxParticipantNum", getMaxParticipantNum())
            .append("voiceActive", getVoiceActive())
            .append("createTime", getCreateTime())
            .append("rate", getRate())
            .append("periodConferenceTime", getPeriodConferenceTime())
            .append("durationPerPeriodUnit", getDurationPerPeriodUnit())
            .append("periodUnitType", getPeriodUnitType())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("weekIndexInMonthMode", getWeekIndexInMonthMode())
            .append("conferenceId", getConferenceId())
            .append("videoResolution", getVideoResolution())
            .append("svcVideoResolution", getSvcVideoResolution())
            .append("autoMute", getAutoMute())
            .append("supportLive", getSupportLive())
            .append("supportRecord", getSupportRecord())
            .append("amcRecord", getAmcRecord())
            .append("accessCode", getAccessCode())
            .append("createHyAdmin", getCreateHyAdmin())
            .append("createUserId", getCreateUserId())
            .append("accountName", getAccountName())
            .append("username", getUsername())
            .append("token", getToken())
            .append("stage", getStage())
            .append("guestLink", getGuestLink())
            .append("chairmanLink", getChairmanLink())
            .append("category", getCategory())
            .append("active", getActive())
            .append("legacyId", getLegacyId())
            .append("organizationName", getOrganizationName())
            .append("masterTerminalId", getMasterTerminalId())
            .append("enableDataConf", getEnableDataConf())
            .append("password", getPassword())
            .toString();
    }
}

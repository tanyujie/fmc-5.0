package com.paradisecloud.fcm.tencent.model.request;

import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/21 14:26
 */
public class BusiTencentConferenceAppointmentRequest {

    private Integer id;

    /** 部门id */
    private Long deptId;

    private String chairmanPassword;

    /** 会议类型 */
    private String conferenceTimeType;

    /** 时长 */
    private Integer duration;

    private String guestPassword;

    /** utc时间 */
    private String scheduleStartTime;

    /** 主题 */
    private String subject;

    /** 虚拟号码 */
    private String vmrNumber;

    /** 视频会议，语音会议 */
    private String type;

    /** 最大入会数量 */
    private Integer maxParticipantNum=500;

    /** 静音入会 */
    private Integer voiceActive=2;

    /** 带宽 */
    private Integer rate=1920;


    private String periodConferenceTime;


    private String durationPerPeriodUnit;


    private String periodUnitType;


    private String startDate;


    private String endDate;


    private Integer weekIndexInMonthMode;

    /** 会议ID */
    private String conferenceId;

    /** 视频分辨率（MPI_1080P） */
    private String videoResolution;

    /** svc视频分辨率 */
    private String svcVideoResolution;

    /** 自动静音 */
    private Integer autoMute=2;

    /** 直播 */
    private Integer supportLive=2;

    /** 录播 */
    private Integer supportRecord=2;

    /** 数据会议 */
    private Integer amcRecord=2;


    private String settings;

    private Long masterTerminalId;

    private Integer enableDataConf=2;

    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getChairmanPassword() {
        return chairmanPassword;
    }

    public void setChairmanPassword(String chairmanPassword) {
        this.chairmanPassword = chairmanPassword;
    }

    public String getConferenceTimeType() {
        return conferenceTimeType;
    }

    public void setConferenceTimeType(String conferenceTimeType) {
        this.conferenceTimeType = conferenceTimeType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getGuestPassword() {
        return guestPassword;
    }

    public void setGuestPassword(String guestPassword) {
        this.guestPassword = guestPassword;
    }

    public String getScheduleStartTime() {
        return scheduleStartTime;
    }

    public void setScheduleStartTime(String scheduleStartTime) {
        this.scheduleStartTime = scheduleStartTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getVmrNumber() {
        return vmrNumber;
    }

    public void setVmrNumber(String vmrNumber) {
        this.vmrNumber = vmrNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMaxParticipantNum() {
        return maxParticipantNum;
    }

    public void setMaxParticipantNum(Integer maxParticipantNum) {
        this.maxParticipantNum = maxParticipantNum;
    }

    public Integer getVoiceActive() {
        return voiceActive;
    }

    public void setVoiceActive(Integer voiceActive) {
        this.voiceActive = voiceActive;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String getPeriodConferenceTime() {
        return periodConferenceTime;
    }

    public void setPeriodConferenceTime(String periodConferenceTime) {
        this.periodConferenceTime = periodConferenceTime;
    }

    public String getDurationPerPeriodUnit() {
        return durationPerPeriodUnit;
    }

    public void setDurationPerPeriodUnit(String durationPerPeriodUnit) {
        this.durationPerPeriodUnit = durationPerPeriodUnit;
    }

    public String getPeriodUnitType() {
        return periodUnitType;
    }

    public void setPeriodUnitType(String periodUnitType) {
        this.periodUnitType = periodUnitType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getWeekIndexInMonthMode() {
        return weekIndexInMonthMode;
    }

    public void setWeekIndexInMonthMode(Integer weekIndexInMonthMode) {
        this.weekIndexInMonthMode = weekIndexInMonthMode;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public String getVideoResolution() {
        return videoResolution;
    }

    public void setVideoResolution(String videoResolution) {
        this.videoResolution = videoResolution;
    }

    public String getSvcVideoResolution() {
        return svcVideoResolution;
    }

    public void setSvcVideoResolution(String svcVideoResolution) {
        this.svcVideoResolution = svcVideoResolution;
    }

    public Integer getAutoMute() {
        return autoMute;
    }

    public void setAutoMute(Integer autoMute) {
        this.autoMute = autoMute;
    }

    public Integer getSupportLive() {
        return supportLive;
    }

    public void setSupportLive(Integer supportLive) {
        this.supportLive = supportLive;
    }

    public Integer getSupportRecord() {
        return supportRecord;
    }

    public void setSupportRecord(Integer supportRecord) {
        this.supportRecord = supportRecord;
    }

    public Integer getAmcRecord() {
        return amcRecord;
    }

    public void setAmcRecord(Integer amcRecord) {
        this.amcRecord = amcRecord;
    }



    public Long getMasterTerminalId() {
        return masterTerminalId;
    }

    public void setMasterTerminalId(Long masterTerminalId) {
        this.masterTerminalId = masterTerminalId;
    }

    public Integer getEnableDataConf() {
        return enableDataConf;
    }

    public void setEnableDataConf(Integer enableDataConf) {
        this.enableDataConf = enableDataConf;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }
}

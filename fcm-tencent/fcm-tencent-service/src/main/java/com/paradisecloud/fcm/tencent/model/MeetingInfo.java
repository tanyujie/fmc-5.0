package com.paradisecloud.fcm.tencent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tencentcloudapi.wemeet.models.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author nj
 * @date 2023/8/8 14:08
 */
public class MeetingInfo implements Serializable {
    @Expose
    @SerializedName("subject")
    private String subject;
    @Expose
    @SerializedName("meeting_id")
    private String meetingId;
    @Expose
    @SerializedName("meeting_code")
    private String meetingCode;
    @Expose
    @SerializedName("password")
    private String password;
    @Expose
    @SerializedName("need_password")
    private Boolean needPassword;
    @Expose
    @SerializedName("status")
    private String status;
    @Expose
    @SerializedName("type")
    private Integer type;
    @Expose
    @SerializedName("join_url")
    private String joinUrl;
    @Expose
    @SerializedName("hosts")
    private List<User> hosts;
    @Expose
    @SerializedName("current_hosts")
    private List<User> currentHosts;
    @Expose
    @SerializedName("current_co_hosts")
    private List<User> currentCoHosts;
    @Expose
    @SerializedName("participants")
    private List<User> participants;
    @Expose
    @SerializedName("start_time")
    private String startTime;
    @Expose
    @SerializedName("end_time")
    private String endTime;
    @Expose
    @SerializedName("settings")
    private MeetingSetting settings;
    @Expose
    @SerializedName("meeting_type")
    private Integer meetingType;
    @Expose
    @SerializedName("recurring_rule")
    private RecurringRule recurringRule;
    @Expose
    @SerializedName("sub_meetings")
    private List<SubMeeting> subMeetings;
    @Expose
    @SerializedName("has_more_sub_meeting")
    private Integer hasMoreSubMeeting;
    @Expose
    @SerializedName("remain_sub_meetings")
    private Integer remainSubMeetings;
    @Expose
    @SerializedName("current_sub_meeting_id")
    private String currentSubMeetingId;
    @Expose
    @SerializedName("user_non_registered")
    private List<String> userNonRegistered;
    @Expose
    @SerializedName("enable_live")
    private Boolean enableLive;
    @Expose
    @SerializedName("live_config")
    private LiveConfig liveConfig;
    @Expose
    @SerializedName("meeting_room_info_list")
    private List<MeetingRoomInfo> meetingRoomInfoList;
    @Expose
    @SerializedName("enable_doc_upload_permission")
    private Boolean enableDocUploadPermission;
    @Expose
    @SerializedName("guests")
    private List<Guest> guests;

    public MeetingInfo() {
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMeetingId() {
        return this.meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingCode() {
        return this.meetingCode;
    }

    public void setMeetingCode(String meetingCode) {
        this.meetingCode = meetingCode;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getNeedPassword() {
        return this.needPassword;
    }

    public void setNeedPassword(Boolean needPassword) {
        this.needPassword = needPassword;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getJoinUrl() {
        return this.joinUrl;
    }

    public void setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
    }

    public List<User> getHosts() {
        return this.hosts;
    }

    public void setHosts(List<User> hosts) {
        this.hosts = hosts;
    }

    public List<User> getParticipants() {
        return this.participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public List<String> getUserNonRegistered() {
        return this.userNonRegistered;
    }

    public void setUserNonRegistered(List<String> userNonRegistered) {
        this.userNonRegistered = userNonRegistered;
    }

    public List<User> getCurrentHosts() {
        return this.currentHosts;
    }

    public void setCurrentHosts(List<User> currentHosts) {
        this.currentHosts = currentHosts;
    }

    public List<User> getCurrentCoHosts() {
        return this.currentCoHosts;
    }

    public void setCurrentCoHosts(List<User> currentCoHosts) {
        this.currentCoHosts = currentCoHosts;
    }

    public Boolean getEnableLive() {
        return this.enableLive;
    }

    public void setEnableLive(Boolean enableLive) {
        this.enableLive = enableLive;
    }

    public LiveConfig getLiveConfig() {
        return this.liveConfig;
    }

    public void setLiveConfig(LiveConfig liveConfig) {
        this.liveConfig = liveConfig;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public MeetingSetting getSettings() {
        return this.settings;
    }

    public void setSettings(MeetingSetting settings) {
        this.settings = settings;
    }

    public Integer getMeetingType() {
        return this.meetingType;
    }

    public void setMeetingType(Integer meetingType) {
        this.meetingType = meetingType;
    }

    public RecurringRule getRecurringRule() {
        return this.recurringRule;
    }

    public void setRecurringRule(RecurringRule recurringRule) {
        this.recurringRule = recurringRule;
    }

    public List<MeetingRoomInfo> getMeetingRoomInfoList() {
        return this.meetingRoomInfoList;
    }

    public void setMeetingRoomInfoList(List<MeetingRoomInfo> meetingRoomInfoList) {
        this.meetingRoomInfoList = meetingRoomInfoList;
    }

    public Boolean getEnableDocUploadPermission() {
        return this.enableDocUploadPermission;
    }

    public void setEnableDocUploadPermission(Boolean enableDocUploadPermission) {
        this.enableDocUploadPermission = enableDocUploadPermission;
    }

    public List<Guest> getGuests() {
        return this.guests;
    }

    public void setGuests(List<Guest> guests) {
        this.guests = guests;
    }

    public List<SubMeeting> getSubMeetings() {
        return this.subMeetings;
    }

    public void setSubMeetings(List<SubMeeting> subMeetings) {
        this.subMeetings = subMeetings;
    }

    public Integer getHasMoreSubMeeting() {
        return this.hasMoreSubMeeting;
    }

    public void setHasMoreSubMeeting(Integer hasMoreSubMeeting) {
        this.hasMoreSubMeeting = hasMoreSubMeeting;
    }

    public Integer getRemainSubMeetings() {
        return this.remainSubMeetings;
    }

    public void setRemainSubMeetings(Integer remainSubMeetings) {
        this.remainSubMeetings = remainSubMeetings;
    }

    public String getCurrentSubMeetingId() {
        return this.currentSubMeetingId;
    }

    public void setCurrentSubMeetingId(String currentSubMeetingId) {
        this.currentSubMeetingId = currentSubMeetingId;
    }
}

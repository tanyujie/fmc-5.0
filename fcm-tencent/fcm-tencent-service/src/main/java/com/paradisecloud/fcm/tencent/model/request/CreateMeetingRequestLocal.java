package com.paradisecloud.fcm.tencent.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.*;

import java.util.List;

/**
 * @author nj
 * @date 2024/2/23 14:52
 */
public class CreateMeetingRequestLocal extends AbstractModel {
    @Expose
    @SerializedName("userid")
    private String userId;
    @Expose
    @SerializedName("instanceid")
    private Integer instanceId;
    @Expose
    @SerializedName("subject")
    private String subject;
    @Expose
    @SerializedName("host_key")
    private String hostKey;
    @Expose
    @SerializedName("type")
    private Integer type;
    @Expose
    @SerializedName("hosts")
    private List<User> hosts;
    @Expose
    @SerializedName("invitees")
    private List<User> invitees;
    @Expose
    @SerializedName("start_time")
    private String startTime;
    @Expose
    @SerializedName("end_time")
    private String endTime;
    @Expose
    @SerializedName("password")
    private String password;
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
    @SerializedName("enable_live")
    private Boolean enableLive;
    @Expose
    @SerializedName("live_config")
    private LiveConfig liveConfig;
    @Expose
    @SerializedName("enable_doc_upload_permission")
    private Boolean enableDocUploadPermission;
    @Expose
    @SerializedName("guests")
    private List<Guest> guests;

    @Expose
    @SerializedName("enable_host_key")
    private Boolean enableHostkey;

    public CreateMeetingRequestLocal() {
    }

    @Override
    public String getPath() {
        return "/v1/meetings";
    }

    @Override
    public String getBody() {
        return GSON.toJson(this);
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/json");
    }

    @Override
    public HttpMethodEnum getMethod() {
        return HttpMethodEnum.POST;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(Integer instanceId) {
        this.instanceId = instanceId;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<User> getHosts() {
        return this.hosts;
    }

    public void setHosts(List<User> hosts) {
        this.hosts = hosts;
    }

    public List<User> getInvitees() {
        return this.invitees;
    }

    public void setInvitees(List<User> invitees) {
        this.invitees = invitees;
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

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getHostKey() {
        return hostKey;
    }

    public void setHostKey(String hostKey) {
        this.hostKey = hostKey;
        this.addParams("hostKey", hostKey);
    }

    public Boolean getEnableHostkey() {
        return enableHostkey;
    }

    public void setEnableHostkey(Boolean enableHostkey) {
        this.enableHostkey = enableHostkey;
        this.addParams("enable_host_key", enableHostkey.toString());
    }
}

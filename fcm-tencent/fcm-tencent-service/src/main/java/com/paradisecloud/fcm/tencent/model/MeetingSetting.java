package com.paradisecloud.fcm.tencent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author nj
 * @date 2023/8/8 14:10
 */
public class MeetingSetting {

    @Expose
    @SerializedName("mute_enable_join")
    private Boolean muteEnableJoin;
    @Expose
    @SerializedName("allow_unmute_self")
    private Boolean allowUnmuteSelf;
    @Expose
    @SerializedName("mute_all")
    private Boolean muteAll;
    @Expose
    @SerializedName("host_video")
    private Boolean hostVideo;
    @Expose
    @SerializedName("participant_video")
    private Boolean participantVideo;
    @Expose
    @SerializedName("play_ivr_on_leave")
    private Boolean playIvrOnLeave;
    @Expose
    @SerializedName("play_ivr_on_join")
    private Boolean playIvrOnJoin;
    @Expose
    @SerializedName("allow_in_before_host")
    private Boolean allowInBeforeHost;
    @Expose
    @SerializedName("auto_in_waiting_room")
    private Boolean autoInWaitingRoom;
    @Expose
    @SerializedName("allow_screen_shared_watermark")
    private Boolean allowScreenSharedWatermark;
    @Expose
    @SerializedName("water_mark_type")
    private Integer waterMarkType;
    @Expose
    @SerializedName("only_enterprise_user_allowed")
    private Boolean onlyEnterpriseUserAllowed;
    @Expose
    @SerializedName("auto_record_type")
    private String autoRecordType;
    @Expose
    @SerializedName("participant_join_auto_record")
    private Boolean participantJoinAutoRecord;
    @Expose
    @SerializedName("enable_host_pause_auto_record")
    private Boolean enableHostPauseAutoRecord;


    @Expose
    @SerializedName("mute_enable_type_join")
    private Integer muteEnableTypeJoin;

    public MeetingSetting() {
    }

    public Boolean getMuteEnableJoin() {
        return this.muteEnableJoin;
    }

    public void setMuteEnableJoin(Boolean muteEnableJoin) {
        this.muteEnableJoin = muteEnableJoin;
    }

    public Boolean getAllowUnmuteSelf() {
        return this.allowUnmuteSelf;
    }

    public void setAllowUnmuteSelf(Boolean allowUnmuteSelf) {
        this.allowUnmuteSelf = allowUnmuteSelf;
    }

    public Boolean getMuteAll() {
        return this.muteAll;
    }

    public void setMuteAll(Boolean muteAll) {
        this.muteAll = muteAll;
    }

    public Boolean getHostVideo() {
        return this.hostVideo;
    }

    public void setHostVideo(Boolean hostVideo) {
        this.hostVideo = hostVideo;
    }

    public Boolean getParticipantVideo() {
        return this.participantVideo;
    }

    public void setParticipantVideo(Boolean participantVideo) {
        this.participantVideo = participantVideo;
    }

    public Boolean getPlayIvrOnLeave() {
        return this.playIvrOnLeave;
    }

    public void setPlayIvrOnLeave(Boolean playIvrOnLeave) {
        this.playIvrOnLeave = playIvrOnLeave;
    }

    public Boolean getPlayIvrOnJoin() {
        return this.playIvrOnJoin;
    }

    public void setPlayIvrOnJoin(Boolean playIvrOnJoin) {
        this.playIvrOnJoin = playIvrOnJoin;
    }

    public Boolean getAllowInBeforeHost() {
        return this.allowInBeforeHost;
    }

    public void setAllowInBeforeHost(Boolean allowInBeforeHost) {
        this.allowInBeforeHost = allowInBeforeHost;
    }

    public Boolean getAutoInWaitingRoom() {
        return this.autoInWaitingRoom;
    }

    public void setAutoInWaitingRoom(Boolean autoInWaitingRoom) {
        this.autoInWaitingRoom = autoInWaitingRoom;
    }

    public Boolean getAllowScreenSharedWatermark() {
        return this.allowScreenSharedWatermark;
    }

    public void setAllowScreenSharedWatermark(Boolean allowScreenSharedWatermark) {
        this.allowScreenSharedWatermark = allowScreenSharedWatermark;
    }

    public Integer getWaterMarkType() {
        return this.waterMarkType;
    }

    public void setWaterMarkType(Integer waterMarkType) {
        this.waterMarkType = waterMarkType;
    }

    public Boolean getOnlyEnterpriseUserAllowed() {
        return this.onlyEnterpriseUserAllowed;
    }

    public void setOnlyEnterpriseUserAllowed(Boolean onlyEnterpriseUserAllowed) {
        this.onlyEnterpriseUserAllowed = onlyEnterpriseUserAllowed;
    }

    public String getAutoRecordType() {
        return this.autoRecordType;
    }

    public void setAutoRecordType(String autoRecordType) {
        this.autoRecordType = autoRecordType;
    }

    public Boolean getParticipantJoinAutoRecord() {
        return this.participantJoinAutoRecord;
    }

    public void setParticipantJoinAutoRecord(Boolean participantJoinAutoRecord) {
        this.participantJoinAutoRecord = participantJoinAutoRecord;
    }

    public Boolean getEnableHostPauseAutoRecord() {
        return this.enableHostPauseAutoRecord;
    }

    public void setEnableHostPauseAutoRecord(Boolean enableHostPauseAutoRecord) {
        this.enableHostPauseAutoRecord = enableHostPauseAutoRecord;
    }

    public Integer getMuteEnableTypeJoin() {
        return muteEnableTypeJoin;
    }

    public void setMuteEnableTypeJoin(Integer muteEnableTypeJoin) {
        this.muteEnableTypeJoin = muteEnableTypeJoin;
    }
}

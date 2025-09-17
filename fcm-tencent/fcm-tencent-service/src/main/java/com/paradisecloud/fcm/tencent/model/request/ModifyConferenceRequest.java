package com.paradisecloud.fcm.tencent.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

/**
 * @author nj
 * @date 2023/7/13 10:12
 */
public class ModifyConferenceRequest extends AbstractModel {
    private String meetingId;

    @Expose
    @SerializedName("uuid")
    private String uuid;
    @Expose
    @SerializedName("instanceid")
    private Integer instanceid;
    @Expose
    @SerializedName("mute_all")
    private Boolean muteAll;
    @Expose
    @SerializedName("allow_unmute_by_self")
    private Boolean allowUnmuteBySelf;
    @Expose
    @SerializedName("participant_join_mute")
    private Integer participantJoinMute;
    @Expose
    @SerializedName("meeting_locked")
    private Boolean meetingLocked;
    @Expose
    @SerializedName("hide_meeting_code_password")
    private Boolean hideMeetingCodePassword;
    @Expose
    @SerializedName("allow_chat")
    private Integer allowChat;
    @Expose
    @SerializedName("share_screen")
    private Boolean shareScreen;
    @Expose
    @SerializedName("enable_red_envelope")
    private Boolean enableRedEnvelope;
    @Expose
    @SerializedName("only_enterprise_user_allowed")
    private Boolean onlyEnterpriseUserAllowed;
    @Expose
    @SerializedName("play_ivr_on_join")
    private Boolean playIvrOnJoin;
    @Expose
    @SerializedName("auto_waiting_room")
    private Boolean autoWaitingRoom;
    @Expose
    @SerializedName("operator_id")
    private String operatorId;
    @Expose
    @SerializedName("operator_id_type")
    private Integer operatorIdType;

    private Integer callInRestriction;


    @Override
    public String getPath() {
        return "/v1/real-control/meetings/"+meetingId+"/status";
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
        return HttpMethodEnum.PUT;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(Integer instanceid) {
        this.instanceid = instanceid;
    }

    public Boolean getMuteAll() {
        return muteAll;
    }

    public void setMuteAll(Boolean muteAll) {
        this.muteAll = muteAll;
    }

    public Boolean getAllowUnmuteBySelf() {
        return allowUnmuteBySelf;
    }

    public void setAllowUnmuteBySelf(Boolean allowUnmuteBySelf) {
        this.allowUnmuteBySelf = allowUnmuteBySelf;
    }

    public Integer getParticipantJoinMute() {
        return participantJoinMute;
    }

    public void setParticipantJoinMute(Integer participantJoinMute) {
        this.participantJoinMute = participantJoinMute;
    }

    public Boolean getMeetingLocked() {
        return meetingLocked;
    }

    public void setMeetingLocked(Boolean meetingLocked) {
        this.meetingLocked = meetingLocked;
    }

    public Boolean getHideMeetingCodePassword() {
        return hideMeetingCodePassword;
    }

    public void setHideMeetingCodePassword(Boolean hideMeetingCodePassword) {
        this.hideMeetingCodePassword = hideMeetingCodePassword;
    }

    public Integer getAllowChat() {
        return allowChat;
    }

    public void setAllowChat(Integer allowChat) {
        this.allowChat = allowChat;
    }

    public Boolean getShareScreen() {
        return shareScreen;
    }

    public void setShareScreen(Boolean shareScreen) {
        this.shareScreen = shareScreen;
    }

    public Boolean getEnableRedEnvelope() {
        return enableRedEnvelope;
    }

    public void setEnableRedEnvelope(Boolean enableRedEnvelope) {
        this.enableRedEnvelope = enableRedEnvelope;
    }

    public Boolean getOnlyEnterpriseUserAllowed() {
        return onlyEnterpriseUserAllowed;
    }

    public void setOnlyEnterpriseUserAllowed(Boolean onlyEnterpriseUserAllowed) {
        this.onlyEnterpriseUserAllowed = onlyEnterpriseUserAllowed;
    }

    public Boolean getPlayIvrOnJoin() {
        return playIvrOnJoin;
    }

    public void setPlayIvrOnJoin(Boolean playIvrOnJoin) {
        this.playIvrOnJoin = playIvrOnJoin;
    }

    public Boolean getAutoWaitingRoom() {
        return autoWaitingRoom;
    }

    public void setAutoWaitingRoom(Boolean autoWaitingRoom) {
        this.autoWaitingRoom = autoWaitingRoom;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getOperatorIdType() {
        return operatorIdType;
    }

    public void setOperatorIdType(Integer operatorIdType) {
        this.operatorIdType = operatorIdType;
    }

    public Integer getCallInRestriction() {
        return callInRestriction;
    }

    public void setCallInRestriction(Integer callInRestriction) {
        this.callInRestriction = callInRestriction;
    }
}

package com.paradisecloud.fcm.tencent.model.reponse;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/7/13 10:16
 */
@NoArgsConstructor
@Data
public class MeetingStatusResponse extends BaseResponse {

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
}

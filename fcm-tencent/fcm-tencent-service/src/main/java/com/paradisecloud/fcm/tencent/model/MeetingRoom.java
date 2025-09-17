package com.paradisecloud.fcm.tencent.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nj
 * @date 2024/3/12 16:51
 */
@NoArgsConstructor
@Data
public class MeetingRoom implements Serializable {


    @Expose
    @SerializedName("meeting_room_id")
    private String meetingRoomId;

    @Expose
    @SerializedName("meeting_room_name")
    private String meetingRoomName;

    @Expose
    @SerializedName("meeting_room_location")
    private String meetingRoomLocation;

    @Expose
    @SerializedName("account_type")
    private Integer accountType;

    @Expose
    @SerializedName("active_code")
    private String activeCode;

    @Expose
    @SerializedName("participant_number")
    private Integer participantNumber;

    @Expose
    @SerializedName("meeting_room_status")
    private Integer meetingRoomStatus;

    @Expose
    @SerializedName("scheduled_status")
    private Integer scheduledStatus;

    @Expose
    @SerializedName("is_allow_call")
    private Boolean isAllowCall;
}

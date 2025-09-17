package com.paradisecloud.fcm.tencent.model.reponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/8/10 16:51
 */
@NoArgsConstructor
@Data
public class WaitingRoomResponse extends BaseResponse {

    @Expose
    @SerializedName("meeting_id")
    private String meetingId;
    @Expose
    @SerializedName("subject")
    private String subject;
    @Expose
    @SerializedName("meeting_code")
    private String meetingCode;
    @Expose
    @SerializedName("schedule_start_time")
    private String scheduleStartTime;
    @Expose
    @SerializedName("schedule_end_time")
    private String scheduleEndTime;
    @Expose
    @SerializedName("participants")
    private List<ParticipantsDTO> participants;
    @Expose
    @SerializedName("current_size")
    private Integer currentSize;
    @Expose
    @SerializedName("total_count")
    private Integer totalCount;
    @Expose
    @SerializedName("current_page")
    private Integer currentPage;
    @Expose
    @SerializedName("total_page")
    private Integer totalPage;

    @NoArgsConstructor
    @Data
    public static class ParticipantsDTO {
        @Expose
        @SerializedName("user_name")
        private String userName;
        @Expose
        @SerializedName("userid")
        private String userid;
        @Expose
        @SerializedName("open_id")
        private String openId;
        @Expose
        @SerializedName("ms_open_id")
        private String msOpenId;
        @Expose
        @SerializedName("instanceid")
        private String instanceid;
        @Expose
        @SerializedName("join_time")
        private String joinTime;
        @Expose
        @SerializedName("left_time")
        private String leftTime;
        @Expose
        @SerializedName("app_version")
        private String appVersion;

        private String loginType;
    }
}

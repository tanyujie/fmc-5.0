package com.paradisecloud.fcm.tencent.model.reponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author nj
 * @date 2023/7/11 10:19
 */
@NoArgsConstructor
@Data
public class RealTimeParticipantsResponse extends BaseResponse implements Serializable {


    @Expose
    @SerializedName("meeting_id")
    private String meetingId;
    @Expose
    @SerializedName("meeting_code")
    private String meetingCode;
    @Expose
    @SerializedName("subject")
    private String subject;
    @Expose
    @SerializedName("schedule_start_time")
    private String scheduleStartTime;
    @Expose
    @SerializedName("schedule_end_time")
    private String scheduleEndTime;
    @Expose
    @SerializedName("status")
    private String status;
    @Expose
    @SerializedName("participants")
    private List<ParticipantsDTO> participants;
    @Expose
    @SerializedName("current_page")
    private Integer currentPage;
    @Expose
    @SerializedName("current_size")
    private Integer currentSize;
    @Expose
    @SerializedName("total_count")
    private Integer totalCount;
    @Expose
    @SerializedName("total_page")
    private Integer totalPage;

    @NoArgsConstructor
    @Data
    public static class ParticipantsDTO {
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
        @SerializedName("user_name")
        private String userName;
        @Expose
        @SerializedName("join_time")
        private String joinTime;
        @Expose
        @SerializedName("instanceid")
        private Integer instanceid;
        /**
         * 0：普通成员角色
         * 1：创建者角色
         * 2：主持人
         * 3：创建者+主持人
         * 4：游客
         * 5：游客+主持人
         * 6：联席主持人
         * 7：创建者+联席主持人
         */
        @Expose
        @SerializedName("user_role")
        private Integer userRole;
        @Expose
        @SerializedName("join_type")
        private Integer joinType;
        @Expose
        @SerializedName("app_version")
        private String appVersion;
        @Expose
        @SerializedName("audio_state")
        private Boolean audioState;
        @Expose
        @SerializedName("video_state")
        private Boolean videoState;
        @Expose
        @SerializedName("screen_shared_state")
        private Boolean screenSharedState;

    }
}

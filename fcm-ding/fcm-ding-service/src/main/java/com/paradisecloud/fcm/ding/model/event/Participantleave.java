package com.paradisecloud.fcm.ding.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/12 17:15
 */
@NoArgsConstructor
@Data
public class Participantleave {


    @JsonProperty("event")
    private String event;
    @JsonProperty("trace_id")
    private String traceId;
    @JsonProperty("payload")
    private List<PayloadDTO> payload;

    @NoArgsConstructor
    @Data
    public static class PayloadDTO {
        @JsonProperty("operate_time")
        private Long operateTime;
        @JsonProperty("operator")
        private OperatorDTO operator;
        @JsonProperty("meeting_info")
        private MeetingInfoDTO meetingInfo;

        @NoArgsConstructor
        @Data
        public static class OperatorDTO {
            @JsonProperty("userid")
            private String userid;
            @JsonProperty("user_name")
            private String userName;
            @JsonProperty("uuid")
            private String uuid;
            @JsonProperty("instance_id")
            private String instanceId;
            @JsonProperty("ms_open_id")
            private String msOpenId;
        }

        @NoArgsConstructor
        @Data
        public static class MeetingInfoDTO {
            @JsonProperty("meeting_id")
            private String meetingId;
            @JsonProperty("meeting_code")
            private String meetingCode;
            @JsonProperty("subject")
            private String subject;
            @JsonProperty("creator")
            private CreatorDTO creator;
            @JsonProperty("meeting_type")
            private Integer meetingType;
            @JsonProperty("start_time")
            private Integer startTime;
            @JsonProperty("end_time")
            private Integer endTime;
            @JsonProperty("meeting_create_mode")
            private Integer meetingCreateMode;

            @NoArgsConstructor
            @Data
            public static class CreatorDTO {
                @JsonProperty("userid")
                private String userid;
                @JsonProperty("user_name")
                private String userName;
                @JsonProperty("uuid")
                private String uuid;
                @JsonProperty("instance_id")
                private String instanceId;
                @JsonProperty("ms_open_id")
                private String msOpenId;
            }
        }
    }
}

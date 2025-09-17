package com.paradisecloud.fcm.ding.model.event;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/10 9:26
 */
@NoArgsConstructor
@Data
public class ParticipantJoined {


    private String event;
    private String traceId;
    private List<PayloadDTO> payload;

    @NoArgsConstructor
    @Data
    public static class PayloadDTO {
        private Long operateTime;
        private OperatorDTO operator;
        private MeetingInfoDTO meetingInfo;

        private OperatorDTO toOperator;

        @NoArgsConstructor
        @Data
        public static class OperatorDTO {
            private String userid;
            private String userName;
            private String uuid;
            private String instanceId;
            private String msOpenId;
            private Integer userRole;
        }

        @NoArgsConstructor
        @Data
        public static class MeetingInfoDTO {
            private String meetingId;
            private String meetingCode;
            private String subject;
            private CreatorDTO creator;
            private Integer meetingType;
            private Integer startTime;
            private Integer endTime;
            private Integer meetingCreateMode;

            @NoArgsConstructor
            @Data
            public static class CreatorDTO {
                private String userid;
                private String userName;
                private String uuid;
                private String instanceId;
                private String msOpenId;
            }
        }
    }
}

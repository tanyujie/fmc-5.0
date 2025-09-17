package com.paradisecloud.fcm.ding.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/2/20 14:40
 */
@NoArgsConstructor
@Data
public class StreamEventModel {


    private String eventUnifiedAppId;
    private String eventCorpId;
    private String eventType;
    private String eventId;
    private Long eventBornTime;
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private OpenConfModelDTO openConfModel;
        private Integer statusSeqNum;
        private String changeScene;
        private List<DataDTO.OpenMemberModelsDTO> openMemberModels;
        @NoArgsConstructor
        @Data
        public static class OpenConfModelDTO {
            private Integer activeNum;
            private String creatorNick;
            private String bizType;
            private Integer attendNum;
            private Integer confDuration;
            private String conferenceId;
            private String creatorId;
            private Long startTime;
            private Integer invitedNum;
            private String title;
            private String scheduleConferenceId;
            private Integer status;
        }

        @NoArgsConstructor
        @Data
        public static class OpenMemberModelsDTO {
            private Integer duration;
            private Long leaveTime;
            private String deviceType;
            private Boolean pstnJoin;
            private Long joinTime;
            private String userNick;
            private String conferenceId;
            private Integer attendStatus;
            private Boolean host;
            private Boolean coHost;
            private String userId;
        }
    }


}

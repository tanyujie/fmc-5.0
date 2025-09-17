package com.paradisecloud.fcm.smc.cache.modle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/5/6 17:30
 */
@NoArgsConstructor
@Data
public class ConferenceState {

    private ParamDTO param;
    private StateDTO state;

    @NoArgsConstructor
    @Data
    public static class ParamDTO {
        private String conferenceId;
        private Integer duration;
        private Integer onlineNum;
        private Integer totalNum;
        private Integer handUpNum;
        private Integer abnormalOfflineNum;
    }

    @NoArgsConstructor
    @Data
    public static class StateDTO {
        private String conferenceId;
        private Boolean mute;
        private Boolean enableUnmuteByGuest;
        private Boolean quiet;
        private String chairmanId;
        private String chooseId;
        private String broadcastId;
        private String spokesmanId;
        private String presenterId;
        private String lockPresenterId;
        private Integer recordStatus;
        private MultiPicInfoDTO multiPicInfo;
        private Boolean enableVoiceActive;
        private String currentSpokesmanId;
        private Boolean lock;
        private Boolean directing;
        private Boolean local;
        private String multiPicPollStatus;
        private String broadcastPollStatus;
        private String chairmanPollStatus;
        private List<ParticipantPollStatusListDTO> participantPollStatusList;
        private Integer handUpNum;
        private String localRecordId;
        private Integer pushStreamStatus;
        private Boolean globalRecordStatus;
        private Boolean enableSiteNameEditByGuest;
        private List<String> currentSpeakers;
        private Integer maxParticipantNum;
        private Boolean generatedAlarm;

        @NoArgsConstructor
        @Data
        public static class MultiPicInfoDTO {
            private Integer picNum;
            private Integer mode;
            private List<SubPicListDTO> subPicList;

            @NoArgsConstructor
            @Data
            public static class SubPicListDTO {
                private String participantId;
                private Integer streamNumber;
                private Boolean vas;
            }
        }

        @NoArgsConstructor
        @Data
        public static class ParticipantPollStatusListDTO {
            private String participantId;
            private Boolean chairmanPoll;
            private String pollStatus;
        }
    }
}

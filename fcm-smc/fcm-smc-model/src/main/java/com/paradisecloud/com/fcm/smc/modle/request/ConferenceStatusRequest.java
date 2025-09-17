package com.paradisecloud.com.fcm.smc.modle.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/26 9:01
 */
@NoArgsConstructor
@Data
public class ConferenceStatusRequest {

    private String conferenceId;
    private String chairman;
    private String broadcaster;
    private String spokesman;
    private String lockPresenter;
    private String presenter;
    private Boolean isOnline;
    private Boolean isMute;
    private Boolean isQuiet;
    private Boolean isVoiceActive;
    private MultiPicInfoDTO multiPicInfo;
    private String recordOpType;
    private RecordResourceDTO recordResource;
    private String mode;
    private Integer subtitlesOpType;
    private Integer extendTime;
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
        }
    }

    @NoArgsConstructor
    @Data
    public static class RecordResourceDTO {
        private Integer picNum;
        private Integer mode;
        private List<SubPicListDTO> subPicList;

        @NoArgsConstructor
        @Data
        public static class SubPicListDTO {
            private String participantId;
            private Integer streamNumber;
        }
    }
}

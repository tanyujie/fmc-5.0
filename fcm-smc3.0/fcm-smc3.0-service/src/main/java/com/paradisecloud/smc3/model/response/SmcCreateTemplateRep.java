package com.paradisecloud.smc3.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/26 17:19
 */
@NoArgsConstructor
@Data
public class SmcCreateTemplateRep {


    private ConferenceDTO conference;
    private MultiConferenceServiceDTO multiConferenceService;
    private StreamServiceDTO streamService;
    private CheckInServiceDTO checkInService;
    private SubtitleServiceDTO subtitleService;
    private ConfPresetParamDTO confPresetParam;
    private List<ParticipantsDTO> participants;
    private List<?> attendees;

    @NoArgsConstructor
    @Data
    public static class ConferenceDTO {
        private String id;
        private Integer legacyId;
        private String subject;
        private String username;
        private String accountName;
        private String scheduleStartTime;
        private String conferenceTimeType;
        private PeriodConferenceTimeDTO periodConferenceTime;
        private Integer duration;
        private String category;
        private String stage;
        private String organizationName;
        private Boolean active;
        private String token;
        private String accessCode;

        @NoArgsConstructor
        @Data
        public static class PeriodConferenceTimeDTO {
            private Integer durationPerPeriodUnit;
            private String startDate;
            private String endDate;
            private Integer weekIndexInMonthMode;
            private Integer dayIndexInMonthMode;
            private List<?> dayLists;
        }
    }

    @NoArgsConstructor
    @Data
    public static class MultiConferenceServiceDTO {
        private ConferencePolicySettingDTO conferencePolicySetting;
        private ConferenceCapabilitySettingDTO conferenceCapabilitySetting;
        private String accessCode;
        private String mainMcuName;
        private String mainServiceZoneName;
        private String mainServiceZoneId;
        private String chairmanLink;
        private String guestLink;

        @NoArgsConstructor
        @Data
        public static class ConferencePolicySettingDTO {
            private Integer language;
            private String timeZoneId;
            private Boolean autoExtend;
            private Boolean autoEnd;
            private Boolean autoMute;
            private Boolean voiceActive;
        }

        @NoArgsConstructor
        @Data
        public static class ConferenceCapabilitySettingDTO {
            private Integer rate;
            private Integer mediaEncrypt;
            private Integer audioProtocol;
            private Integer videoProtocol;
            private Integer videoResolution;
            private Integer dataConfProtocol;
            private Integer reserveResource;
            private Boolean enableDataConf;
            private Boolean enableHdRealTime;
            private String type;
            private Boolean enableRecord;
            private Boolean enableLiveBroadcast;
            private Boolean autoRecord;
            private Boolean audioRecord;
            private Boolean amcRecord;
            private Boolean enableCheckIn;
            private Integer checkInDuration;
        }
    }

    @NoArgsConstructor
    @Data
    public static class StreamServiceDTO {
        private Boolean supportLive;
        private Boolean supportRecord;
        private Boolean autoRecord;
        private Boolean audioRecord;
        private Boolean amcRecord;
        private Boolean supportPushStream;
        private Boolean autoPushStream;
        private Boolean supportMinutes;
        private String auxPushStreamAddress;
        private String livePushStreamAddress;
        private String playPushStreamAddress;
    }

    @NoArgsConstructor
    @Data
    public static class CheckInServiceDTO {
        private Boolean enableCheckIn;
        private Integer checkInDuration;
    }

    @NoArgsConstructor
    @Data
    public static class SubtitleServiceDTO {
        private Boolean enableSubtitle;
        private String srcLang;
        private List<String> supLanguageList;
        private String subscribePath;
    }

    @NoArgsConstructor
    @Data
    public static class ConfPresetParamDTO {
        private List<?> presetMultiPics;
    }

    @NoArgsConstructor
    @Data
    public static class ParticipantsDTO {
        private String id;
        private String uri;
        private String name;
        private Integer ipProtocolType;
        private String dialMode;
        private String encodeType;
        private Boolean forward;
        private Integer rate;
        private Boolean voice;
        private Integer audioProtocol;
        private Integer videoProtocol;
        private Integer videoResolution;
        private Integer dataConfProtocol;
        private String serviceZoneId;
        private String serviceZoneName;
        private String mcuId;
        private String mcuName;
        private Boolean mainParticipant;
        private String dtmfInfo;
        private Long deptId;
        private Long terminalId;
        private Boolean isCascade;
        private Integer terminalType;
        private String conferenceIdFme;
    }
}

package com.paradisecloud.smc3.model.request;


import com.paradisecloud.smc3.model.ParticipantRspDto;
import com.paradisecloud.smc3.model.TemplateTerminal;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * author nj
 */
@Data
@NoArgsConstructor
public class SmcAppointmentConferenceRequest {
    private AppointmentConference conference;
    private List<ParticipantRspDto> participants;
    private List<Long> participantsIds;
    private List<TemplateTerminal> templateTerminalList;
    private Long deptId;

    private List<?> attendees=new ArrayList<>();
    private CheckInServiceDTO checkInService;
    private ConfPresetParamDTO confPresetParam;
    private MultiConferenceServiceDTO multiConferenceService;
    private List<?> participantsX;
    private SubtitleServiceDTO subtitleService;
    private StreamServiceDTO streamService;




    @NoArgsConstructor
    @Data
    public static class CheckInServiceDTO {
        private Boolean enableCheckIn;
        private Integer checkInDuration;
    }

    @NoArgsConstructor
    @Data
    public static class ConfPresetParamDTO {
        private List<?> presetMultiPics=new ArrayList<>();
    }

    @NoArgsConstructor
    @Data
    public static class ConferenceDTO {
        private String chairmanPassword;
        private String conferenceTimeType;
        private Integer duration;
        private String guestPassword;
        private String scheduleStartTime;
        private String subject;
        private String vmrNumber;
    }

    @NoArgsConstructor
    @Data
    public static class MultiConferenceServiceDTO {
        private ConferenceCapabilitySettingDTO conferenceCapabilitySetting;
        private ConferencePolicySettingDTO conferencePolicySetting;
        private String accessCode;
        private String chairmanLink;
        private String guestLink;
        private String mainMcuName;
        private String mainServiceZoneId;
        private String mainServiceZoneName;

        @NoArgsConstructor
        @Data
        public static class ConferenceCapabilitySettingDTO {
            private Integer rate;
            private Integer svcRate;
            private String svcVideoProtocol;
            private String svcVideoResolution;
            private String mediaEncrypt;
            private String audioProtocol;
            private String videoProtocol;
            private String videoResolution;
            private String dataConfProtocol;
            private Integer reserveResource;
            private Boolean enableFec;
            private Boolean enableRecord;
            private Boolean enableLiveBroadcast;
            private Boolean autoRecord;
            private Boolean enableDataConf;
            private Boolean enableHdRealTime;
            private Boolean supportSubtitle;
            private String srcLang;
            private Boolean enableCheckIn;
            private Integer checkInDuration;
            private Boolean audioRecord;
            private Boolean amcRecord;
            private Boolean supportMinutes;
            private String type;
        }

        @NoArgsConstructor
        @Data
        public static class ConferencePolicySettingDTO {
            private Boolean releaseParticipantRes;
            private Integer language;
            private Boolean autoExtend;
            private Boolean autoEnd;
            private Boolean autoMute;
            private Boolean voiceActive;
            private String chairmanPassword;
            private String guestPassword;
            private Boolean displaySubjectAsCallerDisplayInfo;
            private Integer maxParticipantNum;
        }
    }

    @NoArgsConstructor
    @Data
    public static class SubtitleServiceDTO {
        private Boolean enableSubtitle;
        private String srcLang;
    }

    @NoArgsConstructor
    @Data
    public static class StreamServiceDTO {
        private Boolean supportMinutes;
        private Boolean supportLive;
        private Boolean supportRecord;
        private Boolean autoRecord;
        private Boolean audioRecord;
        private Boolean amcRecord;
    }
}

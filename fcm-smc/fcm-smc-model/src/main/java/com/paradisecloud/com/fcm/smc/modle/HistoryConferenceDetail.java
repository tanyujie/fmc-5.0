package com.paradisecloud.com.fcm.smc.modle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/3/14 15:07
 */
@NoArgsConstructor
@Data
public class HistoryConferenceDetail {


    private String id;
    private String accessCode;
    private String subject;
    private String creatorName;
    private String accountName;
    private String scheduleStartTime;
    private String scheduleEndTime;
    private Integer duration;
    private String category;
    private ConferencePolicySettingDTO conferencePolicySetting;
    private ConferenceCapabilitySettingDTO conferenceCapabilitySetting;
    private String scheduleType;
    private String mainServiceZoneName;
    private String mainServiceZoneId;
    private String organizationName;
    private String liveAddress;
    private List<?> participants;
    private List<?> attendees;
    private StreamServiceDTO streamService;
    private SubtitleServiceDTO subtitleService;

    @NoArgsConstructor
    @Data
    public static class ConferencePolicySettingDTO {
        private Integer language;
        private String timeZoneId;
        private Boolean autoExtend;
        private Boolean autoEnd;
        private Boolean autoMute;
        private Boolean voiceActive;
        private Boolean displaySubjectAsCallerDisplayInfo;
        private Boolean releaseParticipantRes;
        private Integer maxParticipantNum;
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
    }

    @NoArgsConstructor
    @Data
    public static class SubtitleServiceDTO {
        private Boolean enableSubtitle;
        private String srcLang;
    }
}

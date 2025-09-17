package com.paradisecloud.com.fcm.smc.modle.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paradisecloud.com.fcm.smc.modle.response.SmcCreateTemplateRep;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/26 18:02
 */
@NoArgsConstructor
@Data
public class SmcNewTemplateResponse {


    private String id;
    private String subject;
    private String chairmanPassword;
    private String guestPassword;
    private Integer duration;
    private String organizationId;
    private String organizationName;
    private String mainMcuId;
    private String mainMcuName;
    private ConferencePolicySettingDTO conferencePolicySetting;
    private ConferenceCapabilitySettingDTO conferenceCapabilitySetting;
    private List<SmcCreateTemplateRep.ParticipantsDTO> templateParticipants;
    private List<?> templateAttendees;
    private StreamServiceDTO streamService;
    private SubtitleServiceDTO subtitleService;
    private ConfPresetParamDTO confPresetParam;

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
        @JsonProperty("enableSubtitle")
        private Boolean enableSubtitle;
        @JsonProperty("srcLang")
        private String srcLang;
    }

    @NoArgsConstructor
    @Data
    public static class ConfPresetParamDTO {
        @JsonProperty("presetMultiPics")
        private List<?> presetMultiPics;
    }
}

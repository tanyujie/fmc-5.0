package com.paradisecloud.com.fcm.smc.modle.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paradisecloud.com.fcm.smc.modle.SmcTemplateType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/26 17:02
 */
@NoArgsConstructor
@Data
public class SmcTemplateRequest {

    /**
     * 会议名称
     */
    private String subject;
    private String templateType= SmcTemplateType.COMMON_CONF.name();
    private Integer duration=120;
    private String chairmanPassword=null;
    private String guestPassword=null;
    private String mainServiceZoneId;
    private String mainMcuId;
    private String mainMcuName;
    private List<TemplateParticipantsDTO> templateParticipants;
    private List<?> templateAttendees;
    private ConferenceCapabilitySettingDTO conferenceCapabilitySetting;
    private ConferencePolicySettingDTO conferencePolicySetting;
    private StreamServiceDTO streamService;
    private SubtitleServiceDTO subtitleService;
    private ConfPresetParamDTO confPresetParam;
    private String vmrNumber;

    @NoArgsConstructor
    @Data
    public static class ConferenceCapabilitySettingDTO {
        private Integer rate;
        private String mediaEncrypt;
        private String audioProtocol;
        private String videoProtocol;
        private String videoResolution;
        private String dataConfProtocol;
        private Integer reserveResource;
        private Boolean enableDataConf;
        private Boolean supportSubtitle;
        private String srcLang;
        private Boolean supportMinutes;
        private Boolean enableHdRealTime;
        private Boolean enableRecord;
        private Boolean enableLiveBroadcast;
        private Boolean enableCheckIn;
        private Integer checkInDuration;
        private Boolean autoRecord;
        private Boolean audioRecord;
        private Boolean amcRecord;
        private String type;
        private Integer svcRate;
        private String svcVideoProtocol;
        private String svcVideoResolution;
    }

    @NoArgsConstructor
    @Data
    public static class ConferencePolicySettingDTO {
        private Integer language;
        private String timeZoneId;
        private Boolean autoExtend;
        private Boolean autoEnd;
        private Boolean autoMute;
        private Boolean voiceActive;
        private String chairmanPassword;
        private String guestPassword;
    }

    @NoArgsConstructor
    @Data
    public static class StreamServiceDTO {
        private Boolean supportMinutes;
    }

    @NoArgsConstructor
    @Data
    public static class SubtitleServiceDTO {
        private Boolean enableSubtitle;
        private String srcLang;
    }

    @NoArgsConstructor
    @Data
    public static class ConfPresetParamDTO {
        private List<?> presetMultiPics;
    }

    @NoArgsConstructor
    @Data
    public static class TemplateParticipantsDTO {
        private String entryUuid;
        private String name;
        private String uri;
        private String terminalTypeIcon;
        private Integer rate;
        private String serviceZoneName;
        private String serviceZoneId;
        private String serviceZoneNameValue;
        private String mcuId;
        private String mcuName;
        private String ipProtocolType;
        private String participantSourceType;
        private Boolean mainParticipant;
        private String uuid;
    }
}

package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2022/8/15 10:09
 */
@NoArgsConstructor
@Data
public class SmcConferenceTemplate {

    private String id;
    private String subject;
    private String templateType;
    private Integer duration;
    private String chairmanPassword;
    private String guestPassword;
    private String mainServiceZoneId;
    private String mainMcuId;
    private String mainMcuName;
    private List<ParticipantRspDto> templateParticipants;
    private List<Long> templateTerminals;
    private List<Long> templateParticipantsIds;
    private List<TemplateTerminal> templateTerminalList;
    private List<?> templateAttendees;
    private ConferenceCapabilitySettingDTO conferenceCapabilitySetting;
    private ConferencePolicySettingDTO conferencePolicySetting;
    private StreamServiceDTO streamService;
    private SubtitleServiceDTO subtitleService;
    private ConfPresetParamDTO confPresetParam;
    private String vmrNumber;
    private long masterTerminalId;
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
        private Integer language;
        private Boolean autoExtend;
        private Boolean autoEnd;
        private Boolean autoMute;
        private Boolean voiceActive;
        private String chairmanPassword;
        private String guestPassword;
        private Integer maxParticipantNum;
    }

    @NoArgsConstructor
    @Data
    public static class StreamServiceDTO {
        private Boolean supportMinutes;
        private Boolean supportLive;
        private Boolean supportRecord;
        private Boolean amcRecord;
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
        private List<PresetMultiPicReqDto> presetMultiPics=new ArrayList<>();
        private List presetMultiPicRollInfos=new ArrayList();
        private List presetSiteMultiPicRollInfos=new ArrayList();
    }
}

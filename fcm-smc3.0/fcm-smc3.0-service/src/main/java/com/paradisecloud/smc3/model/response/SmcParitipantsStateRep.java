package com.paradisecloud.smc3.model.response;

import com.paradisecloud.smc3.model.ParticipantState;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/9/20 14:22
 */
@NoArgsConstructor
@Data
public class SmcParitipantsStateRep {


    private List<ContentDTO> content;
    private PageableDTO pageable;
    private Integer totalPages;
    private Integer totalElements;
    private Boolean last;
    private Boolean first;
    private SortDTO sort;
    private Integer numberOfElements;
    private Integer size;
    private Integer number;
    private Boolean empty;

    @NoArgsConstructor
    @Data
    public static class PageableDTO {
        private SortDTO sort;
        private Integer pageNumber;
        private Integer pageSize;
        private Integer offset;
        private Boolean unpaged;
        private Boolean paged;

        @NoArgsConstructor
        @Data
        public static class SortDTO {
            private Boolean unsorted;
            private Boolean sorted;
            private Boolean empty;
        }
    }

    @NoArgsConstructor
    @Data
    public static class SortDTO {
        private Boolean unsorted;
        private Boolean sorted;
        private Boolean empty;
    }

    @NoArgsConstructor
    @Data
    public static class ContentDTO implements Comparable<ContentDTO> {
        private GeneralParamDTO generalParam;
        private ParticipantState state;
        private CapabilitySetDTO capabilitySet;
        private Long deptId;
        private Long terminalId;
        private Boolean terminalOnline;
        private String terminalTypeName;
        private Boolean isCascade;
        private Integer terminalType;
        private Integer weight;
        private Integer changeType;
        private String conferenceIdFme;
        private Long cascadeTemplateId;
        private String cascadeMcuTye;
        private String cascadeConferenceId;

        private String attendeeId;

        public Long getCascadeTemplateId() {
            return cascadeTemplateId;
        }

        public void setCascadeTemplateId(Long cascadeTemplateId) {
            this.cascadeTemplateId = cascadeTemplateId;
        }

        public String getCascadeMcuTye() {
            return cascadeMcuTye;
        }

        public void setCascadeMcuTye(String cascadeMcuTye) {
            this.cascadeMcuTye = cascadeMcuTye;
        }

        public GeneralParamDTO getGeneralParam() {
            return generalParam;
        }

        public void setGeneralParam(GeneralParamDTO generalParam) {
            this.generalParam = generalParam;
        }

        public ParticipantState getState() {
            return state;
        }

        public void setState(ParticipantState state) {
            this.state = state;
        }

        public CapabilitySetDTO getCapabilitySet() {
            return capabilitySet;
        }

        public void setCapabilitySet(CapabilitySetDTO capabilitySet) {
            this.capabilitySet = capabilitySet;
        }

        public Long getDeptId() {
            return deptId;
        }

        public void setDeptId(Long deptId) {
            this.deptId = deptId;
        }

        public Long getTerminalId() {
            return terminalId;
        }

        public void setTerminalId(Long terminalId) {
            this.terminalId = terminalId;
        }

        public Boolean getTerminalOnline() {
            return terminalOnline;
        }

        public void setTerminalOnline(Boolean terminalOnline) {
            this.terminalOnline = terminalOnline;
        }

        public String getTerminalTypeName() {
            return terminalTypeName;
        }

        public void setTerminalTypeName(String terminalTypeName) {
            this.terminalTypeName = terminalTypeName;
        }


        public Integer getTerminalType() {
            return terminalType;
        }

        public void setTerminalType(Integer terminalType) {
            this.terminalType = terminalType;
        }

        public Integer getWeight() {
            return weight==null?0:weight;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }

        public String getConferenceIdFme() {
            return conferenceIdFme;
        }

        public void setConferenceIdFme(String conferenceIdFme) {
            this.conferenceIdFme = conferenceIdFme;
        }

        @Override
        public int compareTo(ContentDTO o) {
            if(o.getWeight()==null){
                o.setWeight(0);
            }
            if(this.weight==null){
                this.weight=0;
            }
            return o.weight.compareTo(this.weight);
        }

        @NoArgsConstructor
        @Data
        public static class GeneralParamDTO {
            private String id;
            private String name;
            private String uri;
            private Integer type;
            private Boolean voice;
            private Integer model;
            private String encodeType;
            private Boolean display;
            private Integer participantMediaType;
            private Boolean local;
            private String mcuName;
        }

        @NoArgsConstructor
        @Data
        public static class StateDTO {
            private String participantId;
            private Boolean online;
            private Boolean calling;
            private Boolean dataOnline;
            private Boolean pureData;
            private Boolean voice;
            private Boolean mute;
            private Boolean quiet;
            private Boolean videoMute;
            private Boolean siteVideoMute;
            private Boolean important;
            private Integer videoSwitchAttribute;
            private Integer volume;
            private Integer callFailReason;
            private Boolean captionSet;
            private Boolean handUp;
            private Boolean unObserved;
            private Boolean supportAiSubtitleCapability;
            private Boolean displayCaption;
            private Boolean mcuSwitch;
            private Boolean tp;
        }

        @NoArgsConstructor
        @Data
        public static class CapabilitySetDTO {
            private AudioMediaCapabilityDTO audioMediaCapability;
            private VideoMediaCapabilityDTO videoMediaCapability;

            @NoArgsConstructor
            @Data
            public static class AudioMediaCapabilityDTO {
                private Integer mediaDirection;
                private Integer mediaBandWidth;
                private Integer audioProtocol;
            }

            @NoArgsConstructor
            @Data
            public static class VideoMediaCapabilityDTO {
                private Integer mediaDirection;
                private Integer mediaBandWidth;
                private Integer videoProtocol;
                private VideoResolutionCapDTO videoResolutionCap;

                @NoArgsConstructor
                @Data
                public static class VideoResolutionCapDTO {
                    private Integer videoResolution;
                    private Integer frame;
                }
            }
        }
    }
}

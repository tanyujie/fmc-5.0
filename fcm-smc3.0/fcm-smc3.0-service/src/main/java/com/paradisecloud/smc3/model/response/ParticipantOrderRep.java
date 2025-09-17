package com.paradisecloud.smc3.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/2/28 15:06
 */
@NoArgsConstructor
@Data
public class ParticipantOrderRep {

    private List<ContentDTO> content;
    private PageableDTO pageable;
    private Integer totalPages;
    private Integer totalElements;
    private Boolean last;
    private Boolean first;
    private SortDTO sort;
    private Integer size;
    private Integer number;
    private Integer numberOfElements;
    private Boolean empty;

    @NoArgsConstructor
    @Data
    public static class PageableDTO {
        private SortDTO sort;
        private Integer offset;
        private Integer pageNumber;
        private Integer pageSize;
        private Boolean unpaged;
        private Boolean paged;

        @NoArgsConstructor
        @Data
        public static class SortDTO {
            private Boolean sorted;
            private Boolean unsorted;
            private Boolean empty;
        }
    }

    @NoArgsConstructor
    @Data
    public static class SortDTO {
        private Boolean sorted;
        private Boolean unsorted;
        private Boolean empty;
    }

    @NoArgsConstructor
    @Data
    public static class ContentDTO {
        private GeneralParamDTO generalParam;
        private StateDTO state;
        private CapabilitySetDTO capabilitySet;

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
            private MultiPicInfoDTO multiPicInfo;
            private Integer callFailReason;
            private Boolean captionSet;
            private Boolean handUp;
            private Boolean unObserved;
            private Boolean displayCaption;
            private Boolean supportAiSubtitleCapability;
            private Boolean mcuSwitch;
            private Boolean tp;

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

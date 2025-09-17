package com.paradisecloud.fcm.huaweicloud.huaweicloud.model;

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
        private String ms_open_id;
        private Integer instanceid;
        private String nick_name;
        private String uuid;
        private Boolean host;
        /**
         * 0: 普通成员角色
         * 1: 是创建者权限
         * 2: 主持人权限
         * 3: 创建者权限+主持人权限
         * 4: 游客
         * 5: 游客+主持人权限
         * 6: 联席主持人
         * 7:  创建者+联席主持人
         * 8: restApi接口指派的主持人
         */
        private Integer userRole;
        private String attendeeId;
        private String accountId;
        private String callType;
        private String userAgent;
        private String tel;
        @Override
        public int compareTo(ContentDTO o) {
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

package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author nj
 * @date 2022/8/23 11:16
 */
@NoArgsConstructor
@Data
public class MeetingRoomRep {


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
        private String id;
        private String name;
        private String serviceZoneId;
        private String serviceZoneName;
        private String organizationId;
        private String organizationName;
        private Boolean provisionEua;
        private TerminalParamDTO terminalParam;
        private Boolean backup;

        @NoArgsConstructor
        @Data
        public static class TerminalParamDTO {
            private String id;
            private String terminalType;
            private String middleUri;
            private CapabilityDTO capability;
            private String scRegisterAddress;
            private String loginScName;
            private String loginEuaName;
            private String tr069account;
            private String loginSmcName;
            private String cpeAccount;
            private String rate;
            private String ipProtocolType;
            private String nwZoneType;
            private ActiveCodeDTO activeCode;
            private List<String> serviceList;

            @NoArgsConstructor
            @Data
            public static class CapabilityDTO {
                private String id;
                private String name;
                private List<Integer> h263Capabilities;
                private List<Integer> h264BpCapabilities;
                private List<Integer> h264HpCapabilities;
                private List<Integer> h265Capabilities;
                private Boolean svcCapability;
                private List<String> rate;
                private List<?> ipProtocolType;
                private List<String> businessList;
                private List<String> maintenanceList;
                private List<String> serviceList;
                private Boolean tpCapability;
            }

            @NoArgsConstructor
            @Data
            public static class ActiveCodeDTO {
                private String id;
                private Integer validPeriod;
            }
        }
    }
}

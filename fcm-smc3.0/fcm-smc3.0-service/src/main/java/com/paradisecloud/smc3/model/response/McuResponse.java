package com.paradisecloud.smc3.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/10/24 14:57
 */
@NoArgsConstructor
@Data
public class McuResponse {


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
        private Boolean paged;
        private Boolean unpaged;

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
    public static class ContentDTO {
        private String id;
        private McuDTO mcu;
        private String scIpAddress;
        private SnmpSecurityParamDTO snmpSecurityParam;
        private StatusDTO status;
        private String mcuModel;
        private String arch;
        private String version;
        private String smcAddress;

        @NoArgsConstructor
        @Data
        public static class McuDTO {
            private String id;
            private String name;
            private String domainName;
            private AccountDTO account;
            private String uri;
            private String ipAddress;
            private String backIpAddress;
            private ServiceZoneDTO serviceZone;
            private String mcuType;
            private String createdDate;
            private String reserveStatus;

            @NoArgsConstructor
            @Data
            public static class AccountDTO {
                private String name;
            }

            @NoArgsConstructor
            @Data
            public static class ServiceZoneDTO {
                private String id;
                private String name;
                private String servZoneType;
                private Integer avcParticipantNum;
            }
        }

        @NoArgsConstructor
        @Data
        public static class SnmpSecurityParamDTO {
            private String securityName;
            private String authProto;
            private String privProto;
        }

        @NoArgsConstructor
        @Data
        public static class StatusDTO {
            private Boolean online;
            private Boolean gkState;
            private Boolean sipState;
            private Boolean httpState;
            private Integer alarmState;
            private Integer alarmNums;
        }
    }
}

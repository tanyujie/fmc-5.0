package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/22 10:34
 */
@NoArgsConstructor
@Data
public class MeetingRoomResponse {


    private String id;
    private String name;
    private String serviceZoneId;
    private String serviceZoneName;
    private String organizationId;
    private String organizationName;
    private String areaId;
    private String areaName;
    private Boolean provisionEua;
    private TerminalParamDTO terminalParam;
    private String backupName;
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
            private String code;
            private Integer validPeriod;
        }
    }
}

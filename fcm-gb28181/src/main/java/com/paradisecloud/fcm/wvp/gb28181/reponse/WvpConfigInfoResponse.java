package com.paradisecloud.fcm.wvp.gb28181.reponse;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class WvpConfigInfoResponse extends WvpCommonResponse{


    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private Integer serverPort;
        private SipDTO sip;
        private AddOnDTO addOn;
        private VersionDTO version;

        @NoArgsConstructor
        @Data
        public static class SipDTO {
            private String ip;
            private String showIp;
            private Integer port;
            private String domain;
            private String id;
            private String password;
            private Integer ptzSpeed;
            private Integer registerTimeInterval;
            private Boolean alarm;
        }

        @NoArgsConstructor
        @Data
        public static class AddOnDTO {
            private Boolean savePositionHistory;
            private Boolean autoApplyPlay;
            private Boolean seniorSdp;
            private Integer playTimeout;
            private Integer platformPlayTimeout;
            private Boolean interfaceAuthentication;
            private Boolean recordPushLive;
            private Boolean recordSip;
            private Boolean logInDatabase;
            private Boolean usePushingAsStatus;
            private Boolean useSourceIpAsStreamIp;
            private Boolean sipUseSourceIpAsRemoteAddress;
            private Boolean streamOnDemand;
            private Boolean pushAuthority;
            private Boolean syncChannelOnDeviceOnline;
            private Boolean sipLog;
            private Boolean sqlLog;
            private Boolean sendToPlatformsWhenIdLost;
            private Boolean refuseChannelStatusChannelFormNotify;
            private Boolean deviceStatusNotify;
            private Boolean useCustomSsrcForParentInvite;
            private String serverId;
            private String thirdPartyGBIdReg;
            private String broadcastForPlatform;
            private String civilCodeFile;
            private List<?> interfaceAuthenticationExcludes;
            private List<?> allowedOrigins;
            private Integer maxNotifyCountQueue;
            private Integer registerAgainAfterTime;
            private Boolean registerKeepIntDialog;
        }

        @NoArgsConstructor
        @Data
        public static class VersionDTO {
            private Object artifactId;
            private String version;
            private Object project;
            private Object createBy;
            private Object buildJdk;
            private String gitRevision;
            private String gitBranch;
            private String gitUrl;
            private String buildDate;
            private String gitRevisionShort;
            private String gitDate;
        }
    }
}

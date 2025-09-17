package com.paradisecloud.fcm.wvp.gb28181.reponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WvpPlayStartResponse extends WvpCommonResponse{


    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String app;
        private String stream;
        private Object ip;
        private String flv;
        private String httpsFlv;
        private String wsFlv;
        private String wssFlv;
        private String fmp4;
        private String httpsFmp4;
        private String wsFmp4;
        private String wssFmp4;
        private String hls;
        private String httpsHls;
        private String wsHls;
        private String wssHls;
        private String ts;
        private String httpsTs;
        private String wsTs;
        private Object wssTs;
        private String rtmp;
        private Object rtmps;
        private String rtsp;
        private Object rtsps;
        private String rtc;
        private String rtcs;
        private String mediaServerId;
        private MediaInfoDTO mediaInfo;
        private Object startTime;
        private Object endTime;
        private Object downLoadFilePath;
        private Object transcodeStream;
        private Double progress;

        @NoArgsConstructor
        @Data
        public static class MediaInfoDTO {
            private String app;
            private String stream;
            private MediaServerDTO mediaServer;
            private String schema;
            private Integer readerCount;
            private String videoCodec;
            private Integer width;
            private Integer height;
            private String audioCodec;
            private Integer audioChannels;
            private Integer audioSampleRate;
            private Object duration;
            private Boolean online;
            private Integer originType;
            private Integer aliveSecond;
            private Integer bytesSpeed;
            private String callId;

            @NoArgsConstructor
            @Data
            public static class MediaServerDTO {
                private String id;
                private String ip;
                private String hookIp;
                private String sdpIp;
                private String streamIp;
                private Integer httpPort;
                private Integer httpSSlPort;
                private Integer rtmpPort;
                private Integer flvPort;
                private Integer flvSSLPort;
                private Integer wsFlvPort;
                private Integer wsFlvSSLPort;
                private Integer rtmpSSlPort;
                private Integer rtpProxyPort;
                private Integer rtspPort;
                private Integer rtspSSLPort;
                private Boolean autoConfig;
                private String secret;
                private Double hookAliveInterval;
                private Boolean rtpEnable;
                private Boolean status;
                private String rtpPortRange;
                private String sendRtpPortRange;
                private Integer recordAssistPort;
                private String createTime;
                private String updateTime;
                private Object lastKeepaliveTime;
                private Boolean defaultServer;
                private Integer recordDay;
                private String recordPath;
                private String type;
                private Object transcodeSuffix;
            }
        }
    }
}

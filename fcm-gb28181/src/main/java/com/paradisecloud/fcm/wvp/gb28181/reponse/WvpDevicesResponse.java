package com.paradisecloud.fcm.wvp.gb28181.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class WvpDevicesResponse extends WvpCommonResponse{


    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private Integer total;
        private List<ListDTO> list;
        private Integer pageNum;
        private Integer pageSize;
        private Integer size;
        private Integer startRow;
        private Integer endRow;
        private Integer pages;
        private Integer prePage;
        private Integer nextPage;
        private Boolean isFirstPage;
        private Boolean isLastPage;
        private Boolean hasPreviousPage;
        private Boolean hasNextPage;
        private Integer navigatePages;
        private List<Integer> navigatepageNums;
        private Integer navigateFirstPage;
        private Integer navigateLastPage;

        @NoArgsConstructor
        @Data
        public static class ListDTO {
            private String deviceId;
            private String name;
            private Object manufacturer;
            private Object model;
            private Object firmware;
            private String transport;
            private Object streamMode;
            private String ip;
            private Integer port;
            private String hostAddress;
            private Boolean onLine;
            private String registerTime;
            private String keepaliveTime;
            private Integer keepaliveIntervalTime;
            private Integer channelCount;
            private Integer expires;
            private String createTime;
            private String updateTime;
            private Object mediaServerId;
            private Object charset;
            private Integer subscribeCycleForCatalog;
            private Integer subscribeCycleForMobilePosition;
            private Integer mobilePositionSubmissionInterval;
            private Integer subscribeCycleForAlarm;
            private Boolean ssrcCheck;
            private Object geoCoordSys;
            private String password;
            private Object sdpIp;
            private String localIp;
            private Boolean asMessageChannel;
            private Object sipTransactionInfo;
            private Boolean broadcastPushAfterAck;
            private Integer streamModeForParam;
        }
    }
}

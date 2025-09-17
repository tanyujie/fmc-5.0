package com.paradisecloud.fcm.wvp.gb28181.reponse;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class WvpChannelsResponse extends WvpCommonResponse{


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
            private Integer id;
            private String channelId;
            private String deviceId;
            private String name;
            private String manufacture;
            private String model;
            private String owner;
            private Object civilCode;
            private Object block;
            private String address;
            private Integer parental;
            private String parentId;
            private Integer safetyWay;
            private Integer registerWay;
            private Object certNum;
            private Integer certifiable;
            private Integer errCode;
            private Object endTime;
            private String secrecy;
            private Object ipAddress;
            private Integer port;
            private Object password;
            private Integer ptzType;
            private String ptzTypeText;
            private String createTime;
            private String updateTime;
            private Boolean status;
            private Double longitude;
            private Double latitude;
            private Double customLongitude;
            private Double customLatitude;
            private Double longitudeGcj02;
            private Double latitudeGcj02;
            private Double longitudeWgs84;
            private Double latitudeWgs84;
            private Integer subCount;
            private Object streamId;
            private Boolean hasAudio;
            private Integer channelType;
            private Object businessGroupId;
            private String gpsTime;
            private Object streamIdentification;
        }
    }
}

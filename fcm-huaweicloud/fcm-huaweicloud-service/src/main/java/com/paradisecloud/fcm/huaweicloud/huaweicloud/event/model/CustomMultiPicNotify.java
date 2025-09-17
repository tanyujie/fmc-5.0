package com.paradisecloud.fcm.huaweicloud.huaweicloud.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/3/21 16:58
 */
@NoArgsConstructor
@Data
public class CustomMultiPicNotify {


    private String mode;
    private String portalMode;
    private List<PicInfosDTO> picInfos;
    private List<UserPicInfosDTO> userPicInfos;
    private PicLayoutDTO picLayout;
    private PortalPicLayoutDTO portalPicLayout;
    private Integer picMode;
    private Integer portalPicMode;
    private Integer picNum;
    private Integer portalPicNum;
    private Integer activeIndex;
    private Integer portalActiveIndex;
    private Integer period;
    private Integer portalPeriod;
    private Boolean skipEmptyPic;
    private Boolean portalSkipEmptyPic;
    private Integer vas;
    private Integer isChairViewMultiPic;
    private String confID;
    private String msgID;
    private Integer msgMode;
    private Long version;
    private Long createTime;
    private String action;

    @NoArgsConstructor
    @Data
    public static class PicLayoutDTO {
        private Integer x;
        private Integer y;
        private List<SubPicLayoutInfoListDTO> subPicLayoutInfoList;

        @NoArgsConstructor
        @Data
        public static class SubPicLayoutInfoListDTO {
            private Integer id;
            private Integer left;
            private Integer top;
            private Integer xSize;
            private Integer ySize;
        }
    }

    @NoArgsConstructor
    @Data
    public static class PortalPicLayoutDTO {
        private Integer x;
        private Integer y;
        private List<SubPicLayoutInfoListDTO> subPicLayoutInfoList;

        @NoArgsConstructor
        @Data
        public static class SubPicLayoutInfoListDTO {
            private Integer id;
            private Integer left;
            private Integer top;
            private Integer xSize;
            private Integer ySize;
        }
    }

    @NoArgsConstructor
    @Data
    public static class PicInfosDTO {
        private Integer index;
        private List<String> id;
        private List<PicUserListDTO> picUserList;
        private Integer share;
        private Integer isAutoPollingPic;

        @NoArgsConstructor
        @Data
        public static class PicUserListDTO {
            private String userId;
        }
    }

    @NoArgsConstructor
    @Data
    public static class UserPicInfosDTO {
        private Integer index;
        private List<String> id;
        private List<PicUserListDTO> picUserList;
        private Integer share;
        private Integer isAutoPollingPic;

        @NoArgsConstructor
        @Data
        public static class PicUserListDTO {
            private String userId;
        }
    }
}

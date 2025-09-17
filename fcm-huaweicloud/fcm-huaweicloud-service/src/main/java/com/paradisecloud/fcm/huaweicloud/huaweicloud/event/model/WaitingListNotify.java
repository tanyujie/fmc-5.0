package com.paradisecloud.fcm.huaweicloud.huaweicloud.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/3/20 10:22
 */
@NoArgsConstructor
@Data
public class WaitingListNotify {


    private List<DataDTO> data;
    private String confID;
    private String msgID;
    private Integer msgMode;
    private Integer version;
    private Long createTime;
    private String action;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String pid;
        private Integer mode;
        private Integer delType;
        private PinfoMapDTO pinfoMap;

        @NoArgsConstructor
        @Data
        public static class PinfoMapDTO {
            private String name;
            private String orgId;
            private String accountId;
            private String anonymous;
            private String account;
            private String clientLoginType;
            private String tel;
            private String thirdaccount;
            private String addtime;
        }
    }
}

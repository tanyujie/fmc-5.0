package com.paradisecloud.fcm.huaweicloud.huaweicloud.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/9/28 15:54
 */
@NoArgsConstructor
@Data
public class MultiPicInfoReq {

    private MultiPicInfoDTO multiPicInfo;

    private String conferenceId;

    private Boolean broadcast;

    private Integer interval;

    private Integer switchTime;

    @NoArgsConstructor
    @Data
    public static class MultiPicInfoDTO {
        private Integer picNum;
        private Integer mode;
        private List<SubPicListDTO> subPicList;

        @NoArgsConstructor
        @Data
        public static class SubPicListDTO {
            private List<String> participantIds;
            private Integer streamNumber;
        }
    }

}

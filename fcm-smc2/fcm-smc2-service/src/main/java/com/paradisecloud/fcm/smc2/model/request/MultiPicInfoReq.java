package com.paradisecloud.fcm.smc2.model.request;

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

    private String participantId;

    @NoArgsConstructor
    @Data
    public static class MultiPicInfoDTO {
        private Integer picNum;
        private Integer mode;
        private List<SubPicListDTO> subPicList;

        @NoArgsConstructor
        @Data
        public static class SubPicListDTO {
            private String participantId;
            private Integer streamNumber;
        }
    }

}

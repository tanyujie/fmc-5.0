package com.paradisecloud.fcm.smc2.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/9/19 10:09
 */
@NoArgsConstructor
@Data
public class MultiPicInfoTalkReq {

    private MultiPicInfoReq.MultiPicInfoDTO multiPicInfo;

    private String conferenceId;

    private List<String> participantIds;

    @NoArgsConstructor
    @Data
    public static class MultiPicInfoDTO {
        private Integer picNum;
        private Integer mode;
        private List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList;

        @NoArgsConstructor
        @Data
        public static class SubPicListDTO {
            private String participantId;
            private Integer streamNumber;
        }
    }
}

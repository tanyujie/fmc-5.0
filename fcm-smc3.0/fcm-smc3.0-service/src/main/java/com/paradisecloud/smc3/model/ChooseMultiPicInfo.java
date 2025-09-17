package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/9/23 12:00
 */
@NoArgsConstructor
@Data
public class ChooseMultiPicInfo {


    private MultiPicInfoDTO multiPicInfo;

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

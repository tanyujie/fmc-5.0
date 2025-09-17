package com.paradisecloud.smc3.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/11/15 16:35
 */
@NoArgsConstructor
@Data
public class ConfigIvr {


    private String id;
    private String name;
    private ValueDTO value;

    @NoArgsConstructor
    @Data
    public static class ValueDTO {
        private String unifiedAccessCode;
        private Integer limitTimes;
        private Integer allowedWaitingTime;
        private Integer supportActiveTime;
        private Integer delayHandleDuration;
        private Boolean enablePstn;
        private String pstnMode;
    }
}

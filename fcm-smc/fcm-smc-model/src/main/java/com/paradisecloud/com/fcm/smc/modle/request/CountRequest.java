package com.paradisecloud.com.fcm.smc.modle.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/3/16 16:06
 */
@Data
@NoArgsConstructor
public class CountRequest {
    private String startTime;
    private String endTime;
}

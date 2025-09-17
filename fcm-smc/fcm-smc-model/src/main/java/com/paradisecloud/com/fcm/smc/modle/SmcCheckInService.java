package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/17 10:07
 */
@NoArgsConstructor
@Data
public class SmcCheckInService {

    private Boolean enableCheckIn;
    private Integer checkInDuration;
}

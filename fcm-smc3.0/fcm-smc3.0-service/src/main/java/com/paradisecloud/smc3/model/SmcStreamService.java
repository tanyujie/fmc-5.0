package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/17 10:03
 */
@NoArgsConstructor
@Data
public class SmcStreamService {


    private Boolean autoRecord;
    private Boolean supportMinutes;
    private Boolean supportPushStream;
    private Boolean audioRecord;
    private Boolean amcRecord;
    private Boolean supportRecord;
    private Boolean autoPushStream;
    private Boolean supportLive;
}

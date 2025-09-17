package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/16 15:23
 */
@NoArgsConstructor
@Data
public class ConferencePolicySetting {

    private Boolean autoEnd;
    private String timeZoneId;
    private Boolean autoMute;
    private Integer language;
    private Boolean voiceActive;
    private Boolean autoExtend;
}

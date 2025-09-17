package com.paradisecloud.smc3.model;

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
    private Boolean autoMute;
    private Integer language=1;
    private Boolean voiceActive;
    private Boolean autoExtend;
    private String chairmanPassword;
    private String guestPassword;
    private Boolean displaySubjectAsCallerDisplayInfo=Boolean.TRUE;
    private Boolean releaseParticipantRes=Boolean.TRUE;
    private int maxParticipantNum;
}

package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/9/21 14:56
 */
@NoArgsConstructor
@Data
public class SmcMultiConferenceServiceCreate {

    private String accessCode;
    private ConferenceCapabilitySettingCreate conferenceCapabilitySetting;
    private ConferencePolicySetting conferencePolicySetting;
    private String mainServiceZoneId;
    private String mainServiceZoneName;
    private String mainMcuId;
    private String mainMcuName;

}

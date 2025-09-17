package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/16 15:21
 */
@NoArgsConstructor
@Data
public class SmcMultiConferenceService {


    private String mainServiceZoneId;

    private String mainServiceZoneName;

    private String accessCode;

    private ConferencePolicySetting conferencePolicySetting;

    private String chairmanLink;

    private String guestLink;

    private String mainMcuName;

    private ConferenceCapabilitySetting conferenceCapabilitySetting;



}

package com.paradisecloud.fcm.fme.model.response.dtmfprofile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个 dtmfProfiles
 */
@Getter
@Setter
@ToString
public class DtmfProfilesOfOneResponse
{
    
    /**
     * 单个 callLegProfiles
     */
    private ActiveDtmfProfilesOfOneResponse dtmfProfiles;
}

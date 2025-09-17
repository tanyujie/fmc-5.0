package com.paradisecloud.fcm.fme.model.response.dtmfprofile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 dtmfProfiles 响应类
 */
@Getter
@Setter
@ToString
public class DtmfProfilesResponse
{
    
    /**
     * 多个 dtmfProfiles
     */
    private ActiveDtmfProfilesResponse dtmfProfiles;
    
}

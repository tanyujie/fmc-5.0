package com.paradisecloud.fcm.fme.model.response.compatibilityprofile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 compatibilityProfiles 响应类
 */
@Getter
@Setter
@ToString
public class CompatibilityProfilesResponse
{
    
    /**
     * 多个 callProfiles
     */
    private ActiveCompatibilityProfilesResponse compatibilityProfiles;
    
}

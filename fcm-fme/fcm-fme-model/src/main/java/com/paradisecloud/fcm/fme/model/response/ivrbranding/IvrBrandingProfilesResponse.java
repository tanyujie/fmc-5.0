package com.paradisecloud.fcm.fme.model.response.ivrbranding;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 ivrBrandingProfiles 响应类
 */
@Getter
@Setter
@ToString
public class IvrBrandingProfilesResponse
{
    
    /**
     * 多个 ivrBrandingProfiles
     */
    private ActiveIvrBrandingProfilesResponse ivrBrandingProfiles;
    
}

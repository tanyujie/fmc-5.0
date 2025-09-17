package com.paradisecloud.fcm.fme.model.response.callbranding;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 callProfiles 响应类
 */
@Getter
@Setter
@ToString
public class CallBrandingProfilesResponse
{
    
    /**
     * 多个 callProfiles
     */
    private ActiveCallBrandingProfilesResponse callBrandingProfiles;
    
}

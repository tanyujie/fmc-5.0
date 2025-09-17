package com.paradisecloud.fcm.fme.model.response.dialinsecurity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 dialInSecurityProfiles 响应类
 */
@Getter
@Setter
@ToString
public class DialInSecurityProfilesResponse
{
    
    /**
     * 多个 dialInSecurityProfiles
     */
    private ActiveDialInSecurityProfilesResponse dialInSecurityProfiles;
    
}

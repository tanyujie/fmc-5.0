package com.paradisecloud.fcm.fme.model.response.callprofile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 callProfiles 响应类
 */
@Getter
@Setter
@ToString
public class CallProfilesResponse
{
    
    /**
     * 多个 callProfiles
     */
    private ActiveCallProfilesResponse callProfiles;
    
}

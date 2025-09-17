package com.paradisecloud.fcm.fme.model.response.callprofile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个 callProfiles
 */
@Getter
@Setter
@ToString
public class CallProfilesOfOneResponse
{
    
    /**
     * 单个 callProfiles
     */
    private ActiveCallProfilesOfOneResponse callProfiles;
}

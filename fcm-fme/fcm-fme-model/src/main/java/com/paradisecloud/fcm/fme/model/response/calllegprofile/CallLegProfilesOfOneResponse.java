package com.paradisecloud.fcm.fme.model.response.calllegprofile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个 callLegProfiles
 *
 * @author zt1994 2019/8/19 16:59
 */
@Getter
@Setter
@ToString
public class CallLegProfilesOfOneResponse
{
    
    /**
     * 单个 callLegProfiles
     */
    private ActiveCallLegProfilesOfOneResponse callLegProfiles;
}

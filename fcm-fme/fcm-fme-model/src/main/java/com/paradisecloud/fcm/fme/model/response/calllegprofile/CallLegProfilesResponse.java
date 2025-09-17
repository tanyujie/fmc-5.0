package com.paradisecloud.fcm.fme.model.response.calllegprofile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 callLegProfile 响应类
 *
 * @author zt1994 2019/8/19 11:31
 */
@Getter
@Setter
@ToString
public class CallLegProfilesResponse
{
    
    /**
     * 多个 callLegProfiles
     */
    private ActiveCallLegProfilesResponse callLegProfiles;
    
}

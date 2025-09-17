package com.paradisecloud.fcm.fme.model.response.callbranding;

import java.util.List;

import com.paradisecloud.fcm.fme.model.cms.CallBrandingProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个callProfile
 */
@Getter
@Setter
@ToString
public class ActiveCallBrandingProfilesResponse
{
    
    /**
     * callLegProfile 总数
     */
    private Integer total;
    
    /**
     * 单个callLegProfiles
     */
    private List<CallBrandingProfile> callBrandingProfile;
}

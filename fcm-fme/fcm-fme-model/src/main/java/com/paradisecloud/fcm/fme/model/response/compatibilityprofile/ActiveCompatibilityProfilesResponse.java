package com.paradisecloud.fcm.fme.model.response.compatibilityprofile;

import java.util.List;

import com.paradisecloud.fcm.fme.model.cms.CompatibilityProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个callProfile
 */
@Getter
@Setter
@ToString
public class ActiveCompatibilityProfilesResponse
{
    
    /**
     * callLegProfile 总数
     */
    private Integer total;
    
    /**
     * 单个callLegProfiles
     */
    private List<CompatibilityProfile> compatibilityProfile;
}

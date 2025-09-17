package com.paradisecloud.fcm.fme.model.response.ivrbranding;

import java.util.List;

import com.paradisecloud.fcm.fme.model.cms.IvrBrandingProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个ivrBrandingProfile
 */
@Getter
@Setter
@ToString
public class ActiveIvrBrandingProfilesResponse
{
    
    /**
     * dialInSecurityProfile 总数
     */
    private Integer total;
    
    /**
     * 单个ivrBrandingProfile
     */
    private List<IvrBrandingProfile> ivrBrandingProfile;
}

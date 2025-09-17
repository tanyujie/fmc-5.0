package com.paradisecloud.fcm.fme.model.response.ivrbranding;

import com.paradisecloud.fcm.fme.model.cms.IvrBrandingProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * ivrBrandingProfile 详情响应类
 */
@Getter
@Setter
@ToString
public class IvrBrandingProfileInfoResponse
{
    
    /**
     * 单个 callProfile
     */
    private IvrBrandingProfile ivrBrandingProfile;
}

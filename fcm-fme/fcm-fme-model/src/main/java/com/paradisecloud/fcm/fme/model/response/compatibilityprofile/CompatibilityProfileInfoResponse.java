package com.paradisecloud.fcm.fme.model.response.compatibilityprofile;

import com.paradisecloud.fcm.fme.model.cms.CompatibilityProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * callProfile 详情响应类
 */
@Getter
@Setter
@ToString
public class CompatibilityProfileInfoResponse
{
    
    /**
     * 单个 callProfile
     */
    private CompatibilityProfile compatibilityProfile;
}

package com.paradisecloud.fcm.fme.model.response.callbranding;

import com.paradisecloud.fcm.fme.model.cms.CallBrandingProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * callProfile 详情响应类
 */
@Getter
@Setter
@ToString
public class CallBrandingProfileInfoResponse
{
    
    /**
     * 单个 callProfile
     */
    private CallBrandingProfile callBrandingProfile;
}

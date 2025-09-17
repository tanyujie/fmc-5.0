package com.paradisecloud.fcm.fme.model.response.dialinsecurity;

import com.paradisecloud.fcm.fme.model.cms.DialInSecurityProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * dialInSecurityProfile 详情响应类
 */
@Getter
@Setter
@ToString
public class DialInSecurityProfileInfoResponse
{
    
    /**
     * 单个 callProfile
     */
    private DialInSecurityProfile dialInSecurityProfile;
}

package com.paradisecloud.fcm.fme.model.response.dialinsecurity;

import java.util.List;

import com.paradisecloud.fcm.fme.model.cms.DialInSecurityProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个dialInSecurityProfile
 */
@Getter
@Setter
@ToString
public class ActiveDialInSecurityProfilesResponse
{
    
    /**
     * dialInSecurityProfile 总数
     */
    private Integer total;
    
    /**
     * 单个dialInSecurityProfile
     */
    private List<DialInSecurityProfile> dialInSecurityProfile;
}

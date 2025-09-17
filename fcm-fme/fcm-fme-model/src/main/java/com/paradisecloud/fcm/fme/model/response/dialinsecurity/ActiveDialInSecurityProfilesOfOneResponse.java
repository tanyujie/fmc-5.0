package com.paradisecloud.fcm.fme.model.response.dialinsecurity;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.fme.model.cms.DialInSecurityProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个callProfile
 *
 */
@Getter
@Setter
@ToString
public class ActiveDialInSecurityProfilesOfOneResponse
{
    
    /**
     * dialInSecurityProfile 总数
     */
    @JSONField(name = "@total")
    private Integer total;
    
    /**
     * 单个dialInSecurityProfile
     */
    private DialInSecurityProfile dialInSecurityProfile;
}

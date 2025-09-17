package com.paradisecloud.fcm.fme.model.response.compatibilityprofile;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.fme.model.cms.CompatibilityProfile;

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
public class ActiveCompatibilityProfilesOfOneResponse
{
    
    /**
     * callLegProfile 总数
     */
    @JSONField(name = "@total")
    private Integer total;
    
    /**
     * 单个callLegProfiles
     */
    private CompatibilityProfile compatibilityProfile;
}

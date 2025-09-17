package com.paradisecloud.fcm.fme.model.response.callbranding;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.fme.model.cms.CallBrandingProfile;

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
public class ActiveCallBrandingProfilesOfOneResponse
{
    
    /**
     * callLegProfile 总数
     */
    @JSONField(name = "@total")
    private Integer total;
    
    /**
     * 单个callLegProfiles
     */
    private CallBrandingProfile callBrandingProfile;
}

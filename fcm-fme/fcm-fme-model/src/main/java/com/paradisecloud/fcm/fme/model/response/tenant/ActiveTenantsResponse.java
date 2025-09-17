package com.paradisecloud.fcm.fme.model.response.tenant;

import java.util.List;

import com.paradisecloud.fcm.fme.model.cms.Tenant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个callProfile
 */
@Getter
@Setter
@ToString
public class ActiveTenantsResponse
{
    
    /**
     * callLegProfile 总数
     */
    private Integer total;
    
    /**
     * 单个callLegProfiles
     */
    private List<Tenant> tenants;
}

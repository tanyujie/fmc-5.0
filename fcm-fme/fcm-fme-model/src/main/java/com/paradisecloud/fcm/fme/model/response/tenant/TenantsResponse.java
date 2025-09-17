package com.paradisecloud.fcm.fme.model.response.tenant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 tenants 响应类
 */
@Getter
@Setter
@ToString
public class TenantsResponse
{
    
    /**
     * 多个 tenants
     */
    private ActiveTenantsResponse tenants;
    
}

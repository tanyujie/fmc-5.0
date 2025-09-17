package com.paradisecloud.fcm.fme.model.response.tenant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个 callProfiles
 */
@Getter
@Setter
@ToString
public class TenantsOfOneResponse
{
    
    /**
     * 单个 tenants
     */
    private ActiveTenantsOfOneResponse tenants;
}

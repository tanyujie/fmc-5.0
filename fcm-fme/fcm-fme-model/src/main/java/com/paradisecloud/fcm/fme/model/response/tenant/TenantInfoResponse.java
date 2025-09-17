package com.paradisecloud.fcm.fme.model.response.tenant;

import com.paradisecloud.fcm.fme.model.cms.Tenant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * tenant 详情响应类
 */
@Getter
@Setter
@ToString
public class TenantInfoResponse
{
    
    /**
     * 单个 tenant
     */
    private Tenant tenant;
}

package com.paradisecloud.fcm.fme.model.response.tenant;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.fme.model.cms.Tenant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个tenant
 *
 */
@Getter
@Setter
@ToString
public class ActiveTenantsOfOneResponse
{
    
    /**
     * tenant 总数
     */
    @JSONField(name = "@total")
    private Integer total;
    
    /**
     * 单个tenant
     */
    private Tenant tenant;
}

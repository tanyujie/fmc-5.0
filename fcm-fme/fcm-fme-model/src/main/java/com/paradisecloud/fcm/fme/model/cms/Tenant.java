/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : Tenant.java
 * Package     : com.paradisecloud.fcm.fme.model.cms
 * @author sinhy 
 * @since 2021-08-04 19:15
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.cms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**  
 * <pre>Fme租户</pre>
 * @author sinhy
 * @since 2021-08-04 19:15
 * @version V1.0  
 */
@Getter
@Setter
@ToString
public class Tenant
{
    
    /**
     * 唯一id
     */
    private String id;
    
    /**
     * 租户名
     */
    private String name;
    
    /**
     * 开关麦
     */
    private String callLegProfile;
    
    /**
     * callProfile
     */
    private String callProfile;
    
    /**
     * dtmfProfile
     */
    private String dtmfProfile;
    
    /**
     * ivrBrandingProfile
     */
    private String ivrBrandingProfile;
    
    /**
     * callBrandingProfile
     */
    private String callBrandingProfile;
    
    /**
     * 呼入安全模板
     */
    private String dialInSecurityProfile;
    
    /**
     * webBridgeProfile
     */
    private String webBridgeProfile;
    
    /**
     * userProfile
     */
    private String userProfile;
    
    /**
     * 参会者限制数
     */
    private Integer participantLimit;
}

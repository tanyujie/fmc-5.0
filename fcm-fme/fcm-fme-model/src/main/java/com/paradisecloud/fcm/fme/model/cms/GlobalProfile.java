package com.paradisecloud.fcm.fme.model.cms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 全局配置
 *
 * @author zt1994 2019/8/20 13:21
 */
@Getter
@Setter
@ToString
public class GlobalProfile
{
    
    /**
     * callLeg 配置 id
     */
    private String callLegProfile;
    
    /**
     * call 配置 id
     */
    private String callProfile;
    
    /**
     * dtmf配置 id
     */
    private String dtmfProfile;
    
    /**
     * 用户配置 id
     */
    private String userProfile;
    
    /**
     * ivr branding 配置 id
     */
    private String ivrBrandingProfile;
    
    /**
     * call branding 配置 id
     */
    private String callBrandingProfile;
    
    /**
     * 兼容性配置文件 id
     */
    private String compatibilityProfile;
    
}

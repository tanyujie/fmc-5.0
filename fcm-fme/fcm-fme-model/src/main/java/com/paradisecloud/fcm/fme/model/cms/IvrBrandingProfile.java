package com.paradisecloud.fcm.fme.model.cms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * IvrBranding 配置模板
 *
 * @Author yl
 * @Date 2020/3/25 10:46
 */
@Getter
@Setter
@ToString
public class IvrBrandingProfile
{
    
    /**
     * 唯一id
     */
    private String id;
    
    /**
     * 将从中检索IVR文件的HTTP或HTTPS URL,详细信息见Cisco Meeting Server Customization Guide
     */
    private String resourceLocation;
    
}

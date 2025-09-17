package com.paradisecloud.fcm.fme.model.cms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * callBranding配置模板
 *
 * @author zt1994 2019/8/20 13:52
 */
@Getter
@Setter
@ToString
public class CallBrandingProfile
{
    
    /**
     * 唯一id
     */
    private String id;
    
    /**
     * 在构建文本邀请时，会议应用程序将使用邀请模板文本的 HTTP 或 HTTPS URL
     */
    private String invitationTemplate;
    
    /**
     * call Bridge 调用 branding 文件将从HTTP或HTTPS URL中检索。这是单独的音频和图形文件所在的目录。
     */
    private String resourceLocation;
    
}

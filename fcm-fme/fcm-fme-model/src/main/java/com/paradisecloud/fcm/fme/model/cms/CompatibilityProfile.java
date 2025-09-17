/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CompatibilityProfile.java
 * Package     : com.paradisecloud.fcm.fme.model.cms
 * @author sinhy 
 * @since 2021-07-26 17:16
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.cms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**  
 * <pre>请加上该类的描述</pre>
 * @author sinhy
 * @since 2021-07-26 17:16
 * @version V1.0  
 */
@Getter
@Setter
@ToString
public class CompatibilityProfile
{
    
    /**
     * 唯一id
     */
    private String id;

    /**
     * UDT控制       默认: true 是   值: unset|ture /false
     */
    private Boolean sipUdt;
    
    /**
     * 多流   默认: true 是   值: unset|ture /false
     */
    private Boolean sipMultistream;
    
    /**
     * 媒体字节类型 默认:  unset 是   值: unset|auto自动 /broadsoft
     */
    private String sipMediaPayloadTypeMode;
    
    /**
     * h264CHP模式 默认:  unset 是   值: unset|auto 自动/basic
     */
    private String h264CHPMode;
    
    /**
     * ChromeWebrtc视频编码 默认:  unset 是   值: unset|auto自动 /avoidH264
     */
    private String chromeWebRtcVideoCodec;
    
    /**
     * ChromeWebRtc兼容模式 默认:  unset 是   值: unset|auto自动 /None 空
     */
    private String chromeWebRtcH264interopMode;
    
    /**
     * SafariWebRtc兼容模式   默认:  unset 是   值: unset|auto自动 /None 空
     */
    private String safariWebRtcH264interopMode;
    
    /**
     * 穿越模式 默认:  unset 是   值: unset|enabled 启用 /disabled 不启用
     */
    private String passthroughMode;
    
    /**
     * H224 默认: true 是   值: unset|ture /false
     */
    private Boolean sipH224;
    
    /**
     * 默认:  unset 是   值: unset|enabled 启用 /disabled 不启用
     */
    private String distributionLinkMediaTraversal;

}

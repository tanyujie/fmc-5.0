package com.paradisecloud.fcm.fme.model.cms.callleg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call leg 状态 活动控制
 *
 * @author zt1994 2019/8/26 14:05
 */
@Getter
@Setter
@ToString
public class CallLegStatusActiveControl
{
    
    /**
     * 是否加密
     */
    private Boolean encrypted;
    
    /**
     * 本地订阅
     */
    private CallLegStatusActiveControlLocal localSubscriptions;
}

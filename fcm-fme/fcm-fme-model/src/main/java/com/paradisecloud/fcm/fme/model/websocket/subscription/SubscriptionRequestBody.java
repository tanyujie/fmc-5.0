package com.paradisecloud.fcm.fme.model.websocket.subscription;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 订阅请求参数
 *
 * @author zt1994 2019/8/30 15:46
 */
@Getter
@Setter
@ToString
public class SubscriptionRequestBody implements Serializable
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * 
     * @since 2020-12-11 18:01
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 类型
     */
    private String type;
    
    /**
     * message 信息
     */
    private SubscriptionRequestMessage message;
}

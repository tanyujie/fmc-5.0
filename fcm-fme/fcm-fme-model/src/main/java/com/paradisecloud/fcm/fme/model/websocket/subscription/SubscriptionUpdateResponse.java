package com.paradisecloud.fcm.fme.model.websocket.subscription;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * webSocket 订阅更新响应参数
 *
 * @author zt1994 2019/8/30 14:42
 */
@Getter
@Setter
@ToString
public class SubscriptionUpdateResponse
{
    
    /**
     * 类型
     */
    private String type;
    
    /**
     * update 响应
     */
    private SubscriptionUpdate message;
    
}

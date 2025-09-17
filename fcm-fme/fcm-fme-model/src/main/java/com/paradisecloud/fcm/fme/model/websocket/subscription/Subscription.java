package com.paradisecloud.fcm.fme.model.websocket.subscription;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 订阅类
 *
 * @author zt1994 2019/8/30 15:01
 */
@Getter
@Setter
@ToString
public class Subscription
{
    
    /**
     * 订阅索引
     */
    private Integer index;
    
    /**
     * 状态
     */
    private String state;
}

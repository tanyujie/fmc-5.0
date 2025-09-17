package com.paradisecloud.fcm.fme.model.websocket.subscription;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 订阅更新信息
 *
 * @author zt1994 2019/8/30 14:44
 */
@Getter
@Setter
@ToString
public class SubscriptionUpdate
{
    
    /**
     * 消息id
     */
    private Integer messageId;
    
    /**
     * 消息类型
     */
    private String type;
    
    /**
     * 订阅列表信息
     */
    private ArrayList<Subscription> subscriptions;
    
}

/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : MessageType.java
 * Package : com.paradisecloud.common.response.websocket
 * 
 * @author lilinhai
 * 
 * @since 2020-12-10 13:59
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.model.websocket.enumer;

/**
 * <pre>消息类型</pre>
 * 
 * @author lilinhai
 * @since 2020-12-10 13:59
 * @version V1.0
 */
public enum MessageType
{
    
    /**
     * 订阅请求
     */
    SUBSCRIBE_REQUEST("subscribeRequest", "订阅请求"),
    
    /**
     * 订阅更新
     */
    SUBSCRIPTION_UPDATE("subscriptionUpdate", "订阅更新"),
    
    /**
     * 会议列表更新
     */
    CALL_LIST_UPDATE("callListUpdate", "会议列表更新"),
    
    /**
     * 会议室详情更新
     */
    CALL_INFO_UPDATE("callInfoUpdate", "会议室详情更新"),
    
    /**
     * 与会者更新
     */
    ROSTER_UPDATE("rosterUpdate", "与会者更新");
    
    /**
     * 消息值
     */
    private String value;
    
    /**
     * 消息名
     */
    private String name;
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author lilinhai
     * @since 2020-12-10 14:04
     * @param value
     * @param name
     */
    private MessageType(String value, String name)
    {
        this.value = value;
        this.name = name;
    }
    
    /**
     * <p>
     * Get Method : value String
     * </p>
     * 
     * @return value
     */
    public String getValue()
    {
        return value;
    }
    
    /**
     * <p>
     * Get Method : name String
     * </p>
     * 
     * @return name
     */
    public String getName()
    {
        return name;
    }
    
}

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
 * <pre>订阅类型</pre>
 * 
 * @author lilinhai
 * @since 2020-12-10 13:59
 * @version V1.0
 */
public enum SubscriptionType
{
    
    /**
     * 活跃会议室
     */
    CALLS("calls", "活跃会议室"),
    
    /**
     * 会议室详情
     */
    CALL_INFO("callInfo", "会议室详情"),
    
    /**
     * 与会者
     */
    CALL_ROSTER("callRoster", "与会者");
    
    /**
     * 类型值
     */
    private String value;
    
    /**
     * 类型名
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
    private SubscriptionType(String value, String name)
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

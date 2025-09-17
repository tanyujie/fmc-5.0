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
 * <pre>请求类型</pre>
 * 
 * @author lilinhai
 * @since 2020-12-10 13:59
 * @version V1.0
 */
public enum RequestType
{
    
    /**
     * 消息
     */
    MESSAGE("message", "消息"),
    
    /**
     * 消息确认
     */
    MESSAGE_ACK("messageAck", "消息确认");
    
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
    private RequestType(String value, String name)
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

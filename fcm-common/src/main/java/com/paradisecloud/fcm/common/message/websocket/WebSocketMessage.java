/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebsocketMessage.java
 * Package     : com.paradisecloud.fcm.fme.cache
 * @author lilinhai 
 * @since 2021-02-03 17:45
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.common.message.websocket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**  
 * <pre>websocket推送前端的消息</pre>
 * @author lilinhai
 * @since 2021-02-03 17:45
 * @version V1.0  
 */
@JsonIgnoreProperties({ "destination"})
public class WebSocketMessage
{
    
    /**
     * 消息类型，默认为与会者消息
     */
    private String type;
    
    /**
     * 消息内容
     */
    private Object data;
    
    /**
     * 当前时间戳
     */
    private long timestamp = System.currentTimeMillis();
    
    /**
     * 目的地
     */
    private String destination;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 16:38  
     */
    public WebSocketMessage()
    {
    }
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 16:38 
     * @param type
     * @param data 
     */
    public WebSocketMessage(String type, Object data)
    {
        this.type = type;
        this.data = data;
    }
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 17:17 
     * @param type
     * @param data
     * @param destination 
     */
    public WebSocketMessage(String type, Object data, String destination)
    {
        super();
        this.type = type;
        this.data = data;
        this.destination = destination;
    }

    /**
     * <p>Get Method   :   type String</p>
     * @return type
     */
    public String getType()
    {
        return type;
    }

    /**
     * <p>Set Method   :   type String</p>
     * @param type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * <p>Get Method   :   timestamp long</p>
     * @return timestamp
     */
    public long getTimestamp()
    {
        return timestamp;
    }

    /**
     * <p>Set Method   :   timestamp long</p>
     * @param timestamp
     */
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    /**
     * <p>Get Method   :   data Object</p>
     * @return data
     */
    public Object getData()
    {
        return data;
    }

    /**
     * <p>Set Method   :   data Object</p>
     * @param data
     */
    public void setData(Object data)
    {
        this.data = data;
    }
    
    /**
     * <p>Get Method   :   destination String</p>
     * @return destination
     */
    public String getDestination()
    {
        return destination;
    }

    /**
     * <p>Set Method   :   destination String</p>
     * @param destination
     */
    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    @Override
    public String toString()
    {
        return "WebSocketMessage [type=" + type + ", data=" + data + ", timestamp=" + timestamp + ", destination=" + destination + "]";
    }
}

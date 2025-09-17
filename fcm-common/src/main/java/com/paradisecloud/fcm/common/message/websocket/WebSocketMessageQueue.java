/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebsocketMessageQueue.java
 * Package     : com.paradisecloud.fcm.common.model
 * @author lilinhai 
 * @since 2021-02-04 16:33
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.common.message.websocket;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.LoggerFactory;

/**  
 * <pre>websocket消息队列</pre>
 * @author lilinhai
 * @since 2021-02-04 16:33
 * @version V1.0  
 */
public class WebSocketMessageQueue extends LinkedBlockingQueue<WebSocketMessage>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-04 16:34 
     */
    private static final long serialVersionUID = 1L;
    
    private static final WebSocketMessageQueue INSTANCE = new WebSocketMessageQueue();
    
    
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 17:11 
     * @param capacity 
     */
    private WebSocketMessageQueue()
    {
        super(100000);
    }
    
    public void put(WebSocketMessage e) 
    {
        try
        {
            super.put(e);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(WebSocketMessage e)  error", e2);
        }
    }

    public static WebSocketMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

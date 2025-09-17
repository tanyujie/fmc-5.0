/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebsocketMessageQueue.java
 * Package     : com.paradisecloud.fcm.common.model
 * @author lilinhai 
 * @since 2021-02-04 16:33
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.core;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.LoggerFactory;

import com.paradisecloud.fcm.fme.cache.model.ParticipantInfo;

/**  
 * <pre>websocket消息队列</pre>
 * @author lilinhai
 * @since 2021-02-04 16:33
 * @version V1.0  
 */
public class ParticipantMessageQueue extends LinkedBlockingQueue<ParticipantInfo>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-04 16:34 
     */
    private static final long serialVersionUID = 1L;
    
    private static final ParticipantMessageQueue INSTANCE = new ParticipantMessageQueue();
    
    
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 17:11 
     * @param capacity 
     */
    private ParticipantMessageQueue()
    {
        super(1000000);
    }
    
    public void put(ParticipantInfo e) 
    {
        try
        {
            super.put(e);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(Participant e)  error", e2);
        }
    }

    public static ParticipantMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

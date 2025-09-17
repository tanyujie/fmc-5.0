/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterUpdateMessageQueue.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.core
 * @author sinhy 
 * @since 2021-09-07 21:49
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.model.callroster;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.LoggerFactory;

import com.paradisecloud.fcm.fme.websocket.model.callroster.message.RosterProcessorMessage;

public class RosterProcessorMessageQueue extends LinkedBlockingQueue<RosterProcessorMessage>
{
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-04 16:34 
     */
    private static final long serialVersionUID = 1L;
    
    private static final RosterProcessorMessageQueue INSTANCE = new RosterProcessorMessageQueue();
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 17:11 
     * @param capacity 
     */
    private RosterProcessorMessageQueue()
    {
        super(100000);
    }
    
    public void put(RosterProcessorMessage e) 
    {
        try
        {
            super.put(e);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(RosterProcessorMessage e) error", e2);
        }
    }

    public static RosterProcessorMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

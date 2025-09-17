/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterUpdateMessageQueue.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.core
 * @author sinhy 
 * @since 2021-09-07 21:49
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.websocket.processormessage;

import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

public class Smc3RealTimeInfoProcessorMessageQueue extends LinkedBlockingQueue<Smc3RealTimeInfoProcessorMessage>
{
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-04 16:34 
     */
    private static final long serialVersionUID = 1L;
    
    private static final Smc3RealTimeInfoProcessorMessageQueue INSTANCE = new Smc3RealTimeInfoProcessorMessageQueue();
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 17:11 
     */
    private Smc3RealTimeInfoProcessorMessageQueue()
    {
        super(100000);
    }
    
    @Override
    public void put(Smc3RealTimeInfoProcessorMessage e)
    {
        try
        {
            super.put(e);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(Smc3RealTimeInfoProcessorMessage e) error", e2);
        }
    }

    public static Smc3RealTimeInfoProcessorMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

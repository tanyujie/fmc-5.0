/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalEventMessageQueue.java
 * Package     : com.paradisecloud.fcm.common.model.terminal
 * @author lilinhai 
 * @since 2021-03-02 13:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.message;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.LoggerFactory;

/**  
 * <pre>会议上下文消息阻塞队列</pre>
 * @author lilinhai
 * @since 2021-03-02 13:06
 * @version V1.0  
 */
public class ConferenceContextMessageQueue extends LinkedBlockingQueue<ConferenceContextMessage>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-04 16:34 
     */
    private static final long serialVersionUID = 1L;
    
    private static final ConferenceContextMessageQueue INSTANCE = new ConferenceContextMessageQueue();
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 17:11 
     * @param capacity 
     */
    private ConferenceContextMessageQueue()
    {
        super(1000);
    }
    
    public void put(ConferenceContextMessage e) 
    {
        try
        {
            super.put(e);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(AttendeeMessage e)  error", e2);
        }
    }

    public static ConferenceContextMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

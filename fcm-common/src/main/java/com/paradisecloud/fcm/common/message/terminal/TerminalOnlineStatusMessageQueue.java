/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalEventMessageQueue.java
 * Package     : com.paradisecloud.fcm.common.model.terminal
 * @author lilinhai 
 * @since 2021-03-02 13:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.common.message.terminal;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.LoggerFactory;

/**  
 * <pre>终端事件消息阻塞队列</pre>
 * @author lilinhai
 * @since 2021-03-02 13:06
 * @version V1.0  
 */
public class TerminalOnlineStatusMessageQueue extends LinkedBlockingQueue<TerminalOnlineStatusMessage>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-04 16:34 
     */
    private static final long serialVersionUID = 1L;
    
    private static final TerminalOnlineStatusMessageQueue INSTANCE = new TerminalOnlineStatusMessageQueue();
    
    
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 17:11 
     * @param capacity 
     */
    private TerminalOnlineStatusMessageQueue()
    {
        super(1000);
    }
    
    public void put(TerminalOnlineStatusMessage e) 
    {
        try
        {
            super.put(e);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(TerminalEventMessage e)  error", e2);
        }
    }

    public static TerminalOnlineStatusMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

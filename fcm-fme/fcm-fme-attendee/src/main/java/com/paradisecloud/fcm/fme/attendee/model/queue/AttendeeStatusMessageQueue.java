/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeStatusMessageQueue.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.message
 * @author sinhy 
 * @since 2021-12-27 11:37
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.queue;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.LoggerFactory;

import com.paradisecloud.fcm.fme.model.busi.message.AttendeeStatusMessage;

/**  
 * 参会状态消息队列
 * @author sinhy
 * @since 2021-12-27 11:37
 * @version V1.0  
 */
public class AttendeeStatusMessageQueue extends LinkedBlockingQueue<AttendeeStatusMessage>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-12-27 11:37 
     */
    private static final long serialVersionUID = 1L;
    
    private static final AttendeeStatusMessageQueue INSTANCE = new AttendeeStatusMessageQueue();
    
    
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 17:11 
     * @param capacity 
     */
    private AttendeeStatusMessageQueue()
    {
        super(100000);
    }
    
    public void put(AttendeeStatusMessage e) 
    {
        try
        {
            super.put(e);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(AttendeeStatusMessage e)  error", e2);
        }
    }

    public static AttendeeStatusMessageQueue getInstance()
    {
        return INSTANCE;
    }
    
}

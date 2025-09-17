package com.paradisecloud.fcm.fme.conference.model.queue;

import com.paradisecloud.fcm.fme.model.busi.message.AttendeeStatusMessage;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author nj
 * @date 2022/12/19 10:44
 */
public class LayOutStatusMessageQueue extends LinkedBlockingQueue<AttendeeStatusMessage>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-12-27 11:37
     */
    private static final long serialVersionUID = 1L;

    private static final LayOutStatusMessageQueue INSTANCE = new LayOutStatusMessageQueue();



    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-02-04 17:11
     * @param capacity
     */
    private LayOutStatusMessageQueue()
    {
        super(50000);
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

    public static LayOutStatusMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

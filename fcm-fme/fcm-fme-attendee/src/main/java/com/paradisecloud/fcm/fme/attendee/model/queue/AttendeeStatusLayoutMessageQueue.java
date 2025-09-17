package com.paradisecloud.fcm.fme.attendee.model.queue;

import com.paradisecloud.fcm.fme.model.busi.message.AttendeeStatusMessage;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author nj
 * @date 2023/1/12 13:36
 */
public class AttendeeStatusLayoutMessageQueue extends LinkedBlockingQueue<AttendeeStatusMessage>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-12-27 11:37
     */
    private static final long serialVersionUID = 1L;

    private static final AttendeeStatusLayoutMessageQueue INSTANCE = new AttendeeStatusLayoutMessageQueue();



    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-02-04 17:11
     * @param capacity
     */
    private AttendeeStatusLayoutMessageQueue()
    {
        super(100000);
    }

    @Override
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

    public static AttendeeStatusLayoutMessageQueue getInstance()
    {
        return INSTANCE;
    }

}

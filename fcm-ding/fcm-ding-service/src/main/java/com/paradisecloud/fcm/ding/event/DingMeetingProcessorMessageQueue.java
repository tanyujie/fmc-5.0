package com.paradisecloud.fcm.ding.event;

import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author nj
 * @date 2023/4/26 10:50
 */
public class DingMeetingProcessorMessageQueue extends LinkedBlockingQueue<DingMeetingMessage> {
    private static final long serialVersionUID = 1L;

    private static final DingMeetingProcessorMessageQueue INSTANCE = new DingMeetingProcessorMessageQueue();

    private DingMeetingProcessorMessageQueue()
    {
        super(1000);
    }

    @Override
    public void put(DingMeetingMessage e)
    {
        try
        {
            super.put(e);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(DingMeetingMessage e)  error", e2);
        }
    }

    public static DingMeetingProcessorMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

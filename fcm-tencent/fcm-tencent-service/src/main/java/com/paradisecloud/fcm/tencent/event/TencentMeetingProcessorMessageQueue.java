package com.paradisecloud.fcm.tencent.event;

import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author nj
 * @date 2023/4/26 10:50
 */
public class TencentMeetingProcessorMessageQueue extends LinkedBlockingQueue<TencentMeetingMessage> {
    private static final long serialVersionUID = 1L;

    private static final TencentMeetingProcessorMessageQueue INSTANCE = new TencentMeetingProcessorMessageQueue();

    private TencentMeetingProcessorMessageQueue()
    {
        super(1000);
    }

    @Override
    public void put(TencentMeetingMessage e)
    {
        try
        {
            super.put(e);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(TencentMeetingMessage e)  error", e2);
        }
    }

    public static TencentMeetingProcessorMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

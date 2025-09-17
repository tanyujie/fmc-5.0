package com.paradisecloud.fcm.smc2.model.notice;

import com.suntek.smc.esdk.pojo.local.OngoingConfNotificationEx;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author nj
 * @date 2023/4/26 10:50
 */
public class OngoingConfNotificationProcessorMessageQueue extends LinkedBlockingQueue<OngoingConfNotificationExMessage> {
    private static final long serialVersionUID = 1L;

    private static final OngoingConfNotificationProcessorMessageQueue INSTANCE = new OngoingConfNotificationProcessorMessageQueue();

    private OngoingConfNotificationProcessorMessageQueue()
    {
        super(1000);
    }

    @Override
    public void put(OngoingConfNotificationExMessage e)
    {
        try
        {
            super.put(e);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(OngoingConfNotificationEx e)  error", e2);
        }
    }

    public static OngoingConfNotificationProcessorMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

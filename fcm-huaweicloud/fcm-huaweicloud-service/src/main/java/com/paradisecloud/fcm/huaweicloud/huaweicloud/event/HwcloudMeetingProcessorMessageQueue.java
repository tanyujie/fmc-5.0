package com.paradisecloud.fcm.huaweicloud.huaweicloud.event;

import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author nj
 * @date 2023/4/26 10:50
 */
public class HwcloudMeetingProcessorMessageQueue extends LinkedBlockingQueue<HwcloudMeetingMessage> {
    private static final long serialVersionUID = 1L;

    private static final HwcloudMeetingProcessorMessageQueue INSTANCE = new HwcloudMeetingProcessorMessageQueue();

    private HwcloudMeetingProcessorMessageQueue()
    {
        super(1000);
    }

    @Override
    public void put(HwcloudMeetingMessage e)
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

    public static HwcloudMeetingProcessorMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

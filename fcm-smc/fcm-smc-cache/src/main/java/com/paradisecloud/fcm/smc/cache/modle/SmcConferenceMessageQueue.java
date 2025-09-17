package com.paradisecloud.fcm.smc.cache.modle;

import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author nj
 * @date 2023/3/21 11:23
 */
public class SmcConferenceMessageQueue extends LinkedBlockingQueue<SmcConferenceMessage> {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-04 16:34
     */
    private static final long serialVersionUID = 1L;

    private static final SmcConferenceMessageQueue INSTANCE = new SmcConferenceMessageQueue();



    /**
     * <pre>构造方法</pre>
     * @since 2021-02-04 17:11
     */
    private SmcConferenceMessageQueue()
    {
        super(100000);
    }

    @Override
    public void put(SmcConferenceMessage message)
    {
        try
        {
            super.put(message);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(SmcWebSocketMessage e)  error", e2);
        }
    }

    public static SmcConferenceMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

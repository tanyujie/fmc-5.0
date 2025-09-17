package com.paradisecloud.smc3.websocket.processormessage;

import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author nj
 * @date 2023/5/15 15:36
 */
public class Smc3MessageInfoProcessorMessageQueue extends LinkedBlockingQueue<Smc3MessageInfoProcessorMessage>
{
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-04 16:34
     */
    private static final long serialVersionUID = 1L;

    private static final Smc3MessageInfoProcessorMessageQueue INSTANCE = new Smc3MessageInfoProcessorMessageQueue();

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-02-04 17:11
     */
    private Smc3MessageInfoProcessorMessageQueue()
    {
        super(100000);
    }

    @Override
    public void put(Smc3MessageInfoProcessorMessage e)
    {
        try
        {
            super.put(e);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(Smc3MessageInfoProcessorMessage e) error", e2);
        }
    }

    public static Smc3MessageInfoProcessorMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

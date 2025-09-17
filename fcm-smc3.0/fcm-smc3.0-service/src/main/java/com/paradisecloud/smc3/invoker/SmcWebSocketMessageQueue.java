package com.paradisecloud.smc3.invoker;

import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author nj
 * @date 2023/3/20 10:38
 */
public class SmcWebSocketMessageQueue  extends LinkedBlockingQueue<SmcWebsocketMessage>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-04 16:34
     */
    private static final long serialVersionUID = 1L;

    private static final SmcWebSocketMessageQueue INSTANCE = new SmcWebSocketMessageQueue();



    /**
     * <pre>构造方法</pre>
     * @since 2021-02-04 17:11
     */
    private SmcWebSocketMessageQueue()
    {
        super(100000);
    }

    @Override
    public void put(SmcWebsocketMessage e)
    {
        try
        {
            super.put(e);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(SmcWebSocketMessage e)  error", e2);
        }
    }

    public static SmcWebSocketMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

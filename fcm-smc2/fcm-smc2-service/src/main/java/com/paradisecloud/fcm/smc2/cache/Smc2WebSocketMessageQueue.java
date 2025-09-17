package com.paradisecloud.fcm.smc2.cache;

import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author nj
 * @date 2023/3/20 10:38
 */
public class Smc2WebSocketMessageQueue extends LinkedBlockingQueue<Smc2WebsocketMessage>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-04 16:34
     */
    private static final long serialVersionUID = 1L;

    private static final Smc2WebSocketMessageQueue INSTANCE = new Smc2WebSocketMessageQueue();



    /**
     * <pre>构造方法</pre>
     * @since 2021-02-04 17:11
     */
    private Smc2WebSocketMessageQueue()
    {
        super(100000);
    }

    @Override
    public void put(Smc2WebsocketMessage e)
    {
        try
        {
            super.put(e);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(Smc2WebSocketMessage e)  error", e2);
        }
    }

    public static Smc2WebSocketMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

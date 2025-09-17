package com.paradisecloud.fcm.tencent.cache;

import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author nj
 * @date 2023/3/20 10:38
 */
public class TencentWebSocketMessageQueue extends LinkedBlockingQueue<TencentWebsocketMessage>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-04 16:34
     */
    private static final long serialVersionUID = 1L;

    private static final TencentWebSocketMessageQueue INSTANCE = new TencentWebSocketMessageQueue();



    /**
     * <pre>构造方法</pre>
     * @since 2021-02-04 17:11
     */
    private TencentWebSocketMessageQueue()
    {
        super(100000);
    }

    @Override
    public void put(TencentWebsocketMessage e)
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

    public static TencentWebSocketMessageQueue getInstance()
    {
        return INSTANCE;
    }
}

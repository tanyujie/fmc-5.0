package com.paradisecloud.fcm.smc.cache.modle;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author nj
 * @date 2023/6/21 11:50
 */
public class SmcUpdateParticipantQueue extends LinkedBlockingQueue<JSONObject> {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-04 16:34
     */
    private static final long serialVersionUID = 1L;

    private static final SmcUpdateParticipantQueue INSTANCE = new SmcUpdateParticipantQueue();



    /**
     * <pre>构造方法</pre>
     * @since 2021-02-04 17:11
     */
    private SmcUpdateParticipantQueue()
    {
        super(100000);
    }

    @Override
    public void put(JSONObject message)
    {
        try
        {
            super.put(message);
        }
        catch (Throwable e2)
        {
            LoggerFactory.getLogger(getClass()).error("put(jsonObject e)  error", e2);
        }
    }

    public static SmcUpdateParticipantQueue getInstance()
    {
        return INSTANCE;
    }
}

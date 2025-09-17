package com.paradisecloud.fcm.terminal.fs.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiLive;

public class LiveCache extends JavaCache<Long, BusiLive> {
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-01-28 13:36
     */
    private static final long serialVersionUID = 1L;
    private static final LiveCache INSTANCE = new LiveCache();

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-01-22 18:07
     */
    private LiveCache()
    {
    }

    public static LiveCache getInstance()
    {
        return INSTANCE;
    }
}

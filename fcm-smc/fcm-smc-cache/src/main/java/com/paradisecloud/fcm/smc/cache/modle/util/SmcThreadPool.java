package com.paradisecloud.fcm.smc.cache.modle.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author nj
 * @date 2023/3/3 18:02
 */
public class SmcThreadPool {

    private static final ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(2000, new ThreadFactory()
    {
        private int index;
        public Thread newThread(Runnable r)
        {
            index++;
            return new Thread(r, "SmcThreadPool[" + 2000 + "]-Thread-" + index);
        }
    });

    /**
     * 运行一个任务
     * @author lilinhai
     * @since 2021-03-12 18:07
     * @param run void
     */
    public static void exec(Runnable run)
    {
        FIXED_THREAD_POOL.execute(run);
    }

    /**
     * <p>Get Method   :   FIXED_THREAD_POOL ExecutorService</p>
     * @return fixedThreadPool
     */
    public static ExecutorService getFixedThreadPool()
    {
        return FIXED_THREAD_POOL;
    }
}

package com.paradisecloud.fcm.smc2.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author nj
 * @date 2023/4/26 11:15
 */
public class Smc2ThreadPool {

    private static final ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(50, new ThreadFactory()
    {
        private int index;
        public Thread newThread(Runnable r)
        {
            index++;
            return new Thread(r, "Smc2ThreadPool[" + 50 + "]-Thread-" + index);
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

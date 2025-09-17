/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FcmThreadPool.java
 * Package     : com.paradisecloud.fcm.common.utils
 * @author lilinhai 
 * @since 2021-03-12 18:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**  
 * <pre>Fcm系统线程池，最大1000并发</pre>
 * @author lilinhai
 * @since 2021-03-12 18:06
 * @version V1.0  
 */
public class FcmThreadPool
{
    
    private static final ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(2000, new ThreadFactory()
    {
        private int index;
        public Thread newThread(Runnable r)
        {
            index++;
            return new Thread(r, "FcmThreadPool[" + 2000 + "]-Thread-" + index);
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

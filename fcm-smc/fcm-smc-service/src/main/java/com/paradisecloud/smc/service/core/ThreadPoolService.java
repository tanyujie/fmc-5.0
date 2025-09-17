package com.paradisecloud.smc.service.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;


public class ThreadPoolService {

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("SMCSERVICE-POOL-%d").build();

    private static ExecutorService service = new ThreadPoolExecutor(
            4,
            10,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(4),
            namedThreadFactory,
            new ThreadPoolExecutor.AbortPolicy()
    );

    /**
     * 获取线程池
     *
     * @return 线程池
     */
    public static ExecutorService getEs() {
        return service;
    }

    /**
     * 使用线程池创建线程并异步执行任务
     *
     * @param r 任务
     */
    public static void newTask(Runnable r) {
        service.execute(r);
    }

    public static void shutdown() {
        service.shutdown();
    }
}

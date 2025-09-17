/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : AsyncMessageProcessor.java
 * Package : com.paradisecloud.fcm.sync.core.thread
 * 
 * @author lilinhai
 * 
 * @since 2020-12-18 11:02
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.common.async;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

/**
 * <pre>异步消息处理器</pre>
 * 
 * @author lilinhai
 * @since 2020-12-18 11:02
 * @version V1.0
 */
public abstract class AsyncMessageProcessor<T> extends Thread
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 异常断开的会议桥队列
     */
    protected final Queue<T> queue = new ConcurrentLinkedQueue<>();
    
    /**
     * 进入等待状态的消息提示
     */
    protected String waitMessage;
    
    /**
     * 进入工作状态的消息提示
     */
    protected String workMessage;
    
    /**
     * 每次执行完一次process，休息的毫秒数，为0，表示不休息
     */
    protected long sleepMillisecondsPerProcess;
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author lilinhai
     * @since 2020-12-18 11:04
     * @param name
     */
    protected AsyncMessageProcessor(String name)
    {
        this.setName(name);
    }
    
    @Override
    public void run()
    {
        logger.info("AsyncMessageProcessor[{}] start successfully!", getName());
        while (true)
        {
            try
            {
                // 判断看是否需要将本线程设置成wait状态
                setWait();
                
                // 处理业务
                process();
            }
            catch (Throwable e)
            {
                logger.error("AsyncMessageProcessor[" + getName() + "] run error-1", e);
            }
            finally
            {
                if (!queue.isEmpty())
                {
                    sleep0(sleepMillisecondsPerProcess);
                }
            }
        }
    }
    
    /**
     * <pre>抽象业务处理方法</pre>
     * @author lilinhai
     * @since 2020-12-18 11:11  void
     */
    protected void process()
    {
        // 处理业务
        T message = null;
        while ((message = queue.poll()) != null)
        {
            process(message);
        }
    }
    
    /**
     * <pre>抽象业务处理方法</pre>
     * @author lilinhai
     * @since 2020-12-18 11:11  void
     */
    protected void process(T message)
    {
        
    }
    
    /**
     * <pre>添加消息对象，通知线程开始运行</pre>
     * @author lilinhai
     * @since 2020-12-02 15:55 
     * @param webSocketClient void
     */
    public void add(T t)
    {
        queue.add(t);
        
        // 通知数据库同步线程开始工作
        synchronized (this)
        {
            this.notify();
        }
    }
    
    /**
     * <pre>设置成wait状态</pre>
     * 
     * @author lilinhai
     * @since 2020-12-14 18:14 void
     */
    private void setWait()
    {
        if (queue.isEmpty())
        {
            synchronized (this)
            {
                try
                {
                    if (queue.isEmpty())
                    {
                        if (!ObjectUtils.isEmpty(waitMessage))
                        {
                            logger.info(waitMessage);
                        }
                        else
                        {
                            logger.info("AsyncMessageProcessor[" + getName() + "] All messages have been processed and the thread is in sleep state!");
                        }
                        this.wait();
                        if (!ObjectUtils.isEmpty(workMessage))
                        {
                            logger.info(workMessage);
                        }
                        else
                        {
                            logger.info("AsyncMessageProcessor[" + getName() + "] Received a new message, the thread entered the working state!");
                        }
                    }
                }
                catch (Throwable e)
                {
                    logger.error("AsyncMessageProcessor[" + getName() + "]setWait error", e);
                }
            }
        }
    }
    
    protected void sleep0(long milliseconds)
    {
        try
        {
            if (sleepMillisecondsPerProcess > 0)
            {
                Thread.sleep(milliseconds);
            }
        }
        catch (Throwable e2)
        {
        }
    }
}

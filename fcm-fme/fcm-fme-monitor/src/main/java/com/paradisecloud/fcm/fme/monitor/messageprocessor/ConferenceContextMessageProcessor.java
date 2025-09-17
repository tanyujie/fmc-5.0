/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeNotAvailableConferenceMonitor.java
 * Package     : com.paradisecloud.fcm.fme.monitor.conference
 * @author lilinhai 
 * @since 2021-03-08 12:14
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.monitor.messageprocessor;

import java.util.Iterator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.common.async.AsyncMessageProcessor;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.conference.message.ConferenceContextMessage;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;

/**  
 * <pre>会议室上下文监听器</pre>
 * @author lilinhai
 * @since 2021-03-08 12:14
 * @version V1.0  
 */
@Component
public class ConferenceContextMessageProcessor extends AsyncMessageProcessor<ConferenceContextMessage> implements InitializingBean
{

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-08 12:15 
     * @param name 
     */
    public ConferenceContextMessageProcessor()
    {
        super("ConferenceContextMonitor-Thread");
        this.sleepMillisecondsPerProcess = 3000;
        this.waitMessage = " ConferenceContextMonitor---Sleep---没有ConferenceContextMessage对象，会议室上下文监听器进入休眠状态！";
        this.workMessage = " ConferenceContextMonitor---Work---收到ConferenceContextMessage对象通知，会议室上下文监听器进入工作状态！";
    }
    
    protected void process()
    {
        for (Iterator<ConferenceContextMessage> iterator = queue.iterator(); iterator.hasNext();)
        {
            ConferenceContextMessage conferenceContextMessage = iterator.next();
            try
            {
                ConferenceContext cc = ConferenceContextCache.getInstance().get(conferenceContextMessage.getConferenceContext().getContextKey());
                if (cc == null || cc.isEnd())
                {
                    iterator.remove();
                    continue;
                }
                
                // 若是满足执行条件，则进行消息处理，同时移除该消息
                if (conferenceContextMessage.isNeedProcess())
                {
                    iterator.remove();
                    conferenceContextMessage.process();
                }
            }
            catch (Throwable e)
            {
                logger.error("消息处理出错: " + conferenceContextMessage.getClass().getSimpleName() + "-- " + conferenceContextMessage.getConferenceContext().getConferenceNumber(), e);
            }
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author lilinhai
     * @since 2021-03-11 20:31 
     * @param t
     * @see com.paradisecloud.fcm.common.async.AsyncMessageProcessor#add(java.lang.Object)
     */
    @Override
    public void add(ConferenceContextMessage t)
    {
        if (!queue.contains(t))
        {
            super.add(t);
        }
    }
    
}

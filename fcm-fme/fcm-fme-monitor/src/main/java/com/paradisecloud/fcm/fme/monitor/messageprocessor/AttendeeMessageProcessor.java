/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceSynchronizer.java
 * Package     : com.paradisecloud.fcm.fme.conferencemonitor.messageconsumer
 * @author lilinhai 
 * @since 2021-03-02 14:26
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.monitor.messageprocessor;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.common.async.AsyncMessageProcessor;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.AttendeeMessage;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.UpFmeAttendee;

/**  
 * <pre>会议室数据同步器</pre>
 * @author lilinhai
 * @since 2021-03-02 14:26
 * @version V1.0  
 */
@Component
public class AttendeeMessageProcessor extends AsyncMessageProcessor<AttendeeMessage> implements InitializingBean
{
    
    private Map<String, AttendeeMessage> attendeeMessageMap = new ConcurrentHashMap<>();
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2020-12-02 14:23  
     */
    public AttendeeMessageProcessor()
    {
        super("AttendeeMessageProcessor-Thread");
        this.sleepMillisecondsPerProcess = 3000;
        this.waitMessage = " ConferenceDataSynchronizer---Sleep---没有AttendeeMessage对象，会议室数据同步器进入休眠状态！";
        this.workMessage = " ConferenceDataSynchronizer---Work---收到AttendeeMessage对象通知，会议室数据同步器进入工作状态！";
    }
    
    protected void process()
    {
        for (Iterator<AttendeeMessage> iterator = queue.iterator(); iterator.hasNext();)
        {
            AttendeeMessage attendeeMessage = iterator.next();
            try
            {
                ConferenceContext cc = ConferenceContextCache.getInstance().get(attendeeMessage.getAttendee().getContextKey());
                if (cc == null || cc.isEnd() || (!cc.isAutoCallTerminal() && !(attendeeMessage.getAttendee() instanceof FmeAttendee && !(attendeeMessage.getAttendee() instanceof UpFmeAttendee))))
                {
                    iterator.remove();
                    attendeeMessageMap.remove(attendeeMessage.getAttendee().getId());
                    continue;
                }
                
                // 消息作废或参会被移除，重呼不再需要
                if (attendeeMessage.isDiscard() || cc.getAttendeeById(attendeeMessage.getAttendee().getId()) == null)
                {
                    iterator.remove();
                    attendeeMessageMap.remove(attendeeMessage.getAttendee().getId());
                    logger.info("参会消息作废：" + attendeeMessage);
                    continue;
                }
                
                // 若是满足执行条件，则进行消息处理，同时移除该消息
                if (attendeeMessage.isNeedProcess())
                {
                    iterator.remove();
                    attendeeMessageMap.remove(attendeeMessage.getAttendee().getId());
                    attendeeMessage.process();
                }
            }
            catch (Throwable e)
            {
                logger.error("消息处理出错: " + attendeeMessage, e);
            }
        }
    }
    
    @Override
    public void add(AttendeeMessage t)
    {
        if (!attendeeMessageMap.containsKey(t.getAttendee().getId()))
        {
            attendeeMessageMap.put(t.getAttendee().getId(), t);
            super.add(t);
        }
    }

    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}

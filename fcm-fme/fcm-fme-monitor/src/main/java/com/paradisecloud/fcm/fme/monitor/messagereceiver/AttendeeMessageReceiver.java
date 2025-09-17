/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebsocketMessageThreadPoolExecutor.java
 * Package     : com.paradisecloud.fcm.service.websocket
 * @author lilinhai 
 * @since 2021-02-04 16:41
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.monitor.messagereceiver;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.AttendeeMessage;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.AttendeeMessageQueue;
import com.paradisecloud.fcm.fme.monitor.messageprocessor.AttendeeMessageProcessor;
import com.sinhy.model.AsyncBlockingMessageProcessor;
import com.sinhy.utils.ReflectionUtils;

/**  
 * <pre>参会者消息接收器</pre>
 * @author lilinhai
 * @since 2021-02-04 16:41
 * @version V1.0  
 */
@Component
public class AttendeeMessageReceiver extends AsyncBlockingMessageProcessor<AttendeeMessage> implements InitializingBean
{
    
    @Autowired
    private AttendeeMessageProcessor conferenceDataSynchronizer;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 17:00 
     * @param name
     * @param queueSize 
     */
    public AttendeeMessageReceiver()
    {
        super("AttendeeMessageReceiver", (AttendeeMessageQueue)ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(AttendeeMessageQueue.class, "getInstance"), null));
    }

    @Override
    public void process(AttendeeMessage message)
    {
        conferenceDataSynchronizer.add(message);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}

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

import com.paradisecloud.fcm.fme.conference.message.ConferenceContextMessage;
import com.paradisecloud.fcm.fme.conference.message.ConferenceContextMessageQueue;
import com.paradisecloud.fcm.fme.monitor.messageprocessor.ConferenceContextMessageProcessor;
import com.sinhy.model.AsyncBlockingMessageProcessor;
import com.sinhy.utils.ReflectionUtils;

/**  
 * <pre>会议上下文消息接收器</pre>
 * @author lilinhai
 * @since 2021-02-04 16:41
 * @version V1.0  
 */
@Component
public class ConferenceContextMessageReceiver extends AsyncBlockingMessageProcessor<ConferenceContextMessage> implements InitializingBean
{
    
    @Autowired
    private ConferenceContextMessageProcessor conferenceContextMonitor;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 17:00 
     * @param name
     * @param queueSize 
     */
    public ConferenceContextMessageReceiver()
    {
        super("ConferenceContextMessageReceiver", (ConferenceContextMessageQueue)ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(ConferenceContextMessageQueue.class, "getInstance"), null));
    }

    @Override
    public void process(ConferenceContextMessage message)
    {
        conferenceContextMonitor.add(message);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}

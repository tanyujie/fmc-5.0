/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebsocketMessageThreadPoolExecutor.java
 * Package     : com.paradisecloud.fcm.service.websocket
 * @author lilinhai 
 * @since 2021-02-04 16:41
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.queue;

import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeLayoutWebsocketMessageService;
import com.paradisecloud.fcm.fme.model.busi.message.AttendeeStatusMessage;
import com.sinhy.model.AsyncBlockingMessageProcessor;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ClassUtils;
import com.sinhy.utils.ReflectionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**  
 * <pre>webSocket消息异执行器</pre>
 * @author lilinhai
 * @since 2021-02-04 16:41
 * @version V1.0  
 */
@Component
public class AttendeeStatusLayoutMessageProcessor extends AsyncBlockingMessageProcessor<AttendeeStatusMessage> implements InitializingBean
{

    private Method subscribeMethod;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-02-04 17:00
     * @param name
     * @param queueSize
     */
    public AttendeeStatusLayoutMessageProcessor()
    {
        super("AttendeeStatusLayoutMessageProcessor", (AttendeeStatusLayoutMessageQueue)ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(AttendeeStatusLayoutMessageQueue.class, "getInstance"), null));
        subscribeMethod = ClassUtils.getMethod(IAttendeeLayoutWebsocketMessageService.class, "subscribe", AttendeeStatusMessage.class);
    }

    @Override
    public void process(AttendeeStatusMessage message)
    {
        try
        {
            ReflectionUtils.invokeMethod(subscribeMethod, BeanFactory.getBean(IAttendeeLayoutWebsocketMessageService.class), message);
        }
        catch (Throwable e)
        {
            LoggerFactory.getLogger(getClass()).error("Error in process AttendeeStatusMessage", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}

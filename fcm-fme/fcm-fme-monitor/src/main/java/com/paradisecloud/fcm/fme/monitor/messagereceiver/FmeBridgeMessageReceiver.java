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

import com.paradisecloud.fcm.fme.cache.model.message.FmeBridgeMessage;
import com.paradisecloud.fcm.fme.cache.model.message.FmeBridgeMessageQueue;
import com.paradisecloud.fcm.fme.monitor.messageprocessor.FmeBridgeMessageProcessor;
import com.sinhy.model.AsyncBlockingMessageProcessor;
import com.sinhy.utils.ReflectionUtils;

/**  
 * <pre>参会者消息接收器</pre>
 * @author lilinhai
 * @since 2021-02-04 16:41
 * @version V1.0  
 */
@Component
public class FmeBridgeMessageReceiver extends AsyncBlockingMessageProcessor<FmeBridgeMessage> implements InitializingBean
{
    
    @Autowired
    private FmeBridgeMessageProcessor fmeBridgeMessageProcessor;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 17:00 
     * @param name
     * @param queueSize 
     */
    public FmeBridgeMessageReceiver()
    {
        super("FmeBridgeMessageReceiver", (FmeBridgeMessageQueue)ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(FmeBridgeMessageQueue.class, "getInstance"), null));
    }

    @Override
    public void process(FmeBridgeMessage message)
    {
        fmeBridgeMessageProcessor.add(message);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}

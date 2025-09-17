/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebsocketMessageThreadPoolExecutor.java
 * Package     : com.paradisecloud.fcm.service.websocket
 * @author lilinhai 
 * @since 2021-02-04 16:41
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.service.websocket;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.common.message.websocket.WebSocketMessage;
import com.paradisecloud.fcm.common.message.websocket.WebSocketMessageQueue;
import com.sinhy.model.AsyncBlockingMessageProcessor;
import com.sinhy.utils.ReflectionUtils;

/**  
 * <pre>webSocket消息异执行器</pre>
 * @author lilinhai
 * @since 2021-02-04 16:41
 * @version V1.0  
 */
@Component
public class WebsocketMessageProcessor extends AsyncBlockingMessageProcessor<WebSocketMessage> implements InitializingBean
{

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 17:00 
     * @param name
     * @param queueSize 
     */
    public WebsocketMessageProcessor()
    {
        super("WebsocketMessageProcessor", (WebSocketMessageQueue)ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(WebSocketMessageQueue.class, "getInstance"), null));
    }

    @Override
    public void process(WebSocketMessage message)
    {
        simpMessagingTemplate.convertAndSend(message.getDestination(), message);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}

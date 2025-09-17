package com.paradisecloud.fcm.smc.cache.modle;

import com.sinhy.model.AsyncBlockingMessageProcessor;
import com.sinhy.utils.ReflectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * @author nj
 * @date 2023/3/20 14:12
 */
@Component
public class SmcWebsocketMessageProcessor extends AsyncBlockingMessageProcessor<SmcWebsocketMessage> implements InitializingBean
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
    public SmcWebsocketMessageProcessor()
    {
        super("SmcWebsocketMessageProcessor", (SmcWebSocketMessageQueue) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(SmcWebSocketMessageQueue.class, "getInstance"), null));
    }

    @Override
    public void process(SmcWebsocketMessage message)
    {
        simpMessagingTemplate.convertAndSend(message.getDestination(), message);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}
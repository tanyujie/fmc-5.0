package com.paradisecloud.fcm.smc2.cache;

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
public class Smc2WebsocketMessageProcessor extends AsyncBlockingMessageProcessor<Smc2WebsocketMessage> implements InitializingBean
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
    public Smc2WebsocketMessageProcessor()
    {
        super("Smc2WebsocketMessageProcessor", (Smc2WebSocketMessageQueue) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(Smc2WebSocketMessageQueue.class, "getInstance"), null));
    }

    @Override
    public void process(Smc2WebsocketMessage message)
    {
        simpMessagingTemplate.convertAndSend(message.getDestination(), message);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}
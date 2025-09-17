package com.paradisecloud.fcm.huaweicloud.huaweicloud.cache;

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
public class HwcloudWebsocketMessageProcessor extends AsyncBlockingMessageProcessor<HwcloudWebsocketMessage> implements InitializingBean
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
    public HwcloudWebsocketMessageProcessor()
    {
        super("SmcWebsocketMessageProcessor", (HwcloudWebSocketMessageQueue) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(HwcloudWebSocketMessageQueue.class, "getInstance"), null));
    }

    @Override
    public void process(HwcloudWebsocketMessage message)
    {
        simpMessagingTemplate.convertAndSend(message.getDestination(), message);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}
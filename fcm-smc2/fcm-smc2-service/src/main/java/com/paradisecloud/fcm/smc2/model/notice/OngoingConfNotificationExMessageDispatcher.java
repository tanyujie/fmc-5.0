package com.paradisecloud.fcm.smc2.model.notice;

import com.paradisecloud.fcm.smc2.utils.Smc2ThreadPool;
import com.sinhy.core.processormessage.ProcessorMessageDispatcher;
import com.sinhy.utils.ReflectionUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author nj
 * @date 2023/4/26 11:04
 */
@Component
public class OngoingConfNotificationExMessageDispatcher extends ProcessorMessageDispatcher<OngoingConfNotificationExMessage> {
    protected OngoingConfNotificationExMessageDispatcher() {

        super("OngoingConfNotification"
                , (LinkedBlockingQueue<OngoingConfNotificationExMessage>) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(OngoingConfNotificationProcessorMessageQueue.class, "getInstance"), null)
                , Smc2ThreadPool.getFixedThreadPool());
    }
}

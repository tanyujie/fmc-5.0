package com.paradisecloud.fcm.ding.event;

import com.paradisecloud.fcm.common.utils.FcmThreadPool;
import com.sinhy.core.processormessage.ProcessorMessageDispatcher;
import com.sinhy.utils.ReflectionUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author nj
 * @date 2023/4/26 11:04
 */
@Component
public class DingMeetingMessageDispatcher extends ProcessorMessageDispatcher<DingMeetingMessage> {
    protected DingMeetingMessageDispatcher() {

        super("OngoingConfNotification"
                , (LinkedBlockingQueue<DingMeetingMessage>) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(DingMeetingProcessorMessageQueue.class, "getInstance"), null)
                , FcmThreadPool.getFixedThreadPool());
    }
}

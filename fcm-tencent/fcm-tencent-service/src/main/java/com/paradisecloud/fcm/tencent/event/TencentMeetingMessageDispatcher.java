package com.paradisecloud.fcm.tencent.event;

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
public class TencentMeetingMessageDispatcher extends ProcessorMessageDispatcher<TencentMeetingMessage> {
    protected TencentMeetingMessageDispatcher() {

        super("OngoingConfNotification"
                , (LinkedBlockingQueue<TencentMeetingMessage>) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(TencentMeetingProcessorMessageQueue.class, "getInstance"), null)
                , FcmThreadPool.getFixedThreadPool());
    }
}

package com.paradisecloud.fcm.huaweicloud.huaweicloud.event;

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
public class HwcloudMeetingMessageDispatcher extends ProcessorMessageDispatcher<HwcloudMeetingMessage> {
    protected HwcloudMeetingMessageDispatcher() {

        super("OngoingConfNotification"
                , (LinkedBlockingQueue<HwcloudMeetingMessage>) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(HwcloudMeetingProcessorMessageQueue.class, "getInstance"), null)
                , FcmThreadPool.getFixedThreadPool());
    }
}

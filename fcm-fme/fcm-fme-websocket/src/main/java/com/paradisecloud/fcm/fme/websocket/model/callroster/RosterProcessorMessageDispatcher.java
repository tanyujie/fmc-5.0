/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterUpdateMessageQueue.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.core
 * @author sinhy 
 * @since 2021-09-07 21:41
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.model.callroster;

import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.websocket.model.callroster.message.RosterProcessorMessage;
import com.sinhy.core.processormessage.ProcessorMessageDispatcher;
import com.sinhy.utils.ReflectionUtils;

@Component
public class RosterProcessorMessageDispatcher extends ProcessorMessageDispatcher<RosterProcessorMessage>
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-07 21:47 
     * @param name
     * @param queueSize 
     */
    public RosterProcessorMessageDispatcher()
    {
        super("CallRoster"
                , (RosterProcessorMessageQueue) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(RosterProcessorMessageQueue.class, "getInstance"), null)
                , FcmThreadPool.getFixedThreadPool());
    }
}

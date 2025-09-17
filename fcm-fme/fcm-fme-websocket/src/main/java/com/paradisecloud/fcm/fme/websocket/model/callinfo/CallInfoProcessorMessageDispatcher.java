/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallInfoProcessorMessageDispatcher.java
 * Package     : com.paradisecloud.fcm.fme.websocket.model.callinfo
 * @author sinhy 
 * @since 2021-09-12 12:02
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.model.callinfo;

import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.websocket.model.callinfo.message.CallInfoProcessorMessage;
import com.sinhy.core.processormessage.ProcessorMessageDispatcher;
import com.sinhy.utils.ReflectionUtils;

@Component
public class CallInfoProcessorMessageDispatcher extends ProcessorMessageDispatcher<CallInfoProcessorMessage>
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-12 12:04 
     * @param name
     * @param queue 
     */
    protected CallInfoProcessorMessageDispatcher()
    {
        super("CallInfo"
                , (CallInfoProcessorMessageQueue) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(CallInfoProcessorMessageQueue.class, "getInstance"), null)
                , FcmThreadPool.getFixedThreadPool());
    }
}

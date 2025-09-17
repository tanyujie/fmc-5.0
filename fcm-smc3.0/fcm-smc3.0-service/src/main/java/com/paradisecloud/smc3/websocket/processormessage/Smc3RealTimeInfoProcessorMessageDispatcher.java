/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterUpdateMessageQueue.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.core
 * @author sinhy 
 * @since 2021-09-07 21:41
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.websocket.processormessage;

import com.paradisecloud.smc3.invoker.util.SmcThreadPool;
import com.sinhy.core.processormessage.ProcessorMessageDispatcher;
import com.sinhy.utils.ReflectionUtils;
import org.springframework.stereotype.Component;

@Component
public class Smc3RealTimeInfoProcessorMessageDispatcher extends ProcessorMessageDispatcher<Smc3RealTimeInfoProcessorMessage>
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-07 21:47 
     */
    public Smc3RealTimeInfoProcessorMessageDispatcher()
    {
        super("Smc3RealTimeInfo"
                , (Smc3RealTimeInfoProcessorMessageQueue) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(Smc3RealTimeInfoProcessorMessageQueue.class, "getInstance"), null)
                , SmcThreadPool.getFixedThreadPool());
    }
}

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterUpdateMessageQueue.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.core
 * @author sinhy 
 * @since 2021-09-07 21:41
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.processormessage;

import com.paradisecloud.fcm.common.utils.FcmThreadPool;
import com.sinhy.core.processormessage.ProcessorMessageDispatcher;
import com.sinhy.utils.ReflectionUtils;
import org.springframework.stereotype.Component;

@Component
public class HwcloudRealTimeInfoProcessorMessageDispatcher extends ProcessorMessageDispatcher<HwcloudRealTimeInfoProcessorMessage>
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-07 21:47 
     */
    public HwcloudRealTimeInfoProcessorMessageDispatcher()
    {
        super("HwcloudRealTimeInfo"
                , (HwcloudRealTimeInfoProcessorMessageQueue) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(HwcloudRealTimeInfoProcessorMessageQueue.class, "getInstance"), null)
                , FcmThreadPool.getFixedThreadPool());
    }
}

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallInfoUpdateMessageServiceImpl.java
 * Package     : com.paradisecloud.sync.service.impls
 * @author lilinhai 
 * @since 2020-12-14 13:49
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.impls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.websocket.callinfo.CallInfoUpdateMessage;
import com.paradisecloud.fcm.fme.websocket.interfaces.ICallInfoUpdateMessageService;
import com.paradisecloud.fcm.fme.websocket.model.callinfo.CallInfoProcessorMessageQueue;
import com.paradisecloud.fcm.fme.websocket.model.callinfo.message.CallInfoProcessorUpdateMessage;

/**  
 * <pre>CallInfoUpdateMessage业务处理</pre>
 * @author lilinhai
 * @since 2020-12-14 13:49
 * @version V1.0  
 */
@Service
public class CallInfoUpdateMessageServiceImpl implements ICallInfoUpdateMessageService
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CallInfoUpdateMessageServiceImpl.class);
    
    @Override
    public void process(CallInfoUpdateMessage callInfoUpdateMessage, JSONObject messageObj, FmeBridge fmeBridge)
    {
        try
        {
            JSONObject callUpdate = messageObj.getJSONObject("message").getJSONObject("callInfo");
            String callId = callInfoUpdateMessage.getCallInfo().getCall();
            callUpdate.put("call", callId);
            CallInfoProcessorMessageQueue.getInstance().put(new CallInfoProcessorUpdateMessage(fmeBridge, callUpdate));
        }
        catch (Throwable e)
        {
            LOGGER.error(" FCM-SYNC---process CallInfoUpdateMessage service error: ", e);
        }
    }
    
}

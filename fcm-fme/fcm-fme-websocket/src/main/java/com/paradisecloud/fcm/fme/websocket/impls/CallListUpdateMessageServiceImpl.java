/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallListUpdateMessageService.java
 * Package     : com.paradisecloud.sync.service.impls
 * @author lilinhai 
 * @since 2020-12-10 15:49
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.impls;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.websocket.calllist.CallListUpdateMessage;
import com.paradisecloud.fcm.fme.websocket.interfaces.ICallListUpdateMessageService;
import com.paradisecloud.fcm.fme.websocket.model.callinfo.CallInfoProcessorMessageQueue;
import com.paradisecloud.fcm.fme.websocket.model.callinfo.message.CallInfoProcessorAddMessage;
import com.paradisecloud.fcm.fme.websocket.model.callinfo.message.CallInfoProcessorRemoveMessage;
import com.paradisecloud.fcm.fme.websocket.model.callinfo.message.CallInfoProcessorUpdateMessage;

/**  
 * <pre>CallListUpdateMessage业务处理类</pre>
 * @author lilinhai
 * @since 2020-12-10 15:49
 * @version V1.0  
 */
@Service
public class CallListUpdateMessageServiceImpl implements ICallListUpdateMessageService
{
    
    @Override
    public void process(CallListUpdateMessage callListUpdateMessage, JSONObject json, FmeBridge fmeBridge)
    {
        try
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("CallListUpdateMessageJson: " + json, false);
            JSONArray updates = json.getJSONObject("message").getJSONArray("updates");
            if (!ObjectUtils.isEmpty(updates))
            {
                for (int i = 0; i < updates.size(); i++)
                {
                    JSONObject callUpdate = updates.getJSONObject(i);
                    String callId = callUpdate.getString("call");
                    if (!ObjectUtils.isEmpty(callId))
                    {
                        String updateType = callUpdate.getString("updateType");
                        if (!ObjectUtils.isEmpty(updateType))
                        {
                            if (updateType.equals("add"))
                            {
                                CallInfoProcessorMessageQueue.getInstance().put(new CallInfoProcessorAddMessage(fmeBridge, callUpdate));
                            }
                            else if (updateType.equals("update"))
                            {
                                CallInfoProcessorMessageQueue.getInstance().put(new CallInfoProcessorUpdateMessage(fmeBridge, callUpdate));
                            }
                            else
                            {
                                CallInfoProcessorMessageQueue.getInstance().put(new CallInfoProcessorRemoveMessage(fmeBridge, callUpdate));
                            }
                        }
                    }
                }
            }
        }
        catch (Throwable e)
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("CallListUpdateMessage service error:", true, e);
        }
    }
}

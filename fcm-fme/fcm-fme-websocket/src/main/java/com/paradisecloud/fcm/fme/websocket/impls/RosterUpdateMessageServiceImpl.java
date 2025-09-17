/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterUpdateMessageServiceImpl.java
 * Package     : com.paradisecloud.sync.service.impls
 * @author lilinhai 
 * @since 2020-12-14 14:43
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.impls;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.websocket.roster.RosterUpdateMessage;
import com.paradisecloud.fcm.fme.websocket.interfaces.IRosterUpdateMessageService;
import com.paradisecloud.fcm.fme.websocket.model.callroster.RosterProcessorMessageQueue;
import com.paradisecloud.fcm.fme.websocket.model.callroster.message.RosterProcessorAddMessage;
import com.paradisecloud.fcm.fme.websocket.model.callroster.message.RosterProcessorRemoveMessage;
import com.paradisecloud.fcm.fme.websocket.model.callroster.message.RosterProcessorUnknowMessage;
import com.paradisecloud.fcm.fme.websocket.model.callroster.message.RosterProcessorUpdateMessage;

/**  
 * <pre>RosterUpdateMessage业务处理</pre>
 * @author lilinhai
 * @since 2020-12-14 14:43
 * @version V1.0  
 */
@Service
public class RosterUpdateMessageServiceImpl implements IRosterUpdateMessageService
{
    
    public void process(RosterUpdateMessage rosterUpdateMessage, JSONObject json, FmeBridge fmeBridge)
    {
        try
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("RosterUpdateMessageJson: " + json, false);
            JSONArray updates = json.getJSONObject("message").getJSONArray("updates");
            if (!ObjectUtils.isEmpty(updates))
            {
                for (int i = 0; i < updates.size(); i++)
                {
                    JSONObject rosterUpdate = updates.getJSONObject(i);
                    String participant = rosterUpdate.getString("participant");
                    if (!ObjectUtils.isEmpty(participant))
                    {
                        fmeBridge.getFmeLogger().logWebsocketInfo(" =======================> websocket-track rosterUpdate: " + rosterUpdate, true, false);
                        String updateType = rosterUpdate.getString("updateType");
                        if (!ObjectUtils.isEmpty(updateType))
                        {
                            if (updateType.equals("add"))
                            {
                                RosterProcessorMessageQueue.getInstance().put(new RosterProcessorAddMessage(fmeBridge, rosterUpdate));
                            }
                            else if (updateType.equals("update"))
                            {
                                RosterProcessorMessageQueue.getInstance().put(new RosterProcessorUpdateMessage(fmeBridge, rosterUpdate));
                            }
                            else if (updateType.equals("remove"))
                            {
                                RosterProcessorMessageQueue.getInstance().put(new RosterProcessorRemoveMessage(fmeBridge, rosterUpdate));
                            }
                            else
                            {
                                fmeBridge.getFmeLogger().logWebsocketInfo("rosterUpdate对象无法识别的updateType属性: " + updateType, true);
                                RosterProcessorMessageQueue.getInstance().put(new RosterProcessorUnknowMessage(fmeBridge, rosterUpdate));
                            }
                        }
                    }
                    else
                    {
                        fmeBridge.getFmeLogger().logWebsocketInfo("error rosterUpdate: " + rosterUpdate, true, true);
                    }
                }
            }
        }
        catch (Throwable e)
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("rosterUpdateMessage service error", true, e);
        }
    }
}

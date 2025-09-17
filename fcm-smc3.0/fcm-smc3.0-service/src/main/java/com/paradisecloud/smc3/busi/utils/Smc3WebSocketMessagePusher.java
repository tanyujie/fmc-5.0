/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebSocketMessageSender.java
 * Package     : com.paradisecloud.fcm.fme.model.websocket
 * @author lilinhai 
 * @since 2021-02-08 09:46
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.utils;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.message.websocket.WebSocketMessageQueue;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.cache.BaseWebSocketMessagePusher;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.McuAttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.SelfCallAttendeeSmc3;

import javax.management.modelmbean.ModelMBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**  
 * <pre>webSocket消息推送器</pre>
 * @author lilinhai
 * @since 2021-02-08 09:46
 * @version V1.0  
 */
public class Smc3WebSocketMessagePusher
{
    private static final Smc3WebSocketMessagePusher INSTANCE = new Smc3WebSocketMessagePusher();

    private Smc3WebSocketMessagePusher()
    {
        
    }
    
    /**
     * 向上冒泡推送会议消息
     * @author lilinhai
     * @since 2021-02-08 09:51 
     * @param conferenceContext
     * @param websocketMessageType
     * @param messageContent void
     */
    public void upwardPushConferenceMessage(Smc3ConferenceContext conferenceContext, WebsocketMessageType websocketMessageType, Object messageContent)
    {
        Smc3ConferenceContextCache.getInstance().upwardProcessingConferenceContext(conferenceContext, (cc) -> {
            pushSpecificConferenceMessage(cc, websocketMessageType, messageContent);
        });
    }
    
    /**
     * 推送所有级联会议消息
     * @author lilinhai
     * @since 2021-02-08 09:51 
     * @param conferenceContext
     * @param websocketMessageType
     * @param messageContent void
     */
    public void pushConferenceMessageToAll(Smc3ConferenceContext conferenceContext, WebsocketMessageType websocketMessageType, Object messageContent)
    {
        Smc3ConferenceContextCache.getInstance().downwardProcessingConferenceContext(conferenceContext, (cc)->{
            pushSpecificConferenceMessage(cc, websocketMessageType, messageContent);
        });
    }
    
    /**
     * 推送所有级联会议消息
     * @author lilinhai
     * @since 2021-02-08 09:51 
     * @param conferenceContext
     * @param websocketMessageType
     * @param messageContent void
     */
    public void pushSpecificConferenceMessage(Smc3ConferenceContext conferenceContext, WebsocketMessageType websocketMessageType, Object messageContent)
    {
        if (conferenceContext != null)
        {
            BaseWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, websocketMessageType, messageContent);
        }
    }

    public void pushSpecificConferenceMessage(String conferenceId, WebsocketMessageType websocketMessageType, Object messageContent)
    {
        WebSocketMessageQueue.getInstance().put(websocketMessageType.create(messageContent, "/conference/" + conferenceId));
    }


    public static Smc3WebSocketMessagePusher getInstance()
    {
        return INSTANCE;
    }
}

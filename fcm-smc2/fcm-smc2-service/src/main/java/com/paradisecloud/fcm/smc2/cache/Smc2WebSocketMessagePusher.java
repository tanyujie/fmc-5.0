/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebSocketMessageSender.java
 * Package     : com.paradisecloud.fcm.fme.model.websocket
 * @author lilinhai 
 * @since 2021-02-08 09:46
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smc2.cache;

import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.message.websocket.WebSocketMessageQueue;
import com.paradisecloud.fcm.service.conference.cache.BaseWebSocketMessagePusher;

/**
 * <pre>webSocket消息推送器</pre>
 * @author lilinhai
 * @since 2021-02-08 09:46
 * @version V1.0  
 */
public class Smc2WebSocketMessagePusher
{
    private static final Smc2WebSocketMessagePusher INSTANCE = new Smc2WebSocketMessagePusher();

    private Smc2WebSocketMessagePusher()
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
    public void upwardPushConferenceMessage(Smc2ConferenceContext conferenceContext, WebsocketMessageType websocketMessageType, Object messageContent)
    {
        Smc2ConferenceContextCache.getInstance().upwardProcessingConferenceContext(conferenceContext, (cc) -> {
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
    public void pushConferenceMessageToAll(Smc2ConferenceContext conferenceContext, WebsocketMessageType websocketMessageType, Object messageContent)
    {
        Smc2ConferenceContextCache.getInstance().downwardProcessingConferenceContext(conferenceContext, (cc)->{
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
    public void pushSpecificConferenceMessage(Smc2ConferenceContext conferenceContext, WebsocketMessageType websocketMessageType, Object messageContent)
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


    public static Smc2WebSocketMessagePusher getInstance()
    {
        return INSTANCE;
    }
}

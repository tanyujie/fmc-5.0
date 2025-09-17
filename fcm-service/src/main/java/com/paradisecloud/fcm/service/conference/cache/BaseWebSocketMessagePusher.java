/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebSocketMessageSender.java
 * Package     : com.paradisecloud.fcm.fme.model.websocket
 * @author lilinhai 
 * @since 2021-02-08 09:46
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.service.conference.cache;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.message.websocket.WebSocketMessageQueue;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;

import java.util.HashMap;
import java.util.Map;

/**  
 * <pre>webSocket消息推送器</pre>
 * @author lilinhai
 * @since 2021-02-08 09:46
 * @version V1.0  
 */
public class BaseWebSocketMessagePusher
{
    private static final BaseWebSocketMessagePusher INSTANCE = new BaseWebSocketMessagePusher();

    private BaseWebSocketMessagePusher()
    {
        
    }
    
    /**
     * 推送所有级联会议消息
     * @author lilinhai
     * @since 2021-02-08 09:51 
     * @param conferenceContext
     * @param websocketMessageType
     * @param messageContent void
     */
    public void pushSpecificConferenceMessage(BaseConferenceContext conferenceContext, WebsocketMessageType websocketMessageType, Object messageContent)
    {
        if (conferenceContext != null)
        {
            WebSocketMessageQueue.getInstance().put(websocketMessageType.create(messageContent, "/conference/" + conferenceContext.getId()));
            if (StringUtils.isNotEmpty(conferenceContext.getUpCascadeConferenceId())) {
                if (websocketMessageType == WebsocketMessageType.ATTENDEE_ADD
                        || websocketMessageType == WebsocketMessageType.ATTENDEE_UPDATE
                        || websocketMessageType == WebsocketMessageType.ATTENDEE_DELETE
                        || websocketMessageType == WebsocketMessageType.ATTENDEE_SPEAKER) {
                    if (messageContent instanceof Map) {
                        Map mapMessageContentNew = new HashMap((Map) messageContent);
                        mapMessageContentNew.put("conferenceId", conferenceContext.getId());
                        WebSocketMessageQueue.getInstance().put(websocketMessageType.create(mapMessageContentNew, "/conference/" + conferenceContext.getUpCascadeConferenceId()));
                    }
                    if(messageContent instanceof BaseAttendee){
                        HashMap hashMap = JSONObject.parseObject(JSONObject.toJSONString(messageContent), HashMap.class);
                        hashMap.put("conferenceId", conferenceContext.getId());
                        WebSocketMessageQueue.getInstance().put(websocketMessageType.create(hashMap, "/conference/" + conferenceContext.getUpCascadeConferenceId()));
                    }

                }
            }

        }
    }

    /**
     * 推送所有级联会议消息
     * @author lilinhai
     * @since 2021-02-08 09:51
     * @param websocketMessageType
     * @param messageContent void
     */
    public void pushSpecificConferenceMessage(String conferenceId, WebsocketMessageType websocketMessageType, Object messageContent)
    {
        WebSocketMessageQueue.getInstance().put(websocketMessageType.create(messageContent, "/conference/" + conferenceId));
    }

    /**
     * 推送网络检测服务器消息
     * @author lilinhai
     * @since 2021-02-08 09:51
     * @param websocketMessageType
     * @param messageContent void
     */
    public void pushNetCheckServerMessage(WebsocketMessageType websocketMessageType, Object messageContent)
    {
        if (messageContent != null)
        {
            WebSocketMessageQueue.getInstance().put(websocketMessageType.create(messageContent, "/netcheck/server"));
        }
    }


    public static BaseWebSocketMessagePusher getInstance()
    {
        return INSTANCE;
    }
}

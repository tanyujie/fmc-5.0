package com.paradisecloud.fcm.smc.cache.modle;

import org.apache.logging.log4j.util.Strings;

/**
 * @author nj
 * @date 2023/3/20 10:34
 */
public class SmcWebSocketMessagePusher {
    private static final SmcWebSocketMessagePusher INSTANCE = new SmcWebSocketMessagePusher();

    private SmcWebSocketMessagePusher()
    {

    }

    /**
     * 推送所有级联会议消息
     * @param websocketMessageType
     * @param messageContent void
     */
    public void pushSpecificConferenceMessage(String conferenceId, SmcWebsocketMessageType websocketMessageType, Object messageContent)
    {
        if (Strings.isNotBlank(conferenceId))
        {
            SmcWebSocketMessageQueue.getInstance().put(websocketMessageType.create(messageContent, "/conference/" + conferenceId));
        }
    }

    public static SmcWebSocketMessagePusher getInstance()
    {
        return INSTANCE;
    }
}

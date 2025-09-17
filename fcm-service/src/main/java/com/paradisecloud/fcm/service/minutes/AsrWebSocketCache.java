/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebSocketMessageSender.java
 * Package     : com.paradisecloud.fcm.fme.model.websocket
 * @author lilinhai 
 * @since 2021-02-08 09:46
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.service.minutes;

/**
 * <pre>webSocket消息推送器</pre>
 * @author lilinhai
 * @since 2021-02-08 09:46
 * @version V1.0  
 */
public class AsrWebSocketCache
{
    private static final AsrWebSocketCache INSTANCE = new AsrWebSocketCache();

    private WebsocketClient websocketClient;

    private AsrWebSocketCache()
    {
        
    }
    
    public static AsrWebSocketCache getInstance()
    {
        return INSTANCE;
    }

    public WebsocketClient getWebsocketClient() {
        return websocketClient;
    }

    public void setWebsocketClient(WebsocketClient websocketClient) {
        this.websocketClient = websocketClient;
    }
}

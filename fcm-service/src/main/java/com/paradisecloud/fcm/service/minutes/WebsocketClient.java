/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : McuNodeWebSocketClient.java
 * Package : com.paradisecloud.sync.core
 * 
 * @author lilinhai
 * 
 * @since 2020-12-01 14:17
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.service.minutes;

import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * <pre>FME-Websocket客户端</pre>
 * 
 * @author lilinhai
 * @since 2020-12-01 14:17
 * @version V1.0
 */
public class WebsocketClient extends SSLWebSocketClient
{
    private IWebsocketMessageProcessor websocketMessageProcessor;
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author lilinhai
     * @since 2020-12-01 14:18
     * @param serverURI
     */
    public WebsocketClient(URI serverURI, IWebsocketMessageProcessor websocketMessageProcessor)
    {
        super(serverURI, new Draft_6455());
        this.websocketMessageProcessor = websocketMessageProcessor;
    }
    
    @Override
    public void onOpen(ServerHandshake serverHandshake)
    {
        if (websocketMessageProcessor != null) {
            websocketMessageProcessor.onOpen(serverHandshake);
        }
    }
    
    @Override
    public void onMessage(String message)
    {
        if (websocketMessageProcessor != null) {
            websocketMessageProcessor.processMessage(message);
        }
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        if (websocketMessageProcessor != null) {
            websocketMessageProcessor.onClose(code, reason, remote);
        }
    }
    
    @Override
    public void onError(Exception ex)
    {
        if (websocketMessageProcessor != null) {
            websocketMessageProcessor.onError(ex);
        }
    }
}

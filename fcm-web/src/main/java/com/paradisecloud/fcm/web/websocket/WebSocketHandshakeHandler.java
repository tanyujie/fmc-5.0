/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebSocketHandshakeHandler.java
 * Package     : com.paradisecloud.fcm.web.websocket
 * @author lilinhai 
 * @since 2021-01-28 11:26
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.web.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**  
 * <pre>请加上该类的描述</pre>
 * @author lilinhai
 * @since 2021-01-28 11:26
 * @version V1.0  
 */
public class WebSocketHandshakeHandler extends TextWebSocketHandler
{
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }
}

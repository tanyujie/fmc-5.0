/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : StompMessageHandshakeHandler.java
 * Package : com.paradisecloud.fcm.web.websocket
 * 
 * @author lilinhai
 * 
 * @since 2021-01-28 11:24
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.web.websocket;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

/**
 * <pre>请加上该类的描述</pre>
 * 
 * @author lilinhai
 * @since 2021-01-28 11:24
 * @version V1.0
 */
public class StompMessageHandshakeHandler extends DefaultHandshakeHandler
{
    
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes)
    {
        return super.determineUser(request, wsHandler, attributes);
    }
    
}

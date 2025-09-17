/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : WebSocketHandshakeInterceptor.java
 * Package : com.paradisecloud.fcm.web.websocket
 * 
 * @author lilinhai
 * 
 * @since 2021-01-28 11:25
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.web.websocket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * <pre>请加上该类的描述</pre>
 * 
 * @author lilinhai
 * @since 2021-01-28 11:25
 * @version V1.0
 */
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor
{
    
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex)
    {
        super.afterHandshake(request, response, wsHandler, ex);
    }
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception
    {
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}

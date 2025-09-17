/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : WebSocketConfig.java
 * Package : com.paradisecloud.fcm.web.websocket
 * 
 * @author lilinhai
 * 
 * @since 2021-01-25 11:57
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.web.websocket;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.converter.SmartMessageConverter;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * <pre>注解开启使用STOMP协议来传输基于代理(message broker)的消息,这时控制器支持使用@MessageMapping,就像使用@RequestMapping一样</pre>
 * @author lilinhai
 * @since 2021-01-27 16:57
 * @version V1.0
 */
@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer
{
    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry)
    {
        // 开启/client-message-entry端点
        registry.addEndpoint("/client-message-entry")
                
                .setAllowedOriginPatterns("*")
                
                .setHandshakeHandler(new StompMessageHandshakeHandler())
                .addInterceptors(new WebSocketHandshakeInterceptor())
                
                // 使用sockJS
                .withSockJS();
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry)
    {
        // 点对点应配置一个/user消息代理，广播式应配置一个/topic消息代理
        registry.enableSimpleBroker("/conference", "/user", "/netcheck/server");
        
        // 点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
        registry.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration)
    {
        ChannelInterceptor interceptor = new ChannelInterceptor()
        {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel)
            {
                log.info("Inbound preSend. message={}", message);
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                MessageHeaders header = message.getHeaders();
                String sessionId = (String) header.get("simpSessionId");
                if (accessor != null && accessor.getCommand() != null && accessor.getCommand().getMessageType() != null)
                {
                    SimpMessageType type = accessor.getCommand().getMessageType();
                    if (accessor != null && SimpMessageType.CONNECT.equals(type))
                    {
                        String jwtToken = accessor.getFirstNativeHeader("Authorization");
                        if (StringUtils.isNotBlank(jwtToken))
                        {
                            log.info("新websocket链接: sessionId={}, Authorization={}", sessionId, jwtToken);
                        }
                        else
                        {
                            log.error("No Authorization, client will be disallowed to connect.");
                            return null;
                        }
                    }
                    else if (type == SimpMessageType.DISCONNECT)
                    {
                        log.info("websocket链接已断开 sessionId={} is disconnected", sessionId);
                    }
                    else if (type == SimpMessageType.SUBSCRIBE)
                    {
                        String topicDest = (String) header.get("simpDestination");
                        LinkedMultiValueMap<String, Object> s =  (LinkedMultiValueMap<String, Object>)header.get("nativeHeaders");
                        if (s.get("id").size() > 1)
                        {
                            System.out.println("1111");
                        }
                        log.info("subscribe topicDest={}, message={} SUBSCRIBE", topicDest, message);
                    }
                    else if (type == SimpMessageType.MESSAGE)
                    {
                        String topicDest = (String) header.get("simpDestination");
                        //log.info("之前的消息 topicDest={}, message={} MESSAGE", topicDest, message);
                        message = UpdateMessage(message, "Inbound");
                        //log.info("之后的消息e topicDest={}, message={} MESSAGE", topicDest, message);
                    }
                }
                
                return message;
            }
            
            @Override
            public boolean preReceive(MessageChannel channel)
            {
               // log.info("Inbound preReceive. channel={}", channel);
                return true;
            }
            
            @Override
            public Message<?> postReceive(Message<?> message, MessageChannel channel)
            {
                //log.info("Inbound postReceive. message={}", message);
                return message;
            }
            
            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent)
            {
                log.info("发送消息到客户端： message={}", message);
            }
            
            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex)
            {
               // log.info("Inbound afterSendCompletion. message={}", message);
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                MessageHeaders header = message.getHeaders();
                if (accessor != null && accessor.getCommand() != null && accessor.getCommand().getMessageType() != null)
                {
                    SimpMessageType type = accessor.getCommand().getMessageType();
                    if (type == SimpMessageType.SUBSCRIBE)
                    {
                        String topicDest = (String) header.get("simpDestination");
                        //log.info("afterSenfCompletion. topicDest={}, message={} SUBSCRIBE", topicDest, message);
                        
                        String payload = "{\"myfield1\":\"afterSendCompletion初始化消息\"}";
                        //log.info("send complete. topic={}", topicDest);
                    }
                }
            }
            
            @Override
            public void afterReceiveCompletion(@Nullable Message<?> message, MessageChannel channel, @Nullable Exception ex)
            {
                //log.info("Inbound afterReceiveCompletion. message={}", message);
            }
        };
        
        registration.interceptors(interceptor);
    }
    
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration)
    {
        ChannelInterceptor interceptor = new ChannelInterceptor()
        {
            @Override
            public boolean preReceive(MessageChannel channel)
            {
                //log.info("Outbound preReceive: channel={}", channel);
                return true;
            }
            
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel)
            {
                //log.info("Outbound preSend: message={}", message);
                return message;
            }
            
            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent)
            {
                //log.info("Outbound postSend. message={}", message);
            }
            
            @Override
            public Message<?> postReceive(Message<?> message, MessageChannel channel)
            {
                //log.info("Outbound postReceive. message={}", message);
                return message;
            }
            
            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex)
            {
                //log.info("Outbound afterSendCompletion. message={}", message);
            }
            
            @Override
            public void afterReceiveCompletion(@Nullable Message<?> message, MessageChannel channel, @Nullable Exception ex)
            {
                //log.info("Outbound afterReceiveCompletion. message={}", message);
            }
        };
        
        registration.interceptors(interceptor);
    }
    
    private Message<?> UpdateMessage(Message<?> message, String logFlag)
    {
        //log.info(logFlag + " preSend: message={}", message);
        MessageHeaders header = message.getHeaders();
        Object obj = message.getPayload();
        // 一般都是byte[]
        JSONObject jsonObj = null;
        String strUTF8 = null;
        String strJsonUTF8 = null;
        Message<?> msg = null;
        try
        {
            strUTF8 = new String((byte[]) obj, "UTF-8");
            jsonObj = JSON.parseObject(strUTF8);
            jsonObj.put(logFlag + "ChannelContent2", "add to");
            String value = jsonObj.getString("myfield1");
            jsonObj.put("myfield1", logFlag + " add to " + value);
            strJsonUTF8 = jsonObj.toJSONString();
            byte[] msgToByte = strJsonUTF8.getBytes("UTF-8");
            msg = new GenericMessage<>(msgToByte, header);
        }
        catch (Exception ex)
        {
            //log.info("(byte[] to string exception. ex={}", ex.getLocalizedMessage());
        }
        
        if (msg != null)
        {
            //log.info(logFlag + " preSend Modified: message={}, strUTF8={}, strJsonUTF8={}", msg, strUTF8, strJsonUTF8);
            return msg;
        }
        else
        {
            //log.info(logFlag + " preSend Original: message={}, strUTF8={}", message, strUTF8);
            return message;
        }
    }
    
    // "simpMessageType" -> "MESSAGE"
    Message<?> sendInitMsg(Message<?> oldMessage, String dest, Map<String, Object> headers, String payload)
    {
        MessageHeaders messageHeaders = null;
        Object conversionHint = headers != null ? headers.get("conversionHint") : null;
        Map<String, Object> headersToUse = new HashMap<>();
        headersToUse.put("simpMessageType", SimpMessageType.MESSAGE);
        headersToUse.put("destination", dest);
        headersToUse.put("contentType", "text/plain;charset=UTF-8");
        headersToUse.put("stompCommand", "SEND");
        
        Map<String, Object> nativeHeaders = new LinkedHashMap<>();
        nativeHeaders.put("id", "sub-0");
        nativeHeaders.put("destination", dest);
        headersToUse.put("nativeHeaders", nativeHeaders);
        messageHeaders = new MessageHeaders(headersToUse);
        MessageHeaders oldHeaders = oldMessage.getHeaders();
        
        MessageConverter converter = new SimpleMessageConverter();
        Message<?> message = converter instanceof SmartMessageConverter ? ((SmartMessageConverter) converter).toMessage(payload, messageHeaders, conversionHint)
                : converter.toMessage(payload, messageHeaders);
        return message;
    }
    
}

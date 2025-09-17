/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuNodeWebSocketProcessor.java
 * Package     : com.paradisecloud.sync.model.cache
 * @author lilinhai 
 * @since 2020-12-11 14:05
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.model;

import java.util.List;

import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.websocket.callinfo.CallInfoUpdate;
import com.paradisecloud.fcm.fme.model.websocket.callinfo.CallInfoUpdateMessage;
import com.paradisecloud.fcm.fme.model.websocket.callinfo.CallInfoUpdateResponse;
import com.paradisecloud.fcm.fme.model.websocket.calllist.CallListUpdate;
import com.paradisecloud.fcm.fme.model.websocket.calllist.CallListUpdateMessage;
import com.paradisecloud.fcm.fme.model.websocket.calllist.CallListUpdateResponse;
import com.paradisecloud.fcm.fme.model.websocket.enumer.MessageType;
import com.paradisecloud.fcm.fme.model.websocket.enumer.RequestType;
import com.paradisecloud.fcm.fme.model.websocket.message.MessageAck;
import com.paradisecloud.fcm.fme.model.websocket.message.MessageAckResponse;
import com.paradisecloud.fcm.fme.model.websocket.roster.RosterUpdateMessage;
import com.paradisecloud.fcm.fme.model.websocket.roster.RosterUpdateResponse;
import com.paradisecloud.fcm.fme.model.websocket.subscription.Subscription;
import com.paradisecloud.fcm.fme.model.websocket.subscription.SubscriptionRequest;
import com.paradisecloud.fcm.fme.model.websocket.subscription.SubscriptionRequestConfigurator;
import com.paradisecloud.fcm.fme.model.websocket.subscription.SubscriptionUpdateResponse;
import com.paradisecloud.fcm.fme.websocket.core.WebsocketClient;
import com.paradisecloud.fcm.fme.websocket.interfaces.IWebSocketService;
import com.paradisecloud.fcm.fme.websocket.model.callinfo.CallInfoProcessorMessageQueue;
import com.paradisecloud.fcm.fme.websocket.model.callinfo.message.CallInfoProcessorRemoveMessage;

public class WebSocketBusiProcessor
{
    
    /**
     * web socket客户端连接对象
     */
    private WebsocketClient webSocketClient;
    
    /**
     * 连接信息对象缓存
     */
    private FmeBridge fmeBridge;
    
    /**
     * websocket业务服务
     */
    private IWebSocketService webSocketService;
    
    /**
     * websocket消息订阅配置器
     */
    private SubscriptionRequestConfigurator subscriptionRequestConfigurator;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2020-12-11 14:16 
     * @param webSocketClient
     * @param fmeBridge
     * @param webSocketService 
     */
    public WebSocketBusiProcessor(WebsocketClient webSocketClient, FmeBridge fmeBridge, IWebSocketService webSocketService)
    {
        this.webSocketClient = webSocketClient;
        this.fmeBridge = fmeBridge;
        this.webSocketService = webSocketService;
        subscriptionRequestConfigurator = new SubscriptionRequestConfigurator();
    }
    
    /**
     * <pre>发起首次消息订阅</pre>
     * @author lilinhai
     * @since 2020-12-11 14:25  void
     */
    public void sendFirstSubscriptionRequest()
    {
        try
        {
            // 初始化订阅参数
            String jsonMsg = JSON.toJSONString(subscriptionRequestConfigurator.newSubscriptionRequestBody());
            this.webSocketClient.send(jsonMsg);
            fmeBridge.getFmeLogger().logWebsocketInfo("【interactive】client--->server 首次发起消息订阅成功: " + jsonMsg, true);
        }
        catch (WebsocketNotConnectedException e) 
        {
            throw e;
        }
        catch (Throwable e)
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("sendFirstSubscriptionRequest error: ", true, e);
        }
    }
    
    /**
     * <pre>处理消息</pre>
     * @author lilinhai
     * @since 2020-12-11 13:51 
     * @param message void
     */
    public void processMessage(String message)
    {
        fmeBridge.getFmeLogger().logWebsocketInfo("【interactive】server--->client" + message, false);
        JSONObject jsonObj = JSONObject.parseObject(message);
        if (jsonObj != null)
        {
            String type = jsonObj.getString("type");
            
            // 如果是消息确认类型，则进行确认
            if (RequestType.MESSAGE_ACK.getValue().equals(type))
            {
                JSONObject messageAckObj = jsonObj.getJSONObject("messageAck");
                if (messageAckObj.getIntValue("messageId") == SubscriptionRequestConfigurator.getStartMessageId())
                {
                    if (messageAckObj.getString("status").equals("success"))
                    {
                        fmeBridge.getFmeLogger().logWebsocketInfo("【interactive】server--->client. The 'message subscription request' sent has been successfully understood and executed by the websocket server!", true);
                    }
                    else
                    {
                        fmeBridge.getFmeLogger().logWebsocketInfo("【interactive】server--->client. Outgoing message subscription processing failed", true, true);
                    }
                }
                else
                {
                    fmeBridge.getFmeLogger().logWebsocketInfo("【interactive】server--->client. An unknown messageack message was received: " + message, true, true);
                }
            }
            // 如果是消息类型则，根据消息类别分别进行处理
            else if (RequestType.MESSAGE.getValue().equals(type))
            {
                JSONObject messageObj = jsonObj.getJSONObject("message");
                
                // 消息需要确认（必须及时回复服务端，即确认，否则会出现websocket服务端永久不响应问题）
                doMessageAck(messageObj.getIntValue("messageId"));
                
                // 消息业务处理
                doMessageService(jsonObj, messageObj.getString("type"));
            }
            else
            {
                fmeBridge.getFmeLogger().logWebsocketInfo("Unresolved message 2: " + message, true, true);
            }
        }
        else
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("Unresolved message 1: " + message, true, true);
        }
    }
    
    /**
     * <p>Get Method   :   apiUtilNew ApiUtilNew</p>
     * @return apiUtilNew
     */
    public FmeBridge getFmeBridge()
    {
        return fmeBridge;
    }
    
    /**
     * <pre>消息业务处理</pre>
     * @author lilinhai
     * @since 2020-12-11 12:59 
     * @param messageObj void
     */
    private void doMessageService(JSONObject messageObj, String messageType)
    {
        try
        {
            // 订阅更新（websocket会话建立确认分支）
            if (MessageType.SUBSCRIPTION_UPDATE.getValue().equals(messageType))
            {
                SubscriptionUpdateResponse subscriptionUpdateResponse = messageObj.toJavaObject(SubscriptionUpdateResponse.class);
                
                // remove子订阅后，修改订阅请求
                removeDeactivatedSubscription(subscriptionUpdateResponse);
            }
            // 会议列表更新(业务层面分支)
            else if (MessageType.CALL_LIST_UPDATE.getValue().equals(messageType))
            {
                CallListUpdateResponse callListUpdateResponse = messageObj.toJavaObject(CallListUpdateResponse.class);
                
                // 业务处理(移除，添加，修改)
                webSocketService.process(callListUpdateResponse.getMessage(), messageObj, fmeBridge);
                
                // 增加子订阅，更新订阅请求
                processCallListUpdateMessage(callListUpdateResponse);
            }
            // 会议室详情更新(业务层面分支)
            else if (MessageType.CALL_INFO_UPDATE.getValue().equals(messageType))
            {
                CallInfoUpdateResponse callInfoUpdateResponse = messageObj.toJavaObject(CallInfoUpdateResponse.class);
                CallInfoUpdateMessage callInfoUpdateMessage = callInfoUpdateResponse.getMessage();
                SubscriptionRequest sr = subscriptionRequestConfigurator.getSubscriptionRequestBody().getMessage().getSubscriptionByIndex(callInfoUpdateMessage.getSubscriptionIndex());
                if (sr != null)
                {
                    CallInfoUpdate callInfo = callInfoUpdateMessage.getCallInfo();
                    callInfo.setCall(sr.getCall());
                    
                    // 业务处理(修改)
                    webSocketService.process(callInfoUpdateMessage, messageObj, fmeBridge);
                }
                else
                {
                    fmeBridge.getFmeLogger().logWebsocketInfo("Can't find SubscriptionRequest, subscriptionIndex：" + callInfoUpdateMessage.getSubscriptionIndex(), true, true);
                }
            }
            // 与会者更新(业务层面分支)
            else if (MessageType.ROSTER_UPDATE.getValue().equals(messageType))
            {
                RosterUpdateResponse rosterUpdateResponse = messageObj.toJavaObject(RosterUpdateResponse.class);
                RosterUpdateMessage rosterUpdateMessage = rosterUpdateResponse.getMessage();
                
                // 业务处理(与会者修改)
                webSocketService.process(rosterUpdateMessage, messageObj, fmeBridge);
            }
            else
            {
                fmeBridge.getFmeLogger().logWebsocketInfo("Unrecognized message type：" + messageType + ", message: " + messageObj, true, true);
            }
        }
        catch (Throwable e)
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("Business processing failed：", true, e);
        }
    }
    
    /**
     * <pre>增加子订阅，更新订阅请求</pre>
     * @author lilinhai
     * @since 2020-12-11 10:14 
     * @param updates void
     */
    private void processCallListUpdateMessage(CallListUpdateResponse callListUpdateResponse)
    {
        CallListUpdateMessage callListUpdateMessage = callListUpdateResponse.getMessage();
        List<CallListUpdate> updates = callListUpdateMessage.getUpdates();
        
        for (CallListUpdate callsListUpdate : updates)
        {
            if (callsListUpdate.getUpdateType().equals("add")
                    || callsListUpdate.getUpdateType().equals("update"))
            {
                subscriptionRequestConfigurator.addSubscription(callsListUpdate.getCall());
            }
            else if (callsListUpdate.getUpdateType().equals("remove"))
            {
                subscriptionRequestConfigurator.removeSubscription(callsListUpdate.getCall());
                fmeBridge.getFmeLogger().logWebsocketInfo("The subscription has updates. Remove the sub message subscription, call: " + callsListUpdate.getCall(), true);
            }
            else
            {
                fmeBridge.getFmeLogger().logWebsocketInfo("Unrecognized updatetype for calllistupdate：" + callsListUpdate.getUpdateType(), true, true);
            }
        }
        
        if (subscriptionRequestConfigurator.isUpdate())
        {
            subscriptionRequestConfigurator.setUpdate(false);
            String jsonMsg = JSON.toJSONString(subscriptionRequestConfigurator.getSubscriptionRequestBody());
            this.webSocketClient.send(jsonMsg);
            fmeBridge.getFmeLogger().logWebsocketInfo("The subscription has updates, and the message subscription has been re initiated: " + jsonMsg, true);
        }
    }
    
    /**
     * <pre>remove Deactivated子订阅</pre>
     * @author lilinhai
     * @since 2020-12-11 10:13 
     * @param message void
     */
    private void removeDeactivatedSubscription(SubscriptionUpdateResponse subscriptionUpdateResponse)
    {
        List<Subscription> subscriptions = subscriptionUpdateResponse.getMessage().getSubscriptions();
        if (!ObjectUtils.isEmpty(subscriptions))
        {
            for (Subscription subscription : subscriptions)
            {
                // 如果订阅失效，则移除该订阅
                if (subscription.getState().equals("deactivated"))
                {
                    SubscriptionRequest sr = subscriptionRequestConfigurator.getSubscriptionRequestBody().getMessage().getSubscriptionByIndex(subscription.getIndex().intValue());
                    if (sr != null)
                    {
                        subscriptionRequestConfigurator.removeSubscription(sr.getCall());
                        fmeBridge.getFmeLogger().logWebsocketInfo("The subscription has updates. The invalid sub subscription was successfully removed, call: " + sr, true);
                        JSONObject updateItem = new JSONObject();
                        updateItem.put("call", sr.getCall());
                        CallInfoProcessorMessageQueue.getInstance().put(new CallInfoProcessorRemoveMessage(fmeBridge, updateItem));
                    }
                    else
                    {
                        fmeBridge.getFmeLogger().logWebsocketInfo(" The subscription request object could not be found: " + subscription.getIndex().intValue(), true);
                    }
                }
            }
        }
    }
    
    /**
     * <pre>处理消息确认</pre>
     * @author lilinhai
     * @since 2020-12-10 15:54 
     * @param messageId void
     */
    private void doMessageAck(int messageId)
    {
        MessageAckResponse messageAckResponse = new MessageAckResponse();
        messageAckResponse.setType(RequestType.MESSAGE_ACK.getValue());
        MessageAck ma = new MessageAck();
        ma.setMessageId(messageId);
        ma.setStatus("success");
        messageAckResponse.setMessageAck(ma);
        
        fmeBridge.getFmeLogger().logWebsocketInfo("【interactive】client--->server, doMessageAck sends a confirmation message to the websocket server: " + messageId, false);
        this.webSocketClient.send(JSON.toJSONString(messageAckResponse));
    }
}

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SubscriptionRequestConfiguration.java
 * Package     : com.paradisecloud.fcm.fme.model.websocket.subscription
 * @author lilinhai 
 * @since 2021-03-25 15:12
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.websocket.subscription;

import com.paradisecloud.fcm.fme.model.websocket.callinfo.CallInfoUpdate;
import com.paradisecloud.fcm.fme.model.websocket.calllist.CallListUpdate;
import com.paradisecloud.fcm.fme.model.websocket.enumer.MessageType;
import com.paradisecloud.fcm.fme.model.websocket.enumer.RequestType;
import com.paradisecloud.fcm.fme.model.websocket.enumer.SubscriptionType;
import com.paradisecloud.fcm.fme.model.websocket.roster.RosterUpdate;

/**  
 * <pre>订阅消息配置器</pre>
 * @author lilinhai
 * @since 2021-03-25 15:12
 * @version V1.0  
 */
public class SubscriptionRequestConfigurator
{
    
    private static final int START_MESSAGE_ID = 10;
    
    private static final int SUBSCRIPTION_INDEX_START = 10000;
    
    /**
     * 子订阅index
     */
    private int index = SUBSCRIPTION_INDEX_START;
    
    /**
     * 订阅是否有更新（即是否需要重新发起）
     */
    private boolean isUpdate;
    
    /**
     * 订阅请求body
     */
    private SubscriptionRequestBody subscriptionRequestBody;

    /**
     * <p>Get Method   :   isUpdate boolean</p>
     * @return isUpdate
     */
    public boolean isUpdate()
    {
        return isUpdate;
    }

    /**
     * <p>Set Method   :   isUpdate boolean</p>
     * @param isUpdate
     */
    public void setUpdate(boolean isUpdate)
    {
        this.isUpdate = isUpdate;
    }

    /**
     * <p>Get Method   :   subscriptionRequestBody SubscriptionRequestBody</p>
     * @return subscriptionRequestBody
     */
    public SubscriptionRequestBody getSubscriptionRequestBody()
    {
        return subscriptionRequestBody;
    }

    /**
     * <p>Get Method   :   START_MESSAGE_ID int</p>
     * @return startMessageId
     */
    public static int getStartMessageId()
    {
        return START_MESSAGE_ID;
    }
    
    /**
     * <p>Get Method   :   SUBSCRIPTION_INDEX_START int</p>
     * @return subscriptionIndexStart
     */
    public static int getSubscriptionIndexStart()
    {
        return SUBSCRIPTION_INDEX_START;
    }

    public SubscriptionRequestBody newSubscriptionRequestBody()
    {
        // 构建 json 订阅参数
        SubscriptionRequest calls = buildSubscriptionRequest(index, SubscriptionType.CALLS.getValue(), null, CallListUpdate.getAllFieldNames());
        subscriptionRequestBody = buildSubscriptionRequestBody(calls);
        return subscriptionRequestBody;
    }
    
    public void addSubscription(String callId)
    {
        if (!isExistSubscriptionRequest(callId + "_" + SubscriptionType.CALL_INFO.getValue()))
        {
            SubscriptionRequest sr = buildSubscriptionRequest(++index, SubscriptionType.CALL_INFO.getValue(), callId, CallInfoUpdate.getAllFieldNames());
            subscriptionRequestBody.getMessage().addSubscription(sr);
            this.setUpdate(true);
        }
        
        if (!isExistSubscriptionRequest(callId + "_" + SubscriptionType.CALL_ROSTER.getValue()))
        {
            SubscriptionRequest sr = buildSubscriptionRequest(++index, SubscriptionType.CALL_ROSTER.getValue(), callId, RosterUpdate.getAllFieldNames());
            subscriptionRequestBody.getMessage().addSubscription(sr);
            this.setUpdate(true);
        }
    }
    
    public void removeSubscription(String callId)
    {
        int c = subscriptionRequestBody.getMessage().removeByCall(callId);
        this.setUpdate(c > 0);
    }
    
    
    /**
     * 是否存在自定义
     * @author lilinhai
     * @since 2021-03-25 15:25 
     * @param subscriptionRequestKey
     * @return boolean
     */
    private boolean isExistSubscriptionRequest(String subscriptionRequestKey)
    {
        return subscriptionRequestBody.getMessage().getSubscriptionByKey(subscriptionRequestKey) != null;
    }
    
    /**
     * <pre>构建订阅参数</pre>
     * @author lilinhai
     * @since 2020-12-11 14:25 
     * @param subscriptionRequests
     * @return SubscriptionRequestParam
     */
    private SubscriptionRequestBody buildSubscriptionRequestBody(SubscriptionRequest subscriptionRequest)
    {
        SubscriptionRequestBody requestParam = new SubscriptionRequestBody();
        requestParam.setType(RequestType.MESSAGE.getValue());
        
        // 参数message
        SubscriptionRequestMessage requestMessage = new SubscriptionRequestMessage();
        requestMessage.setMessageId(START_MESSAGE_ID);
        requestMessage.setType(MessageType.SUBSCRIBE_REQUEST.getValue());
        
        // 订阅列表信息
        requestMessage.addSubscription(subscriptionRequest);
        requestParam.setMessage(requestMessage);
        return requestParam;
    }
    
    /**
     * <pre>构建订阅请求</pre>
     * @author lilinhai
     * @since 2020-12-11 14:24 
     * @param index
     * @param type
     * @param callId
     * @param elements
     * @return SubscriptionRequest
     */
    private SubscriptionRequest buildSubscriptionRequest(Integer index, String type, String callId, String[] elements)
    {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setIndex(index);
        subscriptionRequest.setType(type);
        subscriptionRequest.setElements(elements);
        if (callId != null)
        {
            subscriptionRequest.setCall(callId);
        }
        return subscriptionRequest;
    }
}

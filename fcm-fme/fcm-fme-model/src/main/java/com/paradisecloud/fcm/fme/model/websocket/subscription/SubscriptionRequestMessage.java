package com.paradisecloud.fcm.fme.model.websocket.subscription;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 订阅请求 message
 *
 * @author zt1994 2019/8/30 15:47
 */
public class SubscriptionRequestMessage implements Serializable
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * 
     * @since 2020-12-11 18:00
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息id
     */
    private Integer messageId;
    
    /**
     * 消息类型
     */
    private String type;
    
    /**
     * 订阅列表信息
     */
    private List<SubscriptionRequest> subscriptions = new ArrayList<>();
    
    private Map<Integer, SubscriptionRequest> map = new ConcurrentHashMap<>();
    private Map<String, SubscriptionRequest> map1 = new ConcurrentHashMap<>();

    /**
     * <p>Get Method   :   messageId Integer</p>
     * @return messageId
     */
    public Integer getMessageId()
    {
        return messageId;
    }

    /**
     * <p>Set Method   :   messageId Integer</p>
     * @param messageId
     */
    public void setMessageId(Integer messageId)
    {
        this.messageId = messageId;
    }

    /**
     * <p>Get Method   :   type String</p>
     * @return type
     */
    public String getType()
    {
        return type;
    }

    /**
     * <p>Set Method   :   type String</p>
     * @param type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * <p>Get Method   :   subscriptions List<SubscriptionRequest></p>
     * @return subscriptions
     */
    public List<SubscriptionRequest> getSubscriptions()
    {
        return subscriptions;
    }
    
    /**
     * <p>Get Method   :   subscriptions List<SubscriptionRequest></p>
     * @return subscriptions
     */
    @JSONField(serialize = false)
    public SubscriptionRequest getSubscriptionByIndex(int index)
    {
        return map.get(index);
    }
    
    /**
     * <p>Get Method   :   subscriptions List<SubscriptionRequest></p>
     * @return subscriptions
     */
    @JSONField(serialize = false)
    public SubscriptionRequest getSubscriptionByKey(String key)
    {
        return map1.get(key);
    }
    
    public int removeByCall(String callId)
    {
        int c = 0;
        for (SubscriptionRequest subscriptionRequest : new ArrayList<>(subscriptions))
        {
            if (callId.equals(subscriptionRequest.getCall()))
            {
                removeSubscription(subscriptionRequest);
                c++;
            }
        }
        return c;
    }

    /**
     * <p>Set Method   :   subscriptions List<SubscriptionRequest></p>
     * @param subscriptions
     */
    public SubscriptionRequest removeSubscription(SubscriptionRequest subscriptionRequest)
    {
        this.subscriptions.remove(subscriptionRequest);
        if (subscriptionRequest.getCall() != null)
        {
            this.map1.remove(subscriptionRequest.getCall() + "_" + subscriptionRequest.getType());
        }
        return this.map.remove(subscriptionRequest.getIndex());
    }
    
    /**
     * <p>Set Method   :   subscriptions List<SubscriptionRequest></p>
     * @param subscriptions
     */
    public void addSubscription(SubscriptionRequest subscriptionRequest)
    {
        this.subscriptions.add(subscriptionRequest);
        this.map.put(subscriptionRequest.getIndex(), subscriptionRequest);
        if (subscriptionRequest.getCall() != null)
        {
            this.map1.put(subscriptionRequest.getCall() + "_" + subscriptionRequest.getType(), subscriptionRequest);
        }
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-09-12 23:00 
     * @return
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(messageId);
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-09-12 23:00 
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SubscriptionRequestMessage other = (SubscriptionRequestMessage) obj;
        return Objects.equals(messageId, other.messageId);
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-09-12 23:00 
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "SubscriptionRequestMessage [messageId=" + messageId + ", type=" + type + "]";
    }
}

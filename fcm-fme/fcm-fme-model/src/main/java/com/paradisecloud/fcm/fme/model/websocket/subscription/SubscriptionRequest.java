package com.paradisecloud.fcm.fme.model.websocket.subscription;

import java.io.Serializable;
import java.util.Objects;

/**
 * 订阅请求参数
 *
 * @author zt1994 2019/8/30 15:39
 */
public class SubscriptionRequest implements Serializable
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * 
     * @since 2020-12-11 18:00
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 订阅索引
     */
    private Integer index;
    
    /**
     * 订阅类型 calls callInfo callRoster
     */
    private String type;
    
    /**
     * 订阅 call id 注意：类型为 calls 时不需要
     */
    private String call;
    
    /**
     * 订阅参数
     */
    private String[] elements;

    /**
     * <p>Get Method   :   index Integer</p>
     * @return index
     */
    public Integer getIndex()
    {
        return index;
    }

    /**
     * <p>Set Method   :   index Integer</p>
     * @param index
     */
    public void setIndex(Integer index)
    {
        this.index = index;
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
     * <p>Get Method   :   call String</p>
     * @return call
     */
    public String getCall()
    {
        return call;
    }

    /**
     * <p>Set Method   :   call String</p>
     * @param call
     */
    public void setCall(String call)
    {
        this.call = call;
    }

    /**
     * <p>Get Method   :   elements String[]</p>
     * @return elements
     */
    public String[] getElements()
    {
        return elements;
    }

    /**
     * <p>Set Method   :   elements String[]</p>
     * @param elements
     */
    public void setElements(String[] elements)
    {
        this.elements = elements;
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-09-12 23:11 
     * @return
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(call, index, type);
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-09-12 23:11 
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
        SubscriptionRequest other = (SubscriptionRequest) obj;
        return Objects.equals(call, other.call) && Objects.equals(index, other.index) && Objects.equals(type, other.type);
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-09-12 23:03 
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "SubscriptionRequest [index=" + index + ", type=" + type + ", call=" + call + "]";
    }
}

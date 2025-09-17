/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceContextMessage.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.message.conference
 * @author lilinhai 
 * @since 2021-03-08 13:53
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;

/**  
 * <pre>会议上下文消息</pre>
 * @author lilinhai
 * @since 2021-03-08 13:53
 * @version V1.0  
 */
public abstract class ConferenceContextMessage
{
    
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 未呼入的会议上下文
     */
    protected ConferenceContext conferenceContext;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-08 14:15 
     * @param conferenceContext 
     */
    protected ConferenceContextMessage(ConferenceContext conferenceContext)
    {
        super();
        this.conferenceContext = conferenceContext;
    }

    /**
     * <p>Get Method   :   conferenceContext ConferenceContext</p>
     * @return conferenceContext
     */
    public ConferenceContext getConferenceContext()
    {
        return conferenceContext;
    }
    
    public abstract boolean isNeedProcess();
    
    public abstract void process();

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author lilinhai
     * @since 2021-03-11 20:32 
     * @return
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((conferenceContext == null) ? 0 : conferenceContext.hashCode());
        return result;
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author lilinhai
     * @since 2021-03-11 20:32 
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
        ConferenceContextMessage other = (ConferenceContextMessage) obj;
        if (conferenceContext == null)
        {
            if (other.conferenceContext != null) return false;
        }
        else if (!conferenceContext.equals(other.conferenceContext)) return false;
        return true;
    }
}

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:35
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.updateprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;

/**  
 * <pre>参会者处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:35
 * @version V1.0  
 */
public abstract class AttendeeProcessor
{
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    protected Participant participant;
    
    protected ConferenceContext conferenceContext;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-04 14:44 
     * @param participant
     * @param conferenceContext 
     */
    protected AttendeeProcessor(Participant participant, ConferenceContext conferenceContext)
    {
        super();
        this.participant = participant;
        this.conferenceContext = conferenceContext;
    }



    public abstract void process();
}

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:25
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smc2.conference.updateprocess;

import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * <pre>参会者处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:25
 * @version V1.0  
 */
public abstract class AttendeeProcessor
{
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    protected SmcParitipantsStateRep.ContentDTO participant;
    
    protected Smc2ConferenceContext conferenceContext;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 14:44 
     * @param participant
     * @param conferenceContext 
     */
    protected AttendeeProcessor(SmcParitipantsStateRep.ContentDTO participant, Smc2ConferenceContext conferenceContext)
    {
        super();
        this.participant = participant;
        this.conferenceContext = conferenceContext;
    }



    public abstract void process();
}

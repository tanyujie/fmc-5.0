/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:35
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.updateprocessor;

import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * <pre>参会者处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:35
 * @version V1.0  
 */
public abstract class AttendeeProcessor
{
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    protected SmcParitipantsStateRep.ContentDTO participant;
    
    protected Smc3ConferenceContext conferenceContext;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-04 14:44 
     * @param participant
     * @param conferenceContext 
     */
    protected AttendeeProcessor(SmcParitipantsStateRep.ContentDTO participant, Smc3ConferenceContext conferenceContext)
    {
        super();
        this.participant = participant;
        this.conferenceContext = conferenceContext;
    }



    public abstract void process();
}

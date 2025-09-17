/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUpdateProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:13
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.updateprocessor;

import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;

/**  
 * <pre>参会者更新处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:13
 * @version V1.0  
 */
public abstract class AttendeeUpdateProcessor extends AttendeeProcessor
{
    
    protected Attendee attendee;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-19 15:11 
     * @param participant
     * @param a
     * @param conferenceContext
     * @param mainConferenceContext
     */
    protected AttendeeUpdateProcessor(Participant participant, Attendee a, ConferenceContext conferenceContext)
    {
        super(participant, conferenceContext);
        this.attendee = a;
    }
}

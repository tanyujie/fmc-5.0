/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUpdateProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:12
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smc2.conference.updateprocess;


import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;

/**
 * <pre>参会者更新处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:12
 * @version V1.0  
 */
public abstract class AttendeeUpdateProcessor extends AttendeeProcessor
{
    
    protected AttendeeSmc2 attendee;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-19 15:11 
     * @param participant
     * @param a
     * @param conferenceContext
     */
    protected AttendeeUpdateProcessor(SmcParitipantsStateRep.ContentDTO participant, AttendeeSmc2 a, Smc2ConferenceContext conferenceContext)
    {
        super(participant, conferenceContext);
        this.attendee = a;
    }
}

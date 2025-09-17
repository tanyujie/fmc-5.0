/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUpdateProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:13
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.updateprocessor;

import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;

/**  
 * <pre>参会者更新处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:13
 * @version V1.0  
 */
public abstract class AttendeeUpdateProcessor extends AttendeeProcessor
{
    
    protected AttendeeSmc3 attendee;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-19 15:11 
     * @param participant
     * @param a
     * @param conferenceContext
     */
    protected AttendeeUpdateProcessor(SmcParitipantsStateRep.ContentDTO participant, AttendeeSmc3 a, Smc3ConferenceContext conferenceContext)
    {
        super(participant, conferenceContext);
        this.attendee = a;
    }
}

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUpdateProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:12
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.ding.templateConference.updateprocess;


import com.paradisecloud.fcm.ding.busi.attende.AttendeeDing;
import com.paradisecloud.fcm.ding.cache.DingConferenceContext;
import com.paradisecloud.fcm.ding.model.SmcParitipantsStateRep;

/**
 * <pre>参会者更新处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:12
 * @version V1.0  
 */
public abstract class AttendeeUpdateProcessor extends AttendeeProcessor
{
    
    protected AttendeeDing attendee;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-19 15:11 
     * @param participant
     * @param a
     * @param conferenceContext
     */
    protected AttendeeUpdateProcessor(SmcParitipantsStateRep.ContentDTO participant, AttendeeDing a, DingConferenceContext conferenceContext)
    {
        super(participant, conferenceContext);
        this.attendee = a;
    }
}

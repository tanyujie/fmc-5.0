/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUpdateProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:12
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.tencent.templateConference.updateprocess;


import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.tencent.model.reponse.RealTimeParticipantsResponse;

/**
 * <pre>参会者更新处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:12
 * @version V1.0  
 */
public abstract class AttendeeUpdateProcessor extends AttendeeProcessor
{
    
    protected AttendeeTencent attendee;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-19 15:11 
     * @param participant
     * @param a
     * @param conferenceContext
     */
    protected AttendeeUpdateProcessor(SmcParitipantsStateRep.ContentDTO participant, AttendeeTencent a, TencentConferenceContext conferenceContext)
    {
        super(participant, conferenceContext);
        this.attendee = a;
    }
}

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:25
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.tencent.templateConference.updateprocess;

import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep;
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
    
    protected TencentConferenceContext conferenceContext;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-04 14:44 
     * @param participant
     * @param conferenceContext 
     */
    protected AttendeeProcessor(SmcParitipantsStateRep.ContentDTO participant, TencentConferenceContext conferenceContext)
    {
        super();
        this.participant = participant;
        this.conferenceContext = conferenceContext;
    }



    public abstract void process();
}

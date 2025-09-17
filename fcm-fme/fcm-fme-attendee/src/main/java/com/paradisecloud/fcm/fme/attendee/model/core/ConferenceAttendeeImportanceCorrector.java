/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceAttendeeImportanceCorrector.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author sinhy 
 * @since 2021-08-23 12:35
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.core;

import java.util.ArrayList;
import java.util.List;

import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.sinhy.spring.BeanFactory;

class ConferenceAttendeeImportanceCorrector
{
    
    private ConferenceContext conferenceContext;

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-08-23 12:35 
     * @param conferenceContext 
     */
    public ConferenceAttendeeImportanceCorrector(ConferenceContext conferenceContext)
    {
        super();
        this.conferenceContext = conferenceContext;
    }
    
    public void correct()
    {
        List<Attendee> operatedAttendees = new ArrayList<>(conferenceContext.getAttendeeOperation().getOperatedAttendees());
        if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined())
        {
            // 校正主会场权重值，使其不能为空
            if (!operatedAttendees.contains(conferenceContext.getMasterAttendee()) || (conferenceContext.getMasterAttendee().getImportance() == null))
            {
                if (!AttendeeImportance.MASTER.is(conferenceContext.getMasterAttendee().getImportance()) && !conferenceContext.getMasterAttendee().isOtherImportance())
                {
                    FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(conferenceContext.getMasterAttendee());
                    if (fmeBridge != null)
                    {
                        fmeBridge.getParticipantInvoker().updateParticipant(conferenceContext.getMasterAttendee().getParticipantUuid(), new ParticipantParamBuilder()
                                .importance(AttendeeImportance.MASTER.getStartValue())
                                .build());
                    }
                }
            }
            
            if (!operatedAttendees.contains(conferenceContext.getMasterAttendee()))
            {
                operatedAttendees.add(conferenceContext.getMasterAttendee());
            }
        }
        operatedAttendees.addAll(conferenceContext.getAttendeeOperation().getOperatedAttendeesForOthers());
        BeanFactory.getBean(IAttendeeService.class).updateAttendeeImportance(conferenceContext, AttendeeImportance.COMMON, operatedAttendees.toArray(new Attendee[0]));
    }
}

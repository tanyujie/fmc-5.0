/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : GetCallLegByParticipantUuid.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.pm.attendee
 * @author sinhy 
 * @since 2021-09-17 21:09
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.pm.calleg;

import java.lang.reflect.Method;

import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;

public class GetCallLegByAttendee extends GetCallLegByParticipantUuid
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-17 21:09 
     * @param method 
     */
    protected GetCallLegByAttendee(Method method)
    {
        super(method);
    }
    
    public CallLeg getCallLeg(Attendee attendee)
    {
        if (!attendee.isMeetingJoined())
        {
            return null;
        }
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(attendee);
        if (fmeBridge == null)
        {
            return null;
        }
        Participant p = fmeBridge.getDataCache().getParticipantByUuid(attendee.getParticipantUuid());
        if (p == null)
        {
            fmeBridge.getFmeLogger().logInfo("Participant not found in datacache: " + attendee.getParticipantUuid(), true, true);
            return null;
        }
        
        CallLeg callLeg = p.getCallLeg();
        if (callLeg == null)
        {
            callLeg = getCallLegByParticipantUuid(fmeBridge, p);
        }
        
        return callLeg;
    }
    
}

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

import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.fme.model.response.callleg.CallLegInfoResponse;
import com.paradisecloud.fcm.fme.model.response.callleg.CallLegsResponse;
import com.sinhy.proxy.ProxyMethod;

public class GetCallLegByParticipantUuid extends ProxyMethod
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-17 21:09 
     * @param method 
     */
    protected GetCallLegByParticipantUuid(Method method)
    {
        super(method);
    }
    
    public CallLeg getCallLegByParticipantUuid(FmeBridge fmeBridge, Participant participant)
    {
        if (participant.getCallLeg() != null)
        {
            CallLegInfoResponse callLegInfoResponse = fmeBridge.getCallLegInvoker().getCallLeg(participant.getCallLeg().getId());
            if (callLegInfoResponse != null)
            {
                return callLegInfoResponse.getCallLeg();
            }
        }
        CallLegsResponse callLegInfoResponse = fmeBridge.getCallLegInvoker().getCallLegs(participant.getId());
        if (callLegInfoResponse != null 
                && callLegInfoResponse.getCallLegs() != null
                && callLegInfoResponse.getCallLegs().getCallLeg() != null
                && !callLegInfoResponse.getCallLegs().getCallLeg().isEmpty())
        {
            for (CallLeg callLeg : callLegInfoResponse.getCallLegs().getCallLeg())
            {
                CallLegInfoResponse infoResponse = fmeBridge.getCallLegInvoker().getCallLeg(callLeg.getId());
                if (infoResponse != null && infoResponse.getCallLeg() != null)
                {
                    return infoResponse.getCallLeg();
                }
            }
        }
        
        fmeBridge.getFmeLogger().logWebsocketInfo("Attendee callleg not found：" + participant, true);
        return null;
    }
    
}

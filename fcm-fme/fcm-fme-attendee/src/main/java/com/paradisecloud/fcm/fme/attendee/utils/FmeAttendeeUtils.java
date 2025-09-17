/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeAttendeeUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author lilinhai 
 * @since 2021-03-03 19:13
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.utils;

import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.UpFmeAttendee;
import com.paradisecloud.fcm.fme.model.busi.cascade.Cascade.FmeAttendeeProcessor;
import com.paradisecloud.fcm.fme.model.busi.cascade.UpCascade;
import com.paradisecloud.fcm.fme.model.busi.cascade.UpCascade.UpFmeAttendeeProcessor;

/**  
 * <pre>FME参会者工具类</pre>
 * @author lilinhai
 * @since 2021-03-03 19:13
 * @version V1.0  
 */
public class FmeAttendeeUtils
{
    
    public static void processUpFmeAttendee(ConferenceContext subConferenceContext, ConferenceContext conferenceContext, UpFmeAttendeeProcessor upFmeAttendeeProcessor)
    {
        process(subConferenceContext, conferenceContext, (UpFmeAttendee upFmeAttendee, FmeAttendee fmeAttendee)->{
            upFmeAttendeeProcessor.process(upFmeAttendee);
        });
    }
    
    public static void processFmeAttendee(ConferenceContext subConferenceContext, ConferenceContext conferenceContext, FmeAttendeeProcessor fmeAttendeeProcessor)
    {
        process(subConferenceContext, conferenceContext, (UpFmeAttendee upFmeAttendee, FmeAttendee fmeAttendee)->{
            fmeAttendeeProcessor.process(fmeAttendee);
        });
    }
    
    private static void process(ConferenceContext subConferenceContext, ConferenceContext conferenceContext, _FmeAttendeeProcessor fmeAttendeeProcessor)
    {
        if (subConferenceContext == null || subConferenceContext == conferenceContext)
        {
            return;
        }
        UpCascade upCascade = subConferenceContext.getUpCascade();
        if (upCascade != null)
        {
            upCascade.eachUpFmeAttendee((upFmeAttendee) -> {
                ConferenceContext upCc = ConferenceContextCache.getInstance().get(upFmeAttendee.getContextKey());
                if (upCc != null)
                {
                    FmeAttendee fmeAttendee = upCc.getCascade().get(subConferenceContext.getConferenceNumber());
                    if (fmeAttendee != null)
                    {
                        fmeAttendeeProcessor.process(upFmeAttendee, fmeAttendee);
                        process(upCc, conferenceContext, fmeAttendeeProcessor);
                    }
                }
            });
        }
    }
    
    private static interface _FmeAttendeeProcessor
    {
        void process(UpFmeAttendee upFmeAttendee, FmeAttendee fmeAttendee);
    }
}

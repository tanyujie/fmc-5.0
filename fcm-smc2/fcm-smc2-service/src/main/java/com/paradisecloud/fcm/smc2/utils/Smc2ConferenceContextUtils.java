/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuZjConferenceContextUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author lilinhai 
 * @since 2021-02-22 12:51
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smc2.utils;

import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.McuAttendeeSmc2;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**  
 * <pre>会议上下文工具类</pre>
 * @author lilinhai
 * @since 2021-02-22 12:51
 * @version V1.0  
 */
public class Smc2ConferenceContextUtils
{

    /**
     * 获取默认选看的参会者
     * @author lilinhai
     * @since 2021-02-22 12:52
     * @param mainConferenceContext
     * @return Attendee
     */
    public static AttendeeSmc2 getDefaultChooseToSee(Smc2ConferenceContext mainConferenceContext)
    {
        List<AttendeeSmc2> masterAttendees = mainConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeSmc2 ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }

        List<AttendeeSmc2> attendees = mainConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (Iterator<AttendeeSmc2> iterator = attendees.iterator(); iterator.hasNext();)
            {
                AttendeeSmc2 attendee = iterator.next();
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }

        for (Iterator<Map.Entry<Long, List<AttendeeSmc2>>> iterator = mainConferenceContext.getCascadeAttendeesMap().entrySet().iterator(); iterator.hasNext();)
        {
            Map.Entry<Long, List<AttendeeSmc2>> e = iterator.next();
            attendees = e.getValue();
            if (!ObjectUtils.isEmpty(attendees))
            {
                for (Iterator<AttendeeSmc2> iterator1 = attendees.iterator(); iterator1.hasNext();)
                {
                    AttendeeSmc2 attendee = iterator1.next();
                    if (attendee.isMeetingJoined())
                    {
                        return attendee;
                    }
                }
            }
        }

        return null;
    }

    /**
     * 遍历每一个非FME参会
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachNonMcuAttendeeInConference(Smc2ConferenceContext smc2ConferenceContext, AttendeeProcessor attendeeProcessor)
    {
        processAttendee(smc2ConferenceContext, smc2ConferenceContext.getMasterAttendee(), attendeeProcessor);
        
        List<AttendeeSmc2> masterAttendees = new ArrayList<>(smc2ConferenceContext.getMasterAttendees());
        for (AttendeeSmc2 terminalAttendee : new ArrayList<>(smc2ConferenceContext.getAttendees()))
        {
            processAttendee(smc2ConferenceContext, terminalAttendee, attendeeProcessor);
        }
        
        for (AttendeeSmc2 masterAttendee0 : masterAttendees)
        {
            processAttendee(smc2ConferenceContext, masterAttendee0, attendeeProcessor);
        }

        smc2ConferenceContext.getCascadeAttendeesMap().forEach((dept, attendees) -> {
            
            // 参会者
            for (AttendeeSmc2 attendee : new ArrayList<>(attendees))
            {
                processAttendee(smc2ConferenceContext, attendee, attendeeProcessor);
            }
        });
    }

    /**
     * 遍历每一个非FME参会
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachNonFmeAttendeeInConference(Smc2ConferenceContext conferenceContext, AttendeeProcessor attendeeProcessor)
    {
        processAttendee(conferenceContext, conferenceContext.getMasterAttendee(), attendeeProcessor);

        List<AttendeeSmc2> masterAttendees = new ArrayList<>(conferenceContext.getMasterAttendees());
        for (AttendeeSmc2 terminalAttendee : new ArrayList<>(conferenceContext.getAttendees()))
        {
            processAttendee(conferenceContext, terminalAttendee, attendeeProcessor);
        }

        for (AttendeeSmc2 masterAttendee0 : masterAttendees)
        {
            processAttendee(conferenceContext, masterAttendee0, attendeeProcessor);
        }

        conferenceContext.getCascadeAttendeesMap().forEach((dept, attendees) -> {

            // 参会者
            for (AttendeeSmc2 attendee : new ArrayList<>(attendees))
            {
                processAttendee(conferenceContext, attendee, attendeeProcessor);
            }
        });
    }


    /**
     * 遍历每一个非FME参会
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachFmeAttendeeInConference(Smc2ConferenceContext smc2ConferenceContext, AttendeeProcessor attendeeProcessor)
    {
        List<AttendeeSmc2> masterAttendees = new ArrayList<>(smc2ConferenceContext.getMcuAttendees());
        for (AttendeeSmc2 masterAttendee0 : masterAttendees)
        {
            processAttendee(smc2ConferenceContext, masterAttendee0, attendeeProcessor);
        }
    }
    
    private static void processAttendee(Smc2ConferenceContext smc2ConferenceContext, AttendeeSmc2 a, AttendeeProcessor attendeeProcessor)
    {
        if (a != null && smc2ConferenceContext.getConferenceNumber()!=null)
        {
            attendeeProcessor.process(a);
        }
    }
    
    /**
     * 遍历每一个参会(包含fme参会)
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachAttendeeInConference(Smc2ConferenceContext smc2ConferenceContext, AttendeeProcessor attendeeProcessor)
    {
        for (McuAttendeeSmc2 mcuAttendee : smc2ConferenceContext.getMcuAttendees())
        {
            processAttendee(smc2ConferenceContext, mcuAttendee, attendeeProcessor);
        }
        
        eachNonMcuAttendeeInConference(smc2ConferenceContext, attendeeProcessor);
    }
    
    public static interface AttendeeProcessor
    {
        void process(AttendeeSmc2 attendee);
    }
}

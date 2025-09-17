/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceContextUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author lilinhai 
 * @since 2021-02-22 12:51
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.utils;

import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.McuAttendeeSmc3;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**  
 * <pre>会议上下文工具类</pre>
 * @author lilinhai
 * @since 2021-02-22 12:51
 * @version V1.0  
 */
public class Smc3ConferenceContextUtils
{
    
    /**
     * 获取默认选看的参会者
     * @author lilinhai
     * @since 2021-02-22 12:52 
     * @param mainConferenceContext
     * @return Attendee
     */
    public static AttendeeSmc3 getDefaultChooseToSee(Smc3ConferenceContext mainConferenceContext)
    {
        List<AttendeeSmc3> masterAttendees = mainConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeSmc3 ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        List<AttendeeSmc3> attendees = mainConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (Iterator<AttendeeSmc3> iterator = attendees.iterator(); iterator.hasNext();)
            {
                AttendeeSmc3 attendee = iterator.next();
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        for (Iterator<Entry<Long, List<AttendeeSmc3>>> iterator = mainConferenceContext.getCascadeAttendeesMap().entrySet().iterator(); iterator.hasNext();)
        {
            Entry<Long, List<AttendeeSmc3>> e = iterator.next();
            attendees = e.getValue();
            if (!ObjectUtils.isEmpty(attendees))
            {
                for (Iterator<AttendeeSmc3> iterator1 = attendees.iterator(); iterator1.hasNext();)
                {
                    AttendeeSmc3 attendee = iterator1.next();
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
     * 获取默认主会场
     * @author lilinhai
     * @since 2021-02-22 12:52 
     * @param mainConferenceContext
     * @return Attendee
     */
    public static AttendeeSmc3 getDefaultMasterAttendee(Smc3ConferenceContext mainConferenceContext)
    {
        List<AttendeeSmc3> attendees = mainConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (AttendeeSmc3 attendee : attendees)
            {
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        List<AttendeeSmc3> masterAttendees = mainConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeSmc3 ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (AttendeeSmc3 attendee : attendees)
            {
                return attendee;
            }
        }
        
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeSmc3 ma : masterAttendees)
            {
                return ma;
            }
        }
        return null;
    }
    
    /**
     * 遍历每一个非FME参会
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachNonFmeAttendeeInConference(Smc3ConferenceContext conferenceContext, AttendeeProcessor attendeeProcessor)
    {
        processAttendee(conferenceContext, conferenceContext.getMasterAttendee(), attendeeProcessor);
        
        List<AttendeeSmc3> masterAttendees = new ArrayList<>(conferenceContext.getMasterAttendees());
        for (AttendeeSmc3 terminalAttendee : new ArrayList<>(conferenceContext.getAttendees()))
        {
            processAttendee(conferenceContext, terminalAttendee, attendeeProcessor);
        }
        
        for (AttendeeSmc3 masterAttendee0 : masterAttendees)
        {
            processAttendee(conferenceContext, masterAttendee0, attendeeProcessor);
        }
        
        conferenceContext.getCascadeAttendeesMap().forEach((dept, attendees) -> {
            
            // 参会者
            for (AttendeeSmc3 attendee : new ArrayList<>(attendees))
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
    public static void eachFmeAttendeeInConference(Smc3ConferenceContext conferenceContext, AttendeeProcessor attendeeProcessor)
    {
        List<AttendeeSmc3> masterAttendees = new ArrayList<>(conferenceContext.getFmeAttendees());
        for (AttendeeSmc3 masterAttendee0 : masterAttendees)
        {
            processAttendee(conferenceContext, masterAttendee0, attendeeProcessor);
        }
    }
    
    private static void processAttendee(Smc3ConferenceContext conferenceContext, AttendeeSmc3 a, AttendeeProcessor attendeeProcessor)
    {
        if (a != null && conferenceContext.getConferenceNumber()!=null)
        {
            attendeeProcessor.process(a);
        }
    }
    
    /**
     * 遍历每一个参会(包含fme参会)
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachAttendeeInConference(Smc3ConferenceContext conferenceContext, AttendeeProcessor attendeeProcessor)
    {
        for (McuAttendeeSmc3 fa : conferenceContext.getMcuAttendees())
        {
            processAttendee(conferenceContext, fa, attendeeProcessor);
        }

        eachNonFmeAttendeeInConference(conferenceContext, attendeeProcessor);
    }
    
    public static interface AttendeeProcessor
    {
        void process(AttendeeSmc3 attendee);
    }
}

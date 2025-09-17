/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceContextUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author lilinhai 
 * @since 2021-02-22 12:51
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.paradisecloud.common.utils.StringUtils;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;

/**  
 * <pre>会议上下文工具类</pre>
 * @author lilinhai
 * @since 2021-02-22 12:51
 * @version V1.0  
 */
public class ConferenceContextUtils
{
    
    /**
     * 获取默认选看的参会者
     * @author lilinhai
     * @since 2021-02-22 12:52 
     * @param mainConferenceContext
     * @return Attendee
     */
    public static Attendee getDefaultChooseToSee(ConferenceContext mainConferenceContext)
    {
        List<Attendee> masterAttendees = mainConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (Attendee ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        List<Attendee> attendees = mainConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (Iterator<Attendee> iterator = attendees.iterator(); iterator.hasNext();)
            {
                Attendee attendee = iterator.next();
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        for (Iterator<Entry<Long, List<Attendee>>> iterator = mainConferenceContext.getCascadeAttendeesMap().entrySet().iterator(); iterator.hasNext();)
        {
            Entry<Long, List<Attendee>> e = iterator.next();
            attendees = e.getValue();
            if (!ObjectUtils.isEmpty(attendees))
            {
                for (Iterator<Attendee> iterator1 = attendees.iterator(); iterator1.hasNext();)
                {
                    Attendee attendee = iterator1.next();
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
    public static Attendee getDefaultMasterAttendee(ConferenceContext mainConferenceContext)
    {
        List<Attendee> attendees = mainConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (Attendee attendee : attendees)
            {
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        List<Attendee> masterAttendees = mainConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (Attendee ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (Attendee attendee : attendees)
            {
                return attendee;
            }
        }
        
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (Attendee ma : masterAttendees)
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
    public static void eachNonFmeAttendeeInConference(ConferenceContext conferenceContext, AttendeeProcessor attendeeProcessor)
    {
        processAttendee(conferenceContext, conferenceContext.getMasterAttendee(), attendeeProcessor);
        
        List<Attendee> masterAttendees = new ArrayList<>(conferenceContext.getMasterAttendees());
        for (Attendee terminalAttendee : new ArrayList<>(conferenceContext.getAttendees()))
        {
            processAttendee(conferenceContext, terminalAttendee, attendeeProcessor);
        }
        
        for (Attendee masterAttendee0 : masterAttendees)
        {
            processAttendee(conferenceContext, masterAttendee0, attendeeProcessor);
        }
        
        conferenceContext.getCascadeAttendeesMap().forEach((dept, attendees) -> {
            
            // 参会者
            for (Attendee attendee : new ArrayList<>(attendees))
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
    public static void eachFmeAttendeeInConference(ConferenceContext conferenceContext, AttendeeProcessor attendeeProcessor)
    {
        List<Attendee> masterAttendees = new ArrayList<>(conferenceContext.getFmeAttendees());
        for (Attendee masterAttendee0 : masterAttendees)
        {
            processAttendee(conferenceContext, masterAttendee0, attendeeProcessor);
        }
    }
    
    private static void processAttendee(ConferenceContext conferenceContext, Attendee a, AttendeeProcessor attendeeProcessor)
    {
        if (a != null && conferenceContext.getContextKey().equals(a.getContextKey()))
        {
            attendeeProcessor.process(a);
        }
    }
    
    /**
     * 遍历每一个参会(包含fme参会)
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachAttendeeInConference(ConferenceContext conferenceContext, AttendeeProcessor attendeeProcessor)
    {
        for (FmeAttendee fa : conferenceContext.getFmeAttendees())
        {
            processAttendee(conferenceContext, fa, attendeeProcessor);
        }
        
        eachNonFmeAttendeeInConference(conferenceContext, attendeeProcessor);
    }
    
    public static interface AttendeeProcessor
    {
        void process(Attendee attendee);
    }
}

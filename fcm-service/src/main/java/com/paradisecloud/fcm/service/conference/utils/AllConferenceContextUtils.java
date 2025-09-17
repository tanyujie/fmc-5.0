/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuZjConferenceContextUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author lilinhai 
 * @since 2021-02-22 12:51
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.service.conference.utils;

import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
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
public class AllConferenceContextUtils
{
    
    /**
     * 获取默认选看的参会者
     * @author lilinhai
     * @since 2021-02-22 12:52 
     * @param mainConferenceContext
     * @return Attendee
     */
    public static BaseAttendee getDefaultChooseToSee(BaseConferenceContext<BaseAttendee> mainConferenceContext)
    {
        List<BaseAttendee> masterAttendees = mainConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (BaseAttendee ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        List<BaseAttendee> attendees = mainConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (Iterator<BaseAttendee> iterator = attendees.iterator(); iterator.hasNext();)
            {
                BaseAttendee attendee = iterator.next();
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        for (Iterator<Entry<Long, List<BaseAttendee>>> iterator = mainConferenceContext.getCascadeAttendeesMap().entrySet().iterator(); iterator.hasNext();)
        {
            Entry<Long, List<BaseAttendee>> e = iterator.next();
            attendees = e.getValue();
            if (!ObjectUtils.isEmpty(attendees))
            {
                for (Iterator<BaseAttendee> iterator1 = attendees.iterator(); iterator1.hasNext();)
                {
                    BaseAttendee attendee = iterator1.next();
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
    public static BaseAttendee getDefaultMasterAttendee(BaseConferenceContext<BaseAttendee> mainConferenceContext)
    {
        List<BaseAttendee> attendees = mainConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (BaseAttendee attendee : attendees)
            {
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        List<BaseAttendee> masterAttendees = mainConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (BaseAttendee ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (BaseAttendee attendee : attendees)
            {
                return attendee;
            }
        }
        
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (BaseAttendee ma : masterAttendees)
            {
                return ma;
            }
        }
        return null;
    }
    
    /**
     * 遍历每一个非mcu参会
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachNonMcuAttendeeInConference(BaseConferenceContext<BaseAttendee> conferenceContext, AttendeeProcessor attendeeProcessor)
    {
        processAttendee(conferenceContext, conferenceContext.getMasterAttendee(), attendeeProcessor);
        
        List<BaseAttendee> masterAttendees = new ArrayList<>(conferenceContext.getMasterAttendees());
        for (BaseAttendee terminalAttendee : new ArrayList<>(conferenceContext.getAttendees()))
        {
            processAttendee(conferenceContext, terminalAttendee, attendeeProcessor);
        }
        
        for (BaseAttendee masterAttendee0 : masterAttendees)
        {
            processAttendee(conferenceContext, masterAttendee0, attendeeProcessor);
        }
        
        conferenceContext.getCascadeAttendeesMap().forEach((dept, attendees) -> {
            
            // 参会者
            for (BaseAttendee attendee : new ArrayList<>(attendees))
            {
                processAttendee(conferenceContext, attendee, attendeeProcessor);
            }
        });
    }
    
    private static void processAttendee(BaseConferenceContext<BaseAttendee> conferenceContext, BaseAttendee a, AttendeeProcessor attendeeProcessor)
    {
        if (a != null && conferenceContext.getContextKey().equals(a.getContextKey()))
        {
            attendeeProcessor.process(a);
        }
    }
    
    /**
     * 遍历每一个参会(包含mcu参会)
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachAttendeeInConference(BaseConferenceContext<BaseAttendee> conferenceContext, AttendeeProcessor attendeeProcessor)
    {
        for (BaseAttendee mcuAttendee : conferenceContext.getMcuAttendees())
        {
            processAttendee(conferenceContext, mcuAttendee, attendeeProcessor);
        }
        
        eachNonMcuAttendeeInConference(conferenceContext, attendeeProcessor);
    }
    
    public static interface AttendeeProcessor
    {
        void process(BaseAttendee attendee);
    }
}

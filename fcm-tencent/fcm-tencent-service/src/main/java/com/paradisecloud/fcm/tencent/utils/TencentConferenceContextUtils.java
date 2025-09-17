/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceContextUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author lilinhai 
 * @since 2021-02-22 12:51
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.tencent.utils;

import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
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
public class TencentConferenceContextUtils
{
    
    /**
     * 获取默认选看的参会者
     * @author lilinhai
     * @since 2021-02-22 12:52 
     * @param mainConferenceContext
     * @return Attendee
     */
    public static AttendeeTencent getDefaultChooseToSee(TencentConferenceContext mainConferenceContext)
    {
        List<AttendeeTencent> masterAttendees = mainConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeTencent ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        List<AttendeeTencent> attendees = mainConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (Iterator<AttendeeTencent> iterator = attendees.iterator(); iterator.hasNext();)
            {
                AttendeeTencent attendee = iterator.next();
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        for (Iterator<Entry<Long, List<AttendeeTencent>>> iterator = mainConferenceContext.getCascadeAttendeesMap().entrySet().iterator(); iterator.hasNext();)
        {
            Entry<Long, List<AttendeeTencent>> e = iterator.next();
            attendees = e.getValue();
            if (!ObjectUtils.isEmpty(attendees))
            {
                for (Iterator<AttendeeTencent> iterator1 = attendees.iterator(); iterator1.hasNext();)
                {
                    AttendeeTencent attendee = iterator1.next();
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
    public static AttendeeTencent getDefaultMasterAttendee(TencentConferenceContext mainConferenceContext)
    {
        List<AttendeeTencent> attendees = mainConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (AttendeeTencent attendee : attendees)
            {
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        List<AttendeeTencent> masterAttendees = mainConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeTencent ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (AttendeeTencent attendee : attendees)
            {
                return attendee;
            }
        }
        
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeTencent ma : masterAttendees)
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
    public static void eachNonFmeAttendeeInConference(TencentConferenceContext conferenceContext, AttendeeProcessor attendeeProcessor)
    {
        processAttendee(conferenceContext, conferenceContext.getMasterAttendee(), attendeeProcessor);
        
        List<AttendeeTencent> masterAttendees = new ArrayList<>(conferenceContext.getMasterAttendees());
        for (AttendeeTencent terminalAttendee : new ArrayList<>(conferenceContext.getAttendees()))
        {
            processAttendee(conferenceContext, terminalAttendee, attendeeProcessor);
        }
        
        for (AttendeeTencent masterAttendee0 : masterAttendees)
        {
            processAttendee(conferenceContext, masterAttendee0, attendeeProcessor);
        }
        
        conferenceContext.getCascadeAttendeesMap().forEach((dept, attendees) -> {
            
            // 参会者
            for (AttendeeTencent attendee : new ArrayList<>(attendees))
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
    public static void eachFmeAttendeeInConference(TencentConferenceContext conferenceContext, AttendeeProcessor attendeeProcessor)
    {
        List<AttendeeTencent> masterAttendees = new ArrayList<>(conferenceContext.getFmeAttendees());
        for (AttendeeTencent masterAttendee0 : masterAttendees)
        {
            processAttendee(conferenceContext, masterAttendee0, attendeeProcessor);
        }
    }
    
    private static void processAttendee(TencentConferenceContext conferenceContext, AttendeeTencent a, AttendeeProcessor attendeeProcessor)
    {
        if (a != null)
        {
            attendeeProcessor.process(a);
        }
    }
    
    /**
     * 遍历每一个参会(包含fme参会)
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachAttendeeInConference(TencentConferenceContext conferenceContext, AttendeeProcessor attendeeProcessor)
    {
        for (AttendeeTencent fa : conferenceContext.getAttendees())
        {
            processAttendee(conferenceContext, fa, attendeeProcessor);
        }
        
        eachNonFmeAttendeeInConference(conferenceContext, attendeeProcessor);
    }
    
    public static interface AttendeeProcessor
    {
        void process(AttendeeTencent attendee);
    }
}

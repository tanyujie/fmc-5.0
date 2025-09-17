/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuKdcConferenceContextUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author lilinhai 
 * @since 2021-02-22 12:51
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.kdc.attendee.utils;

import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.McuAttendeeForMcuKdc;
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
public class McuKdcConferenceContextUtils
{
    
    /**
     * 获取默认选看的参会者
     * @author lilinhai
     * @since 2021-02-22 12:52 
     * @param mainMcuKdcConferenceContext
     * @return Attendee
     */
    public static AttendeeForMcuKdc getDefaultChooseToSee(McuKdcConferenceContext mainMcuKdcConferenceContext)
    {
        List<AttendeeForMcuKdc> masterAttendees = mainMcuKdcConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeForMcuKdc ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        List<AttendeeForMcuKdc> attendees = mainMcuKdcConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (Iterator<AttendeeForMcuKdc> iterator = attendees.iterator(); iterator.hasNext();)
            {
                AttendeeForMcuKdc attendee = iterator.next();
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        for (Iterator<Entry<Long, List<AttendeeForMcuKdc>>> iterator = mainMcuKdcConferenceContext.getCascadeAttendeesMap().entrySet().iterator(); iterator.hasNext();)
        {
            Entry<Long, List<AttendeeForMcuKdc>> e = iterator.next();
            attendees = e.getValue();
            if (!ObjectUtils.isEmpty(attendees))
            {
                for (Iterator<AttendeeForMcuKdc> iterator1 = attendees.iterator(); iterator1.hasNext();)
                {
                    AttendeeForMcuKdc attendee = iterator1.next();
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
     * @param mainMcuKdcConferenceContext
     * @return Attendee
     */
    public static AttendeeForMcuKdc getDefaultMasterAttendee(McuKdcConferenceContext mainMcuKdcConferenceContext)
    {
        List<AttendeeForMcuKdc> attendees = mainMcuKdcConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (AttendeeForMcuKdc attendee : attendees)
            {
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        List<AttendeeForMcuKdc> masterAttendees = mainMcuKdcConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeForMcuKdc ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (AttendeeForMcuKdc attendee : attendees)
            {
                return attendee;
            }
        }
        
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeForMcuKdc ma : masterAttendees)
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
    public static void eachNonMcuAttendeeInConference(McuKdcConferenceContext McuKdcConferenceContext, AttendeeProcessor attendeeProcessor)
    {
        processAttendee(McuKdcConferenceContext, McuKdcConferenceContext.getMasterAttendee(), attendeeProcessor);
        
        List<AttendeeForMcuKdc> masterAttendees = new ArrayList<>(McuKdcConferenceContext.getMasterAttendees());
        for (AttendeeForMcuKdc terminalAttendee : new ArrayList<>(McuKdcConferenceContext.getAttendees()))
        {
            processAttendee(McuKdcConferenceContext, terminalAttendee, attendeeProcessor);
        }
        
        for (AttendeeForMcuKdc masterAttendee0 : masterAttendees)
        {
            processAttendee(McuKdcConferenceContext, masterAttendee0, attendeeProcessor);
        }
        
        McuKdcConferenceContext.getCascadeAttendeesMap().forEach((dept, attendees) -> {
            
            // 参会者
            for (AttendeeForMcuKdc attendee : new ArrayList<>(attendees))
            {
                processAttendee(McuKdcConferenceContext, attendee, attendeeProcessor);
            }
        });
    }
    
    /**
     * 遍历每一个非FME参会
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachFmeAttendeeInConference(McuKdcConferenceContext McuKdcConferenceContext, AttendeeProcessor attendeeProcessor)
    {
        List<AttendeeForMcuKdc> masterAttendees = new ArrayList<>(McuKdcConferenceContext.getMcuAttendees());
        for (AttendeeForMcuKdc masterAttendee0 : masterAttendees)
        {
            processAttendee(McuKdcConferenceContext, masterAttendee0, attendeeProcessor);
        }
    }
    
    private static void processAttendee(McuKdcConferenceContext McuKdcConferenceContext, AttendeeForMcuKdc a, AttendeeProcessor attendeeProcessor)
    {
        if (a != null && McuKdcConferenceContext.getConferenceNumber().equals(a.getConferenceNumber()))
        {
            attendeeProcessor.process(a);
        }
    }
    
    /**
     * 遍历每一个参会(包含fme参会)
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachAttendeeInConference(McuKdcConferenceContext McuKdcConferenceContext, AttendeeProcessor attendeeProcessor)
    {
        for (McuAttendeeForMcuKdc mcuAttendee : McuKdcConferenceContext.getMcuAttendees())
        {
            processAttendee(McuKdcConferenceContext, mcuAttendee, attendeeProcessor);
        }
        
        eachNonMcuAttendeeInConference(McuKdcConferenceContext, attendeeProcessor);
    }
    
    public static interface AttendeeProcessor
    {
        void process(AttendeeForMcuKdc attendee);
    }
}

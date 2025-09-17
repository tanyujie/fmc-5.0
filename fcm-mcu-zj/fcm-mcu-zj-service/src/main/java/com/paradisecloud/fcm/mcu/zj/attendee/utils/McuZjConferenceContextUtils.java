/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuZjConferenceContextUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author lilinhai 
 * @since 2021-02-22 12:51
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.zj.attendee.utils;

import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.McuAttendeeForMcuZj;
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
public class McuZjConferenceContextUtils
{
    
    /**
     * 获取默认选看的参会者
     * @author lilinhai
     * @since 2021-02-22 12:52 
     * @param mainMcuZjConferenceContext
     * @return Attendee
     */
    public static AttendeeForMcuZj getDefaultChooseToSee(McuZjConferenceContext mainMcuZjConferenceContext)
    {
        List<AttendeeForMcuZj> masterAttendees = mainMcuZjConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeForMcuZj ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        List<AttendeeForMcuZj> attendees = mainMcuZjConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (Iterator<AttendeeForMcuZj> iterator = attendees.iterator(); iterator.hasNext();)
            {
                AttendeeForMcuZj attendee = iterator.next();
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        for (Iterator<Entry<Long, List<AttendeeForMcuZj>>> iterator = mainMcuZjConferenceContext.getCascadeAttendeesMap().entrySet().iterator(); iterator.hasNext();)
        {
            Entry<Long, List<AttendeeForMcuZj>> e = iterator.next();
            attendees = e.getValue();
            if (!ObjectUtils.isEmpty(attendees))
            {
                for (Iterator<AttendeeForMcuZj> iterator1 = attendees.iterator(); iterator1.hasNext();)
                {
                    AttendeeForMcuZj attendee = iterator1.next();
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
     * @param mainMcuZjConferenceContext
     * @return Attendee
     */
    public static AttendeeForMcuZj getDefaultMasterAttendee(McuZjConferenceContext mainMcuZjConferenceContext)
    {
        List<AttendeeForMcuZj> attendees = mainMcuZjConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (AttendeeForMcuZj attendee : attendees)
            {
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        List<AttendeeForMcuZj> masterAttendees = mainMcuZjConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeForMcuZj ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (AttendeeForMcuZj attendee : attendees)
            {
                return attendee;
            }
        }
        
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeForMcuZj ma : masterAttendees)
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
    public static void eachNonMcuAttendeeInConference(McuZjConferenceContext McuZjConferenceContext, AttendeeProcessor attendeeProcessor)
    {
        processAttendee(McuZjConferenceContext, McuZjConferenceContext.getMasterAttendee(), attendeeProcessor);
        
        List<AttendeeForMcuZj> masterAttendees = new ArrayList<>(McuZjConferenceContext.getMasterAttendees());
        for (AttendeeForMcuZj terminalAttendee : new ArrayList<>(McuZjConferenceContext.getAttendees()))
        {
            processAttendee(McuZjConferenceContext, terminalAttendee, attendeeProcessor);
        }
        
        for (AttendeeForMcuZj masterAttendee0 : masterAttendees)
        {
            processAttendee(McuZjConferenceContext, masterAttendee0, attendeeProcessor);
        }
        
        McuZjConferenceContext.getCascadeAttendeesMap().forEach((dept, attendees) -> {
            
            // 参会者
            for (AttendeeForMcuZj attendee : new ArrayList<>(attendees))
            {
                processAttendee(McuZjConferenceContext, attendee, attendeeProcessor);
            }
        });
    }
    
    /**
     * 遍历每一个非FME参会
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachFmeAttendeeInConference(McuZjConferenceContext McuZjConferenceContext, AttendeeProcessor attendeeProcessor)
    {
        List<AttendeeForMcuZj> masterAttendees = new ArrayList<>(McuZjConferenceContext.getMcuAttendees());
        for (AttendeeForMcuZj masterAttendee0 : masterAttendees)
        {
            processAttendee(McuZjConferenceContext, masterAttendee0, attendeeProcessor);
        }
    }
    
    private static void processAttendee(McuZjConferenceContext McuZjConferenceContext, AttendeeForMcuZj a, AttendeeProcessor attendeeProcessor)
    {
        if (a != null && McuZjConferenceContext.getConferenceNumber().equals(a.getConferenceNumber()))
        {
            attendeeProcessor.process(a);
        }
    }
    
    /**
     * 遍历每一个参会(包含fme参会)
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachAttendeeInConference(McuZjConferenceContext McuZjConferenceContext, AttendeeProcessor attendeeProcessor)
    {
        for (McuAttendeeForMcuZj mcuAttendee : McuZjConferenceContext.getMcuAttendees())
        {
            processAttendee(McuZjConferenceContext, mcuAttendee, attendeeProcessor);
        }
        
        eachNonMcuAttendeeInConference(McuZjConferenceContext, attendeeProcessor);
    }
    
    public static interface AttendeeProcessor
    {
        void process(AttendeeForMcuZj attendee);
    }
}

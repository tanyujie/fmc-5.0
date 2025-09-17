/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuPlcConferenceContextUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author lilinhai 
 * @since 2021-02-22 12:51
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.plc.attendee.utils;

import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.McuAttendeeForMcuPlc;
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
public class McuPlcConferenceContextUtils
{
    
    /**
     * 获取默认选看的参会者
     * @author lilinhai
     * @since 2021-02-22 12:52 
     * @param mainMcuPlcConferenceContext
     * @return Attendee
     */
    public static AttendeeForMcuPlc getDefaultChooseToSee(McuPlcConferenceContext mainMcuPlcConferenceContext)
    {
        List<AttendeeForMcuPlc> masterAttendees = mainMcuPlcConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeForMcuPlc ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        List<AttendeeForMcuPlc> attendees = mainMcuPlcConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (Iterator<AttendeeForMcuPlc> iterator = attendees.iterator(); iterator.hasNext();)
            {
                AttendeeForMcuPlc attendee = iterator.next();
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        for (Iterator<Entry<Long, List<AttendeeForMcuPlc>>> iterator = mainMcuPlcConferenceContext.getCascadeAttendeesMap().entrySet().iterator(); iterator.hasNext();)
        {
            Entry<Long, List<AttendeeForMcuPlc>> e = iterator.next();
            attendees = e.getValue();
            if (!ObjectUtils.isEmpty(attendees))
            {
                for (Iterator<AttendeeForMcuPlc> iterator1 = attendees.iterator(); iterator1.hasNext();)
                {
                    AttendeeForMcuPlc attendee = iterator1.next();
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
     * @param mainMcuPlcConferenceContext
     * @return Attendee
     */
    public static AttendeeForMcuPlc getDefaultMasterAttendee(McuPlcConferenceContext mainMcuPlcConferenceContext)
    {
        List<AttendeeForMcuPlc> attendees = mainMcuPlcConferenceContext.getAttendees();
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (AttendeeForMcuPlc attendee : attendees)
            {
                if (attendee.isMeetingJoined())
                {
                    return attendee;
                }
            }
        }
        
        List<AttendeeForMcuPlc> masterAttendees = mainMcuPlcConferenceContext.getMasterAttendees();
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeForMcuPlc ma : masterAttendees)
            {
                if (ma.isMeetingJoined())
                {
                    return ma;
                }
            }
        }
        
        if (!ObjectUtils.isEmpty(attendees))
        {
            for (AttendeeForMcuPlc attendee : attendees)
            {
                return attendee;
            }
        }
        
        if (!ObjectUtils.isEmpty(masterAttendees))
        {
            for (AttendeeForMcuPlc ma : masterAttendees)
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
    public static void eachNonMcuAttendeeInConference(McuPlcConferenceContext McuPlcConferenceContext, AttendeeProcessor attendeeProcessor)
    {
        processAttendee(McuPlcConferenceContext, McuPlcConferenceContext.getMasterAttendee(), attendeeProcessor);
        
        List<AttendeeForMcuPlc> masterAttendees = new ArrayList<>(McuPlcConferenceContext.getMasterAttendees());
        for (AttendeeForMcuPlc terminalAttendee : new ArrayList<>(McuPlcConferenceContext.getAttendees()))
        {
            processAttendee(McuPlcConferenceContext, terminalAttendee, attendeeProcessor);
        }
        
        for (AttendeeForMcuPlc masterAttendee0 : masterAttendees)
        {
            processAttendee(McuPlcConferenceContext, masterAttendee0, attendeeProcessor);
        }
        
        McuPlcConferenceContext.getCascadeAttendeesMap().forEach((dept, attendees) -> {
            
            // 参会者
            for (AttendeeForMcuPlc attendee : new ArrayList<>(attendees))
            {
                processAttendee(McuPlcConferenceContext, attendee, attendeeProcessor);
            }
        });
    }
    
    /**
     * 遍历每一个非FME参会
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachFmeAttendeeInConference(McuPlcConferenceContext McuPlcConferenceContext, AttendeeProcessor attendeeProcessor)
    {
        List<AttendeeForMcuPlc> masterAttendees = new ArrayList<>(McuPlcConferenceContext.getMcuAttendees());
        for (AttendeeForMcuPlc masterAttendee0 : masterAttendees)
        {
            processAttendee(McuPlcConferenceContext, masterAttendee0, attendeeProcessor);
        }
    }
    
    private static void processAttendee(McuPlcConferenceContext McuPlcConferenceContext, AttendeeForMcuPlc a, AttendeeProcessor attendeeProcessor)
    {
        if (a != null && McuPlcConferenceContext.getConferenceNumber().equals(a.getConferenceNumber()))
        {
            attendeeProcessor.process(a);
        }
    }
    
    /**
     * 遍历每一个参会(包含fme参会)
     * @author lilinhai
     * @since 2021-03-24 17:25  void
     */
    public static void eachAttendeeInConference(McuPlcConferenceContext McuPlcConferenceContext, AttendeeProcessor attendeeProcessor)
    {
        for (McuAttendeeForMcuPlc mcuAttendee : McuPlcConferenceContext.getMcuAttendees())
        {
            processAttendee(McuPlcConferenceContext, mcuAttendee, attendeeProcessor);
        }
        
        eachNonMcuAttendeeInConference(McuPlcConferenceContext, attendeeProcessor);
    }
    
    public static interface AttendeeProcessor
    {
        void process(AttendeeForMcuPlc attendee);
    }
}

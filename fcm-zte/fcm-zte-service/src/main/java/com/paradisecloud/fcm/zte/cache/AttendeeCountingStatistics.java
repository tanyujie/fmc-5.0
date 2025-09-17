/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalCountInfo.java
 * Package     : com.paradisecloud.fcm.fme.model.busi
 * @author Administrator 
 * @since 2021-06-05 09:29
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.zte.cache;


import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;

/**
 * <pre>参会终端计数统计</pre>
 * @author Administrator
 * @since 2021-06-05 09:29
 * @version V1.0  
 */
public class AttendeeCountingStatistics
{
    
    private int meetingJoinedCount;
    
    private int onlineCount;
    
    private int total;
    
    public AttendeeCountingStatistics(McuZteConferenceContext cc)
    {
        if (cc.getMasterAttendee() != null)
        {
            total++;
            if (cc.getMasterAttendee().isMeetingJoined()) 
            {
                meetingJoinedCount++;
            }
            
            if (cc.getMasterAttendee().isOnline())
            {
                onlineCount++;
            }
        }
        
        for (AttendeeForMcuZte terminalAttendee : cc.getAttendees())
        {
            total++;
            if (terminalAttendee.isMeetingJoined())
            {
                meetingJoinedCount++;
            }
            
            if (terminalAttendee.isOnline())
            {
                onlineCount++;
            }
        }
        
        for (AttendeeForMcuZte masterAttendee0 : cc.getMasterAttendees())
        {
            total++;
            if (masterAttendee0.isMeetingJoined())
            {
                meetingJoinedCount++;
            }

            if (masterAttendee0.isOnline())
            {
                onlineCount++;
            }
        }

        cc.getCascadeAttendeesMap().forEach((dept, attendees) -> {

            // 参会者
            for (AttendeeForMcuZte attendee : attendees)
            {
                total++;
                if (attendee.isMeetingJoined())
                {
                    meetingJoinedCount++;
                }

                if (attendee.isOnline())
                {
                    onlineCount++;
                }
            }
        });
    }

    /**
     * <p>Get Method   :   meetingJoinedCount int</p>
     * @return meetingJoinedCount
     */
    public int getMeetingJoinedCount()
    {
        return meetingJoinedCount;
    }

    /**
     * <p>Get Method   :   onlineCount int</p>
     * @return onlineCount
     */
    public int getOnlineCount()
    {
        return onlineCount;
    }

    /**
     * <p>Get Method   :   total int</p>
     * @return total
     */
    public int getTotal()
    {
        return total;
    }
}

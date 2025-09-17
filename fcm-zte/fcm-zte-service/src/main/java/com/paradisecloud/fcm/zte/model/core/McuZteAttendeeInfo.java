/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeInfo.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-22 17:55
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.zte.model.core;


import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;

/**  
 * <pre>参会者信息</pre>
 * @author lilinhai
 * @since 2021-02-22 17:55
 * @version V1.0  
 */
public class McuZteAttendeeInfo
{

    /**
     * 主会议
     */
    private McuZteConferenceContext conferenceContext;
    private McuZteConferenceContext attendeeConferenceContext;
    private AttendeeForMcuZte attendee;

    public McuZteAttendeeInfo(String contextKey, String attendeeId)
    {
        conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        McuZteConferenceContextCache.getInstance().downwardProcessingConferenceContext(conferenceContext, new McuZteConferenceContextCache.ConferenceContextBreakProcessor() {
            private boolean isBreak;
            public void process(McuZteConferenceContext cc)
            {
                if ((attendee = cc.getAttendeeById(attendeeId)) != null)
                {
                    attendeeConferenceContext = cc;
                    isBreak = true;
                }
            }

            public boolean stopRecursion()
            {
                return isBreak;
            }
            
        });
    }

    /**
     * <p>Get Method   :   conferenceContext ConferenceContext</p>
     * @return conferenceContext
     */
    public McuZteConferenceContext getAttendeeConferenceContext()
    {
        return attendeeConferenceContext;
    }

    /**
     * <p>Get Method   :   attendee Attendee</p>
     * @return attendee
     */
    public AttendeeForMcuZte getAttendee()
    {
        return attendee;
    }

    /**
     * <p>Get Method   :   mainConferenceContext ConferenceContext</p>
     * @return mainConferenceContext
     */
    public McuZteConferenceContext getConferenceContext()
    {
        return conferenceContext;
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author lilinhai
     * @since 2021-03-10 16:04 
     * @return
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return "AttendeeInfo [attendee=" + attendee + "]";
    }
}

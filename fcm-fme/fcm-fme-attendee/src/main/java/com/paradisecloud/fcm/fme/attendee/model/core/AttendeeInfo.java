/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeInfo.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-22 17:55
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.core;

import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache.ConferenceContextBreakProcessor;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;

/**  
 * <pre>参会者信息</pre>
 * @author lilinhai
 * @since 2021-02-22 17:55
 * @version V1.0  
 */
public class AttendeeInfo
{
    
    /**
     * 主会议
     */
    private ConferenceContext conferenceContext;
    private ConferenceContext attendeeConferenceContext;
    private Attendee attendee;
    
    public AttendeeInfo(String contextKey, String attendeeId)
    {
        conferenceContext = ConferenceContextCache.getInstance().get(contextKey);

        if (conferenceContext != null && conferenceContext.getStreamingAttendee() != null) {
            if (conferenceContext.getStreamingAttendee().getId().equals(attendeeId)) {
                attendee = conferenceContext.getStreamingAttendee();
                attendeeConferenceContext = conferenceContext;
                return;
            }
        }
        if (conferenceContext != null && conferenceContext.getRecordingAttendee() != null) {
            if (conferenceContext.getRecordingAttendee().getId().equals(attendeeId)) {
                attendee = conferenceContext.getRecordingAttendee();
                attendeeConferenceContext = conferenceContext;
                return;
            }
        }
        if (conferenceContext != null && conferenceContext.getMinutesAttendee() != null) {
            if (conferenceContext.getMinutesAttendee().getId().equals(attendeeId)) {
                attendee = conferenceContext.getMinutesAttendee();
                attendeeConferenceContext = conferenceContext;
                return;
            }
        }
        ConferenceContextCache.getInstance().downwardProcessingConferenceContext(conferenceContext, new ConferenceContextBreakProcessor() {
            private boolean isBreak;
            public void process(ConferenceContext cc)
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
    public ConferenceContext getAttendeeConferenceContext()
    {
        return attendeeConferenceContext;
    }

    /**
     * <p>Get Method   :   attendee Attendee</p>
     * @return attendee
     */
    public Attendee getAttendee()
    {
        return attendee;
    }

    /**
     * <p>Get Method   :   mainConferenceContext ConferenceContext</p>
     * @return mainConferenceContext
     */
    public ConferenceContext getConferenceContext()
    {
        return conferenceContext;
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author lilinhai
     * @since 2021-03-10 16:04 
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "AttendeeInfo [attendee=" + attendee + "]";
    }
}

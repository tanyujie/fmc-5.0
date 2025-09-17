/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeInfo.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-22 17:55
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi;

import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;

/**  
 * <pre>参会者信息</pre>
 * @author lilinhai
 * @since 2021-02-22 17:55
 * @version V1.0  
 */
public class AttendeeSmc3Info
{

    /**
     * 主会议
     */
    private Smc3ConferenceContext conferenceContext;
    private Smc3ConferenceContext attendeeConferenceContext;
    private AttendeeSmc3 attendee;

    public AttendeeSmc3Info(String conferenceNumber, String attendeeId)
    {
        conferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceNumber);
        if (conferenceContext.getStreamingAttendee() != null) {
            if (conferenceContext.getStreamingAttendee().getId() == attendeeId) {
                attendee = conferenceContext.getStreamingAttendee();
                attendeeConferenceContext = conferenceContext;
                return;
            }
        }
        Smc3ConferenceContextCache.getInstance().downwardProcessingConferenceContext(conferenceContext, new Smc3ConferenceContextCache.ConferenceContextBreakProcessor() {
            private boolean isBreak;
            @Override
            public void process(Smc3ConferenceContext cc)
            {
                if ((attendee = cc.getAttendeeById(attendeeId)) != null)
                {
                    attendeeConferenceContext = cc;
                    isBreak = true;
                }
            }

            @Override
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
    public Smc3ConferenceContext getAttendeeConferenceContext()
    {
        return attendeeConferenceContext;
    }

    /**
     * <p>Get Method   :   attendee Attendee</p>
     * @return attendee
     */
    public AttendeeSmc3 getAttendee()
    {
        return attendee;
    }

    /**
     * <p>Get Method   :   mainConferenceContext ConferenceContext</p>
     * @return mainConferenceContext
     */
    public Smc3ConferenceContext getConferenceContext()
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
        return "AttendeeTeleInfo [attendeeTele=" + attendee + "]";
    }
}

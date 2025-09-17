/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeInfo.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-22 17:55
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.kdc.model.core;

import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;

/**  
 * <pre>参会者信息</pre>
 * @author lilinhai
 * @since 2021-02-22 17:55
 * @version V1.0  
 */
public class McuKdcAttendeeInfo
{

    /**
     * 主会议
     */
    private McuKdcConferenceContext conferenceContext;
    private McuKdcConferenceContext attendeeConferenceContext;
    private AttendeeForMcuKdc attendee;

    public McuKdcAttendeeInfo(String contextKey, String attendeeId)
    {
        conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        McuKdcConferenceContextCache.getInstance().downwardProcessingConferenceContext(conferenceContext, new McuKdcConferenceContextCache.ConferenceContextBreakProcessor() {
            private boolean isBreak;
            public void process(McuKdcConferenceContext cc)
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
    public McuKdcConferenceContext getAttendeeConferenceContext()
    {
        return attendeeConferenceContext;
    }

    /**
     * <p>Get Method   :   attendee Attendee</p>
     * @return attendee
     */
    public AttendeeForMcuKdc getAttendee()
    {
        return attendee;
    }

    /**
     * <p>Get Method   :   mainConferenceContext ConferenceContext</p>
     * @return mainConferenceContext
     */
    public McuKdcConferenceContext getConferenceContext()
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

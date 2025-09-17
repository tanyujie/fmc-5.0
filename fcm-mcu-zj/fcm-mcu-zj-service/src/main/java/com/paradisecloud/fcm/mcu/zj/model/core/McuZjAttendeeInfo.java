/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeInfo.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-22 17:55
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.zj.model.core;

import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;

/**  
 * <pre>参会者信息</pre>
 * @author lilinhai
 * @since 2021-02-22 17:55
 * @version V1.0  
 */
public class McuZjAttendeeInfo
{

    /**
     * 主会议
     */
    private McuZjConferenceContext conferenceContext;
    private McuZjConferenceContext attendeeConferenceContext;
    private AttendeeForMcuZj attendee;

    public McuZjAttendeeInfo(String contextKey, String attendeeId)
    {
        conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        McuZjConferenceContextCache.getInstance().downwardProcessingConferenceContext(conferenceContext, new McuZjConferenceContextCache.ConferenceContextBreakProcessor() {
            private boolean isBreak;
            public void process(McuZjConferenceContext cc)
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
    public McuZjConferenceContext getAttendeeConferenceContext()
    {
        return attendeeConferenceContext;
    }

    /**
     * <p>Get Method   :   attendee Attendee</p>
     * @return attendee
     */
    public AttendeeForMcuZj getAttendee()
    {
        return attendee;
    }

    /**
     * <p>Get Method   :   mainConferenceContext ConferenceContext</p>
     * @return mainConferenceContext
     */
    public McuZjConferenceContext getConferenceContext()
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

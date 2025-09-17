/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : PollingAttendeeOpreation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai 
 * @since 2021-02-26 15:55
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.busi.operation;

import java.util.List;

import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.layout.SplitScreen;

/**  
 * <pre>请加上该类的描述</pre>
 * @author lilinhai
 * @since 2021-02-26 15:55
 * @version V1.0  
 */
public abstract class PollingAttendeeOpreation extends AttendeeOperation
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-26 15:55 
     */
    private static final long serialVersionUID = 1L;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-26 15:55 
     * @param conferenceContext
     * @param splitScreen 
     */
    protected PollingAttendeeOpreation(ConferenceContext conferenceContext)
    {
        super(conferenceContext);
    }

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-26 15:56 
     * @param conferenceContext
     * @param splitScreen
     * @param attendees 
     */
    protected PollingAttendeeOpreation(ConferenceContext conferenceContext, SplitScreen splitScreen, List<Attendee> attendees)
    {
        super(conferenceContext, splitScreen, attendees);
    }
    
    public abstract boolean isPause();
}

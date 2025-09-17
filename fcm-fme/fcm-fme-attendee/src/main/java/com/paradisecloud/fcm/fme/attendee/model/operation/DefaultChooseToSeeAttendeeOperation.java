/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultChooseToSeeAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai 
 * @since 2021-02-22 18:16
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.operation;

import java.util.List;

import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.layout.SplitScreen;

/**  
 * <pre>默认选看</pre>
 * @author lilinhai
 * @since 2021-02-22 18:16
 * @version V1.0  
 */
public class DefaultChooseToSeeAttendeeOperation extends ChooseToSeeAttendeeOperation
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-22 18:16 
     */
    private static final long serialVersionUID = 1L;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-22 18:16 
     * @param conferenceContext
     * @param splitScreen
     * @param attendees 
     */
    public DefaultChooseToSeeAttendeeOperation(ConferenceContext conferenceContext, SplitScreen splitScreen, List<Attendee> attendees)
    {
        super(conferenceContext, splitScreen, attendees);
    }

}

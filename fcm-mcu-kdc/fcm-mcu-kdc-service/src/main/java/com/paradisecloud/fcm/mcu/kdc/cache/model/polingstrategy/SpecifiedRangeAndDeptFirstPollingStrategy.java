/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : GlobalPollingPollingStrategy.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.polingstrategy
 * @author sinhy 
 * @since 2021-09-09 20:38
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.kdc.cache.model.polingstrategy;

import com.paradisecloud.fcm.mcu.kdc.attendee.model.polling.DeptPollingAttendees;
import com.paradisecloud.fcm.mcu.kdc.attendee.model.polling.PollingAttendee;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.layout.SplitScreen;

import java.util.List;

/**  
 * <pre>选定范围+组织架构优先</pre>
 * @author sinhy
 * @since 2021-09-09 20:38
 * @version V1.0  
 */
public class SpecifiedRangeAndDeptFirstPollingStrategy extends AttendeePollingStrategy
{
    
    @Override
    public List<PollingAttendee> parse(McuKdcConferenceContext conferenceContext, List<DeptPollingAttendees> deptPollingAttendeesList, SplitScreen splitScreen)
    {
        return parseSpecifiedRangeAndDeptFirst(conferenceContext, deptPollingAttendeesList);
    }
    
}

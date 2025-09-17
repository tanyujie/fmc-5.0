/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : GlobalPollingPollingStrategy.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.polingstrategy
 * @author sinhy 
 * @since 2021-09-09 20:38
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smc2.model.attendee.operation.polingstrategy;




import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.model.attendee.operation.polling.DeptPollingAttendees;
import com.paradisecloud.fcm.smc2.model.attendee.operation.polling.PollingAttendee;

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
    public List<PollingAttendee> parse(Smc2ConferenceContext conferenceContext, List<DeptPollingAttendees> deptPollingAttendeesList)
    {
        return parseSpecifiedRangeAndDeptFirst(conferenceContext, deptPollingAttendeesList);
    }
    
}

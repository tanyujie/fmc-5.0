/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : GlobalPollingPollingStrategy.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.polingstrategy
 * @author sinhy 
 * @since 2021-09-09 20:38
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.operation.polingstrategy;



import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.operation.polling.DeptPollingAttendees;
import com.paradisecloud.smc3.busi.operation.polling.PollingAttendee;

import java.util.List;

/**  
 * <pre>选定范围</pre>
 * @author sinhy
 * @since 2021-09-09 20:38
 * @version V1.0  
 */
public class SpecifiedRangePollingStrategy extends AttendeePollingStrategy
{
    
    @Override
    public List<PollingAttendee> parse(Smc3ConferenceContext conferenceContext, List<DeptPollingAttendees> deptPollingAttendeesList)
    {
        return parseSpecifiedRangePolling(deptPollingAttendeesList);
    }
    
}

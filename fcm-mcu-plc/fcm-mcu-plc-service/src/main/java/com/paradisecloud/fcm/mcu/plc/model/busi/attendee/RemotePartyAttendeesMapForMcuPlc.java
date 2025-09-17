/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : UriParticipantMap.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author sinhy 
 * @since 2021-09-05 22:53
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.plc.model.busi.attendee;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>UriParticipantMap</pre>
 * @author sinhy
 * @since 2021-09-05 22:53
 * @version V1.0  
 */
public class RemotePartyAttendeesMapForMcuPlc extends ConcurrentHashMap<String, Map<String, AttendeeForMcuPlc>>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-09-05 22:55 
     */
    private static final long serialVersionUID = 1L;
    
    public void addAttendee(AttendeeForMcuPlc attendee)
    {
        Map<String, AttendeeForMcuPlc> uuidAttendeeMap = get(attendee.getRemoteParty());
        if (uuidAttendeeMap == null)
        {
            synchronized (this)
            {
                uuidAttendeeMap = get(attendee.getRemoteParty());
                if (uuidAttendeeMap == null)
                {
                    uuidAttendeeMap = new ConcurrentHashMap<>();
                    put(attendee.getRemoteParty(), uuidAttendeeMap);
                }
            }
        }
        uuidAttendeeMap.put(attendee.getId(), attendee);
    }
    
    public Map<String, AttendeeForMcuPlc> getUuidAttendeeMapByUri(String remoteParty)
    {
        return get(remoteParty);
    }
    
    public AttendeeForMcuPlc removeAttendeeByRemotePartyAndUuid(String uri, String uuid)
    {
        Map<String, AttendeeForMcuPlc> m = get(uri);
        if (m != null)
        {
            AttendeeForMcuPlc a = m.remove(uuid);
            if (m.isEmpty())
            {
                remove(uri);
            }
            return a;
        }
        return null;
    }
}

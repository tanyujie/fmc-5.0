/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : UriParticipantMap.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author sinhy 
 * @since 2021-09-05 22:53
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.zj.model.busi.attendee;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>UriParticipantMap</pre>
 * @author sinhy
 * @since 2021-09-05 22:53
 * @version V1.0  
 */
public class RemotePartyAttendeesMapForMcu extends ConcurrentHashMap<String, Map<String, AttendeeForMcuZj>>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-09-05 22:55 
     */
    private static final long serialVersionUID = 1L;
    
    public void addAttendee(AttendeeForMcuZj attendee)
    {
        Map<String, AttendeeForMcuZj> uuidAttendeeMap = get(attendee.getRemoteParty());
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
    
    public Map<String, AttendeeForMcuZj> getUuidAttendeeMapByUri(String remoteParty)
    {
        return get(remoteParty);
    }
    
    public AttendeeForMcuZj removeAttendeeByRemotePartyAndUuid(String uri, String uuid)
    {
        Map<String, AttendeeForMcuZj> m = get(uri);
        if (m != null)
        {
            AttendeeForMcuZj a = m.remove(uuid);
            if (m.isEmpty())
            {
                remove(uri);
            }
            return a;
        }
        return null;
    }
}

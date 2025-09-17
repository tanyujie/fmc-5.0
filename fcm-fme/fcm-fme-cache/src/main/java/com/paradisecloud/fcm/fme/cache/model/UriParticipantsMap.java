/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : UriParticipantMap.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author sinhy 
 * @since 2021-09-05 22:53
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.fme.model.cms.participant.Participant;

/**  
 * <pre>UriParticipantMap</pre>
 * @author sinhy
 * @since 2021-09-05 22:53
 * @version V1.0  
 */
public class UriParticipantsMap extends ConcurrentHashMap<String, Map<String, Participant>>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-09-05 22:55 
     */
    private static final long serialVersionUID = 1L;
    
    public void addParticipant(Participant participant)
    {
        Map<String, Participant> uuidParticipantMap = get(participant.getUri());
        if (uuidParticipantMap == null)
        {
            synchronized (this)
            {
                uuidParticipantMap = get(participant.getUri());
                if (uuidParticipantMap == null)
                {
                    uuidParticipantMap = new ConcurrentHashMap<>();
                    put(participant.getUri(), uuidParticipantMap);
                }
            }
        }
        uuidParticipantMap.put(participant.getId(), participant);
    }
    
    public Participant getByUri(String uri)
    {
        Map<String, Participant> uuidParticipantMap = get(uri);
        if (uuidParticipantMap == null)
        {
            return null;
        }
        
        for (Entry<String, Participant> e : uuidParticipantMap.entrySet())
        {
            if (ObjectUtils.isEmpty(e.getValue().getAttendeeId()))
            {
                return e.getValue();
            }
        }
        return null;
    }
    
    public Map<String, Participant> getUuidParticipantMapByUri(String uri)
    {
        return get(uri);
    }
    
    public Participant removeParticipantByUriAndUuid(String uri, String uuid)
    {
        Map<String, Participant> m = get(uri);
        if (m != null)
        {
            Participant p = m.remove(uuid);
            if (m.isEmpty())
            {
                remove(uri);
            }
            return p;
        }
        return null;
    }
}

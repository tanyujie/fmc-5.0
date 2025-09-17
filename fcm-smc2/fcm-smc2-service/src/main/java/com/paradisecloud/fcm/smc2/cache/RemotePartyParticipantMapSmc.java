package com.paradisecloud.fcm.smc2.cache;

import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2023/4/27 9:28
 */
public class RemotePartyParticipantMapSmc extends ConcurrentHashMap<String, Map<String, SmcParitipantsStateRep.ContentDTO>> {
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-09-05 22:55
     */
    private static final long serialVersionUID = 1L;

    public void addAttendee(SmcParitipantsStateRep.ContentDTO attendee)
    {
        Map<String, SmcParitipantsStateRep.ContentDTO> uuidAttendeeMap = get(attendee.getGeneralParam().getUri());
        if (uuidAttendeeMap == null)
        {
            synchronized (this)
            {
                uuidAttendeeMap = get(attendee.getGeneralParam().getUri());
                if (uuidAttendeeMap == null)
                {
                    uuidAttendeeMap = new ConcurrentHashMap<>();
                    put(attendee.getGeneralParam().getUri(), uuidAttendeeMap);
                }
            }
        }
        uuidAttendeeMap.put(attendee.getGeneralParam().getId(), attendee);
    }

    public Map<String, SmcParitipantsStateRep.ContentDTO> getUuidParticipantMapByUri(String remoteParty)
    {
        return get(remoteParty);
    }

    public SmcParitipantsStateRep.ContentDTO removeParticipantByRemotePartyAndUuid(String uri, String uuid)
    {
        Map<String, SmcParitipantsStateRep.ContentDTO> m = get(uri);
        if (m != null)
        {
            SmcParitipantsStateRep.ContentDTO a = m.remove(uuid);
            if (m.isEmpty())
            {
                remove(uri);
            }
            return a;
        }
        return null;
    }
}

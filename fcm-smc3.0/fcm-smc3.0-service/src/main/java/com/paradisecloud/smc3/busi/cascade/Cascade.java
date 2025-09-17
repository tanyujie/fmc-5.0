/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : UpCascade.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.cascade
 * @author lilinhai 
 * @since 2021-03-04 09:49
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.cascade;



import com.paradisecloud.smc3.busi.attende.McuAttendeeSmc3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>下级级联</pre>
 * @author lilinhai
 * @since 2021-03-04 09:49
 * @version V1.0  
 */
public class Cascade
{
    
    /**
     * 会议号
     */
    protected String conferenceNumber;
    
    /**
     * 级联的FME参会者
     */
    protected Map<String, McuAttendeeSmc3> fmeAttendeeMap = new ConcurrentHashMap<>();

    /**
     * <p>Get Method   :   conferenceNumber long</p>
     * @return conferenceNumber
     */
    public String getConferenceNumber()
    {
        return conferenceNumber;
    }

    /**
     * <p>Set Method   :   conferenceNumber long</p>
     * @param conferenceNumber
     */
    public void setConferenceNumber(String conferenceNumber)
    {
        this.conferenceNumber = conferenceNumber;
    }

    /**
     * <p>Get Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @return fmeAttendeeMap
     */
    public McuAttendeeSmc3 get(String conferenceNumber)
    {
        return fmeAttendeeMap.get(conferenceNumber);
    }

    /**
     * <p>Set Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     */
    public void add(McuAttendeeSmc3 fmeAttendee)
    {
        this.fmeAttendeeMap.put(fmeAttendee.getCascadeConferenceNumber(), fmeAttendee);
    }
    
    /**
     * <p>Get Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @return fmeAttendeeMap
     */
    public McuAttendeeSmc3 remove(String conferenceNumber)
    {
        return fmeAttendeeMap.remove(conferenceNumber);
    }

    /**
     * <p>Get Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @return fmeAttendeeMap
     */
    public Map<String, McuAttendeeSmc3> getFmeAttendeeMap()
    {
        return fmeAttendeeMap;
    }
    
    public void eachFmeAttendee(FmeAttendeeProcessor fmeAttendeeProcessor)
    {
        List<McuAttendeeSmc3> fmeAttendees = new ArrayList<>(fmeAttendeeMap.values());
        Collections.sort(fmeAttendees);
        fmeAttendees.forEach((fmeAttendee)->{
            fmeAttendeeProcessor.process(fmeAttendee);
        });
    }
    
    public static interface FmeAttendeeProcessor
    {
        void process(McuAttendeeSmc3 fmeAttendee);
    }
}

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : UpCascade.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.cascade
 * @author lilinhai 
 * @since 2021-03-04 09:49
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.zte.model.busi.cascade;


import com.paradisecloud.fcm.zte.model.busi.attendee.McuAttendeeForMcuZte;

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
public class CascadeForMcuZte
{
    
    /**
     * 会议号
     */
    protected String conferenceNumber;
    
    /**
     * 级联的FME参会者
     */
    protected Map<String, McuAttendeeForMcuZte> mcuAttendeeMap = new ConcurrentHashMap<>();

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
    public McuAttendeeForMcuZte get(String conferenceNumber)
    {
        return mcuAttendeeMap.get(conferenceNumber);
    }

    /**
     * <p>Set Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @param mcuAttendee
     */
    public void add(McuAttendeeForMcuZte mcuAttendee)
    {
        this.mcuAttendeeMap.put(mcuAttendee.getCascadeConferenceNumber(), mcuAttendee);
    }
    
    /**
     * <p>Get Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @return fmeAttendeeMap
     */
    public McuAttendeeForMcuZte remove(String conferenceNumber)
    {
        return mcuAttendeeMap.remove(conferenceNumber);
    }

    /**
     * <p>Get Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @return fmeAttendeeMap
     */
    public Map<String, McuAttendeeForMcuZte> getMcuAttendeeMap()
    {
        return mcuAttendeeMap;
    }
    
    public void eachFmeAttendee(FmeAttendeeProcessor fmeAttendeeProcessor)
    {
        List<McuAttendeeForMcuZte> mcuAttendees = new ArrayList<>(mcuAttendeeMap.values());
        Collections.sort(mcuAttendees);
        mcuAttendees.forEach((fmeAttendee)->{
            fmeAttendeeProcessor.process(fmeAttendee);
        });
    }

    public static interface FmeAttendeeProcessor
    {
        void process(McuAttendeeForMcuZte mcuAttendee);
    }
}

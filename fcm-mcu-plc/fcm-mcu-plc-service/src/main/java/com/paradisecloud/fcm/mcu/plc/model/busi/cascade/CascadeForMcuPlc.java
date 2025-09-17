/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : UpCascade.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.cascade
 * @author lilinhai 
 * @since 2021-03-04 09:49
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.plc.model.busi.cascade;

import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.McuAttendeeForMcuPlc;

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
public class CascadeForMcuPlc
{
    
    /**
     * 会议号
     */
    protected String conferenceNumber;
    
    /**
     * 级联的FME参会者
     */
    protected Map<String, McuAttendeeForMcuPlc> mcuAttendeeMap = new ConcurrentHashMap<>();

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
    public McuAttendeeForMcuPlc get(String conferenceNumber)
    {
        return mcuAttendeeMap.get(conferenceNumber);
    }

    /**
     * <p>Set Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @param mcuAttendee
     */
    public void add(McuAttendeeForMcuPlc mcuAttendee)
    {
        this.mcuAttendeeMap.put(mcuAttendee.getCascadeConferenceNumber(), mcuAttendee);
    }
    
    /**
     * <p>Get Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @return fmeAttendeeMap
     */
    public McuAttendeeForMcuPlc remove(String conferenceNumber)
    {
        return mcuAttendeeMap.remove(conferenceNumber);
    }

    /**
     * <p>Get Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @return fmeAttendeeMap
     */
    public Map<String, McuAttendeeForMcuPlc> getMcuAttendeeMap()
    {
        return mcuAttendeeMap;
    }
    
    public void eachFmeAttendee(FmeAttendeeProcessor fmeAttendeeProcessor)
    {
        List<McuAttendeeForMcuPlc> mcuAttendees = new ArrayList<>(mcuAttendeeMap.values());
        Collections.sort(mcuAttendees);
        mcuAttendees.forEach((fmeAttendee)->{
            fmeAttendeeProcessor.process(fmeAttendee);
        });
    }

    public static interface FmeAttendeeProcessor
    {
        void process(McuAttendeeForMcuPlc mcuAttendee);
    }
}

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : UpCascade.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.cascade
 * @author lilinhai 
 * @since 2021-03-04 09:49
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.zj.model.busi.cascade;

import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.McuAttendeeForMcuZj;

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
public class CascadeForMcuZj
{
    
    /**
     * 会议号
     */
    protected String conferenceNumber;
    
    /**
     * 级联的FME参会者
     */
    protected Map<String, McuAttendeeForMcuZj> mcuAttendeeMap = new ConcurrentHashMap<>();

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
    public McuAttendeeForMcuZj get(String conferenceNumber)
    {
        return mcuAttendeeMap.get(conferenceNumber);
    }

    /**
     * <p>Set Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @param mcuAttendee
     */
    public void add(McuAttendeeForMcuZj mcuAttendee)
    {
        this.mcuAttendeeMap.put(mcuAttendee.getCascadeConferenceNumber(), mcuAttendee);
    }
    
    /**
     * <p>Get Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @return fmeAttendeeMap
     */
    public McuAttendeeForMcuZj remove(String conferenceNumber)
    {
        return mcuAttendeeMap.remove(conferenceNumber);
    }

    /**
     * <p>Get Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @return fmeAttendeeMap
     */
    public Map<String, McuAttendeeForMcuZj> getMcuAttendeeMap()
    {
        return mcuAttendeeMap;
    }
    
    public void eachFmeAttendee(FmeAttendeeProcessor fmeAttendeeProcessor)
    {
        List<McuAttendeeForMcuZj> mcuAttendees = new ArrayList<>(mcuAttendeeMap.values());
        Collections.sort(mcuAttendees);
        mcuAttendees.forEach((fmeAttendee)->{
            fmeAttendeeProcessor.process(fmeAttendee);
        });
    }

    public static interface FmeAttendeeProcessor
    {
        void process(McuAttendeeForMcuZj mcuAttendee);
    }
}

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

import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.UpMcuAttendeeForMcuPlc;

/**  
 * <pre>上级级联</pre>
 * @author lilinhai
 * @since 2021-03-04 09:49
 * @version V1.0  
 */
public class UpCascadeForMcuPlc extends CascadeForMcuPlc
{

    public void add(UpMcuAttendeeForMcuPlc mcuAttendee)
    {
        super.add(mcuAttendee);
    }

    public UpMcuAttendeeForMcuPlc get(String conferenceNumber)
    {
        return (UpMcuAttendeeForMcuPlc) super.get(conferenceNumber);
    }
    
    /**
     * <p>Get Method   :   mcuAttendeeMap Map<Long,FmeAttendee></p>
     * @return mcuAttendeeMap
     */
    public UpMcuAttendeeForMcuPlc remove(String conferenceNumber)
    {
        return (UpMcuAttendeeForMcuPlc) mcuAttendeeMap.remove(conferenceNumber);
    }
    
    public void eachUpMcuAttendee(UpMcuAttendeeProcessor upFmeAttendeeProcessor)
    {
        mcuAttendeeMap.forEach((cn, mcuAttendee)->{
            UpMcuAttendeeForMcuPlc upFmeAttendee = (UpMcuAttendeeForMcuPlc) mcuAttendee;
            upFmeAttendeeProcessor.process(upFmeAttendee);
        });
    }
    
    public static interface UpMcuAttendeeProcessor
    {
        void process(UpMcuAttendeeForMcuPlc mcuAttendee);
    }
}

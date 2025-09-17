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


import com.paradisecloud.fcm.zte.model.busi.attendee.UpMcuAttendeeForMcuZte;

/**
 * <pre>上级级联</pre>
 * @author lilinhai
 * @since 2021-03-04 09:49
 * @version V1.0  
 */
public class UpCascadeForMcuZte extends CascadeForMcuZte
{

    public void add(UpMcuAttendeeForMcuZte mcuAttendee)
    {
        super.add(mcuAttendee);
    }

    public UpMcuAttendeeForMcuZte get(String conferenceNumber)
    {
        return (UpMcuAttendeeForMcuZte) super.get(conferenceNumber);
    }
    
    /**
     * <p>Get Method   :   mcuAttendeeMap Map<Long,FmeAttendee></p>
     * @return mcuAttendeeMap
     */
    public UpMcuAttendeeForMcuZte remove(String conferenceNumber)
    {
        return (UpMcuAttendeeForMcuZte) mcuAttendeeMap.remove(conferenceNumber);
    }
    
    public void eachUpMcuAttendee(UpMcuAttendeeProcessor upFmeAttendeeProcessor)
    {
        mcuAttendeeMap.forEach((cn, mcuAttendee)->{
            UpMcuAttendeeForMcuZte upFmeAttendee = (UpMcuAttendeeForMcuZte) mcuAttendee;
            upFmeAttendeeProcessor.process(upFmeAttendee);
        });
    }
    
    public static interface UpMcuAttendeeProcessor
    {
        void process(UpMcuAttendeeForMcuZte mcuAttendee);
    }
}

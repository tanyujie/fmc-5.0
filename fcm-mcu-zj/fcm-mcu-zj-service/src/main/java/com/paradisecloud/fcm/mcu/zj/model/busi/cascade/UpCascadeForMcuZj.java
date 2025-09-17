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

import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.UpMcuAttendeeForMcuZj;

/**  
 * <pre>上级级联</pre>
 * @author lilinhai
 * @since 2021-03-04 09:49
 * @version V1.0  
 */
public class UpCascadeForMcuZj extends CascadeForMcuZj
{

    public void add(UpMcuAttendeeForMcuZj mcuAttendee)
    {
        super.add(mcuAttendee);
    }

    public UpMcuAttendeeForMcuZj get(String conferenceNumber)
    {
        return (UpMcuAttendeeForMcuZj) super.get(conferenceNumber);
    }
    
    /**
     * <p>Get Method   :   mcuAttendeeMap Map<Long,FmeAttendee></p>
     * @return mcuAttendeeMap
     */
    public UpMcuAttendeeForMcuZj remove(String conferenceNumber)
    {
        return (UpMcuAttendeeForMcuZj) mcuAttendeeMap.remove(conferenceNumber);
    }
    
    public void eachUpMcuAttendee(UpMcuAttendeeProcessor upFmeAttendeeProcessor)
    {
        mcuAttendeeMap.forEach((cn, mcuAttendee)->{
            UpMcuAttendeeForMcuZj upFmeAttendee = (UpMcuAttendeeForMcuZj) mcuAttendee;
            upFmeAttendeeProcessor.process(upFmeAttendee);
        });
    }
    
    public static interface UpMcuAttendeeProcessor
    {
        void process(UpMcuAttendeeForMcuZj mcuAttendee);
    }
}

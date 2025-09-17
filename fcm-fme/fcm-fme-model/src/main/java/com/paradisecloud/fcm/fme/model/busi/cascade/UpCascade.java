/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : UpCascade.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.cascade
 * @author lilinhai 
 * @since 2021-03-04 09:49
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.busi.cascade;

import com.paradisecloud.fcm.fme.model.busi.attendee.UpFmeAttendee;

/**  
 * <pre>上级级联</pre>
 * @author lilinhai
 * @since 2021-03-04 09:49
 * @version V1.0  
 */
public class UpCascade extends Cascade
{

    public void add(UpFmeAttendee fmeAttendee)
    {
        super.add(fmeAttendee);
    }

    public UpFmeAttendee get(String conferenceNumber)
    {
        return (UpFmeAttendee) super.get(conferenceNumber);
    }
    
    /**
     * <p>Get Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @return fmeAttendeeMap
     */
    public UpFmeAttendee remove(String conferenceNumber)
    {
        return (UpFmeAttendee) fmeAttendeeMap.remove(conferenceNumber);
    }
    
    public void eachUpFmeAttendee(UpFmeAttendeeProcessor upFmeAttendeeProcessor)
    {
        fmeAttendeeMap.forEach((cn, fmeAttendee)->{
            UpFmeAttendee upFmeAttendee = (UpFmeAttendee) fmeAttendee;
            upFmeAttendeeProcessor.process(upFmeAttendee);
        });
    }
    
    public static interface UpFmeAttendeeProcessor
    {
        void process(UpFmeAttendee fmeAttendee);
    }
}

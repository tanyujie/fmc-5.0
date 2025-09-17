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


import com.paradisecloud.smc3.busi.attende.UpMcuAttendeeSmc3;

/**
 * <pre>上级级联</pre>
 * @author lilinhai
 * @since 2021-03-04 09:49
 * @version V1.0  
 */
public class UpCascade extends Cascade
{

    public void add(UpMcuAttendeeSmc3 fmeAttendee)
    {
        super.add(fmeAttendee);
    }

    @Override
    public UpMcuAttendeeSmc3 get(String conferenceNumber)
    {
        return (UpMcuAttendeeSmc3) super.get(conferenceNumber);
    }
    
    /**
     * <p>Get Method   :   fmeAttendeeMap Map<Long,FmeAttendee></p>
     * @return fmeAttendeeMap
     */
    @Override
    public UpMcuAttendeeSmc3 remove(String conferenceNumber)
    {
        return (UpMcuAttendeeSmc3) fmeAttendeeMap.remove(conferenceNumber);
    }
    
    public void eachUpFmeAttendee(UpFmeAttendeeProcessor upFmeAttendeeProcessor)
    {
        fmeAttendeeMap.forEach((cn, fmeAttendee)->{
            UpMcuAttendeeSmc3 upFmeAttendee = (UpMcuAttendeeSmc3) fmeAttendee;
            upFmeAttendeeProcessor.process(upFmeAttendee);
        });
    }
    
    public static interface UpFmeAttendeeProcessor
    {
        void process(UpMcuAttendeeSmc3 fmeAttendee);
    }
}

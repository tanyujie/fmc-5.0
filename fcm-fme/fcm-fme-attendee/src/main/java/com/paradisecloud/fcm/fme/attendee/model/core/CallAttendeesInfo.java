/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallAttendeesInfo.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-04-19 11:36
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.fme.attendee.constant.BatchConstant;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;

/**  
 * <pre>请加上该类的描述</pre>
 * @author lilinhai
 * @since 2021-04-19 11:36
 * @version V1.0  
 */
public class CallAttendeesInfo
{
    
    private List<Attendee> attendees = new ArrayList<>();
    
    private FmeBridge fmeBridge;
    
    private String callId;
    
    private int batchIndex;

    /**
     * <p>Get Method   :   attendee List<Attendee></p>
     * @return attendee
     */
    public List<Attendee> getAttendees()
    {
        return attendees;
    }
    
    /**
     * <p>Get Method   :   attendee List<Attendee></p>
     * @return attendee
     */
    public String getAttendeeParticipantIds()
    {
        StringBuilder participantIdsStrBuilder = new StringBuilder();
        for (Attendee attendee : attendees)
        {
            if (participantIdsStrBuilder.length() > 0)
            {
                participantIdsStrBuilder.append(",");
            }
            participantIdsStrBuilder.append(attendee.getParticipantUuid());
        }
        return participantIdsStrBuilder.toString();
    }
    
    /**
     * <p>Get Method   :   attendee List<Attendee></p>
     * @return attendee
     */
    public String getNextBatchIds()
    {
        int from = batchIndex * BatchConstant.BATCH_PUT_SIZE;
        int to = from + BatchConstant.BATCH_PUT_SIZE;
        if (to >= attendees.size())
        {
            to = attendees.size();
        }
        if (to - from <= 0)
        {
            return null;
        }
        List<Attendee> subAttendees = attendees.subList(from, to);
        StringBuilder participantIdsStrBuilder = new StringBuilder();
        for (Attendee attendee : subAttendees)
        {
            if (!ObjectUtils.isEmpty(attendee.getParticipantUuid()))
            {
                if (participantIdsStrBuilder.length() > 0)
                {
                    participantIdsStrBuilder.append(",");
                }
                participantIdsStrBuilder.append(attendee.getParticipantUuid());
            }
        }
        
        batchIndex++;
        return participantIdsStrBuilder.toString();
    }

    /**
     * <p>Set Method   :   attendee List<Attendee></p>
     * @param attendee
     */
    public void addAttendee(Attendee attendee)
    {
        this.attendees.add(attendee);
    }

    /**
     * <p>Get Method   :   fmeBridge FmeBridge</p>
     * @return fmeBridge
     */
    public FmeBridge getFmeBridge()
    {
        return fmeBridge;
    }

    /**
     * <p>Set Method   :   fmeBridge FmeBridge</p>
     * @param fmeBridge
     */
    public void setFmeBridge(FmeBridge fmeBridge)
    {
        this.fmeBridge = fmeBridge;
    }

    /**
     * <p>Get Method   :   callId String</p>
     * @return callId
     */
    public String getCallId()
    {
        return callId;
    }

    /**
     * <p>Set Method   :   callId String</p>
     * @param callId
     */
    public void setCallId(String callId)
    {
        this.callId = callId;
    }
    
    
}

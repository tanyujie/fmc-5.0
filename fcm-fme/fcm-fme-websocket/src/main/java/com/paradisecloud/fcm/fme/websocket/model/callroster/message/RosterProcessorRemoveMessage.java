/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterProcessorRemoveMessage.java
 * Package     : com.paradisecloud.fcm.fme.websocket.model.roster.message
 * @author sinhy 
 * @since 2021-09-07 23:54
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.model.callroster.message;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeFieldService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.model.core.ParticipantMessageQueue;
import com.paradisecloud.fcm.fme.cache.AttendeeCallCache;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.ParticipantInfo;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.fme.model.cms.participant.ParticipantStatus;
import com.sinhy.spring.BeanFactory;

public class RosterProcessorRemoveMessage extends RosterProcessorMessage
{
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-07 23:54 
     * @param fmeBridge
     * @param updateItem 
     */
    public RosterProcessorRemoveMessage(FmeBridge fmeBridge, JSONObject updateItem)
    {
        super(fmeBridge, updateItem);
    }

    @Override
    protected void process0()
    {
        ParticipantInfo pi = FmeDataCache.getParticipantByUuid(fmeBridge, itemId);
        Participant p = null;
        if (pi != null)
        {
            p = pi.getFmeBridge().getDataCache().deleteParticipantByUuid(itemId);
            fmeBridge = pi.getFmeBridge();
            
            fmeBridge.getFmeLogger().logWebsocketInfo("Participant disconnect and removed: " + updateItem, true, false);
            p.getStatus().setState(ParticipantState.DISCONNECT.getValue());
            p.setRosterUpdate(updateItem);
            BeanFactory.getBean(IAttendeeFieldService.class).updateByParticipant(fmeBridge, p);
        }
        else
        {
            if (AttendeeCallCache.getInstance().get(itemId) != null)
            {
                fmeBridge.getFmeLogger().logWebsocketInfo("Participant call failed and removed: " + updateItem, true, false);
                BeanFactory.getBean(IAttendeeService.class).callAttendeeFailedNotice(itemId, updateItem.getString("reason"));
            }
            else
            {
                fmeBridge.getFmeLogger().logInfo("Participants are offline, but no relevant information can be found in the memory: " + updateItem, true, true);
            }
            
            if (p == null)
            {
                p = new Participant();
                p.setId(itemId);
                
                ParticipantStatus ps = new ParticipantStatus();
                ps.setState(ParticipantState.DISCONNECT.getValue());
                p.setStatus(ps);
                p.setRosterUpdate(updateItem);
            }
            
            ParticipantMessageQueue.getInstance().put(new ParticipantInfo(p, fmeBridge));
        }
    }
    
}

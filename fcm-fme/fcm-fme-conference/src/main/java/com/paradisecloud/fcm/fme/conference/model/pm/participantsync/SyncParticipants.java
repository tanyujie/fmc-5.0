/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SyncParticipants.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.participantsync
 * @author sinhy 
 * @since 2021-09-18 10:51
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.participantsync;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.fme.attendee.model.core.ParticipantServiceNewSynchronizer;
import com.paradisecloud.fcm.fme.attendee.model.core.ParticipantServiceNewSynchronizer.ParticipantFilterProcessor;
import com.paradisecloud.fcm.fme.attendee.model.core.ParticipantServiceNewSynchronizer.ParticipantNewCallbackProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.conference.interfaces.IParticipantSyncService.ParticipantsResponseGetter;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.fme.model.response.participant.ActiveParticipantsResponse;
import com.paradisecloud.fcm.fme.model.response.participant.ParticipantsResponse;
import com.sinhy.proxy.ProxyMethod;

public class SyncParticipants extends ProxyMethod
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 10:51 
     * @param method 
     */
    protected SyncParticipants(Method method)
    {
        super(method);
    }
    
    public void syncParticipants(FmeBridge fmeBridge, ParticipantsResponseGetter participantsResponseGetter, ParticipantNewCallbackProcessor participantNewCallbackProcessor)
    {
        try
        {
            int offset = 0;
            AtomicInteger totalCount = new AtomicInteger();
            while (true)
            {
                ParticipantsResponse spacesResponse = participantsResponseGetter.get(fmeBridge, offset);
                if (spacesResponse != null)
                {
                    ActiveParticipantsResponse activeCoSpacesResponse = spacesResponse.getParticipants();
                    if (activeCoSpacesResponse != null)
                    {
                        List<Participant> participants = activeCoSpacesResponse.getParticipant();
                        if (participants != null)
                        {
                            // 业务处理
                            doParticipantService(participants, fmeBridge, participantNewCallbackProcessor);
                            Integer total = activeCoSpacesResponse.getTotal();
                            totalCount.addAndGet(participants.size());
                            if (totalCount.get() < total.intValue())
                            {
                                offset = totalCount.get();
                            }
                            else
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
            
            fmeBridge.getFmeLogger().logWebsocketInfo("syncParticipants---------End of synchronizing attendee information, total: " + fmeBridge.getDataCache().getParticipantCount(), true);
        }
        catch (Throwable e)
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("syncParticipants error Top", true, e);
        }
    }
    
    private void doParticipantService(List<Participant> participants, FmeBridge fmeBridge, ParticipantNewCallbackProcessor participantNewCallbackProcessor)
    {
        for (Participant participant : participants)
        {
            try
            {
                if (!ObjectUtils.isEmpty(participant.getCallBridge()))
                {
                    fmeBridge.getFmeLogger().logWebsocketInfo("Attendee does not belong to the current conference bridge, discard processing: " + participant, true);
                    continue;
                }
                
                ParticipantFilterProcessor pfp = new ParticipantFilterProcessor()
                {
                    public boolean filter(FmeBridge fmeBridge, Participant p)
                    {
                        fmeBridge.getFmeLogger().logInfo("Full synchronization [" + participant + "]", true, false);
                        if (p.is(ParticipantState.CONNECTED))
                        {
                            p.setFirstSettingInMeetingCompleted(true);
                        }
                        return true;
                    }
                };
                new ParticipantServiceNewSynchronizer(fmeBridge, participant.getId(), pfp, participantNewCallbackProcessor).sync();
            }
            catch (Throwable e)
            {
                fmeBridge.getFmeLogger().logWebsocketInfo("doParticipantService error: " + participant, true, e);
            }
        }
    }
    
}

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ParticipantServiceSynchronizer.java
 * Package     : com.paradisecloud.fcm.sync.model.core
 * @author lilinhai 
 * @since 2020-12-17 10:42
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.core;

import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallegService;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.fme.model.response.participant.ParticipantInfoResponse;
import com.sinhy.spring.BeanFactory;

public class ParticipantServiceNewSynchronizer
{
    
    private FmeBridge fmeBridge;
    private String participantId;
    
    private JSONObject rosterUpdate;
    
    private ParticipantNewCallbackProcessor participantNewCallbackProcessor;
    
    private ParticipantFilterProcessor participantFilterProcessor;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2020-12-17 10:45 
     * @param fmeBridge
     * @param participantId 
     */
    public ParticipantServiceNewSynchronizer(FmeBridge fmeBridge, String participantId, ParticipantFilterProcessor participantFilterProcessor, ParticipantNewCallbackProcessor participantNewCallbackProcessor)
    {
        this.fmeBridge = fmeBridge;
        this.participantId = participantId;
        this.participantFilterProcessor = participantFilterProcessor;
        this.participantNewCallbackProcessor = participantNewCallbackProcessor;
    }
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2020-12-17 10:45 
     * @param fmeBridge
     * @param participantId 
     */
    public ParticipantServiceNewSynchronizer(FmeBridge fmeBridge, String participantId, ParticipantFilterProcessor participantFilterProcessor)
    {
        this(fmeBridge, participantId, participantFilterProcessor, null);
    }
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2020-12-17 10:45 
     * @param fmeBridge
     * @param participantId 
     */
    public ParticipantServiceNewSynchronizer(FmeBridge fmeBridge, String participantId)
    {
        this(fmeBridge, participantId, null);
    }
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-08-17 11:06 
     * @param fmeBridge
     * @param rosterUpdate 
     */
    public ParticipantServiceNewSynchronizer(FmeBridge fmeBridge, JSONObject rosterUpdate, ParticipantNewCallbackProcessor participantNewCallbackProcessor)
    {
        this(fmeBridge, rosterUpdate.getString("participant"));
        this.rosterUpdate = rosterUpdate;
        this.participantNewCallbackProcessor = participantNewCallbackProcessor;
    }

    public void sync()
    {
        ParticipantInfoResponse participantInfoResponse = fmeBridge.getParticipantInvoker().getParticipant(participantId);
        if (participantInfoResponse != null && participantInfoResponse.getParticipant() != null)
        {
            Participant participant = participantInfoResponse.getParticipant();
            if (ObjectUtils.isEmpty(participant.getStatus().getState()))
            {
                return;
            }
            
            if (participant.getCallBridge() != null)
            {
                FmeBridge realFmeBridge = fmeBridge.getByCallBridge(participant.getCallBridge());
                if (realFmeBridge == null)
                {
                    fmeBridge.getFmeLogger().logWebsocketInfo("Fmebridge not found according to callbridge：" + participant.getCallBridge(), true, true);
                    return;
                }
                fmeBridge.getFmeLogger().logWebsocketInfo(" Participant does not belong to the current FME: " + fmeBridge + ", but belongs to FME: " + realFmeBridge, true, false);
                fmeBridge = realFmeBridge;
                participantInfoResponse = fmeBridge.getParticipantInvoker().getParticipant(participantId);
                if (participantInfoResponse != null && participantInfoResponse.getParticipant() != null)
                {
                    participant = participantInfoResponse.getParticipant();
                    if (ObjectUtils.isEmpty(participant.getStatus().getState()))
                    {
                        return;
                    }
                }
            }

            if (participant.getCallBridge() != null)
            {
                fmeBridge.getFmeLogger().logWebsocketInfo("Participants belong to callbridge. The information is confused and cannot be accurately located：" + participant.getCallBridge(), true, true);
                return;
            }
            
            // 获取CallLeg
            CallLeg callLeg = BeanFactory.getBean(ICallegService.class).getCallLegByParticipantUuid(fmeBridge, participant);
            
            // 设置与会者开关麦信息
            participant.setCallLeg(callLeg);
            
            if (rosterUpdate != null)
            {
                participant.setRosterUpdate(rosterUpdate);
            }
            
            if (participantFilterProcessor == null || participantFilterProcessor.filter(fmeBridge, participant))
            {
                participant = fmeBridge.getDataCache().update(participant);
                if (participantNewCallbackProcessor != null)
                {
                    participantNewCallbackProcessor.process(fmeBridge, participant);
                }
            }
        }
        else
        {
            Participant p = fmeBridge.getDataCache().deleteParticipantByUuid(participantId);
            if (p != null)
            {
                p.getStatus().setState(ParticipantState.DISCONNECT.getValue());
                if (participantNewCallbackProcessor != null)
                {
                    participantNewCallbackProcessor.process(fmeBridge, p);
                }
            }
            else
            {
                // 呼叫失败通知
//                BeanFactory.getBean(IAttendeeService.class).callAttendeeFailedNotice(participantId, "可能是终端网络和MCU之间不互通，请排查！");
            }
        }
    }
    
    /**
     * <p>Get Method   :   fmeBridge FmeBridge</p>
     * @return fmeBridge
     */
    public FmeBridge getFmeBridge()
    {
        return fmeBridge;
    }

    public static interface ParticipantNewCallbackProcessor
    {
        void process(FmeBridge fmeBridge, Participant p);
    }
    
    public static interface ParticipantFilterProcessor
    {
        boolean filter(FmeBridge fmeBridge, Participant p);
    }
}

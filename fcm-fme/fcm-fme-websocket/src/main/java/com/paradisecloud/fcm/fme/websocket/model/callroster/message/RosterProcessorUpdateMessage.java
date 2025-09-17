/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterProcessorUpdateMessage.java
 * Package     : com.paradisecloud.fcm.fme.websocket.model.roster.message
 * @author sinhy 
 * @since 2021-09-07 23:48
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.model.callroster.message;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeFieldService;
import com.paradisecloud.fcm.fme.attendee.model.core.ParticipantMessageQueue;
import com.paradisecloud.fcm.fme.attendee.model.core.ParticipantServiceNewSynchronizer;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.ParticipantInfo;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.sinhy.spring.BeanFactory;

public class RosterProcessorUpdateMessage extends RosterProcessorMessage
{
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-07 23:48 
     * @param fmeBridge
     * @param updateItem 
     */
    public RosterProcessorUpdateMessage(FmeBridge fmeBridge, JSONObject updateItem)
    {
        super(fmeBridge, updateItem);
    }

    @Override
    protected void process0()
    {
        Participant participant = null;
        ParticipantInfo pi = FmeDataCache.getParticipantByUuid(fmeBridge, itemId);
        if (pi == null)
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("根据UUID在所有内存中找不到Participant参会者：" + updateItem, true, true);
            ParticipantServiceNewSynchronizer psns = new ParticipantServiceNewSynchronizer(fmeBridge, itemId);
            psns.sync();
            participant = psns.getFmeBridge().getDataCache().getParticipantByUuid(itemId);
            if (participant == null)
            {
                fmeBridge.getFmeLogger().logWebsocketInfo("根据UUID在FME侧找不到Participant参会者：" + updateItem, true, true);
                return;
            }
            fmeBridge = psns.getFmeBridge();
        }
        else
        {
            fmeBridge = pi.getFmeBridge();
            participant = pi.getParticipant();
        }
        
        compareAndUpdate(participant);
        
        // 只有连接和断开状态的终端可以进行更新
        if (participant.is(ParticipantState.CONNECTED) || participant.is(ParticipantState.DISCONNECT))
        {
            obtainCallLeg(participant, fmeBridge);
            CallLeg callLeg = participant.getCallLeg();
            if(callLeg.getConfiguration().getRxVideoMute()==null){
                fmeBridge.getParticipantInvoker().updateParticipant(participant.getId(),new ParticipantParamBuilder().rxVideoMute(false).build());
                callLeg.getConfiguration().setRxVideoMute(false);
            }
            // 更新会议上下文信息，并发送到前端显示
            BeanFactory.getBean(IAttendeeFieldService.class).updateByParticipant(fmeBridge, participant);
            ParticipantMessageQueue.getInstance().put(new ParticipantInfo(participant, fmeBridge));
        }
    }
    
    private void compareAndUpdate(Participant participant)
    {
        participant.setRosterUpdate(updateItem);
        
        if (updateItem.containsKey("name"))
        {
            participant.setName(updateItem.getString("name"));
        }
        
        if (updateItem.containsKey("state"))
        {
            participant.getStatus().setState(updateItem.getString("state"));
        }
        
        if (updateItem.containsKey("importance"))
        {
            if (participant.getConfiguration() != null)
            {
                participant.getConfiguration().setImportance(updateItem.getInteger("importance"));
            }
        }
        
        CallLeg callLeg = participant.getCallLeg();
        if (callLeg != null)
        {
            if (callLeg.getConfiguration() != null)
            {
                if (updateItem.containsKey("audioMuted"))
                {
                    callLeg.getConfiguration().setRxAudioMute(updateItem.getBoolean("audioMuted"));
                }
                
                if (updateItem.containsKey("videoMuted"))
                {
                    callLeg.getConfiguration().setRxVideoMute(updateItem.getBoolean("videoMuted"));
                }
            }
            
            if (updateItem.containsKey("layout") && callLeg.getStatus() != null)
            {
                callLeg.getStatus().setLayout(updateItem.getString("layout"));
            }
        }
    }
}

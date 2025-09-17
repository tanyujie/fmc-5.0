/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RecallProcessor.java
 * Package     : com.paradisecloud.fcm.fme.service.model.attendeeprocessor
 * @author lilinhai 
 * @since 2021-02-09 11:00
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.busiprocessor;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.model.busi.attendee.McuAttendee;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeFieldService;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallService;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.AttendeeMessageQueue;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.CallFailedAttendeeMessage;
import com.paradisecloud.fcm.fme.attendee.model.core.AttendeeSettingsInitializer;
import com.paradisecloud.fcm.fme.cache.AttendeeCallCache;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.ParticipantInfo;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;

/**  
 * <pre>参会者呼叫处理器</pre>
 * @author lilinhai
 * @since 2021-02-09 11:00
 * @version V1.0  
 */
public class CallAttendeeProcessor extends AttendeeBusiProcessor
{

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 11:00 
     * @param contextKey
     * @param attendeeId 
     */
    public CallAttendeeProcessor(String contextKey, String attendeeId)
    {
        super(contextKey, attendeeId);
    }
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 11:19
     * @param attendee 
     */
    public CallAttendeeProcessor(Attendee attendee)
    {
        super(attendee);
    }

    @Override
    public void process()
    {
        try
        {
            if (conferenceContext.isEnd() || targetAttendee.isHangUp() || targetAttendee.isMeetingJoined())
            {
                return;
            }
            
            ParticipantInfo pi = FmeDataCache.getParticipantByConferenceNumberAndUri(targetAttendee.getDeptId(), targetAttendee.getConferenceNumber(), targetAttendee.getRemoteParty());
            if (pi != null)
            {
                // 关联绑定彼此
                pi.getParticipant().setAttendeeId(targetAttendee.getId());
                targetAttendee.setParticipantUuid(pi.getParticipant().getId());
                targetAttendee.resetUpdateMap();
                targetAttendee.updateMeetingStatus();
                BeanFactory.getBean(IAttendeeFieldService.class).updateByParticipant(pi.getFmeBridge(), conferenceContext, pi.getParticipant(), targetAttendee);
            }
            
            if (targetAttendee instanceof FmeAttendee)
            {
                if (pi != null)
                {
                    FmeAttendee fmeAttendee = (FmeAttendee) targetAttendee;
                    ConferenceContext subConferenceContext = ConferenceContextCache.getInstance().get(fmeAttendee.getContextKey());
                    ParticipantInfo mainFmeParticipantInfo = FmeDataCache.getParticipantByConferenceNumberAndUri(subConferenceContext.getDeptId(), subConferenceContext.getConferenceNumber(), conferenceContext.getFmeAttendeeRemoteParty());
                    if (mainFmeParticipantInfo != null)
                    {
                        BeanFactory.getBean(IAttendeeFieldService.class).updateByParticipant(mainFmeParticipantInfo.getFmeBridge(), mainFmeParticipantInfo.getParticipant());
                    }
                }
                else
                {
                    doCall();
                }
            }
            else
            {
                if (pi == null)
                {
                    doCall();
                }
            }
        
        }
        catch (Throwable e)
        {
            logger.error("呼叫与会者发生异常-out：" + targetAttendee, e);
        }
    }

    /**
     * 执行随机FME呼叫
     * @author lilinhai
     * @since 2021-03-02 12:24  void
     */
    private void doCall()
    {
        try
        {
            if (targetAttendee instanceof TerminalAttendee)
            {
                TerminalAttendee ta = (TerminalAttendee) targetAttendee;
                if (ta.getAttendType() != AttendType.OUT_BOUND.getValue())
                {
                    logger.info("终端设置了禁用外呼功能，不触发被动呼叫：" + ta);
                    return;
                }
                
//                BusiTerminal bt = TerminalCache.getInstance().get(ta.getTerminalId());
//                if (TerminalType.isFCMSIP(bt.getType()))
//                {
//                    logger.info("FCMSIP需要付费，不触发被动呼叫：" + ta);
//                    return;
//                }
            }
            else if (targetAttendee instanceof McuAttendee) {
                McuAttendee mcuAttendee = (McuAttendee) targetAttendee;
                String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId());
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                if (baseConferenceContext != null) {
                    String remoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
                    if (baseConferenceContext.getMcuCallPort() != null && baseConferenceContext.getMcuCallPort() != 5060) {
                        remoteParty += ":" + baseConferenceContext.getMcuCallPort();
                    }
                    String oldRemoteParty = mcuAttendee.getRemoteParty();
                    targetAttendee.setRemoteParty(remoteParty);
                    conferenceContext.updateAttendeeToRemotePartyMap(oldRemoteParty, mcuAttendee);
                    if (StringUtils.isEmpty(remoteParty)) {
                        return;
                    }
                } else {
                    return;
                }
            }
            
            if (targetAttendee.getCallRequestSentTime() != null && (System.currentTimeMillis() - targetAttendee.getCallRequestSentTime()) < 5 * 1000)
            {
                StringBuilder messageTip = new StringBuilder();
                messageTip.append("【").append(targetAttendee.getName()).append("】重呼请求已发起，请耐心等待响应结果，期间不要频繁发起！");
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                return;
            }
            
            FmeBridgeCache.getInstance().doRandomFmeBridgeBusiness(targetAttendee.getDeptId(), new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    if (targetAttendee instanceof FmeAttendee)
                    {
                        FmeAttendee fmeAttendee = (FmeAttendee) targetAttendee;
                        if (!fmeAttendee.getIp().equals(fmeBridge.getAttendeeIp()))
                        {
                            fmeAttendee.setIp(fmeBridge.getAttendeeIp());
                            fmeAttendee.setCascadeConferenceNumber(fmeAttendee.getCascadeConferenceNumber());
                            conferenceContext.addAttendeeToRemotePartyMap(fmeAttendee);
                        }
                    }
                    
                    Call call = BeanFactory.getBean(ICallService.class).createCall(fmeBridge, targetAttendee.getConferenceNumber(), conferenceContext.getName());
                    AttendeeSettingsInitializer attendeeSettingsInitializer = new AttendeeSettingsInitializer(conferenceContext, targetAttendee);
                    ParticipantParamBuilder participantParamBuilder=attendeeSettingsInitializer.getParticipantParamBuilder();
                    String dtmfStr =  targetAttendee.getDtmfStr();
//                    if(Strings.isNotBlank(dtmfStr)){
//                        participantParamBuilder= participantParamBuilder.dtmfSequence(dtmfStr);
//                    }
                    String id = null;
                    while (true)
                    {
                        id = fmeBridge.getParticipantInvoker().createParticipant(call.getId(), participantParamBuilder.build());
                        if (!ObjectUtils.isEmpty(id))
                        {
                            AttendeeCallCache.getInstance().put(id, targetAttendee);
                            targetAttendee.setCallRequestSentTime(System.currentTimeMillis());
                            if (targetAttendee.isOnline())
                            {
                                StringBuilder messageTip = new StringBuilder();
                                messageTip.append("【").append(targetAttendee.getName()).append("】呼叫已发起！");
                                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                            }
                            logger.info("终端【" + targetAttendee.getIp() + "】根据UUID已绑定FME与会者UUID: " + id);

                            break;
                        }
                        
                        logger.error("本次呼叫发起失败，重新发起："+ targetAttendee);
                        ThreadUtils.sleep(100);
                    }

                    if(Strings.isNotBlank(dtmfStr)){
                        String finalId = id;
                        new Thread(()->{
                            ThreadUtils.sleep(3000);
                            fmeBridge.getParticipantInvoker().updateParticipant(finalId,new ParticipantParamBuilder().dtmfSequence(dtmfStr).build());
                        }).start();
                    }
                }
                
            });
        }
        catch (Throwable e)
        {
            logger.error("呼叫与会者发生异常-doCall：" + targetAttendee, e);
            StringBuilder messageTip = new StringBuilder();
            messageTip.append("【").append(targetAttendee.getName()).append("】呼叫失败：").append(e.getMessage());
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
            AttendeeMessageQueue.getInstance().put(new CallFailedAttendeeMessage(targetAttendee));
        }
    }
}

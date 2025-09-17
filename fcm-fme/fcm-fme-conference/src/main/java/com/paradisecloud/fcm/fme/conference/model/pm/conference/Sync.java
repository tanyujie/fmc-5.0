/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : Sync.java
 * Package : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * 
 * @author sinhy
 * 
 * @since 2021-09-18 10:55
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeFieldService;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallService;
import com.paradisecloud.fcm.fme.attendee.utils.ConferenceContextUtils;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.conference.interfaces.IParticipantSyncService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.fme.model.busi.core.SyncInformation;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.proxy.ProxyMethod;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;

public class Sync extends ProxyMethod
{
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author sinhy
     * @since 2021-09-18 10:55
     * @param method
     */
    protected Sync(Method method)
    {
        super(method);
    }
    
    public void sync(ConferenceContext conferenceContext, String reason)
    {
        if (conferenceContext.getSyncInformation() != null)
        {
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始1！");
            return;
        }
        
        synchronized (conferenceContext.getSyncLock())
        {
            if (conferenceContext.getSyncInformation() != null)
            {
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始2！");
                return;
            }
            
            logger.info("One click synchronization start：" + conferenceContext.getConferenceNumber());
            List<Call> okCalls = new ArrayList<>();
            FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(conferenceContext.getDeptId(), new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    if (fmeBridge.isAvailable())
                    {
                        Call call = fmeBridge.getDataCache().getCallByConferenceNumber(conferenceContext.getConferenceNumber());
                        if (call != null)
                        {
                            okCalls.add(call);
                        }
                        else
                        {
                            fmeBridge.getFmeLogger().logInfo("Can't find call: " + conferenceContext.getConferenceNumber(), true, false);
                        }
                    }
                }
            });
            
            Set<String> inMeetingStatusAttendeeIds = new HashSet<>();
            ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                if (attendee.isMeetingJoined())
                {
                    FmeBridge fb = FmeBridgeCache.getInstance().getFmeBridgeByCallId(attendee.getCallId());
                    if (fb == null || (attendee.getParticipantUuid() != null && fb.getDataCache().getParticipantByUuid(attendee.getParticipantUuid()) == null))
                    {
                        synchronized (attendee)
                        {
                            attendee.resetUpdateMap();
                            if (attendee instanceof TerminalAttendee)
                            {
                                TerminalAttendee terminalAttendee = (TerminalAttendee) attendee;
                                BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                                terminalAttendee.setTerminalType(bt.getType());
                                terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                                if (attendee.isMeetingJoined())
                                {
                                    terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                }
                                else
                                {
                                    terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                                    terminalAttendee.setName(bt.getName());
                                }
                            }
                            attendee.leaveMeeting();
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                        }
                    }
                    else
                    {
                        inMeetingStatusAttendeeIds.add(attendee.getId());
                    }
                }
            });
            
            if (!ObjectUtils.isEmpty(okCalls))
            {
                int totalCount = 0;
                SyncInformation syncInformation = new SyncInformation();
                conferenceContext.setSyncInformation(syncInformation);
                syncInformation.setInProgress(true);
                syncInformation.setTotalCallCount(okCalls.size());
                syncInformation.setReason(reason);
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "开始同步本会议的参会信息！");
                for (Call c : okCalls)
                {
                    FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByCallId(conferenceContext.getDeptId(), c.getId());
                    logger.info("sync fmeBridge>"+ fmeBridge);
                    BeanFactory.getBean(ICallService.class).syncCall(fmeBridge, c.getId());
                    Call call = fmeBridge.getDataCache().getCallByConferenceNumber(conferenceContext.getConferenceNumber());
                    logger.info("sync call>"+ call);
                    syncInformation.setCurrentCallTotalParticipantCount(call.getNumParticipantsLocal());
                    if (syncInformation.getCurrentCallTotalParticipantCount() > 0)
                    {
                        try
                        {
                            // 同步并更新
                            syncInformation.setCurrentCallFmeIp(fmeBridge.getBusiFme().getIp());
                            syncInformation.setSyncCurrentCallParticipantCount(0);
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.PARTICIPANT_SYNC, syncInformation);
                            
                            ThreadUtils.sleep(200);
                            BeanFactory.getBean(IParticipantSyncService.class).syncParticipants(fmeBridge, (FmeBridge fmeBridge0, int offset) -> {
                                return fmeBridge0.getCallInvoker().getParticipants(call.getId(), offset);
                            }, (FmeBridge fmeBridge0, Participant p) -> {
                                if (p.is(ParticipantState.CONNECTED))
                                {
                                    BeanFactory.getBean(IAttendeeFieldService.class).updateByParticipant(fmeBridge0, p);
                                    inMeetingStatusAttendeeIds.remove(p.getAttendeeId());
                                    syncInformation.addSyncCurrentCallParticipantCount();
                                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.PARTICIPANT_SYNC, syncInformation);
                                }
                            });
                            
                            syncInformation.addSyncCallCount();
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.PARTICIPANT_SYNC, syncInformation);
                            totalCount += syncInformation.getSyncCurrentCallParticipantCount();
                            WebSocketMessagePusher.getInstance()
                                    .pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "当前Call共同步【" + syncInformation.getSyncCurrentCallParticipantCount() + "】个参会者！");
                            ThreadUtils.sleep(500);
                        }
                        catch (Throwable e)
                        {
                            logger.error("同步call参会信息失败：" + call, e);
                        }
                    }
                }
                
                syncInformation.setInProgress(false);
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.PARTICIPANT_SYNC, syncInformation);
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "已同步完所有参会信息，共【" + totalCount + "】个！");
                conferenceContext.setSyncInformation(null);
            }
        }
    }
}

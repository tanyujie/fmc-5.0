/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : UpdateByParticipant0.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.core.proxymethods
 * @author sinhy 
 * @since 2021-09-17 16:13
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.pm.attendeefield;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.paradisecloud.fcm.fme.model.busi.attendee.MinutesAttendee;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.fme.attendee.model.core.AttendeeSettingsInitializer;
import com.paradisecloud.fcm.fme.attendee.model.updateprocessor.SelfCallAttendeeNewProcessor;
import com.paradisecloud.fcm.fme.cache.AttendeeCallCache;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.fme.model.parambuilder.CallParamBuilder;
import com.sinhy.model.GenericValue;
import org.springframework.util.StringUtils;

public class UpdateByParticipant0 extends UpdateByParticipant1
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-17 16:29 
     * @param method 
     */
    protected UpdateByParticipant0(Method method)
    {
        super(method);
    }

    public void updateByParticipant(FmeBridge fmeBridge, Participant participant)
    {
        Attendee a1 = AttendeeCallCache.getInstance().remove(participant.getId());
        if (a1 != null && ObjectUtils.isEmpty(participant.getAttendeeId()))
        {
            participant.setAttendeeId(a1.getId());
            participant.setFirstSettingInMeetingCompleted(true);
            a1.setParticipantUuid(participant.getId());
        }
        String cn = fmeBridge.getDataCache().getConferenceNumberByCallId(participant.getCall());
        if (!ObjectUtils.isEmpty(cn))
        {
            ConferenceContext conferenceContextExist = null;
            Collection<ConferenceContext> conferenceContextList = ConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(cn);
            if (conferenceContextList != null && conferenceContextList.size() > 0) {
                if (fmeBridge != null) {
                    CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(cn);
                    if (coSpace != null) {
                        for (ConferenceContext conferenceContextTemp : conferenceContextList) {
                            if (coSpace.getId().equals(conferenceContextTemp.getCoSpaceId())) {
                                conferenceContextExist = conferenceContextTemp;
                                break;
                            }
                        }
                    }
                }
            }
            ConferenceContext conferenceContext = conferenceContextExist;
            if (conferenceContext != null)
            {
                // 判断直播录制是否开启
//                ensureOpenRecordingAndStreaming(participant, conferenceContext);
                
                Attendee a = a1 != null ? a1 : matchAttendee(conferenceContext, fmeBridge, participant);
                if (a != null)
                {
                    updateByParticipant(fmeBridge, conferenceContext, participant, a);
                }
                else if (participant.is(ParticipantState.CONNECTED))
                {
                    new SelfCallAttendeeNewProcessor(participant, conferenceContext).process();
                    // 应对自主呼入FCM终端匹配时间差问题
                    if (participant.getTerminalId() != null) {
                        a = conferenceContext.getAttendeeByTerminalId(participant.getTerminalId());
                        if (a != null) {
                            updateByParticipant(fmeBridge, conferenceContext, participant, a);
                        }
                    }
                }
                
                if (!participant.isFirstSettingInMeetingCompleted())
                {
                    final Attendee aFinal = a;
                    participant.setFirstSettingInMeetingCompleted(true);
                    FcmThreadPool.exec(() -> {
                        firstInMeetingSetting(fmeBridge, conferenceContext, participant, aFinal);
                    });
                }
            }
        }
        else
        {
            logger.error("Cannot find conference number based on participant callid: " + participant);
        }
    }

    /**
     * 确保录制和直播是否需要开启
     * @author sinhy
     * @since 2022-03-07 18:14 
     * @param participant
     * @param conferenceContext void
     */
    private void ensureOpenRecordingAndStreaming(Participant participant, ConferenceContext conferenceContext)
    {
        if (participant.is(ParticipantState.CONNECTED))
        {
            if (conferenceContext.isRecorded())
            {
                GenericValue<Boolean> boolVal = new GenericValue<Boolean>();
                boolVal.setValue(false);
                FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(conferenceContext.getDeptId(), new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        Call call = fmeBridge.getDataCache().getCallByConferenceNumber(conferenceContext.getConferenceNumber());
                        if (call == null)
                        {
                            return;
                        }
                        
                        if (call.getRecording())
                        {
                            boolVal.setValue(true);
                        }
                    }
                });
                
                if (!boolVal.getValue())
                {
                    FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(conferenceContext.getDeptId(), new FmeBridgeAddpterProcessor()
                    {
                        public void process(FmeBridge fmeBridge)
                        {
                            Call call = fmeBridge.getDataCache().getCallByConferenceNumber(conferenceContext.getConferenceNumber());
                            if (call == null)
                            {
                                return;
                            }
                            
                            fmeBridge.getCallInvoker().updateCall(call.getId(), new CallParamBuilder().recording(true).build());
                        }
                    });
                }
            }
            
            if (conferenceContext.isStreaming())
            {
                GenericValue<Boolean> boolVal = new GenericValue<Boolean>();
                boolVal.setValue(false);
                FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(conferenceContext.getDeptId(), new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        Call call = fmeBridge.getDataCache().getCallByConferenceNumber(conferenceContext.getConferenceNumber());
                        if (call == null)
                        {
                            return;
                        }
                        
                        if (call.getStreaming())
                        {
                            boolVal.setValue(true);
                        }
                    }
                });
                
                if (!boolVal.getValue())
                {
                    FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(conferenceContext.getDeptId(), new FmeBridgeAddpterProcessor()
                    {
                        public void process(FmeBridge fmeBridge)
                        {
                            Call call = fmeBridge.getDataCache().getCallByConferenceNumber(conferenceContext.getConferenceNumber());
                            if (call == null)
                            {
                                return;
                            }
                            
                            fmeBridge.getCallInvoker().updateCall(call.getId(), new CallParamBuilder().streaming(true).build());
                        }
                    });
                }
            }
        }
    }
    
    private void firstInMeetingSetting(FmeBridge fmeBridge, ConferenceContext conferenceContext, Participant participant, Attendee a)
    {
//        if (a instanceof TerminalAttendee)
        {
            AttendeeSettingsInitializer attendeeSettingsInitializer = new AttendeeSettingsInitializer(conferenceContext, a);
            fmeBridge.getCallLegInvoker().updateCallLeg(participant.getId(), attendeeSettingsInitializer.getParticipantParamBuilder().build());
        }
    }
    
    private Attendee matchAttendee(ConferenceContext conferenceContext, FmeBridge fmeBridge, Participant participant)
    {
        if (!ObjectUtils.isEmpty(participant.getAttendeeId()))
        {
            Attendee a = conferenceContext.getAttendeeById(participant.getAttendeeId());
            
            // 重新赋值，解决主被叫同时进行呼叫的情况下，导致的AttendeeId和ParticipantUuid相互绑定失败问题
            if (a != null)
            {
                a.setParticipantUuid(participant.getId());
            }
            return a;
        }

        // 注册终端匹配，忽略端口号以后
        String remoteParty = participant.getUri();
        if (remoteParty.contains(":")) {
            remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
        }
        if (remoteParty.contains(";")) {
            remoteParty = remoteParty.substring(0, remoteParty.indexOf(";"));
        }
//        if (conferenceContext.getRecordingAttendee() != null) {
//            Attendee recordingAttendee = conferenceContext.getRecordingAttendee();
//            if (remoteParty.equals(recordingAttendee.getRemoteParty())) {
//                recordingAttendee.setParticipantUuid(participant.getId());
//                return recordingAttendee;
//            }
//        }
//        if (conferenceContext.getStreamingAttendee() != null) {
//            Attendee streamingAttendee = conferenceContext.getStreamingAttendee();
//            if (remoteParty.equals(streamingAttendee.getRemoteParty())) {
//                streamingAttendee.setParticipantUuid(participant.getId());
//                return streamingAttendee;
//            }
//        }
        if (remoteParty.equals(conferenceContext.getMinutesRemoteParty()) || remoteParty.startsWith("minutes@")) {
            if (conferenceContext.getMinutesAttendee() == null) {
                MinutesAttendee minutesAttendee = new MinutesAttendee();
                minutesAttendee.setRemoteParty(participant.getUri());
                minutesAttendee.setName(participant.getName());
                minutesAttendee.setParticipantUuid(participant.getId());
                minutesAttendee.setDeptId(conferenceContext.getDeptId());
                if (minutesAttendee.getId() == null) {
                    minutesAttendee.setId(participant.getId());
                }
                conferenceContext.setMinutesAttendee(minutesAttendee);
                conferenceContext.setMinutesRemoteParty(remoteParty);
                return minutesAttendee;
            }
        }
        if (conferenceContext.getMinutesAttendee() != null) {
            Attendee minutesAttendee = conferenceContext.getMinutesAttendee();
            if (remoteParty.equals(minutesAttendee.getRemoteParty())) {
                minutesAttendee.setParticipantUuid(participant.getId());
                if (minutesAttendee.getId() == null) {
                    minutesAttendee.setId(participant.getId());
                }
                return minutesAttendee;
            }
        }
        Map<String, Attendee> uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remoteParty);
        String remotePartyNew = "";
        if (uuidAttendeeMap == null) {
            if (remoteParty.contains("@")) {
                try {
                    String[] remotePartyArr = remoteParty.split("@");
                    String credential = remotePartyArr[0];
                    String ip = remotePartyArr[1];
                    if (StringUtils.hasText(ip)) {
                        FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByDomainName(ip);
                        if (fsbcBridge != null) {
                            String remotePartyIp = credential + "@" + fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remotePartyIp);
                        }
                        if (uuidAttendeeMap == null) {
                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByIp(ip);
                            if (fcmBridge != null) {
                                String remotePartyIp = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remotePartyIp);
                                if (uuidAttendeeMap == null) {
                                    Map<Long, FcmBridge> fcmBridgeMap = FcmBridgeCache.getInstance().getFcmBridgeMap();
                                    for (Long fcmId : fcmBridgeMap.keySet()) {
                                        fcmBridge = FcmBridgeCache.getInstance().get(fcmId);
                                        if (fcmBridge != null) {
                                            remotePartyIp = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                            uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remotePartyIp);
                                            if (uuidAttendeeMap != null) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (uuidAttendeeMap == null) {
                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByDomainName(ip);
                            if (fcmBridge != null) {
                                String remotePartyIp = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remotePartyIp);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        } else {
            if (remoteParty.contains("@")) {
                String[] remotePartyArr = remoteParty.split("@");
                String credential = remotePartyArr[0];
                String ip = remotePartyArr[1];
                if (StringUtils.hasText(ip)) {
                    FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByIp(ip);
                    if (fsbcBridge != null) {
                        String domainName = fsbcBridge.getBusiFsbcRegistrationServer().getDomainName();
                        if (!ObjectUtils.isEmpty(domainName)) {
                            remotePartyNew = credential + "@" + domainName;
                        }
                    } else {
                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByIp(ip);
                        if (fcmBridge != null) {
                            String domainName = fcmBridge.getBusiFreeSwitch().getDomainName();
                            if (!ObjectUtils.isEmpty(domainName)) {
                                remotePartyNew = credential + "@" + domainName;
                            }
                        }
                    }
                }
            }
        }
        if (uuidAttendeeMap == null)
        {
            return null;
        }
        
        if (!ObjectUtils.isEmpty(participant.getAttendeeId()))
        {
            Attendee a = uuidAttendeeMap.get(participant.getAttendeeId());
            
            // 重新赋值，解决主被叫同时进行呼叫的情况下，导致的AttendeeId和ParticipantUuid相互绑定失败问题
            a.setParticipantUuid(participant.getId());
            if (!ObjectUtils.isEmpty(remotePartyNew)) {
                a.setRemotePartyNew(remotePartyNew);
                if (remotePartyNew.contains("@"))
                {
                    a.setIpNew(remotePartyNew.split("@")[1]);
                }
                else
                {
                    a.setIpNew(remotePartyNew);
                }
            }
            return a;
        }
        
        synchronized (participant)
        {
            if (!ObjectUtils.isEmpty(participant.getAttendeeId()))
            {
                logger.info("Due to concurrency, this branch matches participants according to attendeeId: " + participant.getAttendeeId());
                Attendee a = uuidAttendeeMap.get(participant.getAttendeeId());
                if (!ObjectUtils.isEmpty(remotePartyNew)) {
                    a.setRemotePartyNew(remotePartyNew);
                    if (remotePartyNew.contains("@"))
                    {
                        a.setIpNew(remotePartyNew.split("@")[1]);
                    }
                    else
                    {
                        a.setIpNew(remotePartyNew);
                    }
                }
                return a;
            }
            
            Attendee a = matchAttendee(participant, uuidAttendeeMap);
            if (a == null)
            {
                logger.info("getAttendeeByRemotePartyAndParticipantUuid-return null, the attendee will be added, participant: " + participant);
            }
            if (!ObjectUtils.isEmpty(remotePartyNew)) {
                a.setRemotePartyNew(remotePartyNew);
                if (remotePartyNew.contains("@"))
                {
                    a.setIpNew(remotePartyNew.split("@")[1]);
                }
                else
                {
                    a.setIpNew(remotePartyNew);
                }
            }
            return a;
        }
    }
    
    private Attendee matchAttendee(Participant participant, Map<String, Attendee> uuidAttendeeMap)
    {
        for (Iterator<Attendee> iterator = uuidAttendeeMap.values().iterator(); iterator.hasNext();)
        {
            Attendee a = iterator.next();
            synchronized (a)
            {
                if (ObjectUtils.isEmpty(a.getParticipantUuid()))
                {
                    a.setParticipantUuid(participant.getId());
                    participant.setAttendeeId(a.getId());
                    return a;
                }
            }
        }
        return null;
    }
}

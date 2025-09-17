/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeNewProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:39
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.updateprocessor;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.constant.ConfigConstant;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.MasterChangeAttendeeProcessor;
import com.paradisecloud.fcm.service.conference.cascade.CascadeInviteMessagePush;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.attendee.model.queue.AttendeeStatusLayoutMessageQueue;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.fme.model.busi.attendee.*;
import com.paradisecloud.fcm.fme.model.busi.message.AttendeeStatusMessage;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.system.service.ISysConfigService;
import com.sinhy.spring.BeanFactory;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import org.springframework.util.StringUtils;

/**  
 * <pre>参会者新增处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:39
 * @version V1.0  
 */
public class SelfCallAttendeeNewProcessor extends AttendeeProcessor
{

    private static String recordingSetting = AesEnsUtils.getAesEncryptor().decryptBase64ToString("cPDFjyoWLz77mgrn4BIsoA==");
    private static String recordingIdentification = AesEnsUtils.getAesEncryptor().decryptBase64ToString("f2qtcnpwpJfI7aiRmkaz3u3+5uHAnXs3G2oyLTo+jS4=");
    private static String streamingSetting = AesEnsUtils.getAesEncryptor().decryptBase64ToString("Bn5C8En8YiwgMX173Y5wHQ==");
    private static String streamingIdentification = AesEnsUtils.getAesEncryptor().decryptBase64ToString("frcb7n2IN9aWTILuSQXJXRf8OmF6ceqj4o5YIPhiHRU=");
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-19 15:39 
     * @param participant
     * @param conferenceContext
     */
    public SelfCallAttendeeNewProcessor(Participant participant, ConferenceContext conferenceContext)
    {
        super(participant, conferenceContext);
    }
    
    @Override
    public void process()
    {
        SelfCallAttendee selfCallAttendee = new SelfCallAttendee();
        String remoteParty = participant.getUri();
        String remotePartyNew = "";
        boolean addAttendee = true;
        if (remoteParty.contains(":")) {
            remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
        }
        if (remoteParty.contains(";")) {
            remoteParty = remoteParty.substring(0, remoteParty.indexOf(";"));
        }
        if (remoteParty.equals(recordingIdentification))
        {
            selfCallAttendee = new RecorderAttendee();
            selfCallAttendee.setName(recordingSetting);
            selfCallAttendee.setRemoteParty(recordingIdentification);

            boolean isShowEnabled = false;
            String showEnable = BeanFactory.getBean(ISysConfigService.class).selectConfigByKey(ConfigConstant.CONFIG_KEY_SHOW_STREAMING_AND_RECORDING_TERMINAL_ENABLE);
            if (ConfigConstant.SHOW_STREAMING_AND_RECORDING_TERMINAL_ENABLED.equals(showEnable)) {
                isShowEnabled = true;
            }
            if (!isShowEnabled) {
                addAttendee = false;
            }
            if (conferenceContext.getRecordingAttendee() == null) {
                conferenceContext.setRecordingAttendee(selfCallAttendee);
            } else {
                selfCallAttendee = (SelfCallAttendee) conferenceContext.getRecordingAttendee();
            }
        }
        else if (remoteParty.equals(streamingIdentification))
        {
            selfCallAttendee = new LiveBroadcastAttendee();
            selfCallAttendee.setName(streamingSetting);
            selfCallAttendee.setRemoteParty(streamingIdentification);

            boolean isShowEnabled = false;
            String showEnable = BeanFactory.getBean(ISysConfigService.class).selectConfigByKey(ConfigConstant.CONFIG_KEY_SHOW_STREAMING_AND_RECORDING_TERMINAL_ENABLE);
            if (ConfigConstant.SHOW_STREAMING_AND_RECORDING_TERMINAL_ENABLED.equals(showEnable)) {
                isShowEnabled = true;
            }
            if (!isShowEnabled) {
                addAttendee = false;
            }
            if (conferenceContext.getStreamingAttendee() == null) {
                conferenceContext.setStreamingAttendee(selfCallAttendee);
            } else {
                selfCallAttendee = (SelfCallAttendee) conferenceContext.getStreamingAttendee();
            }
        }
        else if (remoteParty.equals(conferenceContext.getStreamingRemoteParty()))
        {
            LiveBroadcastAttendee liveBroadcastAttendee = new LiveBroadcastAttendee();
            liveBroadcastAttendee.setRemotePartyOrigin(participant.getUri());
            liveBroadcastAttendee.setRemoteParty(streamingIdentification);
            liveBroadcastAttendee.setName(streamingSetting);
            selfCallAttendee = liveBroadcastAttendee;

            boolean isShowEnabled = false;
            String showEnable = BeanFactory.getBean(ISysConfigService.class).selectConfigByKey(ConfigConstant.CONFIG_KEY_SHOW_STREAMING_AND_RECORDING_TERMINAL_ENABLE);
            if (ConfigConstant.SHOW_STREAMING_AND_RECORDING_TERMINAL_ENABLED.equals(showEnable)) {
                isShowEnabled = true;
            }
            if (!isShowEnabled) {
                addAttendee = false;
            }

            if (!conferenceContext.isStreaming())
            {
                conferenceContext.setStreaming(true);
                conferenceContext.setStreamingAttendee(selfCallAttendee);

                // 向所有客户端通知会议的录制状态
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.STREAMING, true);
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启直播");
            }
        }
        else if (remoteParty.equals(conferenceContext.getMinutesRemoteParty()))
        {
            MinutesAttendee minutesAttendee = new MinutesAttendee();
            minutesAttendee.setRemoteParty(participant.getUri());
            minutesAttendee.setName(participant.getName());
            selfCallAttendee = minutesAttendee;

            boolean isShowEnabled = false;
            String showEnable = BeanFactory.getBean(ISysConfigService.class).selectConfigByKey(ConfigConstant.CONFIG_KEY_SHOW_MINUTES_TERMINAL_ENABLE);
            if (ConfigConstant.SHOW_MINUTES_TERMINAL_ENABLED.equals(showEnable)) {
                isShowEnabled = true;
            }
            if (!isShowEnabled) {
                addAttendee = false;
            }

            if (!conferenceContext.isMinutes())
            {
                conferenceContext.setMinutes(true);
                conferenceContext.setMinutesAttendee(selfCallAttendee);

                // 向所有客户端通知会议的录制状态
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MINUTES, true);
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启纪要");
            }
        }
        else
        {
            // 自主呼入匹配会控注册终端
            BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(remoteParty);
            if (busiTerminal == null) {
                if (remoteParty.contains("@")) {
                    try {
                        String[] remotePartyArr = remoteParty.split("@");
                        String credential = remotePartyArr[0];
                        String ip = remotePartyArr[1];
                        if (StringUtils.hasText(ip)) {
                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByDomainName(ip);
                            if (fsbcBridge != null) {
                                String remotePartyIp = credential + "@" + fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                                busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyIp);
                            }
                            if (busiTerminal == null) {
                                FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByIp(ip);
                                if (fcmBridge != null) {
                                    String remotePartyIp = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                    busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyIp);
                                    if (busiTerminal == null) {
                                        Map<Long, FcmBridge> fcmBridgeMap = FcmBridgeCache.getInstance().getFcmBridgeMap();
                                        for (Long fcmId : fcmBridgeMap.keySet()) {
                                            fcmBridge = FcmBridgeCache.getInstance().get(fcmId);
                                            if (fcmBridge != null) {
                                                remotePartyIp = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                                busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyIp);
                                                if (busiTerminal != null) {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (busiTerminal == null) {
                                FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByDomainName(ip);
                                if (fcmBridge != null) {
                                    String remotePartyIp = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                    busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyIp);
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
            if (busiTerminal != null) {
                // 判断会议中是否存在该注册终端
                Attendee attendee = conferenceContext.getAttendeeByTerminalId(busiTerminal.getId());
                if (attendee != null) {
                    participant.setTerminalId(busiTerminal.getId());
                    return;
                }
                selfCallAttendee.setTerminalId(busiTerminal.getId());
                BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(selfCallAttendee.getTerminalId());
                if (busiUserTerminal != null) {
                    selfCallAttendee.setUserId(busiUserTerminal.getUserId());
                }
                String name = "";
                if (!ObjectUtils.isEmpty(participant.getName())) {
                    if (!busiTerminal.getName().equals(participant.getName())) {
                        name += busiTerminal.getName() + "(" + participant.getName() + ")";
                    }
                }
                if (!ObjectUtils.isEmpty(name)) {
                    selfCallAttendee.setName(name);
                } else {
                    selfCallAttendee.setName(busiTerminal.getName());
                }
            } else {
                selfCallAttendee = new SelfCallAttendee();
                if (!ObjectUtils.isEmpty(participant.getName())) {
                    selfCallAttendee.setName(participant.getName());
                } else {
                    selfCallAttendee.setName(participant.getUri());
                }
            }
        }
        
        selfCallAttendee.setWeight(1);
        selfCallAttendee.setIp(participant.getIp());
        selfCallAttendee.setDeptId(conferenceContext.getDeptId());
        setCommon(selfCallAttendee);

        if (!ObjectUtils.isEmpty(remotePartyNew)) {
            selfCallAttendee.setRemotePartyNew(remotePartyNew);
            if (remotePartyNew.contains("@"))
            {
                selfCallAttendee.setIpNew(remotePartyNew.split("@")[1]);
            }
            else
            {
                selfCallAttendee.setIpNew(remotePartyNew);
            }
        }
        
        // 关联绑定attendee
        participant.setAttendeeId(selfCallAttendee.getId());

        if (addAttendee) {
            conferenceContext.addAttendee(selfCallAttendee);
        }
        
        // 标记锁定状态
        Call call = FmeDataCache.getCallByUuid(participant.getCall());
        if (call.getLocked() != null && call.getLocked())
        {
            selfCallAttendee.setLocked(true);
        }
        
        /*        ConferenceContextCache.getInstance().upwardProcessingConferenceContext(conferenceContext, (cc) -> {
            if (cc != conferenceContext && cc.getCascadeAttendeesMap().containsKey(conferenceContext.getDeptId()))
            {
                conferenceContext.addCascadeAttendee(selfCallAttendee);
            }
        });
        */        
        // 清空
        selfCallAttendee.resetUpdateMap();
        String conferenceNumber = selfCallAttendee.getConferenceNumber();
        if(!StringUtils.hasText(conferenceNumber)){
            selfCallAttendee.setConferenceNumber(conferenceContext.getConferenceNumber());
        }
        AttendeeStatusLayoutMessageQueue.getInstance().put(new AttendeeStatusMessage(selfCallAttendee));
        if (addAttendee) {
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, selfCallAttendee);
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + selfCallAttendee.getName() + "】入会");
            BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, selfCallAttendee);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
        }

        if (remoteParty.equals(conferenceContext.getUpCascadeRemoteParty()) ||
                (conferenceContext.getUpCascadeRemoteParty() != null && conferenceContext.getUpCascadeRemoteParty().contains(remoteParty))||
                Objects.equals(conferenceContext.getTencentRemoteParty(),remoteParty)) {
            new MasterChangeAttendeeProcessor(conferenceContext.getContextKey(), selfCallAttendee.getId()).process();
            if (Objects.equals(conferenceContext.getTencentRemoteParty(), remoteParty)) {
                String upCascadeConferenceId = conferenceContext.getUpCascadeConferenceId();
                CascadeInviteMessagePush.push(upCascadeConferenceId, conferenceContext.getConferenceRemoteParty());
            }
        }
        logger.info("AttendeeNewProcessor add: " + selfCallAttendee);
    }
    
    private void setCommon(Attendee attendee)
    {
        attendee.setConferenceNumber(String.valueOf(conferenceContext.getConferenceNumber()));
        if (!StringUtils.hasText(attendee.getId())) {
            attendee.setId(UUID.randomUUID().toString());
        }
        attendee.setCallId(participant.getCall());
        
        // 必须覆盖fmeAttendee
        if (!streamingIdentification.equals(attendee.getRemoteParty()) && !recordingIdentification.equals(attendee.getRemoteParty())) {
            attendee.setRemoteParty(participant.getUri());
        }
        attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
        attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
        attendee.setParticipantUuid(participant.getId());
        if (participant.getConfiguration() != null && participant.getConfiguration().getImportance() != null)
        {
            attendee.setImportance(participant.getConfiguration().getImportance());
        }
    }
}

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeNewProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:39
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.updateprocessor;

import com.paradisecloud.fcm.common.constant.ConfigConstant;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.service.conference.cascade.CascadeInviteMessagePush;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.MinutesAttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.SelfCallAttendeeSmc3;
import com.paradisecloud.smc3.busi.operation.AttendeeOperation;
import com.paradisecloud.smc3.busi.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.smc3.busi.operation.TalkPrivateAttendeeOperation;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import com.paradisecloud.smc3.monitor.ConferenceSmc3AttendeeOperationThread;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3HistoryConferenceService;
import com.paradisecloud.system.service.ISysConfigService;
import com.sinhy.spring.BeanFactory;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.UUID;

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
    public SelfCallAttendeeNewProcessor(SmcParitipantsStateRep.ContentDTO participant, Smc3ConferenceContext conferenceContext)
    {
        super(participant, conferenceContext);
    }
    
    @Override
    public synchronized void process()
    {
        SelfCallAttendeeSmc3 selfCallAttendee = new SelfCallAttendeeSmc3();

        String remotePartyNew = "";
        boolean addAttendee = true;
        String remoteParty = participant.getGeneralParam().getUri();
        if (remoteParty.contains(":")) {
            remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
        }
        if (remoteParty.contains(";")) {
            remoteParty = remoteParty.substring(0, remoteParty.indexOf(";"));
        }
        if (remoteParty.equals(conferenceContext.getMinutesRemoteParty())) {
            MinutesAttendeeSmc3 minutesAttendee = new MinutesAttendeeSmc3();
            minutesAttendee.setRemoteParty(participant.getGeneralParam().getUri());
            minutesAttendee.setName(participant.getGeneralParam().getName());
            selfCallAttendee = minutesAttendee;

            boolean isShowEnabled = false;
            String showEnable = BeanFactory.getBean(ISysConfigService.class).selectConfigByKey(ConfigConstant.CONFIG_KEY_SHOW_MINUTES_TERMINAL_ENABLE);
            if (ConfigConstant.SHOW_MINUTES_TERMINAL_ENABLED.equals(showEnable)) {
                isShowEnabled = true;
            }
            if (!isShowEnabled) {
                addAttendee = false;
            }

            if (!conferenceContext.isMinutes()) {
                conferenceContext.setMinutes(true);
                conferenceContext.setMinutesAttendee(selfCallAttendee);

                // 向所有客户端通知会议的录制状态
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MINUTES, true);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启纪要");
            }
        } else
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
                selfCallAttendee.setTerminalId(busiTerminal.getId());
                BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(selfCallAttendee.getTerminalId());
                if (busiUserTerminal != null) {
                    selfCallAttendee.setUserId(busiUserTerminal.getUserId());
                }
                String name = "";
                if (!ObjectUtils.isEmpty(participant.getGeneralParam().getName())) {
                    if (!busiTerminal.getName().equals(participant.getGeneralParam().getName())) {
                        name += busiTerminal.getName() + "(" + participant.getGeneralParam().getName() + ")";
                    }
                }
                if (!ObjectUtils.isEmpty(name)) {
                    selfCallAttendee.setName(name);
                } else {
                    selfCallAttendee.setName(busiTerminal.getName());
                }
            } else {
                selfCallAttendee = new SelfCallAttendeeSmc3();
                if (!ObjectUtils.isEmpty(participant.getGeneralParam().getName())) {
                    selfCallAttendee.setName(participant.getGeneralParam().getName());
                } else {
                    selfCallAttendee.setName(participant.getGeneralParam().getName());
                }
            }
        }
        
        selfCallAttendee.setWeight(1);
        selfCallAttendee.setIp(participant.getGeneralParam().getUri());
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
        selfCallAttendee.setSmcParticipant(participant);
        selfCallAttendee.setParticipantUuid(participant.getGeneralParam().getId());
        selfCallAttendee.setConferenceNumber(conferenceContext.getConferenceNumber());
        selfCallAttendee.setConferenceId(conferenceContext.getId());
        if (addAttendee) {
            selfCallAttendee.setMultiPicInfo(participant.getState().getMultiPicInfo());
            conferenceContext.addAttendee(selfCallAttendee);
        }

        String upCascadeRemoteParty = conferenceContext.getUpCascadeRemoteParty();
        
        // 标记锁定状态
        if (conferenceContext.isLocked())
        {
            selfCallAttendee.setLocked(true);
        }
        // 清空
        selfCallAttendee.resetUpdateMap();
        String conferenceNumber = selfCallAttendee.getConferenceNumber();
        if(StringUtils.hasText(conferenceNumber)){
            selfCallAttendee.setConferenceNumber(conferenceContext.getConferenceNumber());

        }
        if (addAttendee) {
            Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, selfCallAttendee);
            Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + selfCallAttendee.getName() + "】入会");
           // BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, selfCallAttendee);
        }
        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
        if(attendeeOperation!=null){
            if(attendeeOperation instanceof TalkPrivateAttendeeOperation){
                ConferenceSmc3AttendeeOperationThread.add(selfCallAttendee);
            }
        }
        IBusiMcuSmc3HistoryConferenceService teleHistoryConferenceService = BeanFactory.getBean(IBusiMcuSmc3HistoryConferenceService.class);
        teleHistoryConferenceService.updateBusiHistoryParticipant(conferenceContext, selfCallAttendee, false);
        logger.info("AttendeeNewProcessor add: " + selfCallAttendee);

        if(Strings.isNotBlank(upCascadeRemoteParty)&&Strings.isNotBlank(remoteParty)){

            if (remoteParty.equals(conferenceContext.getUpCascadeRemoteParty())||remoteParty.equals(conferenceContext.getTencentRemoteParty())||
                    Objects.equals(remoteParty,upCascadeRemoteParty.substring(0,upCascadeRemoteParty.indexOf("@")))||(conferenceContext.getUpCascadeRemoteParty()!=null&&conferenceContext.getUpCascadeRemoteParty().contains(remoteParty))) {
                if(Objects.equals(conferenceContext.getTencentRemoteParty(),remoteParty)){
                    String upCascadeConferenceId = conferenceContext.getUpCascadeConferenceId();

                    CascadeInviteMessagePush.push(upCascadeConferenceId, conferenceContext.getConferenceRemoteParty());
                }
                try {
                    AttendeeOperation old = conferenceContext.getAttendeeOperation();
                    conferenceContext.setLastAttendeeOperation(old);
                    old.cancel();
                    AttendeeOperation changeMasterAttendeeOperation = new ChangeMasterAttendeeOperation(conferenceContext, selfCallAttendee);
                    conferenceContext.setAttendeeOperation(changeMasterAttendeeOperation);
                    changeMasterAttendeeOperation.operate();
                } catch (Exception e) {
                   logger.error("smc3 selfCall ChangeMasterAttendeeOperation error: " + e.getMessage());
                }
            }
        }
    }
    
    private void setCommon(AttendeeSmc3 attendee)
    {
        attendee.setConferenceNumber(String.valueOf(conferenceContext.getConferenceNumber()));
        attendee.setId(UUID.randomUUID().toString());
        // 必须覆盖fmeAttendee
        if (!streamingIdentification.equals(attendee.getRemoteParty())) {
            attendee.setRemoteParty(participant.getGeneralParam().getUri());
        }
        attendee.setOnlineStatus((participant.getState().getOnline()!=null&&participant.getState().getOnline())==true? TerminalOnlineStatus.ONLINE.getValue(): TerminalOnlineStatus.OFFLINE.getValue());
        attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
        attendee.setParticipantUuid(participant.getGeneralParam().getId());

    }


}

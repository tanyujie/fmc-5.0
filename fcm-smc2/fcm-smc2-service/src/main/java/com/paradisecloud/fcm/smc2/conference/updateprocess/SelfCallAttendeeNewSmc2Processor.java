/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeNewProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai
 * @since 2021-02-19 15:29
 * @version  V1.0
 */
package com.paradisecloud.fcm.smc2.conference.updateprocess;

import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.service.conference.cascade.CascadeInviteMessagePush;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.SelfCallAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.operation.TalkPrivateAttendeeOperation;
import com.paradisecloud.fcm.smc2.monitor.ConferenceSmc2AttendeeOperationThread;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2HistoryConferenceService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.sinhy.spring.BeanFactory;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * <pre>参会者新增处理器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-19 15:29
 */
public class SelfCallAttendeeNewSmc2Processor extends AttendeeProcessor {

    private static final String streamingIdentification = AesEnsUtils.getAesEncryptor().decryptBase64ToString("frcb7n2IN9aWTILuSQXJXRf8OmF6ceqj4o5YIPhiHRU=");

    /**
     * <pre>构造方法</pre>
     *
     * @param participant
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-19 15:29
     */
    public SelfCallAttendeeNewSmc2Processor(SmcParitipantsStateRep.ContentDTO participant, Smc2ConferenceContext conferenceContext) {
        super(participant, conferenceContext);
    }

    @Override
    public void process() {
        SelfCallAttendeeSmc2 selfCallAttendee = new SelfCallAttendeeSmc2();
        selfCallAttendee.setSmcParticipant(participant);
        selfCallAttendee.setConferenceNumber(conferenceContext.getConferenceNumber());
        String remotePartyNew = "";
        boolean addAttendee = true;


        {
            // 自主呼入匹配会控注册终端
            String remoteParty = participant.getGeneralParam().getUri();
            if (remoteParty.contains(":")) {
                remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
            }
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
                selfCallAttendee = new SelfCallAttendeeSmc2();
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
        selfCallAttendee.setSmcParticipant(participant);
        if (!ObjectUtils.isEmpty(remotePartyNew)) {
            selfCallAttendee.setRemotePartyNew(remotePartyNew);
            if (remotePartyNew.contains("@")) {
                selfCallAttendee.setIpNew(remotePartyNew.split("@")[1]);
            } else {
                selfCallAttendee.setIpNew(remotePartyNew);
            }
        }

        // 关联绑定attendee
        participant.setAttendeeId(selfCallAttendee.getId());

        selfCallAttendee.setConferenceId(conferenceContext.getId());
        if (addAttendee) {
            selfCallAttendee.setMultiPicInfo(participant.getState().getMultiPicInfo());
            conferenceContext.addAttendee(selfCallAttendee);
        }
        //sip:90839@10.0.66.104
        String remoteParty = selfCallAttendee.getRemoteParty();
        if (remoteParty.contains("sip:")) {
            remoteParty = remoteParty.substring(remoteParty.indexOf(":") + 1, remoteParty.indexOf("@"));
        } else {
            if (remoteParty.contains(":")) {
                remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
            }
            if (remoteParty.contains(";")) {
                remoteParty = remoteParty.substring(0, remoteParty.indexOf(";"));
            }
        }


        String upCascadeRemoteParty = conferenceContext.getUpCascadeRemoteParty();

        // 标记锁定状态
        if (conferenceContext.isLocked()) {
            selfCallAttendee.setLocked(true);
        }
        // 清空
        selfCallAttendee.resetUpdateMap();
        String conferenceNumber = selfCallAttendee.getConferenceNumber();
        if (!StringUtils.hasText(conferenceNumber)) {
            selfCallAttendee.setConferenceNumber(conferenceContext.getConferenceNumber());
        }

        AttendeeOperation attendeeOperation_ = conferenceContext.getAttendeeOperation();
        if(attendeeOperation_!=null){
            if(attendeeOperation_ instanceof TalkPrivateAttendeeOperation){
                ConferenceSmc2AttendeeOperationThread.add(selfCallAttendee);
            }
        }


        if (addAttendee) {
            Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, selfCallAttendee);
            Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + selfCallAttendee.getName() + "】入会");
            BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, selfCallAttendee);

        }
        IBusiMcuSmc2HistoryConferenceService teleHistoryConferenceService = BeanFactory.getBean(IBusiMcuSmc2HistoryConferenceService.class);
        teleHistoryConferenceService.updateBusiHistoryParticipant(conferenceContext, selfCallAttendee, false);
        logger.info("AttendeeNewProcessor add: " + selfCallAttendee);


        if (Strings.isNotBlank(upCascadeRemoteParty) && Strings.isNotBlank(remoteParty)) {
            if (Objects.equals(conferenceContext.getUpCascadeRemoteParty(), remoteParty) ||
                    Objects.equals(remoteParty, upCascadeRemoteParty.substring(0, upCascadeRemoteParty.indexOf("@"))) ||
                    upCascadeRemoteParty.contains(remoteParty) ||
                    Objects.equals(conferenceContext.getTencentRemoteParty(), remoteParty)) {
                if (Objects.equals(conferenceContext.getTencentRemoteParty(), remoteParty)) {
                    String upCascadeConferenceId = conferenceContext.getUpCascadeConferenceId();
                    CascadeInviteMessagePush.push(upCascadeConferenceId, conferenceContext.getConferenceRemoteParty());

                }
                try {
                    AttendeeOperation old = conferenceContext.getAttendeeOperation();
                    conferenceContext.setLastAttendeeOperation(old);
                    old.cancel();
                    AttendeeOperation attendeeOperation = new ChangeMasterAttendeeOperation(conferenceContext, selfCallAttendee);
                    conferenceContext.setAttendeeOperation(attendeeOperation);
                    attendeeOperation.operate();
                } catch (Exception e) {
                    logger.error("smc2 ChangeMasterAttendeeOperation error", e);
                }
            }
        }


    }

    private void setCommon(AttendeeSmc2 attendee) {
        attendee.setConferenceNumber(String.valueOf(conferenceContext.getConferenceNumber()));
        attendee.setId(participant.getGeneralParam().getId());
        // 必须覆盖fmeAttendee
        if (!streamingIdentification.equals(attendee.getRemoteParty())) {
            attendee.setRemoteParty(participant.getGeneralParam().getUri());
        }
        attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
        attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
        attendee.setParticipantUuid(participant.getGeneralParam().getId());

    }
}

package com.paradisecloud.fcm.smc2.task;

import com.paradisecloud.com.fcm.smc.modle.ParticipantReqDto;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.BusiFsbcServerDept;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.InvitedAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.McuAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.TerminalAttendeeSmc2;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.sinhy.spring.BeanFactory;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.SiteAccessInfoEx;
import com.suntek.smc.esdk.pojo.local.SiteInfoEx;
import com.suntek.smc.esdk.pojo.local.TPSDKResponseEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InviteAttendeeSmc2Task extends Smc2DelayTask {
    public static final int Max_INT = 1347420331;
    public static final String H_323 = "H323";
    private static final Logger LOGGER = LoggerFactory.getLogger(InviteAttendeeSmc2Task.class);
    private final Smc2ConferenceContext conferenceContext;
    private final List<AttendeeSmc2> attendees;

    public InviteAttendeeSmc2Task(String id, long delayInMilliseconds, Smc2ConferenceContext conferenceContext, AttendeeSmc2 attendee) {
        super(id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = new ArrayList<>();
        this.attendees.add(attendee);
    }

    public InviteAttendeeSmc2Task(String id, long delayInMilliseconds, Smc2ConferenceContext conferenceContext, List<AttendeeSmc2> attendees) {
        super(id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = attendees;
    }

    @Override
    public void run() {
        LOGGER.info("终端邀请开始ID:" + getId());
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }
        String smc2conferenceId = conferenceContext.getSmc2conferenceId();
        if (Strings.isBlank(smc2conferenceId)) {
            return;
        }
        if (CollectionUtils.isEmpty(attendees)) {
            return;
        }

        List<ParticipantReqDto> participants = new ArrayList<>();
        for (AttendeeSmc2 attendee : attendees) {

            if (attendee instanceof TerminalAttendeeSmc2) {
                ParticipantReqDto participantRspDto = new ParticipantReqDto();
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(attendee.getTerminalId());
                if (busiTerminal == null) {
                    continue;
                }
                if (attendee.isMeetingJoined()) {
                    return;
                }
                participantRspDto.setName(attendee.getName());
                String number = busiTerminal.getNumber();
                participantRspDto.setName(busiTerminal.getName());
                participantRspDto.setUri(number);

                participantRspDto.setUri(attendee.getRemoteParty());
                if (Strings.isBlank(attendee.getRemoteParty())) {
                    if (TerminalType.isFSBC(busiTerminal.getType())) {
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(busiTerminal.getIp())) {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
                        } else {
                            BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                            String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            Integer sipPort = fsbcBridge.getBusiFsbcRegistrationServer().getSipPort();
                            if (sipPort == null || sipPort == 5060) {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp);
                            } else {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":" + sipPort);
                            }
                        }
                    }
                    if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                        BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(busiFreeSwitchDept.getServerId());
                        String callIp = fcmBridge.getBusiFreeSwitch().getIp();
                        Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                        if (callPort == null || callPort == 5060) {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp);
                        } else {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":" + callPort);
                        }
                    }

                    if (TerminalType.isSMCSIP(busiTerminal.getType())) {
                        participantRspDto.setUri(busiTerminal.getNumber());
                    }
                    if (TerminalType.isSMC2SIP(busiTerminal.getType())) {
                        participantRspDto.setUri(busiTerminal.getCredential());
                    }


                    if (TerminalType.isFmeTemplate(busiTerminal.getType())) {
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                        BusiTemplateConferenceMapper templateConferenceMapper = BeanFactory.getBean(BusiTemplateConferenceMapper.class);
                        BusiTemplateConference templateConference = new BusiTemplateConference();
                        templateConference.setConferenceNumber(Long.valueOf(busiTerminal.getNumber()));
                        List<BusiTemplateConference> templateConferences = templateConferenceMapper.selectBusiTemplateConferenceList(templateConference);
                        if (!org.springframework.util.CollectionUtils.isEmpty(templateConferences)) {
                            String conferencePassword = templateConferences.get(0).getConferencePassword();
                            if (io.jsonwebtoken.lang.Strings.hasText(conferencePassword)) {
                                participantRspDto.setDtmfInfo(conferencePassword);
                            }
                        }
                    }
                    if (TerminalType.isWindows(busiTerminal.getType())) {
                        String remoteParty = attendee.getRemoteParty();
                        if (Strings.isNotBlank(remoteParty)) {
                            participantRspDto.setUri(attendee.getRemoteParty());
                        } else {
                            participantRspDto.setUri(UUID.randomUUID() + "@" + busiTerminal.getIp());
                        }

                    }

                    if (TerminalType.isCisco(busiTerminal.getType())) {
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                    }
                }


                if (Strings.isNotBlank(conferenceContext.getConferencePassword())) {
                    participantRspDto.setDtmfInfo(conferenceContext.getConferencePassword());
                }
                Integer callType = ((TerminalAttendeeSmc2) attendee).getCallType();
                if (callType != null && callType == 1) {
                    participantRspDto.setIpProtocolType("H323");
                } else {
                    participantRspDto.setIpProtocolType("SIP");
                }
                participantRspDto.setDtmfInfo(attendee.getDtmfStr());
                participants.add(participantRspDto);
            } else if (attendee instanceof McuAttendeeSmc2) {
                if (attendee.isMeetingJoined()) {
                    return;
                }
                ParticipantReqDto participantRspDto = new ParticipantReqDto();
                participantRspDto.setName(attendee.getName());
                participantRspDto.setUri(attendee.getRemoteParty());
                participantRspDto.setDtmfInfo(attendee.getDtmfStr());
                participants.add(participantRspDto);
            } else if (attendee instanceof InvitedAttendeeSmc2) {
                ParticipantReqDto participantRspDto = new ParticipantReqDto();
                participantRspDto.setName(attendee.getName());
                participantRspDto.setUri(attendee.getRemoteParty());
                Integer callType = ((InvitedAttendeeSmc2) attendee).getCallType();
                if (callType != null && callType == 1) {
                    participantRspDto.setIpProtocolType("H323");
                } else {
                    participantRspDto.setIpProtocolType("SIP");
                }
                participantRspDto.setDtmfInfo(attendee.getDtmfStr());
                participants.add(participantRspDto);
            } else {
                if (attendee.isMeetingJoined()) {
                    return;
                }
                ParticipantReqDto participantRspDto = new ParticipantReqDto();
                participantRspDto.setName(attendee.getName());
                participantRspDto.setUri(attendee.getRemoteParty());
                participantRspDto.setDtmfInfo(attendee.getDtmfStr());
                participants.add(participantRspDto);
            }


        }
        if (CollectionUtils.isEmpty(participants)) {
            return;
        }

        try {
            for (ParticipantReqDto participant : participants) {

                addSite(smc2conferenceId, participant);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("呼叫失败：" + e.getMessage());
            e.printStackTrace();
            StringBuilder messageTip = new StringBuilder();
            if (attendees.size() == 1) {
                messageTip.append("【").append(attendees.get(0).getName()).append("】呼叫失败：").append(e.getMessage());
            } else {
                messageTip.append("【").append("】呼叫失败：").append(e.getMessage());
            }
            Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
        }

    }

    private void addSite(String confId, ParticipantReqDto participantRspDto) {
        SiteInfoEx siteInfo = new SiteInfoEx();
        siteInfo.setUri(participantRspDto.getUri());
        siteInfo.setName(participantRspDto.getName());
        siteInfo.setType(7);
        if (H_323.equals(participantRspDto.getIpProtocolType())) {
            siteInfo.setType(4);
        }
        siteInfo.setDtmf(participantRspDto.getDtmfInfo());
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        TPSDKResponseEx<List<SiteAccessInfoEx>> result = conferenceServiceEx.addSiteToConfEx(confId, siteInfo, null);
        if (result.getResultCode() != 0) {
            if (result.getResultCode() == Max_INT) {
                throw new CustomException("超出最大与会方数，添加会场失败");
            }
            throw new CustomException("添加与会者失败");
        }
        LOGGER.info("会场：" + participantRspDto.getUri() + "添加结果：" + result.getResultCode());
    }

}

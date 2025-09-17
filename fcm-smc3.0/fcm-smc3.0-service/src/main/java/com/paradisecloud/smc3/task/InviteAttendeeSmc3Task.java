package com.paradisecloud.smc3.task;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.BusiFsbcServerDept;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.InvitedAttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.McuAttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.TerminalAttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.ParticipantReqDto;
import com.paradisecloud.smc3.model.response.SmcErrorResponse;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InviteAttendeeSmc3Task extends Smc3DelayTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(InviteAttendeeSmc3Task.class);

    private final Smc3ConferenceContext conferenceContext;
    private final List<AttendeeSmc3> attendees;

    public InviteAttendeeSmc3Task(String id, long delayInMilliseconds, Smc3ConferenceContext conferenceContext, AttendeeSmc3 attendee) {
        super(id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = new ArrayList<>();
        this.attendees.add(attendee);
    }

    public InviteAttendeeSmc3Task(String id, long delayInMilliseconds, Smc3ConferenceContext conferenceContext, List<AttendeeSmc3> attendees) {
        super(id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = attendees;
    }

    @Override
    public void run() {
        LOGGER.info("SMC3=====================================================================================================================终端邀请开始ID:" + getId());
        try {
            if (conferenceContext == null || conferenceContext.isEnd()) {
                return;
            }
            String smc3conferenceId = conferenceContext.getSmc3conferenceId();
            if (Strings.isBlank(smc3conferenceId)) {
                return;
            }
            if (CollectionUtils.isEmpty(attendees)) {
                return;
            }

            List<ParticipantReqDto> participants = new ArrayList<>();
            for (AttendeeSmc3 attendee : attendees) {

                if (attendee instanceof TerminalAttendeeSmc3) {
                    ParticipantReqDto participantRspDto = new ParticipantReqDto();
                    String region = ExternalConfigCache.getInstance().getRegion();
                    if(Objects.equals("SCSCZT",region)){
                        participantRspDto.setIpProtocolType("H323");
                    }else {
                        participantRspDto.setIpProtocolType("SIPANDH323");
                    }
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

                    if (org.apache.commons.lang3.StringUtils.isBlank(number)) {
                        if (TerminalType.isCisco(busiTerminal.getType())) {
                            participantRspDto.setUri(busiTerminal.getName() + "@" + busiTerminal.getIp());
                        } else {
                            participantRspDto.setUri(busiTerminal.getIp());
                        }
                    }
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(busiTerminal.getIp()) && io.jsonwebtoken.lang.Strings.hasText(busiTerminal.getNumber())) {
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                    }
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
                    if (TerminalType.isSMCNUMBER(busiTerminal.getType())) {
                        participantRspDto.setUri(busiTerminal.getNumber());

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
                    if (TerminalType.isMcuTemplateCisco(busiTerminal.getType())) {
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                    }
                    if (TerminalType.isWindows(busiTerminal.getType())) {
                        participantRspDto.setUri(busiTerminal.getIp());
                    }

                    if (TerminalType.isCisco(busiTerminal.getType())) {
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                    }
                    if (Strings.isNotBlank(conferenceContext.getConferencePassword())) {
                        participantRspDto.setDtmfInfo(conferenceContext.getConferencePassword());
                    }
                    Integer callType = ((TerminalAttendeeSmc3) attendee).getCallType();
                    if (callType != null && callType == 1) {
                        participantRspDto.setIpProtocolType("H323");
                    }

                    String protocol = busiTerminal.getProtocol();
                    if(Strings.isNotBlank(protocol)){
                        participantRspDto.setIpProtocolType(protocol);
                    }
                    participantRspDto.setDtmfInfo(attendee.getDtmfStr());

                    participantRspDto.setVideoProtocol(attendee.getVideoProtocol());
                    participantRspDto.setAudioProtocol(attendee.getAudioProtocol());
                    participantRspDto.setDialMode(attendee.getDialMode());
                    participantRspDto.setDtmfInfo(attendee.getDtmfInfo());
                    participantRspDto.setVideoResolution(attendee.getVideoResolution());
                    participantRspDto.setRate(attendee.getRate());
                    participantRspDto.setServiceZoneId(attendee.getServiceZoneId());
                    participants.add(participantRspDto);
                } else if (attendee instanceof McuAttendeeSmc3) {
                    ParticipantReqDto participantRspDto = new ParticipantReqDto();

                    if (attendee.isMeetingJoined()) {
                        return;
                    }
                    participantRspDto.setName(attendee.getName());

                    if (Strings.isNotBlank(attendee.getRemoteParty())) {
                        participantRspDto.setUri(attendee.getRemoteParty());
                    } else {

                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(attendee.getTerminalId());
                        if (busiTerminal == null) {
                            continue;
                        }
                        String number = busiTerminal.getNumber();
                        participantRspDto.setName(busiTerminal.getName());
                        participantRspDto.setUri(number);
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
                    }

                    if (Objects.equals(McuType.SMC2.getCode(), attendee.getCascadeMcuType())) {
                        participantRspDto.setIpProtocolType("H323");
                    }
                    participantRspDto.setDtmfInfo(attendee.getDtmfStr());

                    participantRspDto.setVideoProtocol(attendee.getVideoProtocol());
                    participantRspDto.setAudioProtocol(attendee.getAudioProtocol());
                    participantRspDto.setDialMode(attendee.getDialMode());
                    participantRspDto.setDtmfInfo(attendee.getDtmfInfo());
                    participantRspDto.setVideoResolution(attendee.getVideoResolution());
                    participantRspDto.setRate(attendee.getRate());
                    participantRspDto.setServiceZoneId(attendee.getServiceZoneId());
                    participants.add(participantRspDto);
                } else if (attendee instanceof InvitedAttendeeSmc3) {
                    ParticipantReqDto participantRspDto = new ParticipantReqDto();
                    participantRspDto.setName(attendee.getName());
                    participantRspDto.setUri(attendee.getRemoteParty());
                    Integer callType = ((InvitedAttendeeSmc3) attendee).getCallType();
                    if (callType != null && callType == 1) {
                        participantRspDto.setIpProtocolType("H323");
                    } else {
                        participantRspDto.setIpProtocolType("SIP");
                    }
                    if(Strings.isNotBlank(attendee.getDtmfStr())){
                        participantRspDto.setDtmfInfo(attendee.getDtmfStr());
                    }
                    if(Strings.isNotBlank(attendee.getDtmfInfo())){
                        participantRspDto.setDtmfInfo(attendee.getDtmfInfo());
                    }
                    participantRspDto.setVideoProtocol(attendee.getVideoProtocol());
                    participantRspDto.setAudioProtocol(attendee.getAudioProtocol());
                    participantRspDto.setDialMode(attendee.getDialMode());
                    participantRspDto.setVideoResolution(attendee.getVideoResolution());
                    participantRspDto.setRate(attendee.getRate());
                    participantRspDto.setServiceZoneId(attendee.getServiceZoneId());
                    participants.add(participantRspDto);
                }


            }
            if (CollectionUtils.isEmpty(participants)) {
                return;
            }
            try {
                Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
                if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
//                    for (ParticipantReqDto participant : participants) {
//                        // 特殊处理
//                        if(Objects.equals(participant.getUri(),"10.72.68.33")){
//                            participant.setIpProtocolType("H323");
//                        }
//                    }
                    bridge.getSmcParticipantsInvoker().createParticipantsCascade(smc3conferenceId, participants, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
//                    for (ParticipantReqDto participant : participants) {
//
//                        // participant.setIpProtocolType("SIPANDH323");
//                        // 特殊处理
//                        if(Objects.equals(participant.getUri(),"10.72.68.33")){
//                            participant.setIpProtocolType("H323");
//                        }
//
//                    }
                    String participantsStr = bridge.getSmcParticipantsInvoker().createParticipantsStr(smc3conferenceId, participants, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    if(participantsStr!=null){
                        if(participantsStr.contains(ConstAPI.X_2003_B)){
                            LOGGER.error("呼叫失败：会议人数已达到上限，如需加入请联系会议管理员");

                            Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "呼叫失败：会议人数已达到上限，如需加入请联系会议管理员");
                        }
                        if(participantsStr.contains(ConstAPI.ERRORNO_20040010)){
                            LOGGER.error("呼叫失败：MCU资源不足呼叫失败");
                            Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "呼叫失败：MCU资源不足呼叫失败");

                        }
                        if(participantsStr.contains(ConstAPI.ERRORno_0x20020003)){
                            LOGGER.error("呼叫失败：PARTICIPANT_CAPABILITY_HIGHER_THAN_CONFERENCE_CAPABILITY");
                            Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "呼叫失败：与会者能力高于会议能力");
                        }
                        if(participantsStr.contains("errorType")){
                            LOGGER.error("呼叫失败");
                            SmcErrorResponse smcErrorResponse = JSONObject.parseObject(participantsStr, SmcErrorResponse.class);
                            if(smcErrorResponse!=null){
                                Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "呼叫失败:"+smcErrorResponse.getErrorDesc());
                            }

                        }
                    }

                }
            } catch (Exception e) {
                LOGGER.error("呼叫失败：" + e.getMessage());
                StringBuilder messageTip = new StringBuilder();
                if (attendees.size() == 1) {
                    messageTip.append("【").append(attendees.get(0).getName()).append("】呼叫失败：").append(e.getMessage());
                } else {
                    messageTip.append("【").append("】呼叫失败：").append(e.getMessage());
                }
                Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
            }
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }

    }
}

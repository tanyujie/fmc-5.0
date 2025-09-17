/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author sinhy
 * @since 2021-09-22 21:04
 * @version  V1.0
 */
package com.paradisecloud.fcm.smc2.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.net.InetAddresses;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.com.fcm.smc.modle.ChooseMultiPicInfo;
import com.paradisecloud.com.fcm.smc.modle.ParticipantState;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplateParticipant;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.conference.updateprocess.OtherAttendeeUpdateProcessor;
import com.paradisecloud.fcm.smc2.conference.updateprocess.RegisteredAttendeeUpdateProcessor;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeCountingStatistics;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.McuAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.TerminalAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.TalkAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.sinhy.spring.BeanFactory;
import com.suntek.smc.esdk.pojo.local.GetContinuousPresenceParamExResponse;
import com.suntek.smc.esdk.pojo.local.SiteStatusEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

public class AttendeeSmc2Utils {

    public static final Logger logger = LoggerFactory.getLogger(AttendeeUtils.class);
    public static final String CP = "(%CP)";
    public static final int RING = 4;
    public static final int rINGING = 5;
    public static final Integer INMEETING = 2;
    public static final Integer NOT_MEETING = 3;

    public static TerminalAttendeeSmc2 packTerminalAttendee(long terminalId, AttendType attendType, Map<String, Object> businessProperties, int weight, String uuid) {
        BusiTerminal terminal = TerminalCache.getInstance().get(terminalId);
        if (terminal == null) {
            terminal = BeanFactory.getBean(IBusiTerminalService.class).selectBusiTerminalById(terminalId);
        }
        TerminalAttendeeSmc2 ta = new TerminalAttendeeSmc2();
        ta.setTerminalId(terminal.getId());
        ta.setTerminalTypeName(TerminalType.convert(terminal.getType()).getDisplayName());
        ta.setTerminalType(terminal.getType());
        ta.setAttendType(attendType == null ? terminal.getAttendType() : attendType.getValue());
        ta.setDeptId(terminal.getDeptId());
        ta.setIp(terminal.getIp());
        ta.setName(terminal.getName());
        ta.setWeight(weight);
        ta.setId(uuid);
        ta.setOnlineStatus(terminal.getOnlineStatus());
        ta.setSn(terminal.getSn());
        ta.setProtocol(TerminalType.isFSIP(terminal.getType()) == true ? "sip" : "h323");
        if (TerminalType.isFSBC(terminal.getType())) {
            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(terminal.getFsbcServerId());
            String ip = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
            String domainName = fsbcBridge.getBusiFsbcRegistrationServer().getDomainName();
            ta.setIp(ip);
            ta.setRemoteParty(terminal.getCredential() + "@" + ta.getIp());
            if (!ObjectUtils.isEmpty(domainName)) {
                ta.setIpNew(domainName);
                ta.setRemotePartyNew(terminal.getCredential() + "@" + domainName);
            }
            Integer sipPort = fsbcBridge.getBusiFsbcRegistrationServer().getSipPort();
            if (sipPort == null || sipPort == 5060) {
                ta.setRemoteParty(terminal.getCredential() + "@" + ta.getIp());
                ta.setRemoteParty(terminal.getCredential() + "@" + ip);
            } else {
                ta.setRemoteParty(terminal.getCredential() + "@" + ip + ":" + sipPort);
            }
        } else if (TerminalType.isFCMSIP(terminal.getType())) {
            FcmBridge fcmBridge = null;
            BusiFreeSwitchDept fsd = DeptFcmMappingCache.getInstance().get(terminal.getDeptId());
            if (FcmType.CLUSTER == FcmType.convert(fsd.getFcmType())) {
                if (terminal.getFsServerId() != null) {
                    fcmBridge = FcmBridgeCache.getInstance().get(terminal.getFsServerId());
                }
                if (fcmBridge == null) {
                    FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(fsd.getServerId());
                    if (fcmBridgeCluster != null) {
                        List<FcmBridge> fcmBridges = fcmBridgeCluster.getFcmBridges();
                        // 由于使用固定用户信息数据库，任意一个FCM即可
                        fcmBridge = fcmBridges.get(0);
                    }
                }
                String callIp = fcmBridge.getBusiFreeSwitch().getIp();
                Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                if (callPort == null || callPort == 5060) {
                    ta.setRemoteParty(terminal.getCredential() + "@" + ta.getIp());
                    ta.setRemoteParty(terminal.getCredential() + "@" + callIp);
                } else {
                    ta.setRemoteParty(terminal.getCredential() + "@" + callIp + ":" + callPort);
                }
                ta.setIp(callIp);
            } else {
                fcmBridge = FcmBridgeCache.getInstance().getById(fsd.getServerId());
            }
            ta.setIp(fcmBridge.getBusiFreeSwitch().getIp());
            ta.setRemoteParty(terminal.getCredential() + "@" + ta.getIp());
            String domainName = fcmBridge.getBusiFreeSwitch().getDomainName();
            if (!ObjectUtils.isEmpty(domainName)) {
                ta.setIpNew(domainName);
                ta.setRemotePartyNew(terminal.getCredential() + "@" + domainName);
            }
        } else if (TerminalType.isRtsp(terminal.getType())) {
            ta.setRemoteParty(terminal.getProtocol());
            Map<String, Object> properties = new HashMap<>();
            properties.put("rtsp_uri", terminal.getProtocol());
            ta.putBusinessProperties(properties);
        } else if (TerminalType.isSMCSIP(terminal.getType())) {
            ta.setRemoteParty(terminal.getNumber());
            ta.setRemotePartyNew(terminal.getNumber());
        }else if (TerminalType.isSMC2SIP(terminal.getType())) {
            ta.setRemoteParty(terminal.getCredential());
            ta.setRemotePartyNew(terminal.getCredential());
        } else {
            if (ObjectUtils.isEmpty(terminal.getNumber())) {
                ta.setRemoteParty(ta.getIp());
            } else {
                ta.setRemoteParty(terminal.getNumber() + "@" + ta.getIp());
            }
        }
        if (TerminalType.isWindows(terminal.getType()) || TerminalType.isCisco(terminal.getType())) {
            ta.setProtocol("sip");
            if (TerminalType.isWindows(terminal.getType())) {
                ta.setRemoteParty(UUID.randomUUID() + "@" + ta.getIp());
            }
        }
        if (TerminalType.isIp(terminal.getType())) {
            ta.setProtocol("sip");
            String remoteParty = ta.getRemoteParty();
            if (Strings.isNotBlank(remoteParty)) {
                if (InetAddresses.isInetAddress(remoteParty)) {
                    ta.setRemoteParty(UUID.randomUUID() + "@" + ta.getIp());
                }
            } else {
                ta.setRemoteParty(UUID.randomUUID() + "@" + ta.getIp());
            }
        }

        // 业务属性为空就获取终端的
        businessProperties = businessProperties == null ? terminal.getBusinessProperties() : businessProperties;
        Map<String, Object> properties = BusinessFieldType.convert(terminal.getBusinessFieldType()).getBusinessFieldService().parseTerminalBusinessProperties(businessProperties);
        if (!ObjectUtils.isEmpty(properties)) {
            ta.putBusinessProperties(properties);
        }

        return ta;
    }

    public static TerminalAttendeeSmc2 packTerminalAttendee(long terminalId) {
        return packTerminalAttendee(terminalId, null, null, 1, UUID.randomUUID().toString());
    }

    public static TerminalAttendeeSmc2 packTerminalAttendee(BusiMcuSmc2TemplateParticipant busiTemplateParticipant) {
        return packTerminalAttendee(busiTemplateParticipant.getTerminalId(), AttendType.convert(busiTemplateParticipant.getAttendType()), busiTemplateParticipant.getBusinessProperties(), busiTemplateParticipant.getWeight(), busiTemplateParticipant.getUuid());
    }


    public static void updateByParticipant(Smc2ConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO participant, AttendeeSmc2 a) {
        synchronized (a) {

            try {
                a.resetUpdateMap();
                Boolean pushCountingStatistics = false;
                if (participant != null) {
                    if (participant.getState().getOnline() != null) {
                        if (participant.getState().getOnline()) {
                            if (participant.getState().getMute() != null) {
                                int value = participant.getState().getMute() ? AttendeeMixingStatus.NO.getValue() : AttendeeMixingStatus.YES.getValue();
                                if (value != a.getMixingStatus()) {
                                    a.setMixingStatus(value);
                                }
                            }

                            if (participant.getState().getQuiet() != null) {
                                int value = participant.getState().getQuiet() ? YesOrNo.NO.getValue() : YesOrNo.YES.getValue();
                                if (value != a.getSpeakerStatus()) {
                                    a.setSpeakerStatus(value);
                                }
                            }


                            if (Strings.isNotBlank(participant.getGeneralParam().getName())) {
                                a.setName(participant.getGeneralParam().getName());
                            }

                            if (participant.getState().getVideoMute() != null) {
                                int value = participant.getState().getVideoMute() ? AttendeeVideoStatus.NO.getValue() : AttendeeVideoStatus.YES.getValue();
                                if (value != a.getVideoStatus()) {
                                    a.setVideoStatus(value);
                                }
                            }

                            if (!Objects.equals(a.getName(), participant.getGeneralParam().getName())) {
                                a.setName(participant.getGeneralParam().getName());
                            }
                            a.setMultiPicInfo(participant.getState().getMultiPicInfo());
                            if (participant.getState().getVideoSwitchAttribute() != null) {
                                Integer value = participant.getState().getVideoSwitchAttribute() == 1 ? 1 : 2;
                                if (!Objects.equals(value, a.getVideoSwitchAttribute())) {
                                    a.setVideoSwitchAttribute(value);
                                }
                            }

                            if (participant.getState().getVolume() != null) {
                                Integer value = participant.getState().getVolume();
                                if (!Objects.equals(value, a.getVolume())) {
                                    a.setVolume(value);
                                }
                            }

                        }

                    }

                    int meetingStatus = a.getMeetingStatus();

                    Boolean online = participant.getState().getOnline();

                    if (!Objects.equals(online, meetingStatus == 1)) {
                        pushCountingStatistics = true;
                    }


                }


                if (a instanceof TerminalAttendeeSmc2 || a instanceof McuAttendeeSmc2) {
                    new RegisteredAttendeeUpdateProcessor(participant, a, conferenceContext).process();
                } else {
                    new OtherAttendeeUpdateProcessor(participant, a, conferenceContext).process();
                }

                if (a.isMeetingJoined() && !ObjectUtils.isEmpty(participant.getGeneralParam().getName())) {
                    Long terminalId = null;
                    String name = "";
                    if (a instanceof TerminalAttendeeSmc2) {
                        terminalId = a.getTerminalId();
                    } else if (a instanceof TerminalAttendeeSmc2) {
                        terminalId = a.getTerminalId();
                    }
                    if (terminalId != null) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
                        if (busiTerminal != null) {
                            if (!ObjectUtils.isEmpty(busiTerminal.getName())) {
                                name += busiTerminal.getName();
                                if (!name.equals(participant.getGeneralParam().getName())) {
                                    name += "(" + participant.getGeneralParam().getName() + ")";
                                }
                            }
                        }
                    }
                    if (!ObjectUtils.isEmpty(name)) {
                        a.setName(name);
                    } else {
                        a.setName(participant.getGeneralParam().getName());
                    }
                }

                if (a.getUpdateMap().size() > 1) {
                    a.getUpdateMap().put("participantUuid", participant.getGeneralParam().getId());
                    a.getUpdateMap().put("onlineStatus", a.getOnlineStatus());

                    if (a instanceof McuAttendeeSmc2) {
                        if (a.getUpdateMap().containsKey("meetingStatus")) {
                            Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, a);
                        }
                    }
//                    if(a.getUpdateMap().get("multiPicInfo")!=null){
//                        if(a.getUpdateMap().containsKey("mixingStatus")){
//                            a.getUpdateMap().remove("mixingStatus");
//                        }
//                    }
                    Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(a.getUpdateMap()));
                }
                if (pushCountingStatistics) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

        }
    }

    public static AttendeeSmc2 matchAttendee(Smc2ConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO participant) {

        if (!ObjectUtils.isEmpty(participant.getAttendeeId())) {
            AttendeeSmc2 a = conferenceContext.getAttendeeById(participant.getAttendeeId());

            // 重新赋值，解决主被叫同时进行呼叫的情况下，导致的AttendeeId和ParticipantUuid相互绑定失败问题
            if (a != null) {
                a.setParticipantUuid(participant.getGeneralParam().getId());
            }
            return a;
        }

        // 注册终端匹配，忽略端口号以后
        String remoteParty = participant.getGeneralParam().getUri();
        if (remoteParty != null) {
            if (remoteParty.contains(":")) {
                remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
            }
            Map<String, AttendeeSmc2> uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remoteParty);
            if (uuidAttendeeMap != null) {
                for (Map.Entry<String, AttendeeSmc2> stringAttendeeTeleEntry : uuidAttendeeMap.entrySet()) {
                    AttendeeSmc2 value = stringAttendeeTeleEntry.getValue();
                    value.setParticipantUuid(value.getId());
                    SmcParitipantsStateRep.ContentDTO smcParticipant = value.getSmcParticipant();
                    if (smcParticipant != null && smcParticipant.getGeneralParam() != null && smcParticipant.getGeneralParam().getUri() != null && smcParticipant.getGeneralParam().getUri().equals(remoteParty)) {
                        smcParticipant.getGeneralParam().setId(value.getId());
                        smcParticipant.getState().setParticipantId(value.getId());
                    }
                    return value;
                }
            } else {
                if (remoteParty.contains("@")) {
                    try {
                        String[] remotePartyArr = remoteParty.split("@");
                        String credential = remotePartyArr[0];
                        String ip = remotePartyArr[1];
                        if (org.springframework.util.StringUtils.hasText(ip)) {
                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByDomainName(ip);
                            if (fsbcBridge != null) {
                                String remotePartyIp = credential + "@" + fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                                uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remotePartyIp);
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
            }
        }

        String remotePartyNew = "";
        Map<String, AttendeeSmc2> uuidAttendeeMap = null;
        if (Strings.isNotBlank(remotePartyNew)) {
            uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remoteParty);
        }

        if (uuidAttendeeMap != null) {
            if (remoteParty.contains("@")) {
                String[] remotePartyArr = remoteParty.split("@");
                String credential = remotePartyArr[0];
                String ip = remotePartyArr[1];
                if (org.springframework.util.StringUtils.hasText(ip)) {
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


        if (!ObjectUtils.isEmpty(participant.getAttendeeId())) {
            if (uuidAttendeeMap != null) {
                AttendeeSmc2 a = uuidAttendeeMap.get(participant.getAttendeeId());

                // 重新赋值，解决主被叫同时进行呼叫的情况下，导致的AttendeeId和ParticipantUuid相互绑定失败问题
                a.setParticipantUuid(participant.getGeneralParam().getId());
                if (!ObjectUtils.isEmpty(remotePartyNew)) {
                    a.setRemotePartyNew(remotePartyNew);
                    if (remotePartyNew.contains("@")) {
                        a.setIpNew(remotePartyNew.split("@")[1]);
                    } else {
                        a.setIpNew(remotePartyNew);
                    }
                }
                return a;
            }
        }

        synchronized (participant) {
            if (!ObjectUtils.isEmpty(participant.getAttendeeId())) {
                if (uuidAttendeeMap != null) {
                    AttendeeSmc2 a = uuidAttendeeMap.get(participant.getAttendeeId());
                    if (!ObjectUtils.isEmpty(remotePartyNew)) {
                        a.setRemotePartyNew(remotePartyNew);
                        if (remotePartyNew.contains("@")) {
                            a.setIpNew(remotePartyNew.split("@")[1]);
                        } else {
                            a.setIpNew(remotePartyNew);
                        }
                    }
                    return a;
                }

            }

            AttendeeSmc2 a = matchAttendee(participant, uuidAttendeeMap);
            if (a == null) {
                if (uuidAttendeeMap != null) {
                    a = uuidAttendeeMap.get(remoteParty);
                    if (a != null) {
                        a.setParticipantUuid(participant.getGeneralParam().getId());
                    }
                }

            }
            if (a != null) {
                if (!ObjectUtils.isEmpty(remotePartyNew)) {
                    a.setRemotePartyNew(remotePartyNew);
                    if (remotePartyNew.contains("@")) {
                        a.setIpNew(remotePartyNew.split("@")[1]);
                    } else {
                        a.setIpNew(remotePartyNew);
                    }
                }

                return a;
            }

        }
        return null;
    }

    public static AttendeeSmc2 matchAttendee(SmcParitipantsStateRep.ContentDTO participant, Map<String, AttendeeSmc2> uuidAttendeeMap) {
        if (uuidAttendeeMap == null) {
            return null;
        }
        for (Iterator<AttendeeSmc2> iterator = uuidAttendeeMap.values().iterator(); iterator.hasNext(); ) {
            AttendeeSmc2 a = iterator.next();
            synchronized (a) {
                if (ObjectUtils.isEmpty(a.getParticipantUuid())) {
                    a.setParticipantUuid(participant.getGeneralParam().getId());
                    participant.setAttendeeId(a.getId());
                    return a;
                }
            }
        }
        return null;
    }


    public static List<AttendeeSmc2> getAll(Smc2ConferenceContext conferenceContext) {
        List<AttendeeSmc2> list = new ArrayList<>();
        if (conferenceContext == null) {
            return list;
        }
        List<AttendeeSmc2> attendees = conferenceContext.getAttendees();
        if (attendees != null && attendees.size() > 0) {
            list.addAll(attendees);
        }
        AttendeeSmc2 masterAttendee = conferenceContext.getMasterAttendee();
        if (masterAttendee != null) {
            list.add(masterAttendee);
        }
        List<AttendeeSmc2> masterAttendees = conferenceContext.getMasterAttendees();
        if (masterAttendees != null && masterAttendees.size() > 0) {
            list.addAll(masterAttendees);
        }

        Map<Long, List<AttendeeSmc2>> cascadeAttendeesMap = conferenceContext.getCascadeAttendeesMap();
        if (cascadeAttendeesMap != null) {
            Collection<List<AttendeeSmc2>> values = cascadeAttendeesMap.values();
            for (List<AttendeeSmc2> value : values) {
                list.addAll(value);
            }

        }
        return list;
    }


    public static SmcParitipantsStateRep.ContentDTO initContent(SmcParitipantsStateRep.ContentDTO contentDTO, Smc2ConferenceContext smc2ConferenceContext, SiteStatusEx statusEx) {
        if (contentDTO == null) {
            contentDTO = new SmcParitipantsStateRep.ContentDTO();
            SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
            com.paradisecloud.com.fcm.smc.modle.ParticipantState participantState = new com.paradisecloud.com.fcm.smc.modle.ParticipantState();
            contentDTO.setGeneralParam(generalParam);
            contentDTO.setState(participantState);
        }
        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = contentDTO.getGeneralParam();
        ParticipantState participantState = contentDTO.getState();
        if (statusEx.getUri() != null) {
            generalParam.setUri(statusEx.getUri());
        }
        generalParam.setType(statusEx.getType());

        participantState.setVolume(statusEx.getVolume());
        participantState.setCallFailReason(statusEx.getCallFailedReason().getErrCode());

        generalParam.setName(statusEx.getName());


        participantState.setMute(statusEx.getIsMute() == 1);
        participantState.setQuiet(statusEx.getIsQuiet() == 1);
        if (statusEx.getIsDataOnline() != null) {
            participantState.setDataOnline(statusEx.getIsDataOnline() == 1);
        }

        if (statusEx.getIsLocalVideoOpen() != null) {
            participantState.setVideoMute(statusEx.getIsLocalVideoOpen() != 1);
        }


        Integer status = statusEx.getStatus();
        if (status != null) {
            if (status.equals(INMEETING)) {
                participantState.setOnline(true);
            } else if (status == RING || status == rINGING) {
                participantState.setOnline(false);
                participantState.setCalling(true);
            } else if (status.equals(NOT_MEETING)) {
                participantState.setOnline(false);
            } else if (status == 1) {
                participantState.setOnline(false);
            } else {
                participantState.setOnline(false);
            }
        }
        if (Strings.isNotBlank(statusEx.getVideoSource())) {
            if (Objects.equals(CP, statusEx.getVideoSource())) {
                ChooseMultiPicInfo.MultiPicInfoDTO source = new ChooseMultiPicInfo.MultiPicInfoDTO();

                MultiPicPollRequest multiPicPollRequest = smc2ConferenceContext.getMultiPicPollRequest();
                MultiPicPollRequest chairmanMultiPicPollRequest = smc2ConferenceContext.getChairmanMultiPicPollRequest();
                if (multiPicPollRequest != null || chairmanMultiPicPollRequest != null) {
                    Integer picNum = multiPicPollRequest == null ? chairmanMultiPicPollRequest.getPicNum() : multiPicPollRequest.getPicNum();
                    source.setPicNum(picNum);
                    if (picNum == 1) {
                        //查询广播多画面
                        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
                        String confId = smc2ConferenceContext.getSmc2conferenceId();
                        GetContinuousPresenceParamExResponse result
                                = conferenceServiceEx.getContinuousPresenceParamEx(confId, CP);
                        if (0 == result.getResultCode()) {
                            List<ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO> list = new ArrayList<>();
                            ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO();
                            String s = result.getSubPics().get(0);
                            subPicListDTO.setParticipantId(smc2ConferenceContext.getParticiPantIdBySiteUri(s));
                            list.add(subPicListDTO);
                            source.setSubPicList(list);
                        }

                    }
                    participantState.setMultiPicInfo(source);
                } else {
                    MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = smc2ConferenceContext.getMultiPicInfo();
                    if (multiPicInfo != null) {
                        ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfoDTO = new ChooseMultiPicInfo.MultiPicInfoDTO();
                        multiPicInfoDTO.setMode(multiPicInfo.getMode());
                        multiPicInfoDTO.setPicNum(multiPicInfo.getPicNum());
                        List<ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO> list = new ArrayList<>();
                        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfo.getSubPicList();
                        for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                            ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO subPicListDTO1 = new ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO();
                            subPicListDTO1.setParticipantId(subPicListDTO.getParticipantId());
                            subPicListDTO1.setStreamNumber(subPicListDTO.getStreamNumber());
                            list.add(subPicListDTO1);
                        }
                        multiPicInfoDTO.setSubPicList(list);
                        participantState.setMultiPicInfo(multiPicInfoDTO);
                    } else {
                        AttendeeOperation attendeeOperation = smc2ConferenceContext.getAttendeeOperation();

                        if (attendeeOperation instanceof TalkAttendeeOperation) {
                            TalkAttendeeOperation talkAttendeeOperation = (TalkAttendeeOperation) attendeeOperation;
                            MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO = talkAttendeeOperation.getMultiPicInfoDTO();
                            if (multiPicInfoDTO != null) {
                                String s = JSONObject.toJSONString(multiPicInfoDTO);
                                ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfoDTO1 = JSONObject.parseObject(s, ChooseMultiPicInfo.MultiPicInfoDTO.class);
                                participantState.setMultiPicInfo(multiPicInfoDTO1);
                            }

                        }

                    }

                }

            } else {
                String particiPantIdBySiteUri = smc2ConferenceContext.getParticiPantIdBySiteUri(statusEx.getVideoSource());
                ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfoDTO = new ChooseMultiPicInfo.MultiPicInfoDTO();
                multiPicInfoDTO.setPicNum(1);
                List<ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO> list = new ArrayList<>();
                ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO();
                subPicListDTO.setParticipantId(particiPantIdBySiteUri);
                list.add(subPicListDTO);
                multiPicInfoDTO.setSubPicList(list);
                participantState.setMultiPicInfo(multiPicInfoDTO);
            }
        }
        return contentDTO;
    }


}

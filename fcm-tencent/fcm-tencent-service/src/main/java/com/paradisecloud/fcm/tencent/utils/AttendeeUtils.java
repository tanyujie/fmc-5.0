/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author sinhy
 * @since 2021-09-22 21:04
 * @version  V1.0
 */
package com.paradisecloud.fcm.tencent.utils;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeVideoStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.tencent.busi.AttendeeCountingStatistics;
import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.McuAttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.RoomAttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.TerminalAttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentWebSocketMessagePusher;
import com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.tencent.templateConference.updateprocess.OtherAttendeeUpdateProcessor;
import com.paradisecloud.fcm.tencent.templateConference.updateprocess.RegisteredAttendeeUpdateProcessor;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

public class AttendeeUtils {

    public static final Logger logger = LoggerFactory.getLogger(AttendeeUtils.class);


    public static void updateByParticipant(TencentConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO participant, AttendeeTencent a) {
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


                            if (participant.getState().getVolume() != null) {
                                Integer value = participant.getState().getVolume();
                                if (!Objects.equals(value, a.getVolume())) {
                                    a.setVolume(value);
                                }
                            }
                            if (participant.getUserRole() != null) {
                                a.setUserRole(participant.getUserRole());
                            }
                            if (participant.getNick_name() != null) {
                                a.setNickName(participant.getNick_name());
                            }
                            if (participant.getState().getScreen_shared_state() != null) {
                                a.setPresentStatus(participant.getState().getScreen_shared_state() ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
                            }

                            if (participant.getState().getRaise_hands_state() != null) {
                                a.setRaiseHandStatus(participant.getState().getRaise_hands_state() ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
                            }


                        }

                    }

                    int meetingStatus = a.getMeetingStatus();

                    Boolean online = participant.getState().getOnline();

                    if (!Objects.equals(online, meetingStatus == 1)) {
                        pushCountingStatistics = true;
                    }


                }


                if (a instanceof TerminalAttendeeTencent || a instanceof McuAttendeeTencent|| a instanceof RoomAttendeeTencent) {
                    new RegisteredAttendeeUpdateProcessor(participant, a, conferenceContext).process();
                } else {
                    new OtherAttendeeUpdateProcessor(participant, a, conferenceContext).process();
                }

                if (a.isMeetingJoined() && !ObjectUtils.isEmpty(participant.getGeneralParam().getName())) {
                    Long terminalId = null;
                    String name = "";
                    if (a instanceof TerminalAttendeeTencent) {
                        terminalId = a.getTerminalId();
                    } else if (a instanceof TerminalAttendeeTencent) {
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
                    a.getUpdateMap().put("nickName", a.getNickName());
                    TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(a.getUpdateMap()));
                    if (a instanceof McuAttendeeTencent) {
                        if (a.getUpdateMap().containsKey("meetingStatus")) {
                            TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, a);
                        }
                    }
                }
                if (pushCountingStatistics) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                    TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

        }
    }

    public static AttendeeTencent matchAttendee(TencentConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO participant) {

        if (!ObjectUtils.isEmpty(participant.getAttendeeId())) {
            AttendeeTencent a = conferenceContext.getAttendeeById(participant.getAttendeeId());

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
            Map<String, AttendeeTencent> uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remoteParty);
            if (uuidAttendeeMap != null) {
                for (Map.Entry<String, AttendeeTencent> stringAttendeeTeleEntry : uuidAttendeeMap.entrySet()) {
                    AttendeeTencent value = stringAttendeeTeleEntry.getValue();
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
        Map<String, AttendeeTencent> uuidAttendeeMap = null;
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
                AttendeeTencent a = uuidAttendeeMap.get(participant.getAttendeeId());

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
                    AttendeeTencent a = uuidAttendeeMap.get(participant.getAttendeeId());
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

            AttendeeTencent a = matchAttendee(participant, uuidAttendeeMap);
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

    public static AttendeeTencent matchAttendee(SmcParitipantsStateRep.ContentDTO participant, Map<String, AttendeeTencent> uuidAttendeeMap) {
        if (uuidAttendeeMap == null) {
            return null;
        }
        for (Iterator<AttendeeTencent> iterator = uuidAttendeeMap.values().iterator(); iterator.hasNext(); ) {
            AttendeeTencent a = iterator.next();
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


    public static List<AttendeeTencent> getAll(TencentConferenceContext conferenceContext) {
        List<AttendeeTencent> list = new ArrayList<>();
        if (conferenceContext == null) {
            return list;
        }
        List<AttendeeTencent> attendees = conferenceContext.getAttendees();
        if (attendees != null && attendees.size() > 0) {
            list.addAll(attendees);
        }
        AttendeeTencent masterAttendee = conferenceContext.getMasterAttendee();
        if (masterAttendee != null) {
            list.add(masterAttendee);
        }
        List<AttendeeTencent> masterAttendees = conferenceContext.getMasterAttendees();
        if (masterAttendees != null && masterAttendees.size() > 0) {
            list.addAll(masterAttendees);
        }

        Map<Long, List<AttendeeTencent>> cascadeAttendeesMap = conferenceContext.getCascadeAttendeesMap();
        if (cascadeAttendeesMap != null) {
            Collection<List<AttendeeTencent>> values = cascadeAttendeesMap.values();
            for (List<AttendeeTencent> value : values) {
                list.addAll(value);
            }

        }
        return list;
    }


}

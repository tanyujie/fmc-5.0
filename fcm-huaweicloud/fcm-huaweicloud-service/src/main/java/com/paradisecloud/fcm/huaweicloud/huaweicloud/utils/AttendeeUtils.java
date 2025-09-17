/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author sinhy
 * @since 2021-09-22 21:04
 * @version  V1.0
 */
package com.paradisecloud.fcm.huaweicloud.huaweicloud.utils;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeVideoStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.AttendeeCountingStatistics;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.CropDirAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.McuAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.TerminalAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudWebSocketMessagePusher;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.updateprocess.OtherAttendeeUpdateProcessor;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.updateprocess.RegisteredAttendeeUpdateProcessor;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

public class AttendeeUtils {

    public static final Logger logger = LoggerFactory.getLogger(AttendeeUtils.class);


    public static void updateByParticipant(HwcloudConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO participant, AttendeeHwcloud a) {
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

                            if (participant.getState().getBroadcast() != null) {
                                int value = participant.getState().getBroadcast() ? AttendeeVideoStatus.YES.getValue() : AttendeeVideoStatus.NO.getValue();
                                if (value != a.getBroadcastStatus()) {
                                    a.setBroadcastStatus(value);
                                }
                            }

                            if (participant.getState().getScreen_shared_state() != null) {
                                int value = participant.getState().getScreen_shared_state() ? AttendeeVideoStatus.YES.getValue() : AttendeeVideoStatus.NO.getValue();
                                if (value != a.getPresentStatus()) {
                                    a.setPresentStatus(value);
                                }
                            }

                            if (participant.getState().getRollcall() != null) {
                                int value = participant.getState().getRollcall() ? AttendeeVideoStatus.YES.getValue() : AttendeeVideoStatus.NO.getValue();
                                if (value != a.getCallTheRollStatus()) {
                                    a.setCallTheRollStatus(value);
                                }
                            }

                            if (participant.getState().getRaise_hands_state() != null) {
                                int value = participant.getState().getRaise_hands_state() ? AttendeeVideoStatus.YES.getValue() : AttendeeVideoStatus.NO.getValue();
                                if (value != a.getRaiseHandStatus()) {
                                    a.setRaiseHandStatus(value);
                                }
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


                        }

                    }

                    int meetingStatus = a.getMeetingStatus();

                    Boolean online = participant.getState().getOnline();

                    if (!Objects.equals(online, meetingStatus == 1)) {
                        pushCountingStatistics = true;
                    }


                }


                if (a instanceof TerminalAttendeeHwcloud || a instanceof McuAttendeeHwcloud||a instanceof CropDirAttendeeHwcloud) {
                    new RegisteredAttendeeUpdateProcessor(participant, a, conferenceContext).process();
                } else {
                    new OtherAttendeeUpdateProcessor(participant, a, conferenceContext).process();
                }

                if (a.isMeetingJoined() && !ObjectUtils.isEmpty(participant.getGeneralParam().getName())) {
                    Long terminalId = null;
                    String name = "";
                    if (a instanceof TerminalAttendeeHwcloud) {
                        terminalId = a.getTerminalId();
                    } else if (a instanceof TerminalAttendeeHwcloud) {
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
                    a.getUpdateMap().put("number", a.getNumber());
                    HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(a.getUpdateMap()));
                    if (a instanceof McuAttendeeHwcloud) {
                        if (a.getUpdateMap().containsKey("meetingStatus")) {
                            HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, a);
                        }
                    }
                }
                if (pushCountingStatistics) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                    HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

        }
    }

    public static AttendeeHwcloud matchAttendee(HwcloudConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO participant) {

        if (!ObjectUtils.isEmpty(participant.getAttendeeId())) {
            AttendeeHwcloud a = conferenceContext.getAttendeeById(participant.getAttendeeId());

            // 重新赋值，解决主被叫同时进行呼叫的情况下，导致的AttendeeId和ParticipantUuid相互绑定失败问题
            if (a != null) {
                a.setParticipantUuid(participant.getGeneralParam().getId());
            }
            return a;
        }
        Map<String, AttendeeHwcloud> participantAttendeeAllMap = conferenceContext.getParticipantAttendeeAllMap();
        AttendeeHwcloud attendeeHwcloud = participantAttendeeAllMap.get(participant.getGeneralParam().getId());
        if(attendeeHwcloud!=null){
            return attendeeHwcloud;
        }

        for (AttendeeHwcloud attendee : conferenceContext.getAttendees()) {
            if(attendee instanceof CropDirAttendeeHwcloud){
                CropDirAttendeeHwcloud cropDirAttendeeHwcloud=(CropDirAttendeeHwcloud)attendee;

                String accountId = cropDirAttendeeHwcloud.getAccountId();
                String type = cropDirAttendeeHwcloud.getType();

                if(participant.getCallType()==null){
                    break;
                }
                if(Objects.equals(accountId,participant.getAccountId())&&Objects.equals(type.toLowerCase(),participant.getCallType().toLowerCase()))
                {
                    return attendee;
                }
                if(Objects.equals(type,"normal")&&Objects.equals(participant.getCallType().toLowerCase(),"desktop")&&Objects.equals(accountId,participant.getAccountId())){
                    return attendee;
                }

                if(Objects.equals(accountId,participant.getAccountId())&&Objects.equals(type,"normal")&&Objects.equals(participant.getUserAgent(),"WeLink-desktop")){
                    return attendee;
                }
            }
        }


        return attendeeHwcloud;
    }

    public static AttendeeHwcloud matchAttendee(SmcParitipantsStateRep.ContentDTO participant, Map<String, AttendeeHwcloud> uuidAttendeeMap) {
        if (uuidAttendeeMap == null) {
            return null;
        }
        for (Iterator<AttendeeHwcloud> iterator = uuidAttendeeMap.values().iterator(); iterator.hasNext(); ) {
            AttendeeHwcloud a = iterator.next();
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


    public static List<AttendeeHwcloud> getAll(HwcloudConferenceContext conferenceContext) {
        List<AttendeeHwcloud> list = new ArrayList<>();
        if (conferenceContext == null) {
            return list;
        }
        List<AttendeeHwcloud> attendees = conferenceContext.getAttendees();
        if (attendees != null && attendees.size() > 0) {
            list.addAll(attendees);
        }
        AttendeeHwcloud masterAttendee = conferenceContext.getMasterAttendee();
        if (masterAttendee != null) {
            list.add(masterAttendee);
        }
        List<AttendeeHwcloud> masterAttendees = conferenceContext.getMasterAttendees();
        if (masterAttendees != null && masterAttendees.size() > 0) {
            list.addAll(masterAttendees);
        }

        Map<Long, List<AttendeeHwcloud>> cascadeAttendeesMap = conferenceContext.getCascadeAttendeesMap();
        if (cascadeAttendeesMap != null) {
            Collection<List<AttendeeHwcloud>> values = cascadeAttendeesMap.values();
            for (List<AttendeeHwcloud> value : values) {
                list.addAll(value);
            }

        }
        return list;
    }


}

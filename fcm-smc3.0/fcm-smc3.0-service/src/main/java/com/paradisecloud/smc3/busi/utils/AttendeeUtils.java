/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeUtils.java
 * Package     : com.paradisecloud.fcm.fme.attendee.utils
 * @author sinhy
 * @since 2021-09-22 21:04
 * @version  V1.0
 */
package com.paradisecloud.smc3.busi.utils;

import com.alibaba.fastjson.JSONObject;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.MinutesAttendee;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.smc3.busi.AttendeeCountingStatistics;
import com.paradisecloud.smc3.busi.ConferenceNode;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.McuAttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.MinutesAttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.TerminalAttendeeSmc3;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3TemplateParticipant;
import com.paradisecloud.smc3.busi.updateprocessor.OtherAttendeeUpdateProcessor;
import com.paradisecloud.smc3.busi.updateprocessor.RegisteredAttendeeUpdateProcessor;
import com.paradisecloud.smc3.model.ChooseMultiPicInfo;
import com.paradisecloud.smc3.model.DialMode;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import com.paradisecloud.smc3.task.Smc3DelayTaskService;
import com.paradisecloud.smc3.task.Smc3MeetingRoomRegTask;
import com.sinhy.spring.BeanFactory;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

public class AttendeeUtils {

    public static final Logger logger= LoggerFactory.getLogger(AttendeeUtils.class);

    public static TerminalAttendeeSmc3 packTerminalAttendee(long terminalId, AttendType attendType, Map<String, Object> businessProperties, int weight, String uuid) {
        BusiTerminal terminal = TerminalCache.getInstance().get(terminalId);
        if(terminal==null){
            terminal=BeanFactory.getBean(IBusiTerminalService.class).selectBusiTerminalById(terminalId);
        }
        TerminalAttendeeSmc3 ta = new TerminalAttendeeSmc3();
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
        }else if (TerminalType.isSMCSIP(terminal.getType())) {
            ta.setRemoteParty(terminal.getNumber());
            ta.setRemotePartyNew(terminal.getNumber());
        }
        else {
            if (ObjectUtils.isEmpty(terminal.getNumber())) {
                ta.setRemoteParty(ta.getIp());
            } else {
                ta.setRemoteParty(terminal.getNumber() + "@" + ta.getIp());
                if(org.apache.commons.lang.StringUtils.isBlank(ta.getIp())){
                    ta.setRemoteParty(terminal.getNumber());
                }
            }
        }
//        if (TerminalType.isWindows(terminal.getType()) || TerminalType.isCisco(terminal.getType())) {
//               ta.setProtocol("sip");
//        }
//        if(TerminalType.isSMCNUMBER(terminal.getType())){
//            ta.setRemoteParty(terminal.getNumber());
//            ta.setRemotePartyNew(terminal.getNumber());
//        }

        if(businessProperties != null){
            if(businessProperties.get("terminalAbility")!=null){
                Object terminalAbilityoBJ = businessProperties.get("terminalAbility");
                if(terminalAbilityoBJ instanceof JSONObject){

                    JSONObject terminalAbilityoBJ1=(JSONObject) terminalAbilityoBJ;
                    try {
                        if(terminalAbilityoBJ1.get("audioProtocol")!=null&&!Objects.equals("自动",terminalAbilityoBJ1.get("audioProtocol"))){
                            ta.setAudioProtocol((Integer)terminalAbilityoBJ1.get("audioProtocol"));
                        }
                        if(terminalAbilityoBJ1.get("videoProtocol")!=null&&!Objects.equals("自动",terminalAbilityoBJ1.get("videoProtocol"))){
                            ta.setVideoProtocol((Integer)terminalAbilityoBJ1.get("videoProtocol"));
                        }
                        if(terminalAbilityoBJ1.get("videoResolution")!=null&&!Objects.equals("自动",terminalAbilityoBJ1.get("videoResolution"))){
                            ta.setVideoResolution((Integer)terminalAbilityoBJ1.get("videoResolution"));
                        }
                        if(terminalAbilityoBJ1.get("dialMode")!=null&&!Objects.equals("自动",terminalAbilityoBJ1.get("dialMode"))){
                            ta.setDialMode(DialMode.valueOf(terminalAbilityoBJ1.get("dialMode")+""));
                        }
                        if(terminalAbilityoBJ1.get("rate")!=null&&!Objects.equals("自动",terminalAbilityoBJ1.get("rate"))){
                            ta.setRate((Integer) terminalAbilityoBJ1.get("rate"));
                        }

                        if(terminalAbilityoBJ1.get("dtmfInfo")!=null&&!Objects.equals("自动",terminalAbilityoBJ1.get("dtmfInfo"))){
                            ta.setDtmfInfo((String) terminalAbilityoBJ1.get("dtmfInfo"));
                        }
                        if(terminalAbilityoBJ1.get("serviceZoneId")!=null&&!Objects.equals("自动",terminalAbilityoBJ1.get("serviceZoneId"))){
                            ta.setServiceZoneId((String) terminalAbilityoBJ1.get("serviceZoneId"));
                        }
                    } catch (IllegalArgumentException e) {
                        logger.info("编辑能力错误"+e.getMessage());
                    }

                }


            }

        }


        // 业务属性为空就获取终端的
        businessProperties = businessProperties == null ? terminal.getBusinessProperties() : businessProperties;
        Map<String, Object> properties = BusinessFieldType.convert(terminal.getBusinessFieldType()).getBusinessFieldService().parseTerminalBusinessProperties(businessProperties);
        if (!ObjectUtils.isEmpty(properties)) {
            ta.putBusinessProperties(properties);
        }


        //注册到外部会议室
        Smc3MeetingRoomRegTask smcMeetingRoomRegTask = new Smc3MeetingRoomRegTask(ta.getId(),ta.getRemoteParty(),100,  ta.getDeptId(), terminal);
        Smc3DelayTaskService smcDelayTaskService = BeanFactory.getBean(Smc3DelayTaskService.class);
        smcDelayTaskService.addTask(smcMeetingRoomRegTask);


        return ta;
    }

    public static TerminalAttendeeSmc3 packTerminalAttendee(long terminalId) {
        return packTerminalAttendee(terminalId, null, null, 1, UUID.randomUUID().toString());
    }

    public static TerminalAttendeeSmc3 packTerminalAttendee(BusiMcuSmc3TemplateParticipant busiTemplateParticipant) {
        return packTerminalAttendee(busiTemplateParticipant.getTerminalId(), AttendType.convert(busiTemplateParticipant.getAttendType()), busiTemplateParticipant.getBusinessProperties(), busiTemplateParticipant.getWeight(), busiTemplateParticipant.getUuid());
    }


    public static void updateByParticipant(Smc3ConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO participant, AttendeeSmc3 a) {
        synchronized (a) {

            try {
                a.resetUpdateMap();
                Boolean pushCountingStatistics=false;
                if (participant != null) {
                    if (participant.getState().getOnline()!=null) {
                        if(participant.getState().getOnline()){
                            if (participant.getState().getMute() != null) {
                                int value =participant.getState().getMute() ? AttendeeMixingStatus.NO.getValue() : AttendeeMixingStatus.YES.getValue();
                                if (value != a.getMixingStatus()) {
                                    a.setMixingStatus(value);
                                }
                            }

                            if (participant.getState().getQuiet() != null) {
                                int value =participant.getState().getQuiet() ? YesOrNo.NO.getValue() : YesOrNo.YES.getValue();
                                if (value != a.getSpeakerStatus()) {
                                    a.setSpeakerStatus(value);
                                }
                            }
                            if (participant.getState().getVideoSwitchAttribute() != null) {
                                Integer value =participant.getState().getVideoSwitchAttribute()==1?1:2;
                                if (!Objects.equals(value,a.getVideoSwitchAttribute())) {
                                    a.setVideoSwitchAttribute(value);
                                }
                            }

                            if(participant.getState().getVolume()!=null){
                                Integer value =participant.getState().getVolume();
                                if (!Objects.equals(value,a.getVolume())) {
                                    a.setVolume(value);
                                }
                            }


                            if(Strings.isNotBlank(participant.getGeneralParam().getName())){
                                a.setName(participant.getGeneralParam().getName());
                            }

                            if (participant.getState().getVideoMute() != null) {
                                int value = participant.getState().getVideoMute() ? AttendeeVideoStatus.NO.getValue() : AttendeeVideoStatus.YES.getValue();
                                if (value != a.getVideoStatus()) {
                                    a.setVideoStatus(value);
                                }
                            }

                            if(!Objects.equals(a.getName(),participant.getGeneralParam().getName())){
                                a.setName(participant.getGeneralParam().getName());
                            }
                            ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfo = a.getMultiPicInfo();
                            if(multiPicInfo!=null){
                                ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfo1 = participant.getState().getMultiPicInfo();
                                if(multiPicInfo1==null){
                                    a.setMultiPicInfo(participant.getState().getMultiPicInfo());
                                }else {
                                    Integer picNum = multiPicInfo.getPicNum();
                                    Integer mode = multiPicInfo.getMode();

                                    Integer picNum1 = multiPicInfo1.getPicNum();
                                    Integer mode1 = multiPicInfo1.getMode();
                                    if(!Objects.equals(picNum,picNum1)||!Objects.equals(mode,mode1)){
                                        a.setMultiPicInfo(participant.getState().getMultiPicInfo());
                                    }
                                }
                            }else {
                                a.setMultiPicInfo(participant.getState().getMultiPicInfo());
                            }


                        }

                    }

                    int meetingStatus = a.getMeetingStatus();

                    Boolean online = participant.getState().getOnline();

                    if(!Objects.equals(online,meetingStatus==1?true:false)){
                        pushCountingStatistics=true;
                    }


                }


                if (a instanceof TerminalAttendeeSmc3 || a instanceof McuAttendeeSmc3) {
                    new RegisteredAttendeeUpdateProcessor(participant, a, conferenceContext).process();
                } else {
                    new OtherAttendeeUpdateProcessor(participant, a, conferenceContext).process();
                }

                if (a.isMeetingJoined() && !ObjectUtils.isEmpty(participant.getGeneralParam().getName())) {
                    Long terminalId = null;
                    String name = "";
                    if (a instanceof TerminalAttendeeSmc3) {
                        terminalId = a.getTerminalId();
                    } else if (a instanceof TerminalAttendeeSmc3) {
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
                    a.getUpdateMap().put("id", a.getId());
                    a.getUpdateMap().put("onlineStatus", a.getOnlineStatus());
                    a.getUpdateMap().put("meetingStatus", a.getMeetingStatus());
                    a.getUpdateMap().put("participantUuid",participant.getGeneralParam().getId());

                    Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(a.getUpdateMap()));
                }
                if(pushCountingStatistics){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                }


            } catch (Exception e) {
                logger.error(e.getMessage());
            }

        }
    }

    public static synchronized AttendeeSmc3 matchAttendee(Smc3ConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO participant) {
        String id = participant.getGeneralParam().getId();
        if(Strings.isBlank(id)){
            id=participant.getState().getParticipantId();
        }

        if(Strings.isBlank(id)){
            return null;
        }
        AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(id);
        if(attendeeBySmc3Id!=null){
            return attendeeBySmc3Id;
        }

        if (!ObjectUtils.isEmpty(participant.getAttendeeId())) {
            AttendeeSmc3 a = conferenceContext.getAttendeeById(participant.getAttendeeId());

            // 重新赋值，解决主被叫同时进行呼叫的情况下，导致的AttendeeId和ParticipantUuid相互绑定失败问题
            if (a != null) {
                a.setParticipantUuid(participant.getGeneralParam().getId());
            }
            return a;
        }

        // 注册终端匹配，忽略端口号以后
        String remoteParty = participant.getGeneralParam().getUri();
        if(remoteParty!=null){
            if (remoteParty.contains(":")) {
                remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
            }
            if (remoteParty.contains(";")) {
                remoteParty = remoteParty.substring(0, remoteParty.indexOf(";"));
            }
            if (remoteParty.equals(conferenceContext.getMinutesRemoteParty()) || remoteParty.startsWith("minutes@")) {
                if (conferenceContext.getMinutesAttendee() == null) {
                    MinutesAttendeeSmc3 minutesAttendee = new MinutesAttendeeSmc3();
                    minutesAttendee.setRemoteParty(participant.getGeneralParam().getUri());
                    minutesAttendee.setName(participant.getGeneralParam().getName());
                    minutesAttendee.setParticipantUuid(participant.getGeneralParam().getId());
                    minutesAttendee.setDeptId(conferenceContext.getDeptId());
                    if (minutesAttendee.getId() == null) {
                        minutesAttendee.setId(participant.getGeneralParam().getId());
                    }
                    conferenceContext.setMinutesAttendee(minutesAttendee);
                    conferenceContext.setMinutesRemoteParty(remoteParty);
                    return minutesAttendee;
                }
            }
            Map<String, AttendeeSmc3> uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remoteParty);
            if (uuidAttendeeMap != null) {
                for (Map.Entry<String, AttendeeSmc3> stringAttendeeTeleEntry : uuidAttendeeMap.entrySet()) {
                    AttendeeSmc3 value = stringAttendeeTeleEntry.getValue();
                    value.setParticipantUuid(participant.getGeneralParam().getId());
                    return value;
                }
            }else {
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
        Map<String, AttendeeSmc3> uuidAttendeeMap =null;
        if(Strings.isNotBlank(remotePartyNew)){
            uuidAttendeeMap=  conferenceContext.getUuidAttendeeMapByUri(remoteParty);
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
            if(uuidAttendeeMap!=null){
                AttendeeSmc3 a = uuidAttendeeMap.get(participant.getAttendeeId());

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
                if(uuidAttendeeMap!=null){
                    AttendeeSmc3 a = uuidAttendeeMap.get(participant.getAttendeeId());
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

            AttendeeSmc3 a = matchAttendee(participant, uuidAttendeeMap);
            if (a == null) {
                if(uuidAttendeeMap!=null){
                    a = uuidAttendeeMap.get(remoteParty);
                    if (a != null) {
                        a.setParticipantUuid(participant.getGeneralParam().getId());
                    }
                }

            }
            if(a!=null){
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

    public static AttendeeSmc3 matchAttendee(SmcParitipantsStateRep.ContentDTO participant, Map<String, AttendeeSmc3> uuidAttendeeMap) {
        if(uuidAttendeeMap==null){
            return null;
        }
        for (Iterator<AttendeeSmc3> iterator = uuidAttendeeMap.values().iterator(); iterator.hasNext(); ) {
            AttendeeSmc3 a = iterator.next();
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

}

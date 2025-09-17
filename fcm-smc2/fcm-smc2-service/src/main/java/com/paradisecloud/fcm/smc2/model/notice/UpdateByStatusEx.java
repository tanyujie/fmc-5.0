//package com.paradisecloud.fcm.smc2.model.notice;
//
//import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
//import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
//import com.paradisecloud.fcm.common.enumer.AttendeeVideoStatus;
//import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
//import com.paradisecloud.fcm.dao.model.BusiTerminal;
//import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
//import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
//import com.paradisecloud.fcm.smc2.conference.updateprocess.OtherAttendeeUpdateProcessor;
//import com.paradisecloud.fcm.smc2.conference.updateprocess.RegisteredAttendeeUpdateProcessor;
//import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
//import com.paradisecloud.fcm.smc2.model.attendee.McuAttendeeSmc2;
//import com.paradisecloud.fcm.smc2.model.attendee.TerminalAttendeeSmc2;
//import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2HistoryConferenceService;
//import com.paradisecloud.fcm.terminal.cache.TerminalCache;
//import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
//import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
//import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
//import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
//import com.sinhy.spring.BeanFactory;
//import com.suntek.smc.esdk.pojo.local.SiteStatusEx;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.util.ObjectUtils;
//
//import java.util.*;
//
///**
// * "uri", "name", "type", "status", "volume",
// * "videoSource", "isMute", "isRemoteMute",
// * "isQuiet", "isLocalVideoOpen", "participantType",
// * "callFailedReson", "screens", "mcuId", "isDataOnline",
// * "isBroadCast", "isChairMan", "isDisplaySite",
// * "accessCode", "siteNetInfo", "isSendLiveAmc",
// * "leftUri", "rightUri"
// *
// * @author nj
// * @date 2022/4/27 10:49
// */
//public class UpdateByStatusEx {
//  private Logger logger= LoggerFactory.getLogger(getClass());
//    public static final int INMEETING = 2;
//    public static final String CP = "(%CP)";
//    public static final int RING = 4;
//    public static final int rINGING = 5;
//    public static final int NOT_MEETING = 2;
//
//    public void updateByStatus(Smc2ConferenceContext conferenceContext, SiteStatusEx siteStatus, AttendeeSmc2 a) {
//        synchronized (a) {
//            List<AttendeeNoticeInfo.ChangeListDTO> changeList = new ArrayList<>();
//            Boolean send = false;
//            if (siteStatus != null) {
//
//                AttendeeNoticeInfo.ChangeListDTO changeListDTO = new AttendeeNoticeInfo.ChangeListDTO();
//                changeListDTO.setParticipantId(a.getGeneralParam().getId());
//                if (siteStatus.getIsMute() != null) {
//                    a.getState().setMute(siteStatus.getIsMute() == 1 ? true : false);
//                    changeListDTO.setMute(siteStatus.getIsMute() == 1 ? true : false);
//                }
//                if (siteStatus.getIsQuiet() != null) {
//                    a.getState().setQuiet(siteStatus.getIsQuiet() == 1 ? true : false);
//                    changeListDTO.setQuiet(siteStatus.getIsQuiet() == 1 ? true : false);
//                }
//                if (siteStatus.getName() != null) {
//                    a.getGeneralParam().setName(siteStatus.getName());
//                    changeListDTO.setName(siteStatus.getName());
//                }
//                if (siteStatus.getVolume() != null) {
//                    a.getState().setVolume(siteStatus.getVolume());
//                    changeListDTO.setVolume(siteStatus.getVolume());
//                }
//                if (siteStatus.getCallFailedReason() != null) {
//                    a.getState().setCallFailReason(siteStatus.getCallFailedReason().getErrCode());
//                    changeListDTO.setCallFailReason(siteStatus.getCallFailedReason().getErrCode());
//                }
//                if (siteStatus.getIsDataOnline() != null) {
//                    a.getState().setDataOnline(siteStatus.getIsDataOnline() == 1 ? true : false);
//                    changeListDTO.setDataOnline(siteStatus.getIsDataOnline() == 1 ? true : false);
//                }
//                if (siteStatus.getIsLocalVideoOpen() != null) {
//                    a.getState().setVideoMute(siteStatus.getIsLocalVideoOpen() == 1 ? false : true);
//                    changeListDTO.setVideoMute(siteStatus.getIsLocalVideoOpen() == 1 ? false : true);
//                }
//                if (Strings.isNotBlank(siteStatus.getVideoSource())) {
//                    send = true;
//                    if(Objects.equals(CP,siteStatus.getVideoSource())){
//                        ChooseMultiPicInfo.MultiPicInfoDTO source =new   ChooseMultiPicInfo.MultiPicInfoDTO();
//
//                        MultiPicPollRequest multiPicPollRequest = conferenceContext.getMultiPicPollRequest();
//                        MultiPicPollRequest chairmanMultiPicPollRequest = conferenceContext.getChairmanMultiPicPollRequest();
//                        if(multiPicPollRequest!=null||chairmanMultiPicPollRequest!=null){
//                            Integer picNum = multiPicPollRequest==null?chairmanMultiPicPollRequest.getPicNum():multiPicPollRequest.getPicNum();
//                            source.setPicNum(picNum);
//                            if(picNum==1){
//                                //查询广播多画面
//                                ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
//                                String confId =conferenceContext.getConference().getConfId();
//                                GetContinuousPresenceParamExResponse result
//                                        = conferenceServiceEx.getContinuousPresenceParamEx(confId, CP);
//                                if(0==result.getResultCode()){
//                                    List<ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO> list=new ArrayList<>();
//                                    ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new  ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO();
//                                    String s = result.getSubPics().get(0);
//                                    subPicListDTO.setParticipantId(conferenceContext.getParticiPantIdBySiteUri(s));
//                                    list.add(subPicListDTO);
//                                    source.setSubPicList(list);
//                                }
//
//                            }
//
//                            changeListDTO.setMultiPicInfo(source);
//                            a.getState().setMultiPicInfo(source);
//                        }else {
//                            ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfo = conferenceContext.getDetailConference().getConferenceState().getMultiPicInfo();
//                            changeListDTO.setMultiPicInfo(multiPicInfo);
//                            a.getState().setMultiPicInfo(multiPicInfo);
//                        }
//
//                    }else {
//                        String particiPantIdBySiteUri = conferenceContext.getParticiPantIdBySiteUri(siteStatus.getVideoSource());
//                        ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfoDTO=new  ChooseMultiPicInfo.MultiPicInfoDTO();
//                        multiPicInfoDTO.setPicNum(1);
//                        List<ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO> list=new ArrayList<>();
//                        ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new  ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO();
//                        subPicListDTO.setParticipantId(particiPantIdBySiteUri);
//                        list.add(subPicListDTO);
//                        multiPicInfoDTO.setSubPicList(list);
//                        changeListDTO.setMultiPicInfo(multiPicInfoDTO);
//                        a.getState().setMultiPicInfo(multiPicInfoDTO);
//                    }
//                } else {
//                    send = true;
//                }
//                Integer videoSwitchAttribute = a.getState().getVideoSwitchAttribute();
//                if(videoSwitchAttribute==null){
//                    changeListDTO.setVideoSwitchAttribute(0);
//                }else {
//                    changeListDTO.setVideoSwitchAttribute(a.getState().getVideoSwitchAttribute());
//                }
//
//                /**
//                 * 会场状态。
//                 * 0：未知状态（保留）
//                 * 1：会场不存在
//                 * 2：在会议中
//                 * 2：未入会
//                 * 4：正在呼叫
//                 * 5：正在振铃
//                 */
//                Integer status = siteStatus.getStatus();
//                if (status != null) {
//                    if (status == INMEETING) {
//                        a.getState().setOnline(true);
//                        changeListDTO.setOnline(true);
//                        a.setTerminalOnline(true);
//                        changeListDTO.setTerminalOnline(true);
//                    }else if(status== RING||status== rINGING){
//                        a.getState().setCalling(true);
//                        a.getState().setOnline(false);
//                        changeListDTO.setOnline(false);
//                        changeListDTO.setCalling(true);
//                    }else if(status== NOT_MEETING){
//                        a.getState().setOnline(false);
//                        changeListDTO.setOnline(false);
//                    }else if(status== 1){
//                        a.setTerminalOnline(false);
//                        changeListDTO.setTerminalOnline(false);
//                        a.getState().setOnline(false);
//                        changeListDTO.setOnline(false);
//                    }else {
//                        a.getState().setOnline(false);
//                        changeListDTO.setOnline(false);
//                    }
//
//                }
//                if(a.getTerminalId()!=null){
//                    BusiTerminal terminal = TerminalCache.getInstance().get(a.getTerminalId());
//                    if(terminal!=null){
//                        Integer onlineStatus = terminal.getOnlineStatus();
//                        changeListDTO.setTerminalOnline(onlineStatus==1?true:false);
//                    }
//                }
//
//                changeList.add(changeListDTO);
//            }
//            if (send) {
//                AttendeeNoticeInfo attendeeNoticeInfo = new AttendeeNoticeInfo();
//                attendeeNoticeInfo.setConferenceId(conferenceContext.getId());
//                attendeeNoticeInfo.setType(2);
//                attendeeNoticeInfo.setSize(1);
//                attendeeNoticeInfo.setChangeList(changeList);
//                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.PARTICIPANT_CHANGED, attendeeNoticeInfo);
//
//
//                if(!a.getState().getOnline()){
//                    ConferenceState conferenceState = conferenceContext.getDetailConference().getConferenceState();
//                    String id = a.getGeneralParam().getId();
//                    String chairmanId = conferenceState.getChairmanId();
//                    String broadcastId = conferenceState.getBroadcastId();
//                    String spokesmanId = conferenceState.getSpokesmanId();
//                    String chooseId = conferenceState.getChooseId();
//                    if(Objects.equals(id,chairmanId)){
//                        conferenceState.setChairmanId("");
//                        conferenceState.setChairman(null);
//                    }
//                    if(Objects.equals(id,broadcastId)){
//                        conferenceState.setBroadcastId("");
//                    }
//                    if(Objects.equals(id,spokesmanId)){
//                        conferenceState.setSpokesmanId("");
//                    }
//                    if(Objects.equals(id,chooseId)){
//                        conferenceState.setChooseId("");
//                    }
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("state",conferenceState);
//                    Smc2WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGED,jsonObject);
//                }
//
//                processUpdateParticipant(conferenceContext,a,false);
//            }
//        }
//
//        if (conferenceContext != null) {
//            AttendeeSmc2 attendeeSmc2 = matchAttendee(conferenceContext,a);
//            if (attendeeSmc2!= null) {
//                conferenceContext.getParticipantAttendeeAllMap().put(a.getGeneralParam().getId(),attendeeSmc2);
//                attendeeSmc2.setConferenceNumber(conferenceContext.getConferenceNumber());
//                attendeeSmc2.setSmcParticipant(a);
//                updateByParticipant(conferenceContext, a, attendeeSmc2);
//                processUpdateParticipant(conferenceContext, attendeeSmc2, false);
//            }
//        }
//    }
//    private void processUpdateParticipant(Smc2ConferenceContext conferenceContext, AttendeeSmc2 attendeeTele, boolean updateMediaInfo) {
//        IBusiMcuSmc2HistoryConferenceService teleHistoryConferenceService = BeanFactory.getBean(IBusiMcuSmc2HistoryConferenceService.class);
//        teleHistoryConferenceService.updateBusiHistoryParticipant(conferenceContext, attendeeTele, updateMediaInfo);
//    }
//
//    public void updateByParticipant(Smc2ConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO participant, AttendeeSmc2 a) {
//        synchronized (a) {
//
//            try {
//                a.resetUpdateMap();
//
//                if (participant != null) {
//                    if (a.isMeetingJoined()) {
//                        if (participant.getState().getMute() != null) {
//                            int value =participant.getState().getMute() ? AttendeeMixingStatus.NO.getValue() : AttendeeMixingStatus.YES.getValue();
//                            if (value != a.getMixingStatus()) {
//                                a.setMixingStatus(value);
//                            }
//
//                        }
//
//                        if (participant.getState().getVideoMute() != null) {
//                            int value = participant.getState().getVideoMute() ? AttendeeVideoStatus.NO.getValue() : AttendeeVideoStatus.YES.getValue();
//                            if (value != a.getVideoStatus()) {
//                                a.setVideoStatus(value);
//                            }
//                        }
//
//                        if(!Objects.equals(a.getName(),participant.getGeneralParam().getName())){
//                            a.setName(participant.getGeneralParam().getName());
//                        }
//
//                    }
//
//                }
//
//
//                if (a instanceof TerminalAttendeeSmc2 || a instanceof McuAttendeeSmc2) {
//                    new RegisteredAttendeeUpdateProcessor(participant, a, conferenceContext).process();
//                } else {
//                    new OtherAttendeeUpdateProcessor(participant, a, conferenceContext).process();
//                }
//
//                if (a.isMeetingJoined() && !ObjectUtils.isEmpty(participant.getGeneralParam().getName())) {
//                    Long terminalId = null;
//                    String name = "";
//                    if (a instanceof TerminalAttendeeSmc2) {
//                        terminalId = a.getTerminalId();
//                    } else if (a instanceof TerminalAttendeeSmc2) {
//                        terminalId = a.getTerminalId();
//                    }
//                    if (terminalId != null) {
//                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
//                        if (busiTerminal != null) {
//                            if (!ObjectUtils.isEmpty(busiTerminal.getName())) {
//                                name += busiTerminal.getName();
//                                if (!name.equals(participant.getGeneralParam().getName())) {
//                                    name += "(" + participant.getGeneralParam().getName() + ")";
//                                }
//                            }
//                        }
//                    }
//                    if (!ObjectUtils.isEmpty(name)) {
//                        a.setName(name);
//                    } else {
//                        a.setName(participant.getGeneralParam().getName());
//                    }
//                }
//
//                if (a.getUpdateMap().size() > 1) {
//                    Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(a.getUpdateMap()));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                logger.error(e.getMessage());
//            }
//
//        }
//    }
//
//    private AttendeeSmc2 matchAttendee(Smc2ConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO participant) {
//        if (!ObjectUtils.isEmpty(participant.getAttendeeId())) {
//            AttendeeSmc2 a = conferenceContext.getAttendeeById(participant.getAttendeeId());
//
//            // 重新赋值，解决主被叫同时进行呼叫的情况下，导致的AttendeeId和ParticipantUuid相互绑定失败问题
//            if (a != null) {
//                a.setParticipantUuid(participant.getGeneralParam().getId());
//            }
//            return a;
//        }
//
//        // 注册终端匹配，忽略端口号以后
//        String remoteParty = participant.getGeneralParam().getUri();
//        if (remoteParty.contains(":")) {
//            remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
//        }
//        Map<String, AttendeeSmc2> uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remoteParty);
//        if (uuidAttendeeMap != null) {
//            for (Map.Entry<String, AttendeeSmc2> stringAttendeeTeleEntry : uuidAttendeeMap.entrySet()) {
//                AttendeeSmc2 value = stringAttendeeTeleEntry.getValue();
//                value.setParticipantUuid(participant.getGeneralParam().getId());
//                return value;
//            }
//        }
//        String remotePartyNew = "";
//        if (uuidAttendeeMap == null) {
//            if (remoteParty.contains("@")) {
//                try {
//                    String[] remotePartyArr = remoteParty.split("@");
//                    String credential = remotePartyArr[0];
//                    String ip = remotePartyArr[1];
//                    if (org.springframework.util.StringUtils.hasText(ip)) {
//                        FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByDomainName(ip);
//                        if (fsbcBridge != null) {
//                            String remotePartyIp = credential + "@" + fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
//                            uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remotePartyIp);
//                        }
//                        if (uuidAttendeeMap == null) {
//                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByDomainName(ip);
//                            if (fcmBridge != null) {
//                                String remotePartyIp = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
//                                uuidAttendeeMap = conferenceContext.getUuidAttendeeMapByUri(remotePartyIp);
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                }
//            }
//        } else {
//            if (remoteParty.contains("@")) {
//                String[] remotePartyArr = remoteParty.split("@");
//                String credential = remotePartyArr[0];
//                String ip = remotePartyArr[1];
//                if (org.springframework.util.StringUtils.hasText(ip)) {
//                    FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByIp(ip);
//                    if (fsbcBridge != null) {
//                        String domainName = fsbcBridge.getBusiFsbcRegistrationServer().getDomainName();
//                        if (!ObjectUtils.isEmpty(domainName)) {
//                            remotePartyNew = credential + "@" + domainName;
//                        }
//                    } else {
//                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByIp(ip);
//                        if (fcmBridge != null) {
//                            String domainName = fcmBridge.getBusiFreeSwitch().getDomainName();
//                            if (!ObjectUtils.isEmpty(domainName)) {
//                                remotePartyNew = credential + "@" + domainName;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        if (uuidAttendeeMap == null) {
//            return null;
//        }
//
//        if (!ObjectUtils.isEmpty(participant.getAttendeeId())) {
//            AttendeeSmc2 a = uuidAttendeeMap.get(participant.getAttendeeId());
//
//            // 重新赋值，解决主被叫同时进行呼叫的情况下，导致的AttendeeId和ParticipantUuid相互绑定失败问题
//            a.setParticipantUuid(participant.getGeneralParam().getId());
//            if (!ObjectUtils.isEmpty(remotePartyNew)) {
//                a.setRemotePartyNew(remotePartyNew);
//                if (remotePartyNew.contains("@")) {
//                    a.setIpNew(remotePartyNew.split("@")[1]);
//                } else {
//                    a.setIpNew(remotePartyNew);
//                }
//            }
//            return a;
//        }
//
//        synchronized (participant) {
//            if (!ObjectUtils.isEmpty(participant.getAttendeeId())) {
//                AttendeeSmc2 a = uuidAttendeeMap.get(participant.getAttendeeId());
//                if (!ObjectUtils.isEmpty(remotePartyNew)) {
//                    a.setRemotePartyNew(remotePartyNew);
//                    if (remotePartyNew.contains("@")) {
//                        a.setIpNew(remotePartyNew.split("@")[1]);
//                    } else {
//                        a.setIpNew(remotePartyNew);
//                    }
//                }
//                return a;
//            }
//
//            AttendeeSmc2 a = matchAttendee(participant, uuidAttendeeMap);
//            if (a == null) {
//                a = uuidAttendeeMap.get(remoteParty);
//                if (a != null) {
//                    a.setParticipantUuid(participant.getGeneralParam().getId());
//                }
//            }
//            if (!ObjectUtils.isEmpty(remotePartyNew)) {
//                a.setRemotePartyNew(remotePartyNew);
//                if (remotePartyNew.contains("@")) {
//                    a.setIpNew(remotePartyNew.split("@")[1]);
//                } else {
//                    a.setIpNew(remotePartyNew);
//                }
//            }
//
//            return a;
//        }
//    }
//
//    private AttendeeSmc2 matchAttendee(SmcParitipantsStateRep.ContentDTO participant, Map<String, AttendeeSmc2> uuidAttendeeMap) {
//        for (Iterator<AttendeeSmc2> iterator = uuidAttendeeMap.values().iterator(); iterator.hasNext(); ) {
//            AttendeeSmc2 a = iterator.next();
//            synchronized (a) {
//                if (ObjectUtils.isEmpty(a.getParticipantUuid())) {
//                    a.setParticipantUuid(participant.getGeneralParam().getId());
//                    participant.setAttendeeId(a.getId());
//                    return a;
//                }
//            }
//        }
//        return null;
//    }
//
//}

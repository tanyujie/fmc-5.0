package com.paradisecloud.smc3.websocket.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.smc3.busi.AttendeeCountingStatistics;
import com.paradisecloud.smc3.busi.ConferenceNode;
import com.paradisecloud.smc3.busi.DefaultAttendeeOperation;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.McuAttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.enumer.AttendeeImportance;
import com.paradisecloud.smc3.busi.operation.AttendeeOperation;
import com.paradisecloud.smc3.busi.operation.CallTheRollAttendeeOperation;
import com.paradisecloud.smc3.busi.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.smc3.busi.operation.PollingAttendeeOperation;
import com.paradisecloud.smc3.busi.updateprocessor.SelfCallAttendeeNewProcessor;
import com.paradisecloud.smc3.busi.utils.AttendeeUtils;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.invoker.ConferenceState;
import com.paradisecloud.smc3.model.ChooseMultiPicInfo;
import com.paradisecloud.smc3.model.ParticipantState;
import com.paradisecloud.smc3.model.request.MultiPicInfoReq;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import com.paradisecloud.smc3.service.impls.BusiSmc3ConferenceServiceImpl;
import com.paradisecloud.smc3.service.interfaces.IAttendeeSmc3Service;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3HistoryConferenceService;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3ConferenceService;
import com.paradisecloud.smc3.websocket.interfaces.ISmc3WebSocketService;
import com.paradisecloud.smc3.websocket.processormessage.Smc3RealTimeInfoProcessorMessage;
import com.paradisecloud.smc3.websocket.processormessage.Smc3RealTimeInfoProcessorMessageQueue;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.paradisecloud.smc3.websocket.processormessage.Smc3MessageInfoProcessorMessage.SUB_1;

/**
 * @author nj
 * @date 2022/8/22 9:42
 */
public class Smc3WebSocketProcessor {

    private Logger logger=LoggerFactory.getLogger(getClass());

    public static final int PAGE = 10;
    public static final int _TYPE = 3;
    public static final String ONLINE = "online";
    public static final String QUIET = "quiet";
    public static final String MUTE = "mute";
    public static final String VOICE = "voice";
    public static final String VIDEO_MUTE = "videoMute";
    public static final String CALL_FAIL_REASON = "callFailReason";
    public static final String VOLUME = "volume";
    public static final String TYPE = "type";
    public static final String URI = "uri";
    private static final Map<String, List<String>> realTimeParticipantsMap = new ConcurrentHashMap<>();
    private static final Map<String, String> smcParticipantNames = new ConcurrentHashMap<>();
    private static final Map<String, JSONObject> realTimeMap = new ConcurrentHashMap<>();
    private static final Map<String, Object> realTimeGroupMap = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, JSONObject>> ConferenceParitipantsRealTimeMap = new ConcurrentHashMap<>();

    private final SMC3WebsocketClient websocketClient;
    private final Smc3Bridge smcBridge;
    private final ISmc3WebSocketService webSocketService;
    private final Map<String, List<String>> particiPantsMap = new ConcurrentHashMap<>();
    private final Map<String, String> groupIdMap = new ConcurrentHashMap<>();
    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private int number = 2;
    public Smc3WebSocketProcessor(SMC3WebsocketClient websocketClient, Smc3Bridge smcBridge, ISmc3WebSocketService webSocketService) {
        this.websocketClient = websocketClient;
        this.smcBridge = smcBridge;
        this.webSocketService = webSocketService;
    }

    public static Map<String, List<String>> getRealTimeParticipantsMap() {
        return realTimeParticipantsMap;
    }

    public static Map<String, String> getSmcParticipantNames() {
        return smcParticipantNames;
    }

    public static Map<String, JSONObject> getRealTimeMap() {
        return realTimeMap;
    }

    public static Map<String, Object> getRealTimeGroupMap() {
        return realTimeGroupMap;
    }

    public static Map<String, Map<String, JSONObject>> getConferenceParitipantsRealTimeMap() {
        return ConferenceParitipantsRealTimeMap;
    }

    public SMC3WebsocketClient getWebsocketClient() {
        return websocketClient;
    }

    public Smc3Bridge getSmcBridge() {
        return smcBridge;
    }

    public void sendFirstSubscriptionRequest() {
        Map<String, Smc3Bridge> conferenceBridge = Smc3BridgeCache.getInstance().getIpToTeleBridgeMap();
        for (Map.Entry<String, Smc3Bridge> stringSmcBridgeEntry : conferenceBridge.entrySet()) {
            Smc3Bridge value = stringSmcBridgeEntry.getValue();
            String conferenceId = stringSmcBridgeEntry.getKey();
            if (Objects.equals(smcBridge.getIp(), value.getIp())) {
                String tokenByConferencesId = smcBridge.getSmcConferencesInvoker().getTokenByConferencesId(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                long groupId = System.currentTimeMillis();
                //订阅变化changeList
                String res = smcBridge.getSmcParticipantsInvoker().getConferencesParticipantsState(conferenceId, 0, 1000, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                SmcParitipantsStateRep smcParitipantsStateRep = JSON.parseObject(res, SmcParitipantsStateRep.class);
                if (smcParitipantsStateRep != null) {
                    List<SmcParitipantsStateRep.ContentDTO> content = smcParitipantsStateRep.getContent();
                    List<String> participantIdList = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(content)) {
                        for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
                            String participantId = contentDTO.getState().getParticipantId();
                            if (contentDTO.getState().getOnline()) {
                                //请求订阅
                                participantIdList.add(participantId);
                            }
                        }
                        smcBridge.getSmcConferencesInvoker().realTimeInfoGroup(conferenceId, groupId + "", participantIdList, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

                        String destination = "/topic/conferences" + conferenceId + "/participants/groups/" + groupId + "realTimeInfo";
                        TopicMessage subscribe = new TopicMessage("SUBSCRIBE", "sub-4", tokenByConferencesId, destination);
                        String subscribeMessage = subscribe.getSubscribeMessage();
                        this.websocketClient.send(subscribeMessage);
                    }
                }
            }

        }


    }


    public void firstSubscriptionRequest(String conferenceId) {
        long groupId = System.currentTimeMillis();
        String res = null;
        while (true) {
            //订阅变化changeList
            res = smcBridge.getSmcParticipantsInvoker().getConferencesParticipantsState(conferenceId, 0, 200, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            if (res != null && !res.contains("message")) {
                break;
            }
        }

        SmcParitipantsStateRep smcParitipantsStateRep = JSON.parseObject(res, SmcParitipantsStateRep.class);
        if (smcParitipantsStateRep != null) {
            List<SmcParitipantsStateRep.ContentDTO> content = smcParitipantsStateRep.getContent();
            List<String> participantIdList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(content)) {
                for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
                    String participantId = contentDTO.getState().getParticipantId();
                    participantIdList.add(participantId);
                }
                if (participantIdList.size() <= PAGE) {
                    smcBridge.getSmcConferencesInvoker().changeListGroup(conferenceId, groupId + "", participantIdList, 10, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                } else {
                    smcBridge.getSmcConferencesInvoker().changeListGroup(conferenceId, groupId + "", participantIdList, 200, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

                }

            } else {
                smcBridge.getSmcConferencesInvoker().changeListGroup(conferenceId, groupId + "", participantIdList, 10, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }


            particiPantsMap.put(conferenceId, participantIdList);
            groupIdMap.put(conferenceId, groupId + "");
            Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceId);
            smc3ConferenceContext.setGroupId(groupId + "");

            String destination = "/topic/conferences/" + conferenceId + "/participants/groups/" + groupId;
            String tokenByConferencesId = null;
            while (true) {
                tokenByConferencesId = smcBridge.getSmcConferencesInvoker().getTokenByConferencesId(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                if (tokenByConferencesId != null && !tokenByConferencesId.contains("message")) {
                    break;
                }
            }
            TopicMessage subscribe = new TopicMessage("SUBSCRIBE", "sub-" + number++, tokenByConferencesId, destination);
            String subscribeMessage = subscribe.getSubscribeMessage();
            this.websocketClient.send(subscribeMessage);
            logger.info("SMC3  sub participant subscribeMessage:{}", subscribeMessage);
        }

    }


    public synchronized void firstSubscription(String conferenceId) {
        Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceId);
        Boolean aBoolean = smc3ConferenceContext.getSubscribe();
        logger.info("======SMC3 SUBSCRIBE==============================>>>>>>>>>>>>>>>>>>>>>>>>>>>>========================================"+aBoolean);
        if (aBoolean == null || !aBoolean) {
            subConferenceControl(conferenceId);
            firstSubscriptionRequest(conferenceId);
            smc3ConferenceContext.setSubscribe(true);
        }
    }

    public void subConferenceControl(String conferenceId) {
        String sd = "/topic/conferences/" + conferenceId;
        String tokenByConferencesId = smcBridge.getSmcConferencesInvoker().getTokenByConferencesId(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        TopicMessage subscribe = new TopicMessage("SUBSCRIBE", "sub-" + 0, tokenByConferencesId, sd);
        String subscribeMessage = subscribe.getSubscribeMessage();
        this.websocketClient.sendMessage(subscribeMessage);
        logger.info("======SMC3 SUBSCRIBE==============================>>>>>>>>>>>>>>>>>>>>>>>>>>>>========================================"+conferenceId);
    }

    public void processMessage(String message) {
        logger.info("======>==============================>>>>>>>>>>>>>>>>>>>>>>>>>>>>========================================");
        logger.info(message);
        logger.info("message<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
                StompMessage stompMessage = new StompMessage();
                try {
                    String line = reader.readLine();
                    while (line != null) {
                        if (line.startsWith("conferenceId:")) {
                            stompMessage.setConferenceId(StringUtils.substringAfter(line, ":"));
                        }
                        if (line.startsWith("destination:")) {
                            stompMessage.setDestination(StringUtils.substringAfter(line, ":"));
                        }
                        if (line.startsWith("content-type:")) {
                            stompMessage.setContentType(StringUtils.substringAfter(line, ":"));
                        }
                        if (line.startsWith("message-id:")) {
                            stompMessage.setMessageId(StringUtils.substringAfter(line, ":"));
                        }
                        if (line.startsWith("content-length:")) {
                            stompMessage.setContentLength(Integer.valueOf(StringUtils.substringAfter(line, ":")));
                        }
                        if (line.startsWith("subscription:")) {
                            stompMessage.setSubscription(StringUtils.substringAfter(line, ":"));
                        }
                        if (line.startsWith("{")) {
                            stompMessage.setObj(line);
                        }
                        line = reader.readLine();

                    }
                    String obj = stompMessage.getObj();
                    JSONObject jsonObj = JSONObject.parseObject(obj);
                    if (jsonObj != null) {
                        String subscription = stompMessage.getSubscription();
                        String conferenceId = stompMessage.getConferenceId();
                        jsonObj.put("messageId", stompMessage.getMessageId());
                        jsonObj.put("subscription", subscription);
                        jsonObj.put("conferenceId", conferenceId);

                        JSONArray conferenceStages = (JSONArray) jsonObj.get("conferenceStages");
                        Smc3ConferenceContext smc3ConferenceContext = null;

                        if (Strings.isNotBlank(conferenceId)) {
                            smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceId);
                        }

                        try {
                            if (!Objects.isNull(conferenceStages)) {
                                for (Object objStages : conferenceStages) {
                                    Smc3RealTimeInfoProcessorMessage.ConferenceStages o = JSONObject.parseObject(objStages.toString(), Smc3RealTimeInfoProcessorMessage.ConferenceStages.class);
                                    String stage = o.getStage();
                                    smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(o.getConferenceId());
                                    if (smc3ConferenceContext != null) {
                                        if (Objects.equals(stage, "ONLINE")) {
                                            Boolean aBoolean = smc3ConferenceContext.getSubscribe();
                                            if (aBoolean == null || !aBoolean) {
                                                firstSubscription(o.getConferenceId());
                                            }
                                        } else {
                                            BeanFactory.getBean(IBusiSmc3ConferenceService.class).endConference(smc3ConferenceContext.getId(), EndReasonsType.AUTO_END, false, true);
                                        }
                                        // Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObj);
                                    }

                                }
                            }
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                        Object realTimeInfo = jsonObj.get("realTimeInfo");
                        if (Objects.equals(subscription, SUB_1) || realTimeInfo != null) {
                            Smc3RealTimeInfoProcessorMessageQueue.getInstance().put(new Smc3RealTimeInfoProcessorMessage(smcBridge, jsonObj));
                        }
                        // webSocketService.process(jsonObj, smcBridge);
                        Object state = jsonObj.get("state");

                        //会控状态变化
                        try {
                            if (!Objects.isNull(state)) {
                                ConferenceState conferenceState = JSONObject.parseObject(jsonObj.toJSONString(), ConferenceState.class);
                                if (conferenceState != null) {
                                    ConferenceState.StateDTO stateDTO = conferenceState.getState();
                                    if (stateDTO != null) {

                                        List<ConferenceState.StateDTO.ParticipantPollStatusListDTO> participantPollStatusList = stateDTO.getParticipantPollStatusList();
                                        if (!CollectionUtils.isEmpty(participantPollStatusList)) {
                                            ConferenceState.StateDTO.ParticipantPollStatusListDTO participantPollStatusListDTO = participantPollStatusList.get(0);
                                            String pollStatus = participantPollStatusListDTO.getPollStatus();
                                            stateDTO.setChairmanPollStatus(pollStatus);
                                            smc3ConferenceContext.setChairmanPollStatus(PollOperateTypeDto.valueOf(pollStatus));
                                        }

                                        List<String> currentSpeakers = stateDTO.getCurrentSpeakers();
                                        List<String>   currentSpeakersSmc3=new ArrayList<>();
                                        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(currentSpeakers)){
                                            for (String currentSpeaker : currentSpeakers) {
                                                AttendeeSmc3 attendeeBySmc3Id = smc3ConferenceContext.getAttendeeBySmc3Id(currentSpeaker);
                                                currentSpeakersSmc3.add(attendeeBySmc3Id.getId());
                                            }
                                        }

                                        JSONObject jsonObject_sp = new JSONObject();
                                        jsonObject_sp.put("conferenceId", smc3ConferenceContext.getId());
                                        jsonObject_sp.put("currentSpeakers",currentSpeakersSmc3);
                                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.CURRENT_SPEAKERS, jsonObject_sp);


                                        String chairmanId = stateDTO.getChairmanId();
                                        stateDTO.setLocked(stateDTO.getLock());
                                        smc3ConferenceContext.setLocked(stateDTO.getLock());

                                        smc3ConferenceContext.setChairmanId(chairmanId);
//                                        if(Strings.isNotBlank(chairmanId)){
//                                            AttendeeSmc3 masterAttendee = smc3ConferenceContext.getMasterAttendee();
//                                            if((masterAttendee==null||(masterAttendee!=null&&!Objects.equals(masterAttendee.getSmcParticipant().getGeneralParam().getId(),chairmanId)))){
//                                                AttendeeSmc3 attendeeBySmc3Id = smc3ConferenceContext.getAttendeeBySmc3Id(chairmanId);
//                                                if(attendeeBySmc3Id!=null){
//                                                    ChangeMasterAttendeeOperation changeMasterAttendeeOperation = new ChangeMasterAttendeeOperation(smc3ConferenceContext, attendeeBySmc3Id);
//                                                    AttendeeOperation lastOP = smc3ConferenceContext.getAttendeeOperation();
//                                                    smc3ConferenceContext.setLastAttendeeOperation(lastOP);
//                                                    lastOP.cancel();
//                                                    smc3ConferenceContext.setAttendeeOperation(changeMasterAttendeeOperation);
//                                                    changeMasterAttendeeOperation.operate();
//                                                }
//
//                                            }
//                                        }else {
//                                            AttendeeOperation attendeeOperation = smc3ConferenceContext.getAttendeeOperation();
//                                            if(attendeeOperation!=null&&attendeeOperation instanceof ChangeMasterAttendeeOperation){
//                                                ChangeMasterAttendeeOperation changeMasterAttendeeOperation=(ChangeMasterAttendeeOperation)attendeeOperation;
//                                                changeMasterAttendeeOperation.cancel();
//                                            }
//                                        }


                                        String spokesmanId = stateDTO.getSpokesmanId();
//                                        if(Strings.isNotBlank(spokesmanId)){
//                                            AttendeeSmc3 attendee = smc3ConferenceContext.getAttendeeBySmc3Id(spokesmanId);
//                                            if (attendee != null) {
//                                                if(attendee.getCallTheRollStatus()== AttendeeCallTheRollStatus.NO.getValue()){
//                                                    smc3ConferenceContext.setLastAttendeeOperation(smc3ConferenceContext.getAttendeeOperation());
//                                                    CallTheRollAttendeeOperation callTheRollAttendeeOperation = new CallTheRollAttendeeOperation(smc3ConferenceContext, attendee);
//                                                    AttendeeOperation attendeeOperation = smc3ConferenceContext.getAttendeeOperation();
//                                                    attendeeOperation.cancel();
//                                                    smc3ConferenceContext.setAttendeeOperation(callTheRollAttendeeOperation);
//                                                    callTheRollAttendeeOperation.operate();
//                                                }
//                                            }
//                                        }else {
//                                            AttendeeOperation attendeeOperation = smc3ConferenceContext.getAttendeeOperation();
//                                            if(attendeeOperation!=null&&attendeeOperation instanceof CallTheRollAttendeeOperation){
//                                                CallTheRollAttendeeOperation callTheRollAttendeeOperation=(CallTheRollAttendeeOperation)attendeeOperation;
//                                                callTheRollAttendeeOperation.cancel();
//                                            }
//                                        }


                                        smc3ConferenceContext.setMaxParticipantNum(stateDTO.getMaxParticipantNum()==0?smc3ConferenceContext.getMaxParticipantNum():stateDTO.getMaxParticipantNum());
                                        stateDTO.setMaxParticipantNum(smc3ConferenceContext.getMaxParticipantNum());
                                        smc3ConferenceContext.setMuteStatus(stateDTO.getMute() == true ? 1 : 2);
                                        smc3ConferenceContext.setEnableUnmuteByGuest(stateDTO.getEnableUnmuteByGuest());
                                        smc3ConferenceContext.setEnableSiteNameEditByGuest(stateDTO.getEnableSiteNameEditByGuest());
                                        String lockPresenterId = stateDTO.getLockPresenterId();
                                        String lockPresenterId_o = smc3ConferenceContext.getLockPresenterId();
                                        if (Strings.isBlank(lockPresenterId)) {
                                            if (Strings.isNotBlank(lockPresenterId_o)) {
                                                AttendeeSmc3 attendeeBySmc3Id = smc3ConferenceContext.getAttendeeBySmc3Id(lockPresenterId_o);
                                                if (attendeeBySmc3Id != null) {
                                                    attendeeBySmc3Id.setLockPresenter(false);
                                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id.getUpdateMap());
                                                }
                                            }
                                            smc3ConferenceContext.setLockPresenterStatus(false);
                                        } else {
                                            AttendeeSmc3 attendeeBySmc3Id = smc3ConferenceContext.getAttendeeBySmc3Id(lockPresenterId);
                                            if (attendeeBySmc3Id != null) {
                                                attendeeBySmc3Id.setLockPresenter(true);
                                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id.getUpdateMap());
                                            }
                                            smc3ConferenceContext.setLockPresenterStatus(true);

                                            if (Strings.isNotBlank(lockPresenterId_o)) {
                                                AttendeeSmc3 attendeeBySmc3Id_O = smc3ConferenceContext.getAttendeeBySmc3Id(lockPresenterId_o);
                                                if (attendeeBySmc3Id_O != null) {
                                                    attendeeBySmc3Id_O.setLockPresenter(false);
                                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id_O.getUpdateMap());
                                                }
                                            }
                                        }
                                        smc3ConferenceContext.setLockPresenterId(lockPresenterId);
                                        String presenterId = smc3ConferenceContext.getPresenterId();
                                        if (Strings.isNotBlank(stateDTO.getPresenterId())) {
                                            AttendeeSmc3 attendeeBySmc3Id = smc3ConferenceContext.getAttendeeBySmc3Id(stateDTO.getPresenterId());
                                            if (attendeeBySmc3Id != null) {
                                                if (!Objects.equals(presenterId, stateDTO.getPresenterId())) {
                                                    attendeeBySmc3Id.setPresentStatus(YesOrNo.YES.getValue());
                                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id.getUpdateMap());
                                                    smc3ConferenceContext.setPresenterAttendee(attendeeBySmc3Id);
                                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext,WebsocketMessageType.ATTENDEE_DUAL,attendeeBySmc3Id);

                                                    if(Strings.isNotBlank(smc3ConferenceContext.getParentConferenceId())){
                                                        Smc3ConferenceContext smc3ConferenceContext1 = Smc3ConferenceContextCache.getInstance().get(smc3ConferenceContext.getParentConferenceId());
                                                        smc3ConferenceContext1.setPresenterAttendee(attendeeBySmc3Id);
                                                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext1,WebsocketMessageType.ATTENDEE_DUAL,attendeeBySmc3Id);
                                                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext1,WebsocketMessageType.ATTENDEE_UPDATE,attendeeBySmc3Id.getUpdateMap());

                                                        String presentAttendeeId = smc3ConferenceContext1.getPresenterId();
                                                        if(Strings.isNotBlank(presentAttendeeId)){
                                                            AttendeeSmc3 attendeeBySmc3Id_node = smc3ConferenceContext1.getAttendeeBySmc3Id(presentAttendeeId);
                                                            if(attendeeBySmc3Id_node!=null){
                                                                attendeeBySmc3Id_node.setPresentStatus(YesOrNo.NO.getValue());
                                                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext1, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id_node.getUpdateMap());
                                                            }

                                                        }
                                                    }
                                                    if(Objects.equals("CASCADE",smc3ConferenceContext.getCategory())){
                                                        List<ConferenceNode> cascadeConferenceTree = smc3ConferenceContext.getCascadeConferenceTree();
                                                        for (ConferenceNode conferenceNode : cascadeConferenceTree) {
                                                            String conferenceId1 = conferenceNode.getConferenceId();
                                                            String contextKey = EncryptIdUtil.parasToContextKey(conferenceId1);
                                                            Smc3ConferenceContext smc3ConferenceContext_node = Smc3ConferenceContextCache.getInstance().get(contextKey);
                                                            smc3ConferenceContext_node.setPresenterAttendee(attendeeBySmc3Id);
                                                            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext_node,WebsocketMessageType.ATTENDEE_DUAL,attendeeBySmc3Id);


                                                            if(Strings.isNotBlank(presenterId)){
                                                                AttendeeSmc3 attendeeBySmc3Id_node = smc3ConferenceContext_node.getAttendeeBySmc3Id(presenterId);
                                                                if(attendeeBySmc3Id_node!=null){
                                                                    attendeeBySmc3Id_node.setPresentStatus(YesOrNo.NO.getValue());
                                                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext_node, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id_node.getUpdateMap());
                                                                    if(Strings.isNotBlank(smc3ConferenceContext_node.getParentConferenceId())){
                                                                        Smc3ConferenceContext smc3ConferenceContext1 = Smc3ConferenceContextCache.getInstance().get(smc3ConferenceContext_node.getParentConferenceId());
                                                                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext1, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id_node.getUpdateMap());
                                                                    }
                                                                }


                                                            }


                                                        }
                                                    }
                                                    if (Strings.isNotBlank(presenterId)) {
                                                        AttendeeSmc3 oldPresenter = smc3ConferenceContext.getAttendeeBySmc3Id(presenterId);
                                                        if(oldPresenter!=null){
                                                            oldPresenter.setPresentStatus(YesOrNo.NO.getValue());
                                                            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, oldPresenter.getUpdateMap());

                                                            if(Strings.isNotBlank(smc3ConferenceContext.getParentConferenceId())){
                                                                Smc3ConferenceContext smc3ConferenceContext1 = Smc3ConferenceContextCache.getInstance().get(smc3ConferenceContext.getParentConferenceId());
                                                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext1, WebsocketMessageType.ATTENDEE_UPDATE, oldPresenter.getUpdateMap());
                                                            }
                                                        }


                                                    }
                                                }

                                                int presentStatus = attendeeBySmc3Id.getPresentStatus();
                                                if (presentStatus == YesOrNo.NO.getValue()) {
                                                    attendeeBySmc3Id.setPresentStatus(YesOrNo.YES.getValue());
                                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id.getUpdateMap());
                                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext,WebsocketMessageType.ATTENDEE_DUAL,attendeeBySmc3Id);

                                                }

                                            }

                                        } else {
                                            if (Strings.isNotBlank(presenterId)) {
                                                AttendeeSmc3 oldPresenter = smc3ConferenceContext.getAttendeeBySmc3Id(presenterId);
                                                if (oldPresenter != null) {
                                                    oldPresenter.setPresentStatus(YesOrNo.NO.getValue());
                                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, oldPresenter.getUpdateMap());

                                                    if(Strings.isNotBlank(smc3ConferenceContext.getParentConferenceId())){
                                                        Smc3ConferenceContext smc3ConferenceContext1 = Smc3ConferenceContextCache.getInstance().get(smc3ConferenceContext.getParentConferenceId());
                                                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext1, WebsocketMessageType.ATTENDEE_UPDATE, oldPresenter.getUpdateMap());
                                                    }
                                                }
                                            }
                                            if(Strings.isNotBlank(smc3ConferenceContext.getParentConferenceId())){
                                                Smc3ConferenceContext smc3ConferenceContext1 = Smc3ConferenceContextCache.getInstance().get(smc3ConferenceContext.getParentConferenceId());
                                                smc3ConferenceContext1.setPresenterAttendee(null);
                                                String presentAttendeeId = smc3ConferenceContext1.getPresenterId();
                                                if(Strings.isNotBlank(presentAttendeeId)){
                                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext1,WebsocketMessageType.ATTENDEE_DUAL,"");
                                                    AttendeeSmc3 attendeeBySmc3Id = smc3ConferenceContext1.getAttendeeBySmc3Id(presentAttendeeId);
                                                    if(attendeeBySmc3Id!=null){
                                                        attendeeBySmc3Id.setPresentStatus(YesOrNo.NO.getValue());
                                                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext1, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id.getUpdateMap());
                                                    }

                                                }
                                             //   Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext1,WebsocketMessageType.ATTENDEE_DUAL,"");



                                            }
                                            if(Objects.equals("CASCADE",smc3ConferenceContext.getCategory())){
                                                List<ConferenceNode> cascadeConferenceTree = smc3ConferenceContext.getCascadeConferenceTree();
                                                for (ConferenceNode conferenceNode : cascadeConferenceTree) {
                                                    String conferenceId1 = conferenceNode.getConferenceId();
                                                    String contextKey = EncryptIdUtil.parasToContextKey(conferenceId1);
                                                    Smc3ConferenceContext smc3ConferenceContext_node = Smc3ConferenceContextCache.getInstance().get(contextKey);
                                                    smc3ConferenceContext_node.setPresenterAttendee(null);


                                                    String presentAttendeeId = smc3ConferenceContext_node.getPresenterId();
                                                    if(Strings.isNotBlank(presentAttendeeId)){
                                                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext_node,WebsocketMessageType.ATTENDEE_DUAL,null);
                                                        AttendeeSmc3 attendeeBySmc3Id = smc3ConferenceContext_node.getAttendeeBySmc3Id(presentAttendeeId);
                                                        if(attendeeBySmc3Id!=null){
                                                            attendeeBySmc3Id.setPresentStatus(YesOrNo.NO.getValue());
                                                            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext_node, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id.getUpdateMap());
                                                        }

                                                    }
                                                }
                                            }
                                            smc3ConferenceContext.setPresenterAttendee(null);


                                        }
                                        String presenterId1 = stateDTO.getPresenterId();
                                        if(Strings.isNotBlank(presenterId1)){
                                            AttendeeSmc3 attendeeBySmc3Id = smc3ConferenceContext.getAttendeeBySmc3Id(presenterId1);
                                            if(attendeeBySmc3Id!=null){
                                                smc3ConferenceContext.setPresentAttendeeId(attendeeBySmc3Id.getId());
                                            }
                                        }else {
                                            smc3ConferenceContext.setPresentAttendeeId(null);
                                        }


                                        smc3ConferenceContext.setPresenterId(stateDTO.getPresenterId());
                                        smc3ConferenceContext.setMultiPicInfo(stateDTO.getMultiPicInfo());
                                        smc3ConferenceContext.setEnableVoiceActive(stateDTO.getEnableVoiceActive());
                                        smc3ConferenceContext.setDirecting(stateDTO.getDirecting());
                                        smc3ConferenceContext.setQuiet(stateDTO.getQuiet());

                                        String broadcastId = stateDTO.getBroadcastId();
                                        smc3ConferenceContext.setBroadId(broadcastId);
                                        if (Objects.equals("00000000-0000-0000-0000-000000000000", broadcastId)) {
                                            smc3ConferenceContext.setMultiPicBroadcastStatus(true);
                                            stateDTO.setMultiPicBroadcastStatus(true);
                                            ConferenceState.StateDTO.MultiPicInfoDTO multiPicInfo = stateDTO.getMultiPicInfo();
 //                                           AttendeeOperation attendeeOperation = smc3ConferenceContext.getAttendeeOperation();
//                                            if (attendeeOperation instanceof DefaultAttendeeOperation) {
//
//                                            }else {
//                                                smc3ConferenceContext.setLastAttendeeOperation(attendeeOperation);
//                                                attendeeOperation.cancel();
//                                                JSONObject jsonObject = new JSONObject();
//                                                jsonObject.put("multiPicInfo",multiPicInfo);
//                                                jsonObject.put("conferenceId",smc3ConferenceContext.getSmc3conferenceId());
//                                                jsonObject.put("broadcast",true);
//                                                DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(smc3ConferenceContext,jsonObject);
//                                                smc3ConferenceContext.setAttendeeOperation(defaultAttendeeOperation);
//                                                defaultAttendeeOperation.operate();
//                                            }


                                            if (Objects.equals(stateDTO.getMultiPicPollStatus(), PollOperateTypeDto.START.name())) {
                                                //广播
                                                Set<String> broadSet = new HashSet<>();
                                                List<ConferenceState.StateDTO.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfo.getSubPicList();
                                                for (ConferenceState.StateDTO.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {

                                                    String participantId = subPicListDTO.getParticipantId();
                                                    broadSet.add(participantId);
                                                }

                                                AttendeeOperation attendeeOperation = smc3ConferenceContext.getAttendeeOperation();
                                                if (attendeeOperation instanceof PollingAttendeeOperation) {
                                                    PollingAttendeeOperation pollingAttendeeOperation = (PollingAttendeeOperation) attendeeOperation;
                                                    if (!pollingAttendeeOperation.isCascadePolling(smc3ConferenceContext, smc3ConferenceContext.getMultiPicPollRequest())) {
                                                        List<AttendeeSmc3> pollingAttendeeSmc3List = pollingAttendeeOperation.getPollingAttendeeSmc3List();
                                                        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(pollingAttendeeSmc3List)) {
                                                            for (AttendeeSmc3 attendeeSmc3 : pollingAttendeeSmc3List) {
                                                                boolean contains = broadSet.contains(attendeeSmc3.getParticipantUuid());
                                                                if (contains) {
                                                                    AttendeeSmc3 attendeeBySmc3Id = smc3ConferenceContext.getAttendeeBySmc3Id(attendeeSmc3.getParticipantUuid());
                                                                    if (attendeeBySmc3Id != null) {
                                                                        AttendeeImportance.ROUND_BROADCAST.processAttendeeWebsocketMessage(attendeeBySmc3Id);
                                                                    }
                                                                    AttendeeImportance.ROUND_BROADCAST.processAttendeeWebsocketMessage(attendeeSmc3);
                                                                } else {
                                                                    if (attendeeSmc3.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                                                        AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendeeSmc3);
                                                                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeSmc3.getUpdateMap());
                                                                    }
                                                                }

                                                            }
                                                        }
                                                    }

                                                }


                                            }
                                        } else {
                                            smc3ConferenceContext.setMultiPicBroadcastStatus(false);
                                            stateDTO.setMultiPicBroadcastStatus(false);
                                            AttendeeOperation attendeeOperation = smc3ConferenceContext.getAttendeeOperation();
                                            if (attendeeOperation instanceof DefaultAttendeeOperation) {
                                                ((DefaultAttendeeOperation) attendeeOperation).cancelBroadCast();
                                            }
                                        }
                                        smc3ConferenceContext.setMultiPicPollStatus(stateDTO.getMultiPicPollStatus());
                                    }
                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, stateDTO);


                                }

                            }
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                        Object changeList = jsonObj.get("changeList");
                        List<JSONObject> stateList = null;
                        if (!Objects.isNull(changeList)) {
                            int type = (int) jsonObj.get("type");
                            stateList = JSONArray.parseArray(JSON.toJSONString(changeList), JSONObject.class);
                            if (type < _TYPE) {
                                List<String> strings = particiPantsMap.get(conferenceId);
                                for (JSONObject object : stateList) {
                                    String participantId = (String) object.get("id");
                                    if (type == 1) {
                                        if (!strings.contains(participantId)) {
                                            strings.add(participantId);
                                        }
                                    } else {
                                        strings.remove(participantId);
                                    }
                                }
                                try {
                                    if (strings.size() < PAGE) {
                                        smcBridge.getSmcConferencesInvoker().changeListGroup(conferenceId, groupIdMap.get(conferenceId), strings, 10, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                                    } else {
                                        smcBridge.getSmcConferencesInvoker().changeListGroup(conferenceId, groupIdMap.get(conferenceId), strings, 500, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                                    }
                                } finally {
                                    Threads.sleep(300);
                                }
                            }


                            switch (type) {
                                case 1:
                                    try {
                                        List<String> participantIdList = new ArrayList<>();
                                        synchronized (Smc3WebSocketProcessor.class){
                                            for (JSONObject jsonObject : stateList) {
                                                SmcParitipantsStateRep.ContentDTO contentDTO = initContent(jsonObject);
                                                contentDTO.getState().setOnline(true);
                                                contentDTO.setChangeType(type);


                                                AttendeeSmc3 a = AttendeeUtils.matchAttendee(smc3ConferenceContext, contentDTO);
                                                if (a == null) {
                                                    new SelfCallAttendeeNewProcessor(contentDTO, smc3ConferenceContext).process();
                                                } else {
                                                    processParticipants(smc3ConferenceContext, contentDTO, contentDTO.getGeneralParam(), a);
                                                }
                                                participantIdList.add(contentDTO.getGeneralParam().getId());
                                                if (a instanceof McuAttendeeSmc3) {
                                                    Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(smc3ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, a);
                                                }
                                            }
                                            if (participantIdList.size() > 0) {
                                                //订阅实时信息
                                                Smc3ConferenceContext finalSmc3ConferenceContext = smc3ConferenceContext;
                                                new Thread(() -> {
                                                    Threads.sleep(2000);
                                                    subscription(finalSmc3ConferenceContext, participantIdList);
                                                }).start();
                                            }
                                        }


                                    } catch (Exception e) {
                                       logger.info(e.getMessage());
                                    }
                                    break;
                                case 2://移除
                                case 3://修改
                                case 4://修改名称
                                    for (JSONObject jsonObject : stateList) {
                                        SmcParitipantsStateRep.ContentDTO contentDTO = initContent(jsonObject);
                                        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = contentDTO.getGeneralParam();
                                        ParticipantState participantState = contentDTO.getState();
                                        contentDTO.setChangeType(type);

                                        if (smc3ConferenceContext != null) {

                                            AttendeeSmc3 a = AttendeeUtils.matchAttendee(smc3ConferenceContext, contentDTO);
                                            if (a != null) {
                                                SmcParitipantsStateRep.ContentDTO smcParticipant = a.getSmcParticipant();
                                                if (smcParticipant != null) {
                                                    generalParam.setType(smcParticipant.getGeneralParam().getType());
                                                    generalParam.setUri(smcParticipant.getGeneralParam().getUri());
                                                }
                                                processParticipants(smc3ConferenceContext, contentDTO, generalParam, a);
                                                if(type==4){
                                                    a.setName((String)jsonObject.get("name"));
                                                    JSONObject jsonObjectjs = new JSONObject();
                                                    jsonObjectjs.put("name", jsonObject.get("name"));
                                                    jsonObjectjs.put("id",a.getId());
                                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, jsonObjectjs);
                                                }
                                            }

                                        }
                                        if (type == 2) {
                                            JSONObject jsonObjectjs = new JSONObject();
                                            jsonObjectjs.put("attendeeCountingStatistics", new AttendeeCountingStatistics(smc3ConferenceContext));
                                            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObjectjs);
                                        }

                                    }


                                    break;
                            }
                        }


                    }
                    reader.close();
                } catch (IOException e) {
                    logger.info(e.getMessage());
                }

            }

            private void subscription(Smc3ConferenceContext smc3ConferenceContext, List<String> participantIdList) {
                String conferenceId = smc3ConferenceContext.getSmc3conferenceId();
                Smc3Bridge smcBridge = smc3ConferenceContext.getSmc3Bridge();

                SMC3WebsocketClient smc3WebsocketClient = Smc3WebsocketContext.getSmcWebsocketClientMap().get(smcBridge.getBridgeIp());

                String tokenByConferencesId = Smc3WebsocketContext.getConferenceTokenId(conferenceId);
                if (Strings.isBlank(tokenByConferencesId)) {
                    tokenByConferencesId = smcBridge.getSmcConferencesInvoker().getTokenByConferencesId(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    Smc3WebsocketContext.setConferenceTokenId(conferenceId, tokenByConferencesId);
                }

                long groupId = System.currentTimeMillis();
                //  Smc3RealTimeInfoProcessorMessage.getGroupIdP().add("sub-"+groupId);
                realTimeParticipantsMap.put("sub-" + groupId, participantIdList);
                smcBridge.getSmcConferencesInvoker().realTimeInfoGroup(conferenceId, groupId + "", participantIdList, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                String destination = "/topic/conferences/" + conferenceId + "/participants/groups/" + groupId + "/realTimeInfo";
                TopicMessage subscribe = new TopicMessage("SUBSCRIBE", "sub-" + groupId, tokenByConferencesId, destination);
                String subscribeMessage = subscribe.getSubscribeMessage();
                smc3WebsocketClient.sendMessage(subscribeMessage);


            }

            private void processParticipants(Smc3ConferenceContext smc3ConferenceContext, SmcParitipantsStateRep.ContentDTO contentDTO, SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam, AttendeeSmc3 a) {
                smc3ConferenceContext.getParticipantAttendeeAllMap().put(generalParam.getId(), a);
                a.setConferenceNumber(smc3ConferenceContext.getConferenceNumber());
                a.setSmcParticipant(contentDTO);
                AttendeeUtils.updateByParticipant(smc3ConferenceContext, contentDTO, a);
                processUpdateParticipant(smc3ConferenceContext, a, contentDTO.getChangeType() != 1);
            }

        });


    }

    private void processUpdateParticipant(Smc3ConferenceContext conferenceContext, AttendeeSmc3 attendeeSmc3, boolean updateMediaInfo) {
        IBusiMcuSmc3HistoryConferenceService teleHistoryConferenceService = BeanFactory.getBean(IBusiMcuSmc3HistoryConferenceService.class);
        teleHistoryConferenceService.updateBusiHistoryParticipant(conferenceContext, attendeeSmc3, updateMediaInfo);
    }

    private SmcParitipantsStateRep.ContentDTO initContent(JSONObject jsonObject) {
        SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
        ParticipantState participantState = new ParticipantState();
        if (jsonObject.get(URI) != null) {
            generalParam.setUri((String) jsonObject.get("uri"));
        }
        if (jsonObject.get(TYPE) != null) {
            generalParam.setType((Integer) jsonObject.get("type"));
        }

        Object id = jsonObject.get("id");
        if (id != null) {
            participantState.setParticipantId((String) id);
            generalParam.setId((String) id);
        }
        Object participantId = jsonObject.get("participantId");
        if (participantId != null) {
            participantState.setParticipantId((String) participantId);
            generalParam.setId((String) participantId);
        }

        if (jsonObject.get(VOLUME) != null) {
            participantState.setVolume((Integer) jsonObject.get("volume"));
        }
        if (jsonObject.get(CALL_FAIL_REASON) != null) {
            participantState.setCallFailReason((Integer) jsonObject.get("callFailReason"));
        }
        Object videoSwitchAttribute = jsonObject.get("videoSwitchAttribute");
        if (videoSwitchAttribute != null) {
            participantState.setVideoSwitchAttribute((Integer) videoSwitchAttribute);
        }
        String name = (String) jsonObject.get("name");
        if (Strings.isNotBlank(name)) {
            generalParam.setName(name);
        }

        if (jsonObject.get(ONLINE) != null) {
            participantState.setOnline((Boolean) jsonObject.get("online"));
        }
        if (jsonObject.get(QUIET) != null) {
            participantState.setQuiet((Boolean) jsonObject.get("quiet"));
        }
        if (jsonObject.get(MUTE) != null) {
            participantState.setMute((Boolean) jsonObject.get("mute"));
        }
        if (jsonObject.get(VOICE) != null) {
            participantState.setVoice((Boolean) jsonObject.get("voice"));
        }
        if (jsonObject.get(VIDEO_MUTE) != null) {
            participantState.setVideoMute((Boolean) jsonObject.get("videoMute"));
        }
        contentDTO.setState(participantState);
        contentDTO.setGeneralParam(generalParam);
        contentDTO.setTerminalOnline(true);

        Object multiPicInfo = jsonObject.get("multiPicInfo");
        if (multiPicInfo != null) {
            contentDTO.getState().setMultiPicInfo(JSONObject.parseObject(JSONObject.toJSONString(multiPicInfo), ChooseMultiPicInfo.MultiPicInfoDTO.class));
        }

        return contentDTO;
    }

}

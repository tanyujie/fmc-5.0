package com.paradisecloud.smc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.ParticipantState;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.smc.cache.modle.*;
import com.paradisecloud.smc.interfaces.ISmcWebSocketService;
import com.paradisecloud.smc.processormessage.RealTimeInfoProcessorMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author nj
 * @date 2022/8/22 9:42
 */
public class SmcWebSocketProcessor {

    public static final int PAGE = 10;
    public static final int _TYPE = 3;
    private final Logger logger = LoggerFactory.getLogger(SmcWebSocketProcessor.class);

    private final SMCWebsocketClient websocketClient;

    private final SmcBridge smcBridge;

    private final ISmcWebSocketService webSocketService;
    private final Map<String, List<String>> particiPantsMap = new ConcurrentHashMap<>();
    private final Map<String, String> groupIdMap = new ConcurrentHashMap<>();
    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private int number = 2;

    public SmcWebSocketProcessor(SMCWebsocketClient websocketClient, SmcBridge smcBridge, ISmcWebSocketService webSocketService) {
        this.websocketClient = websocketClient;
        this.smcBridge = smcBridge;
        this.webSocketService = webSocketService;
    }

    public SMCWebsocketClient getWebsocketClient() {
        return websocketClient;
    }

    public SmcBridge getSmcBridge() {
        return smcBridge;
    }

    public void sendFirstSubscriptionRequest() {
        Map<String, SmcBridge> conferenceBridge = SmcBridgeCache.getInstance().getConferenceBridge();
        for (Map.Entry<String, SmcBridge> stringSmcBridgeEntry : conferenceBridge.entrySet()) {
            SmcBridge value = stringSmcBridgeEntry.getValue();
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
        }

    }

    public void realTimeSubcribe(String conferenceId, String sub) {
        try {
            String tokenByConferencesId = smcBridge.getSmcConferencesInvoker().getTokenByConferencesId(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            List<String> participantIdList = new ArrayList<>();
            long groupId = System.currentTimeMillis();
            participantIdList = particiPantsMap.get(conferenceId);
            this.smcBridge.getSmcConferencesInvoker().realTimeInfoGroup(conferenceId, groupId + "", participantIdList, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            String destination = "/topic/conferences/" + conferenceId + "/participants/groups/" + groupId + "/realTimeInfo";
            TopicMessage subscribe = new TopicMessage("SUBSCRIBE", sub, tokenByConferencesId, destination);
            String subscribeMessage = subscribe.getSubscribeMessage();
            this.websocketClient.send(subscribeMessage);
            RealTimeInfoProcessorMessage.getRealTimeGroupMap().put(conferenceId + sub, groupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized void firstSubscription(String conferenceId) {
        Boolean aBoolean = SmcConferenceContextCache.getInstance().getSmcConferenceSubcribeMap().get(conferenceId);
        if (aBoolean == null || !aBoolean) {
            subConferenceControl(conferenceId);
            firstSubscriptionRequest(conferenceId);

            SmcConferenceContextCache.getInstance().getSmcConferenceSubcribeMap().put(conferenceId, true);
        }
    }

    public void subConferenceControl(String conferenceId) {
        String sd = "/topic/conferences/" + conferenceId;
        String tokenByConferencesId = smcBridge.getSmcConferencesInvoker().getTokenByConferencesId(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        TopicMessage subscribe = new TopicMessage("SUBSCRIBE", "sub-" + 0, tokenByConferencesId, sd);
        String subscribeMessage = subscribe.getSubscribeMessage();
        this.websocketClient.sendMessage(subscribeMessage);
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
                        try {
                            if (!Objects.isNull(conferenceStages)) {
                                for (Object objStages : conferenceStages) {
                                    RealTimeInfoProcessorMessage.ConferenceStages o = JSONObject.parseObject(objStages.toString(), RealTimeInfoProcessorMessage.ConferenceStages.class);
                                    String stage = o.getStage();

                                    if (Objects.equals(stage, "ONLINE")) {
                                        Boolean aBoolean = SmcConferenceContextCache.getInstance().getSmcConferenceSubcribeMap().get(o.getConferenceId());
                                        if (aBoolean == null || !aBoolean) {
                                            firstSubscription(o.getConferenceId());
                                        }
                                    } else {
                                        particiPantsMap.remove(o.getConferenceId());
                                        groupIdMap.remove(o.getConferenceId());
                                        SmcConferenceContextCache.getInstance().getSmcConferenceStateMap().remove(o.getConferenceId());
                                    }
                                    SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(o.getConferenceId(), SmcWebsocketMessageType.CONFERENCE_STAGE, jsonObj);

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        webSocketService.process(jsonObj, smcBridge);
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
                                        }
                                    }
                                    SmcConferenceContextCache.getInstance().getSmcConferenceStateMap().put(conferenceId, conferenceState);
                                    String chooseId = SmcConferenceContextCache.getInstance().getChooseParticipantMap().get(conferenceId);
                                    conferenceState.getState().setChooseId(chooseId);
                                    SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CONFERENCE_CHANGED, conferenceState);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
                                if (strings.size() < PAGE) {
                                    smcBridge.getSmcConferencesInvoker().changeListGroup(conferenceId, groupIdMap.get(conferenceId), strings, 10, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                                } else {
                                    smcBridge.getSmcConferencesInvoker().changeListGroup(conferenceId, groupIdMap.get(conferenceId), strings, 500, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                                }
                            }


                            switch (type) {
                                case 1:
                                    try {
                                        List<SmcParitipantsStateRep.ContentDTO> contentDTOList = new ArrayList<>();
                                        for (JSONObject jsonObject : stateList) {
                                            SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
                                            SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
                                            generalParam.setId((String) jsonObject.get("id"));
                                            generalParam.setName((String) jsonObject.get("name"));
                                            generalParam.setUri((String) jsonObject.get("uri"));
                                            generalParam.setType((int) jsonObject.get("type"));
                                            ParticipantState participantState = new ParticipantState();
                                            participantState.setParticipantId((String) jsonObject.get("id"));
                                            participantState.setOnline(true);
                                            contentDTO.setState(participantState);
                                            contentDTO.setGeneralParam(generalParam);
                                            contentDTO.setTerminalOnline(true);
                                            contentDTOList.add(contentDTO);
                                        }
                                        if (!CollectionUtils.isEmpty(contentDTOList) && contentDTOList.size() > 0) {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("changeList", contentDTOList);
                                            logger.error(JSONObject.toJSONString(jsonObject) + "PARTICIPANT_ADD");
                                            SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.PARTICIPANT_ADD, jsonObject);
                                            SmcUpdateParticipantQueue.getInstance().put(jsonObject);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 2:
                                    SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.PARTICIPANT_REMOVE, jsonObj);
                                    SmcUpdateParticipantQueue.getInstance().put(jsonObj);
                                    break;
                                case 3:
                                    SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.PARTICIPANT_CHANGED, jsonObj);
                                    SmcUpdateParticipantQueue.getInstance().put(jsonObj);
                                    break;
                                case 4:
                                    SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.PARTICIPANT_NAME_CHANGED, jsonObj);
                                    break;
                            }
                        }
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        });


    }


}

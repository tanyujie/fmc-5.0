package com.paradisecloud.smc.processormessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.ParticipantState;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.smc.cache.modle.*;
import com.paradisecloud.smc.SMCWebsocketClient;
import com.paradisecloud.smc.SmcWebSocketProcessor;
import com.paradisecloud.smc.StompMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author nj
 * @date 2023/5/15 15:34
 */
public class MessageInfoProcessorMessage extends BusiProcessorMessage{

    private Logger logger= LoggerFactory.getLogger(getClass());

    private SmcWebSocketProcessor smcWebSocketProcessor;

    public static final int PAGE = 10;
    public static final int _TYPE = 3;
    public static final String SUB_1 = "sub-1";

    @Override
    protected void process0() {
        String message = JSONObject.toJSONString(updateItem);
        StompMessage stompMessage = JSONObject.parseObject(message, StompMessage.class);

        try {
            String obj = stompMessage.getObj();
            JSONObject jsonObj = JSONObject.parseObject(obj);
            if (jsonObj != null) {
                String subscription = stompMessage.getSubscription();
                String conferenceId = stompMessage.getConferenceId();
                jsonObj.put("messageId", stompMessage.getMessageId());
                jsonObj.put("subscription", subscription);
                jsonObj.put("conferenceId", conferenceId);

                JSONArray conferenceStages = (JSONArray) jsonObj.get("conferenceStages");
                if (!Objects.isNull(conferenceStages)) {
                    for (Object objStages : conferenceStages) {
                        RealTimeInfoProcessorMessage.ConferenceStages o = JSONObject.parseObject(objStages.toString(), RealTimeInfoProcessorMessage.ConferenceStages.class);
                        String stage = o.getStage();

                        if (Objects.equals(stage, "ONLINE")) {
                            Boolean aBoolean = SmcConferenceContextCache.getInstance().getSmcConferenceSubcribeMap().get(o.getConferenceId());
                            if(aBoolean==null||!aBoolean){
                                smcWebSocketProcessor.subConferenceControl(o.getConferenceId());
                                smcWebSocketProcessor.firstSubscriptionRequest(o.getConferenceId());
                            }
                        } else {
                            SmcConferenceContextCache.getInstance().getParticiPantsMap().remove(o.getConferenceId());
                            SmcConferenceContextCache.getInstance().getGroupIdMap().remove(o.getConferenceId());
                            SmcConferenceContextCache.getInstance().getSmcConferenceStateMap().remove(o.getConferenceId());
                        }
                        SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(o.getConferenceId(), SmcWebsocketMessageType.CONFERENCE_STAGE, jsonObj);

                    }
                }
                Object realTimeInfo = updateItem.get("realTimeInfo");
                if(Objects.equals(subscription, SUB_1)||realTimeInfo!=null){
                    RealTimeInfoProcessorMessageQueue.getInstance().put(new RealTimeInfoProcessorMessage(smcBridge,jsonObj));
                }

                Object state = jsonObj.get("state");
                //会控状态变化
                if (!Objects.isNull(state)) {
                    ConferenceState conferenceState = JSONObject.parseObject(jsonObj.toJSONString(), ConferenceState.class);
                    if(conferenceState!=null){
                        SmcConferenceContextCache.getInstance().getSmcConferenceStateMap().put(conferenceState.getState().getConferenceId(),conferenceState);
                        ConferenceState.StateDTO stateDTO = conferenceState.getState();
                        if(stateDTO!=null){
                            List<ConferenceState.StateDTO.ParticipantPollStatusListDTO> participantPollStatusList = stateDTO.getParticipantPollStatusList();
                            if(!CollectionUtils.isEmpty(participantPollStatusList)){
                                ConferenceState.StateDTO.ParticipantPollStatusListDTO participantPollStatusListDTO = participantPollStatusList.get(0);
                                String pollStatus = participantPollStatusListDTO.getPollStatus();
                                stateDTO.setChairmanPollStatus(pollStatus);
                            }
                        }
                        SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CONFERENCE_CHANGED, conferenceState);
                    }

                }
                Object changeList = jsonObj.get("changeList");
                List<JSONObject> stateList=null;
                if (!Objects.isNull(changeList)) {
                    int type = (int) jsonObj.get("type");
                    if (type < _TYPE) {
                        stateList = JSONArray.parseArray(JSON.toJSONString(changeList), JSONObject.class);
                        List<String> strings =  SmcConferenceContextCache.getInstance().getParticiPantsMap().get(conferenceId);
                        for (JSONObject object : stateList) {
                            String participantId = (String)object.get("id");
                            if (type == 1) {
                                strings.add(participantId);
                            } else {
                                strings.remove(participantId);
                            }
                        }
                        if (strings.size() <= PAGE) {
                            smcBridge.getSmcConferencesInvoker().changeListGroup(conferenceId,  SmcConferenceContextCache.getInstance().getGroupIdMap().get(conferenceId), strings, 10, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                        } else {
                            smcBridge.getSmcConferencesInvoker().changeListGroup(conferenceId,  SmcConferenceContextCache.getInstance().getGroupIdMap().get(conferenceId), strings, 300, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                        }

                    }

                    switch (type) {
                        case 1:
                            List<SmcParitipantsStateRep.ContentDTO> contentDTOList = new ArrayList<>();
                            for (JSONObject jsonObject : stateList) {
                                SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
                                SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam=  new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
                                generalParam.setId((String)jsonObject.get("id"));
                                generalParam.setName((String)jsonObject.get("name"));
                                generalParam.setUri((String)jsonObject.get("uri"));
                                generalParam.setType((int)jsonObject.get("type"));
                                ParticipantState participantState = new ParticipantState();
                                participantState.setParticipantId((String)jsonObject.get("id"));
                                participantState.setOnline(true);
                                contentDTO.setState(participantState);
                                contentDTO.setGeneralParam(generalParam);
                                contentDTO.setTerminalOnline(true);
                                contentDTOList.add(contentDTO);
                            }
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("changeList",contentDTOList);
                            SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.PARTICIPANT_ADD, jsonObject);
                            break;
                        case 2:
                            SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.PARTICIPANT_REMOVE, jsonObj);
                            break;
                        case 3:
                            SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.PARTICIPANT_CHANGED, jsonObj);
                            break;
                    }

                }
                logger.info(jsonObj.toJSONString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public MessageInfoProcessorMessage(SmcBridge smcBridge, JSONObject updateItem,SmcWebSocketProcessor smcWebSocketProcessor) {
        super(smcBridge, updateItem, updateItem.getString("messageId"));
        this.smcWebSocketProcessor=smcWebSocketProcessor;
    }

}

package com.paradisecloud.smc3.websocket.processormessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.websocket.client.Smc3WebSocketProcessor;
import com.paradisecloud.smc3.websocket.client.StompMessage;
import com.paradisecloud.smc3.invoker.ConferenceState;
import com.paradisecloud.smc3.model.ParticipantState;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author nj
 * @date 2023/5/15 15:34
 */
public class Smc3MessageInfoProcessorMessage extends Smc3BusiProcessorMessage {

    private Logger logger= LoggerFactory.getLogger(getClass());

    private Smc3WebSocketProcessor smc3WebSocketProcessor;

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
                Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceId);
                JSONArray conferenceStages = (JSONArray) jsonObj.get("conferenceStages");
                if (!Objects.isNull(conferenceStages)) {
                    for (Object objStages : conferenceStages) {
                        Smc3RealTimeInfoProcessorMessage.ConferenceStages o = JSONObject.parseObject(objStages.toString(), Smc3RealTimeInfoProcessorMessage.ConferenceStages.class);
                        String stage = o.getStage();
                        Smc3ConferenceContext smc3ConferenceContextC = Smc3ConferenceContextCache.getInstance().get(o.getConferenceId());

                        if(smc3ConferenceContextC==null){
                            //TODO
                        }
                        if (Objects.equals(stage, "ONLINE")) {
                            Boolean subscribe = smc3ConferenceContext.getSubscribe();
                            if(subscribe==null||!subscribe){
                                smc3WebSocketProcessor.subConferenceControl(o.getConferenceId());
                                smc3WebSocketProcessor.firstSubscriptionRequest(o.getConferenceId());
                            }
                        } else {
                            //TODO 结束会议
                        }
                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContextC, WebsocketMessageType.CONFERENCE_CHANGE, jsonObj);

                    }
                }
                Object realTimeInfo = updateItem.get("realTimeInfo");
                if(Objects.equals(subscription, SUB_1)||realTimeInfo!=null){
                    Smc3RealTimeInfoProcessorMessageQueue.getInstance().put(new Smc3RealTimeInfoProcessorMessage(smcBridge,jsonObj));
                }

                Object state = jsonObj.get("state");
                //会控状态变化
                if (!Objects.isNull(state)) {
                    ConferenceState conferenceState = JSONObject.parseObject(jsonObj.toJSONString(), ConferenceState.class);
                    if(conferenceState!=null){
                        ConferenceState.StateDTO stateDTO = conferenceState.getState();
                        if(stateDTO!=null){
                            List<ConferenceState.StateDTO.ParticipantPollStatusListDTO> participantPollStatusList = stateDTO.getParticipantPollStatusList();
                            if(!CollectionUtils.isEmpty(participantPollStatusList)){
                                ConferenceState.StateDTO.ParticipantPollStatusListDTO participantPollStatusListDTO = participantPollStatusList.get(0);
                                String pollStatus = participantPollStatusListDTO.getPollStatus();
                                stateDTO.setChairmanPollStatus(pollStatus);
                            }
                        }
                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, conferenceState);
                    }

                }
                Object changeList = jsonObj.get("changeList");
                List<JSONObject> stateList=null;
                if (!Objects.isNull(changeList)) {
                    int type = (int) jsonObj.get("type");
                    if (type < _TYPE) {
                        stateList = JSONArray.parseArray(JSON.toJSONString(changeList), JSONObject.class);
                        List<String> strings =smc3ConferenceContext.getChangeParticipants();
                        for (JSONObject object : stateList) {
                            String participantId = (String)object.get("id");
                            if (type == 1) {
                                strings.add(participantId);
                            } else {
                                strings.remove(participantId);
                            }
                        }
                        if (strings.size() <= PAGE) {
                            smcBridge.getSmcConferencesInvoker().changeListGroup(conferenceId,  smc3ConferenceContext.getGroupId(), strings, 10, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                        } else {
                            smcBridge.getSmcConferencesInvoker().changeListGroup(conferenceId,  smc3ConferenceContext.getGroupId(), strings, 300, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
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
                            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.ATTENDEE_ADD, jsonObject);
                            break;
                        case 2:
                            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.ATTENDEE_DELETE, jsonObj);
                            break;
                        case 3:
                            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, jsonObj);
                            break;
                    }

                }
                logger.info(jsonObj.toJSONString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Smc3MessageInfoProcessorMessage(Smc3Bridge smcBridge, JSONObject updateItem, Smc3WebSocketProcessor smc3WebSocketProcessor) {
        super(smcBridge, updateItem, updateItem.getString("messageId"));
        this.smc3WebSocketProcessor = smc3WebSocketProcessor;
    }

}

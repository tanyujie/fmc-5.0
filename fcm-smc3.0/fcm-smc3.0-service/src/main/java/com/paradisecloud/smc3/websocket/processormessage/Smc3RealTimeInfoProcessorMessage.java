package com.paradisecloud.smc3.websocket.processormessage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.*;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3HistoryConferenceService;
import com.paradisecloud.smc3.websocket.client.SMC3WebsocketClient;
import com.paradisecloud.smc3.websocket.client.Smc3WebSocketProcessor;
import com.paradisecloud.smc3.websocket.client.Smc3WebsocketContext;
import com.paradisecloud.smc3.websocket.client.TopicMessage;
import com.sinhy.spring.BeanFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author nj
 * @date 2023/3/2 14:50
 */
public class Smc3RealTimeInfoProcessorMessage extends Smc3BusiProcessorMessage {

    /**
     * <pre>构造方法</pre>
     *
     * @param updateItem
     * @author sinhy
     * @since 2021-09-12 15:50
     */
    public Smc3RealTimeInfoProcessorMessage(Smc3Bridge smcBridge, JSONObject updateItem) {
        super(smcBridge, updateItem, updateItem.getString("messageId"));
    }

    @Override
    protected  void process0() {
        try {
            String subscription = (String)updateItem.get("subscription");
            String conferenceId = (String)updateItem.get("conferenceId");
            Object realTimeInfo = updateItem.get("realTimeInfo");
            if(Objects.equals(subscription, ConstAPI.SUB_1)){
                JSONArray conferenceStages =(JSONArray) updateItem.get("conferenceStages");
                if(!Objects.isNull(conferenceStages)){
                    logger.info("===================conferenceStages====================");
                    for (Object obj : conferenceStages) {
                        ConferenceStages o = JSONObject.parseObject(obj.toString(), ConferenceStages.class);
                        String stage = o.getStage();
                        conferenceId = o.conferenceId;
                        Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceId);
                        if(Objects.equals(stage,"CANCEL")){
                            logger.info("===========CANCEL========conferences====================>"+o.getConferenceId());
                           // SmcConferenceMessageQueue.getInstance().put( SmcConferenceMessage.builder().conferenceId(conferenceId).stage("CANCEL").build());
                            Iterator<String> iterator = Smc3WebSocketProcessor.getConferenceParitipantsRealTimeMap().keySet().iterator();
                            while (iterator.hasNext()){
                                String key = iterator.next();
                                if(key.contains(conferenceId)){
                                    iterator.remove();
                                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc3ConferenceContext, WebsocketMessageType.CONFERENCE_ENDED,updateItem);
                                }
                            }
                        }
                        if(Objects.equals(stage,"ONLINE")){
                            logger.info("===========ONLINE========conferences====================>"+o.getConferenceId());
                           // SmcConferenceMessageQueue.getInstance().put( SmcConferenceMessage.builder().conferenceId(conferenceId).stage("ONLINE").build());

                        }

                    }
                }
            }
            if(!Objects.isNull(realTimeInfo)){
                String participantId = (String)updateItem.get("participantId");
                String name = Smc3WebSocketProcessor.getSmcParticipantNames().get(participantId);
                if(Strings.isNotBlank(name)){
                    updateItem.put("name",name);
                }
                Smc3WebSocketProcessor.getRealTimeMap().put(participantId,updateItem);
                Map<String, Map<String, JSONObject>> conferenceParitipantsRealTimeMap = Smc3WebSocketProcessor.getConferenceParitipantsRealTimeMap();
                Map<String ,JSONObject> map= conferenceParitipantsRealTimeMap.get(conferenceId+subscription);
                if(Objects.isNull(map)){
                    HashMap<String, JSONObject> objectHashMap = new HashMap<>(1);
                    objectHashMap.put(participantId,updateItem);
                    conferenceParitipantsRealTimeMap.put(conferenceId+subscription,objectHashMap);
                }else {
                    map.put(participantId,updateItem);
                }


                Map<String ,JSONObject> map2= conferenceParitipantsRealTimeMap.get(conferenceId);
                if(Objects.isNull(map2)){
                    HashMap<String, JSONObject> objectHashMap = new HashMap<>();
                    objectHashMap.put(participantId,updateItem);
                    conferenceParitipantsRealTimeMap.put(conferenceId,objectHashMap);
                }else {
                    map2.put(participantId,updateItem);
                }




                Map<String, List<String>> realTimeParticipantsMap = Smc3WebSocketProcessor.getRealTimeParticipantsMap();
                if(realTimeParticipantsMap!=null){

                    List<String> value = realTimeParticipantsMap.get(subscription);
                    if(!CollectionUtils.isEmpty(value)){
                        boolean remove = value.remove(participantId);
                        if(remove){
                            Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceId);
                            AttendeeSmc3 attendeeSmc3 = smc3ConferenceContext.getAttendeeBySmc3Id(participantId);
                            IBusiMcuSmc3HistoryConferenceService teleHistoryConferenceService = BeanFactory.getBean(IBusiMcuSmc3HistoryConferenceService.class);
                            teleHistoryConferenceService.updateBusiHistoryParticipant(smc3ConferenceContext, attendeeSmc3, true);
                        }
                        if(value.size()==0){
                            cancelSub(subscription, conferenceId);
                            realTimeParticipantsMap.remove(subscription);
                        }
                    }

                }


            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }


    }

    private void cancelSub(String subscription, String conferenceId) {
        Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceId);
        Smc3Bridge smcBridge = smc3ConferenceContext.getSmc3Bridge();
        long groupId = subscription.indexOf(3);
        smcBridge.getSmcConferencesInvoker().cancelRealTimeInfo(conferenceId,groupId,smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        try {
            SMC3WebsocketClient smc3WebsocketClient = Smc3WebsocketContext.getSmcWebsocketClientMap().get(smcBridge.getBridgeIp());
            smc3WebsocketClient.sendMessage(TopicMessage.getUNSubscribeMessage(subscription));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







    @Data
    @NoArgsConstructor
    public static class ConferenceStages{
        private String stage;
        private String conferenceId;
    }



    public static JSONObject toDetail(JSONObject jsonObject, Integer protoType,String uri){
        if(Objects.isNull(jsonObject)||Objects.isNull(uri)||Objects.isNull(protoType)){
            return null;
        }
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("direction", "outgoing");
        String protoTypeStr = null;
        if (protoType != null) {
            if (protoType == 1) {
                protoTypeStr = "sip";
            } else if (protoType ==0) {
                protoTypeStr = "h323";
            }else {
                protoTypeStr = "sipAndH323";
            }
        }
        jsonObj.put("type", protoTypeStr);
        jsonObj.put("isEncrypted", false);
        jsonObj.put("remoteParty",uri);
        JSONObject upLink = new JSONObject();
        jsonObj.put("upLink", upLink);
        JSONObject upLinkAudio = new JSONObject();
        JSONArray upLinkVideos = new JSONArray();

        JSONObject downLink = new JSONObject();
        jsonObj.put("downLink", downLink);

        JSONObject downLinkAudio = new JSONObject();
        JSONArray downLinkVideos = new JSONArray();

        upLink.put("videos", upLinkVideos);
        upLink.put("audio", upLinkAudio);
        downLink.put("videos", downLinkVideos);
        downLink.put("audio", downLinkAudio);

        JSONObject realTimeInfo = (JSONObject)jsonObject.get("realTimeInfo");
        //下行信息
        JSONObject receiveRealTimeInfo = (JSONObject)realTimeInfo.get("receiveRealTimeInfo");

        JSONObject rttRealTimeInfo = (JSONObject)realTimeInfo.get("rttRealTimeInfo");
        JSONObject video = new JSONObject();
        video.put("role", Objects.equals(false,receiveRealTimeInfo.get("openAux"))?"main":"");
        int videoResolutionCode = (int)receiveRealTimeInfo.get("videoResolution");
        int videoProtocolCode = (int)receiveRealTimeInfo.get("videoProtocol");
        video.put("resolutionRatio", VideoResolutionEnum.getValueByCode(videoResolutionCode).name());
        video.put("frameRate", receiveRealTimeInfo.get("videoFrameRate"));
        video.put("videoCodec", VideoProtocolEnum.getValueByCode(videoProtocolCode).name());
        video.put("bandwidth", receiveRealTimeInfo.get("videoBandWidth"));
        video.put("packetLossPercentage", receiveRealTimeInfo.get("videoLoss"));
        video.put("jitter", receiveRealTimeInfo.get("videoJitter"));
        video.put("roundTripTime", rttRealTimeInfo.get("videoRtt"));
        downLinkVideos.add(video);


        int audioProtocolCode = (int)receiveRealTimeInfo.get("audioProtocol");

        downLinkAudio.put("codec", AudioProtocolEnum.getValueByCode(audioProtocolCode).name());
        downLinkAudio.put("bandwidth",  receiveRealTimeInfo.get("audioBandWidth"));
        downLinkAudio.put("packetLossPercentage", receiveRealTimeInfo.get("audioLoss"));
        downLinkAudio.put("codecBitRate", null);
        downLinkAudio.put("jitter", receiveRealTimeInfo.get("audioJitter"));
        downLinkAudio.put("roundTripTime", rttRealTimeInfo.get("audioRtt"));
        downLinkAudio.put("gainApplied", null);

        //上行
        JSONObject sendRealTimeInfo = (JSONObject)realTimeInfo.get("sendRealTimeInfo");
        if(sendRealTimeInfo==null){
            JSONObject videoSend = new JSONObject();
            videoSend.put("role", Objects.equals(false,sendRealTimeInfo.get("openAux"))?"main":"");
            int videoResolutionCode2 = (int)sendRealTimeInfo.get("videoResolution");
            int videoProtocolCode2 = (int)sendRealTimeInfo.get("videoProtocol");
            videoSend.put("resolutionRatio", VideoResolutionEnum.getValueByCode(videoResolutionCode2).name());
            videoSend.put("frameRate", sendRealTimeInfo.get("videoFrameRate"));
            videoSend.put("videoCodec", VideoProtocolEnum.getValueByCode(videoProtocolCode2).name());
            videoSend.put("bandwidth", sendRealTimeInfo.get("videoBandWidth"));
            videoSend.put("packetLossPercentage", sendRealTimeInfo.get("videoLoss"));
            videoSend.put("jitter", sendRealTimeInfo.get("videoJitter"));
            videoSend.put("roundTripTime", rttRealTimeInfo.get("videoRtt"));
            upLinkVideos.add(videoSend);

            int audioProtocolCode2 = (int)sendRealTimeInfo.get("audioProtocol");

            upLinkAudio.put("codec", AudioProtocolEnum.getValueByCode(audioProtocolCode2).name());
            upLinkAudio.put("bandwidth",  sendRealTimeInfo.get("audioBandWidth"));
            upLinkAudio.put("packetLossPercentage", sendRealTimeInfo.get("audioLoss"));
            upLinkAudio.put("codecBitRate", null);
            upLinkAudio.put("jitter", sendRealTimeInfo.get("audioJitter"));
            upLinkAudio.put("roundTripTime", rttRealTimeInfo.get("audioRtt"));
            upLinkAudio.put("gainApplied", null);
        }

        return jsonObj;
    }



}

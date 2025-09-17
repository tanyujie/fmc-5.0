package com.paradisecloud.smc.processormessage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import com.paradisecloud.fcm.smc.cache.modle.*;
import com.sinhy.spring.BeanFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2023/3/2 14:50
 */
public class RealTimeInfoProcessorMessage extends  BusiProcessorMessage{
  
    
    private Logger logger= LoggerFactory.getLogger(getClass());

    private static Map<String,String> smcParticipantNames=new ConcurrentHashMap<>();
    public static final String SUB_1 = "sub-1";
    private static Map<String ,JSONObject> realTimeMap=new ConcurrentHashMap<>();
    private static Map<String ,Object> realTimeGroupMap=new ConcurrentHashMap<>();

    private static Map<String ,Map<String ,JSONObject>> ConferenceParitipantsRealTimeMap=new ConcurrentHashMap<>();
    /**
     * <pre>构造方法</pre>
     *
     * @param fmeBridge
     * @param updateItem
     * @author sinhy
     * @since 2021-09-12 15:50
     */
    public RealTimeInfoProcessorMessage(SmcBridge smcBridge, JSONObject updateItem) {
        super(smcBridge, updateItem, updateItem.getString("messageId"));
    }

    @Override
    protected void process0() {
        String subscription = (String)updateItem.get("subscription");
        String conferenceId = (String)updateItem.get("conferenceId");
        Object realTimeInfo = updateItem.get("realTimeInfo");
        if(Objects.equals(subscription, SUB_1)){
            JSONArray conferenceStages =(JSONArray) updateItem.get("conferenceStages");
            if(!Objects.isNull(conferenceStages)){
                logger.info("===================conferenceStages====================");
                for (Object obj : conferenceStages) {
                    ConferenceStages o = JSONObject.parseObject(obj.toString(), ConferenceStages.class);
                    String stage = o.getStage();
                    conferenceId = o.conferenceId;
                    if(Objects.equals(stage,"CANCEL")){
                        logger.info("===========CANCEL========conferences====================>"+o.getConferenceId());
                        SmcConferenceMessageQueue.getInstance().put( SmcConferenceMessage.builder().conferenceId(conferenceId).stage("CANCEL").build());
                        Iterator<String> iterator = getConferenceParitipantsRealTimeMap().keySet().iterator();
                        while (iterator.hasNext()){
                            String key = iterator.next();
                            if(key.contains(conferenceId)){
                                iterator.remove();
                            }
                        }
                    }
                    if(Objects.equals(stage,"ONLINE")){
                        logger.info("===========ONLINE========conferences====================>"+o.getConferenceId());
                        SmcConferenceMessageQueue.getInstance().put( SmcConferenceMessage.builder().conferenceId(conferenceId).stage("ONLINE").build());
                        Map<String, SmcBridge> conferenceBridge = SmcBridgeCache.getInstance().getConferenceBridge();
                        if(conferenceBridge.get(conferenceId)==null){
                            conferenceBridge.put(conferenceId,SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null));
                        }
                    }
                    SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CONFERENCE_STAGE,updateItem);

                }
            }
        }
        if(!Objects.isNull(realTimeInfo)){
            String participantId = (String)updateItem.get("participantId");
            String name = smcParticipantNames.get(participantId);
            if(Strings.isNotBlank(name)){
                updateItem.put("name",name);
            }
            realTimeMap.put(participantId,updateItem);
            Map<String ,JSONObject> map= ConferenceParitipantsRealTimeMap.get(conferenceId+subscription);
            if(Objects.isNull(map)){
                HashMap<String, JSONObject> objectHashMap = new HashMap<>();
                objectHashMap.put(participantId,updateItem);
                ConferenceParitipantsRealTimeMap.put(conferenceId+subscription,objectHashMap);
            }else {
                map.put(participantId,updateItem);
            }
       //     SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.REALTIME_INFO,updateItem);

        }


    }

    private void updateBusiHistoryParticipantTerminal(String conferenceId, String participantId) {
        try {
            BusiHistoryParticipantMapper busiHistoryParticipantMapper = BeanFactory.getBean(BusiHistoryParticipantMapper.class);
            BusiHistoryParticipant busiHistoryParticipant = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(participantId);
            if(busiHistoryParticipant!=null){
                SmcConferenceContext smcConferenceContext = SmcConferenceContextCache.getInstance().getSmcConferenceContextMap().get(conferenceId);
                if(smcConferenceContext!=null){
                    List<ParticipantRspDto> participants = smcConferenceContext.getParticipants();
                    Optional<ParticipantRspDto> optional = participants.stream().filter(p -> Objects.equals(p.getId(), participantId)).findFirst();
                    if(optional.isPresent()){
                        JSONObject jsonObject = toDetail(updateItem, optional.get());
                        busiHistoryParticipant.setMediaInfo(jsonObject);
                        busiHistoryParticipant.setUpdateTime(new Date());
                        busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
                        IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService = BeanFactory.getBean(IBusiHistoryParticipantTerminalService.class);
                        busiHistoryParticipantTerminalService.updateBusiHistoryParticipantTerminalByBusiHistoryParticipant(busiHistoryParticipant);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, JSONObject> getRealTimeMap() {
        return realTimeMap;
    }

    public static Map<String, Map<String, JSONObject>> getConferenceParitipantsRealTimeMap() {
        return ConferenceParitipantsRealTimeMap;
    }

    public static Map<String, Object> getRealTimeGroupMap() {
        return realTimeGroupMap;
    }

    @Data
    @NoArgsConstructor
    public static class ConferenceStages{
        private String stage;
        private String conferenceId;
    }

    public static Map<String, String> getSmcParticipantNames() {
        return smcParticipantNames;
    }

    public JSONObject toDetail(JSONObject jsonObject,  ParticipantRspDto contentDTO){
        if(Objects.isNull(jsonObject)||Objects.isNull(contentDTO)){
            return null;
        }
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("direction", "outgoing");
        Integer protoType = contentDTO.getIpProtocolType();
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
        jsonObj.put("remoteParty", contentDTO.getUri());
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
            video.put("role", Objects.equals(false,sendRealTimeInfo.get("openAux"))?"main":"");
            int videoResolutionCode2 = (int)sendRealTimeInfo.get("videoResolution");
            int videoProtocolCode2 = (int)sendRealTimeInfo.get("videoProtocol");
            video.put("resolutionRatio", VideoResolutionEnum.getValueByCode(videoResolutionCode2).name());
            video.put("frameRate", sendRealTimeInfo.get("videoFrameRate"));
            video.put("videoCodec", VideoProtocolEnum.getValueByCode(videoProtocolCode2).name());
            video.put("bandwidth", sendRealTimeInfo.get("videoBandWidth"));
            video.put("packetLossPercentage", sendRealTimeInfo.get("videoLoss"));
            video.put("jitter", sendRealTimeInfo.get("videoJitter"));
            video.put("roundTripTime", rttRealTimeInfo.get("videoRtt"));
            upLinkVideos.add(videoSend);

            int audioProtocolCode2 = (int)sendRealTimeInfo.get("audioProtocol");

            upLinkAudio.put("codec",AudioProtocolEnum.getValueByCode(audioProtocolCode2).name());
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

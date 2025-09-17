package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.processormessage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.sinhy.spring.BeanFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author nj
 * @date 2023/3/2 14:50
 */
public class HwcloudRealTimeInfoProcessorMessage extends HwcloudBusiProcessorMessage {
  
    
    private Logger logger= LoggerFactory.getLogger(getClass());


    /**
     * <pre>构造方法</pre>
     *
     * @param updateItem
     * @author sinhy
     * @since 2021-09-12 15:50
     */
    public HwcloudRealTimeInfoProcessorMessage(HwcloudBridge hwcloudBridge, JSONObject updateItem) {
        super(hwcloudBridge, updateItem, updateItem.getString("messageId"));
    }

    @Override
    protected  void process0() {



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
//        jsonObj.put("direction", "outgoing");
//        String protoTypeStr = null;
//        if (protoType != null) {
//            if (protoType == 1) {
//                protoTypeStr = "sip";
//            } else if (protoType ==0) {
//                protoTypeStr = "h323";
//            }else {
//                protoTypeStr = "sipAndH323";
//            }
//        }
//        jsonObj.put("type", protoTypeStr);
//        jsonObj.put("isEncrypted", false);
//        jsonObj.put("remoteParty",uri);
//        JSONObject upLink = new JSONObject();
//        jsonObj.put("upLink", upLink);
//        JSONObject upLinkAudio = new JSONObject();
//        JSONArray upLinkVideos = new JSONArray();
//
//        JSONObject downLink = new JSONObject();
//        jsonObj.put("downLink", downLink);
//
//        JSONObject downLinkAudio = new JSONObject();
//        JSONArray downLinkVideos = new JSONArray();
//
//        upLink.put("videos", upLinkVideos);
//        upLink.put("audio", upLinkAudio);
//        downLink.put("videos", downLinkVideos);
//        downLink.put("audio", downLinkAudio);
//
//        JSONObject realTimeInfo = (JSONObject)jsonObject.get("realTimeInfo");
//        //下行信息
//        JSONObject receiveRealTimeInfo = (JSONObject)realTimeInfo.get("receiveRealTimeInfo");
//
//        JSONObject rttRealTimeInfo = (JSONObject)realTimeInfo.get("rttRealTimeInfo");
//        JSONObject video = new JSONObject();
//        video.put("role", Objects.equals(false,receiveRealTimeInfo.get("openAux"))?"main":"");
//        int videoResolutionCode = (int)receiveRealTimeInfo.get("videoResolution");
//        int videoProtocolCode = (int)receiveRealTimeInfo.get("videoProtocol");
//        video.put("resolutionRatio", VideoResolutionEnum.getValueByCode(videoResolutionCode).name());
//        video.put("frameRate", receiveRealTimeInfo.get("videoFrameRate"));
//        video.put("videoCodec", VideoProtocolEnum.getValueByCode(videoProtocolCode).name());
//        video.put("bandwidth", receiveRealTimeInfo.get("videoBandWidth"));
//        video.put("packetLossPercentage", receiveRealTimeInfo.get("videoLoss"));
//        video.put("jitter", receiveRealTimeInfo.get("videoJitter"));
//        video.put("roundTripTime", rttRealTimeInfo.get("videoRtt"));
//        downLinkVideos.add(video);
//
//
//        int audioProtocolCode = (int)receiveRealTimeInfo.get("audioProtocol");
//
//        downLinkAudio.put("codec", AudioProtocolEnum.getValueByCode(audioProtocolCode).name());
//        downLinkAudio.put("bandwidth",  receiveRealTimeInfo.get("audioBandWidth"));
//        downLinkAudio.put("packetLossPercentage", receiveRealTimeInfo.get("audioLoss"));
//        downLinkAudio.put("codecBitRate", null);
//        downLinkAudio.put("jitter", receiveRealTimeInfo.get("audioJitter"));
//        downLinkAudio.put("roundTripTime", rttRealTimeInfo.get("audioRtt"));
//        downLinkAudio.put("gainApplied", null);
//
//        //上行
//        JSONObject sendRealTimeInfo = (JSONObject)realTimeInfo.get("sendRealTimeInfo");
//        if(sendRealTimeInfo==null){
//            JSONObject videoSend = new JSONObject();
//            video.put("role", Objects.equals(false,sendRealTimeInfo.get("openAux"))?"main":"");
//            int videoResolutionCode2 = (int)sendRealTimeInfo.get("videoResolution");
//            int videoProtocolCode2 = (int)sendRealTimeInfo.get("videoProtocol");
//            video.put("resolutionRatio", VideoResolutionEnum.getValueByCode(videoResolutionCode2).name());
//            video.put("frameRate", sendRealTimeInfo.get("videoFrameRate"));
//            video.put("videoCodec", VideoProtocolEnum.getValueByCode(videoProtocolCode2).name());
//            video.put("bandwidth", sendRealTimeInfo.get("videoBandWidth"));
//            video.put("packetLossPercentage", sendRealTimeInfo.get("videoLoss"));
//            video.put("jitter", sendRealTimeInfo.get("videoJitter"));
//            video.put("roundTripTime", rttRealTimeInfo.get("videoRtt"));
//            upLinkVideos.add(videoSend);
//
//            int audioProtocolCode2 = (int)sendRealTimeInfo.get("audioProtocol");
//
//            upLinkAudio.put("codec", AudioProtocolEnum.getValueByCode(audioProtocolCode2).name());
//            upLinkAudio.put("bandwidth",  sendRealTimeInfo.get("audioBandWidth"));
//            upLinkAudio.put("packetLossPercentage", sendRealTimeInfo.get("audioLoss"));
//            upLinkAudio.put("codecBitRate", null);
//            upLinkAudio.put("jitter", sendRealTimeInfo.get("audioJitter"));
//            upLinkAudio.put("roundTripTime", rttRealTimeInfo.get("audioRtt"));
//            upLinkAudio.put("gainApplied", null);
//        }
//
        return jsonObj;
    }



}

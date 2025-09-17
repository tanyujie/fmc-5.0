package com.paradisecloud.fcm.huaweicloud.huaweicloud.event;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.event.model.NetConditionNotifyParticipant;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2024/3/7 14:01
 */
public class NetConditionNotifyParticipantCache extends JavaCache<String, NetConditionNotifyParticipant> {

    private static final NetConditionNotifyParticipantCache INSTANCE = new NetConditionNotifyParticipantCache();
    private Map<String, List<NetConditionNotifyParticipant>> participantMap = new ConcurrentHashMap<>();

    public static NetConditionNotifyParticipantCache getInstance() {
        return INSTANCE;
    }

    public static JSONObject toDetail(NetConditionNotifyParticipant netConditionNotifyParticipant) {
        if (Objects.isNull(netConditionNotifyParticipant)) {
            return null;
        }
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("direction", "outgoing");
        String protoTypeStr = "sipAndH323";

        jsonObj.put("type", protoTypeStr);
        jsonObj.put("isEncrypted", false);
        jsonObj.put("remoteParty", "");
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


        //下行信息
        {
            JSONObject video = new JSONObject();
            String videoFluxIn = netConditionNotifyParticipant.getVideoFluxIn();
            int videoFluxInInt = Integer.parseInt(videoFluxIn);
            video.put("role", videoFluxInInt > 0 ? "main" : "");


            video.put("resolutionRatio", null);
            video.put("frameRate", null);
            video.put("videoCodec", netConditionNotifyParticipant.getVideoCodecType());
            video.put("bandwidth", videoFluxIn);
            video.put("packetLossPercentage", netConditionNotifyParticipant.getVideoOutLossPacketRate());
            video.put("jitter", netConditionNotifyParticipant.getJitter());
            video.put("roundTripTime", netConditionNotifyParticipant.getDelay());
            downLinkVideos.add(video);


            downLinkAudio.put("codec", netConditionNotifyParticipant.getAudioCodecType());
            downLinkAudio.put("bandwidth", netConditionNotifyParticipant.getFluxIn());
            downLinkAudio.put("packetLossPercentage", netConditionNotifyParticipant.getAudioOutLossPacketRate());
            downLinkAudio.put("codecBitRate", null);
            downLinkAudio.put("jitter", netConditionNotifyParticipant.getJitter());
            downLinkAudio.put("roundTripTime", netConditionNotifyParticipant.getDelay());
            downLinkAudio.put("gainApplied", null);
        }


        //上行
        {
            JSONObject videoSend = new JSONObject();

            String videoFluxOut = netConditionNotifyParticipant.getVideoFluxOut();
            int videoFluxOutInt = Integer.parseInt(videoFluxOut);
            videoSend.put("role", videoFluxOutInt > 0 ? "main" : "");


            videoSend.put("resolutionRatio", null);
            videoSend.put("frameRate", null);
            videoSend.put("videoCodec", netConditionNotifyParticipant.getVideoCodecType());
            videoSend.put("bandwidth", netConditionNotifyParticipant.getVideoFluxOut());
            videoSend.put("packetLossPercentage", netConditionNotifyParticipant.getVideoInLossPacketRate());
            videoSend.put("jitter", netConditionNotifyParticipant.getJitter());
            videoSend.put("roundTripTime", netConditionNotifyParticipant.getDelay());
            upLinkVideos.add(videoSend);


            upLinkAudio.put("codec", netConditionNotifyParticipant.getAudioCodecType());
            upLinkAudio.put("bandwidth", netConditionNotifyParticipant.getFluxOut());
            upLinkAudio.put("packetLossPercentage", netConditionNotifyParticipant.getLostPacketRate());
            upLinkAudio.put("codecBitRate", null);
            upLinkAudio.put("jitter", netConditionNotifyParticipant.getJitter());
            upLinkAudio.put("roundTripTime", netConditionNotifyParticipant.getDelay());
            upLinkAudio.put("gainApplied", null);
        }

        return jsonObj;
    }

    public Map<String, List<NetConditionNotifyParticipant>> getParticipantMap() {
        return participantMap;
    }

    public void setParticipantMap(Map<String, List<NetConditionNotifyParticipant>> participantMap) {
        this.participantMap = participantMap;
    }

    public synchronized void remove(String confId) {
        participantMap.remove(confId);
    }

    public synchronized void put(String confId, List<NetConditionNotifyParticipant> list) {
        participantMap.put(confId, list);
    }
}

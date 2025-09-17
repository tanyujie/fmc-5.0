package com.paradisecloud.smc.service.core;

import com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicInfoReq;
import com.paradisecloud.com.fcm.smc.modle.response.VideoSourceRep;
import com.paradisecloud.fcm.smc.cache.modle.ConferenceState;
import com.paradisecloud.fcm.smc.cache.modle.SmcConferenceContextCache;
import com.paradisecloud.fcm.smc.cache.modle.SmcWebSocketMessagePusher;
import com.paradisecloud.fcm.smc.cache.modle.SmcWebsocketMessageType;
import com.paradisecloud.smc.service.SmcConferenceService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author nj
 * @date 2023/5/19 10:27
 */
public  class ChairManPollingThread extends Thread {
    public volatile boolean flag = true;
    private String conferenceId;
    private String chairmanId;
    private SmcConferenceService smcConferenceService;

    public ChairManPollingThread(String conferenceId, String chairmanId, SmcConferenceService smcConferenceService) {
        this.conferenceId = conferenceId;
        this.chairmanId = chairmanId;
        this.smcConferenceService = smcConferenceService;
    }

    @Override
    public void run() {
        while (flag) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            reInterrupt();
            Map<String, ConferenceState> smcConferenceStateMap = SmcConferenceContextCache.getInstance().getSmcConferenceStateMap();
            ConferenceState conferenceState = smcConferenceStateMap.get(conferenceId);
            if (conferenceState == null) {
                SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.POLLING_LIST, new ArrayList<>());
                break;
            }
            if (conferenceState.getState() == null) {
                SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.POLLING_LIST, new ArrayList<>());
                break;
            }

            List<ConferenceState.StateDTO.ParticipantPollStatusListDTO> participantPollStatusList = conferenceState.getState().getParticipantPollStatusList();
            if (CollectionUtils.isEmpty(participantPollStatusList)) {
                SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.POLLING_LIST, new ArrayList<>());
                break;
            }
            ConferenceState.StateDTO.ParticipantPollStatusListDTO participantPollStatusListDTO = participantPollStatusList.get(0);
            if (participantPollStatusListDTO.getChairmanPoll() && !Objects.equals(participantPollStatusListDTO.getPollStatus(), PollOperateTypeDto.START.name())) {
                if (Objects.equals(participantPollStatusListDTO.getPollStatus(), PollOperateTypeDto.CANCEL.name())) {
                    SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.POLLING_LIST, new ArrayList<>());
                }
                break;
            }
            List<String> chairmanIdList = new ArrayList<>();
            chairmanIdList.add(chairmanId);
            List<VideoSourceRep> chairmanIdvideoSourceReps = smcConferenceService.conferencesVideoSource(conferenceId, chairmanIdList);
            if (!CollectionUtils.isEmpty(chairmanIdvideoSourceReps)) {
                for (VideoSourceRep chairmanIdvideoSourceRep : chairmanIdvideoSourceReps) {
                    MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = chairmanIdvideoSourceRep.getMultiPicInfo();
                    if (multiPicInfo != null) {
                        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfo.getSubPicList();
                        if (!CollectionUtils.isEmpty(subPicList)) {
                            List<String> participants = new ArrayList<>();
                            for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                                String participantId = subPicListDTO.getParticipantId();
                                participants.add(participantId);
                            }
                            SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.POLLING_LIST, participants);
                        }
                    }
                }
            }

        }
    }

    private void reInterrupt() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

}

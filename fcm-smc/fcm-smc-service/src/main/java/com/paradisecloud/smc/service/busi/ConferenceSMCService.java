package com.paradisecloud.smc.service.busi;

import com.paradisecloud.com.fcm.smc.modle.DetailConference;
import com.paradisecloud.com.fcm.smc.modle.ParticipantStatus;
import com.paradisecloud.com.fcm.smc.modle.ParticipantStatusDto;
import com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto;
import com.paradisecloud.com.fcm.smc.modle.mix.ConferenceControllerRequest;
import com.paradisecloud.com.fcm.smc.modle.request.ChairmanPollOperateReq;
import com.paradisecloud.com.fcm.smc.modle.request.ConferenceStatusRequest;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicInfoReq;
import com.paradisecloud.com.fcm.smc.modle.response.VideoSourceRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.smc.cache.modle.ConferenceState;
import com.paradisecloud.fcm.smc.cache.modle.SmcConferenceContextCache;
import com.paradisecloud.fcm.smc.cache.modle.SmcWebSocketMessagePusher;
import com.paradisecloud.fcm.smc.cache.modle.SmcWebsocketMessageType;
import com.paradisecloud.smc.dao.model.BusiSmcConferenceState;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcMulitpicMapper;
import com.paradisecloud.smc.service.*;
import com.paradisecloud.smc.service.core.ChairManPollingThread;
import com.sinhy.spring.BeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author nj
 * @date 2022/10/20 11:07
 */
@Service
public class ConferenceSMCService {


    @Resource
    private SmcConferenceService smcConferenceService;
    @Resource
    private SmcParticipantsService smcParticipantsService;
    @Resource
    private IBusiSmcMulitpicService busiSmcMulitpicService;
    @Resource
    private IBusiSmcConferenceStateService conferenceStateService;



    public void choose(String conferenceId1, String participantId) {
        //查看会议状态
        DetailConference detailConference = smcConferenceService.getDetailConferenceInfoById(conferenceId1);
        if (detailConference == null) {
            throw new CustomException("会议不存在");
        }
        String chairmanId = detailConference.getConferenceState().getChairmanId();
        if (StringUtils.isBlank(chairmanId)) {
            throw new CustomException("请先设置主席");
        }


        try {
            ChairmanPollOperateReq chairmanPollOperateReq = new ChairmanPollOperateReq();
            chairmanPollOperateReq.setConferenceId(conferenceId1);
            chairmanPollOperateReq.setPollStatus(PollOperateTypeDto.CANCEL);
            smcConferenceService.chairmanParticipantMultiPicPollOperate(chairmanPollOperateReq);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> strings = new ArrayList<>();
        strings.add(participantId);
        List<VideoSourceRep> videoSourceReps = smcConferenceService.conferencesVideoSource(conferenceId1, strings);
        SmcConferenceContextCache.getInstance().getVideoSourceRepMap().put(participantId, videoSourceReps);

        //String lastChooseId = SmcConferenceContextCache.getInstance().getChooseParticipantMap().get(conferenceId1);
        //锁定视频源
        ParticipantStatus participantStatus = new ParticipantStatus();
        participantStatus.setVideoSwitchAttribute("CUSTOMIZED");
        smcParticipantsService.changeParticipantStatusOnly(conferenceId1, chairmanId, participantStatus);
        //锁定分会场


        String broadcastId = detailConference.getConferenceState().getBroadcastId();

        if (StringUtils.isNotBlank(broadcastId)) {
            if (!Objects.equals(broadcastId, chairmanId)) {
                muteTrue(conferenceId1, broadcastId);
                broadcaster(conferenceId1, chairmanId);
            }
        } else {
            broadcaster(conferenceId1, chairmanId);
        }

        ConferenceControllerRequest callTheRollRequest = new ConferenceControllerRequest();
        callTheRollRequest.setConferenceId(conferenceId1);
        callTheRollRequest.setParticipantId(participantId);
        smcConferenceService.statusControlchoose(chairmanId, callTheRollRequest);

        String participantIdLast = SmcConferenceContextCache.getInstance().getChooseParticipantMap().get(callTheRollRequest.getConferenceId());
        muteTrue(conferenceId1, participantIdLast);

        SmcConferenceContextCache.getInstance().getChooseParticipantMap().put(callTheRollRequest.getConferenceId(), callTheRollRequest.getParticipantId());

        BusiSmcConferenceState busiSmcConferenceState = new BusiSmcConferenceState();
        busiSmcConferenceState.setConferenceId(callTheRollRequest.getConferenceId());
        List<BusiSmcConferenceState> busiSmcConferenceStates = conferenceStateService.selectBusiSmcConferenceStateList(busiSmcConferenceState);
        if (CollectionUtils.isEmpty(busiSmcConferenceStates)) {
            busiSmcConferenceState.setCreateTime(new Date());
            busiSmcConferenceState.setChooseid(callTheRollRequest.getParticipantId());
            conferenceStateService.insertBusiSmcConferenceState(busiSmcConferenceState);
        } else {
            busiSmcConferenceState.setChooseid(callTheRollRequest.getParticipantId());
            conferenceStateService.updateBusiSmcConferenceState(busiSmcConferenceState);
        }


        participantStatus.setVideoSwitchAttribute("AUTO");
        smcParticipantsService.changeParticipantStatusOnly(conferenceId1, chairmanId, participantStatus);


        List<String> chooseList = new ArrayList<>();
        chooseList.add(participantId);
        SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId1, SmcWebsocketMessageType.CHOOSE_LIST, chooseList);

        String chooseId = SmcConferenceContextCache.getInstance().getChooseParticipantMap().get(conferenceId1);

        ConferenceState conferenceState = SmcConferenceContextCache.getInstance().getSmcConferenceStateMap().get(conferenceId1);
        if (conferenceState != null) {
            conferenceState.getState().setChooseId(chooseId);
            SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId1, SmcWebsocketMessageType.CONFERENCE_CHANGED, conferenceState);
        }

    }


    public void cancelChoose(String conferenceId, String participantId, Boolean polling) {

        try {
            MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO = new MultiPicInfoReq.MultiPicInfoDTO();
            multiPicInfoDTO.setMode(1);
            multiPicInfoDTO.setPicNum(1);
            List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = new ArrayList<>();
            MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO();
            subPicListDTO.setParticipantId("00000000-0000-0000-0000-000000000000");
            subPicListDTO.setStreamNumber(0);
            subPicList.add(subPicListDTO);
            multiPicInfoDTO.setSubPicList(subPicList);
            smcConferenceService.conferencesControlChoose(conferenceId, participantId, multiPicInfoDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SmcConferenceContextCache.getInstance().getChooseParticipantMap().remove(conferenceId);
        BusiSmcConferenceState busiSmcConferenceState = new BusiSmcConferenceState();
        busiSmcConferenceState.setConferenceId(conferenceId);
        busiSmcConferenceState.setChooseid(participantId);
        List<BusiSmcConferenceState> busiSmcConferenceStates = conferenceStateService.selectBusiSmcConferenceStateList(busiSmcConferenceState);
        if (!CollectionUtils.isEmpty(busiSmcConferenceStates)) {
            conferenceStateService.deleteBusiSmcConferenceStateById(busiSmcConferenceStates.get(0).getId());
        }
        SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CHOOSE_LIST, new ArrayList<>());
        ConferenceState conferenceState = SmcConferenceContextCache.getInstance().getSmcConferenceStateMap().get(conferenceId);
        if (conferenceState != null) {
            conferenceState.getState().setChooseId("");
            SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CONFERENCE_CHANGED, conferenceState);
        }

    }


    public void callTheRoll(String conferenceId, String participantId) {

        Map<String, ConferenceState> smcConferenceStateMap = SmcConferenceContextCache.getInstance().getSmcConferenceStateMap();
        ConferenceState conferenceState = smcConferenceStateMap.get(conferenceId);
        if (conferenceState == null) {
            throw new CustomException("会议状态未获取");
        }
        String chairmanId = conferenceState.getState().getChairmanId();
        if (StringUtils.isBlank(chairmanId)) {
            throw new CustomException("请先设置主席");
        }
        try {
            ChairmanPollOperateReq chairmanPollOperateReq = new ChairmanPollOperateReq();
            chairmanPollOperateReq.setConferenceId(conferenceId);
            chairmanPollOperateReq.setPollStatus(PollOperateTypeDto.CANCEL);
            smcConferenceService.chairmanParticipantMultiPicPollOperate(chairmanPollOperateReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String Last = conferenceState.getState().getBroadcastId();
        if (!Objects.equals(Last, participantId)) {
            muteTrue(conferenceId, Last);
        }

        ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
        conferenceStatusRequest.setSpokesman(participantId);
        //当前会场未打开视频源，不具备广播和点名功能
        smcConferenceService.statusControl(conferenceId, conferenceStatusRequest);

        ConferenceControllerRequest callTheRollRequest = new ConferenceControllerRequest();
        callTheRollRequest.setConferenceId(conferenceId);
        callTheRollRequest.setParticipantId(chairmanId);
        smcConferenceService.statusControlchoose(participantId, callTheRollRequest);
        SmcConferenceContextCache.getInstance().getChooseParticipantMap().remove(conferenceId);
        deleteChooseState(conferenceId);
        SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.MESSAGE_TIP, "点名");
        SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CHOOSE_LIST, new ArrayList<>());
    }


    /**
     * 取消点名
     */
    public void cancelCallTheRoll(String conferenceId, String participantId, Boolean polling) {
        ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
        conferenceStatusRequest.setSpokesman("");
        smcConferenceService.statusControl(conferenceId, conferenceStatusRequest);
        Map<String, ConferenceState> smcConferenceStateMap = SmcConferenceContextCache.getInstance().getSmcConferenceStateMap();
        ConferenceState conferenceState = smcConferenceStateMap.get(conferenceId);
        if (conferenceState == null) {
            throw new CustomException("会议状态未获取");
        }
        String chairmanId = conferenceState.getState().getChairmanId();
        if (!Objects.equals(chairmanId, participantId)) {
            muteTrue(conferenceId, participantId);
        }
        broadcaster(conferenceId, chairmanId);


        SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CHOOSE_LIST, new ArrayList<>());


    }


    private void broadcaster(String conferenceId, String participantId) {
        if (StringUtils.isBlank(participantId)) {
            return;
        }
        ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
        conferenceStatusRequest.setBroadcaster(participantId);
        smcConferenceService.statusControl(conferenceId, conferenceStatusRequest);
    }


    private void cancelSpokesman(String conferenceId1) {
        ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
        conferenceStatusRequest.setSpokesman("");
        smcConferenceService.statusControl(conferenceId1, conferenceStatusRequest);
    }

    private void muliltPic(String conferenceId1, String chairmanId, boolean broad) {
        List<String> chairmanIdList = new ArrayList<>();
        chairmanIdList.add(chairmanId);
        List<VideoSourceRep> chairmanIdvideoSourceReps = smcConferenceService.conferencesVideoSource(conferenceId1, chairmanIdList);
        SmcConferenceContextCache.getInstance().getChairmanIdVideoSourceRepMap().put(chairmanId, chairmanIdvideoSourceReps);

        //muteTrue(conferenceId1, chairmanId);

        MultiPicInfoReq multiPicInfoReq = new MultiPicInfoReq();
        multiPicInfoReq.setBroadcast(broad);
        multiPicInfoReq.setConferenceId(conferenceId1);

        MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO = new MultiPicInfoReq.MultiPicInfoDTO();
        ArrayList<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> objects = new ArrayList<>();
        MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO();
        subPicListDTO.setParticipantId(chairmanId);
        subPicListDTO.setStreamNumber(0);
        objects.add(subPicListDTO);
        multiPicInfoDTO.setMode(1);
        multiPicInfoDTO.setPicNum(1);
        multiPicInfoDTO.setSubPicList(objects);
        multiPicInfoReq.setMultiPicInfo(multiPicInfoDTO);

        //其他人看主会场
        smcConferenceService.createMulitiPic(multiPicInfoReq);
    }

    private void muteTrue(String conferenceId, String broadcastId) {
        List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(broadcastId);
        participantStatusDto.setIsMute(true);
        participantStatusList.add(participantStatusDto);
        smcParticipantsService.PATCHParticipantsOnly(conferenceId, participantStatusList);
    }

    public void endConference(String conferenceId) {
        smcConferenceService.endConference(conferenceId);
        SmcConferenceContextCache.getInstance().cleanCacheMap(conferenceId);
        busiSmcMulitpicService.deleteBusiSmcMulitpicByConferenceId(conferenceId);
        deleteChooseState(conferenceId);
        try {
            BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
            BusiHistoryConference busiHistoryConference = new BusiHistoryConference();
            busiHistoryConference.setCallLegProfileId(conferenceId);
            List<BusiHistoryConference> busiHistoryConferences = busiHistoryConferenceMapper.selectBusiHistoryConferenceList(busiHistoryConference);
            if(!CollectionUtils.isEmpty(busiHistoryConferences)){
                BusiHistoryConference busiHistoryConference1 = busiHistoryConferences.get(0);
                busiHistoryConference1.setConferenceEndTime(new Date());
                busiHistoryConference1.setDuration((int) ((System.currentTimeMillis() - busiHistoryConference1.getConferenceStartTime().getTime()) / 1000));
                busiHistoryConference1.setEndReasonsType(EndReasonsType.ADMINISTRATOR_HANGS_UP);
                busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteChooseState(String conferenceId) {
        BusiSmcConferenceState busiSmcConferenceState = new BusiSmcConferenceState();
        busiSmcConferenceState.setConferenceId(conferenceId);
        List<BusiSmcConferenceState> busiSmcConferenceStates = conferenceStateService.selectBusiSmcConferenceStateList(busiSmcConferenceState);
        if (!CollectionUtils.isEmpty(busiSmcConferenceStates)) {
            conferenceStateService.deleteBusiSmcConferenceStateById(busiSmcConferenceStates.get(0).getId());
        }
    }


}

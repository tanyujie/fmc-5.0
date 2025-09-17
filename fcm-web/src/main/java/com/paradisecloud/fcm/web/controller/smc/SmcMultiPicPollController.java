package com.paradisecloud.fcm.web.controller.smc;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.ChooseMultiPicInfo;
import com.paradisecloud.com.fcm.smc.modle.DetailConference;
import com.paradisecloud.com.fcm.smc.modle.MasterPollTemplate;
import com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto;
import com.paradisecloud.com.fcm.smc.modle.request.*;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.smc.cache.modle.ConferenceState;
import com.paradisecloud.fcm.smc.cache.modle.SmcConferenceContextCache;
import com.paradisecloud.fcm.smc.cache.modle.SmcWebSocketMessagePusher;
import com.paradisecloud.fcm.smc.cache.modle.SmcWebsocketMessageType;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiParaticipantsService;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiTeleConferenceService;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiTeleParticipantService;
import com.paradisecloud.smc.service.*;
import com.paradisecloud.smc.service.busi.ConferenceSMCService;
import com.paradisecloud.smc.service.core.ChairManPollingThread;
import com.sinhy.spring.BeanFactory;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author nj
 * @date 2022/8/26 11:17
 */
@RestController
@RequestMapping("/smc/mulitiPicPoll")
public class SmcMultiPicPollController {

    public static final String CHOOSE_ID = "{\"chooseId\":\"\"}";
    @Resource
    private SmcConferenceService smcConferenceService;
    @Resource
    private IBusiSmcTemplateConferenceService busiSmcTemplateConferenceService;

    @Resource
    private SmcTemplateTerminalService smcTemplateTerminalService;

    @Resource
    private IBusiTeleConferenceService teleConferenceService;
    @Resource
    private IBusiParaticipantsService iBusiParaticipantsService;


    @Resource
    private IBusiTeleParticipantService teleParticipantService;

    @Resource
    private ConferenceSMCService conferenceSMCService;
    @Resource
    private IBusiSmcConferenceStateService conferenceStateService;


    @Resource
    private SmcParticipantsService smcParticipantsService;

    /**
     * 主席多画面轮询
     *
     * @param multiPicInfoReq
     */
    @PostMapping("/create")
    public void create(@RequestBody MultiPicInfoReq multiPicInfoReq) {
        smcConferenceService.createMulitiPicPoll(multiPicInfoReq);
    }

    /**
     * 设置多画面
     *
     * @param multiPicInfoReq
     */
    @PostMapping("/create/multiPic")
    public void createMultiPic(@RequestBody MultiPicInfoReq multiPicInfoReq) {
        smcConferenceService.createMulitiPicNObroad(multiPicInfoReq);
    }


    /**
     * 选看多画面
     *
     * @param conferenceId
     */
    @PatchMapping("/chooseManly/multiPic/{conferenceId}/{participantId}")
    public void chooseMultiPicManly(@PathVariable String conferenceId, @PathVariable String participantId, @RequestBody MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO) {
        smcConferenceService.conferencesControlChoose(conferenceId, participantId, multiPicInfoDTO);
        Map<String, ConferenceState> smcConferenceStateMap = SmcConferenceContextCache.getInstance().getSmcConferenceStateMap();
        ConferenceState conferenceState = smcConferenceStateMap.get(conferenceId);
        if (conferenceState != null) {
            String chairmanId = conferenceState.getState().getChairmanId();
            if (Objects.equals(chairmanId, participantId)) {
                List<String> chooseParticipants = new ArrayList<>();
                List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfoDTO.getSubPicList();
                if (!CollectionUtils.isEmpty(subPicList)) {
                    for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                        String chooseId = subPicListDTO.getParticipantId();
                        chooseParticipants.add(chooseId);
                    }
                }
                SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CHOOSE_LIST, chooseParticipants);
            }
        }

    }

    /**
     * 选看单画面
     *
     * @param conferenceId
     */
    @PatchMapping("/choose/multiPic/{conferenceId}/{participantId}")
    public void chooseMultiPicOnly(@PathVariable String conferenceId, @PathVariable String participantId, @RequestBody(required = false) String chooseId) {
        MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO = new MultiPicInfoReq.MultiPicInfoDTO();
        multiPicInfoDTO.setMode(1);
        multiPicInfoDTO.setPicNum(1);
        ArrayList<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicListDTOS = new ArrayList<>();
        MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO();
        subPicListDTO.setStreamNumber(0);
        if (Strings.isBlank(chooseId) || Objects.equals(CHOOSE_ID, chooseId)) {
            subPicListDTO.setParticipantId("00000000-0000-0000-0000-000000000000");
        } else {
            Map parseObject = JSON.parseObject(chooseId, Map.class);
            String s = (String) parseObject.get("chooseId");
            if (Strings.isBlank(s)) {
                s = "00000000-0000-0000-0000-000000000000";
            }
            subPicListDTO.setParticipantId(s);

        }

        subPicListDTOS.add(subPicListDTO);
        multiPicInfoDTO.setSubPicList(subPicListDTOS);
        smcConferenceService.conferencesControlChoose(conferenceId, participantId, multiPicInfoDTO);
    }

    /**
     * 设置主席轮询
     *
     * @param masterPollTemplate
     */
    @PostMapping("/chairmanPoll/create")
    public RestResponse chairmanPollset(@RequestBody MasterPollTemplate masterPollTemplate) {
        smcConferenceService.createChairmanPollMulitiPicPoll(masterPollTemplate);
        return RestResponse.success();
    }

    /**
     * 设置主席轮询 新版本
     *
     * @param multiPicPollRequest
     */
    @PostMapping("/chairman/participantMultiPicPoll")
    public RestResponse chairmanParticipantMultiPicPoll(@RequestBody MultiPicPollRequest multiPicPollRequest) {
        smcConferenceService.chairmanParticipantMultiPicPoll(multiPicPollRequest);
        return RestResponse.success();
    }

    /**
     * 查询主席轮询 新版本
     *
     * @param conferenceId
     */
    @GetMapping("/chairmanParticipantMultiPicPollQuery")
    public RestResponse chairmanParticipantMultiPicPollQuery(@RequestParam("conferenceId") String conferenceId, @RequestParam(value = "participantId", required = false) String participantId) {

        MultiPicPollRequest multiPicPollRequest = smcConferenceService.chairmanParticipantMultiPicPollQuery(conferenceId, participantId);
        return RestResponse.success(multiPicPollRequest);
    }

    /**
     * 主席轮询操作 新版本
     *
     * @param chairmanPollOperateReq
     */
    @PostMapping("/chairman/participantMultiPicPoll/operate")
    public RestResponse chairmanParticipantMultiPicPollOperate(@RequestBody ChairmanPollOperateReq chairmanPollOperateReq) {

        smcConferenceService.chairmanParticipantMultiPicPollOperate(chairmanPollOperateReq);

        return RestResponse.success();
    }


    /**
     * 查询主席轮询
     *
     * @param conferenceId
     */
    @GetMapping("/chairmanPoll/{conferenceId}")
    public RestResponse chairmanPollQuery(@PathVariable String conferenceId) {
        MasterPollTemplate masterPollTemplate = smcConferenceService.chairmanPollQuery(conferenceId);
        return RestResponse.success(masterPollTemplate);
    }


    /**
     * 开始 暂停  结束 主席轮询 操作
     *
     * @param chairmanPollOperateReq
     */
    @PostMapping("/chairmanPoll/operate")
    public RestResponse chairmanPollOperate(@RequestBody ChairmanPollOperateReq chairmanPollOperateReq) {
        String conferenceId = chairmanPollOperateReq.getConferenceId();
        smcConferenceService.chairmanPollOperate(chairmanPollOperateReq);
        Map<String, ConferenceState> smcConferenceStateMap = SmcConferenceContextCache.getInstance().getSmcConferenceStateMap();
        ConferenceState conferenceState = smcConferenceStateMap.get(conferenceId);
        if (conferenceState == null) {
            throw new CustomException("会议状态获取错误");
        }
        String chairmanId = conferenceState.getState().getChairmanId();
        if (Objects.equals(chairmanPollOperateReq.getPollStatus(), PollOperateTypeDto.START)) {
            //取消广播
            ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
            conferenceStatusRequest.setBroadcaster(chairmanId);
            smcConferenceService.statusControl(conferenceId, conferenceStatusRequest);
            Map<String, Thread> chairmanPollingThread = SmcConferenceContextCache.getInstance().getChairmanPollingThread();
            Thread thread = chairmanPollingThread.get(conferenceId);


            //取消选看
            try {
                String cId = SmcConferenceContextCache.getInstance().getChooseParticipantMap().get(conferenceId);
                conferenceSMCService.cancelChoose(conferenceId, cId, true);
                conferenceSMCService.cancelCallTheRoll(conferenceId, cId, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (Objects.equals(chairmanPollOperateReq.getPollStatus(), PollOperateTypeDto.STOP) || Objects.equals(chairmanPollOperateReq.getPollStatus(), PollOperateTypeDto.CANCEL)) {

            ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();

            conferenceStatusRequest.setBroadcaster(chairmanId);
            smcConferenceService.statusControl(conferenceId, conferenceStatusRequest);
            Map<String, Thread> chairmanPollingThread = SmcConferenceContextCache.getInstance().getChairmanPollingThread();
            Thread thread = chairmanPollingThread.get(conferenceId);
            if (thread != null) {
                ChairManPollingThread chairManPollingThread = (ChairManPollingThread) thread;
                chairManPollingThread.flag = false;
            }
        }

        return RestResponse.success();
    }


    /**
     * 获取多画面轮询
     *
     * @param conferenceId
     */
    @GetMapping("/{conferenceId}")
    public RestResponse query(@PathVariable String conferenceId) {
        return RestResponse.success(smcConferenceService.queryMulitiPicPoll(conferenceId));
    }

    /**
     * 多画面轮询操作
     *
     * @param broadcastPollRequest
     */
    @PostMapping("/broadcastPoll")
    public void setBroadcastPoll(@RequestBody BroadcastPollRequest broadcastPollRequest) {
        smcConferenceService.setBroadcastPoll(broadcastPollRequest);
    }

    /**
     * 多画面广播
     *
     * @param conferenceId
     * @param enable
     */
    @PatchMapping("/multiPicBroad/{conferenceId}/{enable}")
    public RestResponse multiPicBroad(@PathVariable String conferenceId, @PathVariable boolean enable) {
        DetailConference detailConference = smcConferenceService.getDetailConferenceInfoById(conferenceId);
        if (detailConference == null) {
            throw new CustomException("会议不存在");
        }
        if (enable) {
            ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfo = detailConference.getConferenceState().getMultiPicInfo();
            if (Objects.isNull(multiPicInfo)) {
                throw new CustomException("多画面未设置");
            }
            try {
                String chooseId = SmcConferenceContextCache.getInstance().getChooseParticipantMap().get(conferenceId);
                if (Strings.isNotBlank(chooseId)) {
                    conferenceSMCService.cancelChoose(conferenceId, chooseId, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CHOOSE_LIST, new ArrayList<>());
                String chairmanId = detailConference.getConferenceState().getChairmanId();
                if (Strings.isNotBlank(chairmanId)) {
                    ChairmanPollOperateReq chairmanPollOperateReq = new ChairmanPollOperateReq();
                    chairmanPollOperateReq.setPollStatus(PollOperateTypeDto.CANCEL);
                    chairmanPollOperateReq.setConferenceId(conferenceId);
                    smcConferenceService.chairmanParticipantMultiPicPollOperate(chairmanPollOperateReq);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        smcConferenceService.multiPicBroad(conferenceId, enable);
        if(enable){
            smcConferenceService.setMute(conferenceId, true);
        }
        if (!enable) {
            ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
            String chairman = detailConference.getConferenceState().getChairmanId();
            if (Strings.isNotBlank(chairman)) {
                conferenceStatusRequest.setBroadcaster(chairman);
            }
            smcConferenceService.statusControl(conferenceId, conferenceStatusRequest);
            SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CHOOSE_LIST, new ArrayList<>());
        }
        return RestResponse.success();
    }


    /**
     * 多画面轮询设置
     *
     * @param multiPicPollRequest
     */
    @PostMapping("/setMultiPicPoll")
    public void setMultiPicPoll(@RequestBody MultiPicPollRequest multiPicPollRequest) {
        smcConferenceService.setMultiPicPoll(multiPicPollRequest);
    }

    /**
     * 停止多画面轮询
     *
     * @param conferenceId
     */
    @PostMapping("/stopMultiPicPoll/{conferenceId}")
    public void stopMultiPicPoll(@PathVariable String conferenceId) {
        smcConferenceService.stopMultiPicPoll(conferenceId);

    }


    /**
     * 开始多画面轮询
     *
     * @param conferenceId
     */
    @PostMapping("/startMultiPicPoll/{conferenceId}")
    public void startMultiPicPoll(@PathVariable String conferenceId) {
        MultiPicPollRequest multiPicPollRequest = smcConferenceService.queryMulitiPicPoll(conferenceId);
        if (multiPicPollRequest == null) {
            throw new CustomException("未设置多画面轮询");
        }
        try {
            ChairmanPollOperateReq chairmanPollOperateReq = new ChairmanPollOperateReq();
            chairmanPollOperateReq.setPollStatus(PollOperateTypeDto.CANCEL);
            chairmanPollOperateReq.setConferenceId(conferenceId);
            smcConferenceService.chairmanParticipantMultiPicPollOperate(chairmanPollOperateReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //取消选看
        try {
            String cId = SmcConferenceContextCache.getInstance().getChooseParticipantMap().get(conferenceId);
            ConferenceSMCService conferenceSMCService = BeanFactory.getBean(ConferenceSMCService.class);
            if (Strings.isNotBlank(cId)) {
                conferenceSMCService.cancelChoose(conferenceId, cId, true);
            }
            Map<String, ConferenceState> smcConferenceStateMap = SmcConferenceContextCache.getInstance().getSmcConferenceStateMap();
            if (smcConferenceStateMap != null) {
                ConferenceState conferenceState = smcConferenceStateMap.get(conferenceId);
                if (conferenceState != null && Strings.isNotBlank(conferenceState.getState().getSpokesmanId())) {
                    conferenceSMCService.cancelCallTheRoll(conferenceId, conferenceState.getState().getSpokesmanId(), true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        smcConferenceService.startMultiPicPoll(conferenceId);
        smcConferenceService.setMute(conferenceId, true);

    }


    /**
     * 取消多画面轮询
     *
     * @param conferenceId
     */
    @PostMapping("/cancelMultiPicPoll/{conferenceId}")
    public void cancelMultiPicPoll(@PathVariable String conferenceId) {
        smcConferenceService.cancelMultiPicPoll(conferenceId);
        smcConferenceService.multiPicBroad(conferenceId, false);


        ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
        DetailConference detailConference = smcConferenceService.getDetailConferenceInfoById(conferenceId);
        if (detailConference != null) {
            String chairman = detailConference.getConferenceState().getChairmanId();
            if (Strings.isNotBlank(chairman)) {
                conferenceStatusRequest.setBroadcaster(chairman);
            }
            smcConferenceService.statusControl(conferenceId, conferenceStatusRequest);
            SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CHOOSE_LIST, new ArrayList<>());
        }

        SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.BROAD_LIST, new ArrayList<>());
    }


}

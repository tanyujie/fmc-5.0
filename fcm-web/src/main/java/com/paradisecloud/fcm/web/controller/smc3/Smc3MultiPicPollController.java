package com.paradisecloud.fcm.web.controller.smc3;



import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.smc3.model.request.BroadcastPollRequest;
import com.paradisecloud.smc3.model.request.ChairmanPollOperateReq;
import com.paradisecloud.smc3.model.request.MultiPicInfoReq;
import com.paradisecloud.smc3.model.request.MultiPicPollRequest;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3ConferenceService;

import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


/**
 * @author nj
 * @date 2022/8/26 11:17
 */
@RestController
@RequestMapping("/smc3/mulitiPicPoll")
public class Smc3MultiPicPollController {

    @Resource
    private IBusiSmc3ConferenceService busiSmc3ConferenceService;

    public static final String CHOOSE_ID = "{\"chooseId\":\"\"}";

    /**
     * 设置多画面
     *
     * @param multiPicInfoReq
     */
    @PostMapping("/create/multiPic")
    public void createMultiPic(@RequestBody MultiPicInfoReq multiPicInfoReq) {
        busiSmc3ConferenceService.createMultiPic(multiPicInfoReq);

    }


    /**
     * 选看多画面
     *
     * @param conferenceId
     */
    @PatchMapping("/chooseManly/multiPic/{conferenceId}/{participantId}")
    public void chooseMultiPicManly(@PathVariable String conferenceId, @PathVariable String participantId, @RequestBody MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO) {

        busiSmc3ConferenceService.conferencesControlChoose(conferenceId, participantId, multiPicInfoDTO);
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
        busiSmc3ConferenceService.conferencesControlChoose(conferenceId, participantId, multiPicInfoDTO);
    }


    /**
     * 设置主席轮询 新版本
     *
     * @param multiPicPollRequest
     */
    @PostMapping("/chairman/participantMultiPicPoll")
    public RestResponse chairmanParticipantMultiPicPoll(@RequestBody MultiPicPollRequest multiPicPollRequest) {
        busiSmc3ConferenceService.setChairmanParticipantMultiPicPoll(multiPicPollRequest);
        return RestResponse.success();
    }

    /**
     * 查询主席轮询 新版本
     *
     * @param conferenceId
     */
    @GetMapping("/chairmanParticipantMultiPicPollQuery")
    public RestResponse chairmanParticipantMultiPicPollQuery(@RequestParam("conferenceId") String conferenceId, @RequestParam(value = "participantId", required = false) String participantId) {
        return RestResponse.success( busiSmc3ConferenceService.chairmanParticipantMultiPicPollQuery(conferenceId,participantId));
    }

    /**
     * 主席轮询操作 新版本
     *
     * @param chairmanPollOperateReq
     */
    @PostMapping("/chairman/participantMultiPicPoll/operate")
    public RestResponse chairmanParticipantMultiPicPollOperate(@RequestBody ChairmanPollOperateReq chairmanPollOperateReq) {
        busiSmc3ConferenceService.chairmanParticipantMultiPicPollOperate(chairmanPollOperateReq);
        return RestResponse.success();
    }






    /**
     * 获取多画面轮询
     *
     * @param conferenceId
     */
    @GetMapping("/{conferenceId}")
    public RestResponse query(@PathVariable String conferenceId) {
        return RestResponse.success(busiSmc3ConferenceService.queryMulitiPicPoll(conferenceId));
    }

    /**
     *定时广播设置功能
     *
     * @param broadcastPollRequest
     */
    @PostMapping("/broadcastPoll")
    public void setBroadcastPoll(@RequestBody BroadcastPollRequest broadcastPollRequest) {
        busiSmc3ConferenceService.setBroadcastPoll(broadcastPollRequest);
    }

    /**
     * 多画面广播
     *
     * @param conferenceId
     * @param enable
     */
    @PatchMapping("/multiPicBroad/{conferenceId}/{enable}")
    public RestResponse multiPicBroad(@PathVariable String conferenceId, @PathVariable boolean enable) {
        busiSmc3ConferenceService.multiPicBroad(conferenceId,enable);
        return RestResponse.success();
    }


    /**
     * 多画面轮询设置
     *
     * @param multiPicPollRequest
     */
    @PostMapping("/setMultiPicPoll")
    public void setMultiPicPoll(@RequestBody MultiPicPollRequest multiPicPollRequest) {
        busiSmc3ConferenceService.setMultiPicPoll(multiPicPollRequest);
    }

    /**
     * 停止多画面轮询
     *
     * @param conferenceId
     */
    @PostMapping("/stopMultiPicPoll/{conferenceId}")
    public void stopMultiPicPoll(@PathVariable String conferenceId) {
        busiSmc3ConferenceService.stopMultiPicPoll(conferenceId);

    }


    /**
     * 开始多画面轮询
     *
     * @param conferenceId
     */
    @PostMapping("/startMultiPicPoll/{conferenceId}")
    public void startMultiPicPoll(@PathVariable String conferenceId) {
        busiSmc3ConferenceService.startMultiPicPoll(conferenceId);

    }


    /**
     * 取消多画面轮询
     *
     * @param conferenceId
     */
    @PostMapping("/cancelMultiPicPoll/{conferenceId}")
    public void cancelMultiPicPoll(@PathVariable String conferenceId) {
        busiSmc3ConferenceService.cancelMultiPicPoll(conferenceId);

    }


}

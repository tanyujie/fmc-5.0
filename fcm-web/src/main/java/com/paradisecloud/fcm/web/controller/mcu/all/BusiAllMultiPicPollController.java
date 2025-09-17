package com.paradisecloud.fcm.web.controller.mcu.all;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiSmc2ConferenceService;
import com.paradisecloud.smc3.model.request.BroadcastPollRequest;
import com.paradisecloud.smc3.model.request.ChairmanPollOperateReq;
import com.paradisecloud.smc3.model.request.MultiPicInfoReq;
import com.paradisecloud.smc3.model.request.MultiPicPollRequest;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3ConferenceService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paradisecloud.common.core.model.RestResponse.success;


/**
 * @author nj
 * @date 2022/8/26 11:17
 */
@RestController
@RequestMapping("/busi/mcu/all/mulitiPicPoll")
public class BusiAllMultiPicPollController extends BaseController {

    @Resource
    private IBusiSmc3ConferenceService busiSmc3ConferenceService;
    @Resource
    private IBusiSmc2ConferenceService busiSmc2ConferenceService;
    public static final String CHOOSE_ID = "{\"chooseId\":\"\"}";



    /**
     * 选看多画面
     *
     * @param conferenceId
     */
    @PatchMapping("/chooseManly/multiPic/{conferenceId}/{participantId}")
    @Operation(summary = "选看多画面")
    public void chooseMultiPicManly(@PathVariable String conferenceId, @PathVariable String participantId, @RequestBody MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO) {



        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {

            case SMC3: {
                busiSmc3ConferenceService.conferencesControlChoose(conferenceId, participantId, multiPicInfoDTO);
               break;
            }
            case SMC2: {
                String jsonString = JSONObject.toJSONString(multiPicInfoDTO);
                com.paradisecloud.com.fcm.smc.modle.request.MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO2 = JSONObject.parseObject(jsonString, com.paradisecloud.com.fcm.smc.modle.request.MultiPicInfoReq.MultiPicInfoDTO.class);
                busiSmc2ConferenceService.chooseMultiPicManly(conferenceId, participantId, multiPicInfoDTO2);
               break;
            }
        }

    }
     /**
     * 选看单画面
     *
     * @param conferenceId
     */
    @PatchMapping("/choose/multiPic/{conferenceId}/{participantId}")
    @Operation(summary = "选看单画面")
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
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {

            case SMC3: {
                busiSmc3ConferenceService.conferencesControlChoose(conferenceId, participantId, multiPicInfoDTO);
                break;
            }
            case SMC2: {
                String jsonString = JSONObject.toJSONString(multiPicInfoDTO);
                com.paradisecloud.com.fcm.smc.modle.request.MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO2 = JSONObject.parseObject(jsonString, com.paradisecloud.com.fcm.smc.modle.request.MultiPicInfoReq.MultiPicInfoDTO.class);
                busiSmc2ConferenceService.conferencesControlChoose(conferenceId, participantId, multiPicInfoDTO2);
                break;
            }
        }
    }


    /**
     * 设置主席轮询 新版本
     *
     * @param multiPicPollRequest
     */
    @PostMapping("/chairman/participantMultiPicPoll")
    @Operation(summary = "设置主席轮询")
    public RestResponse chairmanParticipantMultiPicPoll(@RequestBody MultiPicPollRequest multiPicPollRequest) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(multiPicPollRequest.getConferenceId());
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case SMC3: {
                busiSmc3ConferenceService.setChairmanParticipantMultiPicPoll(multiPicPollRequest);
                return success();
            }
            case SMC2: {
                com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest multiPicPollRequest1 = new com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest();
                BeanUtils.copyProperties(multiPicPollRequest,multiPicPollRequest1);
                List<com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoListDTOS = new ArrayList<>();
                List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoList = multiPicPollRequest.getSubPicPollInfoList();
                for (MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO : subPicPollInfoList) {
                    Integer interval = subPicPollInfoListDTO.getInterval();
                    List<com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> objects = new ArrayList<>();
                    com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO1 = new com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest.SubPicPollInfoListDTO();
                    subPicPollInfoListDTO1.setInterval(interval);
                    subPicPollInfoListDTO1.setParticipantIds(objects);
                    List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = subPicPollInfoListDTO.getParticipantIds();
                    for (MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantId : participantIds) {
                        com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO = new com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO();
                        participantIdsDTO.setParticipantId(participantId.getParticipantId());
                        participantIdsDTO.setStreamNumber(participantId.getStreamNumber());
                        objects.add(participantIdsDTO);
                    }
                    subPicPollInfoListDTOS.add(subPicPollInfoListDTO1);
                }
                multiPicPollRequest1.setSubPicPollInfoList(subPicPollInfoListDTOS);
                busiSmc2ConferenceService.setChairmanParticipantMultiPicPoll(multiPicPollRequest1);
                return success();
            }
        }
        return RestResponse.fail();
    }

    /**
     * 查询主席轮询 新版本
     *
     * @param conferenceId
     */
    @GetMapping("/chairmanParticipantMultiPicPollQuery")
    @Operation(summary = "查询主席轮询")
    public RestResponse chairmanParticipantMultiPicPollQuery(@RequestParam("conferenceId") String conferenceId, @RequestParam(value = "participantId", required = false) String participantId) {

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case SMC3: {
                return success( busiSmc3ConferenceService.chairmanParticipantMultiPicPollQuery(conferenceId,participantId));
            }
            case SMC2: {
                return success( busiSmc2ConferenceService.chairmanParticipantMultiPicPollQuery(conferenceId,participantId));
            }
        }
        return RestResponse.fail();
    }

    /**
     * 主席轮询操作 新版本
     *
     * @param chairmanPollOperateReq
     */
    @PostMapping("/chairman/participantMultiPicPoll/operate")
    @Operation(summary = "主席轮询")
    public RestResponse chairmanParticipantMultiPicPollOperate(@RequestBody ChairmanPollOperateReq chairmanPollOperateReq) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(chairmanPollOperateReq.getConferenceId());
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case SMC3: {
                busiSmc3ConferenceService.chairmanParticipantMultiPicPollOperate(chairmanPollOperateReq);
                return success();
            }
            case SMC2: {
                com.paradisecloud.com.fcm.smc.modle.request.ChairmanPollOperateReq chairmanPollOperateReq1 = new com.paradisecloud.com.fcm.smc.modle.request.ChairmanPollOperateReq();
                chairmanPollOperateReq1.setConferenceId(chairmanPollOperateReq.getConferenceId());
                chairmanPollOperateReq1.setPollStatus(PollOperateTypeDto.valueOf(chairmanPollOperateReq.getPollStatus().name()));
                busiSmc2ConferenceService.chairmanParticipantMultiPicPollOperate(chairmanPollOperateReq1);
                return success();
            }
        }
        return RestResponse.fail();
    }






    /**
     * 获取多画面轮询
     *
     * @param conferenceId
     */
    @GetMapping("/{conferenceId}")
    @Operation(summary = "获取多画面轮询")
    public RestResponse query(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {

            case SMC3: {
                return success(busiSmc3ConferenceService.queryMulitiPicPoll(conferenceId));
            }
            case SMC2: {
                return success(busiSmc2ConferenceService.queryMulitiPicPoll(conferenceId));
            }
        }
        return RestResponse.fail();



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
    @Operation(summary = "多画面广播")
    public RestResponse multiPicBroad(@PathVariable String conferenceId, @PathVariable boolean enable) {
        busiSmc3ConferenceService.multiPicBroad(conferenceId,enable);
        return success();
    }


    /**
     * 多画面轮询设置
     *
     * @param
     */
    @PostMapping("/setMultiPicPoll/{conferenceId}")
    @Operation(summary = "多画面轮询设置")
    public RestResponse setMultiPicPoll(@PathVariable String conferenceId, @RequestBody JSONObject jsonObject) {

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {

            case SMC3: {
                MultiPicPollRequest multiPicPollRequest = JSONObject.parseObject(jsonObject.toJSONString(), MultiPicPollRequest.class);
                multiPicPollRequest.setConferenceId(conferenceId);
                busiSmc3ConferenceService.setMultiPicPoll(multiPicPollRequest);
                return success();
            }
            case SMC2: {
                com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest multiPicPollRequest = JSONObject.parseObject(jsonObject.toJSONString(), com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest.class);
                multiPicPollRequest.setConferenceId(conferenceId);
                busiSmc2ConferenceService.setMultiPicPoll(multiPicPollRequest);
                return success();
            }
        }
        return RestResponse.fail();


    }

    /**
     * 停止多画面轮询
     *
     * @param conferenceId
     */
    @PostMapping("/stopMultiPicPoll/{conferenceId}")
    @Operation(summary = "停止多画面轮询")
    public RestResponse stopMultiPicPoll(@PathVariable String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {

            case SMC3: {
                busiSmc3ConferenceService.stopMultiPicPoll(conferenceId);
                return success();
            }
            case SMC2: {
                busiSmc2ConferenceService.stopMultiPicPoll(conferenceId);
                return success();
            }
        }
        return RestResponse.fail();


    }


    /**
     * 开始多画面轮询
     *
     * @param conferenceId
     */
    @PostMapping("/startMultiPicPoll/{conferenceId}")
    @Operation(summary = "开始多画面轮询")
    public RestResponse startMultiPicPoll(@PathVariable String conferenceId) {

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {

            case SMC3: {
                busiSmc3ConferenceService.startMultiPicPoll(conferenceId);
                return success();
            }
            case SMC2: {
                busiSmc2ConferenceService.startMultiPicPoll(conferenceId);
                return success();
            }
        }
        return RestResponse.fail();
    }


    /**
     * 取消多画面轮询
     *
     * @param conferenceId
     */
    @PostMapping("/cancelMultiPicPoll/{conferenceId}")
    @Operation(summary = "停止多画面轮询")
    public RestResponse cancelMultiPicPoll(@PathVariable String conferenceId) {

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {

            case SMC3: {
                busiSmc3ConferenceService.cancelMultiPicPoll(conferenceId);
                return success();
            }
            case SMC2: {
                busiSmc2ConferenceService.cancelMultiPicPoll(conferenceId);
                return success();
            }
        }
        return RestResponse.fail();

    }


}

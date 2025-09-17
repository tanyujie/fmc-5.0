package com.paradisecloud.fcm.web.controller.smc.tele;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.telep.cache.TeleBridgeCache;
import com.paradisecloud.fcm.telep.cache.TelepBridge;
import com.paradisecloud.fcm.telep.model.busi.ConferencesResponse;
import com.paradisecloud.fcm.telep.model.busi.TeleConference;
import com.paradisecloud.fcm.telep.model.busi.participants.TeleParticipant;
import com.paradisecloud.fcm.telep.model.request.EnumerateFilter;
import com.paradisecloud.fcm.telep.model.request.ParticipantAddRequest;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiParaticipantsService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * @author nj
 * @date 2022/12/30 14:56
 */
@RestController
@RequestMapping("/smc/tele/participants")
public class TeleParaticipantsController extends BaseController {

    @Resource
    private IBusiParaticipantsService iBusiParaticipantsService;

    @PostMapping("/add/participants")
    @Operation(summary = "邀请与会者")
    public RestResponse participantAdd(@RequestBody ParticipantAddRequest participantAddRequest) {
        String uri = participantAddRequest.getUri();
        String[] split = uri.split("@");
        String number = split[0];
        String ip=split[1];


        if (Strings.isBlank(number) || Strings.isBlank(ip)||Strings.isBlank(participantAddRequest.getAddress())) {
           throw new CustomException("参数错误");
        }

        Map<String, TelepBridge> ipToTeleBridgeMap = TeleBridgeCache.getInstance().getIpToTeleBridgeMap();

        TelepBridge telepBridge = ipToTeleBridgeMap.get(ip);
        if (telepBridge == null) {
            throw new CustomException("邀请失败");
        }
        ConferencesResponse response = telepBridge.getTeleConferenceApiInvoker().enumerateBean(null);
        if (response != null) {
            List<TeleConference> conferences = response.getConferences();
            if (!CollectionUtils.isEmpty(conferences)) {
                Optional<TeleConference> first = conferences.stream().filter(p -> Objects.equals(number, p.getNumericId())).findFirst();
                if (first.isPresent()) {
                    participantAddRequest.setConferenceName(first.get().getConferenceName());
                }else {
                    throw new CustomException("邀请失败");
                }
            }
        }
        participantAddRequest.setAddResponse(true);
        iBusiParaticipantsService.participantAdd(ip,participantAddRequest);
        return RestResponse.success();
    }


    /**
     * 一键清除
     */
    @GetMapping("/removeDisconnected")
    @Operation(summary = "通过uri一键清除")
    public RestResponse getParticipantsByUri(@RequestParam String uri) {
        String[] split = uri.split("@");
        String number = split[0];
        String ip=split[1];
        List<TeleParticipant> list = iBusiParaticipantsService.getList(number, ip, EnumerateFilter.DISCONNECTED);
        if(!CollectionUtils.isEmpty(list)){
            logger.info("TeleParticipantList DISCONNECTED return"+ JSON.toJSONString(list));
            for (TeleParticipant teleParticipant : list) {
                iBusiParaticipantsService.participantRemove(ip,teleParticipant);
            }
        }
        return  RestResponse.success();
    }



    @PostMapping("/modify/participant")
    @Operation(summary = "修改与会者")
    public RestResponse participantModify(@RequestParam String uri,@RequestBody TeleParticipant teleParticipant) {
        String[] split = uri.split("@");
        String ip=split[1];
        iBusiParaticipantsService.participantModify(ip,teleParticipant);
        return RestResponse.success();
    }
}

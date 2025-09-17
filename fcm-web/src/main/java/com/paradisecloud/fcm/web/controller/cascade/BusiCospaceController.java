package com.paradisecloud.fcm.web.controller.cascade;

import com.paradisecloud.com.fcm.smc.modle.ParticipantReqDto;
import com.paradisecloud.com.fcm.smc.modle.mix.CreateParticipantsReq;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallService;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.enumer.FmeBridgeProcessingStrategy;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.paradisecloud.fcm.fme.model.response.callleg.CallLegInfoResponse;
import com.paradisecloud.fcm.fme.model.response.callleg.CallLegsResponse;
import com.paradisecloud.fcm.fme.model.response.cospace.CoSpaceInfoResponse;
import com.paradisecloud.fcm.fme.model.response.participant.ActiveParticipantsResponse;
import com.paradisecloud.fcm.fme.model.response.participant.ParticipantsResponse;
import com.paradisecloud.fcm.smc.cache.modle.SmcConferenceContextCache;
import com.paradisecloud.fcm.smc.cache.modle.SmcWebSocketMessagePusher;
import com.paradisecloud.fcm.smc.cache.modle.SmcWebsocketMessageType;
import com.paradisecloud.fcm.web.model.smc.CospaceRequest;
import com.paradisecloud.fcm.web.utils.AuthenticationUtil;
import com.paradisecloud.smc.service.SmcParticipantsService;
import com.sinhy.utils.ThreadUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nj
 * @date 2023/4/6 10:20
 */
@RestController
@RequestMapping("/smc/monitor/fme")
@Tag(name = "WEB会监")
public class BusiCospaceController extends BaseController {


    @Resource
    private ICoSpaceService cospaceService;
    @Resource
    private SmcParticipantsService smcParticipantsService;


    @Resource
    private ICallService callService;

    /**
     * 获取会议号码
     */
    @PostMapping(value = "/cospace/close")
    @Operation(summary = "获取会议号码记")
    public RestResponse deleteCospace(@RequestBody CospaceRequest cospaceRequest) {
        String conferenceId = cospaceRequest.getConferenceId();
        String cospaceId = cospaceRequest.getMonitorNumber();
        Long deptId = AuthenticationUtil.getDeptId() == null ? 1 : AuthenticationUtil.getDeptId();

        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
        CoSpace coSpace = (CoSpace) SmcConferenceContextCache.getInstance().getMonitorParticipantMap().get(conferenceId);
        if (coSpace != null) {
            try {
                fmeBridge.getCoSpaceInvoker().deleteCoSpace(coSpace.getId());
            } catch (Exception e) {
                LoggerFactory.getLogger(getClass()).error("recoveryCospace fail: ", e);
            }

            fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor() {
                public void process(FmeBridge fmeBridge) {
                    fmeBridge.getDataCache().deleteCoSpace(coSpace.getId());
                }
            });
        }

        SmcConferenceContextCache.getInstance().getMonitorParticipantMap().remove(conferenceId);
        try {
            SmcParitipantsStateRep conferencesParticipantsState = smcParticipantsService.getConferencesParticipantsState(conferenceId, 0, 10000);
            if (conferencesParticipantsState != null) {
                if (!CollectionUtils.isEmpty(conferencesParticipantsState.getContent())) {
                    Optional<SmcParitipantsStateRep.ContentDTO> first = conferencesParticipantsState.getContent().stream().filter(p -> p.getGeneralParam().getUri().startsWith(cospaceId)).findFirst();
                    if (first.isPresent()) {
                        String participantId = first.get().getGeneralParam().getId();
                        List<String> participantIds = new ArrayList<>();
                        participantIds.add(participantId);
                        smcParticipantsService.delete(conferenceId, participantIds);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.SMC_MONITOR_NUMBER_REMOVE, cospaceRequest.getMonitorNumber());
        return RestResponse.success();
    }

    /**
     * 获取会议号码
     */
    @PostMapping(value = "/cospace")
    @Operation(summary = "获取会议号码记")
    public synchronized RestResponse getInfo(@RequestBody CospaceRequest cospaceRequest) {

        String conferenceId = cospaceRequest.getConferenceId();
        String subject = cospaceRequest.getSubject();
        String accessCode = cospaceRequest.getAccessCode();
        Long deptId = AuthenticationUtil.getDeptId() == null ? 1 : AuthenticationUtil.getDeptId();
        long cn = System.currentTimeMillis();

        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
        Object o = SmcConferenceContextCache.getInstance().getMonitorParticipantMap().get(conferenceId);
        if (o != null) {
            return RestResponse.success(o);
        }
        //创建COSPACE
        CoSpace coSpace = getCoSpaceByConferenceNumber(fmeBridge, "7" + cn);
        if (coSpace == null) {
            return RestResponse.fail();
        }
        Call call = callService.createCall(fmeBridge, "7" + cn, "会议监控");
      //  String passcode="230728";
        CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
        coSpaceParamBuilder.name("SMC-MONITOR");
        coSpaceParamBuilder.panePlacementHighestImportance(100);
        coSpaceParamBuilder.defaultLayout("speakerOnly");
      //  coSpaceParamBuilder.passcode(passcode);
        cospaceService.updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);
        String number = coSpace.getUri();
        String uri = number + "@" + fmeBridge.getBusiFme().getIp()+":5060";
        CreateParticipantsReq createParticipantsReq = new CreateParticipantsReq();
        createParticipantsReq.setConferenceId(conferenceId);
        List<ParticipantReqDto> participants = new ArrayList<>(1);
        ParticipantReqDto participantReqDto = new ParticipantReqDto();
        participantReqDto.setName("会议监控");
        participantReqDto.setUri(uri);
        participantReqDto.setIpProtocolType("SIP");
       // participantReqDto.setDtmfInfo(passcode);
        participants.add(participantReqDto);
        createParticipantsReq.setParticipants(participants);
        smcParticipantsService.addParticipants(createParticipantsReq);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //查找到这个与会者并设置为权重100,
        AtomicInteger count = new AtomicInteger();

        Participant smcParticipant = null;
        while (count.getAndIncrement() < 100) {
            try {
                ParticipantsResponse participantsResponse = fmeBridge.getCallInvoker().getParticipants(call.getId(), 0);
                if (participantsResponse != null) {
                    ActiveParticipantsResponse participantsResponseParticipants = participantsResponse.getParticipants();
                    if (participantsResponseParticipants != null) {
                        ArrayList<Participant> participant = participantsResponseParticipants.getParticipant();
                        if (!CollectionUtils.isEmpty(participant)) {
                            Optional<Participant> first = participant.stream().filter(p -> {
                                if (Objects.equals(p.getName(), subject) || Objects.equals(p.getName(), accessCode)) {
                                    return true;
                                }
                                return false;
                            }).findFirst();
                            if (first.isPresent()) {
                                smcParticipant = first.get();
                                fmeBridge.getParticipantInvoker().updateParticipant(smcParticipant.getId(), new ParticipantParamBuilder().importance(100).build());
                                CallLeg callLeg = getCallLegByParticipant(fmeBridge, smcParticipant);
                                if (callLeg != null) {
                                    fmeBridge.getCallLegInvoker().updateCallLeg(callLeg.getId(), new ParticipantParamBuilder().rxAudioMute(false).rxVideoMute(false).build());
                                    break;
                                }

                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("会监权重修改失败：=》" + e.getMessage());
                break;
            }
        }


        SmcConferenceContextCache.getInstance().getMonitorParticipantMap().put(conferenceId, coSpace);
        SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.SMC_MONITOR_NUMBER_ADD, number);
        return RestResponse.success(coSpace);


    }

    public CallLeg getCallLegByParticipant(FmeBridge fmeBridge, Participant participant) {
        CallLegsResponse callLegInfoResponse = fmeBridge.getCallLegInvoker().getCallLegs(participant.getId());
        if (callLegInfoResponse != null
                && callLegInfoResponse.getCallLegs() != null
                && callLegInfoResponse.getCallLegs().getCallLeg() != null
                && !callLegInfoResponse.getCallLegs().getCallLeg().isEmpty()) {
            for (CallLeg callLeg : callLegInfoResponse.getCallLegs().getCallLeg()) {
                CallLegInfoResponse infoResponse = fmeBridge.getCallLegInvoker().getCallLeg(callLeg.getId());
                if (infoResponse != null && infoResponse.getCallLeg() != null) {
                    return infoResponse.getCallLeg();
                }
            }
        }
        fmeBridge.getFmeLogger().logWebsocketInfo("smc monitor callleg not found：" + participant, true);
        return null;
    }

    public CoSpace getCoSpaceByConferenceNumber(FmeBridge fmeBridge, String conferenceNumber) {
        CoSpace coSpace = null;
        // 根据会议号获取coSpace，没有则自动创建
        try {
            coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceNumber);
            if (coSpace != null) {
                return coSpace;
            }
            String coSpaceId = fmeBridge.getCoSpaceInvoker().createCoSpace(new CoSpaceParamBuilder().conferenceNumber(conferenceNumber).build());
            CoSpaceInfoResponse coSpaceInfoResponse = fmeBridge.getCoSpaceInvoker().getCoSpaceInfo(coSpaceId);
            if (coSpaceInfoResponse != null && coSpaceInfoResponse.getCoSpace() != null) {
                coSpace = coSpaceInfoResponse.getCoSpace();
                if (!ObjectUtils.isEmpty(coSpaceInfoResponse.getCoSpace().getUri())) {
                    fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor() {
                        public void process(FmeBridge fmeBridge) {
                            fmeBridge.getDataCache().update(coSpaceInfoResponse.getCoSpace());
                            fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace data sync: " + coSpaceInfoResponse.getCoSpace(), true);
                        }
                    });
                    return coSpace;
                } else {
                    fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace data sync, 发现会议号为空的coSpace记录: " + coSpaceInfoResponse.getCoSpace(), true);
                }
            }
        } catch (Throwable e) {
            ThreadUtils.sleep(100);
            fmeBridge.getFmeLogger().logWebsocketInfo("createCoSpace error, begin sync all cospaces: " + conferenceNumber, true, e);
            fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor() {
                public void process(FmeBridge fmeBridge) {
                    cospaceService.syncCoSpaces(fmeBridge);
                }
            });
        }
        return coSpace;
    }

}

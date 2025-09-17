package com.paradisecloud.fcm.web.controller.mcu.all;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
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
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.attendee.InvitedAttendeeSmc2;
import com.paradisecloud.fcm.web.model.smc.CospaceRequest;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.ParticipantReqDto;
import com.paradisecloud.smc3.model.mix.CreateParticipantsReq;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import com.paradisecloud.smc3.monitor.SmcWebMonitorStopThread;
import com.sinhy.utils.ThreadUtils;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.SiteAccessInfoEx;
import com.suntek.smc.esdk.pojo.local.SiteInfoEx;
import com.suntek.smc.esdk.pojo.local.TPSDKResponseEx;
import com.suntek.smc.esdk.pojo.local.WSCtrlSiteCommParamEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nj
 * @date 2023/4/6 10:20
 */
@RestController
@RequestMapping("/busi/mcu/all/conferencMonitor")
@Tag(name = "WEB会监")
public class BusiMcuSmcCospaceController extends BaseController {


    public static final int C_INT = 100;
    @Resource
    private ICoSpaceService cospaceService;


    @Resource
    private ICallService callService;

    /**
     * 关闭会监
     */
    @PostMapping(value = "/cospace/close")
    @Operation(summary = "关闭会监")
    public RestResponse deleteCospace(@RequestBody CospaceRequest cospaceRequest) {

        String conferenceId = cospaceRequest.getConferenceId();
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        FmeBridge fmeBridge;
        switch (mcuType) {
            case SMC3:
                Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
                conferenceId = conferenceContext.getSmc3conferenceId();
                Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
                String cospaceId = conferenceContext.getCospaceId();
                Long deptId = conferenceContext.getDeptId();
                fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
                if (fmeBridge == null) {
                    throw new CustomException("无法关闭会监,FME失去连接");
                }
                deleteCospace(fmeBridge, cospaceId);

                try {

                    String res = bridge.getSmcParticipantsInvoker().getConferencesParticipantsState(conferenceId, 0, 1000, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

                    SmcParitipantsStateRep conferencesParticipantsState = JSON.parseObject(res, SmcParitipantsStateRep.class);
                    if (conferencesParticipantsState != null) {
                        if (!CollectionUtils.isEmpty(conferencesParticipantsState.getContent())) {
                            Optional<SmcParitipantsStateRep.ContentDTO> first = conferencesParticipantsState.getContent().stream().filter(p -> p.getGeneralParam().getUri().startsWith(conferenceContext.getMonitorNumber())).findFirst();
                            if (first.isPresent()) {
                                String participantId = first.get().getGeneralParam().getId();
                                List<String> participantIds = new ArrayList<>();
                                participantIds.add(participantId);
                                bridge.getSmcParticipantsInvoker().deleteParticipants(conferenceId, participantIds, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                conferenceContext.setMonitorNumber(null);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.SMC_MONITOR_NUMBER_REMOVE, cospaceRequest.getMonitorNumber());
                return RestResponse.success();
            case SMC2:

                Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
                fmeBridge = BridgeUtils.getAvailableFmeBridge(smc2ConferenceContext.getDeptId());
                if (fmeBridge == null) {
                    throw new CustomException("无法关闭会监,FME失去连接");
                }
                CoSpace coSpace = (CoSpace) smc2ConferenceContext.getMonitorParticipantMap().get(smc2ConferenceContext.getSmc2conferenceId());
                smc2ConferenceContext.setMonitorNumber(null);
                if (smc2ConferenceContext != null) {

                    smc2ConferenceContext.setMonitorNumber(null);
                    smc2ConferenceContext.getDetailConference().setMonitorNumber(null);
                    smc2ConferenceContext.getMonitorParticipantMap().remove(conferenceId);
                    smc2ConferenceContext.setMonitorNumber(null);
                    String uri = coSpace.getUri();
                    ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                    Integer resultCode = conferenceServiceEx.delSiteFromConfEx(smc2ConferenceContext.getSmc2conferenceId(), uri, null);
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.SMC_MONITOR_NUMBER_REMOVE, cospaceRequest.getMonitorNumber());
                }
                deleteCospace(fmeBridge, coSpace.getId());
                return RestResponse.success();
            default:
                return RestResponse.success();
        }


    }

    private void deleteCospace(FmeBridge fmeBridge, String cospaceId) {
        try {
            fmeBridge.getCoSpaceInvoker().deleteCoSpace(cospaceId);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("recoveryCospace fail: ", e);
        }

        fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor() {
            @Override
            public void process(FmeBridge fmeBridge) {
                fmeBridge.getDataCache().deleteCoSpace(cospaceId);
            }
        });
    }

    /**
     * 获取会议号码
     */
    @PostMapping(value = "/cospace")
    @Operation(summary = "获取会议号码记")
    public synchronized RestResponse getInfo(@RequestBody CospaceRequest cospaceRequest) {

        String conferenceId = cospaceRequest.getConferenceId();
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);

        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();

        FmeBridge fmeBridge;
        String subject = cospaceRequest.getSubject();
        String accessCode = cospaceRequest.getAccessCode();
        CoSpace coSpace = null;
        long cn = System.currentTimeMillis();
        CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
        Call call;
        AtomicInteger count = new AtomicInteger();
        String number;
        String uri;
        switch (mcuType) {
            case SMC3:
                Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
                conferenceId = conferenceContext.getSmc3conferenceId();
                Smc3Bridge bridge = conferenceContext.getSmc3Bridge();

                fmeBridge = BridgeUtils.getAvailableFmeBridge(conferenceContext.getDeptId());
                if (fmeBridge == null) {
                    throw new CustomException("无法开启会监,无可用FME");
                }

                String monitorNumber = conferenceContext.getMonitorNumber();
                if (monitorNumber != null) {
                    return RestResponse.success(monitorNumber);
                }
                //创建COSPACE
                coSpace = getCoSpaceByConferenceNumber(fmeBridge, "7" + cn);
                if (coSpace == null) {
                    return RestResponse.fail();
                }

                call = callService.createCall(fmeBridge, "7" + cn, "会议监控");
                coSpaceParamBuilder.name(ConstAPI.SMC3_MONITOR);
                coSpaceParamBuilder.panePlacementHighestImportance(100);
                coSpaceParamBuilder.defaultLayout("speakerOnly");

                cospaceService.updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);
                number = coSpace.getUri();
                SmcWebMonitorStopThread.setCallID.add(number);
                uri = number + "@" + fmeBridge.getBusiFme().getIp() + ":5060";
                CreateParticipantsReq createParticipantsReq = new CreateParticipantsReq();
                createParticipantsReq.setConferenceId(conferenceId);
                List<ParticipantReqDto> participants = new ArrayList<>(1);
                ParticipantReqDto participantReqDto = new ParticipantReqDto();
                participantReqDto.setName("会议监控");
                participantReqDto.setUri(uri);
                participantReqDto.setIpProtocolType("SIP");
                if (Strings.isNotBlank(conferenceContext.getGuestPassword())) {
                    participantReqDto.setDtmfInfo(conferenceContext.getGuestPassword());
                }
                participants.add(participantReqDto);
                createParticipantsReq.setParticipants(participants);
                bridge.getSmcParticipantsInvoker().createParticipants(createParticipantsReq.getConferenceId(), createParticipantsReq.getParticipants(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                try {
                    String region = ExternalConfigCache.getInstance().getRegion();
                    if ("ops".equalsIgnoreCase(region)) {
                        Thread.sleep(2000);
                    } else {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //查找到这个与会者并设置为权重100,
                Participant fmeParticipant;
                while (count.getAndIncrement() < C_INT) {
                    try {
                        ParticipantsResponse participantsResponse = fmeBridge.getCallInvoker().getParticipants(call.getId(), 0);
                        if (participantsResponse != null) {
                            ActiveParticipantsResponse participantsResponseParticipants = participantsResponse.getParticipants();
                            if (participantsResponseParticipants != null) {
                                ArrayList<Participant> participant = participantsResponseParticipants.getParticipant();
                                if (!CollectionUtils.isEmpty(participant)) {
                                    Optional<Participant> first = participant.stream().filter(p -> {
                                        return Objects.equals(p.getName(), subject) || Objects.equals(p.getName(), accessCode);
                                    }).findFirst();
                                    if (first.isPresent()) {
                                        fmeParticipant = first.get();
                                        fmeBridge.getParticipantInvoker().updateParticipant(fmeParticipant.getId(), new ParticipantParamBuilder().importance(100).build());
                                        CallLeg callLeg = getCallLegByParticipant(fmeBridge, fmeParticipant);
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

                conferenceContext.setCospaceId(coSpace.getId());
                conferenceContext.setMonitorNumber(number);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.SMC_MONITOR_NUMBER_ADD, number);
                return RestResponse.success(coSpace);
            case SMC2:
                Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
                cn = System.currentTimeMillis();
                fmeBridge = BridgeUtils.getAvailableFmeBridge(smc2ConferenceContext.getDeptId());
                if (fmeBridge == null) {
                    throw new CustomException("无法开启会监,无可用FME");
                }

                if (smc2ConferenceContext != null) {
                    Object o = smc2ConferenceContext.getMonitorParticipantMap().get(conferenceId);
                    if (o != null) {
                        return RestResponse.success(o);
                    }
                } else {
                    Object o = SmcConferenceContextCache.getInstance().getMonitorParticipantMap().get(conferenceId);
                    if (o != null) {
                        return RestResponse.success(o);
                    }
                }

                //创建COSPACE
                coSpace = getCoSpaceByConferenceNumber(fmeBridge, "7" + cn);
                if (coSpace == null) {
                    return RestResponse.fail();
                }

                coSpaceParamBuilder.name("SMC-MONITOR");
                coSpaceParamBuilder.panePlacementHighestImportance(100);
                coSpaceParamBuilder.defaultLayout("speakerOnly");
                cospaceService.updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);

                call = callService.createCall(fmeBridge, "7" + cn, "会议监控");

                number = coSpace.getUri();
                uri = number + "@" + fmeBridge.getBusiFme().getIp();
                if (smc2ConferenceContext != null) {

                    String confId = smc2ConferenceContext.getSmc2conferenceId();
                    SiteInfoEx siteInfo = new SiteInfoEx();
                    siteInfo.setUri(uri);
                    siteInfo.setType(7);
                    siteInfo.setName("会议监控");
                    siteInfo.setDtmf(smc2ConferenceContext.getConferencePassword()+"#");
                    ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                    TPSDKResponseEx<List<SiteAccessInfoEx>> result = conferenceServiceEx.addSiteToConfEx(confId, siteInfo, null);
                    //锁定视频源
                    List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams = new ArrayList<>();
                    WSCtrlSiteCommParamEx item1 = new WSCtrlSiteCommParamEx();
                    //锁定
                    item1.setOperaTypeParam(1);
                    item1.setSiteUri(uri);
                    wsCtrlSiteCommParams.add(item1);
                    conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams);
                    List list = new ArrayList<>();
                    list.add(uri);
                    conferenceServiceEx.setSitesMuteEx(confId, list, 1);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //查找到这个与会者并设置为权重100,
                setWeight(fmeBridge, subject, accessCode, call, count);


                smc2ConferenceContext.getMonitorParticipantMap().put(smc2ConferenceContext.getSmc2conferenceId(), coSpace);
                smc2ConferenceContext.setMonitorNumber("7" + cn);
                smc2ConferenceContext.getDetailConference().setMonitorNumber("7" + cn);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, WebsocketMessageType.SMC_MONITOR_NUMBER_ADD, number);

                return RestResponse.success(coSpace);
            default:
                return RestResponse.success();
        }


    }

    private void setWeight(FmeBridge fmeBridge, String subject, String accessCode, Call call, AtomicInteger count) {
        if (call != null) {
            Participant smcParticipant = null;
            while (true) {
                try {
                    if (count.incrementAndGet() > 100) {
                        break;
                    }
                    logger.info("会监权重修改：=》");
                    ParticipantsResponse participantsResponse = fmeBridge.getCallInvoker().getParticipants(call.getId(), 0);
                    if (participantsResponse != null) {
                        ActiveParticipantsResponse participantsResponseParticipants = participantsResponse.getParticipants();
                        if (participantsResponseParticipants != null) {
                            ArrayList<Participant> participant = participantsResponseParticipants.getParticipant();
                            if (!CollectionUtils.isEmpty(participant)) {
                                Optional<Participant> first = participant.stream().filter(p -> {
                                    return Objects.equals(p.getName(), subject) || Objects.equals(p.getName(), accessCode);
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
        }
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
                        @Override
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
                @Override
                public void process(FmeBridge fmeBridge) {
                    cospaceService.syncCoSpaces(fmeBridge);
                }
            });
        }
        return coSpace;
    }

}

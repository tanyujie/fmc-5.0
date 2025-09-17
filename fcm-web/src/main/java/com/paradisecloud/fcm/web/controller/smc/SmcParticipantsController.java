package com.paradisecloud.fcm.web.controller.smc;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.com.fcm.smc.modle.mix.ConferenceControllerRequest;
import com.paradisecloud.com.fcm.smc.modle.mix.CreateParticipantsReq;
import com.paradisecloud.com.fcm.smc.modle.request.ConferenceStatusRequest;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.com.fcm.smc.modle.response.VideoSourceRep;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.dao.model.BusiFsbcServerDept;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.smc.cache.modle.*;
import com.paradisecloud.fcm.smc.cache.modle.ConferenceState;
import com.paradisecloud.fcm.telep.cache.TeleBridgeCache;
import com.paradisecloud.fcm.telep.cache.TeleParticipantCache;
import com.paradisecloud.fcm.telep.cache.TelepBridge;
import com.paradisecloud.fcm.telep.dao.model.BusiTeleParticipant;
import com.paradisecloud.fcm.telep.model.busi.participants.TeleParticipant;
import com.paradisecloud.fcm.telep.model.busi.participants.VideoToUse;
import com.paradisecloud.fcm.telep.model.request.EnumerateFilter;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiParaticipantsService;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiTeleConferenceService;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiTeleParticipantService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.smc.dao.model.BusiSmcConferenceState;
import com.paradisecloud.smc.dao.model.BusiSmcTemplateConference;
import com.paradisecloud.smc.dao.model.SmcTemplateTerminal;
import com.paradisecloud.smc.service.*;
import com.paradisecloud.smc.service.busi.ConferenceSMCService;
import com.sinhy.spring.BeanFactory;
import io.jsonwebtoken.lang.Strings;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

/**
 * @author nj
 * @date 2022/9/23 11:02
 */
@RestController
@RequestMapping("/smc/participants")
public class SmcParticipantsController extends BaseController {

    @Resource
    private IBusiSmcConferenceStateService conferenceStateService;

    @Resource
    private SmcConferenceService smcConferenceService;
    @Resource
    private SmcParticipantsService smcParticipantsService;
    @Resource
    private ConferenceSMCService conferenceSMCService;

    @Resource
    private IBusiTerminalService iBusiTerminalService;

    @Resource
    private IBusiTeleParticipantService busiTeleParticipantService;
    @Resource
    private IBusiSmcTemplateConferenceService busiSmcTemplateConferenceService;

    @Resource
    private SmcTemplateTerminalService smcTemplateTerminalService;
    @Resource
    private IBusiTeleConferenceService teleConferenceService;

    @Resource
    private IBusiTeleConferenceService busiTeleConferenceService;

    /**
     * 参会者重呼
     */
    @PostMapping("/recall")
    @Operation(summary = "参会者重呼")
    public RestResponse recall(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {

        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();
        ParticipantStatus participantStatus = new ParticipantStatus();
        participantStatus.setIsOnline(true);
        participantStatus.setId(participantId);
        smcParticipantsService.changeParticipantStatusOnly(conferenceId, participantId, participantStatus);

        return success();
    }

    /**
     * URI邀请与会者
     *
     * @param createParticipantsReq
     */
    @PostMapping("/add/participants")
    @Operation(summary = "邀请与会者")
    public RestResponse addParticipants(@RequestBody CreateParticipantsReq createParticipantsReq) {
        smcParticipantsService.addParticipants(createParticipantsReq);
        return RestResponse.success();
    }

    /**
     * 批量邀请与会者
     *
     * @param createParticipantsReq
     */
    @PostMapping("/batchInvite/participants")
    @Operation(summary = "邀请与会者")
    public RestResponse batchInviteParticipants(@RequestBody CreateParticipantsReq createParticipantsReq) {
        List<Long> terminalIds = createParticipantsReq.getTerminalIds();
        List<ParticipantReqDto> participants=new ArrayList<>();
        Map<String, Object> terminalURiIdMap = SmcConferenceContextCache.getInstance().getTerminalURiIdMap();
        if (!CollectionUtils.isEmpty(terminalIds)) {
            for (Long id : terminalIds) {
                BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(id);
                if (!Objects.isNull(busiTerminal)) {
                    ParticipantReqDto participantRspDto = new ParticipantReqDto();
                    String number = busiTerminal.getNumber();
                    participantRspDto.setName(busiTerminal.getName());
                    participantRspDto.setUri(number);
                    if (org.apache.commons.lang3.StringUtils.isBlank(number)) {
                        if(TerminalType.isCisco(busiTerminal.getType())){
                            participantRspDto.setUri(busiTerminal.getName()+"@"+busiTerminal.getIp());
                        }else {
                            participantRspDto.setUri(busiTerminal.getIp());
                        }
                    }
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(busiTerminal.getIp()) && Strings.hasText(busiTerminal.getNumber())) {
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                    }
                    if (TerminalType.isFSBC(busiTerminal.getType())) {
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(busiTerminal.getIp())) {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp()+":5060");
                        } else {
                            BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                            String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            Integer sipPort = fsbcBridge.getBusiFsbcRegistrationServer().getSipPort();
                            if(sipPort==null||sipPort==5060){
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp+":5060");
                            }else {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp+":"+sipPort );
                            }
                        }
                    }
                    if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                        BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(busiFreeSwitchDept.getServerId());
                        String callIp = fcmBridge.getBusiFreeSwitch().getIp();
                        Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                        if(callPort==null||callPort==5060){
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp+":5060");
                        }else {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp+":"+callPort );
                        }
                    }
                    if(TerminalType.isFmeTemplate(busiTerminal.getType())){
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp()+":5060");
                        BusiTemplateConferenceMapper templateConferenceMapper = BeanFactory.getBean(BusiTemplateConferenceMapper.class);
                        BusiTemplateConference templateConference = new BusiTemplateConference();
                        templateConference.setConferenceNumber(Long.valueOf(busiTerminal.getNumber()));
                        List<BusiTemplateConference> templateConferences = templateConferenceMapper.selectBusiTemplateConferenceList(templateConference);
                        if(!CollectionUtils.isEmpty(templateConferences)){
                            String conferencePassword = templateConferences.get(0).getConferencePassword();
                            if(Strings.hasText(conferencePassword)){
                                participantRspDto.setDtmfInfo(conferencePassword);
                            }
                        }
                    }
                    terminalURiIdMap.put(participantRspDto.getUri(),busiTerminal);
                    participants.add(participantRspDto);
                }
            }
        }
        createParticipantsReq.setParticipants(participants);
        smcParticipantsService.addParticipants(createParticipantsReq);
       // new  Thread(()->subcribe(createParticipantsReq.getConferenceId())).start();



        return RestResponse.success();
    }


    /**
     * 挂断
     *
     * @param conferenceControllerRequest
     */
    @PutMapping("/hangup")
    @Operation(summary = "挂断")
    public RestResponse hangUpParticipants(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();

        ParticipantStatus participantStatus = new ParticipantStatus();
        participantStatus.setIsOnline(false);
        participantStatus.setId(participantId);
        smcParticipantsService.changeParticipantStatusOnly(conferenceId, participantId, participantStatus);
        conferenceSMCService.cancelChoose(conferenceId,participantId,null);
        return RestResponse.success();
    }

    /**
     * 移除与会者
     *
     * @param conferenceControllerRequest
     */
    @DeleteMapping("/delete")
    @Operation(summary = "移除与会者")
    public RestResponse delete(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();
        List<String> participantIds = new ArrayList<>();
        participantIds.add(participantId);
        smcParticipantsService.delete(conferenceId, participantIds);
        return RestResponse.success();
    }


    /**
     * 摄像头开启
     *
     * @param conferenceControllerRequest
     */
    @PatchMapping("/videoMuteOpenOne")
    @Operation(summary = "摄像头开启")
    public RestResponse setVideoMuteOPenOne(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();


        List< ParticipantStatusDto > participantStatusList=new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(participantId);
        participantStatusDto.setIsVideoMute(false);
        participantStatusList.add(participantStatusDto);
        smcParticipantsService.PATCHParticipantsOnly(conferenceId,participantStatusList);

        return RestResponse.success();
    }

    /**
     * 摄像头关闭
     *
     * @param conferenceControllerRequest
     */
    @PatchMapping("/videoMuteCloseOne")
    @Operation(summary = "摄像头关闭")
    public RestResponse videoMuteCloseOne(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {

        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();


        List< ParticipantStatusDto > participantStatusList=new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(participantId);
        participantStatusDto.setIsVideoMute(true);
        participantStatusList.add(participantStatusDto);
        smcParticipantsService.PATCHParticipantsOnly(conferenceId,participantStatusList);

        return RestResponse.success();
    }

    /**
     * 麦克风 静音
     *
     * @param conferenceControllerRequest
     */
    @PatchMapping("/muteCloseOne")
    @Operation(summary = "麦克风 静音")
    public RestResponse muteCloseOne(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {

        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();


        muteTrue(conferenceId, participantId);

        return RestResponse.success();
    }

    /**
     * 麦克风 打开
     *
     * @param conferenceControllerRequest
     */
    @PatchMapping("/muteOpenOne")
    @Operation(summary = "麦克风 打开")
    public RestResponse muteOpenOne(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();


        List< ParticipantStatusDto > participantStatusList=new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(participantId);
        participantStatusDto.setIsMute(false);
        participantStatusList.add(participantStatusDto);
        smcParticipantsService.PATCHParticipantsOnly(conferenceId,participantStatusList);

        return RestResponse.success();
    }

    /**
     * 打开扬声器
     *
     * @param conferenceControllerRequest
     */
    @PatchMapping("/quietOpenOne")
    @Operation(summary = "打开扬声器")
    public RestResponse quietOpenOne(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();

        List< ParticipantStatusDto > participantStatusList=new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(participantId);
        participantStatusDto.setIsQuiet(false);
        participantStatusList.add(participantStatusDto);
        smcParticipantsService.PATCHParticipantsOnly(conferenceId,participantStatusList);

        return RestResponse.success();
    }

    /**
     * 关闭扬声器
     *
     * @param conferenceControllerRequest
     */
    @PatchMapping("/quietCloseOne")
    @Operation(summary = "关闭扬声器")
    public RestResponse quietCloseOne(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();

        List< ParticipantStatusDto > participantStatusList=new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(participantId);
        participantStatusDto.setIsQuiet(true);
        participantStatusList.add(participantStatusDto);
        smcParticipantsService.PATCHParticipantsOnly(conferenceId,participantStatusList);

        return RestResponse.success();
    }




    /**
     * 点名
     */
    @PostMapping("/callTheRoll")
    @Operation(summary = "点名")
    public RestResponse callTheRoll(@Valid @RequestBody ConferenceControllerRequest callTheRollRequest) {
        String participantId = callTheRollRequest.getParticipantId();
        String conferenceId = callTheRollRequest.getConferenceId();

        DetailConference detailConference = smcConferenceService.getDetailConferenceInfoById(conferenceId);
        String accessCode = detailConference.getConferenceUiParam().getAccessCode();

        Map<String, String> uriMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(conferenceId);
        if(!CollectionUtils.isEmpty(uriMap)) {
            for (Map.Entry entry : uriMap.entrySet()) {
                if (!Objects.equals(entry.getKey(), participantId)) {
                    String uri = (String) entry.getValue();
                    String[] split = uri.split("@");
                    busiTeleConferenceService.settingConference(split[0], split[1], accessCode);
                    BusiTeleParticipant busiTeleParticipant = new BusiTeleParticipant();
                    busiTeleParticipant.setConferenceNumber(split[0]);
                    List<BusiTeleParticipant> all = busiTeleParticipantService.selectBusiTeleParticipantList(busiTeleParticipant);
                    if (!CollectionUtils.isEmpty(all)) {
                        all.forEach(s -> busiTeleParticipantService.deleteBusiTeleParticipantById(s.getId()));
                    }
                    TeleParticipantCache.getInstance().removeAll();
                }
            }
        }


        conferenceSMCService.callTheRoll(conferenceId,participantId);


        Map<String, TelepBridge> ipToTeleBridgeMap = TeleBridgeCache.getInstance().getIpToTeleBridgeMap();
        if (CollectionUtils.isEmpty(ipToTeleBridgeMap)) {
            return RestResponse.success();
        }
        BusiSmcTemplateConference query = new BusiSmcTemplateConference();
        query.setConferenceId(conferenceId);
        List<BusiSmcTemplateConference> busiSmcTemplateConferences = busiSmcTemplateConferenceService.selectBusiSmcTemplateConferenceList(query);
        if (CollectionUtils.isEmpty(busiSmcTemplateConferences)) {
            return RestResponse.success();
        }
        String smcTemplateId = busiSmcTemplateConferences.get(0).getSmcTemplateId();
        List<SmcTemplateTerminal> templateTerminals = smcTemplateTerminalService.list(smcTemplateId);
        FcmThreadPool.exec(() -> {
            //级联处理
            if (!CollectionUtils.isEmpty(templateTerminals)) {
                templateTerminals.stream().forEach(s -> {
                    BusiTerminal terminal = TerminalCache.getInstance().get(s.getTerminalId());
                    List<TeleParticipant> list = iBusiParaticipantsService.getList(terminal.getNumber(), terminal.getIp(), EnumerateFilter.CONNECTED);
                    if (!CollectionUtils.isEmpty(list)) {
                        Optional<TeleParticipant> first = list.stream().filter(sc -> Objects.equals(sc.getCallState(), "connected") && Objects.equals(sc.getDisplayName(), ConstAPI.MCU_SMC_ID)).findFirst();
                        for (TeleParticipant p : list) {
                            if (Objects.equals(p.getCallState(), "connected")) {
                                if (!Objects.equals(p.getDisplayName(), first.get().getDisplayName())) {
                                    p.setFocusType("participant");
                                    VideoToUse videoToUse = new VideoToUse();
                                    videoToUse.setParticipantProtocol(first.get().getParticipantProtocol());
                                    videoToUse.setParticipantName(first.get().getParticipantName());
                                    videoToUse.setParticipantType(first.get().getParticipantType());
                                    p.setFocusParticipant(videoToUse);
                                    p.setAudioRxMuted(true);
                                } else {
                                    p.setAudioRxMuted(false);
                                }
                                try {
                                    iBusiParaticipantsService.participantModify(terminal.getIp(), p);
                                    TeleParticipantCache.getInstance().remove(terminal.getNumber() + p.getParticipantName());
                                    busiTeleParticipantService.deleteBusiTeleParticipantByConferenceNumber(terminal.getNumber());
                                    TeleParticipantCache.getInstance().remove(terminal.getNumber() + p.getParticipantName());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        });
        try {
            TeleParticipantCache.getInstance().removeAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return RestResponse.success();
    }

    /**
     * 取消点名
     */
    @PostMapping("/cancleCallTheRoll")
    @Operation(summary = "取消点名")
    public RestResponse cancelCallTheRoll(@Valid @RequestBody ConferenceControllerRequest callTheRollRequest) {
        String conferenceId = callTheRollRequest.getConferenceId();
        String participantId = callTheRollRequest.getParticipantId();
        conferenceSMCService.cancelCallTheRoll(conferenceId, participantId, null);
        return RestResponse.success();
    }




    /**
     * 选看
     */
    @PatchMapping("/choose")
    @Operation(summary = "选看")
    public RestResponse choose(@Valid @RequestBody ConferenceControllerRequest callTheRollRequest) {
        String conferenceId1 = callTheRollRequest.getConferenceId();
        String participantId = callTheRollRequest.getParticipantId();
        DetailConference detailConference = smcConferenceService.getDetailConferenceInfoById(conferenceId1);
        String accessCode = detailConference.getConferenceUiParam().getAccessCode();
        Map<String, String> uriMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(conferenceId1);
        if(!CollectionUtils.isEmpty(uriMap)){
            for (Map.Entry entry : uriMap.entrySet()) {
                if(!Objects.equals(entry.getKey(),participantId)){
                    String value = (String)entry.getValue();
                    String[] split = value.split("@");
                    BusiTeleParticipant busiTeleParticipant = new BusiTeleParticipant();
                    busiTeleParticipant.setConferenceNumber(split[0]);
                    List<BusiTeleParticipant> all = busiTeleParticipantService.selectBusiTeleParticipantList(busiTeleParticipant);
                    if (!CollectionUtils.isEmpty(all)) {
                        all.forEach(s -> busiTeleParticipantService.deleteBusiTeleParticipantById(s.getId()));
                    }
                    TeleParticipantCache.getInstance().removeAll();
                }
            }
        }


        conferenceSMCService.choose(conferenceId1, participantId);

        return RestResponse.success();
    }

    private void muteTrue(String conferenceId, String broadcastId) {
        List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(broadcastId);
        participantStatusDto.setIsMute(true);
        participantStatusList.add(participantStatusDto);
        smcParticipantsService.PATCHParticipantsOnly(conferenceId, participantStatusList);
    }

    private void muteFalse(String conferenceId, String broadcastId) {
        List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(broadcastId);
        participantStatusDto.setIsMute(false);
        participantStatusList.add(participantStatusDto);
        smcParticipantsService.PATCHParticipantsOnly(conferenceId, participantStatusList);
    }

    /**
     * 取消选看
     */
    @PatchMapping("/cancelChoose")
    @Operation(summary = "取消选看")
    public RestResponse cancelChoose(@Valid @RequestBody ConferenceControllerRequest callTheRollRequest) {

        conferenceSMCService.cancelChoose(callTheRollRequest.getConferenceId(), callTheRollRequest.getParticipantId(), null);
        return RestResponse.success();
    }


    /**
     * 广播
     */
    @PatchMapping("/broadcaster")
    @Operation(summary = "广播")
    public RestResponse broadcaster(@Valid @RequestBody ConferenceControllerRequest callTheRollRequest) {
        String conferenceId = callTheRollRequest.getConferenceId();
        String participantId = callTheRollRequest.getParticipantId();
        broadcaster(conferenceId, participantId);
        return RestResponse.success();
    }

    private void broadcaster(String conferenceId, String participantId) {
        ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
        conferenceStatusRequest.setBroadcaster(participantId);
        smcConferenceService.statusControl(conferenceId, conferenceStatusRequest);
    }

    /**
     * 取消广播
     */
    @PatchMapping("/cancleBroadcaster")
    @Operation(summary = "取消广播")
    public RestResponse cancelBroadcaster(@Valid @RequestBody ConferenceControllerRequest callTheRollRequest) {
        String conferenceId = callTheRollRequest.getConferenceId();
        cancelBroadcaster(conferenceId);
        return RestResponse.success();
    }

    private void cancelBroadcaster(String conferenceId) {
        broadcaster(conferenceId, "");
    }

    @Resource
    private IBusiParaticipantsService iBusiParaticipantsService;

    /**
     * 设置主席
     */
    @PatchMapping("/chairman")
    @Operation(summary = "设置主席")
    public RestResponse chairman(@Valid @RequestBody ConferenceControllerRequest callTheRollRequest) {
        String conferenceId = callTheRollRequest.getConferenceId();
        String participantId = callTheRollRequest.getParticipantId();

        //锁定视频源
        ParticipantStatus participantStatus=new ParticipantStatus();
        participantStatus.setVideoSwitchAttribute("CUSTOMIZED");
        smcParticipantsService.changeParticipantStatusOnly(conferenceId,participantId,participantStatus);

        Map<String, ConferenceState> smcConferenceStateMap = SmcConferenceContextCache.getInstance().getSmcConferenceStateMap();
        ConferenceState conferenceState = smcConferenceStateMap.get(conferenceId);
        if(conferenceState!=null){
            String chairmanId = conferenceState.getState().getChairmanId();
            if(!StringUtils.isBlank(chairmanId)){
                if(!Objects.equals(chairmanId,participantId)){
                    muteTrue(conferenceId, chairmanId);
                    SmcConferenceContextCache.getInstance().getChairmanIdVideoSourceRepMap().remove(chairmanId);
                }
            }
            String spokesmanId = conferenceState.getState().getSpokesmanId();
            if(Strings.hasText(spokesmanId)){
                conferenceSMCService.cancelCallTheRoll(conferenceId,spokesmanId,null);
            }

        }


        ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
        conferenceStatusRequest.setChairman(participantId);
        smcConferenceService.statusControl(conferenceId, conferenceStatusRequest);


        broadcaster(conferenceId, participantId);
        smcConferenceService.setMute(conferenceId,true);


        String participantIdLast = SmcConferenceContextCache.getInstance().getChooseParticipantMap().get(conferenceId);
        conferenceSMCService.cancelChoose(conferenceId, participantIdLast, null);


        SmcParitipantsStateRep conferencesParticipantsState = smcParticipantsService.getConferencesParticipantsState(conferenceId, 0, 10);
        List<SmcParitipantsStateRep.ContentDTO> content = conferencesParticipantsState.getContent();
        String chooseId=participantId;
        for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
            String participantId1 = contentDTO.getState().getParticipantId();
            if(!Objects.equals(participantId1,participantId)){
                if(!Objects.equals("会议监控",contentDTO.getGeneralParam().getName())||!Objects.equals("SMC-MONITOR",contentDTO.getGeneralParam().getName())){
                    if(contentDTO.getState().getOnline()&&!Objects.equals(participantId1,participantId)){
                        chooseId=participantId1;
                        break;
                    }
                }
            }
        }
        ConferenceControllerRequest chooseRequest = new ConferenceControllerRequest();
        chooseRequest.setConferenceId(conferenceId);
        chooseRequest.setParticipantId(chooseId);
        smcConferenceService.statusControlchoose(participantId, chooseRequest);
        conferenceState.getState().setChooseId(chooseId);
        SmcConferenceContextCache.getInstance().getChooseParticipantMap().put(callTheRollRequest.getConferenceId(), chooseId);
        BusiSmcConferenceState busiSmcConferenceState = new BusiSmcConferenceState();
        busiSmcConferenceState.setConferenceId(conferenceId);

        List<BusiSmcConferenceState> busiSmcConferenceStates = conferenceStateService.selectBusiSmcConferenceStateList(busiSmcConferenceState);
        if(CollectionUtils.isEmpty(busiSmcConferenceStates)){
            busiSmcConferenceState.setCreateTime(new Date());
            busiSmcConferenceState.setChooseid(chooseId);
            conferenceStateService.insertBusiSmcConferenceState(busiSmcConferenceState);
        }else {
            busiSmcConferenceState.setChooseid(chooseId);
            conferenceStateService.updateBusiSmcConferenceState(busiSmcConferenceState);
        }



        //清除点名状态
        TeleParticipantCache.getInstance().removeAll();
        //锁定视频源
        participantStatus.setVideoSwitchAttribute("AUTO");
        smcParticipantsService.changeParticipantStatusOnly(conferenceId,participantId,participantStatus);

        List<String> chooseList = new ArrayList<>();
        chooseList.add(chooseId);
        SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CHOOSE_LIST, chooseList);

        muteFalse(conferenceId, participantId);
        return RestResponse.success();
    }


    /**
     * 取消主席
     */
    @PatchMapping("/cancleChairman")
    @Operation(summary = "取消主席")
    public RestResponse cancleChairman(@Valid @RequestBody ConferenceControllerRequest callTheRollRequest) {
        String conferenceId = callTheRollRequest.getConferenceId();
        ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
        conferenceStatusRequest.setChairman("");
        smcConferenceService.statusControl(conferenceId,conferenceStatusRequest);
        cancelBroadcaster(conferenceId);
        SmcConferenceContextCache.getInstance().getChooseParticipantMap().put(conferenceId, "");
        SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CHOOSE_LIST, new ArrayList<>());
        muteTrue(conferenceId,callTheRollRequest.getParticipantId());
        BusiSmcConferenceState busiSmcConferenceState = new BusiSmcConferenceState();
        busiSmcConferenceState.setConferenceId(conferenceId);
        List<BusiSmcConferenceState> busiSmcConferenceStates = conferenceStateService.selectBusiSmcConferenceStateList(busiSmcConferenceState);
        if(!CollectionUtils.isEmpty(busiSmcConferenceStates)){
            for (BusiSmcConferenceState smcConferenceState : busiSmcConferenceStates) {
                conferenceStateService.deleteBusiSmcConferenceStateById(smcConferenceState.getId());
            }
        }
        return RestResponse.success();
    }



    /**
     * 共享材料
     */
    @PatchMapping("/sharedMaterial")
    @Operation(summary = "共享材料")
    public RestResponse sharedMaterial(@Valid @RequestBody ConferenceControllerRequest callTheRollRequest) {
        String conferenceId = callTheRollRequest.getConferenceId();
        smcConferenceService.share(conferenceId,callTheRollRequest.getParticipantId());
        return RestResponse.success();
    }



    /**
     * 摄像机控制
     */
    @PostMapping("/camera")
    @Operation(summary = "摄像机控制")
    public RestResponse camera(@Valid @RequestBody ConferenceControllerRequest callTheRollRequest) {
        String conferenceId = callTheRollRequest.getConferenceId();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("number", callTheRollRequest.getNumber());
        jsonObject.put("operate",callTheRollRequest.getOperate());
        jsonObject.put("controlType",callTheRollRequest.getControlType());
        smcConferenceService.camera(conferenceId,callTheRollRequest.getParticipantId(), jsonObject);
        return RestResponse.success();
    }



    /**
     * 修改会场名称
     */
    @PutMapping("/changeName")
    @Operation(summary = "修改会场名称")
    public RestResponse changeName( @RequestBody ConferenceControllerRequest callTheRollRequest) {
        String conferenceId = callTheRollRequest.getConferenceId();
        JSONObject jsonObject = new JSONObject();
        JSONObject js = new JSONObject();
        js.put("id",callTheRollRequest.getParticipantId());
        js.put("name",callTheRollRequest.getName());
        jsonObject.put("participantNameInfo",js);
        smcConferenceService.changeName(conferenceId,jsonObject);
        return RestResponse.success();
    }


    /**
     *  会场点名前提示
     * remind
     */
    @PostMapping("/remind")
    @Operation(summary = "remind")
    public RestResponse remind( @RequestBody ConferenceControllerRequest callTheRollRequest) {
        String conferenceId = callTheRollRequest.getConferenceId();
        String participantId = callTheRollRequest.getParticipantId();
        smcConferenceService.remind(conferenceId,participantId);
        return RestResponse.success();
    }



    /**
     * 锁定视频源
     *
     * @param conferenceControllerRequest
     */
    @PatchMapping("/videoSwitchAttribute/lock")
    @Operation(summary = "锁定视频源")
    public RestResponse videoSwitchAttribute(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();

        ParticipantStatus participantStatus=new ParticipantStatus();
        participantStatus.setVideoSwitchAttribute("CUSTOMIZED");
        smcParticipantsService.changeParticipantStatusOnly(conferenceId,participantId,participantStatus);

        return RestResponse.success();
    }

    /**
     * 解锁视频源
     *
     * @param conferenceControllerRequest
     */
    @PatchMapping("/videoSwitchAttribute/unLock")
    @Operation(summary = "解锁视频源")
    public RestResponse videoSwitchAttributeAUTO(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();

        ParticipantStatus participantStatus=new ParticipantStatus();
        participantStatus.setVideoSwitchAttribute("AUTO");
        smcParticipantsService.changeParticipantStatusOnly(conferenceId,participantId,participantStatus);
        return RestResponse.success();
    }


    /**
     * 音量设置
     *
     * @param conferenceControllerRequest
     */
    @PatchMapping("/volume")
    @Operation(summary = "音量设置")
    public RestResponse volume(@RequestBody ConferenceControllerRequest conferenceControllerRequest) {
        String participantId = conferenceControllerRequest.getParticipantId();
        String conferenceId = conferenceControllerRequest.getConferenceId();
        int volume = conferenceControllerRequest.getVolume();
        ParticipantStatus participantStatus=new ParticipantStatus();
        participantStatus.setVolume(volume);
        smcParticipantsService.changeParticipantStatusOnly(conferenceId,participantId,participantStatus);
        return RestResponse.success();
    }


    /**
     * 锁定会议材料(取消)
     */
    @PatchMapping("/lockPresenter/{conferenceId}/{participantId}/{lock}")
    @Operation(summary = "锁定会议材料")
    public RestResponse lockPresenter(@PathVariable String conferenceId,@PathVariable String participantId,@PathVariable Boolean lock) {
        smcConferenceService.lockPresenterParticipant(conferenceId,participantId,lock);
        return RestResponse.success();
    }

    /**
     * 获取当前videoSource
     * @param conferenceControllerRequest
     * @return
     */
    @PostMapping("/videoSource")
    @Operation(summary = "获取当前videoSource")
    public RestResponse getVideoSource(@RequestBody ConferenceControllerRequest conferenceControllerRequest){
        List<String> particiPantIds = new ArrayList<>();
        particiPantIds.add(conferenceControllerRequest.getParticipantId());
        List<VideoSourceRep> chairmanIdvideoSourceReps = smcConferenceService.conferencesVideoSource(conferenceControllerRequest.getConferenceId(),particiPantIds);
        return RestResponse.success(chairmanIdvideoSourceReps);
    }

}



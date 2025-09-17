package com.paradisecloud.fcm.web.controller.smc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.com.fcm.smc.modle.request.*;
import com.paradisecloud.com.fcm.smc.modle.response.LogsConferenceRep;
import com.paradisecloud.com.fcm.smc.modle.response.ParticipantOrderRep;
import com.paradisecloud.com.fcm.smc.modle.response.SmcErrorResponse;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.mqtt.cache.MqttBridgeCache;
import com.paradisecloud.fcm.mqtt.cache.MqttClusterCache;
import com.paradisecloud.fcm.mqtt.cache.MqttDeptMappingCache;
import com.paradisecloud.fcm.mqtt.enums.MqttType;
import com.paradisecloud.fcm.mqtt.model.MqttBridge;
import com.paradisecloud.fcm.mqtt.model.MqttBridgeCluster;
import com.paradisecloud.fcm.web.service.interfaces.IWelcomeService;
import com.paradisecloud.fcm.smc.cache.modle.ConferenceLogConfigCache;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.fcm.smc.cache.modle.SmcConferenceContextCache;
import com.paradisecloud.fcm.telep.cache.TeleBridgeCache;
import com.paradisecloud.fcm.telep.cache.TelepBridge;
import com.paradisecloud.fcm.telep.model.busi.participants.TeleParticipant;
import com.paradisecloud.fcm.telep.model.request.EnumerateFilter;
import com.paradisecloud.fcm.telep.model.request.ParticipantMessage;
import com.paradisecloud.fcm.telep.model.request.VerticalPosition;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiParaticipantsService;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiTeleConferenceService;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiTeleParticipantService;
import com.paradisecloud.fcm.terminal.fs.cache.FcmAccountCacheAndUtils;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.fcm.web.model.smc.ParticipantFontSetting;
import com.paradisecloud.smc.dao.model.*;
import com.paradisecloud.smc.service.*;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author nj
 * @date 2022/8/16 15:32
 */
@Slf4j
@RestController
@RequestMapping("/smc/conference")
public class SmcConferenceController {

    @Resource
    private SmcConferenceService smcConferenceService;

    @Resource
    private SmcParticipantsService smcParticipantsService;

    @Resource
    private IBusiSmcTemplateConferenceService busiSmcTemplateConferenceService;

    @Resource
    private SmcTemplateTerminalService smcTemplateTerminalService;

    @Resource
    private IBusiParaticipantsService paraticipantsService;


    @Resource
    private IBusiSmcMulitpicService busiSmcMulitpicService;

    @Resource
    private IBusiTerminalService iBusiTerminalService;


    @Resource
    private IBusiTeleParticipantService busiTeleParticipantService;

    @Resource
    private IBusiTeleConferenceService busiTeleConferenceService;

    @Resource
    private IBusiTeleConferenceService teleConferenceService;
    @Resource
    private IBusiParaticipantsService iBusiParaticipantsService;


    @Resource
    private IBusiSmcAppointmentConferencePaticipantService appointmentPaticipantService;

    @Autowired
    private IWelcomeService welcomeService;
    /**
     * ID找到会议基本信息
     *
     * @param conferenceId
     * @return
     */
    @GetMapping("/info/{conferenceId}")
    public RestResponse getConferencesInfo(@PathVariable String conferenceId) {

        String smcConferenceInfoById = smcConferenceService.getSmcConferenceInfoById(conferenceId);
        if (StringUtils.isNotBlank(smcConferenceInfoById) && smcConferenceInfoById.contains(ConstAPI.ERRORNO)) {
            SmcErrorResponse smcErrorResponse = JSON.parseObject(smcConferenceInfoById, SmcErrorResponse.class);
            throw new CustomException("会议信息查询错误"+smcErrorResponse.getErrorDesc());
        }
        SmcConferenceContext smcConferenceContext = JSON.parseObject(smcConferenceInfoById, SmcConferenceContext.class);
        return RestResponse.success(smcConferenceContext);
    }



    /**
     * 会议详情
     * @param conferenceId
     * @return
     */
    @GetMapping("/info/detail/{conferenceId}")
    public RestResponse getConferencesDetailInfo(@PathVariable String conferenceId) {

        DetailConference detailConference = null;
        try {
            Callable<DetailConference> callableTask = () ->
                 smcConferenceService.getDetailConferenceInfoById(conferenceId);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future future = executor.submit(callableTask);
            try {
                detailConference=(DetailConference)future.get(2, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                return RestResponse.success();
            } finally {
                executor.shutdownNow();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.success();
        }
        Map<String,String> uriMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(conferenceId);
        if(detailConference!=null){
            ConferenceState conferenceState = detailConference.getConferenceState();
            if(conferenceState!=null){

                if (uriMap!=null&&uriMap.size()>0) {
                    conferenceState.setCascade(true);
                }else {
                    conferenceState.setCascade(false);
                }

                String s = SmcConferenceContextCache.getInstance().getChooseParticipantMap().get(conferenceId);
                conferenceState.setChooseId(s);


            }
            CoSpace coSpace = (CoSpace)SmcConferenceContextCache.getInstance().getMonitorParticipantMap().get(conferenceId);
            if(coSpace!=null){
                detailConference.setMonitorNumber(coSpace.getUri());
            }

            List<ConferenceState.ParticipantPollStatusListDTO> participantPollStatusList = detailConference.getConferenceState().getParticipantPollStatusList();
            if(!CollectionUtils.isEmpty(participantPollStatusList)){
                ConferenceState.ParticipantPollStatusListDTO participantPollStatusListDTO = participantPollStatusList.get(0);
                String pollStatus = participantPollStatusListDTO.getPollStatus();
                detailConference.getConferenceState().setChairmanPollStatus(pollStatus);
            }


        }




        return RestResponse.success(detailConference);
    }


    /**
     * 获取预置多画面
     * @param conferenceId
     * @return
     */
    @GetMapping("/presetParam/{conferenceId}")
    public RestResponse getConferencesPresetParam(@PathVariable String conferenceId) {

        List<PresetMultiPicReqDto> presetMultiPics = smcConferenceService.getConferencesPresetParam(conferenceId);
        return RestResponse.success(presetMultiPics);
    }

    /**
     *
     * 会场状态详情
     * @param conferenceId
     * @return
     */
    @PostMapping("/info/participants/state/{conferenceId}")
    public RestResponse getConferencesparticipantState(@PathVariable String conferenceId) {

        SmcParitipantsStateRep conferencesParticipantsState = null;
        List<SmcParitipantsStateRep.ContentDTO> collect = null;
        try {

            Callable<SmcParitipantsStateRep> callableTask = () ->
                    smcParticipantsService.getConferencesParticipantsState(conferenceId, 0, 500);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future future = executor.submit(callableTask);
            try {
                conferencesParticipantsState = (SmcParitipantsStateRep) future.get(5, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                return RestResponse.success();
            } finally {
                executor.shutdownNow();
            }
        List<SmcParitipantsStateRep.ContentDTO> content = conferencesParticipantsState.getContent();


        BusiSmcTemplateConference busiSmcTemplateConference = new BusiSmcTemplateConference();
        busiSmcTemplateConference.setConferenceId(conferenceId);
        List<BusiSmcTemplateConference> busiSmcTemplateConferences = busiSmcTemplateConferenceService.selectBusiSmcTemplateConferenceList(busiSmcTemplateConference);
        if(!CollectionUtils.isEmpty(busiSmcTemplateConferences)){
            List<SmcTemplateTerminal> templateTerminals = smcTemplateTerminalService.list(busiSmcTemplateConferences.get(0).getSmcTemplateId());

            content.stream().forEach(p -> {
                Optional<SmcTemplateTerminal> first = templateTerminals.stream().filter(m ->
                    Objects.equals(p.getGeneralParam().getUri(), m.getSmcnumber())
                   ).findFirst();
                if (first.isPresent()) {
                    SmcTemplateTerminal smcTemplateTerminal = first.get();
                    p.setDeptId(smcTemplateTerminal.getTerminalDeptId());
                    p.setTerminalId(smcTemplateTerminal.getTerminalId());
                    p.setWeight(smcTemplateTerminal.getWeight());
                    // BusiTerminal terminal = TerminalCache.getInstance().get(smcTemplateTerminal.getTerminalId());
                    BusiTerminal terminal = iBusiTerminalService.selectBusiTerminalById(smcTemplateTerminal.getTerminalId());
                    if(terminal!=null){
                        Boolean onlineState = terminal.getOnlineStatus()==1?true:false;
                        p.setTerminalOnline(onlineState);
                        p.setTerminalTypeName(TerminalType.convert(terminal.getType()).getDisplayName());
                        p.setTerminalType(terminal.getType());
                        if(TerminalType.isMcuTemplateCisco(terminal.getType())){
                            p.setIsCascade(true);
                            if (StringUtils.isNotBlank(conferenceId)) {
                                Map<String, String> stringStringMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(conferenceId);
                                if(Objects.isNull(stringStringMap)){
                                    Map<String, String> uirMap = new HashMap<>();
                                    uirMap.put(p.getGeneralParam().getId(), p.getGeneralParam().getUri());
                                    SmcConferenceContextCache.getInstance().getCascadeConference().put(conferenceId, uirMap);
                                }else {
                                    stringStringMap.put(p.getGeneralParam().getId(), p.getGeneralParam().getUri());
                                }
                            }
                        }

                        if (TerminalType.isFmeTemplate(terminal.getType())) {
                            p.setIsCascade(true);
                            p.setTerminalTypeName(TerminalType.FME_TEMPLATE.getDisplayName());
                            String fmeConfId = AesEnsUtils.getAesEncryptor().encryptToHex(String.valueOf(terminal.getNumber()));
                            p.setConferenceIdFme(fmeConfId);
                            if (StringUtils.isNotBlank(conferenceId)) {
                                Map<String, String> stringStringMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(conferenceId);
                                if(Objects.isNull(stringStringMap)){
                                    Map<String, String> uirMap = new HashMap<>();
                                    uirMap.put(p.getGeneralParam().getId(), p.getGeneralParam().getUri());
                                    SmcConferenceContextCache.getInstance().getCascadeConference().put(conferenceId, uirMap);
                                }else {
                                    stringStringMap.put(p.getGeneralParam().getId(), p.getGeneralParam().getUri());
                                }
                            }

                            FcmThreadPool.exec(() -> {
                                try {
                                    ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(String.valueOf(terminal.getNumber()));
                                    if(conferenceContext==null){
                                        IBusiTemplateConferenceService busiTemplateConferenceService = BeanFactory.getBean(IBusiTemplateConferenceService.class);
                                        List<BusiTemplateConference> list = busiTemplateConferenceService.selectBusiTemplateConferenceList(String.valueOf(terminal.getNumber()), null);
                                        if (!CollectionUtils.isEmpty(list)) {
                                            BusiTemplateConference templateConference = list.get(0);
                                            Long templateConferenceId = templateConference.getId();
                                            if(templateConferenceId!=null){
                                                ITemplateConferenceStartService templateConferenceStartService = BeanFactory.getBean(ITemplateConferenceStartService.class);
                                                templateConferenceStartService.startConference(templateConferenceId);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }
                if(p.getState().getOnline()){
                    p.setTerminalOnline(true);
                }

            });
//            if (!CollectionUtils.isEmpty(templateTerminals)) {
//                for (SmcTemplateTerminal templateTerminal : templateTerminals) {
//                    BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(templateTerminal.getTerminalId());
//                    if (TerminalType.isFCMSIP(busiTerminal.getType())) {
//                        SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
//                        contentDTO.setDeptId(busiTerminal.getDeptId());
//                        contentDTO.setTerminalId(busiTerminal.getId());
//                        ParticipantState participantState = new ParticipantState();
//                        participantState.setParticipantId(null);
//                        contentDTO.setState(participantState);
//                        contentDTO.setWeight(templateTerminal.getWeight());
//                        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO paramDTO = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
//                        paramDTO.setName(busiTerminal.getName());
//                        paramDTO.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
//                        contentDTO.setState(participantState);
//                        contentDTO.setGeneralParam(paramDTO);
//
//                        Boolean onlineState = busiTerminal.getOnlineStatus() == 1 ? true : false;
//                        contentDTO.setTerminalOnline(onlineState);
//
//                        if(!CollectionUtils.isEmpty(content)){
//                            Optional<SmcParitipantsStateRep.ContentDTO> any = content.stream().filter(cm -> Objects.equals(cm.getGeneralParam().getName(), busiTerminal.getName())).findAny();
//                            if(!any.isPresent()){
//                                content.add(contentDTO);
//                            }
//                        }
//
//                    }
//
//                }
//            }
        }

        BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant = new BusiSmcAppointmentConferencePaticipant();
        busiSmcAppointmentConferencePaticipant.setConferenceId(conferenceId);
        List<BusiSmcAppointmentConferencePaticipant> paticipantList = appointmentPaticipantService.selectBusiSmcAppointmentConferencePaticipantList(busiSmcAppointmentConferencePaticipant);
        if(!CollectionUtils.isEmpty(paticipantList)){
//            for (BusiSmcAppointmentConferencePaticipant smcAppointmentConferencePaticipant : paticipantList) {
//
//                BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(smcAppointmentConferencePaticipant.getTerminalId());
//                if (TerminalType.isFCMSIP(busiTerminal.getType())) {
//                    SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
//                    contentDTO.setDeptId(busiTerminal.getDeptId());
//                    contentDTO.setTerminalId(busiTerminal.getId());
//                    ParticipantState participantState = new ParticipantState();
//                    participantState.setParticipantId(null);
//                    contentDTO.setState(participantState);
//                    contentDTO.setWeight(smcAppointmentConferencePaticipant.getWeight());
//                    SmcParitipantsStateRep.ContentDTO.GeneralParamDTO paramDTO = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
//                    paramDTO.setName(busiTerminal.getName());
//                    paramDTO.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
//                    contentDTO.setState(participantState);
//                    contentDTO.setGeneralParam(paramDTO);
//                    Boolean onlineState = busiTerminal.getOnlineStatus() == 1 ? true : false;
//                    contentDTO.setTerminalOnline(onlineState);
//                    if(!CollectionUtils.isEmpty(content)){
//                        Optional<SmcParitipantsStateRep.ContentDTO> any = content.stream().filter(cm -> Objects.equals(cm.getGeneralParam().getName(), busiTerminal.getName())).findAny();
//                        if(!any.isPresent()){
//                            content.add(contentDTO);
//                        }
//                    }
//
//                }
//            }

            if (!CollectionUtils.isEmpty(content)) {
                content.stream().forEach(cc -> {
                    Optional<BusiSmcAppointmentConferencePaticipant> first = paticipantList.stream().filter(pp -> Objects.equals(cc.getGeneralParam().getUri(), pp.getSmcnumber())).findFirst();
                    if (first.isPresent()) {
                        BusiSmcAppointmentConferencePaticipant paticipant = first.get();
                        cc.setTerminalId(paticipant.getTerminalId());
                        cc.setWeight(paticipant.getWeight());
                        // BusiTerminal terminal = TerminalCache.getInstance().get(smcTemplateTerminal.getTerminalId());
                        BusiTerminal terminal = iBusiTerminalService.selectBusiTerminalById(paticipant.getTerminalId());
                        cc.setDeptId(terminal.getDeptId());
                        if(terminal!=null){
                            Boolean onlineState = terminal.getOnlineStatus()==1?true:false;
                            cc.setTerminalOnline(onlineState);
                            cc.setTerminalTypeName(TerminalType.convert(terminal.getType()).getDisplayName());
                            cc.setTerminalType(terminal.getType());
                            if(TerminalType.isMcuTemplateCisco(terminal.getType())){
                                cc.setIsCascade(true);
                                if (StringUtils.isNotBlank(conferenceId)) {
                                    Map<String, String> stringStringMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(conferenceId);
                                    if(Objects.isNull(stringStringMap)){
                                        Map<String, String> uirMap = new HashMap<>();
                                        uirMap.put(cc.getGeneralParam().getId(), cc.getGeneralParam().getUri());
                                        SmcConferenceContextCache.getInstance().getCascadeConference().put(conferenceId, uirMap);
                                    }else {
                                        stringStringMap.put(cc.getGeneralParam().getId(), cc.getGeneralParam().getUri());
                                    }
                                }
                            }

                            if (TerminalType.isFmeTemplate(terminal.getType())) {
                                cc.setIsCascade(true);
                                cc.setTerminalTypeName(TerminalType.FME_TEMPLATE.getDisplayName());
                                String fmeConfId = AesEnsUtils.getAesEncryptor().encryptToHex(String.valueOf(terminal.getNumber()));
                                cc.setConferenceIdFme(fmeConfId);
                                if (StringUtils.isNotBlank(conferenceId)) {
                                    Map<String, String> stringStringMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(conferenceId);
                                    if(Objects.isNull(stringStringMap)){
                                        Map<String, String> uirMap = new HashMap<>();
                                        uirMap.put(cc.getGeneralParam().getId(), cc.getGeneralParam().getUri());
                                        SmcConferenceContextCache.getInstance().getCascadeConference().put(conferenceId, uirMap);
                                    }else {
                                        stringStringMap.put(cc.getGeneralParam().getId(), cc.getGeneralParam().getUri());
                                    }
                                }
                            }
                        }
                    }
                    if (cc.getState().getOnline()) {
                        cc.setTerminalOnline(true);
                    }

                });
            }
        }
        if (!CollectionUtils.isEmpty(content)&&CollectionUtils.isEmpty(paticipantList)&&CollectionUtils.isEmpty(busiSmcTemplateConferences)) {
            content.stream().forEach(cbc -> {
                if (cbc.getState().getOnline()) {
                    cbc.setTerminalOnline(true);
                }
            });
        }
            Map<String, Object> terminalURiIdMap = SmcConferenceContextCache.getInstance().getTerminalURiIdMap();

            if (!CollectionUtils.isEmpty(content)) {
            for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
                String uri = contentDTO.getGeneralParam().getUri();


                Object o = terminalURiIdMap.get(uri);
                if(o!=null){
                    BusiTerminal busiTerminal =(BusiTerminal)o;
                    BusiTerminal busiTerminalConditon = new BusiTerminal();
                    busiTerminalConditon.setId(busiTerminal.getId());
                    BusiTerminal busiTerminal1 = iBusiTerminalService.selectBusiTerminal(busiTerminalConditon);
                    contentDTO.setDeptId(busiTerminal1.getDeptId());
                    contentDTO.setTerminalId(busiTerminal1.getId());
                    contentDTO.setWeight(0);
                    Boolean onlineState = busiTerminal1.getOnlineStatus()==1?true:false;
                    contentDTO.setTerminalOnline(onlineState);
                    contentDTO.setTerminalTypeName(TerminalType.convert(busiTerminal1.getType()).getDisplayName());
                    contentDTO.setTerminalType(busiTerminal1.getType());
                }else {
                    BusiTerminal busiTerminal = new BusiTerminal();
                    busiTerminal.setNumber(uri);
                    busiTerminal.setName(contentDTO.getGeneralParam().getName());
                    BusiTerminal busiTerminal1 = iBusiTerminalService.selectBusiTerminal(busiTerminal);
                    if(busiTerminal1!=null){
                        if(Objects.equals(uri, busiTerminal1.getNumber())){
                            contentDTO.setDeptId(busiTerminal1.getDeptId());
                            contentDTO.setTerminalId(busiTerminal1.getId());
                            contentDTO.setWeight(0);
                            Boolean onlineState = busiTerminal1.getOnlineStatus()==1?true:false;
                            contentDTO.setTerminalOnline(onlineState);
                            contentDTO.setTerminalTypeName(TerminalType.convert(busiTerminal1.getType()).getDisplayName());
                            contentDTO.setTerminalType(busiTerminal1.getType());
                        }
                    }
                }
            }

        }

        if(conferencesParticipantsState.getContent()!=null){
            collect = conferencesParticipantsState.getContent().stream().sorted(Comparator.comparing(SmcParitipantsStateRep.ContentDTO::getWeight, Comparator.reverseOrder())).collect(Collectors.toList());
        }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return RestResponse.success(collect);
    }

    /**
     * 会场状态详情
     * @param conferenceId
     * @return
     */
    @PostMapping("/info/participants/state/page/{conferenceId}")
    public RestResponse getConferencesparticipantStatePage(@PathVariable String conferenceId,@RequestParam(value = "page",required = false,defaultValue = "0") int page,@RequestParam(value = "size",required = false,defaultValue = "1000")  int size) {
        SmcParitipantsStateRep conferencesParticipantsState = smcParticipantsService.getConferencesParticipantsState(conferenceId, page, size);
        return RestResponse.success(conferencesParticipantsState);
    }




    /**
     * 会议列表
     * @param smcConferenceRequest
     * @return
     */
    @PostMapping("/list")
    public RestResponse getConferencesInfo(@RequestBody SmcConferenceRequest smcConferenceRequest) {
        return RestResponse.success(smcConferenceService.getConferenceList(smcConferenceRequest));
    }

    /**
     * 结束会议
     * @param conferenceId
     */
    @DeleteMapping("/end/{conferenceId}")
    public RestResponse endConference(@PathVariable String conferenceId) {
        smcConferenceService.endConference(conferenceId);
        busiSmcTemplateConferenceService.deleteBusiSmcTemplateConferenceByConferenceId(conferenceId);
        SmcConferenceContextCache.getInstance().cleanCacheMap(conferenceId);
        busiSmcMulitpicService.deleteBusiSmcMulitpicByConferenceId(conferenceId);
        return  RestResponse.success();
    }


    /**
     * 开始广播
     * @param conferenceId
     */
    @PatchMapping("/broadcast/start/{conferenceId}")
    public RestResponse broadcastStart(@PathVariable String conferenceId) {
        smcConferenceService.broadcastStart(conferenceId);

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

        return RestResponse.success();
    }

    /**
     * 结束广播
     * @param conferenceId
     */
    @PatchMapping("/broadcast/end/{conferenceId}")
    public RestResponse broadcastEnd(@PathVariable String conferenceId) {
        smcConferenceService.broadcastEnd(conferenceId);
        return  RestResponse.success();
    }


    /**
     * 锁定
     * @param conferenceId
     */
    @PatchMapping("/lock/{conferenceId}")
    public RestResponse lockConference(@PathVariable String conferenceId) {
        smcConferenceService.lockConference(conferenceId);
        return  RestResponse.success();
    }


    /**
     * 取消锁定
     * @param conferenceId
     */
    @PatchMapping("/unlock/{conferenceId}")
    public RestResponse unlockConference(@PathVariable String conferenceId) {
        smcConferenceService.unlockConference(conferenceId);
        return  RestResponse.success();
    }


    /**
     * 会场一键呼入
     * @param conferenceId
     */
    @PatchMapping("/recall/{conferenceId}")
    public RestResponse changeParticipantsStatus(@PathVariable String conferenceId){
        ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
        conferenceStatusRequest.setIsOnline(true);
        smcConferenceService.statusControl(conferenceId,conferenceStatusRequest);
        return  RestResponse.success();

    }


    /**
     * 全体一键静音
     * @param conferenceId
     */
    @PatchMapping("/isMute/{conferenceId}")
    public RestResponse changeParticipantsStatusMute(@PathVariable String conferenceId){
        smcConferenceService.setMute(conferenceId,true);
        return  RestResponse.success();
    }


    /**
     * 取消全体一键静音
     * @param conferenceId
     */
    @PatchMapping("/unMute/{conferenceId}")
    public RestResponse changeParticipantsStatusMuteFalse(@PathVariable String conferenceId){
        smcConferenceService.setMute(conferenceId,false);
        return  RestResponse.success();
    }



    /**
     * 全体麦克风打开/关闭
     * @param conferenceId
     */
    @PatchMapping("/quiet/{conferenceId}/{enable}")
    public RestResponse changeQuietFalse(@PathVariable String conferenceId,@PathVariable boolean enable){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("isQuiet",enable);
        smcConferenceService.setStatus(conferenceId,jsonObject);
        return  RestResponse.success();
    }


    /**
     * 最大人数设置
     * @param conferenceId
     */
    @PatchMapping("/maxParticipantNum/{conferenceId}/{number}")
    public RestResponse changemaxParticipantNum(@PathVariable String conferenceId,@PathVariable int number){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("maxParticipantNum",number);
        smcConferenceService.setStatus(conferenceId,jsonObject);
        return  RestResponse.success();
    }

    /**
     * 打开(true)、关闭 非主席名称修改
     * @param conferenceId
     */
    @PatchMapping("/enableSiteNameEditByGuest/{conferenceId}/{enable}")
    public RestResponse enableSiteNameEditByGuest(@PathVariable String conferenceId,@PathVariable boolean enable){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("enableSiteNameEditByGuest",enable);
        smcConferenceService.setStatus(conferenceId,jsonObject);
        return  RestResponse.success();
    }


    /**
     * 导播模式/自由模式
     * @param conferenceId
     */
    @PatchMapping("/directMode/{conferenceId}/{enable}")
    public RestResponse enableDIRECT_MODE(@PathVariable String conferenceId,@PathVariable boolean enable){
        JSONObject jsonObject=new JSONObject();
        if(enable){
            jsonObject.put("mode","DIRECT_MODE");
        }else {
            jsonObject.put("mode","FREE_MODE");
        }

        smcConferenceService.setStatus(conferenceId,jsonObject);
        return  RestResponse.success();
    }

    /**
     * 自由讨论
     * @param conferenceId
     */
    @PatchMapping("/freeTalk/{conferenceId}")
    public RestResponse changeParticipantsStatusFreeTalk(@PathVariable String conferenceId){
        smcConferenceService.setFreeTalk(conferenceId);
        return  RestResponse.success();
    }


    /**
     * 延长会议
     * @param
     */
    @PutMapping("/extendTime")
    public RestResponse changeParticipantsStatusExtendTime(@RequestBody  @Validated  ExtendTimeReq extendTimeReq){
        smcConferenceService.ExtendTime(extendTimeReq);
        return  RestResponse.success();
    }



    /**
     * 会议横幅
     * @param
     */
    @PostMapping("/textTips")
    public RestResponse changeConferencesStatusTextTips(@RequestBody  TextTipsSetting textTipsSetting){
        smcConferenceService.textTipsSetting(textTipsSetting);
        return  RestResponse.success();
    }

    /**
     * 停止会议横幅
     * @param
     */
    @PostMapping("/stop/textTips")
    public RestResponse changeConferencesStatusTextTipsStop(@RequestBody  TextTipsSetting textTipsSetting){
        textTipsSetting.setType( TxtTypeEnum.BANNER.name());
        textTipsSetting.setOpType(TxtOperationTypeEnumDto.CANCEL.name());
        smcConferenceService.textTipsSetting(textTipsSetting);
        return  RestResponse.success();
    }


    /**
     * 会议字幕
     * @param
     */
    @PostMapping("/textTips/caption")
    public RestResponse changeConferencesStatusTextTipsCaption(@RequestBody  TextTipsSetting textTipsSetting){
        textTipsSetting.setType( TxtTypeEnum.CAPTION.name());
        smcConferenceService.textTipsSetting(textTipsSetting);

        String conferenceId = textTipsSetting.getConferenceId();
        Map<String, String> uriMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(conferenceId);

        if(!CollectionUtils.isEmpty(uriMap)){
            SmcParitipantsStateRep conferencesParticipantsState = smcParticipantsService.getConferencesParticipantsState(conferenceId, 0, 10000);
            List<SmcParitipantsStateRep.ContentDTO> content = conferencesParticipantsState.getContent();
            if (!CollectionUtils.isEmpty(content)) {
                List<SmcParitipantsStateRep.ContentDTO> collect = content.stream().filter(p -> isCascade(p)).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(collect)){
                    collect.stream().forEach(pa->{
                        sendMessageTotele(textTipsSetting, pa);
                    });
                }
            }
        }

        return  RestResponse.success();
    }

    private void sendMessageTotele(TextTipsSetting textTipsSetting, SmcParitipantsStateRep.ContentDTO pa) {
        String uri = pa.getGeneralParam().getUri();
        String[] split = uri.split("@");
        String ip = split[1];
        String number = split[0];
        List<TeleParticipant> list = paraticipantsService.getList(number, ip, EnumerateFilter.CONNECTED);
        if(!CollectionUtils.isEmpty(list)){

            list.stream().forEach(m->{
                convertMessage(textTipsSetting, ip, m);
            });
        }
    }

    private boolean isCascade(SmcParitipantsStateRep.ContentDTO p) {
        String uri = p.getGeneralParam().getUri();
        String ip = uri.split("@")[1];

        TelepBridge ipToTeleBridgeMap = TeleBridgeCache.getInstance().getIpToTeleBridgeMap().get(ip);
        if (ipToTeleBridgeMap != null) {
            return true;
        }
        return false;
    }

    private void convertMessage(TextTipsSetting textTipsSetting, String ip, TeleParticipant m) {
        ParticipantMessage participantMessage = new ParticipantMessage();
        participantMessage.setMessage(textTipsSetting.getContent());
        participantMessage.setParticipantName(m.getParticipantName());
        participantMessage.setParticipantProtocol(m.getParticipantProtocol());
        participantMessage.setParticipantType(m.getParticipantType());
        participantMessage.setConferenceName(m.getConferenceName());
        participantMessage.setAutoAttendantUniqueID(m.getAutoAttendantUniqueID());
        participantMessage.setDurationSeconds(8639999);
        int disPosition = textTipsSetting.getDisPosition();
        String position;
        switch(disPosition){
            case 1:
                position= VerticalPosition.TOP.name();
                break;
            case 2:
                position= VerticalPosition.MIDDLE.name();
                break;
            case 3:
                position= VerticalPosition.BOTTOM.name();
                break;
            default:
                position= VerticalPosition.MIDDLE.name();
                break;
        }
        participantMessage.setVerticalPosition(position);
        paraticipantsService.participantMessage(ip,participantMessage);
    }

    /**
     * 停止会议字幕
     * @param
     */
    @PostMapping("/stop/textTips/caption")
    public RestResponse changeConferencesStatusTextTipsStopCaption(@RequestBody  TextTipsSetting textTipsSetting){
        textTipsSetting.setOpType(TxtOperationTypeEnumDto.CANCEL.name());
        textTipsSetting.setType( TxtTypeEnum.CAPTION.name());
        smcConferenceService.textTipsSetting(textTipsSetting);


        String conferenceId = textTipsSetting.getConferenceId();
        Map<String, String> uriMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(conferenceId);
        if(!CollectionUtils.isEmpty(uriMap)){
            SmcParitipantsStateRep conferencesParticipantsState = smcParticipantsService.getConferencesParticipantsState(conferenceId, 0, 10000);
            List<SmcParitipantsStateRep.ContentDTO> content = conferencesParticipantsState.getContent();
            if (!CollectionUtils.isEmpty(content)) {
                List<SmcParitipantsStateRep.ContentDTO> collect = content.stream().filter(p -> isCascade(p)).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(collect)){
                    collect.stream().forEach(pa->{
                        textTipsSetting.setContent("");
                        sendMessageTotele(textTipsSetting, pa);
                    });
                }
            }
        }
        return RestResponse.success();

    }


    /**
     * 多画面设置
     *
     * @param multiPicInfoReq
     */
    @PatchMapping("/multiPic/create")
    public RestResponse multiPicSave(@RequestBody MultiPicInfoReq multiPicInfoReq) {
        String conferenceId = multiPicInfoReq.getConferenceId();
        cancelPolling(conferenceId);
        smcConferenceService.createMulitiPic(multiPicInfoReq);

        try {
            BusiSmcMulitpic busiSmcMulitpic = busiSmcMulitpicService.selectBusiSmcMulitpicByConferenceId(conferenceId);
            if (busiSmcMulitpic == null) {
                BusiSmcMulitpic busiSmcMulitpic1 = new BusiSmcMulitpic();
                busiSmcMulitpic1.setConferenceId(conferenceId);
                busiSmcMulitpic1.setMulitpic(JSON.toJSONString(multiPicInfoReq));
                busiSmcMulitpicService.insertBusiSmcMulitpic(busiSmcMulitpic1);
            } else {
                busiSmcMulitpic.setMulitpic(JSON.toJSONString(multiPicInfoReq));
                busiSmcMulitpicService.updateBusiSmcMulitpic(busiSmcMulitpic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


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

        SmcConferenceContextCache.getInstance().getLocalMultiPicInfoMap().put(multiPicInfoReq.getConferenceId(), multiPicInfoReq);
        return RestResponse.success();
    }

    private void cancelPolling(String conferenceId) {
        try {
            ChairmanPollOperateReq chairmanPollOperateReq = new ChairmanPollOperateReq();
            chairmanPollOperateReq.setConferenceId(conferenceId);
            chairmanPollOperateReq.setPollStatus(PollOperateTypeDto.CANCEL);
            smcConferenceService.chairmanPollOperate(chairmanPollOperateReq);
        } catch (Exception e) {
            log.error("SMC停止轮询错误:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *  查询常用会场列表
     * @param
     */
    @PostMapping("/orderQuery")
    public RestResponse orderQuery(@RequestBody SmcConferenceRequest conferenceRequest){
        String conferenceId = conferenceRequest.getConferenceId();
        ParticipantOrderRep orderRep= smcConferenceService.orderQuery(conferenceRequest);
        if(Objects.isNull(orderRep)){
            return RestResponse.success();
        }
        List<ParticipantOrderRep.ContentDTO> contentSourece = orderRep.getContent();
        if(Objects.isNull(contentSourece)){
            return RestResponse.success();
        }
        String s = JSON.toJSONString(contentSourece);
        List<SmcParitipantsStateRep.ContentDTO> content = JSON.parseArray(s, SmcParitipantsStateRep.ContentDTO.class);

        BusiSmcTemplateConference busiSmcTemplateConference = new BusiSmcTemplateConference();
        busiSmcTemplateConference.setConferenceId(conferenceId);
        List<BusiSmcTemplateConference> busiSmcTemplateConferences = busiSmcTemplateConferenceService.selectBusiSmcTemplateConferenceList(busiSmcTemplateConference);
        if(!CollectionUtils.isEmpty(busiSmcTemplateConferences)){
            List<SmcTemplateTerminal> templateTerminals = smcTemplateTerminalService.list(busiSmcTemplateConferences.get(0).getSmcTemplateId());

            if (!CollectionUtils.isEmpty(content)) {
                content.stream().forEach(p -> {
                    Optional<SmcTemplateTerminal> first = templateTerminals.stream().filter(m -> Objects.equals(p.getGeneralParam().getUri(), m.getSmcnumber())).findFirst();
                    if (first.isPresent()) {
                        SmcTemplateTerminal smcTemplateTerminal = first.get();
                        p.setDeptId(smcTemplateTerminal.getTerminalDeptId());
                        p.setTerminalId(smcTemplateTerminal.getTerminalId());
                        // BusiTerminal terminal = TerminalCache.getInstance().get(smcTemplateTerminal.getTerminalId());
                        BusiTerminal terminal = iBusiTerminalService.selectBusiTerminalById(smcTemplateTerminal.getTerminalId());
                        if(terminal!=null){
                            Boolean onlineState = terminal.getOnlineStatus()==1?true:false;
                            p.setTerminalOnline(onlineState);
                            p.setTerminalTypeName(TerminalType.convert(terminal.getType()).getDisplayName());
                            p.setTerminalType(terminal.getType());
                            if(TerminalType.isMcuTemplateCisco(terminal.getType())){
                                p.setIsCascade(true);
                                if (StringUtils.isNotBlank(conferenceId)) {
                                    Map<String, String> stringStringMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(conferenceId);
                                    if(Objects.isNull(stringStringMap)){
                                        Map<String, String> uirMap = new HashMap<>();
                                        uirMap.put(p.getGeneralParam().getId(), p.getGeneralParam().getUri());
                                        SmcConferenceContextCache.getInstance().getCascadeConference().put(conferenceId, uirMap);
                                    }else {
                                        stringStringMap.put(p.getGeneralParam().getId(), p.getGeneralParam().getUri());
                                    }
                                }
                            }

                            if (TerminalType.isFmeTemplate(terminal.getType())) {
                                p.setIsCascade(true);
                                p.setTerminalTypeName(TerminalType.FME_TEMPLATE.getDisplayName());
                                String fmeConfId = AesEnsUtils.getAesEncryptor().encryptToHex(String.valueOf(terminal.getNumber()));
                                p.setConferenceIdFme(fmeConfId);
                                if (StringUtils.isNotBlank(conferenceId)) {
                                    Map<String, String> stringStringMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(conferenceId);
                                    if(Objects.isNull(stringStringMap)){
                                        Map<String, String> uirMap = new HashMap<>();
                                        uirMap.put(p.getGeneralParam().getId(), p.getGeneralParam().getUri());
                                        SmcConferenceContextCache.getInstance().getCascadeConference().put(conferenceId, uirMap);
                                    }else {
                                        stringStringMap.put(p.getGeneralParam().getId(), p.getGeneralParam().getUri());
                                    }
                                }
                            }
                        }
                    }

                    if(p.getState().getOnline()){
                        p.setTerminalOnline(true);
                    }


                });


            }


            if (!CollectionUtils.isEmpty(templateTerminals)) {
                for (SmcTemplateTerminal templateTerminal : templateTerminals) {
                    BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(templateTerminal.getTerminalId());
                    if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                        SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
                        contentDTO.setDeptId(busiTerminal.getDeptId());
                        contentDTO.setTerminalId(busiTerminal.getId());
                        ParticipantState participantState = new ParticipantState();
                        participantState.setParticipantId(null);
                        contentDTO.setState(participantState);
                        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO paramDTO = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
                        paramDTO.setName(busiTerminal.getName());
                        paramDTO.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
                        contentDTO.setState(participantState);
                        contentDTO.setGeneralParam(paramDTO);
                        Boolean onlineState = busiTerminal.getOnlineStatus() == 1 ? true : false;
                        contentDTO.setTerminalOnline(onlineState);

                    }

                }
            }
        }

        BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant = new BusiSmcAppointmentConferencePaticipant();
        busiSmcAppointmentConferencePaticipant.setConferenceId(conferenceId);
        List<BusiSmcAppointmentConferencePaticipant> paticipantList = appointmentPaticipantService.selectBusiSmcAppointmentConferencePaticipantList(busiSmcAppointmentConferencePaticipant);
        if(!CollectionUtils.isEmpty(paticipantList)){
            if (!CollectionUtils.isEmpty(content)) {
                content.stream().forEach(cc -> {
                    Optional<BusiSmcAppointmentConferencePaticipant> first = paticipantList.stream().filter(pp -> Objects.equals(cc.getGeneralParam().getUri(), pp.getSmcnumber())).findFirst();
                    if (first.isPresent()) {
                        BusiSmcAppointmentConferencePaticipant paticipant = first.get();
                        cc.setTerminalId(paticipant.getTerminalId());
                        cc.setWeight(paticipant.getWeight());
                        // BusiTerminal terminal = TerminalCache.getInstance().get(smcTemplateTerminal.getTerminalId());
                        BusiTerminal terminal = iBusiTerminalService.selectBusiTerminalById(paticipant.getTerminalId());
                        cc.setDeptId(terminal.getDeptId());
                        if(terminal!=null){
                            Boolean onlineState = terminal.getOnlineStatus()==1?true:false;
                            cc.setTerminalOnline(onlineState);
                            cc.setTerminalTypeName(TerminalType.convert(terminal.getType()).getDisplayName());
                            cc.setTerminalType(terminal.getType());
                            if(TerminalType.isMcuTemplateCisco(terminal.getType())){
                                cc.setIsCascade(true);
                                cc.setTerminalTypeName(TerminalType.MCU_TEMPLATE_CISCO.getDisplayName());
                                if (StringUtils.isNotBlank(conferenceId)) {
                                    Map<String, String> stringStringMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(conferenceId);
                                    if(Objects.isNull(stringStringMap)){
                                        Map<String, String> uirMap = new HashMap<>();
                                        uirMap.put(cc.getGeneralParam().getId(), cc.getGeneralParam().getUri());
                                        SmcConferenceContextCache.getInstance().getCascadeConference().put(conferenceId, uirMap);
                                    }else {
                                        stringStringMap.put(cc.getGeneralParam().getId(), cc.getGeneralParam().getUri());
                                    }
                                }
                            }

                            if (TerminalType.isFmeTemplate(terminal.getType())) {
                                cc.setIsCascade(true);
                                cc.setTerminalTypeName(TerminalType.FME_TEMPLATE.getDisplayName());
                                String fmeConfId = AesEnsUtils.getAesEncryptor().encryptToHex(String.valueOf(terminal.getNumber()));
                                cc.setConferenceIdFme(fmeConfId);
                                if (StringUtils.isNotBlank(conferenceId)) {
                                    Map<String, String> stringStringMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(conferenceId);
                                    if(Objects.isNull(stringStringMap)){
                                        Map<String, String> uirMap = new HashMap<>();
                                        uirMap.put(cc.getGeneralParam().getId(), cc.getGeneralParam().getUri());
                                        SmcConferenceContextCache.getInstance().getCascadeConference().put(conferenceId, uirMap);
                                    }else {
                                        stringStringMap.put(cc.getGeneralParam().getId(), cc.getGeneralParam().getUri());
                                    }
                                }
                            }
                        }
                    }
                    if (cc.getState().getOnline()) {
                        cc.setTerminalOnline(true);
                    }

                });
            }
        }
        if (!CollectionUtils.isEmpty(content)) {
            for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
                String uri = contentDTO.getGeneralParam().getUri();
                BusiTerminal busiTerminal = new BusiTerminal();
                busiTerminal.setNumber(uri);
                busiTerminal.setName(contentDTO.getGeneralParam().getName());
                BusiTerminal busiTerminal1 = iBusiTerminalService.selectBusiTerminal(busiTerminal);
                if(busiTerminal1!=null){
                    if(Objects.equals(uri, busiTerminal1.getNumber())){
                        contentDTO.setDeptId(busiTerminal1.getDeptId());
                        contentDTO.setTerminalId(busiTerminal1.getId());
                        contentDTO.setWeight(0);
                        Boolean onlineState = busiTerminal1.getOnlineStatus()==1?true:false;
                        contentDTO.setTerminalOnline(onlineState);
                        contentDTO.setTerminalTypeName(TerminalType.convert(busiTerminal1.getType()).getDisplayName());
                        contentDTO.setTerminalType(busiTerminal1.getType());
                    }
                }

            }
        }
        return RestResponse.success(content);

    }

    /**
     * 设置常用会场
     * @param participantOrderRequest
     */
    @PutMapping("/order/enable")
    public void order(@RequestBody ParticipantOrderRequest participantOrderRequest){

       smcConferenceService.order(participantOrderRequest);

    }


    /**
     * 移除
     * @param participantOrderRequest
     */
    @PostMapping("/order/cancel")
    public void orderFalse(@RequestBody ParticipantOrderRequest participantOrderRequest){

        smcConferenceService.order(participantOrderRequest);

    }




    @GetMapping("/logs/search/findAllByConfId")
    public RestResponse  listLog(SmcConferenceRequest smcConferenceRequest) {
        try {
            ConferenceLogConfigCache instance = ConferenceLogConfigCache.getInstance();
            Map<String, String> logIdMap = ConferenceLogConfigCache.getLogIdMap();
            LogsConferenceRep logsConferenceRep = smcConferenceService.listLog(smcConferenceRequest);
            if (logsConferenceRep != null) {
                List<LogsConferenceRep.ContentDTO> content = logsConferenceRep.getContent();
                if (!CollectionUtils.isEmpty(content)) {
                    for (LogsConferenceRep.ContentDTO contentDTO : content) {
                        LogsConferenceRep.ContentDTO.OthersDTO others = contentDTO.getOthers();

                    }
                }
            }
            return RestResponse.success(logsConferenceRep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RestResponse.success(null);
    }



    @GetMapping("/logs/export")
    public RestResponse  exportLog(SmcConferenceRequest smcConferenceRequest, HttpServletResponse response){
        convertToUtc(smcConferenceRequest);
        smcConferenceService.downloadLog(smcConferenceRequest,response);
        return  RestResponse.success();
    }


    @GetMapping("/logs/search/findAllByConfIdpage")
    public RestResponse  listLogPage(SmcConferenceRequest smcConferenceRequest) {
        try {
            convertToUtc(smcConferenceRequest);
            Object obj = smcConferenceService.httpGetListString(smcConferenceRequest);

            return RestResponse.success(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RestResponse.success(null);
    }

    /**
     * 锁定会议材料(取消)
     */
    @PatchMapping("/lockPresenter/{conferenceId}/{lock}")
    @Operation(summary = "锁定会议材料")
    public RestResponse lockPresenter(@PathVariable String conferenceId,@PathVariable Boolean lock) {
        smcConferenceService.lockPresenter(conferenceId,lock);
        return RestResponse.success();
    }

    /**
     * 会场字体设置
     */
    @PatchMapping("/participantFontSetting")
    @Operation(summary = "会场字体设置")
    public RestResponse participantFont(@RequestBody ParticipantFontSetting participantFontSetting) {
        String conferenceId = participantFontSetting.getConferenceId();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("participantFontDto",participantFontSetting);
        smcConferenceService.setStatus(conferenceId,jsonObject);
        return RestResponse.success();
    }

    /**
     * 允许非主席会场打开麦克风
     */
    @PatchMapping("/enableUnmuteByGuest/{conferenceId}/{enable}")
    @Operation(summary = "会场字体设置")
    public RestResponse enableUnmuteByGuest(@PathVariable String conferenceId,@PathVariable Boolean enable) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enableUnmuteByGuest",enable);
        smcConferenceService.setStatus(conferenceId,jsonObject);
        return RestResponse.success();
    }

    /**
     * 打开、关闭全体扬声器
     *
     * @param conferenceId
     */
    @PatchMapping("/quietConf/{conferenceId}/{enable}")
    @Operation(summary = "打开、关闭全体扬声器")
    public RestResponse quietConf(@PathVariable String conferenceId,@PathVariable Boolean enable) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isQuiet",enable);
        smcConferenceService.setStatus(conferenceId,jsonObject);
        return RestResponse.success();
    }


    /**
     * 打开、关闭声控
     *
     * @param conferenceId
     */
    @PatchMapping("/voiceActive/{conferenceId}/{enable}")
    @Operation(summary = "打开、关闭声控")
    public RestResponse isVoiceActive(@PathVariable String conferenceId,@PathVariable Boolean enable) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isVoiceActive",enable);
        smcConferenceService.setStatus(conferenceId,jsonObject);
        return RestResponse.success();
    }


    private void convertToUtc(SmcConferenceRequest smcConferenceRequest) {
        String startTime = smcConferenceRequest.getStartTime();
        String endTime = smcConferenceRequest.getEndTime();
        if(!isValidDate(startTime)){
            startTime= DateUtils.formatTo("yyyy-MM-dd HH:mm:ss",new Date());
        }
        smcConferenceRequest.setStartTime(UTCTimeFormatUtil.convertToUtc(startTime));
        smcConferenceRequest.setEndTime( UTCTimeFormatUtil.convertToUtc(endTime));
    }

    public static boolean isValidDate(String str) {
        boolean convertSuccess=true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            convertSuccess=false;
        }
        return convertSuccess;
    }


    /**
     * 会议统计
     */
    @PostMapping("/count")
    @Operation(summary = "会议统计")
    public RestResponse count() {
        Object obj= smcConferenceService.count();
        return RestResponse.success(obj);
    }
    @GetMapping("/conferenceStat")
    @Operation(summary = "会议统计")
    public RestResponse conferenceStat(){
        Object obj=  smcConferenceService.conferenceStat();
        return RestResponse.success(obj);
    }

    /**
     * 首页会议报表统计(会议时长、终端总数、会议次数)
     */
    @GetMapping("/reportConferenceOfIndex")
    @Operation(summary = "首页会议报表统计(会议时长、终端总数、会议次数)")
    public RestResponse reportConferenceOfIndex(@RequestParam(required = false) Long deptId, @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime)
    {
        Map<String, Object> map = smcConferenceService.reportConferenceOfIndex(deptId, startTime, endTime);
        return RestResponse.success(map);
    }

    @GetMapping("/tenantResource")
    @Operation(summary = "租户资源信息")
    public RestResponse tenantResource()
    {
        JSONObject jsonObject = welcomeService.tenantResource();
        Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        if (deptId == null)
        {
            return RestResponse.success(jsonObject);
        }
        SmcBridge smcBridgeByDeptId = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);
        if (smcBridgeByDeptId != null) {
            BusiSmc busiSMC = smcBridgeByDeptId.getBusiSMC();
            ModelBean modelBean = new ModelBean();
            modelBean.put("ip",busiSMC.getIp());
            modelBean.put("type","无");
            jsonObject.put("bindSmcInfo", modelBean);
        } else {
            jsonObject.put("bindSmcInfo", null);
        }


        BusiMqttDept busiMqttDept = MqttDeptMappingCache.getInstance().getBindMqttNode(deptId);
        if (busiMqttDept != null) {
            jsonObject.put("bindFmqInfo", toModelBean(busiMqttDept));
        } else {
            jsonObject.put("bindFmqInfo", null);
        }

        List<BusiFcmNumberSection> busiFcmNumberSectionList = FcmAccountCacheAndUtils.getInstance().selectBindingBusiFcmNumberSectionByDeptId(deptId);
        if (busiFcmNumberSectionList != null && busiFcmNumberSectionList.size() > 0) {
            jsonObject.put("bindFcmAccount", busiFcmNumberSectionList);
        } else {
            jsonObject.put("bindFcmAccount", null);
        }

        return RestResponse.success(jsonObject);
    }


    public ModelBean toModelBean(BusiMqttDept busiMqttDept) {
        ModelBean mb = new ModelBean(busiMqttDept);
        mb.put("deptName", SysDeptCache.getInstance().get(busiMqttDept.getDeptId()).getDeptName());
        mb.put("fmqTypeName", MqttType.convert(busiMqttDept.getMqttType()).getName());
        StringBuilder fmeInfoBuilder = new StringBuilder();
        List<BusiMqtt> busiMqttList = new ArrayList<>();
        if (MqttType.convert(busiMqttDept.getMqttType()) == MqttType.CLUSTER) {
            BusiMqttCluster busiMqttCluster = MqttClusterCache.getInstance().get(busiMqttDept.getMqttId());
            fmeInfoBuilder.append("【").append(busiMqttCluster.getMqttClusterName()).append("】");
            MqttBridgeCluster busiMqttClusterById = MqttBridgeCache.getInstance().getBusiMqttClusterById(busiMqttDept.getMqttId());
            for (MqttBridge mqttBridge : busiMqttClusterById.getMqttBridges()) {
                busiMqttList.add(mqttBridge.getBusiMqtt());
            }
        } else {
            MqttBridge mqttBridge = MqttBridgeCache.getInstance().getMqttBridgeById(busiMqttDept.getMqttId());
            fmeInfoBuilder.append("【").append(mqttBridge.getBusiMqtt().getMqttName()).append("】");
            BusiMqtt busiMqtt = mqttBridge.getBusiMqtt();
            busiMqttList.add(busiMqtt);
        }

        mb.put("existAvailableFmqBridge", busiMqttList != null);
        if (busiMqttList == null) {
            fmeInfoBuilder.append("-").append("当前无可用的FME信息");
            mb.put("fmqs", new ArrayList<>());
        } else {
            fmeInfoBuilder.append("FMQ[");

            List<String> fmqs = new ArrayList<String>();
            StringBuilder fmeIpInfoBuilder = new StringBuilder();
            busiMqttList.forEach((busiMqtt) -> {
                if (!ObjectUtils.isEmpty(fmeIpInfoBuilder)) {
                    fmeIpInfoBuilder.append(", ");
                }
                fmeIpInfoBuilder.append(busiMqtt.getIp());

                fmqs.add(busiMqtt.getIp());
            });
            fmeInfoBuilder.append(fmeIpInfoBuilder);
            fmeInfoBuilder.append("]");
            mb.put("fmqs", fmqs);
        }
        mb.put("fmqInfo", fmeInfoBuilder.toString());
        return mb;
    }


}

package com.paradisecloud.fcm.web.controller.smc;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.com.fcm.smc.modle.mix.CreateParticipantsReq;
import com.paradisecloud.com.fcm.smc.modle.request.BusiSmcAppointmentConferenceQuery;
import com.paradisecloud.com.fcm.smc.modle.request.SmcAppointmentConferenceRequest;
import com.paradisecloud.com.fcm.smc.modle.response.SmcErrorResponse;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryCallMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.paradisecloud.fcm.fme.model.response.participant.ActiveParticipantsResponse;
import com.paradisecloud.fcm.fme.model.response.participant.ParticipantsResponse;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.fcm.smc.cache.modle.SmcConferenceContextCache;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.fcm.web.model.smc.BusiSmcAppointmentConferenceInfo;
import com.paradisecloud.fcm.web.model.smc.BusiSmcAppointmentConferenceRequest;
import com.paradisecloud.smc.dao.model.*;
import com.paradisecloud.smc.service.*;
import com.paradisecloud.smc.service.task.SmcMeetingRoomRegTask;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.ExcelUtil;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import io.jsonwebtoken.lang.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;

import static com.paradisecloud.smc.service.impl.BusiSmcAppointmentConferenceServiceImpl.buildSmcAppointmentConference;

/**
 * 【预约会议】Controller
 * 
 * @author nj
 * @date 2023-03-15
 */
@RestController
@RequestMapping("/smc/appointment/conference")
@Tag(name = "【请填写功能名称】")
public class BusiSmcAppointmentConferenceController extends BaseController
{
    @Resource
    private IBusiSmcAppointmentConferenceService busiSmcAppointmentConferenceService;

    @Resource
    private IBusiSmcAppointmentConferencePaticipantService paticipantService;


    @Resource
    private IBusiTerminalService iBusiTerminalService;

    @Resource
    private IBusiSmcHistoryConferenceService smcHistoryConferenceService;

    @Resource
    private IBusiCallLegProfileService busiCallLegProfileService;
    @Resource
    private ICoSpaceService cospaceService;
    @Resource
    private SmcParticipantsService smcParticipantsService;
    /**

    /**
     * 查询【预约会议】列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询【请填写功能名称】列表")
    public RestResponse list(BusiSmcAppointmentConferenceQuery query)
    {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        List<BusiSmcAppointmentConference> list = busiSmcAppointmentConferenceService.selectBusiSmcAppointmentConferenceQuery(query);
        return getDataTable(list);
    }

    /**
     * 导出【预约会议】列表
     */
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出【请填写功能名称】列表")
    public RestResponse export(BusiSmcAppointmentConference busiSmcAppointmentConference)
    {
        List<BusiSmcAppointmentConference> list = busiSmcAppointmentConferenceService.selectBusiSmcAppointmentConferenceList(busiSmcAppointmentConference);
        ExcelUtil<BusiSmcAppointmentConference> util = new ExcelUtil<BusiSmcAppointmentConference>(BusiSmcAppointmentConference.class);
        return util.exportExcel(list, "conference");
    }

    /**
     * 获取【预约会议】详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取【请填写功能名称】详细信息")
    public RestResponse getInfo(@PathVariable("id") Integer id)
    {

        BusiSmcAppointmentConference busiSmcAppointmentConference = busiSmcAppointmentConferenceService.selectBusiSmcAppointmentConferenceById(id);
        if(busiSmcAppointmentConference==null){
            return RestResponse.success();
        }
        BusiSmcAppointmentConferenceInfo info=new BusiSmcAppointmentConferenceInfo();
        BeanUtils.copyProperties(busiSmcAppointmentConference,info);
        BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant = new BusiSmcAppointmentConferencePaticipant();
        busiSmcAppointmentConferencePaticipant.setAppointmentId(id);
        List<BusiSmcAppointmentConferencePaticipant> paticipantList = paticipantService.selectBusiSmcAppointmentConferencePaticipantListByAppointId(id);
        info.setTemplateTerminalList(paticipantList);
        return RestResponse.success(info);
    }

    /**
     * 新增预约会议
     */
    @Log(title = "新增预约会议", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增【请填写功能名称】")
    @Transactional
    public RestResponse add(@RequestBody BusiSmcAppointmentConferenceRequest appointmentConferenceRequest)
    {
        String conferenceTimeType = appointmentConferenceRequest.getConferenceTimeType();

        String scheduleStartTime = appointmentConferenceRequest.getScheduleStartTime();
        String s = UTCTimeFormatUtil.changeUTCTimeStr(appointmentConferenceRequest.getScheduleStartTime(),8);
        appointmentConferenceRequest.setScheduleStartTime(s);

        BusiSmcAppointmentConference busiSmcAppointmentConference = new BusiSmcAppointmentConference();
        BeanUtils.copyProperties(appointmentConferenceRequest,busiSmcAppointmentConference);

        List<TemplateTerminal> templateTerminalList = appointmentConferenceRequest.getTemplateTerminalList();
        List<ParticipantRspDto> templateParticipants = new ArrayList<>();
        List<BusiSmcAppointmentConferencePaticipant> paticipantList=new ArrayList<>();
        if (!CollectionUtils.isEmpty(templateTerminalList)) {
            for (TemplateTerminal templateTerminal : templateTerminalList) {
                BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(templateTerminal.getId());
                if (!Objects.isNull(busiTerminal)) {
                    ParticipantRspDto participantRspDto = new ParticipantRspDto();
                    String number = busiTerminal.getNumber();
                    participantRspDto.setName(busiTerminal.getName());
                    participantRspDto.setUri(number);
                    participantRspDto.setIpProtocolType(2);
                    participantRspDto.setDialMode("OUT");
                    participantRspDto.setVoice(false);
                    participantRspDto.setRate(0);
                    if(Objects.equals(busiTerminal.getId(),appointmentConferenceRequest.getMasterTerminalId())){
                        participantRspDto.setMainParticipant(true);
                    }else {
                        participantRspDto.setMainParticipant(false);
                    }
                    if (StringUtils.isBlank(number)) {
                        participantRspDto.setUri(busiTerminal.getIp());
                    }
                    if (StringUtils.isNotBlank(busiTerminal.getIp()) && Strings.hasText(busiTerminal.getNumber())) {
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                    }
                    if (TerminalType.isFSBC(busiTerminal.getType())) {
                        if (StringUtils.isNotBlank(busiTerminal.getIp())) {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp()+":5060");
                        } else {
                            BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                            String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp+":5060");
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
                    templateParticipants.add(participantRspDto);
                    //保存关系
                    BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant = new BusiSmcAppointmentConferencePaticipant();
                    busiSmcAppointmentConferencePaticipant.setTerminalId(templateTerminal.getId());
                    busiSmcAppointmentConferencePaticipant.setSmcnumber(participantRspDto.getUri());
                    busiSmcAppointmentConferencePaticipant.setWeight(templateTerminal.getWeight());
                    busiSmcAppointmentConferencePaticipant.setTerminalName(busiTerminal.getName());
                    busiSmcAppointmentConferencePaticipant.setTerminalDeptId(busiTerminal.getDeptId());
                    paticipantList.add(busiSmcAppointmentConferencePaticipant);

                }
            }
        }
        SmcAppointmentConferenceRequest request = buildSmcAppointmentConference(busiSmcAppointmentConference);
        request.setParticipants(templateParticipants);

        Long deptId = busiSmcAppointmentConference.getDeptId();
        SysDept sysDept = SysDeptCache.getInstance().get(deptId);
        Date date = new Date();
        if(Objects.equals(conferenceTimeType, ConferenceTimeType.INSTANT_CONFERENCE.name())){
            request.getConference().setScheduleStartTime(UTCTimeFormatUtil.changeUTCTimeStr(DateUtils.formatTo("yyyy-MM-dd HH:mm:ss",date),8));
        }

        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);
        SmcAppointmentConferenceContext conferenceContext = bridge.getSmcConferencesInvoker().appointmentConferenceAdd(request, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        SmcBridgeCache.getInstance().putAppointmentConferenceBridge(conferenceContext.getConference().getId(), bridge);



        busiSmcAppointmentConference.setCreateTime(new Date());
        busiSmcAppointmentConference.setConferenceId(conferenceContext.getConference().getId());
        busiSmcAppointmentConference.setAccessCode(conferenceContext.getMultiConferenceService().getAccessCode());
        busiSmcAppointmentConference.setCreateHyAdmin(conferenceContext.getConference().getUsername());
      //  busiSmcAppointmentConference.setCreateUserId(AuthenticationUtil.getUserId());
        busiSmcAppointmentConference.setStartDate(scheduleStartTime);
        busiSmcAppointmentConference.setAccountName(conferenceContext.getConference().getAccountName());
        busiSmcAppointmentConference.setUsername(conferenceContext.getConference().getUsername());
        busiSmcAppointmentConference.setToken(conferenceContext.getConference().getToken());
        busiSmcAppointmentConference.setStage(conferenceContext.getConference().getStage());
        busiSmcAppointmentConference.setLegacyId(conferenceContext.getConference().getLegacyId());
        busiSmcAppointmentConference.setGuestLink(conferenceContext.getMultiConferenceService().getGuestLink());
        busiSmcAppointmentConference.setChairmanLink(conferenceContext.getMultiConferenceService().getChairmanLink());
        busiSmcAppointmentConference.setCategory(conferenceContext.getConference().getCategory());
        busiSmcAppointmentConference.setActive(conferenceContext.getConference().getActive()==true?1:2);
        busiSmcAppointmentConference.setCategory(conferenceContext.getConference().getCategory());
        busiSmcAppointmentConference.setOrganizationName(sysDept.getDeptName());
        busiSmcAppointmentConference.setDeptId(appointmentConferenceRequest.getDeptId());
        int i = busiSmcAppointmentConferenceService.insertBusiSmcAppointmentConference(busiSmcAppointmentConference);

        if(Objects.equals(conferenceTimeType, ConferenceTimeType.INSTANT_CONFERENCE.name())){
            BusiSmcHistoryConference historyConference = new BusiSmcHistoryConference();
            historyConference.setConferenceId(conferenceContext.getConference().getId());
            historyConference.setCreateTime(new Date());
            historyConference.setDeptId(deptId);
            historyConference.setEndStatus(2);
            historyConference.setConferenceCode(conferenceContext.getMultiConferenceService().getAccessCode());
            historyConference.setSubject(conferenceContext.getConference().getSubject());
            historyConference.setConferenceAvcType(appointmentConferenceRequest.getType());
            historyConference.setStartTime(date);
            historyConference.setDuration(appointmentConferenceRequest.getDuration());
            smcHistoryConferenceService.insertBusiSmcHistoryConference(historyConference);

            try {
                BusiHistoryConference busiHistoryConference =   BeanFactory.getBean(IBusiSmc3HistoryConferenceService.class).saveHistory(historyConference,appointmentConferenceRequest.getRate(),ConferenceTimeType.INSTANT_CONFERENCE);
                // 历史call保存
                String callId = com.paradisecloud.common.utils.uuid.UUID.randomUUID().toString();
                BusiHistoryCall busiHistoryCall = new BusiHistoryCall();
                busiHistoryCall.setCallId(callId);
                busiHistoryCall.setCoSpace(historyConference.getConferenceCode());
                busiHistoryCall.setDeptId(historyConference.getDeptId());
                busiHistoryCall.setCreateTime(new Date());
                busiHistoryCall.setHistoryConferenceId(busiHistoryConference.getId());
                BeanFactory.getBean(BusiHistoryCallMapper.class).insertBusiHistoryCall(busiHistoryCall);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(!CollectionUtils.isEmpty(paticipantList)){
            for (BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant : paticipantList) {
                busiSmcAppointmentConferencePaticipant.setConferenceId(conferenceContext.getConference().getId());
                busiSmcAppointmentConferencePaticipant.setAppointmentId(busiSmcAppointmentConference.getId());
                paticipantService.insertBusiSmcAppointmentConferencePaticipant(busiSmcAppointmentConferencePaticipant);
            }
        }
        return RestResponse.success(busiSmcAppointmentConference);
    }

    /**
     * 修改【预约会议】
     */
    @Log(title = "修改预约会议", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改【请填写功能名称】")
    @Transactional
    public RestResponse edit(@RequestBody BusiSmcAppointmentConferenceRequest appointmentConferenceRequest)
    {
        BusiSmcAppointmentConference appointmentConference = busiSmcAppointmentConferenceService.selectBusiSmcAppointmentConferenceById(appointmentConferenceRequest.getId());

        String conferenceTimeType = appointmentConferenceRequest.getConferenceTimeType();
        String scheduleStartTime = appointmentConferenceRequest.getScheduleStartTime();
        String s = UTCTimeFormatUtil.changeUTCTimeStr(appointmentConferenceRequest.getScheduleStartTime(),8);
        appointmentConferenceRequest.setScheduleStartTime(s);

        BusiSmcAppointmentConference busiSmcAppointmentConference = new BusiSmcAppointmentConference();
        BeanUtils.copyProperties(appointmentConferenceRequest,busiSmcAppointmentConference);

        List<TemplateTerminal> templateTerminalList = appointmentConferenceRequest.getTemplateTerminalList();
        List<ParticipantRspDto> templateParticipants = new ArrayList<>();
        List<BusiSmcAppointmentConferencePaticipant> paticipantList=new ArrayList<>();
        if (!CollectionUtils.isEmpty(templateTerminalList)) {
            for (TemplateTerminal templateTerminal : templateTerminalList) {
                BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(templateTerminal.getId());
                if (!Objects.isNull(busiTerminal)) {
                    ParticipantRspDto participantRspDto = new ParticipantRspDto();
                    String number = busiTerminal.getNumber();
                    participantRspDto.setName(busiTerminal.getName());
                    participantRspDto.setUri(number);
                    participantRspDto.setIpProtocolType(2);
                    participantRspDto.setDialMode("OUT");
                    participantRspDto.setVoice(false);
                    participantRspDto.setRate(0);
                    participantRspDto.setMainParticipant(false);
                    if (StringUtils.isBlank(number)) {
                        participantRspDto.setUri(busiTerminal.getIp());
                    }
                    if (StringUtils.isNotBlank(busiTerminal.getIp()) && Strings.hasText(busiTerminal.getNumber())) {
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                    }
                    if (TerminalType.isFSBC(busiTerminal.getType())) {
                        if (StringUtils.isNotBlank(busiTerminal.getIp())) {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp()+":5060");
                        } else {
                            BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                            String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp+":5060");
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
                    templateParticipants.add(participantRspDto);
                    //保存关系
                    BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant = new BusiSmcAppointmentConferencePaticipant();
                    busiSmcAppointmentConferencePaticipant.setTerminalId(templateTerminal.getId());
                    busiSmcAppointmentConferencePaticipant.setSmcnumber(participantRspDto.getUri());
                    busiSmcAppointmentConferencePaticipant.setWeight(templateTerminal.getWeight());
                    busiSmcAppointmentConferencePaticipant.setTerminalName(busiTerminal.getName());
                    busiSmcAppointmentConferencePaticipant.setAppointmentId(appointmentConference.getId());
                    busiSmcAppointmentConferencePaticipant.setConferenceId(appointmentConference.getConferenceId());
                    busiSmcAppointmentConferencePaticipant.setTerminalDeptId(busiTerminal.getDeptId());
                    paticipantList.add(busiSmcAppointmentConferencePaticipant);

                }
            }
        }
        SmcAppointmentConferenceRequest request = buildSmcAppointmentConference(busiSmcAppointmentConference);
        request.setParticipants(templateParticipants);
        request.getConference().setId(appointmentConference.getConferenceId());
        Long deptId = busiSmcAppointmentConference.getDeptId();
        Date date = new Date();
        if(Objects.equals(conferenceTimeType, ConferenceTimeType.INSTANT_CONFERENCE.name())){
            request.getConference().setScheduleStartTime(UTCTimeFormatUtil.changeUTCTimeStr(DateUtils.formatTo("yyyy-MM-dd HH:mm:ss",date),8));
        }
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);
        SmcAppointmentConferenceContext conferenceContext = bridge.getSmcConferencesInvoker().appointmentConferenceEdit(appointmentConference.getConferenceId(),request, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        SmcBridgeCache.getInstance().putAppointmentConferenceBridge(conferenceContext.getConference().getId(), bridge);

        if(Objects.equals(conferenceTimeType, ConferenceTimeType.INSTANT_CONFERENCE.name())){
            BusiSmcHistoryConference historyConference = new BusiSmcHistoryConference();
            historyConference.setConferenceId(conferenceContext.getConference().getId());
            historyConference.setCreateTime(new Date());
            historyConference.setDeptId(deptId);
            historyConference.setEndStatus(2);
            historyConference.setConferenceCode(conferenceContext.getMultiConferenceService().getAccessCode());
            historyConference.setSubject(conferenceContext.getConference().getSubject());
            historyConference.setConferenceAvcType(appointmentConferenceRequest.getType());
            historyConference.setStartTime(date);
            historyConference.setDuration(appointmentConferenceRequest.getDuration());
            smcHistoryConferenceService.insertBusiSmcHistoryConference(historyConference);

            try {
                BusiHistoryConference busiHistoryConference =   BeanFactory.getBean(IBusiSmc3HistoryConferenceService.class).saveHistory(historyConference,appointmentConferenceRequest.getRate(),ConferenceTimeType.INSTANT_CONFERENCE);
                // 历史call保存
                String callId = com.paradisecloud.common.utils.uuid.UUID.randomUUID().toString();
                BusiHistoryCall busiHistoryCall = new BusiHistoryCall();
                busiHistoryCall.setCallId(callId);
                busiHistoryCall.setCoSpace(historyConference.getConferenceCode());
                busiHistoryCall.setDeptId(historyConference.getDeptId());
                busiHistoryCall.setCreateTime(new Date());
                busiHistoryCall.setHistoryConferenceId(busiHistoryConference.getId());
                BeanFactory.getBean(BusiHistoryCallMapper.class).insertBusiHistoryCall(busiHistoryCall);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        busiSmcAppointmentConference.setStartDate(scheduleStartTime);

        paticipantService.deleteBusiSmcAppointmentConferencePaticipantByConferenceId(appointmentConferenceRequest.getConferenceId());
        if(!paticipantList.isEmpty()){
            for (BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant : paticipantList) {

                busiSmcAppointmentConferencePaticipant.setConferenceId(appointmentConferenceRequest.getConferenceId());
                paticipantService.insertBusiSmcAppointmentConferencePaticipant(busiSmcAppointmentConferencePaticipant);
            }
        }
        busiSmcAppointmentConference.setId(appointmentConferenceRequest.getId());
        return toAjax(busiSmcAppointmentConferenceService.updateBusiSmcAppointmentConference(busiSmcAppointmentConference));
    }



    /**
     * 删除预约会议
     */
    @Log(title = "【删除预约会议】", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    @Operation(summary = "删除【请填写功能名称】")
    public RestResponse remove(@PathVariable Integer id)
    {


        BusiSmcAppointmentConference busiSmcAppointmentConference = busiSmcAppointmentConferenceService.selectBusiSmcAppointmentConferenceById(id);
        if(busiSmcAppointmentConference!=null){
            String conferenceId = busiSmcAppointmentConference.getConferenceId();
            Long deptId = busiSmcAppointmentConference.getDeptId();
            SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);
             bridge.getSmcConferencesInvoker().deleteConference(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }
        return toAjax(busiSmcAppointmentConferenceService.deleteBusiSmcAppointmentConferenceById(id));
    }

    @Resource
    private SmcConferenceService smcConferenceService;

    /**
     * 模板的 ID 找到会议模板
     * @return
     */
    @GetMapping("/info/{conferenceId}")
    public RestResponse getappointmentConferencesSimple(@PathVariable String conferenceId) {

        String smcConferenceInfoById = smcConferenceService.getSmcConferenceInfoById(conferenceId);
        if (StringUtils.isNotBlank(smcConferenceInfoById) && smcConferenceInfoById.contains(ConstAPI.ERRORNO)) {
            SmcErrorResponse smcErrorResponse = JSON.parseObject(smcConferenceInfoById, SmcErrorResponse.class);
            throw new CustomException("会议信息查询错误"+smcErrorResponse.getErrorDesc());
        }
        SmcConferenceContext smcConferenceContext = JSON.parseObject(smcConferenceInfoById, SmcConferenceContext.class);
        DetailConference detailConference = smcConferenceService.getDetailConferenceInfoById(conferenceId);
        smcConferenceContext.setDetailConference(detailConference);
        List<SmcParitipantsStateRep.ContentDTO> contents=new ArrayList<>();
        BusiSmcAppointmentConference busiSmcAppointmentConference = busiSmcAppointmentConferenceService.selectBusiSmcAppointmentConferenceByConferenceId(conferenceId);
        if(busiSmcAppointmentConference==null){
            return null;
        }
        Integer id = busiSmcAppointmentConference.getId();
        smcConferenceContext.setDeptId(busiSmcAppointmentConference.getDeptId());
        BusiSmcAppointmentConferencePaticipant busiSmcAppointmentConferencePaticipant = new BusiSmcAppointmentConferencePaticipant();
        busiSmcAppointmentConferencePaticipant.setAppointmentId(id);
        List<BusiSmcAppointmentConferencePaticipant> paticipantList = paticipantService.selectBusiSmcAppointmentConferencePaticipantListByAppointId(id);
        List<ParticipantRspDto> Participants = smcConferenceContext.getParticipants();
        if (!CollectionUtils.isEmpty(Participants)) {
            Participants.stream().forEach(p -> {
                SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
                Optional<BusiSmcAppointmentConferencePaticipant> first = paticipantList.stream().filter(m -> Objects.equals(p.getUri(), m.getSmcnumber())).findFirst();
                if (first.isPresent()) {
                    BusiSmcAppointmentConferencePaticipant smcTemplateTerminal = first.get();

                    contentDTO.setDeptId(smcTemplateTerminal.getTerminalDeptId());
                    contentDTO.setTerminalId(smcTemplateTerminal.getTerminalId());
                    BusiTerminal terminal = iBusiTerminalService.selectBusiTerminalById(smcTemplateTerminal.getTerminalId());
                    if (terminal != null) {
                        Boolean onlineState = terminal.getOnlineStatus() == 1 ? true : false;
                        contentDTO.setTerminalOnline(onlineState);
                        contentDTO.setTerminalType(terminal.getType());
                        if (TerminalType.isMcuTemplateCisco(terminal.getType())) {
                            contentDTO.setIsCascade(true);
                            contentDTO.setTerminalTypeName(TerminalType.MCU_TEMPLATE_CISCO.getDisplayName());
                            if (StringUtils.isNotBlank(smcConferenceContext.getConference().getId())) {
                                Map<String, String> stringStringMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(smcConferenceContext.getConference().getId());
                                if(Objects.isNull(stringStringMap)){
                                    Map<String, String> uirMap = new HashMap<>();
                                    uirMap.put(contentDTO.getGeneralParam().getId(), contentDTO.getGeneralParam().getUri());
                                    SmcConferenceContextCache.getInstance().getCascadeConference().put(smcConferenceContext.getConference().getId(), uirMap);
                                }else {
                                    stringStringMap.put(contentDTO.getGeneralParam().getId(), contentDTO.getGeneralParam().getUri());
                                }
                            }
                        }

                        if (TerminalType.isFmeTemplate(terminal.getType())) {
                            contentDTO.setIsCascade(true);
                            contentDTO.setTerminalTypeName(TerminalType.FME_TEMPLATE.getDisplayName());
                            String fmeConfId = AesEnsUtils.getAesEncryptor().encryptToHex(String.valueOf(terminal.getNumber()));
                            contentDTO.setConferenceIdFme(fmeConfId);
                            if (StringUtils.isNotBlank(smcConferenceContext.getConference().getId())) {
                                Map<String, String> stringStringMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(smcConferenceContext.getConference().getId());
                                if(Objects.isNull(stringStringMap)){
                                    Map<String, String> uirMap = new HashMap<>();
                                    uirMap.put(contentDTO.getGeneralParam().getId(), contentDTO.getGeneralParam().getUri());
                                    SmcConferenceContextCache.getInstance().getCascadeConference().put(smcConferenceContext.getConference().getId(), uirMap);
                                }else {
                                    stringStringMap.put(contentDTO.getGeneralParam().getId(), contentDTO.getGeneralParam().getUri());
                                }
                            }
                        }

                    }
                }
                ParticipantState participantState = new ParticipantState();
                participantState.setParticipantId(p.getId());
                p.setParticipantState(participantState);

                SmcParitipantsStateRep.ContentDTO.GeneralParamDTO paramDTO = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
                paramDTO.setName(p.getName());
                paramDTO.setUri(p.getUri());
                contentDTO.setState(participantState);
                contentDTO.setGeneralParam(paramDTO);

                contents.add(contentDTO);
            });
        }

        smcConferenceContext.setContent(contents);

        List<SmcParitipantsStateRep.ContentDTO> collect = smcConferenceContext.getContent().stream().sorted(Comparator.comparing(SmcParitipantsStateRep.ContentDTO::getWeight, Comparator.reverseOrder())).collect(Collectors.toList());
        smcConferenceContext.setContent(collect);


        return  RestResponse.success(smcConferenceContext);
    }

    private String getProfileId(Long deptId, FmeBridge fmeBridge, String profileId) {
        BusiCallLegProfile con = new BusiCallLegProfile();
        con.setDeptId(deptId);
        List<BusiCallLegProfile> clps = busiCallLegProfileService.selectBusiCallLegProfileList(con);
        if (!ObjectUtils.isEmpty(clps)) {
            for (BusiCallLegProfile busiCallLegProfile : clps) {
                CallLegProfile clp = fmeBridge.getDataCache().getCallLegProfile(busiCallLegProfile.getCallLegProfileUuid());
                if (clp != null) {
                    if (!clp.getRxAudioMute()) {
                        profileId = busiCallLegProfile.getCallLegProfileUuid();
                        break;
                    }
                }
            }
            if (profileId == null) {
                profileId = busiCallLegProfileService.createDefaultCalllegProfileIsMute(fmeBridge, deptId, false);
            }
        } else {
            profileId = busiCallLegProfileService.createDefaultCalllegProfileIsMute(fmeBridge, deptId, false);
        }
        return profileId;
    }

    private void calltosmc(String conferenceId, FmeBridge fmeBridge, CoSpace coSpace) {
        CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
        coSpaceParamBuilder.name("SMC-MONITOR");
        coSpaceParamBuilder.panePlacementHighestImportance(100);
        coSpaceParamBuilder.defaultLayout("speakerOnly");
        cospaceService.updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);
        String number = coSpace.getUri();
        String uri=number+"@"+ fmeBridge.getBusiFme().getIp();
        CreateParticipantsReq createParticipantsReq=new CreateParticipantsReq();
        createParticipantsReq.setConferenceId(conferenceId);
        List<ParticipantReqDto> participants=new ArrayList<>(1);
        ParticipantReqDto participantReqDto = new ParticipantReqDto();
        participantReqDto.setName("会议监控");
        participantReqDto.setUri(uri);
        participants.add(participantReqDto);
        createParticipantsReq.setParticipants(participants);
        smcParticipantsService.addParticipants(createParticipantsReq);
        DetailConference detailConferenceInfoById = smcConferenceService.getDetailConferenceInfoById(conferenceId);
        String accessCode = detailConferenceInfoById.getConferenceUiParam().getAccessCode();
        Participant smcParticipant=null;
        //查找到这个与会者并设置为权重100,
        do{
            try {
                ParticipantsResponse participantsResponse = fmeBridge.getCallInvoker().getParticipants(coSpace.getCallId(), 0);
                if(participantsResponse!=null){
                    ActiveParticipantsResponse participantsResponseParticipants = participantsResponse.getParticipants();
                    if(participantsResponseParticipants!=null){
                        ArrayList<Participant> participant = participantsResponseParticipants.getParticipant();
                        if(!CollectionUtils.isEmpty(participant)){
                            Optional<Participant> first = participant.stream().filter(p -> p.getUri().startsWith(accessCode + "@")).findFirst();
                            if(first.isPresent()){
                                smcParticipant = first.get();
                                fmeBridge.getParticipantInvoker().updateParticipant(smcParticipant.getId(), new ParticipantParamBuilder().importance(100).rxAudioMute(false).rxAudioMute(false).build());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("会议监控修改权重失败"+e.getMessage());
                break;
            }
        }while (smcParticipant!=null);

        SmcConferenceContextCache.getInstance().getMonitorParticipantMap().put(conferenceId,coSpace);
    }

}

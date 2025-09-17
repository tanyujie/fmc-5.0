package com.paradisecloud.fcm.web.controller.cascade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.com.fcm.smc.modle.response.SmcCreateTemplateRep;
import com.paradisecloud.com.fcm.smc.modle.response.SmcErrorResponse;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.com.fcm.smc.modle.response.SmcTemplateListRep;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryCallMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.fcm.smc.cache.modle.SmcConferenceContextCache;
import com.paradisecloud.fcm.telep.cache.TeleParticipantCache;
import com.paradisecloud.fcm.telep.dao.model.BusiTeleParticipant;
import com.paradisecloud.fcm.telep.model.busi.ConferencesResponse;
import com.paradisecloud.fcm.telep.model.busi.TeleConference;
import com.paradisecloud.fcm.telep.model.busi.participants.TeleParticipant;
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
import com.paradisecloud.fcm.web.model.smc.BusiSmcTemplateConferenceRep;
import com.paradisecloud.fcm.web.model.smc.BusiSmcTemplateConferenceRequest;
import com.paradisecloud.fcm.web.model.smc.ConferenceEnd;
import com.paradisecloud.fcm.web.model.tele.ParticipantFeccReqVo;
import com.paradisecloud.fcm.web.model.tele.ParticipantVo;
import com.paradisecloud.fcm.web.model.tele.TeleCallTheRoll;
import com.paradisecloud.fcm.web.utils.AuthenticationUtil;
import com.paradisecloud.smc.dao.model.BusiSmcDeptTemplate;
import com.paradisecloud.smc.dao.model.BusiSmcHistoryConference;
import com.paradisecloud.smc.dao.model.BusiSmcTemplateConference;
import com.paradisecloud.smc.dao.model.SmcTemplateTerminal;
import com.paradisecloud.smc.service.*;
import com.paradisecloud.smc.service.busi.ConferenceSMCService;
import com.paradisecloud.smc.service.task.SmcDelayTaskService;
import com.paradisecloud.smc.service.task.SmcMeetingRoomRegTask;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.service.ISysDeptService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;
import io.jsonwebtoken.lang.Strings;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SMC为主的级联FME会议
 *
 * @author nj
 * @date 2022/8/26 16:48
 */
@RestController
@RequestMapping("/cascade")
@Slf4j
public class SmcCascadeConferencesController extends BaseController {
    //public static final String ConstAPI.MCU_SMC_ID = "07551260889551";
    public static final String DISCONNECTED = "disconnected";
    public static final String VOICE_ACTIVATED = "voiceActivated";
    public static final String PARTICIPANT = "participant";
    public static final String SVC_VIDEO_RESOLUTION = "MPI_1080P";//MPI_720P
    public static final String VIDEO_RESOLUTION = "MPI_1080P";//MPI_720P60
    @Resource
    private TemplateService templateService;
    @Resource
    private ConferenceSMCService conferenceSMCService;
    @Resource
    private SmcParticipantsService smcParticipantsService;

    @Resource
    private SmcConferenceService smcConferenceService;
    @Resource
    private BusiSmcDeptTemplateService busiSmcDeptTemplateService;

    @Resource
    private IBusiTerminalService iBusiTerminalService;

    @Resource
    private SmcTemplateTerminalService smcTemplateTerminalService;

    @Resource
    private IBusiSmcTemplateConferenceService smcTemplateConferenceService;

    @Resource
    private IBusiParaticipantsService iBusiParaticipantsService;

    @Resource
    private PlatformTransactionManager transactionManager;


    @Resource
    private ISysDeptService sysDeptService;
    @Resource
    private SmcUserService smcUserService;
    @Resource
    private SmcDelayTaskService smcDelayTaskService;
    @Resource
    private IBusiTeleConferenceService teleConferenceService;
    @Resource
    private IBusiSmc3HistoryConferenceService smc3HistoryConferenceService;
    @Resource
    private IBusiSmcHistoryConferenceService smcHistoryConferenceService;

    @Resource
    private IBusiTeleParticipantService busiTeleParticipantService;

    public static <T> List<T> copy(List<?> list, Class<T> clazz) {
        String oldOb = JSON.toJSONString(list);
        return JSON.parseArray(oldOb, clazz);
    }

    public static String localToUTC(String localTimeStr) {
        try {
            Date localDate = getLocalSDF().parse(localTimeStr);
            return getUTCSDF().format(localDate) + " UTC";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SimpleDateFormat getLocalSDF() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    private static SimpleDateFormat getUTCSDF() {
        SimpleDateFormat utcSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        utcSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcSDF;
    }

    @GetMapping("/activeConferencesPages")
    @Operation(summary = "首页活跃会议室列表")
    public RestResponse activeConferencesPages(@RequestParam(value = "searchKey", required = false) String searchKey,
                                               @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
                                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = loginUser.getUser().getDeptId();
        List<SysDept> sds = new ArrayList<>();
        if (deptId != null) {
            SysDept con = new SysDept();
            con.setDeptId(deptId);
            sds = sysDeptService.selectDeptList(con);
        }

        List<DetailConference> contextBaseList = new ArrayList<>();
        PageHelper.startPage(pageIndex, pageSize);
        List<BusiSmcHistoryConference> busiSmcHistoryConferences = smcHistoryConferenceService.selectBusiSmcHistoryConferenceNotTemplate(searchKey, sds);
        if (!CollectionUtils.isEmpty(busiSmcHistoryConferences)) {
            for (BusiSmcHistoryConference busiSmcHistoryConference : busiSmcHistoryConferences) {
                String conferenceId = busiSmcHistoryConference.getConferenceId();
                Long deptId1 = busiSmcHistoryConference.getDeptId();
                SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId1);
                DetailConference detailConference = bridge.getSmcConferencesInvoker().getDetailConferencesById(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                if (detailConference != null) {
                    detailConference.getConferenceState().setTemplateId(busiSmcHistoryConference.getTemplateId());
                    detailConference.getConferenceState().setType(busiSmcHistoryConference.getConferenceAvcType());
                    String chairmanId = detailConference.getConferenceState().getChairmanId();
                    if (StringUtils.isNotBlank(chairmanId)) {
                        //查询会场详情
                        ParticipantRspDto participantsDetailInfoDto = bridge.getSmcParticipantsInvoker().getParticipantsDetailInfoDto(conferenceId, chairmanId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                        detailConference.getConferenceState().setChairman(participantsDetailInfoDto);
                    }

                    String scheduleStartTime = detailConference.getConferenceUiParam().getScheduleStartTime();
                    try {
                        Date date = UTCTimeFormatUtil.utcToLocal(scheduleStartTime);
                        detailConference.getConferenceUiParam().setLocalTime(DateUtil.convertDateToString(date, "yyyy-MM-dd HH:mm:ss"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    contextBaseList.add(detailConference);
                }
            }
        }
        return RestResponse.success(0, "查询成功", new PageInfo<>(contextBaseList));
    }

    /**
     * 查询FME会议信息
     *
     * @return
     */
    @GetMapping("/conference/fme/{conferenceId}")
    public RestResponse getConferenceFme(@PathVariable String conferenceId) {
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(conferenceId);
        return RestResponse.success(conferenceContext);
    }

    /**
     * 创建模板会议
     */
    @PostMapping("/conferences/templates/create")
    public RestResponse addmeetingRooms(@RequestBody JSONObject jsonObj) {

        String type = jsonObj.getString("templateConferenceType");
        Assert.isTrue(type != null, "会议模板不能为空！");
        Long deptId = jsonObj.getLong("deptId");
        Assert.isTrue(deptId != null, "会议模板部门ID不能为空！");

        if (Objects.equals("smc", type)) {
            JSONObject smcConferenceTemplateRquestjson = jsonObj.getJSONObject("smcConferenceTemplateRquest");
            Long masterTerminalId = smcConferenceTemplateRquestjson.getLong("masterTerminalId");
            SmcConferenceTemplate smcConferenceTemplateRquest = smcConferenceTemplateRquestjson.toJavaObject(SmcConferenceTemplate.class);
            smcConferenceTemplateRquest.setGuestPassword(null);
            smcConferenceTemplateRquest.setChairmanPassword(null);
            SmcConferenceTemplate.ConferenceCapabilitySettingDTO conferenceCapabilitySetting = smcConferenceTemplateRquest.getConferenceCapabilitySetting();
            conferenceCapabilitySetting.setSvcVideoResolution(SVC_VIDEO_RESOLUTION);
            conferenceCapabilitySetting.setVideoResolution(VIDEO_RESOLUTION);
            conferenceCapabilitySetting.setMediaEncrypt("NOT_ENCRYPT_MODE");
            smcConferenceTemplateRquest.setConferenceCapabilitySetting(conferenceCapabilitySetting);
            List<ParticipantRspDto> templateParticipants = new ArrayList<>();
            List<Long> templateParticipantsIds = smcConferenceTemplateRquest.getTemplateParticipantsIds();
            List<SmcTemplateTerminal> templateTerminals = new ArrayList<>();
            List<TemplateTerminal> templateTerminalList = smcConferenceTemplateRquest.getTemplateTerminalList();
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
                        participantRspDto.setMainParticipant(Objects.equals(busiTerminal.getId(), masterTerminalId));
                        if (StringUtils.isBlank(number)) {
                            participantRspDto.setUri(busiTerminal.getIp());
                        }
                        if (StringUtils.isNotBlank(busiTerminal.getIp()) && Strings.hasText(busiTerminal.getNumber())) {
                            participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                        }
                        if (TerminalType.isFSBC(busiTerminal.getType())) {
                            if (StringUtils.isNotBlank(busiTerminal.getIp())) {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
                            } else {
                                BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                                FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                                String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                                Integer sipPort = fsbcBridge.getBusiFsbcRegistrationServer().getSipPort();
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp);
                            }
                        }
                        if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                            BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(busiFreeSwitchDept.getServerId());

                            String callIp = fcmBridge.getBusiFreeSwitch().getIp();
                            Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                            if (callPort != null && callPort != 5060) {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":" + callPort);
                            } else {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp);
                            }


                        }
                        templateParticipants.add(participantRspDto);
                        //保存关系
                        SmcTemplateTerminal smcTemplateTerminal = new SmcTemplateTerminal();
                        smcTemplateTerminal.setTerminalId(templateTerminal.getId());
                        smcTemplateTerminal.setTerminalDeptId(busiTerminal.getDeptId());
                        smcTemplateTerminal.setSmcnumber(participantRspDto.getUri());
                        smcTemplateTerminal.setWeight(templateTerminal.getWeight());
                        templateTerminals.add(smcTemplateTerminal);

                    }


                }
            } else {
                if (!CollectionUtils.isEmpty(templateParticipantsIds)) {
                    for (Long id : templateParticipantsIds) {
                        BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(id);
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
                                if (org.apache.commons.lang3.StringUtils.isNotBlank(busiTerminal.getIp())) {
                                    participantRspDto.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
                                } else {
                                    BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                                    FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                                    String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                                    Integer sipPort = fsbcBridge.getBusiFsbcRegistrationServer().getSipPort();
                                    if (sipPort == null || sipPort == 5060) {
                                        participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp);
                                    } else {
                                        participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":" + sipPort);
                                    }
                                }
                            }
                            if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                                BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
                                FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(busiFreeSwitchDept.getServerId());
                                String callIp = fcmBridge.getBusiFreeSwitch().getIp();
                                Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                                if (callPort == null || callPort == 5060) {
                                    participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp);
                                } else {
                                    participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":" + callPort);
                                }

                            }

                            templateParticipants.add(participantRspDto);

                            //保存关系
                            SmcTemplateTerminal smcTemplateTerminal = new SmcTemplateTerminal();
                            smcTemplateTerminal.setTerminalId(id);
                            smcTemplateTerminal.setTerminalDeptId(busiTerminal.getDeptId());
                            smcTemplateTerminal.setSmcnumber(participantRspDto.getUri());
                            templateTerminals.add(smcTemplateTerminal);
                        }
                    }
                }
            }
            smcConferenceTemplateRquest.setTemplateParticipants(templateParticipants);
            SmcConferenceTemplate smcConferenceTemplate = templateService.addTemplateRoom(smcConferenceTemplateRquest, deptId, smcConferenceTemplateRquest.getMasterTerminalId());
            String conferenceTemplateId = smcConferenceTemplate.getId();
            //保存关系
            for (SmcTemplateTerminal smcTemplateTerminal : templateTerminals) {
                smcTemplateTerminal.setSmcTemplateId(conferenceTemplateId);
                smcTemplateTerminalService.add(smcTemplateTerminal);
            }

            return RestResponse.success(smcConferenceTemplate);
        }

        if (Objects.equals("smcCascade", type)) {
            JSONObject smcConferenceTemplateRquestjson = jsonObj.getJSONObject("smcConferenceTemplateRquest");
            SmcConferenceTemplate smcConferenceTemplateRquest = smcConferenceTemplateRquestjson.toJavaObject(SmcConferenceTemplate.class);
            smcConferenceTemplateRquest.setGuestPassword(null);
            smcConferenceTemplateRquest.setChairmanPassword(null);
            List<ParticipantRspDto> templateParticipants = new ArrayList<>();
            List<Long> templateParticipantsIds = smcConferenceTemplateRquest.getTemplateParticipantsIds();
            List<SmcTemplateTerminal> templateTerminals = new ArrayList<>();
            if (!CollectionUtils.isEmpty(templateParticipantsIds)) {
                for (Long id : templateParticipantsIds) {
                    BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(id);
                    if (!Objects.isNull(busiTerminal)) {
                        ParticipantRspDto participantRspDto = new ParticipantRspDto();
                        //templateParticipantReq.setEntryUuid(UUID.randomUUID().toString().toLowerCase());
                        String number = busiTerminal.getNumber();
                        participantRspDto.setName(busiTerminal.getName());
                        participantRspDto.setUri(number);
                        //templateParticipantReq.setRate(1920);
                        if (StringUtils.isBlank(number)) {
                            participantRspDto.setUri(busiTerminal.getIp());
                        }
                        if (StringUtils.isBlank(busiTerminal.getIp())) {
                            break;
                        }
                        if (StringUtils.isNotBlank(busiTerminal.getIp()) && Strings.hasText(busiTerminal.getNumber())) {
                            participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                        }
                        if (TerminalType.isFSBC(busiTerminal.getType())) {
                            if (org.apache.commons.lang3.StringUtils.isNotBlank(busiTerminal.getIp())) {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
                            } else {
                                BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                                FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                                String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                                Integer sipPort = fsbcBridge.getBusiFsbcRegistrationServer().getSipPort();
                                if (sipPort == null) {
                                    sipPort = 5060;
                                }
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":" + sipPort);
                            }
                        }
                        if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                            BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(busiFreeSwitchDept.getServerId());
                            String callIp = fcmBridge.getBusiFreeSwitch().getIp();
                            Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                            if (callPort == null) {
                                callPort = 5060;
                            }
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":" + callPort);
                        }
                        templateParticipants.add(participantRspDto);


                        //保存关系
                        SmcTemplateTerminal smcTemplateTerminal = new SmcTemplateTerminal();
                        smcTemplateTerminal.setTerminalId(id);
                        smcTemplateTerminal.setTerminalDeptId(busiTerminal.getDeptId());
                        smcTemplateTerminal.setSmcnumber(participantRspDto.getUri());
                        templateTerminals.add(smcTemplateTerminal);

                    }


                }
            }
            smcConferenceTemplateRquest.setTemplateParticipants(templateParticipants);
            SmcConferenceTemplate smcConferenceTemplate = templateService.addTemplateRoom(smcConferenceTemplateRquest, deptId, smcConferenceTemplateRquest.getMasterTerminalId());
            String conferenceTemplateId = smcConferenceTemplate.getId();
            //保存关系
            for (SmcTemplateTerminal smcTemplateTerminal : templateTerminals) {
                smcTemplateTerminal.setSmcTemplateId(conferenceTemplateId);
                smcTemplateTerminalService.add(smcTemplateTerminal);
            }

            return RestResponse.success(smcConferenceTemplate);
        }
        return RestResponse.success();

    }

    /**
     * 创建模板会议
     */
    @PostMapping("/conferences/templates/create/simple")
    public RestResponse createTemplate(@RequestBody BusiSmcTemplateConferenceRequest smcTemplateConferenceRequest) {
        Assert.isTrue(smcTemplateConferenceRequest.getDuration() != null, "会议时长不能为空！");
        Assert.isTrue(smcTemplateConferenceRequest.getSubject() != null, "会议主题不能为空！");
        Assert.isTrue(smcTemplateConferenceRequest.getDeptId() != null, "会议模板部门ID不能为空！");
        Long masterTerminalId = smcTemplateConferenceRequest.getMasterTerminalId();
        SmcConferenceTemplate smcConferenceTemplate = getSmcConferenceTemplate(smcTemplateConferenceRequest);
        List<ParticipantRspDto> templateParticipants = new ArrayList<>();
        List<SmcTemplateTerminal> templateTerminals = new ArrayList<>();
        List<TemplateTerminal> templateTerminalList = smcTemplateConferenceRequest.getTemplateTerminalList();
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
                    participantRspDto.setMainParticipant(Objects.equals(busiTerminal.getId(), masterTerminalId));
                    if (StringUtils.isBlank(number)) {
                        if (TerminalType.isCisco(busiTerminal.getType())) {
                            participantRspDto.setUri(busiTerminal.getName() + "@" + busiTerminal.getIp());
                        } else {
                            participantRspDto.setUri(busiTerminal.getIp());
                        }

                    }
                    if (StringUtils.isNotBlank(busiTerminal.getIp()) && Strings.hasText(busiTerminal.getNumber())) {
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                    }
                    if (TerminalType.isFSBC(busiTerminal.getType())) {
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(busiTerminal.getIp() + ":5060")) {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
                        } else {
                            BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                            String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            Integer sipPort = fsbcBridge.getBusiFsbcRegistrationServer().getSipPort();
                            if (sipPort == null || sipPort == 5060) {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":5060");
                            } else {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":" + sipPort);
                            }

                        }

                        //注册到外部会议室
                        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(smcTemplateConferenceRequest.getDeptId());
                        SmcMeetingRoomRegTask smcMeetingRoomRegTask = new SmcMeetingRoomRegTask(participantRspDto.getUri(), busiTerminal.getName(), participantRspDto.getUri(), 200, bridge, smcUserService, busiTerminal);
                        smcDelayTaskService.addTask(smcMeetingRoomRegTask);
                    }
                    if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                        BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(busiFreeSwitchDept.getServerId());
                        String callIp = fcmBridge.getBusiFreeSwitch().getIp();
                        Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                        if (callPort == null || callPort == 5060) {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":5060");
                        } else {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":" + callPort);
                        }
                        String uri = participantRspDto.getUri();
                        //注册到外部会议室
                        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(smcTemplateConferenceRequest.getDeptId());
                        SmcMeetingRoomRegTask smcMeetingRoomRegTask = new SmcMeetingRoomRegTask(uri, busiTerminal.getName(), uri, 200, bridge, smcUserService, busiTerminal);
                        smcDelayTaskService.addTask(smcMeetingRoomRegTask);

                    }

                    if (TerminalType.isFmeTemplate(busiTerminal.getType())) {
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp() + ":5060");
                        BusiTemplateConferenceMapper templateConferenceMapper = BeanFactory.getBean(BusiTemplateConferenceMapper.class);
                        BusiTemplateConference templateConference = new BusiTemplateConference();
                        templateConference.setConferenceNumber(Long.valueOf(busiTerminal.getNumber()));
                        List<BusiTemplateConference> templateConferences = templateConferenceMapper.selectBusiTemplateConferenceList(templateConference);
                        if (!CollectionUtils.isEmpty(templateConferences)) {
                            String conferencePassword = templateConferences.get(0).getConferencePassword();
                            if (Strings.hasText(conferencePassword)) {
                                participantRspDto.setDtmfInfo(conferencePassword);
                            }
                        }
                    }
                    templateParticipants.add(participantRspDto);
                    //保存关系
                    SmcTemplateTerminal smcTemplateTerminal = new SmcTemplateTerminal();
                    smcTemplateTerminal.setTerminalId(templateTerminal.getId());
                    smcTemplateTerminal.setTerminalDeptId(busiTerminal.getDeptId());
                    smcTemplateTerminal.setSmcnumber(participantRspDto.getUri());
                    smcTemplateTerminal.setWeight(templateTerminal.getWeight());
                    templateTerminals.add(smcTemplateTerminal);
                }


            }
        }
        smcConferenceTemplate.setTemplateParticipants(templateParticipants);
        SmcConferenceTemplate smcConferenceTemplateCreate = templateService.addTemplateRoom(smcConferenceTemplate, smcTemplateConferenceRequest.getDeptId(), masterTerminalId);
        String conferenceTemplateId = smcConferenceTemplateCreate.getId();
        //保存关系
        for (SmcTemplateTerminal smcTemplateTerminal : templateTerminals) {
            smcTemplateTerminal.setSmcTemplateId(conferenceTemplateId);
            smcTemplateTerminalService.add(smcTemplateTerminal);
        }

        return RestResponse.success(smcConferenceTemplateCreate);


    }

    private SmcConferenceTemplate getSmcConferenceTemplate(BusiSmcTemplateConferenceRequest smcTemplateConferenceRequest) {
        SmcConferenceTemplate smcConferenceTemplate = buildTemplateConference();

        smcConferenceTemplate.setGuestPassword(smcTemplateConferenceRequest.getGuestPassword());
        smcConferenceTemplate.setChairmanPassword(smcTemplateConferenceRequest.getChairmanPassword());
        SmcConferenceTemplate.ConferenceCapabilitySettingDTO conferenceCapabilitySetting = smcConferenceTemplate.getConferenceCapabilitySetting();
        conferenceCapabilitySetting.setSvcVideoResolution(SVC_VIDEO_RESOLUTION);
        conferenceCapabilitySetting.setVideoResolution(VIDEO_RESOLUTION);
        conferenceCapabilitySetting.setRate(smcTemplateConferenceRequest.getRate());
        String videoResolution = smcTemplateConferenceRequest.getVideoResolution();
        if (StringUtils.isNotBlank(videoResolution)) {
            conferenceCapabilitySetting.setVideoResolution(videoResolution);
            conferenceCapabilitySetting.setSvcVideoResolution(videoResolution);
            conferenceCapabilitySetting.setVideoProtocol("H264_HP");
        }
        conferenceCapabilitySetting.setEnableRecord(smcTemplateConferenceRequest.getSupportRecord() == 1);
        conferenceCapabilitySetting.setEnableLiveBroadcast(smcTemplateConferenceRequest.getSupportLive() == 1);
        conferenceCapabilitySetting.setEnableDataConf(smcTemplateConferenceRequest.getEnableDataConf() == 1);
        smcConferenceTemplate.setConferenceCapabilitySetting(conferenceCapabilitySetting);

        SmcConferenceTemplate.StreamServiceDTO streamService = smcConferenceTemplate.getStreamService();
        streamService.setSupportRecord(smcTemplateConferenceRequest.getSupportRecord() == 1);
        streamService.setAmcRecord(smcTemplateConferenceRequest.getAmcRecord() == 1);
        streamService.setSupportLive(smcTemplateConferenceRequest.getSupportLive() == 1);
        smcConferenceTemplate.setStreamService(streamService);

        smcConferenceTemplate.setSubject(smcTemplateConferenceRequest.getSubject());
        smcConferenceTemplate.setVmrNumber(smcTemplateConferenceRequest.getVmrNumber());
        smcConferenceTemplate.setDuration(smcTemplateConferenceRequest.getDuration());
        SmcConferenceTemplate.ConferencePolicySettingDTO conferencePolicySetting = smcConferenceTemplate.getConferencePolicySetting();
        conferencePolicySetting.setVoiceActive(smcTemplateConferenceRequest.getVoiceActive() == 1);
        conferencePolicySetting.setAutoMute(smcTemplateConferenceRequest.getAutoMute() == 1);
        conferencePolicySetting.setMaxParticipantNum(smcTemplateConferenceRequest.getMaxParticipantNum());
        return smcConferenceTemplate;
    }

    /**
     * 开始模板会议
     *
     * @return
     */
    @PostMapping("/conferences/templates/{id}/start")
    public RestResponse startConferenceTemplate(@PathVariable String id) {
        BusiSmcTemplateConference templateConference = queryConference(id);
        if (templateConference != null) {
            String queryConferenceId = templateConference.getConferenceId();
            if (StringUtils.isNotBlank(queryConferenceId)) {
                //查询会议信息
                SmcBridge bridge = SmcBridgeCache.getInstance().getConferenceBridge().get(queryConferenceId);
                if (bridge == null) {
                    smcTemplateConferenceService.deleteBusiSmcTemplateConferenceById(templateConference.getId());
                    SmcCreateTemplateRep smcCreateTemplateRep = startTemplate(id);
                    return RestResponse.success(smcCreateTemplateRep);
                }
                String smcConferenceInfoById = smcConferenceService.getSmcConferenceInfoById(queryConferenceId);

                if (StringUtils.isNotBlank(smcConferenceInfoById) && smcConferenceInfoById.contains(ConstAPI.CONFERENCE_NOT_EXIST)) {
                    smcTemplateConferenceService.deleteBusiSmcTemplateConferenceById(templateConference.getId());
                    SmcCreateTemplateRep smcCreateTemplateRep = startTemplate(id);
                    return RestResponse.success(smcCreateTemplateRep);
                }

                if (StringUtils.isNotBlank(smcConferenceInfoById) && smcConferenceInfoById.contains(ConstAPI.ERRORNO)) {
                    SmcErrorResponse smcErrorResponse = JSON.parseObject(smcConferenceInfoById, SmcErrorResponse.class);
                    throw new CustomException("会议信息查询错误" + smcErrorResponse.getErrorDesc());
                }
                SmcConferenceContext smcConferenceContext = JSON.parseObject(smcConferenceInfoById, SmcConferenceContext.class);
                if (smcConferenceContext == null) {
                    throw new CustomException("会议信息查询错误!");
                }
                SmcConference conference = smcConferenceContext.getConference();
                if (Objects.equals(ConferenceStage.OFFLINE.name(), conference.getStage()) || Objects.equals(ConferenceStage.CANCEL.name(), conference.getStage())) {
                    smcTemplateConferenceService.deleteBusiSmcTemplateConferenceById(templateConference.getId());
                    SmcCreateTemplateRep smcCreateTemplateRep = startTemplate(id);
                    return RestResponse.success(smcCreateTemplateRep);
                }
                if (Objects.equals(ConferenceStage.ONLINE.name(), conference.getStage())) {
                    throw new CustomException("会议已经开始");
                }

                return RestResponse.success(smcConferenceContext);
            }
        }


        SmcCreateTemplateRep smcCreateTemplateRep = startTemplate(id);
        ThreadUtils.sleep(800);
        return RestResponse.success(smcCreateTemplateRep);
    }

    /**
     * 开始模板会议
     *
     * @return
     */
    @PostMapping("/conferences/templatesLocal/{id}/start")
    public RestResponse startConferenceTemplateLocal(@PathVariable Long id) {
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplateById(id);
        String smcTemplateId = busiSmcDeptTemplate.getSmcTemplateId();
        BusiSmcTemplateConference templateConference = queryConference(smcTemplateId);
        if (templateConference != null) {
            String queryConferenceId = templateConference.getConferenceId();
            if (StringUtils.isNotBlank(queryConferenceId)) {
                //查询会议信息
                SmcBridge bridge = SmcBridgeCache.getInstance().getConferenceBridge().get(queryConferenceId);
                if (bridge == null) {
                    smcTemplateConferenceService.deleteBusiSmcTemplateConferenceById(templateConference.getId());
                    SmcCreateTemplateRep smcCreateTemplateRep = startTemplate(smcTemplateId);
                    return RestResponse.success(smcCreateTemplateRep);
                }
                String smcConferenceInfoById = smcConferenceService.getSmcConferenceInfoById(queryConferenceId);

                if (StringUtils.isNotBlank(smcConferenceInfoById) && smcConferenceInfoById.contains(ConstAPI.CONFERENCE_NOT_EXIST)) {
                    smcTemplateConferenceService.deleteBusiSmcTemplateConferenceById(templateConference.getId());
                    SmcCreateTemplateRep smcCreateTemplateRep = startTemplate(smcTemplateId);
                    return RestResponse.success(smcCreateTemplateRep);
                }

                SmcConferenceContext smcConferenceContext = null;
                try {
                    if (StringUtils.isNotBlank(smcConferenceInfoById) && smcConferenceInfoById.contains(ConstAPI.ERRORNO)) {
                        SmcErrorResponse smcErrorResponse = JSON.parseObject(smcConferenceInfoById, SmcErrorResponse.class);
                        throw new CustomException("会议信息查询错误" + smcErrorResponse.getErrorDesc());
                    }
                    smcConferenceContext = JSON.parseObject(smcConferenceInfoById, SmcConferenceContext.class);
                    if (smcConferenceContext == null) {
                        throw new CustomException("会议信息查询错误!");
                    }
                } catch (CustomException e) {
                    e.printStackTrace();
                }
                SmcConference conference = smcConferenceContext.getConference();
                if (Objects.equals(ConferenceStage.OFFLINE.name(), conference.getStage()) || Objects.equals(ConferenceStage.CANCEL.name(), conference.getStage())) {
                    smcTemplateConferenceService.deleteBusiSmcTemplateConferenceById(templateConference.getId());
                    SmcCreateTemplateRep smcCreateTemplateRep = startTemplate(smcTemplateId);
                    return RestResponse.success(smcCreateTemplateRep);
                }
                if (Objects.equals(ConferenceStage.ONLINE.name(), conference.getStage())) {
                    throw new CustomException("会议已经开始");
                }

                return RestResponse.success(smcConferenceContext);
            }
        }


        SmcCreateTemplateRep smcCreateTemplateRep = startTemplate(smcTemplateId);
        ThreadUtils.sleep(800);

        return RestResponse.success(smcCreateTemplateRep);
    }

    private SmcCreateTemplateRep startTemplate(String id) {
        List<SmcTemplateTerminal> templateTerminals = smcTemplateTerminalService.list(id);
        FcmThreadPool.exec(() -> {
            try {
                //级联处理
                if (!CollectionUtils.isEmpty(templateTerminals)) {
                    templateTerminals.stream().forEach(s -> {
                        BusiTerminal terminal = TerminalCache.getInstance().get(s.getTerminalId());
                        teleConferenceService.startConference(terminal.getNumber(), terminal.getIp());
                        if (TerminalType.isFmeTemplate(terminal.getType())) {
                            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(String.valueOf(terminal.getNumber()));
                            if (conferenceContext == null) {
                                IBusiTemplateConferenceService busiTemplateConferenceService = BeanFactory.getBean(IBusiTemplateConferenceService.class);
                                List<BusiTemplateConference> list = busiTemplateConferenceService.selectBusiTemplateConferenceList(String.valueOf(terminal.getNumber()), null);
                                if (!CollectionUtils.isEmpty(list)) {
                                    BusiTemplateConference templateConference = list.get(0);
                                    Long templateConferenceId = templateConference.getId();
                                    if (templateConferenceId != null) {
                                        ITemplateConferenceStartService templateConferenceStartService = BeanFactory.getBean(ITemplateConferenceStartService.class);
                                        templateConferenceStartService.startConference(templateConferenceId);
                                    }
                                }
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        String templateById = templateService.getTemplateById(id);
        SmcConferenceTemplate smcConferenceTemplate = JSON.parseObject(templateById, SmcConferenceTemplate.class);
        StartConference startConference2 = new StartConference();
        Date now = new Date();
        startConference2.setScheduleStartTime(localToUTC(DateUtil.convertDateToString(now, null)));
        startConference2.setSubject(smcConferenceTemplate.getSubject());
        startConference2.setId(id);
        startConference2.setDuration(smcConferenceTemplate.getDuration());
        StartConference.PeriodConferenceTimeDTO periodConferenceTimeDTO = new StartConference.PeriodConferenceTimeDTO();

        periodConferenceTimeDTO.setStartDate(startConference2.getScheduleStartTime());
        Date endDate = DateUtils.addMinutes(now, startConference2.getDuration());
        periodConferenceTimeDTO.setEndDate(localToUTC(DateUtil.convertDateToString(endDate, null)));
        periodConferenceTimeDTO.setDayLists(new ArrayList<>());
        startConference2.setPeriodConferenceTime(periodConferenceTimeDTO);
        SmcCreateTemplateRep smcCreateTemplateRep = JSON.parseObject(templateService.startConferenceTemplate(startConference2), SmcCreateTemplateRep.class);
        if (smcCreateTemplateRep == null) {
            throw new CustomException("开始会议调度失败,请重试");
        }
        List<SmcCreateTemplateRep.ParticipantsDTO> participants = smcCreateTemplateRep.getParticipants();
        //级联会议


        String conferenceId = smcCreateTemplateRep.getConference().getId();
        String accessCode = smcCreateTemplateRep.getMultiConferenceService().getAccessCode();
        String subject = smcCreateTemplateRep.getConference().getSubject();
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplate(id);
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(busiSmcDeptTemplate.getDeptId());
        SmcBridgeCache.getInstance().updateConferenceBridge(conferenceId, bridge);
        BusiSmcTemplateConference busiSmcTemplateConference = new BusiSmcTemplateConference();
        busiSmcTemplateConference.setSmcTemplateId(id);
        busiSmcTemplateConference.setConferenceId(smcCreateTemplateRep.getConference().getId());
        smcTemplateConferenceService.insertBusiSmcTemplateConference(busiSmcTemplateConference);

        SmcConferenceContext smcConferenceContext = new SmcConferenceContext();
        SmcCreateTemplateRep.ConferenceDTO conference = smcCreateTemplateRep.getConference();
        SmcConference smcConferenceCo = new SmcConference();
        BeanUtils.copyProperties(conference, smcConferenceCo);
        smcConferenceContext.setConference(smcConferenceCo);
        try {
            SmcCreateTemplateRep.MultiConferenceServiceDTO multiConferenceService = smcCreateTemplateRep.getMultiConferenceService();
            smcConferenceContext.setNumber(multiConferenceService.getAccessCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<SmcCreateTemplateRep.ParticipantsDTO> repParticipants = smcCreateTemplateRep.getParticipants();
        if (!CollectionUtils.isEmpty(participants)) {
            FcmThreadPool.exec(() -> {
                participants.stream().forEach(p -> {
                    Optional<SmcTemplateTerminal> first = templateTerminals.stream().filter(m -> Objects.equals(p.getUri(), m.getSmcnumber())).findFirst();
                    if (first.isPresent()) {
                        SmcTemplateTerminal smcTemplateTerminal = first.get();
                        p.setDeptId(smcTemplateTerminal.getTerminalDeptId());
                        p.setTerminalId(smcTemplateTerminal.getTerminalId());
                        BusiTerminal terminal = TerminalCache.getInstance().get(smcTemplateTerminal.getTerminalId());
                        p.setTerminalType(terminal.getType());
                        if (TerminalType.isMcuTemplateCisco(terminal.getType())) {
                            p.setIsCascade(true);

                            smcConferenceContext.setCascade(true);
                            Map<String, String> stringStringMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(smcConferenceContext.getConference().getId());
                            if (Objects.isNull(stringStringMap)) {
                                Map<String, String> uirMap = new HashMap<>();
                                uirMap.put(p.getId(), p.getUri());
                                SmcConferenceContextCache.getInstance().getCascadeConference().put(smcConferenceContext.getConference().getId(), uirMap);
                            } else {
                                stringStringMap.put(p.getId(), p.getUri());
                            }

                        }

                        if (TerminalType.isFmeTemplate(terminal.getType())) {
                            p.setIsCascade(true);

                            smcConferenceContext.setCascade(true);
                            Map<String, String> stringStringMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(smcConferenceContext.getConference().getId());
                            if (Objects.isNull(stringStringMap)) {
                                Map<String, String> uirMap = new HashMap<>();
                                uirMap.put(p.getId(), p.getUri());
                                SmcConferenceContextCache.getInstance().getCascadeConference().put(smcConferenceContext.getConference().getId(), uirMap);
                            } else {
                                stringStringMap.put(p.getId(), p.getUri());
                            }
                            String fmeConfId = AesEnsUtils.getAesEncryptor().encryptToHex(String.valueOf(terminal.getNumber()));
                            p.setConferenceIdFme(fmeConfId);
                        }
                    }

                });
                smcConferenceContext.setParticipants(copy(repParticipants, ParticipantRspDto.class));
            });
        }


        smcConferenceContext.setDeptId(busiSmcDeptTemplate.getDeptId());


        SmcConferenceContextCache.getInstance().getSmcConferenceContextMap().put(conferenceId, smcConferenceContext);


        BusiSmcHistoryConference busiSmcHistoryConference = new BusiSmcHistoryConference();
        busiSmcHistoryConference.setConferenceId(conferenceId);
        busiSmcHistoryConference.setConferenceCode(accessCode);
        busiSmcHistoryConference.setSubject(subject);
        busiSmcHistoryConference.setDeptId(busiSmcDeptTemplate.getDeptId());
        busiSmcHistoryConference.setCreateTime(new Date());
        busiSmcHistoryConference.setEndStatus(2);
        busiSmcHistoryConference.setTemplateId(busiSmcDeptTemplate.getId());
        busiSmcHistoryConference.setConferenceAvcType(smcConferenceTemplate.getConferenceCapabilitySetting().getType());
        busiSmcHistoryConference.setDuration(busiSmcDeptTemplate.getDuration());
        try {
            busiSmcHistoryConference.setStartTime(UTCTimeFormatUtil.utcToLocal(smcCreateTemplateRep.getConference().getScheduleStartTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        smcHistoryConferenceService.insertBusiSmcHistoryConference(busiSmcHistoryConference);
        try {
            smcConferenceContext.setRate(smcConferenceTemplate.getConferenceCapabilitySetting().getRate());
            BusiHistoryConference busiHistoryConference = smc3HistoryConferenceService.saveHistory(smcConferenceContext);
            // 历史call保存
            String callId = com.paradisecloud.common.utils.uuid.UUID.randomUUID().toString();
            BusiHistoryCall busiHistoryCall = new BusiHistoryCall();
            busiHistoryCall.setCallId(callId);
            busiHistoryCall.setCoSpace(smcConferenceContext.getConference().getId());
            busiHistoryCall.setDeptId(smcConferenceContext.getDeptId());
            busiHistoryCall.setCreateTime(new Date());
            busiHistoryCall.setHistoryConferenceId(busiHistoryConference.getId());
            BeanFactory.getBean(BusiHistoryCallMapper.class).insertBusiHistoryCall(busiHistoryCall);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return smcCreateTemplateRep;
    }

    private BusiSmcTemplateConference queryConference(String id) {
        BusiSmcTemplateConference templateConference = new BusiSmcTemplateConference();
        templateConference.setSmcTemplateId(id);
        //查找模板
        List<BusiSmcTemplateConference> busiSmcTemplateConferences = smcTemplateConferenceService.selectBusiSmcTemplateConferenceList(templateConference);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(busiSmcTemplateConferences)) {
            BusiSmcTemplateConference busiSmcTemplateConference = busiSmcTemplateConferences.get(0);
            return busiSmcTemplateConference;
        }
        return null;
    }

    /**
     * 通过会议号查询FME与会者列表
     */
    @GetMapping("/getParticipantsBycoferenceNumber")
    @Operation(summary = "通过会议号查询FME与会者列表")
    public RestResponse getParticipantsBycoferenceNumber(@RequestParam String conferenceNumber) {
        String conferenceIdFme = conferenceNumber == null ? null : AesEnsUtils.getAesEncryptor().encryptToHex(conferenceNumber);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(conferenceIdFme);
        return success(conferenceContext);
    }

    /**
     * 通过uri查询TELE与会者列表
     */
    @GetMapping("/getParticipantsByUri")
    @Operation(summary = "通过uri查询TELE与会者列表")
    public RestResponse getParticipantsByUri(@RequestParam String uri, @RequestParam(required = false) String conferenceId) {
        if (StringUtils.isBlank(conferenceId)) {
            return success();
        }
        DetailConference detailConference = null;
        try {
            detailConference = smcConferenceService.getDetailConferenceInfoById(conferenceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (detailConference == null) {
            return success();
        }
        String accessCode = detailConference.getConferenceUiParam().getAccessCode();
        Map<String, SmcBridge> conferenceBridge = SmcBridgeCache.getInstance().getConferenceBridge();
        SmcBridge smcBridge = conferenceBridge.get(conferenceId);
        String smcBridgeIp = smcBridge.getIp();
        String scIp = smcBridge.getBusiSMC().getScIp();
        String[] split = uri.split("@");
        String number = split[0];
        String ip = split[1];
        List<TeleParticipant> list = iBusiParaticipantsService.getList(number, ip, null);
        if (!CollectionUtils.isEmpty(list)) {
            logger.info("TeleParticipantList return" + JSON.toJSONString(list));
            List<TeleParticipant> disconnected = list.stream().filter(p -> {
                return Objects.equals(p.getCallState(), DISCONNECTED) && (Objects.equals(p.getAddress(), smcBridgeIp) || Objects.equals(p.getAddress(), scIp)) && !Objects.equals(p.getDisplayName(), ConstAPI.MCU_SMC_ID);
            }).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(disconnected)) {
                list.removeAll(disconnected);
            }
        }
        if (!CollectionUtils.isEmpty(list)) {
            FcmThreadPool.exec(() -> {
                list.stream().forEach(s -> {
                    if (Objects.equals(s.getCallState(), "connected")) {
                        BusiTeleParticipant busiTeleParticipant = TeleParticipantCache.getInstance().get(number + s.getParticipantName());
                        if (busiTeleParticipant == null) {

                            BusiTeleParticipant busiTeleParticipantquery = new BusiTeleParticipant();
                            busiTeleParticipantquery.setParticipantName(s.getParticipantName());
                            busiTeleParticipantquery.setConferenceName(s.getConferenceName());
                            busiTeleParticipantquery.setConferenceNumber(number);
                            List<BusiTeleParticipant> busiTeleParticipants = busiTeleParticipantService.selectBusiTeleParticipantList(busiTeleParticipantquery);
                            if (CollectionUtils.isEmpty(busiTeleParticipants)) {
                                busiTeleParticipantquery.setCalltheroll(0);
                                busiTeleParticipantquery.setChoose(0);
                                TeleParticipantCache.getInstance().put(number + s.getParticipantName(), busiTeleParticipantquery);
                                s.setCallTheRoll(false);
                                s.setChoose(false);
                            } else {
                                BusiTeleParticipant participant = busiTeleParticipants.get(0);
                                s.setCallTheRoll(participant.getCalltheroll() == 1);
                                s.setChoose(participant.getChoose() == 1);
                                TeleParticipantCache.getInstance().put(number + s.getParticipantName(), participant);
                            }

                        } else {
                            s.setCallTheRoll(busiTeleParticipant.getCalltheroll() == 1);
                            s.setChoose(busiTeleParticipant.getChoose() == 1);
                        }
                    }

                });
            });
        }
        ThreadUtils.sleep(500);
        return success(list == null ? new ArrayList<>() : list.stream().sorted(Comparator.comparing(TeleParticipant::getCallState)).collect(Collectors.toList()));
    }

    /**
     * 重呼
     */
    @PostMapping("/participantConnect")
    @Operation(summary = "重呼/呼叫")
    public RestResponse participantConnect(@RequestBody ParticipantVo connectVo) {
        String uri = connectVo.getUri();
        String ip = uri.split("@")[1];
        iBusiParaticipantsService.participantConnect(ip, connectVo.getTeleParticipant());
        return success(null);
    }

    /**
     * 断开连接
     */
    @PostMapping("/participantDisConnect")
    @Operation(summary = "断开连接")
    public RestResponse participantDisConnect(@RequestBody ParticipantVo connectVo) {
        String uri = connectVo.getUri();
        String ip = uri.split("@")[1];
        iBusiParaticipantsService.participantDisConnect(ip, connectVo.getTeleParticipant());
        return success(null);
    }

    /**
     * 重要性设置
     */
    @PostMapping("/participantImport")
    @Operation(summary = "重要性设置")
    public RestResponse participantImport(@RequestBody ParticipantVo connectVo) {
        String uri = connectVo.getUri();
        String ip = uri.split("@")[1];
        connectVo.getTeleParticipant().setImportant(true);
        iBusiParaticipantsService.participantModify(ip, connectVo.getTeleParticipant());
        return success(null);
    }

    /**
     * 重要性设置
     */
    @PostMapping("/participantImportCancle")
    @Operation(summary = "取消重要性")
    public RestResponse participantImportCancle(@RequestBody ParticipantVo connectVo) {
        String uri = connectVo.getUri();
        String ip = uri.split("@")[1];
        connectVo.getTeleParticipant().setImportant(false);
        iBusiParaticipantsService.participantModify(ip, connectVo.getTeleParticipant());
        return success(null);
    }

    /**
     * 视频关闭
     */
    @PostMapping("/participantVideoRxMuted")
    @Operation(summary = "视频关闭")
    public RestResponse participantVideoRxMuted(@RequestBody ParticipantVo connectVo) {
        String uri = connectVo.getUri();
        String ip = uri.split("@")[1];
        connectVo.getTeleParticipant().setVideoRxMuted(true);
        iBusiParaticipantsService.participantModify(ip, connectVo.getTeleParticipant());
        return success(null);
    }

    /**
     * 视频开启
     */
    @PostMapping("/participantVideoRxMutedCancle")
    @Operation(summary = "视频开启")
    public RestResponse participantVideoRxMutedCancle(@RequestBody ParticipantVo connectVo) {
        String uri = connectVo.getUri();
        String ip = uri.split("@")[1];
        connectVo.getTeleParticipant().setVideoRxMuted(false);
        iBusiParaticipantsService.participantModify(ip, connectVo.getTeleParticipant());
        return success(null);
    }

    /**
     * 音频静音
     */
    @PostMapping("/participantAudioRxMuted")
    @Operation(summary = "音频静音")
    public RestResponse participantAudioRxMuted(@RequestBody ParticipantVo connectVo) {
        String uri = connectVo.getUri();
        String ip = uri.split("@")[1];
        connectVo.getTeleParticipant().setAudioRxMuted(true);
        iBusiParaticipantsService.participantModify(ip, connectVo.getTeleParticipant());
        SmcParitipantsStateRep conferencesParticipantsState = null;
        String conferenceId = connectVo.getConferenceId();
        try {
            conferencesParticipantsState = smcParticipantsService.getConferencesParticipantsState(conferenceId, 0, 10000);

        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.success(null);
        }

        List<SmcParitipantsStateRep.ContentDTO> content = conferencesParticipantsState.getContent();

        if (!CollectionUtils.isEmpty(content)) {


            Optional<SmcParitipantsStateRep.ContentDTO> first = content.stream().filter(c -> Objects.equals(c.getGeneralParam().getUri(), uri)).findFirst();
            if (first.isPresent()) {
                String participantId = first.get().getState().getParticipantId();

                List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
                ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
                participantStatusDto.setId(participantId);
                participantStatusDto.setIsMute(true);
                participantStatusList.add(participantStatusDto);
                smcParticipantsService.PATCHParticipantsOnly(conferenceId, participantStatusList);
            }
        }

        return success(null);
    }

    /**
     * 取消音频静音
     */
    @PostMapping("/participantAudioRxMutedCancle")
    @Operation(summary = "取消音频静音")
    public RestResponse participantAudioRxMutedCancle(@RequestBody ParticipantVo connectVo) {
        String uri = connectVo.getUri();
        String ip = uri.split("@")[1];
        connectVo.getTeleParticipant().setAudioRxMuted(false);
        iBusiParaticipantsService.participantModify(ip, connectVo.getTeleParticipant());
        //上级会议开启混音
        SmcParitipantsStateRep conferencesParticipantsState = null;
        String conferenceId = connectVo.getConferenceId();
        try {
            conferencesParticipantsState = smcParticipantsService.getConferencesParticipantsState(conferenceId, 0, 10000);

        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.success(null);
        }

        List<SmcParitipantsStateRep.ContentDTO> content = conferencesParticipantsState.getContent();

        if (!CollectionUtils.isEmpty(content)) {


            Optional<SmcParitipantsStateRep.ContentDTO> first = content.stream().filter(c -> Objects.equals(c.getGeneralParam().getUri(), uri)).findFirst();
            if (first.isPresent()) {
                String participantId = first.get().getState().getParticipantId();

                List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
                ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
                participantStatusDto.setId(participantId);
                participantStatusDto.setIsMute(false);
                participantStatusList.add(participantStatusDto);
                smcParticipantsService.PATCHParticipantsOnly(conferenceId, participantStatusList);
            }
        }

        return success(null);
    }

    /**
     * 关闭扬声器
     */
    @PostMapping("/participantAudioTxMuted")
    @Operation(summary = "关闭扬声器")
    public RestResponse participantAudioTxMuted(@RequestBody ParticipantVo connectVo) {
        String uri = connectVo.getUri();
        String ip = uri.split("@")[1];
        connectVo.getTeleParticipant().setAudioTxMuted(true);
        iBusiParaticipantsService.participantModify(ip, connectVo.getTeleParticipant());
        return success(null);
    }

    /**
     * 打开扬声器
     */
    @PostMapping("/participantAudioTxMutedCancle")
    @Operation(summary = "打开扬声器")
    public RestResponse participantAudioTxMutedCancle(@RequestBody ParticipantVo connectVo) {
        String uri = connectVo.getUri();
        String ip = uri.split("@")[1];
        connectVo.getTeleParticipant().setAudioTxMuted(false);
        iBusiParaticipantsService.participantModify(ip, connectVo.getTeleParticipant());
        return success(null);
    }

    /**
     * 摄像头控制
     */
    @PostMapping("/participantFecc")
    @Operation(summary = "摄像头控制")
    public RestResponse participantFecc(@RequestBody ParticipantFeccReqVo fecc) {
        String uri = fecc.getUri();
        String ip = uri.split("@")[1];
        iBusiParaticipantsService.participantFecc(ip, fecc.getParticipantFecc());
        return success(null);
    }

    /**
     * 点名
     */
    @PostMapping("/callTheRoll")
    @Operation(summary = "点名")
    public RestResponse callTheRoll(@RequestBody TeleCallTheRoll teleCallTheRoll) {
        String conferenceId = teleCallTheRoll.getConferenceId();
        String participantId = teleCallTheRoll.getParticipantId();
        if (StringUtils.isNotBlank(conferenceId) && StringUtils.isNotBlank(participantId)) {

            conferenceSMCService.callTheRoll(conferenceId, participantId);

        }
        return success(null);
    }

    /**
     * 取消点名
     */
    @PostMapping("/callTheRoll/cancle")
    @Operation(summary = "取消点名")
    public RestResponse callTheRollCancle(@RequestBody TeleCallTheRoll teleCallTheRoll) {

        String conferenceId = teleCallTheRoll.getConferenceId();
        String participantId = teleCallTheRoll.getParticipantId();
        if (StringUtils.isNotBlank(conferenceId) && StringUtils.isNotBlank(participantId)) {
            conferenceSMCService.cancelCallTheRoll(conferenceId, participantId, null);
        }

        return success(null);
    }

    /**
     * 选看
     */
    @PostMapping("/choose")
    @Operation(summary = "选看")
    public RestResponse chooseTele(@RequestBody TeleCallTheRoll teleCallTheRoll) {

        String conferenceId = teleCallTheRoll.getConferenceId();
        String participantId = teleCallTheRoll.getParticipantId();
        if (StringUtils.isNotBlank(conferenceId) && StringUtils.isNotBlank(participantId)) {
            conferenceSMCService.choose(conferenceId, participantId);
        }
        return success(null);
    }

    private void participanControllStatus(String number, TeleParticipant teleParticipant, int choose, int callTheRoll) {
        BusiTeleParticipant busiTeleParticipant = new BusiTeleParticipant();
        busiTeleParticipant.setConferenceName(teleParticipant.getConferenceName());
        busiTeleParticipant.setConferenceNumber(number);
        List<BusiTeleParticipant> all = busiTeleParticipantService.selectBusiTeleParticipantList(busiTeleParticipant);
        if (!CollectionUtils.isEmpty(all)) {
            all.forEach(s -> busiTeleParticipantService.deleteBusiTeleParticipantById(s.getId()));
        }
        busiTeleParticipant.setParticipantName(teleParticipant.getParticipantName());
        List<BusiTeleParticipant> busiTeleParticipants = busiTeleParticipantService.selectBusiTeleParticipantList(busiTeleParticipant);
        if (CollectionUtils.isEmpty(busiTeleParticipants)) {
            busiTeleParticipant.setChoose(choose);
            busiTeleParticipant.setCalltheroll(callTheRoll);
            busiTeleParticipant.setCreateTime(new Date());
            busiTeleParticipantService.insertBusiTeleParticipant(busiTeleParticipant);
        } else {
            BusiTeleParticipant busiTeleParticipant1 = busiTeleParticipants.get(0);
            busiTeleParticipant1.setChoose(choose);
            busiTeleParticipant1.setCalltheroll(callTheRoll);
            busiTeleParticipantService.updateBusiTeleParticipant(busiTeleParticipant1);
            busiTeleParticipant = busiTeleParticipant1;
        }
        TeleParticipantCache.getInstance().removeAll();

        TeleParticipantCache.getInstance().put(number, teleParticipant.getParticipantName(), busiTeleParticipant);
    }

    /**
     * 取消选看
     */
    @PostMapping("/choose/cancle")
    @Operation(summary = "取消选看")
    public RestResponse chooseTeleCancle(@RequestBody TeleCallTheRoll teleCallTheRoll) {

        String conferenceId = teleCallTheRoll.getConferenceId();
        String participantId = teleCallTheRoll.getParticipantId();
        if (StringUtils.isNotBlank(conferenceId) && StringUtils.isNotBlank(participantId)) {
            conferenceSMCService.cancelChoose(conferenceId, participantId, null);
        }

        return success(null);
    }

    /**
     * 移除与会者
     */
    @PostMapping("/participant/remove")
    @Operation(summary = "移除与会者")
    public RestResponse participantRemove(@RequestBody ParticipantVo connectVo) {
        String uri = connectVo.getUri();
        String[] split = uri.split("@");
        String number = split[0];
        String ip = split[1];

        TeleParticipant teleParticipant = connectVo.getTeleParticipant();
        iBusiParaticipantsService.participantRemove(ip, teleParticipant);


        return success(null);
    }

    /**
     * 结束会议
     */
    @PostMapping("/conference/end")
    @Operation(summary = "结束会议")
    @Transactional
    public RestResponse conferenceEnd(@RequestBody ConferenceEnd conferenceEnd) {
        String conferenceId = conferenceEnd.getConferenceId();
        Long deptId = AuthenticationUtil.getDeptId() == null ? 1 : AuthenticationUtil.getDeptId();


        try {
            endteleConference(conferenceEnd, conferenceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conferenceSMCService.endConference(conferenceId);
        TeleParticipantCache.getInstance().removeAll();
        SmcConferenceContextCache.getInstance().getSmcConferenceContextMap().remove(conferenceId);
        try {
            Map<String, Object> monitorParticipantMap = SmcConferenceContextCache.getInstance().getMonitorParticipantMap();
            if (monitorParticipantMap != null) {
                CoSpace coSpace = (CoSpace) monitorParticipantMap.get(conferenceId);
                if (coSpace != null) {
                    FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
                    fmeBridge.getCoSpaceInvoker().deleteCoSpace(coSpace.getId());
                    monitorParticipantMap.remove(conferenceId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success(null);
    }

    private void endteleConference(ConferenceEnd conferenceEnd, String conferenceId) {
        List<String> arrayList = new ArrayList<>();
        SmcParitipantsStateRep conferencesParticipantsState = smcParticipantsService.getConferencesParticipantsState(conferenceId, 0, 10000);
        List<SmcParitipantsStateRep.ContentDTO> content = conferencesParticipantsState.getContent();

        BusiSmcTemplateConference busiSmcTemplateConference = new BusiSmcTemplateConference();
        busiSmcTemplateConference.setConferenceId(conferenceId);
        List<BusiSmcTemplateConference> busiSmcTemplateConferences = smcTemplateConferenceService.selectBusiSmcTemplateConferenceList(busiSmcTemplateConference);
        if (!CollectionUtils.isEmpty(busiSmcTemplateConferences)) {
            List<SmcTemplateTerminal> templateTerminals = smcTemplateTerminalService.list(busiSmcTemplateConferences.get(0).getSmcTemplateId());

            if (!CollectionUtils.isEmpty(content)) {
                content.stream().forEach(p -> {
                    Optional<SmcTemplateTerminal> first = templateTerminals.stream().filter(m -> Objects.equals(p.getGeneralParam().getUri(), m.getSmcnumber())).findFirst();
                    if (first.isPresent()) {
                        SmcTemplateTerminal smcTemplateTerminal = first.get();
                        BusiTerminal terminal = TerminalCache.getInstance().get(smcTemplateTerminal.getTerminalId());
                        if (terminal != null) {
                            if (TerminalType.isMcuTemplateCisco(terminal.getType())) {
                                arrayList.add(p.getGeneralParam().getUri());
                            }
                            if (TerminalType.isFmeTemplate(terminal.getType())) {
                                if (conferenceEnd.getCloseCascade()) {
                                    try {
                                        IBusiConferenceService busiConferenceService = BeanFactory.getBean(IBusiConferenceService.class);
                                        busiConferenceService.endConference(AesEnsUtils.getAesEncryptor().encryptToHex(terminal.getNumber()), ConferenceEndType.COMMON.getValue(), EndReasonsType.ADMINISTRATOR_HANGS_UP);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }
                    }

                });
            }
        }


        if (!CollectionUtils.isEmpty(arrayList)) {
            for (String cascade : arrayList) {
                String[] split = cascade.split("@");
                String number = split[0];
                String ip = split[1];
                ConferencesResponse conferencesResponse = teleConferenceService.conferenceList(ip, EnumerateFilter.SCHEDULED);
                if (conferencesResponse != null) {
                    List<TeleConference> conferences = conferencesResponse.getConferences();
                    if (!CollectionUtils.isEmpty(conferences)) {
                        for (TeleConference conference : conferences) {
                            if (Objects.equals(number, conference.getNumericId())) {

                                if (conferenceEnd.getCloseCascade()) {
                                    try {
                                        teleConferenceService.conferenceEnd(ip, conference.getConferenceName());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                BusiTeleParticipant busiTeleParticipant = new BusiTeleParticipant();
                                busiTeleParticipant.setConferenceName(conference.getConferenceName());
                                busiTeleParticipant.setConferenceNumber(number);
                                List<BusiTeleParticipant> all = busiTeleParticipantService.selectBusiTeleParticipantList(busiTeleParticipant);
                                if (!CollectionUtils.isEmpty(all)) {
                                    all.forEach(s -> busiTeleParticipantService.deleteBusiTeleParticipantById(s.getId()));
                                }
                            }
                        }

                    }
                }
            }
        }

        TeleParticipantCache.getInstance().removeAll();
    }

    /**
     * 查询会议模板列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询会议模板列表")
    public RestResponse list(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "deptId") Long deptId) {

        List<BusiSmcDeptTemplate> busiSmcDeptTemplateList = busiSmcDeptTemplateService.queryTemplateListByDeptId(deptId);
        if (!CollectionUtils.isEmpty(busiSmcDeptTemplateList)) {
            String s = templateService.queryConferenceTemplates(name, deptId);
            SmcTemplateListRep smcTemplateListRep = JSON.parseObject(s, SmcTemplateListRep.class);
            List<SmcTemplateListRep.ContentDTO> content = smcTemplateListRep.getContent();
            if (!CollectionUtils.isEmpty(content)) {

                List<SmcTemplateListRep.ContentDTO> collect = content.stream().filter(p -> {
                    for (BusiSmcDeptTemplate b : busiSmcDeptTemplateList) {
                        if (Objects.equals(p.getId(), b.getSmcTemplateId())) {
                            return true;
                        }
                    }
                    return false;

                }).collect(Collectors.toList());
                smcTemplateListRep.setContent(collect);
                return RestResponse.success(0, "查询成功", smcTemplateListRep);
            }
        }
        SmcTemplateListRep smcTemplateListRep = new SmcTemplateListRep();
        smcTemplateListRep.setContent(new ArrayList<>());

        return RestResponse.success(0, "查询成功", smcTemplateListRep);
    }

    /**
     * 查询本地会议模板列表
     */
    @GetMapping("/LocalList")
    @Operation(summary = "查询会议模板列表")
    public RestResponse LocalList(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "deptId") Long deptId) {

        List<BusiSmcDeptTemplate> busiSmcDeptTemplateList = busiSmcDeptTemplateService.queryTemplateListByDeptId(deptId);
        return RestResponse.success(0, "查询成功", busiSmcDeptTemplateList);
    }

    /**
     * 删除模板会议
     */
    @Transactional
    @DeleteMapping("/conferences/templates/{id}")
    public RestResponse deleteMeetingRooms(@PathVariable String id) {
        BusiSmcTemplateConference busiSmcTemplateConference = queryConference(id);
        BusiSmcTemplateConference templateConference = busiSmcTemplateConference;
        if (templateConference != null) {
            String queryConferenceId = templateConference.getConferenceId();
            if (StringUtils.isNotBlank(queryConferenceId)) {
                //查询会议信息
                SmcBridge bridge = SmcBridgeCache.getInstance().getConferenceBridge().get(queryConferenceId);
                if (bridge != null) {
                    String smcConferenceInfoById = smcConferenceService.getSmcConferenceInfoById(queryConferenceId);
                    if (StringUtils.isNotBlank(smcConferenceInfoById) && !smcConferenceInfoById.contains(ConstAPI.ERRO)) {
                        SmcConferenceContext smcConferenceContext = JSON.parseObject(smcConferenceInfoById, SmcConferenceContext.class);
                        SmcConference conference = smcConferenceContext.getConference();
                        if (Objects.equals(ConferenceStage.OFFLINE.name(), conference.getStage()) || Objects.equals(ConferenceStage.ONLINE.name(), conference.getStage())) {
                            throw new CustomException("请先结束会议!");
                        } else {
                            smcTemplateConferenceService.deleteBusiSmcTemplateConferenceById(templateConference.getId());
                            templateService.deleteTemplateById(id);
                        }
                    }
                }
            }

        }

        return RestResponse.success();

    }

    /**
     * 删除模板会议
     */
    @DeleteMapping("/conferences/templatesLocal/{id}")
    public RestResponse deleteMeetingRoomsLocal(@PathVariable Long id) {
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplateById(id);
        String smcTemplateId = busiSmcDeptTemplate.getSmcTemplateId();
        String templateById = templateService.getTemplateById(smcTemplateId);
        if (org.springframework.util.StringUtils.hasText(templateById)) {
            if (templateById.contains(ConstAPI.TOKEN_NOT_EXIST)) {
                throw new CustomException("token不存在,请重试");
            }
            BusiSmcTemplateConference templateConference = queryConference(smcTemplateId);
            if (templateConference != null) {
                String queryConferenceId = templateConference.getConferenceId();
                if (StringUtils.isNotBlank(queryConferenceId)) {
                    //查询会议信息
                    SmcBridge bridge = SmcBridgeCache.getInstance().getConferenceBridge().get(queryConferenceId);
                    if (bridge == null) {
                        bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
                    }
                    if (bridge != null) {
                        String smcConferenceInfoById = smcConferenceService.getSmcConferenceInfoById(queryConferenceId);
                        if (StringUtils.isNotBlank(smcConferenceInfoById) && !smcConferenceInfoById.contains(ConstAPI.ERRO)) {
                            SmcConferenceContext smcConferenceContext = JSON.parseObject(smcConferenceInfoById, SmcConferenceContext.class);
                            SmcConference conference = smcConferenceContext.getConference();
                            if (Objects.equals(ConferenceStage.OFFLINE.name(), conference.getStage()) || Objects.equals(ConferenceStage.ONLINE.name(), conference.getStage())) {
                                throw new CustomException("请先结束会议!");
                            } else {
                                smcTemplateConferenceService.deleteBusiSmcTemplateConferenceById(templateConference.getId());
                                templateService.deleteTemplateById(templateById);
                            }
                        }
                        if (StringUtils.isNotBlank(smcConferenceInfoById) && smcConferenceInfoById.contains(ConstAPI.CONFERENCE_NOT_EXIST)) {
                            smcTemplateConferenceService.deleteBusiSmcTemplateConferenceById(templateConference.getId());
                            templateService.deleteTemplateById(templateById);
                        }
                    }
                }

            } else {
                busiSmcDeptTemplateService.delete(id);
                smcTemplateTerminalService.deleteBytemplateId(smcTemplateId);
            }
        } else {
            busiSmcDeptTemplateService.delete(id);
            smcTemplateTerminalService.deleteBytemplateId(smcTemplateId);
        }

        return RestResponse.success();
    }

    private RestResponse deleteTemplateResponse(BusiSmcTemplateConference busiSmcTemplateConference, String s) {
        BusiSmcTemplateConference templateConference = busiSmcTemplateConference;
        if (templateConference != null) {
            String queryConferenceId = templateConference.getConferenceId();
            if (StringUtils.isNotBlank(queryConferenceId)) {
                //查询会议信息
                SmcBridge bridge = SmcBridgeCache.getInstance().getConferenceBridge().get(queryConferenceId);
                if (bridge != null) {
                    String smcConferenceInfoById = smcConferenceService.getSmcConferenceInfoById(queryConferenceId);
                    if (StringUtils.isNotBlank(smcConferenceInfoById) && !smcConferenceInfoById.contains(ConstAPI.ERRO)) {
                        SmcConferenceContext smcConferenceContext = JSON.parseObject(smcConferenceInfoById, SmcConferenceContext.class);
                        SmcConference conference = smcConferenceContext.getConference();
                        if (Objects.equals(ConferenceStage.OFFLINE.name(), conference.getStage()) || Objects.equals(ConferenceStage.ONLINE.name(), conference.getStage())) {
                            throw new CustomException("请先结束会议!");
                        }
                    }
                }
            }
            smcTemplateConferenceService.deleteBusiSmcTemplateConferenceById(templateConference.getId());
        }

        return RestResponse.success(s);
    }

    /**
     * 修改模板会议
     */
    @PutMapping("/conferences/templates/{id}")
    public RestResponse updateMeetingRooms(@PathVariable String id, @RequestBody JSONObject jsonObj) {
        BusiSmcTemplateConference busiSmcTemplateConference = new BusiSmcTemplateConference();
        busiSmcTemplateConference.setSmcTemplateId(id);
        List<BusiSmcTemplateConference> busiSmcTemplateConferences = smcTemplateConferenceService.selectBusiSmcTemplateConferenceList(busiSmcTemplateConference);
        if (!CollectionUtils.isEmpty(busiSmcTemplateConferences)) {
            throw new CustomException("会议进行中不能修改");
        }
        JSONObject smcConferenceTemplateRquestjson = jsonObj.getJSONObject("smcConferenceTemplateRquest");
        SmcConferenceTemplate smcConferenceTemplateRquest = smcConferenceTemplateRquestjson.toJavaObject(SmcConferenceTemplate.class);
        smcTemplateTerminalService.deleteBytemplateId(smcConferenceTemplateRquest.getId());
        List<SmcTemplateTerminal> templateTerminals = new ArrayList<>();
        List<Long> templateParticipantsIds = smcConferenceTemplateRquest.getTemplateParticipantsIds();
        List<ParticipantRspDto> templateParticipants = new ArrayList<>();

        List<TemplateTerminal> templateTerminalList = smcConferenceTemplateRquest.getTemplateTerminalList();
        if (!CollectionUtils.isEmpty(templateTerminalList)) {
            updateTerminal(id, templateTerminals, templateParticipants, templateTerminalList);
        } else {
            if (!CollectionUtils.isEmpty(templateParticipantsIds)) {
                for (Long tid : templateParticipantsIds) {
                    BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(tid);
                    if (busiTerminal == null) {
                        continue;
                    }
                    ParticipantRspDto participantRspDto = new ParticipantRspDto();
                    SmcTemplateTerminal smcTemplateTerminal = new SmcTemplateTerminal();
                    if (!Objects.isNull(busiTerminal)) {
                        String number = busiTerminal.getNumber();
                        smcTemplateTerminal.setSmcnumber(number);
                        participantRspDto.setUri(number);
                        participantRspDto.setName(busiTerminal.getName());
                        participantRspDto.setIpProtocolType(2);
                        participantRspDto.setDialMode("OUT");
                        participantRspDto.setVoice(false);
                        participantRspDto.setRate(0);
                        participantRspDto.setMainParticipant(false);
                        if (StringUtils.isBlank(number)) {
                            if (TerminalType.isCisco(busiTerminal.getType())) {
                                participantRspDto.setUri(busiTerminal.getName() + "@" + busiTerminal.getIp());
                                smcTemplateTerminal.setSmcnumber(busiTerminal.getName() + "@" + busiTerminal.getIp());
                            } else {
                                participantRspDto.setUri(busiTerminal.getIp());
                                smcTemplateTerminal.setSmcnumber(busiTerminal.getIp());
                            }
                        }
                        if (StringUtils.isNotBlank(busiTerminal.getIp()) && Strings.hasText(busiTerminal.getNumber())) {
                            String uri = busiTerminal.getNumber() + "@" + busiTerminal.getIp();
                            participantRspDto.setUri(uri);
                            smcTemplateTerminal.setSmcnumber(uri);
                        }
                        if (TerminalType.isFSBC(busiTerminal.getType())) {
                            if (StringUtils.isNotBlank(busiTerminal.getIp())) {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp() + ":5060");
                                smcTemplateTerminal.setSmcnumber(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
                            } else {
                                BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                                FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                                String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":5060");
                                smcTemplateTerminal.setSmcnumber(busiTerminal.getCredential() + "@" + callIp);
                            }
                        }

                        if (!TerminalType.isFCMSIP(busiTerminal.getType())) {
                            templateParticipants.add(participantRspDto);
                        }
                        //保存关系
                        smcTemplateTerminal.setTerminalId(tid);
                        smcTemplateTerminal.setTerminalDeptId(busiTerminal.getDeptId());
                        smcTemplateTerminal.setSmcTemplateId(id);
                        templateTerminals.add(smcTemplateTerminal);
                    }
                    smcTemplateTerminalService.add(smcTemplateTerminal);

                }
            }
        }

        smcConferenceTemplateRquest.setTemplateParticipants(templateParticipants);
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplate(smcConferenceTemplateRquest.getId());
        busiSmcDeptTemplate.setTemplateName(smcConferenceTemplateRquest.getSubject());
        busiSmcDeptTemplate.setDuration(smcConferenceTemplateRquest.getDuration());
        busiSmcDeptTemplateService.update(busiSmcDeptTemplate);
        return RestResponse.success(templateService.putTemplate(smcConferenceTemplateRquest.getId(), smcConferenceTemplateRquest));

    }

    private void updateTerminal(String id, List<SmcTemplateTerminal> templateTerminals, List<ParticipantRspDto> templateParticipants, List<TemplateTerminal> templateTerminalList) {
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(templateTerminalList)) {
            for (TemplateTerminal templateTerminal : templateTerminalList) {
                Long tid = templateTerminal.getId();
                int weight = templateTerminal.getWeight();
                BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(tid);
                if (busiTerminal == null) {
                    continue;
                }
                ParticipantRspDto participantRspDto = new ParticipantRspDto();
                SmcTemplateTerminal smcTemplateTerminal = new SmcTemplateTerminal();
                if (!Objects.isNull(busiTerminal)) {
                    String number = busiTerminal.getNumber();
                    smcTemplateTerminal.setSmcnumber(number);
                    participantRspDto.setUri(number);
                    participantRspDto.setName(busiTerminal.getName());
                    participantRspDto.setIpProtocolType(2);
                    participantRspDto.setDialMode("OUT");
                    participantRspDto.setVoice(false);
                    participantRspDto.setRate(0);
                    participantRspDto.setMainParticipant(false);
                    if (StringUtils.isBlank(number)) {
                        smcTemplateTerminal.setSmcnumber(busiTerminal.getIp());
                        participantRspDto.setUri(busiTerminal.getIp());
                    }
                    if (StringUtils.isNotBlank(busiTerminal.getIp()) && Strings.hasText(busiTerminal.getNumber())) {
                        String uri = busiTerminal.getNumber() + "@" + busiTerminal.getIp();
                        participantRspDto.setUri(uri);
                        smcTemplateTerminal.setSmcnumber(uri);
                    }
                    if (TerminalType.isFSBC(busiTerminal.getType())) {
                        if (StringUtils.isNotBlank(busiTerminal.getIp())) {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
                            smcTemplateTerminal.setSmcnumber(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
                        } else {
                            BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                            String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp);
                            smcTemplateTerminal.setSmcnumber(busiTerminal.getCredential() + "@" + callIp);
                        }
                    }

                    if (!TerminalType.isFCMSIP(busiTerminal.getType())) {
                        templateParticipants.add(participantRspDto);
                    }
                    //保存关系
                    smcTemplateTerminal.setTerminalId(tid);
                    smcTemplateTerminal.setTerminalDeptId(busiTerminal.getDeptId());
                    smcTemplateTerminal.setSmcTemplateId(id);
                    smcTemplateTerminal.setWeight(weight);
                    templateTerminals.add(smcTemplateTerminal);
                }
                smcTemplateTerminalService.add(smcTemplateTerminal);

            }
        }
    }

    /**
     * 修改模板会议
     */
    @PutMapping("/conferences/templatesInfo/{id}")
    public RestResponse updateMeetingRoomsInfo(@PathVariable Long id, @RequestBody JSONObject jsonObj) {
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplateById(id);
        String smcTemplateId = busiSmcDeptTemplate.getSmcTemplateId();

        BusiSmcTemplateConference busiSmcTemplateConference = new BusiSmcTemplateConference();
        busiSmcTemplateConference.setSmcTemplateId(smcTemplateId);
        List<BusiSmcTemplateConference> busiSmcTemplateConferences = smcTemplateConferenceService.selectBusiSmcTemplateConferenceList(busiSmcTemplateConference);
        if (!CollectionUtils.isEmpty(busiSmcTemplateConferences)) {
            throw new CustomException("会议进行中不能修改");
        }
        JSONObject smcConferenceTemplateRquestjson = jsonObj.getJSONObject("smcConferenceTemplateRquest");
        SmcConferenceTemplate smcConferenceTemplateRquest = smcConferenceTemplateRquestjson.toJavaObject(SmcConferenceTemplate.class);
        smcTemplateTerminalService.deleteBytemplateId(smcConferenceTemplateRquest.getId());
        List<SmcTemplateTerminal> templateTerminals = new ArrayList<>();
        List<Long> templateParticipantsIds = smcConferenceTemplateRquest.getTemplateParticipantsIds();
        List<ParticipantRspDto> templateParticipants = new ArrayList<>();
        if (!CollectionUtils.isEmpty(templateParticipantsIds)) {
            for (Long tid : templateParticipantsIds) {
                BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(tid);
                if (busiTerminal == null) {
                    continue;
                }
                ParticipantRspDto participantRspDto = new ParticipantRspDto();
                SmcTemplateTerminal smcTemplateTerminal = new SmcTemplateTerminal();
                if (!Objects.isNull(busiTerminal)) {
                    String number = busiTerminal.getNumber();
                    smcTemplateTerminal.setSmcnumber(number);
                    participantRspDto.setUri(number);
                    participantRspDto.setName(busiTerminal.getName());
                    participantRspDto.setIpProtocolType(2);
                    participantRspDto.setDialMode("OUT");
                    participantRspDto.setVoice(false);
                    participantRspDto.setRate(0);
                    participantRspDto.setMainParticipant(false);
                    if (StringUtils.isBlank(number)) {
                        if (TerminalType.isCisco(busiTerminal.getType())) {
                            participantRspDto.setUri(busiTerminal.getName() + "@" + busiTerminal.getIp());
                            smcTemplateTerminal.setSmcnumber(busiTerminal.getName() + "@" + busiTerminal.getIp());
                        } else {
                            participantRspDto.setUri(busiTerminal.getIp());
                            smcTemplateTerminal.setSmcnumber(busiTerminal.getIp());
                        }
                    }
                    if (StringUtils.isNotBlank(busiTerminal.getIp()) && Strings.hasText(busiTerminal.getNumber())) {
                        String uri = busiTerminal.getNumber() + "@" + busiTerminal.getIp();
                        participantRspDto.setUri(uri);
                        smcTemplateTerminal.setSmcnumber(uri);
                    }
                    if (TerminalType.isFSBC(busiTerminal.getType())) {
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(busiTerminal.getIp())) {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
                        } else {
                            BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                            String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            Integer sipPort = fsbcBridge.getBusiFsbcRegistrationServer().getSipPort();
                            if (sipPort == null) {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp);
                            } else {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":" + sipPort);
                            }
                        }
                    }
                    if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                        BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(busiFreeSwitchDept.getServerId());
                        String callIp = fcmBridge.getBusiFreeSwitch().getIp();
                        Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                        if (callPort == null) {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp);
                        } else {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":" + callPort);
                        }
                    }

                    templateParticipants.add(participantRspDto);
                    //保存关系
                    smcTemplateTerminal.setTerminalId(tid);
                    smcTemplateTerminal.setTerminalDeptId(busiTerminal.getDeptId());
                    smcTemplateTerminal.setSmcTemplateId(smcTemplateId);
                    smcTemplateTerminal.setSmcnumber(participantRspDto.getUri());
                    templateTerminals.add(smcTemplateTerminal);

                }
                smcTemplateTerminalService.add(smcTemplateTerminal);
            }
        }
        smcConferenceTemplateRquest.setTemplateParticipants(templateParticipants);
        return RestResponse.success(templateService.putTemplate(smcConferenceTemplateRquest.getId(), smcConferenceTemplateRquest));

    }

    /**
     * 修改模板会议
     */
    @Transactional
    @PutMapping("/conferences/templatesInfo/{id}/simple")
    public RestResponse editTemplate(@PathVariable Long id, @RequestBody BusiSmcTemplateConferenceRequest templateConferenceRequest) {
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplateById(id);
        String smcTemplateId = busiSmcDeptTemplate.getSmcTemplateId();
        Long masterTerminalId = templateConferenceRequest.getMasterTerminalId();
        BusiSmcTemplateConference busiSmcTemplateConference = new BusiSmcTemplateConference();
        busiSmcTemplateConference.setSmcTemplateId(smcTemplateId);
        List<BusiSmcTemplateConference> busiSmcTemplateConferences = smcTemplateConferenceService.selectBusiSmcTemplateConferenceList(busiSmcTemplateConference);
        if (!CollectionUtils.isEmpty(busiSmcTemplateConferences)) {
            throw new CustomException("会议进行中不能修改");
        }
        SmcConferenceTemplate smcConferenceTemplate = getSmcConferenceTemplate(templateConferenceRequest);
        smcTemplateTerminalService.deleteBytemplateId(busiSmcDeptTemplate.getSmcTemplateId());
        List<SmcTemplateTerminal> templateTerminals = new ArrayList<>();
        List<ParticipantRspDto> templateParticipants = new ArrayList();
        List<TemplateTerminal> templateTerminalList = templateConferenceRequest.getTemplateTerminalList();
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
                    participantRspDto.setMainParticipant(Objects.equals(busiTerminal.getId(), masterTerminalId));
                    if (StringUtils.isBlank(number)) {
                        if (TerminalType.isCisco(busiTerminal.getType())) {
                            participantRspDto.setUri(busiTerminal.getName() + "@" + busiTerminal.getIp());
                        } else {
                            participantRspDto.setUri(busiTerminal.getIp());
                        }
                    }
                    if (StringUtils.isNotBlank(busiTerminal.getIp()) && Strings.hasText(busiTerminal.getNumber())) {
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                    }
                    if (TerminalType.isFSBC(busiTerminal.getType())) {
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(busiTerminal.getIp())) {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp() + ":5060");
                        } else {
                            BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                            String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            Integer sipPort = fsbcBridge.getBusiFsbcRegistrationServer().getSipPort();
                            if (sipPort == null || sipPort == 5060) {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":5060");
                            } else {
                                participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":" + sipPort);
                            }
                        }
                        //注册到外部会议室
                        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(templateConferenceRequest.getDeptId());
                        SmcMeetingRoomRegTask smcMeetingRoomRegTask = new SmcMeetingRoomRegTask(participantRspDto.getUri(), busiTerminal.getName(), participantRspDto.getUri(), 200, bridge, smcUserService, busiTerminal);
                        smcDelayTaskService.addTask(smcMeetingRoomRegTask);
                    }
                    if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                        BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(busiTerminal.getDeptId());
                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(busiFreeSwitchDept.getServerId());
                        String callIp = fcmBridge.getBusiFreeSwitch().getIp();
                        Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                        if (callPort == null || callPort == 5060) {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":5060");
                        } else {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp + ":" + callPort);
                        }
                        //注册到外部会议室
                        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(templateConferenceRequest.getDeptId());
                        SmcMeetingRoomRegTask smcMeetingRoomRegTask = new SmcMeetingRoomRegTask(participantRspDto.getUri(), busiTerminal.getName(), participantRspDto.getUri(), 200, bridge, smcUserService, busiTerminal);
                        smcDelayTaskService.addTask(smcMeetingRoomRegTask);
                    }

                    if (TerminalType.isFmeTemplate(busiTerminal.getType())) {
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp() + ":5060");
                        BusiTemplateConferenceMapper templateConferenceMapper = BeanFactory.getBean(BusiTemplateConferenceMapper.class);
                        BusiTemplateConference templateConference = new BusiTemplateConference();
                        templateConference.setConferenceNumber(Long.valueOf(busiTerminal.getNumber()));
                        List<BusiTemplateConference> templateConferences = templateConferenceMapper.selectBusiTemplateConferenceList(templateConference);
                        if (!CollectionUtils.isEmpty(templateConferences)) {
                            String conferencePassword = templateConferences.get(0).getConferencePassword();
                            if (Strings.hasText(conferencePassword)) {
                                participantRspDto.setDtmfInfo(conferencePassword);
                            }
                        }
                    }
                    templateParticipants.add(participantRspDto);
                    //保存关系
                    SmcTemplateTerminal smcTemplateTerminal = new SmcTemplateTerminal();
                    smcTemplateTerminal.setTerminalId(templateTerminal.getId());
                    smcTemplateTerminal.setTerminalDeptId(busiTerminal.getDeptId());
                    smcTemplateTerminal.setSmcnumber(participantRspDto.getUri());
                    smcTemplateTerminal.setWeight(templateTerminal.getWeight());
                    smcTemplateTerminal.setSmcTemplateId(smcTemplateId);
                    smcTemplateTerminalService.add(smcTemplateTerminal);
                }


            }
        }
        smcConferenceTemplate.setId(smcTemplateId);
        smcConferenceTemplate.setTemplateParticipants(templateParticipants);


        busiSmcDeptTemplate.setTemplateName(templateConferenceRequest.getSubject());
        busiSmcDeptTemplate.setDuration(templateConferenceRequest.getDuration());
        busiSmcDeptTemplate.setType(templateConferenceRequest.getType());

        busiSmcDeptTemplate.setAmcRecord(templateConferenceRequest.getAmcRecord());
        busiSmcDeptTemplate.setSupportLive(templateConferenceRequest.getSupportLive());
        busiSmcDeptTemplate.setSupportRecord(templateConferenceRequest.getSupportRecord());

        busiSmcDeptTemplate.setVmrNumber(templateConferenceRequest.getVmrNumber());
        busiSmcDeptTemplate.setChairmanPassword(templateConferenceRequest.getChairmanPassword());
        busiSmcDeptTemplate.setGuestPassword(templateConferenceRequest.getGuestPassword());
        busiSmcDeptTemplate.setRate(templateConferenceRequest.getRate());
        busiSmcDeptTemplate.setMaxParticipantNum(templateConferenceRequest.getMaxParticipantNum());
        busiSmcDeptTemplate.setAutoMute(templateConferenceRequest.getAutoMute());
        busiSmcDeptTemplateService.update(busiSmcDeptTemplate);


        return RestResponse.success(templateService.putTemplate(smcConferenceTemplate.getId(), smcConferenceTemplate));

    }

    /**
     * 模板的 ID 找到会议模板
     *
     * @return
     */
    @GetMapping("/conferences/templateInfo/{id}/simple")
    public RestResponse getTemplateInfoByTemplateId(@PathVariable Long id) {
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplateById(id);
        if (busiSmcDeptTemplate == null) {
            return RestResponse.success();
        }
        List<SmcTemplateTerminal> list = smcTemplateTerminalService.list(busiSmcDeptTemplate.getSmcTemplateId());
        BusiSmcTemplateConferenceRep rep = new BusiSmcTemplateConferenceRep();
        BeanUtils.copyProperties(busiSmcDeptTemplate, rep);
        Long templateId = busiSmcDeptTemplate.getId();
        rep.setId(templateId.intValue());
        rep.setSubject(busiSmcDeptTemplate.getTemplateName());
        rep.setTemplateTerminalList(list);
        return RestResponse.success(rep);
    }

    /**
     * 模板的 ID 找到会议模板
     *
     * @return
     */
    @GetMapping("/conferences/templateInfo/{id}")
    public RestResponse getTemplateInfoByTemplateIdSimple(@PathVariable Long id) {
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplateById(id);
        String smcTemplateId = busiSmcDeptTemplate.getSmcTemplateId();
        String templateById = templateService.getTemplateById(smcTemplateId);
        if (org.springframework.util.StringUtils.hasText(templateById)) {
            if (templateById.contains(ConstAPI.TOKEN_NOT_EXIST)) {
                throw new CustomException("token不存在,请重试");
            }
            return getRestResponseTemplateSmcInfo(templateById);
        } else {
            //重新创建
            SmcConferenceTemplate smcConferenceTemplate = getSmcConferenceTemplate(busiSmcDeptTemplate, smcTemplateId);
            converSmcconferenceTemlateParticipants(smcConferenceTemplate);
            return RestResponse.success(smcConferenceTemplate);
        }
    }

    private SmcConferenceTemplate getSmcConferenceTemplate(BusiSmcDeptTemplate busiSmcDeptTemplate, String OldSmcTemplateId) {
        SmcConferenceTemplate smcConferenceTemplateRequest = buildTemplateConference();
        smcConferenceTemplateRequest.setChairmanPassword(busiSmcDeptTemplate.getChairmanPassword());
        smcConferenceTemplateRequest.setGuestPassword(busiSmcDeptTemplate.getChairmanPassword());
        smcConferenceTemplateRequest.setVmrNumber(busiSmcDeptTemplate.getVmrNumber());
        SmcConferenceTemplate.StreamServiceDTO streamService = smcConferenceTemplateRequest.getStreamService();
        streamService.setAmcRecord(busiSmcDeptTemplate.getAmcRecord() == 1);
        streamService.setSupportLive(busiSmcDeptTemplate.getSupportLive() == 1);
        streamService.setSupportRecord(busiSmcDeptTemplate.getSupportRecord() == 1);
        SmcConferenceTemplate.ConferenceCapabilitySettingDTO conferenceCapabilitySetting = smcConferenceTemplateRequest.getConferenceCapabilitySetting();
        conferenceCapabilitySetting.setRate(busiSmcDeptTemplate.getRate());
        SmcConferenceTemplate.ConferencePolicySettingDTO conferencePolicySetting = smcConferenceTemplateRequest.getConferencePolicySetting();
        conferencePolicySetting.setMaxParticipantNum(busiSmcDeptTemplate.getMaxParticipantNum());
        List<ParticipantRspDto> templateParticipants = new ArrayList<>();
        List<SmcTemplateTerminal> list = smcTemplateTerminalService.list(OldSmcTemplateId);
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(p -> {
                BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(p.getTerminalId());
                if (!Objects.isNull(busiTerminal)) {
                    ParticipantRspDto participantRspDto = new ParticipantRspDto();
                    String number = busiTerminal.getNumber();
                    participantRspDto.setName(busiTerminal.getName());
                    participantRspDto.setUri(number);
                    if (StringUtils.isBlank(number)) {
                        if (TerminalType.isCisco(busiTerminal.getType())) {
                            participantRspDto.setUri(busiTerminal.getName() + "@" + busiTerminal.getIp());
                        } else {
                            participantRspDto.setUri(busiTerminal.getIp());
                        }

                    }
                    if (StringUtils.isNotBlank(busiTerminal.getIp()) && Strings.hasText(busiTerminal.getNumber())) {
                        participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                    }
                    if (TerminalType.isFSBC(busiTerminal.getType())) {
                        if (StringUtils.isNotBlank(busiTerminal.getIp())) {
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp());
                        } else {
                            BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(busiTerminal.getDeptId());
                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId());
                            String callIp = fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            participantRspDto.setUri(busiTerminal.getCredential() + "@" + callIp);
                        }
                    }
                    if (!TerminalType.isFCMSIP(busiTerminal.getType())) {
                        templateParticipants.add(participantRspDto);
                    }

                }
            });
            smcConferenceTemplateRequest.setTemplateParticipants(templateParticipants);
        }
        smcConferenceTemplateRequest.setSubject(busiSmcDeptTemplate.getTemplateName());
        smcConferenceTemplateRequest.setDuration(busiSmcDeptTemplate.getDuration());
        SmcConferenceTemplate smcConferenceTemplate = templateService.addTemplateRoomSmc(smcConferenceTemplateRequest, busiSmcDeptTemplate.getDeptId());
        String conferenceTemplateId = smcConferenceTemplate.getId();

        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transactionStatus = transactionManager.getTransaction(defaultTransactionDefinition);

        try {
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(s -> {
                    s.setSmcTemplateId(conferenceTemplateId);
                    smcTemplateTerminalService.update(s);
                });

            }
            busiSmcDeptTemplate.setSmcTemplateId(conferenceTemplateId);
            busiSmcDeptTemplateService.update(busiSmcDeptTemplate);
            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("SMC模板更新失败", e);
            transactionManager.rollback(transactionStatus);
        }
        return smcConferenceTemplate;
    }

    private RestResponse getRestResponseTemplateSmcInfo(String templateById) {
        SmcConferenceTemplate smcConferenceTemplate = JSON.parseObject(templateById, SmcConferenceTemplate.class);
        converSmcconferenceTemlateParticipants(smcConferenceTemplate);
        return RestResponse.success(smcConferenceTemplate);
    }

    private void converSmcconferenceTemlateParticipants(SmcConferenceTemplate smcConferenceTemplate) {
        List<ParticipantRspDto> templateParticipants = smcConferenceTemplate.getTemplateParticipants();
        List<SmcTemplateTerminal> templateTerminals = smcTemplateTerminalService.list(smcConferenceTemplate.getId());
        setTemplateParticipantsAttr(templateParticipants, templateTerminals);

        if (!CollectionUtils.isEmpty(templateTerminals)) {
            for (SmcTemplateTerminal templateTerminal : templateTerminals) {
                BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(templateTerminal.getTerminalId());
                if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                    ParticipantRspDto contentDTO = new ParticipantRspDto();
                    contentDTO.setDeptId(busiTerminal.getDeptId());
                    contentDTO.setTerminalId(busiTerminal.getId());
                    contentDTO.setId(UUID.randomUUID().toString());

                    contentDTO.setName(busiTerminal.getName());
                    contentDTO.setUri(busiTerminal.getCredential() + "@" + busiTerminal.getIp());

                    Boolean onlineState = busiTerminal.getOnlineStatus() == 1;
                    contentDTO.setIsOnline(onlineState);
                    templateParticipants.add(contentDTO);
                }

            }
        }
    }

    /**
     * 模板的 ID 找到会议模板
     *
     * @return
     */
    @GetMapping("/conferences/templates/{id}")
    public RestResponse getTemplateInfo(@PathVariable String id) {
        String templateById = templateService.getTemplateById(id);
        if (!Objects.isNull(templateById)) {
            return getRestResponseTemplateSmcInfo(templateById);
        }

        return RestResponse.success();
    }

    private void setTemplateParticipantsAttr(List<ParticipantRspDto> templateParticipants, List<SmcTemplateTerminal> templateTerminals) {
        if (!CollectionUtils.isEmpty(templateParticipants)) {
            templateParticipants.stream().forEach(p -> {
                Optional<SmcTemplateTerminal> first = templateTerminals.stream().filter(m -> Objects.equals(p.getUri(), m.getSmcnumber())).findFirst();
                if (first.isPresent()) {
                    SmcTemplateTerminal smcTemplateTerminal = first.get();
                    p.setDeptId(smcTemplateTerminal.getTerminalDeptId());
                    p.setWeight(smcTemplateTerminal.getWeight());
                    p.setTerminalId(smcTemplateTerminal.getTerminalId());
                    BusiTerminal terminal = TerminalCache.getInstance().get(smcTemplateTerminal.getTerminalId());
                    p.setTerminalType(terminal.getType());
                    if (TerminalType.isMcuTemplateCisco(terminal.getType())) {
                        p.setIsCascade(true);
                    }
                }
            });

        }
    }

    /**
     * 获取会议模板详细信息
     */
    @GetMapping(value = "/getCurrentConferenceInfo/{id}")
    @Operation(summary = "获取会议模板详细信息")
    public RestResponse getCurrentConferenceInfo(@PathVariable("id") String id) {
        return getCurrentInfoRestResponse(smcConferenceService.buildTemplateConferenceContext(id), smcTemplateTerminalService.list(id));
    }

    /**
     * 获取会议模板详细信息
     */
    @GetMapping(value = "/getCurrentConferenceInfoByLocalId/{id}")
    @Operation(summary = "获取会议模板详细信息")
    public RestResponse getCurrentConferenceInfoByLocalId(@PathVariable("id") Long id) {
        RestResponse currentInfoRestResponse;
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplateById(id);
        String oldSmcTemplateId = busiSmcDeptTemplate.getSmcTemplateId();
        String templateById = templateService.getTemplateById(oldSmcTemplateId);
        if (org.springframework.util.StringUtils.hasText(templateById)) {
            if (templateById.contains(ConstAPI.TOKEN_NOT_EXIST)) {
                throw new CustomException("token不存在,请重试");
            }
            currentInfoRestResponse = getCurrentInfoRestResponse(smcConferenceService.buildTemplateConferenceContext(oldSmcTemplateId), smcTemplateTerminalService.list(oldSmcTemplateId));
        } else {
            SmcConferenceTemplate smcConferenceTemplate = getSmcConferenceTemplate(busiSmcDeptTemplate, oldSmcTemplateId);
            currentInfoRestResponse = getCurrentInfoRestResponse(smcConferenceService.buildTemplateConferenceContext(smcConferenceTemplate.getId()), smcTemplateTerminalService.list(smcConferenceTemplate.getId()));
        }

        SmcConferenceContext smcConferenceContext = (SmcConferenceContext) currentInfoRestResponse.getData();
        smcConferenceContext.getConference().setRate(busiSmcDeptTemplate.getRate());
        return currentInfoRestResponse;
    }

    private RestResponse getCurrentInfoRestResponse(SmcConferenceContext smcConferenceContext2, List<SmcTemplateTerminal> list) {
        SmcConferenceContext smcConferenceContext = smcConferenceContext2;
        List<SmcParitipantsStateRep.ContentDTO> contentDTOList = smcConferenceContext.getContent();

        ConferenceUiParam conferenceUiParam = smcConferenceContext.getDetailConference().getConferenceUiParam();

        Integer onlineParticipantNum = 0;

        if (!CollectionUtils.isEmpty(contentDTOList)) {
            for (SmcParitipantsStateRep.ContentDTO contentDTO : contentDTOList) {
                if (contentDTO.getState().getOnline()) {
                    onlineParticipantNum++;
                }
                Optional<SmcTemplateTerminal> first = list.stream().filter(m -> Objects.equals(contentDTO.getGeneralParam().getUri(), m.getSmcnumber())).findFirst();
                if (first.isPresent()) {
                    contentDTO.setWeight(first.get().getWeight());
                }
                Long terminalId = contentDTO.getTerminalId();
                BusiTerminal terminal = iBusiTerminalService.selectBusiTerminalById(terminalId);
                if (terminal != null) {
                    Boolean onlineState = terminal.getOnlineStatus() == 1;

                    contentDTO.setTerminalOnline(onlineState);
                    contentDTO.setTerminalType(terminal.getType());
                    if (TerminalType.isMcuTemplateCisco(terminal.getType())) {
                        contentDTO.setIsCascade(true);
                        contentDTO.setTerminalTypeName(TerminalType.MCU_TEMPLATE_CISCO.getDisplayName());
                        if (StringUtils.isNotBlank(smcConferenceContext.getConference().getId())) {
                            Map<String, String> stringStringMap = SmcConferenceContextCache.getInstance().getCascadeConference().get(smcConferenceContext.getConference().getId());
                            if (Objects.isNull(stringStringMap)) {
                                Map<String, String> uirMap = new HashMap<>();
                                uirMap.put(contentDTO.getGeneralParam().getId(), contentDTO.getGeneralParam().getUri());
                                SmcConferenceContextCache.getInstance().getCascadeConference().put(smcConferenceContext.getConference().getId(), uirMap);
                            } else {
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
                            if (Objects.isNull(stringStringMap)) {
                                Map<String, String> uirMap = new HashMap<>();
                                uirMap.put(contentDTO.getGeneralParam().getId(), contentDTO.getGeneralParam().getUri());
                                SmcConferenceContextCache.getInstance().getCascadeConference().put(smcConferenceContext.getConference().getId(), uirMap);
                            } else {
                                stringStringMap.put(contentDTO.getGeneralParam().getId(), contentDTO.getGeneralParam().getUri());
                            }
                        }
                    }

                }
            }
        }

        conferenceUiParam.setOnlineParticipantNum(onlineParticipantNum);
        conferenceUiParam.setTotalParticipantNum(contentDTOList.size());
        List<SmcTemplateTerminal> templateTerminals = list;
        smcConferenceContext.setContent(contentDTOList);
        List<SmcParitipantsStateRep.ContentDTO> collect = smcConferenceContext.getContent().stream().sorted(Comparator.comparing(SmcParitipantsStateRep.ContentDTO::getWeight, Comparator.reverseOrder())).collect(Collectors.toList());
        smcConferenceContext.setContent(collect);
        if (smcConferenceContext.getConference().getId() != null) {
            CoSpace coSpace = (CoSpace) SmcConferenceContextCache.getInstance().getMonitorParticipantMap().get(smcConferenceContext.getConference().getId());
            if (coSpace != null) {
                smcConferenceContext.setMonitorNumber(coSpace.getUri());
            }


        }


        return RestResponse.success(smcConferenceContext);
    }

    public SmcConferenceTemplate buildTemplateConference() {

        SmcConferenceTemplate smcConferenceTemplateRquest = new SmcConferenceTemplate();
        smcConferenceTemplateRquest.setGuestPassword(null);
        smcConferenceTemplateRquest.setChairmanPassword(null);
        smcConferenceTemplateRquest.setMainMcuId("");
        smcConferenceTemplateRquest.setMainMcuName("");
        smcConferenceTemplateRquest.setMainServiceZoneId("");
        smcConferenceTemplateRquest.setTemplateType("COMMON_CONF");
        smcConferenceTemplateRquest.setVmrNumber("");
        SmcConferenceTemplate.ConferenceCapabilitySettingDTO conferenceCapabilitySetting = new SmcConferenceTemplate.ConferenceCapabilitySettingDTO();
        conferenceCapabilitySetting.setSvcVideoResolution(SVC_VIDEO_RESOLUTION);
        conferenceCapabilitySetting.setVideoResolution(VIDEO_RESOLUTION);
        conferenceCapabilitySetting.setAmcRecord(false);
        conferenceCapabilitySetting.setAudioProtocol("AAC_LD_S");
        conferenceCapabilitySetting.setAudioRecord(false);
        conferenceCapabilitySetting.setAutoRecord(false);
        conferenceCapabilitySetting.setCheckInDuration(10);
        conferenceCapabilitySetting.setDataConfProtocol("DATA_RESOLUTION_STANDARD");
        conferenceCapabilitySetting.setEnableCheckIn(false);
        conferenceCapabilitySetting.setEnableDataConf(false);
        conferenceCapabilitySetting.setEnableFec(false);
        conferenceCapabilitySetting.setEnableLiveBroadcast(false);
        conferenceCapabilitySetting.setEnableRecord(false);
        conferenceCapabilitySetting.setMediaEncrypt("NOT_ENCRYPT_MODE");//AUTO_ENCRYPT_MODE NOT_ENCRYPT_MODE
        conferenceCapabilitySetting.setRate(1920);
        conferenceCapabilitySetting.setReserveResource(0);
        conferenceCapabilitySetting.setSvcRate(3840);
        conferenceCapabilitySetting.setVideoProtocol("H265");
        conferenceCapabilitySetting.setType("AVC");
        conferenceCapabilitySetting.setVideoProtocol("H264_BP");//H264_HP
        smcConferenceTemplateRquest.setConferenceCapabilitySetting(conferenceCapabilitySetting);
        SmcConferenceTemplate.ConferencePolicySettingDTO policySettingDTO = new SmcConferenceTemplate.ConferencePolicySettingDTO();
        policySettingDTO.setAutoEnd(true);
        policySettingDTO.setAutoExtend(true);
        policySettingDTO.setAutoMute(false);
        policySettingDTO.setChairmanPassword("");
        policySettingDTO.setGuestPassword("");
        policySettingDTO.setLanguage(1);
        policySettingDTO.setVoiceActive(false);

        smcConferenceTemplateRquest.setConferencePolicySetting(policySettingDTO);

        SmcConferenceTemplate.StreamServiceDTO streamServiceDTO = new SmcConferenceTemplate.StreamServiceDTO();
        streamServiceDTO.setSupportMinutes(false);
        smcConferenceTemplateRquest.setStreamService(streamServiceDTO);

        SmcConferenceTemplate.SubtitleServiceDTO subtitleServiceDTO = new SmcConferenceTemplate.SubtitleServiceDTO();
        subtitleServiceDTO.setEnableSubtitle(false);
        subtitleServiceDTO.setSrcLang("CHINESE");
        smcConferenceTemplateRquest.setSubtitleService(subtitleServiceDTO);

        return smcConferenceTemplateRquest;

    }

}
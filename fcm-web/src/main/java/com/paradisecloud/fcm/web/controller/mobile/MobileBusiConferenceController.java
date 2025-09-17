package com.paradisecloud.fcm.web.controller.mobile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.model.MinutesParam;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.McuTypeVo;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.conference.interfaces.*;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcBridgeCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.*;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.*;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.*;
import com.paradisecloud.fcm.mqtt.cache.AppointmentCache;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.service.minutes.StartMeetingMinutesTask;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.fcm.web.model.mobile.AppointmentConferenceSyncExecutor;
import com.paradisecloud.fcm.web.model.mobile.MobileConferenceAppointmentRequest;
import com.paradisecloud.fcm.web.model.mobile.req.DefaultViewCellScreens;
import com.paradisecloud.fcm.web.model.mobile.vo.MobileAttendeeVo;
import com.paradisecloud.fcm.web.service.interfaces.IBusiAllMcuService;
import com.paradisecloud.fcm.web.task.ConferenceTakeSnapshotPdfTask;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.service.interfaces.*;
import com.paradisecloud.system.dao.mapper.SysConfigMapper;
import com.paradisecloud.system.dao.model.SysConfig;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author nj
 * @date 2023/1/6 9:59
 */
@RestController
@RequestMapping("/mobile/conference")
@Tag(name = "移动端会议直播管理")
public class MobileBusiConferenceController extends MobileBaseController {

    private static final String SCHEME_NAME = "全局轮询";
    private static final String IS_AUTO_CALL = "isAutoCall";
    private static final String CONFERENCE_NAME = "conferenceName";
    private static final String DEFAULT_VIEW_LAYOUT = "defaultViewLayout";
    private static final String DEFAULT_VIEW_IS_DISPLAY_SELF = "defaultViewIsDisplaySelf";
    private static final String RECORDING_ENABLED = "recordingEnabled";
    private static final String STREAMING_ENABLED = "streamingEnabled";
    private static final String DEFAULT_VIEW_IS_BROADCAST = "defaultViewIsBroadcast";
    private static final String DEFAULT_VIEW_IS_FILL = "defaultViewIsFill";
    private static final String BUSINESS_FIELD_TYPE = "businessFieldType";
    private static final String ALL_EQUAL = "allEqual";
    private static final int INT4 = 4;
    private static final int INT8 = 8;

    @Resource
    private IBusiConferenceService busiConferenceService;

    @Resource
    private IBusiMcuZjConferenceService busiMcuZjConferenceService;

    @Resource
    private IBusiMcuPlcConferenceService busiMcuPlcConferenceService;

    @Resource
    private IBusiMcuKdcConferenceService busiMcuKdcConferenceService;

    @Resource
    private IBusiSmc3ConferenceService busiSmc3ConferenceService;

    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;
    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;

    @Resource
    private IBusiMcuKdcTemplateConferenceService busiMcuKdcTemplateConferenceService;

    @Resource
    private IBusiMcuPlcTemplateConferenceService busiMcuPlcTemplateConferenceService;

    @Resource
    private IBusiMcuSmc3TemplateConferenceService busiMcuSmc3TemplateConferenceService;

    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;

    @Resource
    private IBusiMcuZjTemplateConferenceService busiMcuZjTemplateConferenceService;

    @Resource
    private IBusiRecordsService busiRecordsService;
    @Resource
    private IBusiRecordsForMcuZjService busiRecordsForMcuZjService;
    @Resource
    private IBusiRecordsForMcuPlcService busiRecordsForMcuPlcService;
    @Resource
    private IBusiRecordsForMcuKdcService busiRecordsForMcuKdcService;
    @Resource
    private IBusiRecordsForMcuSmc3Service busiRecordsForMcuSmc3Service;
    @Resource
    private IAttendeeService attendeeService;
    @Resource
    private IAttendeeForMcuZjService attendeeForMcuZjService;
    @Resource
    private IAttendeeForMcuPlcService attendeeForMcuPlcService;
    @Resource
    private IAttendeeForMcuKdcService attendeeForMcuKdcService;
    @Resource
    private IAttendeeSmc3Service attendeeSmc3Service;
    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;
    @Resource
    private BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper;
    @Resource
    private BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper;
    @Resource
    private BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper;
    @Resource
    private BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper;

    @Resource
    private IDefaultAttendeeOperationPackageService defaultAttendeeOperationPackageService;
    @Resource
    private IDefaultAttendeeOperationPackageForMcuZjService defaultAttendeeOperationPackageForMcuZjService;
    @Resource
    private IDefaultAttendeeOperationPackageForMcuPlcService defaultAttendeeOperationPackageForMcuPlcService;
    @Resource
    private IDefaultAttendeeOperationPackageForMcuKdcService defaultAttendeeOperationPackageForMcuKdcService;
    @Resource
    private IBusiConferenceAppointmentService busiConferenceAppointmentService;
    @Resource
    private IBusiMcuZjConferenceAppointmentService busiMcuZjConferenceAppointmentService;
    @Resource
    private IBusiMcuPlcConferenceAppointmentService busiMcuPlcConferenceAppointmentService;
    @Resource
    private IBusiMcuKdcConferenceAppointmentService busiMcuKdcConferenceAppointmentService;
    @Resource
    private IBusiMcuSmc3ConferenceAppointmentService busiMcuSmc3ConferenceAppointmentService;
    @Resource
    private IBusiAllMcuService busiAllMcuService;
    @Resource
    private BusiConferenceAppointmentMapper busiConferenceAppointmentMapper;
    @Resource
    private BusiMcuSmc3ConferenceAppointmentMapper busiMcuSmc3ConferenceAppointmentMapper;
    @Resource
    private AppointmentConferenceSyncExecutor appointmentConferenceSyncExecutor;
    @Resource
    private IBusiTerminalService busiTerminalService;


    private static ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2,
            new BasicThreadFactory.Builder().namingPattern("MobileConferenceAppointment-schedule-pool-%d").daemon(true).build());


    @PutMapping("/stream/{conferenceId}/{enabled}")
    @Operation(summary = "开始直播,关闭直播")
    public RestResponse stream(@PathVariable String conferenceId, @PathVariable Boolean enabled) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        String streamingUrl = baseConferenceContext.getStreamingUrl();
        if (StringUtils.isEmpty(streamingUrl)) {
            return fail(1, "该会议不支持直播！");
        }
        switch (mcuType) {
            case FME: {
                busiConferenceService.stream(conferenceId, enabled, streamingUrl);
                break;
            }
            case MCU_ZJ: {
                busiMcuZjConferenceService.stream(conferenceId, enabled, streamingUrl);
                break;
            }
            case MCU_PLC: {
                busiMcuPlcConferenceService.stream(conferenceId, enabled, streamingUrl);
                break;
            }
            case MCU_KDC: {
                busiMcuKdcConferenceService.stream(conferenceId, enabled, streamingUrl);
                break;
            }
            case SMC3: {
                busiSmc3ConferenceService.stream(conferenceId, enabled, streamingUrl);
            }
        }

        return success();
    }

    @PutMapping("/record/{conferenceId}/{enabled}")
    @Operation(summary = "开始直播,关闭直播")
    public RestResponse record(@PathVariable String conferenceId, @PathVariable Boolean enabled) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                busiRecordsService.updateBusiRecords(enabled, contextKey);
                break;
            }
            case MCU_ZJ: {
                busiRecordsForMcuZjService.updateBusiRecords(enabled, contextKey);
                break;
            }
            case MCU_PLC: {
                busiRecordsForMcuPlcService.updateBusiRecords(enabled, contextKey);
                break;
            }
            case MCU_KDC: {
                busiRecordsForMcuKdcService.updateBusiRecords(enabled, contextKey);
                break;
            }
            case SMC3: {
                busiRecordsForMcuSmc3Service.updateBusiRecords(enabled, contextKey);
                break;
            }
        }

        return success();
    }

    @PutMapping("/minutes/{conferenceId}/{enabled}")
    @Operation(summary = "开始会议纪要,关闭会议纪要")
    public RestResponse minutes(@PathVariable String conferenceId, @PathVariable Boolean enabled) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        if (enabled) {
            if (baseConferenceContext.isMinutes()) {
                return RestResponse.fail("会议纪要使用中，请刷新后重试！");
            }
            MinutesParam minutesParam = null;
            String host = "";
            BusiTransServerMapper busiTransServerMapper = BeanFactory.getBean(BusiTransServerMapper.class);
            List<BusiTransServer> busiTransServerList = busiTransServerMapper.selectBusiTransServerList(new BusiTransServer());
            if (busiTransServerList.size() > 0) {
                BusiTransServer busiTransServer = busiTransServerList.get(0);
                try {
                    host = busiTransServer.getIp();
                } catch (Exception e) {
                }

                if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(host)) {
                    if (minutesParam == null) {
                        minutesParam = new MinutesParam();
                    }
                    if ("normal".equals(minutesParam.getTransType())) {
                        if (minutesParam.getLang() == null) {
                            minutesParam.setLang("cn");
                            minutesParam.setTransStrategy(2);
                            minutesParam.setTargetLang("en");
                        }
                    } else {
                        minutesParam.setTransType(null);
                        minutesParam.setLang(null);
                        minutesParam.setTransStrategy(null);
                        minutesParam.setTargetLang(null);
                    }
                    baseConferenceContext.setMinutesParam(minutesParam);
                    if (baseConferenceContext instanceof ConferenceContext) {
                        StartMeetingMinutesTask startMeetingMinutesTask = new StartMeetingMinutesTask(conferenceId, 1000, conferenceId, host, 9900);
                        TaskService taskService = BeanFactory.getBean(TaskService.class);
                        taskService.addTask(startMeetingMinutesTask);
                        return success();
                    } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
                        StartMeetingMinutesTask startMeetingMinutesTask = new StartMeetingMinutesTask(conferenceId, 1000, conferenceId, host, 9900);
                        TaskService taskService = BeanFactory.getBean(TaskService.class);
                        taskService.addTask(startMeetingMinutesTask);
                        return success();
                    }
                } else {
                    return RestResponse.fail("未配置虚拟终端服务器！");
                }
            }
        } else {
            if (baseConferenceContext instanceof ConferenceContext) {
                if (baseConferenceContext.getMinutesAttendee() != null) {
                    String attendeeId = baseConferenceContext.getMinutesAttendee().getId();
                    IAttendeeService attendeeService = BeanFactory.getBean(IAttendeeService.class);
                    attendeeService.hangUp(conferenceId, attendeeId);
                    if (baseConferenceContext.getAttendeeById(attendeeId) != null) {
                        attendeeService.remove(conferenceId, baseConferenceContext.getMinutesAttendee().getId());
                    }
                    baseConferenceContext.closeMinutesLog();
                }
                return success();
            } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
                if (baseConferenceContext.getMinutesAttendee() != null) {
                    String attendeeId = baseConferenceContext.getMinutesAttendee().getId();
                    IAttendeeService attendeeService = BeanFactory.getBean(IAttendeeService.class);
                    attendeeService.hangUp(conferenceId, attendeeId);
                    if (baseConferenceContext.getAttendeeById(attendeeId) != null) {
                        attendeeService.remove(conferenceId, baseConferenceContext.getMinutesAttendee().getId());
                    }
                    baseConferenceContext.closeMinutesLog();
                }
                return success();
            }
        }
        return RestResponse.fail();
    }

    @PutMapping("/presenter/{conferenceId}/{enabled}/{userId}")
    @Operation(summary = "设置主持人")
    public RestResponse setPresenter(@PathVariable String conferenceId, @PathVariable Boolean enabled, @PathVariable Long userId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        if (baseConferenceContext instanceof ConferenceContext) {
            busiConferenceService.updateConferencePresenter(conferenceId, enabled, userId);
        } else if (baseConferenceContext instanceof McuZjConferenceContext) {
            McuZjConferenceContext mcuZjConferenceContext = (McuZjConferenceContext) baseConferenceContext;
            if (enabled) {
                mcuZjConferenceContext.setPresenter(userId);
            } else {
                mcuZjConferenceContext.setPresenter(0l);
            }
            BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mcuZjConferenceContext);
        } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
            McuPlcConferenceContext mcuPlcConferenceContext = (McuPlcConferenceContext) baseConferenceContext;
            if (enabled) {
                mcuPlcConferenceContext.setPresenter(userId);
            } else {
                mcuPlcConferenceContext.setPresenter(0l);
            }
            BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mcuPlcConferenceContext);
        } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
            McuKdcConferenceContext mcuKdcConferenceContext = (McuKdcConferenceContext) baseConferenceContext;
            if (enabled) {
                mcuKdcConferenceContext.setPresenter(userId);
            } else {
                mcuKdcConferenceContext.setPresenter(0l);
            }
            BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mcuKdcConferenceContext);
        } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
            Smc3ConferenceContext smc3ConferenceContext = (Smc3ConferenceContext) baseConferenceContext;
            if (enabled) {
                smc3ConferenceContext.setPresenter(userId);
            } else {
                smc3ConferenceContext.setPresenter(0l);
            }
            BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(smc3ConferenceContext);
        }

        return success();
    }

    @PutMapping("/lock/{conferenceId}/{enabled}")
    @Operation(summary = "会议锁定")
    public RestResponse lock(@PathVariable String conferenceId, @PathVariable Boolean enabled) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                busiConferenceService.lock(conferenceId, enabled);
                break;
            }
            case MCU_ZJ: {
                busiMcuZjConferenceService.lock(conferenceId, enabled);
                break;
            }
            case MCU_PLC: {
                busiMcuPlcConferenceService.lock(conferenceId, enabled);
                break;
            }
            case MCU_KDC: {
                busiMcuKdcConferenceService.lock(conferenceId, enabled);
                break;
            }
            case SMC3: {
                busiSmc3ConferenceService.lock(conferenceId, enabled);
            }
        }

        return success();
    }

    /**
     * 通过会议号码查询查询会议详情
     */
    @GetMapping("/conferenceNumber/{conferenceNumber}")
    @Operation(summary = "通过会议号码查询查询会议详情")
    public RestResponse listKeySearch(@PathVariable Long conferenceNumber) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = loginUser.getUser().getDeptId();
        List<String> mcuTypes = new ArrayList<>();
        mcuTypes.add(McuType.FME.getCode());
        mcuTypes.add(McuType.MCU_ZJ.getCode());
        mcuTypes.add(McuType.MCU_PLC.getCode());
        mcuTypes.add(McuType.MCU_KDC.getCode());
        ViewTemplateConference viewTemplateConference = new ViewTemplateConference();
        viewTemplateConference.setDeptId(deptId);
        viewTemplateConference.setConferenceNumber(conferenceNumber);
        viewTemplateConference.getParams().put("mcuTypes", mcuTypes);
        List<ViewTemplateConference> allList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConference);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (ViewTemplateConference viewTemplateConferenceTemp : allList) {
            McuType mcuType = McuType.convert(viewTemplateConferenceTemp.getMcuType());
            switch (mcuType) {
                case FME: {
                    BusiTemplateConference templateConferenceCon = new BusiTemplateConference();
                    templateConferenceCon.setId(viewTemplateConferenceTemp.getId());
                    templateConferenceCon.setBusinessFieldType(BusinessFieldType.COMMON.getValue());
                    List<BusiTemplateConference> list = busiTemplateConferenceService.selectBusiTemplateConferenceList(templateConferenceCon);
                    if (!org.springframework.util.CollectionUtils.isEmpty(list)) {
                        BusiTemplateConference templateConference = list.get(0);
                        Long templateConferenceId = templateConference.getId();
                        ConferenceContext conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        List<ModelBean> splitScreenList = new ArrayList<>();
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "一分屏");
                            modelBean.put("value", OneSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "四分屏");
                            modelBean.put("value", FourSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "九分屏");
                            modelBean.put("value", NineSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "自动");
                            modelBean.put("value", AutomaticSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "全等");
                            modelBean.put("value", AllEqualSplitScreen.LAYOUT);
                            modelBean.put("isDefault", true);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "一大N小");
                            modelBean.put("value", OnePlusNSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }

                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
                case MCU_ZJ: {
                    BusiMcuZjTemplateConference templateConferenceCon = new BusiMcuZjTemplateConference();
                    templateConferenceCon.setId(viewTemplateConferenceTemp.getId());
                    templateConferenceCon.setBusinessFieldType(BusinessFieldType.COMMON.getValue());
                    List<BusiMcuZjTemplateConference> list = busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceList(templateConferenceCon);
                    if (!org.springframework.util.CollectionUtils.isEmpty(list)) {
                        BusiMcuZjTemplateConference templateConference = list.get(0);
                        Long templateConferenceId = templateConference.getId();
                        McuZjConferenceContext conferenceContext = busiMcuZjConferenceService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        json.remove("supervisorPassword");
                        List<ModelBean> splitScreenList = new ArrayList<>();

                        if (conferenceContext.isSingleView()) {
                            splitScreenList = conferenceContext.getSpeakerSplitScreenList();
                        } else {
                            splitScreenList = conferenceContext.getGuestSplitScreenList();
                        }
                        for (int i = 0; i < splitScreenList.size(); i++) {
                            ModelBean modelBean = splitScreenList.get(i);
                            if (i == 0) {
                                modelBean.put("isDefault", true);
                            } else {
                                modelBean.put("isDefault", false);
                            }
                        }
                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
                case MCU_PLC: {
                    BusiMcuPlcTemplateConference templateConferenceCon = new BusiMcuPlcTemplateConference();
                    templateConferenceCon.setId(viewTemplateConferenceTemp.getId());
                    templateConferenceCon.setBusinessFieldType(BusinessFieldType.COMMON.getValue());
                    List<BusiMcuPlcTemplateConference> list = busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceList(templateConferenceCon);
                    if (!org.springframework.util.CollectionUtils.isEmpty(list)) {
                        BusiMcuPlcTemplateConference templateConference = list.get(0);
                        Long templateConferenceId = templateConference.getId();
                        McuPlcConferenceContext conferenceContext = busiMcuPlcConferenceService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        json.remove("supervisorPassword");
                        List<ModelBean> splitScreenList = conferenceContext.getSpeakerSplitScreenList();
                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
                case MCU_KDC: {
                    BusiMcuKdcTemplateConference templateConferenceCon = new BusiMcuKdcTemplateConference();
                    templateConferenceCon.setId(viewTemplateConferenceTemp.getId());
                    templateConferenceCon.setBusinessFieldType(BusinessFieldType.COMMON.getValue());
                    List<BusiMcuKdcTemplateConference> list = busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceList(templateConferenceCon);
                    if (!org.springframework.util.CollectionUtils.isEmpty(list)) {
                        BusiMcuKdcTemplateConference templateConference = list.get(0);
                        Long templateConferenceId = templateConference.getId();
                        McuKdcConferenceContext conferenceContext = busiMcuKdcConferenceService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        json.remove("supervisorPassword");
                        List<ModelBean> splitScreenList = conferenceContext.getSpeakerSplitScreenList();
                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
                case SMC3: {
                    BusiMcuSmc3TemplateConference templateConferenceCon = new BusiMcuSmc3TemplateConference();
                    templateConferenceCon.setId(viewTemplateConferenceTemp.getId());
                    templateConferenceCon.setBusinessFieldType(BusinessFieldType.COMMON.getValue());
                    List<BusiMcuSmc3TemplateConference> list = busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceList(templateConferenceCon);
                    if (!org.springframework.util.CollectionUtils.isEmpty(list)) {
                        BusiMcuSmc3TemplateConference templateConference = list.get(0);
                        Long templateConferenceId = templateConference.getId();
                        Smc3ConferenceContext conferenceContext = busiSmc3ConferenceService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        json.remove("chairmanPassword");
                        json.put("conferencePassword", conferenceContext.getGuestPassword());
                        List<ModelBean> splitScreenList = new ArrayList<>();
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "一分屏");
                            modelBean.put("value", OneSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "四分屏");
                            modelBean.put("value", FourSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "九分屏");
                            modelBean.put("value", NineSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "自动");
                            modelBean.put("value", AutomaticSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "全等");
                            modelBean.put("value", AllEqualSplitScreen.LAYOUT);
                            modelBean.put("isDefault", true);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "一大N小");
                            modelBean.put("value", OnePlusNSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }

                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
            }
        }
        return RestResponse.success(0, "查询成功", jsonObjectList);
    }

    /**
     * 通过会议号码查询查询会议详情
     */
    @GetMapping("/getInfo")
    @Operation(summary = "通过会议号码查询查询会议详情")
    public RestResponse getInfo(@RequestParam("conferenceNumber") String conferenceNumber) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = loginUser.getUser().getDeptId();
        List<String> mcuTypes = new ArrayList<>();
        mcuTypes.add(McuType.FME.getCode());
        mcuTypes.add(McuType.MCU_ZJ.getCode());
        mcuTypes.add(McuType.MCU_PLC.getCode());
        mcuTypes.add(McuType.MCU_KDC.getCode());
        ViewTemplateConference viewTemplateConference = new ViewTemplateConference();
        viewTemplateConference.setDeptId(deptId);
        viewTemplateConference.setConferenceNumber(Long.valueOf(conferenceNumber));
        viewTemplateConference.getParams().put("mcuTypes", mcuTypes);
        List<ViewTemplateConference> allList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConference);
        JSONObject jsonObject = null;
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (ViewTemplateConference viewTemplateConferenceTemp : allList) {
            McuType mcuType = McuType.convert(viewTemplateConferenceTemp.getMcuType());
            switch (mcuType) {
                case FME: {
                    BusiTemplateConference templateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(viewTemplateConferenceTemp.getId());
                    if (templateConference != null) {
                        Long templateConferenceId = templateConference.getId();
                        ConferenceContext conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        List<ModelBean> splitScreenList = new ArrayList<>();
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "一分屏");
                            modelBean.put("value", OneSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "四分屏");
                            modelBean.put("value", FourSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "九分屏");
                            modelBean.put("value", NineSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "自动");
                            modelBean.put("value", AutomaticSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "全等");
                            modelBean.put("value", AllEqualSplitScreen.LAYOUT);
                            modelBean.put("isDefault", true);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "一大N小");
                            modelBean.put("value", OnePlusNSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }

                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
                case MCU_ZJ: {
                    BusiMcuZjTemplateConference templateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(viewTemplateConferenceTemp.getId());
                    if (templateConference != null) {
                        Long templateConferenceId = templateConference.getId();
                        McuZjConferenceContext conferenceContext = busiMcuZjConferenceService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        json.remove("supervisorPassword");
                        List<ModelBean> splitScreenList = new ArrayList<>();

                        if (conferenceContext.isSingleView()) {
                            splitScreenList = conferenceContext.getSpeakerSplitScreenList();
                        } else {
                            splitScreenList = conferenceContext.getGuestSplitScreenList();
                        }
                        for (int i = 0; i < splitScreenList.size(); i++) {
                            ModelBean modelBean = splitScreenList.get(i);
                            if (i == 0) {
                                modelBean.put("isDefault", true);
                            } else {
                                modelBean.put("isDefault", false);
                            }
                        }
                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
                case MCU_PLC: {
                    BusiMcuPlcTemplateConference templateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(viewTemplateConferenceTemp.getId());
                    if (templateConference != null) {
                        Long templateConferenceId = templateConference.getId();
                        McuPlcConferenceContext conferenceContext = busiMcuPlcConferenceService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        json.remove("supervisorPassword");
                        List<ModelBean> splitScreenList = conferenceContext.getSpeakerSplitScreenList();
                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
                case MCU_KDC: {
                    BusiMcuKdcTemplateConference templateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(viewTemplateConferenceTemp.getId());
                    if (templateConference != null) {
                        Long templateConferenceId = templateConference.getId();
                        McuKdcConferenceContext conferenceContext = busiMcuKdcConferenceService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        json.remove("supervisorPassword");
                        List<ModelBean> splitScreenList = conferenceContext.getSpeakerSplitScreenList();
                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
                case SMC3: {
                    BusiMcuSmc3TemplateConference templateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(viewTemplateConferenceTemp.getId());
                    if (templateConference != null) {
                        Long templateConferenceId = templateConference.getId();
                        Smc3ConferenceContext conferenceContext = busiSmc3ConferenceService.buildTemplateConferenceContext(templateConferenceId);
                        JSONObject json = null;
                        ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }
                        boolean isPresenter = false;
                        boolean isMyConference = false;
                        if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isPresenter = true;
                        }
                        if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                            isMyConference = true;
                        }
                        json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                        json.put("isPresenter", isPresenter);
                        json.put("isMyConference", isMyConference);
                        json.remove("chairmanPassword");
                        json.put("conferencePassword", conferenceContext.getGuestPassword());
                        List<ModelBean> splitScreenList = new ArrayList<>();
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "一分屏");
                            modelBean.put("value", OneSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "四分屏");
                            modelBean.put("value", FourSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "九分屏");
                            modelBean.put("value", NineSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "自动");
                            modelBean.put("value", AutomaticSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "全等");
                            modelBean.put("value", AllEqualSplitScreen.LAYOUT);
                            modelBean.put("isDefault", true);
                            splitScreenList.add(modelBean);
                        }
                        {
                            ModelBean modelBean = new ModelBean();
                            modelBean.put("name", "一大N小");
                            modelBean.put("value", OnePlusNSplitScreen.LAYOUT);
                            modelBean.put("isDefault", false);
                            splitScreenList.add(modelBean);
                        }

                        json.put("splitScreenList", splitScreenList);
                        jsonObjectList.add(json);
                    }
                    break;
                }
            }
        }
        if (jsonObjectList.size() > 0) {
            jsonObject = jsonObjectList.get(0);
        }
        return RestResponse.success(0, "查询成功", jsonObject);
    }

    /**
     * 根据会议ID获取会议信息
     */
    @GetMapping(value = "/getCurrentConferenceInfo")
    @Operation(summary = "获取会议详细信息(会议ID)")
    public RestResponse getCurrentConferenceInfo(@RequestParam("conferenceId") String conferenceId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                ConferenceContext conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(id);
//                if ("ops".equalsIgnoreCase(ExternalConfigCache.getInstance().getRegion())) {
                    List<Attendee> attendeesOps =  new ArrayList<>();
                    HashSet<String> attendeeIdSet = new HashSet<>();
                    if (conferenceContext.getAttendeesOps() != null) {
                        for (Attendee attendeeTemp : conferenceContext.getAttendeesOps()) {
                            boolean isMasterAttendee = false;
                            Attendee attendeeExist = conferenceContext.getAttendeeById(attendeeTemp.getId());
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeTemp.getId())) {
                                attendeeExist = conferenceContext.getMasterAttendee();
                                isMasterAttendee = true;
                            }
                            if (attendeeExist != null) {
                                if (!attendeeIdSet.contains(attendeeExist.getId())) {
                                    attendeeTemp.setMasterAttendee(isMasterAttendee);
                                    attendeesOps.add(attendeeExist);
                                    attendeeIdSet.add(attendeeExist.getId());
                                }
                            }
                        }
                    }
                    if (conferenceContext.getMasterAttendee() != null) {
                        Attendee attendeeTemp = conferenceContext.getMasterAttendee();
                        if (!attendeeIdSet.contains(attendeeTemp.getId())) {
                            attendeeTemp.setMasterAttendee(true);
                            attendeesOps.add(attendeeTemp);
                            attendeeIdSet.add(attendeeTemp.getId());
                        }
                    }
                    if (conferenceContext.getAttendees() != null) {
                        for (Attendee attendeeTemp : conferenceContext.getAttendees()) {
                            if (!attendeeIdSet.contains(attendeeTemp.getId())) {
                                attendeeTemp.setMasterAttendee(false);
                                attendeesOps.add(attendeeTemp);
                                attendeeIdSet.add(attendeeTemp.getId());
                            }
                        }
                    }
                    conferenceContext.setAttendeesOps(attendeesOps);
//                }
                ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                JSONObject json = null;
                try {
                    json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                } catch (JsonProcessingException e) {
                }
                boolean isPresenter = false;
                boolean isMyConference = false;
                if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                    isPresenter = true;
                }
                if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                    isMyConference = true;
                }
                json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                json.put("isPresenter", isPresenter);
                json.put("isMyConference", isMyConference);
                List<ModelBean> splitScreenList = new ArrayList<>();
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "一分屏");
                    modelBean.put("value", OneSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    splitScreenList.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "四分屏");
                    modelBean.put("value", FourSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    splitScreenList.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "九分屏");
                    modelBean.put("value", NineSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    splitScreenList.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "自动");
                    modelBean.put("value", AutomaticSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    splitScreenList.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "全等");
                    modelBean.put("value", AllEqualSplitScreen.LAYOUT);
                    modelBean.put("isDefault", true);
                    splitScreenList.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "一大N小");
                    modelBean.put("value", OnePlusNSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    splitScreenList.add(modelBean);
                }
                json.put("splitScreenList", splitScreenList);
                return RestResponse.success(json);
            }
            case MCU_ZJ: {
                McuZjConferenceContext conferenceContext = busiMcuZjConferenceService.buildTemplateConferenceContext(id);
                ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                JSONObject json = null;
                try {
                    json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                } catch (JsonProcessingException e) {
                }
                boolean isPresenter = false;
                boolean isMyConference = false;
                if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                    isPresenter = true;
                }
                if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                    isMyConference = true;
                }
                json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                json.put("isPresenter", isPresenter);
                json.put("isMyConference", isMyConference);
                json.remove("supervisorPassword");
                List<ModelBean> splitScreenList = new ArrayList<>();

                if (conferenceContext.isSingleView()) {
                    splitScreenList = conferenceContext.getSpeakerSplitScreenList();
                } else {
                    splitScreenList = conferenceContext.getGuestSplitScreenList();
                }
                for (int i = 0; i < splitScreenList.size(); i++) {
                    ModelBean modelBean = splitScreenList.get(i);
                    if (i == 0) {
                        modelBean.put("isDefault", true);
                    } else {
                        modelBean.put("isDefault", false);
                    }
                }
                json.put("splitScreenList", splitScreenList);
                return RestResponse.success(json);
            }
            case MCU_PLC: {
                McuPlcConferenceContext conferenceContext = busiMcuPlcConferenceService.buildTemplateConferenceContext(id);
                ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                JSONObject json = null;
                try {
                    json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                } catch (JsonProcessingException e) {
                }
                boolean isPresenter = false;
                boolean isMyConference = false;
                if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                    isPresenter = true;
                }
                if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                    isMyConference = true;
                }
                json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                json.put("isPresenter", isPresenter);
                json.put("isMyConference", isMyConference);
                json.remove("supervisorPassword");
                List<ModelBean> splitScreenList = conferenceContext.getSpeakerSplitScreenList();
                json.put("splitScreenList", splitScreenList);
                return RestResponse.success(json);
            }
            case MCU_KDC: {
                McuKdcConferenceContext conferenceContext = busiMcuKdcConferenceService.buildTemplateConferenceContext(id);
                ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                JSONObject json = null;
                try {
                    json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                } catch (JsonProcessingException e) {
                }
                boolean isPresenter = false;
                boolean isMyConference = false;
                if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                    isPresenter = true;
                }
                if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                    isMyConference = true;
                }
                json.put("isPresenter", isPresenter);
                json.put("isMyConference", isMyConference);
                json.remove("supervisorPassword");
                List<ModelBean> splitScreenList = conferenceContext.getSpeakerSplitScreenList();
                json.put("splitScreenList", splitScreenList);
                return RestResponse.success(json);
            }
            case SMC3: {
                Smc3ConferenceContext conferenceContext = busiSmc3ConferenceService.buildTemplateConferenceContext(id);
                JSONObject json = null;
                ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                try {
                    json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                } catch (Exception e) {
                }
                boolean isPresenter = false;
                boolean isMyConference = false;
                if (conferenceContext.getPresenter() != null && conferenceContext.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                    isPresenter = true;
                }
                if (conferenceContext.getCreateUserId() != null && conferenceContext.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                    isMyConference = true;
                }
                json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                json.put("isPresenter", isPresenter);
                json.put("isMyConference", isMyConference);
                json.remove("chairmanPassword");
                json.put("conferencePassword", conferenceContext.getGuestPassword());
                List<ModelBean> splitScreenList = new ArrayList<>();
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "一分屏");
                    modelBean.put("value", OneSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    splitScreenList.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "四分屏");
                    modelBean.put("value", FourSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    splitScreenList.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "九分屏");
                    modelBean.put("value", NineSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    splitScreenList.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "自动");
                    modelBean.put("value", AutomaticSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    splitScreenList.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "全等");
                    modelBean.put("value", AllEqualSplitScreen.LAYOUT);
                    modelBean.put("isDefault", true);
                    splitScreenList.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "一大N小");
                    modelBean.put("value", OnePlusNSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    splitScreenList.add(modelBean);
                }

                json.put("splitScreenList", splitScreenList);
                return RestResponse.success(json);
            }
        }
        return RestResponse.fail();
    }

    /**
     * 邀请终端
     */
    @PutMapping("/inviteTerminal/{conferenceId}")
    @Operation(summary = "邀请终端")
    public RestResponse inviteTerminal(@PathVariable String conferenceId, @RequestBody JSONObject jsonObject) {
        JSONArray terminalIdsJson = jsonObject.getJSONArray("terminalIds");
        List<Long> terminalIds = new ArrayList<>();
        if (terminalIdsJson != null) {
            for (Object terminalObj : terminalIdsJson) {
                try {
                    terminalIds.add(Long.valueOf(terminalObj.toString()));
                } catch (Exception e) {
                }
            }
        }
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.invite(conferenceId, terminalIds);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.invite(conferenceId, terminalIds);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.invite(conferenceId, terminalIds);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.invite(conferenceId, terminalIds);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.invite(conferenceId, terminalIds);
            }
        }

        return success();
    }

    /**
     * 查询终端会议列表
     */
    @GetMapping("/getConferenceList")
    @Operation(summary = "查询终端会议列表")
    public RestResponse getConferenceList() {
        Integer page = 0;
        Integer size = 20;

        List<ViewConferenceAppointment> cas = new ArrayList<>();
        PaginationData<ViewConferenceAppointment> pd = new PaginationData<>();

        // 预约会议
        Collection<BusiConferenceAppointment> valuesAppointment = AppointmentCache.getInstance().getAll().values();
        if (valuesAppointment != null && valuesAppointment.size() > 0) {
            for (BusiConferenceAppointment busiConferenceAppointmentCached : valuesAppointment) {
                if (busiConferenceAppointmentCached == null) {
                    continue;
                }
                BusiTemplateConference busiTemplateConference = null;

                McuType mcuType = null;
                busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                if (busiConferenceAppointmentCached instanceof BusiConferenceAppointment) {
                    BusiTemplateConference busiTemplateConferenceTemp = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                    if (busiTemplateConferenceTemp != null) {
                        busiTemplateConference = new BusiTemplateConference();
                        BeanUtils.copyBeanProp(busiTemplateConference, busiTemplateConferenceTemp);
                        mcuType = McuType.FME;
                    }
                } else if (busiConferenceAppointmentCached instanceof BusiMcuZjConferenceAppointment) {
                    BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                    if (busiMcuZjTemplateConference != null) {
                        busiTemplateConference = new BusiTemplateConference();
                        BeanUtils.copyBeanProp(busiTemplateConference, busiMcuZjTemplateConference);
                        mcuType = McuType.MCU_ZJ;
                    }
                } else if (busiConferenceAppointmentCached instanceof BusiMcuPlcConferenceAppointment) {
                    BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                    if (busiMcuPlcTemplateConference != null) {
                        busiTemplateConference = new BusiTemplateConference();
                        BeanUtils.copyBeanProp(busiTemplateConference, busiMcuPlcTemplateConference);
                        mcuType = McuType.MCU_PLC;
                    }
                } else if (busiConferenceAppointmentCached instanceof BusiMcuKdcConferenceAppointment) {
                    BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                    if (busiMcuKdcTemplateConference != null) {
                        busiTemplateConference = new BusiTemplateConference();
                        BeanUtils.copyBeanProp(busiTemplateConference, busiMcuKdcTemplateConference);
                        mcuType = McuType.MCU_KDC;
                    }
                } else if (busiConferenceAppointmentCached instanceof BusiMcuSmc3ConferenceAppointment) {
                    BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(busiConferenceAppointmentCached.getTemplateId());
                    if (busiMcuSmc3TemplateConference != null) {
                        busiTemplateConference = new BusiTemplateConference();
                        BeanUtils.copyBeanProp(busiTemplateConference, busiMcuSmc3TemplateConference);
                        mcuType = McuType.SMC3;
                    }
                }
                if (busiTemplateConference == null) {
                    continue;
                }
                ViewConferenceAppointment busiConferenceAppointment = new ViewConferenceAppointment();
                busiConferenceAppointment.setTemplateId(busiTemplateConference.getId());
                busiConferenceAppointment.setMcuType(mcuType.getCode());
                ModelBean modelBeanTemplateDetails = new ModelBean();
                ModelBean modelBeanParams = new ModelBean();
                busiConferenceAppointment.setStartTime(busiConferenceAppointmentCached.getStartTime());
                busiConferenceAppointment.setEndTime(busiConferenceAppointmentCached.getEndTime());
                boolean isStart = false;
                if (busiTemplateConference != null) {
                    modelBeanParams.put("conferencePassword", busiTemplateConference.getConferencePassword());
                    modelBeanParams.put("conferenceName", busiTemplateConference.getName());
                    modelBeanParams.put("conferenceNumber", busiTemplateConference.getConferenceNumber());
                    String contextKey = EncryptIdUtil.generateContextKey(busiTemplateConference.getId(), mcuType.getCode());
                    String conferenceId = EncryptIdUtil.generateConferenceId(contextKey);
                    modelBeanParams.put("conferenceId", conferenceId);
                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                    if (baseConferenceContext != null) {
                        isStart = true;
                        String conferenceRemoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
                        modelBeanParams.put("conferenceRemoteParty", conferenceRemoteParty);
                    }
                }
                modelBeanTemplateDetails.put("isStart", isStart);
                busiConferenceAppointment.setStatus(isStart ? 1 : 0);

                ArrayList<ModelBean> templateParticipantList = new ArrayList<>();
                modelBeanTemplateDetails.put("templateParticipants", templateParticipantList);
                modelBeanParams.put("templateDetails", modelBeanTemplateDetails);

                busiConferenceAppointment.setParams(modelBeanParams);
                cas.add(busiConferenceAppointment);
            }
        }

//        // 模板会议
//        Collection<BaseConferenceContext> valuesTemplate = AllConferenceContextCache.getInstance().values();
//        if (valuesTemplate != null) {
//            for (BaseConferenceContext conferenceContext : valuesTemplate) {
//                boolean isTemplateConference = true;
//                if (conferenceContext.isAppointment()) {
//                    if (conferenceContext.getConferenceAppointment() != null) {
//                        isTemplateConference = false;
//                    }
//                }
//                if (conferenceContext.getTerminalAttendeeMap() != null && isTemplateConference) {
//                    ViewConferenceAppointment busiConferenceAppointment = new ViewConferenceAppointment();
//                    busiConferenceAppointment.setTemplateId(conferenceContext.getTemplateConferenceId());
//                    busiConferenceAppointment.setMcuType(conferenceContext.getMcuType());
//                    ModelBean modelBeanTemplateDetails = new ModelBean();
//                    ModelBean modelBeanParams = new ModelBean();
//
//                    String startTimeStr = DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", conferenceContext.getStartTime());
//                    Date endTime = DateUtils.getDiffDate(conferenceContext.getStartTime(), conferenceContext.getDurationTime(), TimeUnit.MINUTES);
//                    String endTimeStr = DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", endTime);
//                    busiConferenceAppointment.setStartTime(startTimeStr);
//                    busiConferenceAppointment.setEndTime(endTimeStr);
//
//                    modelBeanParams.put("conferencePassword", conferenceContext.getConferencePassword());
//                    modelBeanParams.put("conferenceName", conferenceContext.getName());
//                    modelBeanParams.put("conferenceNumber", conferenceContext.getConferenceNumber());
//                    List<String> stringList = new ArrayList<>();
//                    if (conferenceContext.getIsAutoCreateStreamUrl() == 1) {
//                        stringList = conferenceContext.getStreamUrlList();
//                    } else {
//                        if (org.apache.commons.lang.StringUtils.isNotEmpty(conferenceContext.getStreamingUrl())) {
//                            stringList.add(conferenceContext.getStreamingUrl());
//                        }
//                    }
//                    modelBeanParams.put("liveUrlList", stringList);
//                    modelBeanTemplateDetails.put("isStart", true);
//                    busiConferenceAppointment.setStatus(1);
//
//                    ArrayList<ModelBean> templateParticipantList = new ArrayList<>();
//                    modelBeanTemplateDetails.put("templateParticipants", templateParticipantList);
//                    modelBeanParams.put("templateDetails", modelBeanTemplateDetails);
//
//                    busiConferenceAppointment.setParams(modelBeanParams);
//                    cas.add(busiConferenceAppointment);
//                }
//            }
//        }

        int fromIndex = page * size;
        int toIndex = fromIndex + size;
        if (toIndex >= cas.size()) {
            toIndex = cas.size();
        }

        pd.setRecords(cas.subList(fromIndex, toIndex));
        pd.setPage(page);
        pd.setSize(size);

        return RestResponse.success(pd);
    }

    @PutMapping("/updateDefaultViewConfigInfo")
    @Operation(summary = "修改布局")
    public RestResponse updateDefaultViewConfigInfo(@RequestBody JSONObject jsonObject) {
        String conferenceId = jsonObject.getString("conferenceId");
        Assert.isTrue(conferenceId != null, "会议ID不能为空");
        Assert.isTrue(jsonObject.containsKey("defaultViewLayout"), "默认视图布局不能为空");
        String defaultViewLayout = jsonObject.getString("defaultViewLayout");
        List<DefaultViewCellScreens> obj = new ArrayList<>();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        switch (mcuType) {
            case FME: {
                if (!canControlConference(baseConferenceContext)) {
                    return fail(1, "没有权限控制会议！");
                }

                switch (defaultViewLayout) {
                    case OneSplitScreen.LAYOUT:
                        obj.add(new DefaultViewCellScreens(1));
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case FourSplitScreen.LAYOUT:
                        for (int i = 0; i < INT4; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case NineSplitScreen.LAYOUT:
                        for (int i = 0; i < INT8; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    default:
                        break;
                }
                defaultAttendeeOperationPackageService.updateDefaultViewConfigInfo(conferenceId, jsonObject);
                break;
            }
            case MCU_ZJ: {
                if (!canControlConference(baseConferenceContext)) {
                    return fail(1, "没有权限控制会议！");
                }
                McuZjConferenceContext mcuZjConferenceContext = (McuZjConferenceContext) baseConferenceContext;

                switch (defaultViewLayout) {
                    case OneSplitScreen.LAYOUT:
                        obj.add(new DefaultViewCellScreens(1));
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case FourSplitScreen.LAYOUT:
                        for (int i = 0; i < 4; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case OnePlusFiveSplitScreen.LAYOUT:
                        for (int i = 0; i < 6; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case OnePlusSevenSplitScreen.LAYOUT:
                        for (int i = 0; i < 8; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case NineSplitScreen.LAYOUT:
                        for (int i = 0; i < 9; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case OnePlusNineSplitScreen.LAYOUT:
                        for (int i = 0; i < 10; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case SixteenSplitScreen.LAYOUT:
                        for (int i = 0; i < 16; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case TwentyFiveSplitScreen.LAYOUT:
                        for (int i = 0; i < 25; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    default:
                        break;
                }
                defaultAttendeeOperationPackageForMcuZjService.updateDefaultViewConfigInfo(conferenceId, jsonObject);
                // 移动端会控可广播但不广播时，有主会场时看主会场，无主会场时自动
                if (!mcuZjConferenceContext.isSingleView() && mcuZjConferenceContext.isSupportBroadcast()) {
                    if (mcuZjConferenceContext.getDefaultViewOperation().getDefaultViewIsBroadcast() == BroadcastStatus.NO.getValue()) {
                        mcuZjConferenceContext.setAttendeeOperationForGuest(mcuZjConferenceContext.getDefaultViewOperation());
                    }
                }
                break;
            }
            case MCU_PLC: {
                if (!canControlConference(baseConferenceContext)) {
                    return fail(1, "没有权限控制会议！");
                }

                switch (defaultViewLayout) {
                    case OneSplitScreen.LAYOUT:
                        obj.add(new DefaultViewCellScreens(1));
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case FourSplitScreen.LAYOUT:
                        for (int i = 0; i < 4; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case OnePlusFiveSplitScreen.LAYOUT:
                        for (int i = 0; i < 6; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case OnePlusSevenSplitScreen.LAYOUT:
                        for (int i = 0; i < 8; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case NineSplitScreen.LAYOUT:
                        for (int i = 0; i < 9; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case SixteenSplitScreen.LAYOUT:
                        for (int i = 0; i < 16; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    default:
                        break;
                }
                defaultAttendeeOperationPackageForMcuPlcService.updateDefaultViewConfigInfo(conferenceId, jsonObject);
                break;
            }
            case MCU_KDC: {
                if (!canControlConference(baseConferenceContext)) {
                    return fail(1, "没有权限控制会议！");
                }

                switch (defaultViewLayout) {
                    case OneSplitScreen.LAYOUT:
                        obj.add(new DefaultViewCellScreens(1));
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case FourSplitScreen.LAYOUT:
                        for (int i = 0; i < 4; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case OnePlusFiveSplitScreen.LAYOUT:
                        for (int i = 0; i < 6; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case OnePlusSevenSplitScreen.LAYOUT:
                        for (int i = 0; i < 8; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case NineSplitScreen.LAYOUT:
                        for (int i = 0; i < 9; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case SixteenSplitScreen.LAYOUT:
                        for (int i = 0; i < 16; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    default:
                        break;
                }
                defaultAttendeeOperationPackageForMcuKdcService.updateDefaultViewConfigInfo(conferenceId, jsonObject);
                break;
            }
            case SMC3: {
                if (!canControlConference(baseConferenceContext)) {
                    return fail(1, "没有权限控制会议！");
                }

                switch (defaultViewLayout) {
                    case OneSplitScreen.LAYOUT:
                        obj.add(new DefaultViewCellScreens(1));
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case FourSplitScreen.LAYOUT:
                        for (int i = 0; i < INT4; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    case NineSplitScreen.LAYOUT:
                        for (int i = 0; i < INT8; i++) {
                            obj.add(new DefaultViewCellScreens(i + 1));
                        }
                        jsonObject.put("defaultViewCellScreens", obj);
                        break;
                    default:
                        break;
                }

                Smc3ConferenceContext cc = Smc3ConferenceContextCache.getInstance().get(contextKey);
                com.paradisecloud.smc3.busi.operation.AttendeeOperation old = cc.getAttendeeOperation();

                cc.setLastAttendeeOperation(old);
                if (!(old instanceof ChangeMasterAttendeeOperation)) {
                    old.cancel();
                } else {
                    ((ChangeMasterAttendeeOperation) old).cancelChooseStatus();
                }
                com.paradisecloud.smc3.busi.operation.AttendeeOperation attendeeOperation = new com.paradisecloud.smc3.busi.DefaultAttendeeOperation(cc, jsonObject);
                cc.setAttendeeOperation(attendeeOperation);
                attendeeOperation.operate();
            }
        }

        return success();
    }

    /**
     * 新增会议预约记录
     */
    @PutMapping("addSchedule")
    @Operation(summary = "新增会议预约记录")
    public RestResponse addSchedule(@RequestBody JSONObject jsonObject) throws ParseException {
        MobileConferenceAppointmentRequest mobileConferenceAppointmentRequest = jsonObject.toJavaObject(MobileConferenceAppointmentRequest.class);
        String mcuTypeStr = "";
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = loginUser.getUser().getDeptId();
        McuTypeVo mcuTypeVo = busiAllMcuService.getDefaultMcuType(deptId);
        mcuTypeStr = mcuTypeVo.getCode();
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                {
                    synchronized (MobileConferenceAppointmentController.class) {
                        setParticipants(mobileConferenceAppointmentRequest);
                        BusiConferenceAppointment busiConferenceAppointment = initDeptParamTemplate();
                        if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
                            busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                        }
                        setRequestparams(mobileConferenceAppointmentRequest, busiConferenceAppointment);
                        createMobileConferenceParamTemplate(busiConferenceAppointment);
                        Boolean isMute = mobileConferenceAppointmentRequest.getIsMute();

                        if (mobileConferenceAppointmentRequest.getType() != null) {
                            if (mobileConferenceAppointmentRequest.getType() == 1) {
                                busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                                busiConferenceAppointment.setType(1);
                                busiConferenceAppointmentService.insertBusiConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                                BusiConferenceAppointment conferenceAppointment = busiConferenceAppointmentMapper.selectBusiConferenceAppointmentById(busiConferenceAppointment.getId());
                                ConferenceContext conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(conferenceAppointment.getTemplateId());
                                conferenceContext.setConferenceAppointment(conferenceAppointment);
                                return RestResponse.success(conferenceContext);
                            } else if (mobileConferenceAppointmentRequest.getType() == 2) {
                                BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
                                busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                                busiTemplateConference.setBusinessFieldType(100);
                                busiTemplateConference.setType(2);
                                checkType(busiTemplateConference);
                                busiConferenceAppointment.setStatus(2);
                                busiConferenceAppointment.setType(2);
                                busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                            } else {
                                throw new CustomException("类型错误");
                            }
                        } else {
                            if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
                                // 没用传type的，如果开始时间在60秒内，直接看作即使会议
                                Date startTime = DateUtil.convertDateByString(mobileConferenceAppointmentRequest.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                                if (startTime.getTime() - System.currentTimeMillis() < 60000) {
                                    BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
                                    busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                                    busiTemplateConference.setBusinessFieldType(100);
                                    busiTemplateConference.setType(2);
                                    checkType(busiTemplateConference);
                                    busiConferenceAppointment.setStatus(2);
                                    busiConferenceAppointment.setType(2);
                                    busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss",new Date()));
                                } else {
                                    busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                                    busiConferenceAppointment.setType(1);
                                    busiConferenceAppointmentService.insertBusiConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                                    BusiConferenceAppointment conferenceAppointment = busiConferenceAppointmentMapper.selectBusiConferenceAppointmentById(busiConferenceAppointment.getId());
                                    ConferenceContext conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(conferenceAppointment.getTemplateId());
                                    conferenceContext.setConferenceAppointment(conferenceAppointment);
                                    return RestResponse.success(conferenceContext);
                                }
                            } else {
                                BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
                                busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                                busiTemplateConference.setBusinessFieldType(100);
                                busiTemplateConference.setType(2);
                                checkType(busiTemplateConference);
                                busiConferenceAppointment.setStatus(2);
                                busiConferenceAppointment.setType(2);
                                busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                            }
                        }

                        if (busiConferenceAppointmentService.insertBusiConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId()) > 0) {
                            appointmentConferenceSyncExecutor.exec(busiConferenceAppointment);
                            executorService.schedule(()-> {
                                BusiConferenceAppointment conferenceAppointment = busiConferenceAppointmentMapper.selectBusiConferenceAppointmentById(busiConferenceAppointment.getId());
                                conferenceAppointment.setStatus(1);
                                busiConferenceAppointmentMapper.updateBusiConferenceAppointment(conferenceAppointment);
                            }, 10, TimeUnit.SECONDS);

                            return RestResponse.success(templateConferenceStartService.buildTemplateConferenceContext(busiConferenceAppointment.getTemplateId()));
                        }
                        return null;
                    }
                }
            }
            case MCU_ZJ: {
                synchronized (MobileConferenceAppointmentController.class) {
                    setParticipants(mobileConferenceAppointmentRequest);
                    BusiMcuZjConferenceAppointment busiConferenceAppointment = initDeptParamTemplateForZj();
                    if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
                        busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                    }
                    setRequestparams(mobileConferenceAppointmentRequest, busiConferenceAppointment);
                    createMobileConferenceParamTemplate(busiConferenceAppointment);
                    busiConferenceAppointment.getParams().put("createUserId", loginUser.getUser().getUserId());
                    busiConferenceAppointment.getParams().put("createUserName", loginUser.getUser().getUserName());
                    Boolean isMute = mobileConferenceAppointmentRequest.getIsMute();
                    Integer type = mobileConferenceAppointmentRequest.getType();

                    if (mobileConferenceAppointmentRequest.getType() != null) {
                        if (mobileConferenceAppointmentRequest.getType() == 1) {
                            busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                            busiConferenceAppointment.setType(1);
                            checkResource(busiConferenceAppointment);
                            busiMcuZjConferenceAppointmentService.insertBusiMcuZjConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                            BusiMcuZjConferenceAppointment conferenceAppointment = busiMcuZjConferenceAppointmentService.selectBusiMcuZjConferenceAppointmentById(busiConferenceAppointment.getId());
                            McuZjConferenceContext conferenceContext = busiMcuZjConferenceService.buildTemplateConferenceContext(conferenceAppointment.getTemplateId());
                            conferenceContext.setConferenceAppointment(conferenceAppointment);
                            return RestResponse.success(conferenceContext);
                        } else if (mobileConferenceAppointmentRequest.getType() == 2) {
                            BusiMcuZjTemplateConference busiTemplateConference = new BusiMcuZjTemplateConference();
                            busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                            busiTemplateConference.setBusinessFieldType(100);
                            busiTemplateConference.setType(2);
                            checkType(busiTemplateConference);
                            busiConferenceAppointment.setStatus(2);
                            busiConferenceAppointment.setType(2);
                            busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                        } else {
                            throw new CustomException("类型错误");
                        }
                    } else {
                        if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
                            // 没用传type的，如果开始时间在60秒内，直接看作即使会议
                            Date startTime = DateUtil.convertDateByString(mobileConferenceAppointmentRequest.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                            if (startTime.getTime() - System.currentTimeMillis() < 60000) {
                                type = 2;
                                BusiMcuZjTemplateConference busiTemplateConference = new BusiMcuZjTemplateConference();
                                busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                                busiTemplateConference.setBusinessFieldType(100);
                                busiTemplateConference.setType(2);
                                checkType(busiTemplateConference);
                                busiConferenceAppointment.setStatus(2);
                                busiConferenceAppointment.setType(2);
                                busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                            } else {
                                type = 1;
                                busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                                busiConferenceAppointment.setType(1);
                                checkResource(busiConferenceAppointment);
                                busiMcuZjConferenceAppointmentService.insertBusiMcuZjConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                                BusiMcuZjConferenceAppointment conferenceAppointment = busiMcuZjConferenceAppointmentService.selectBusiMcuZjConferenceAppointmentById(busiConferenceAppointment.getId());
                                McuZjConferenceContext conferenceContext = busiMcuZjConferenceService.buildTemplateConferenceContext(conferenceAppointment.getTemplateId());
                                conferenceContext.setConferenceAppointment(conferenceAppointment);
                                return RestResponse.success(conferenceContext);
                            }
                        } else {
                            type = 2;
                            BusiMcuZjTemplateConference busiTemplateConference = new BusiMcuZjTemplateConference();
                            busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                            busiTemplateConference.setBusinessFieldType(100);
                            busiTemplateConference.setType(2);
                            checkType(busiTemplateConference);
                            busiConferenceAppointment.setStatus(2);
                            busiConferenceAppointment.setType(2);
                            busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                        }
                    }

                    checkResource(busiConferenceAppointment);
                    Map<String, Object> resultMap = busiMcuZjConferenceAppointmentService.insertBusiMcuZjConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                    Integer success = 0;
                    try {
                        success = (Integer) resultMap.get("rows");
                    } catch (Exception e) {
                    }
                    if (success > 0) {
                        Long conferenceNumber = null;
                        Long templateId = null;
                        String tenantId = "";
                        try {
                            conferenceNumber = (Long) resultMap.get("conferenceNumber");
                        } catch (Exception e) {

                        }
                        try {
                            templateId = (Long) resultMap.get("templateId");
                        } catch (Exception e) {

                        }
                        try {
                            tenantId = (String) resultMap.get("tenantId");
                        } catch (Exception e) {

                        }
                        if (type == 2) {
                            if (templateId != null) {
                                try {
                                    String contextKey = busiMcuZjConferenceService.startConference(templateId);
                                    if (contextKey != null) {
                                        if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                            busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                            busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                            busiMcuZjConferenceAppointmentService.updateBusiMcuZjConferenceAppointment(busiConferenceAppointment, false);
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                        return RestResponse.success(busiMcuZjConferenceService.buildTemplateConferenceContext(templateId));
                    }
                    return RestResponse.fail();
                }
            }
            case MCU_PLC: {
                synchronized (MobileConferenceAppointmentController.class) {
                    setParticipants(mobileConferenceAppointmentRequest);
                    BusiMcuPlcConferenceAppointment busiConferenceAppointment = initDeptParamTemplateForPlc();
                    if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
                        busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                    }
                    setRequestparams(mobileConferenceAppointmentRequest, busiConferenceAppointment);
                    createMobileConferenceParamTemplate(busiConferenceAppointment);
                    busiConferenceAppointment.getParams().put("createUserId", loginUser.getUser().getUserId());
                    busiConferenceAppointment.getParams().put("createUserName", loginUser.getUser().getUserName());
                    Boolean isMute = mobileConferenceAppointmentRequest.getIsMute();
                    Integer type = mobileConferenceAppointmentRequest.getType();

                    if (mobileConferenceAppointmentRequest.getType() != null) {
                        if (mobileConferenceAppointmentRequest.getType() == 1) {
                            busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                            busiConferenceAppointment.setType(1);
//                        checkResource(busiConferenceAppointment);
                            busiMcuPlcConferenceAppointmentService.insertBusiMcuPlcConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                            BusiMcuPlcConferenceAppointment conferenceAppointment = busiMcuPlcConferenceAppointmentService.selectBusiMcuPlcConferenceAppointmentById(busiConferenceAppointment.getId());
                            McuPlcConferenceContext conferenceContext = busiMcuPlcConferenceService.buildTemplateConferenceContext(conferenceAppointment.getTemplateId());
                            conferenceContext.setConferenceAppointment(conferenceAppointment);
                            return RestResponse.success(conferenceContext);
                        } else if (mobileConferenceAppointmentRequest.getType() == 2) {
                            BusiMcuPlcTemplateConference busiTemplateConference = new BusiMcuPlcTemplateConference();
                            busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                            busiTemplateConference.setBusinessFieldType(100);
                            busiTemplateConference.setType(2);
//                        checkType(busiTemplateConference);
                            busiConferenceAppointment.setStatus(2);
                            busiConferenceAppointment.setType(2);
                            busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                        } else {
                            throw new CustomException("类型错误");
                        }
                    } else {
                        if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
                            // 没用传type的，如果开始时间在60秒内，直接看作即使会议
                            Date startTime = DateUtil.convertDateByString(mobileConferenceAppointmentRequest.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                            if (startTime.getTime() - System.currentTimeMillis() < 60000) {
                                type = 2;
                                BusiMcuPlcTemplateConference busiTemplateConference = new BusiMcuPlcTemplateConference();
                                busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                                busiTemplateConference.setBusinessFieldType(100);
                                busiTemplateConference.setType(2);
//                        checkType(busiTemplateConference);
                                busiConferenceAppointment.setStatus(2);
                                busiConferenceAppointment.setType(2);
                                busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                            } else {
                                type = 1;
                                busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                                busiConferenceAppointment.setType(1);
//                        checkResource(busiConferenceAppointment);
                                busiMcuPlcConferenceAppointmentService.insertBusiMcuPlcConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                                BusiMcuPlcConferenceAppointment conferenceAppointment = busiMcuPlcConferenceAppointmentService.selectBusiMcuPlcConferenceAppointmentById(busiConferenceAppointment.getId());
                                McuPlcConferenceContext conferenceContext = busiMcuPlcConferenceService.buildTemplateConferenceContext(conferenceAppointment.getTemplateId());
                                conferenceContext.setConferenceAppointment(conferenceAppointment);
                                return RestResponse.success(conferenceContext);
                            }
                        } else {
                            type = 2;
                            BusiMcuPlcTemplateConference busiTemplateConference = new BusiMcuPlcTemplateConference();
                            busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                            busiTemplateConference.setBusinessFieldType(100);
                            busiTemplateConference.setType(2);
//                        checkType(busiTemplateConference);
                            busiConferenceAppointment.setStatus(2);
                            busiConferenceAppointment.setType(2);
                            busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                        }
                    }

//                checkResource(busiConferenceAppointment);
                    Map<String, Object> resultMap = busiMcuPlcConferenceAppointmentService.insertBusiMcuPlcConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                    Integer success = 0;
                    try {
                        success = (Integer) resultMap.get("rows");
                    } catch (Exception e) {
                    }
                    if (success > 0) {
                        Long conferenceNumber = null;
                        Long templateId = null;
                        String tenantId = "";
                        try {
                            conferenceNumber = (Long) resultMap.get("conferenceNumber");
                        } catch (Exception e) {

                        }
                        try {
                            templateId = (Long) resultMap.get("templateId");
                        } catch (Exception e) {

                        }
                        try {
                            tenantId = (String) resultMap.get("tenantId");
                        } catch (Exception e) {

                        }
                        if (type == 2) {
                            if (templateId != null) {
                                try {
                                    String contextKey = busiMcuPlcConferenceService.startConference(templateId);
                                    if (contextKey != null) {
                                        if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                            busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                            busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                            busiMcuPlcConferenceAppointmentService.updateBusiMcuPlcConferenceAppointment(busiConferenceAppointment, false);
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                        return RestResponse.success(busiMcuPlcConferenceService.buildTemplateConferenceContext(templateId));
                    }
                    return RestResponse.fail();
                }
            }
            case MCU_KDC: {
                synchronized (MobileConferenceAppointmentController.class) {
                    setParticipants(mobileConferenceAppointmentRequest);
                    BusiMcuKdcConferenceAppointment busiConferenceAppointment = initDeptParamTemplateForKdc();
                    if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
                        busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                    }
                    setRequestparams(mobileConferenceAppointmentRequest, busiConferenceAppointment);
                    createMobileConferenceParamTemplate(busiConferenceAppointment);
                    busiConferenceAppointment.getParams().put("createUserId", loginUser.getUser().getUserId());
                    busiConferenceAppointment.getParams().put("createUserName", loginUser.getUser().getUserName());
                    Boolean isMute = mobileConferenceAppointmentRequest.getIsMute();
                    Integer type = mobileConferenceAppointmentRequest.getType();

                    if (mobileConferenceAppointmentRequest.getType() != null) {
                        if (mobileConferenceAppointmentRequest.getType() == 1) {
                            busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                            busiConferenceAppointment.setType(1);
                            checkResource(busiConferenceAppointment);
                            busiMcuKdcConferenceAppointmentService.insertBusiMcuKdcConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                            BusiMcuKdcConferenceAppointment conferenceAppointment = busiMcuKdcConferenceAppointmentService.selectBusiMcuKdcConferenceAppointmentById(busiConferenceAppointment.getId());
                            McuKdcConferenceContext conferenceContext = busiMcuKdcConferenceService.buildTemplateConferenceContext(conferenceAppointment.getTemplateId());
                            conferenceContext.setConferenceAppointment(conferenceAppointment);
                            return RestResponse.success(conferenceContext);
                        } else if (mobileConferenceAppointmentRequest.getType() == 2) {
                            BusiMcuKdcTemplateConference busiTemplateConference = new BusiMcuKdcTemplateConference();
                            busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                            busiTemplateConference.setBusinessFieldType(100);
                            busiTemplateConference.setType(2);
//                            checkType(busiTemplateConference);
                            busiConferenceAppointment.setStatus(2);
                            busiConferenceAppointment.setType(2);
                            busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                        } else {
                            throw new CustomException("类型错误");
                        }
                    } else {
                        if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
                            // 没用传type的，如果开始时间在60秒内，直接看作即使会议
                            Date startTime = DateUtil.convertDateByString(mobileConferenceAppointmentRequest.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                            if (startTime.getTime() - System.currentTimeMillis() < 60000) {
                                type = 2;
                                BusiMcuKdcTemplateConference busiTemplateConference = new BusiMcuKdcTemplateConference();
                                busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                                busiTemplateConference.setBusinessFieldType(100);
                                busiTemplateConference.setType(2);
//                                checkType(busiTemplateConference);
                                busiConferenceAppointment.setStatus(2);
                                busiConferenceAppointment.setType(2);
                                busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                            } else {
                                type = 1;
                                busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                                busiConferenceAppointment.setType(1);
                                checkResource(busiConferenceAppointment);
                                busiMcuKdcConferenceAppointmentService.insertBusiMcuKdcConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                                BusiMcuKdcConferenceAppointment conferenceAppointment = busiMcuKdcConferenceAppointmentService.selectBusiMcuKdcConferenceAppointmentById(busiConferenceAppointment.getId());
                                McuKdcConferenceContext conferenceContext = busiMcuKdcConferenceService.buildTemplateConferenceContext(conferenceAppointment.getTemplateId());
                                conferenceContext.setConferenceAppointment(conferenceAppointment);
                                return RestResponse.success(conferenceContext);
                            }
                        } else {
                            type = 2;
                            BusiMcuKdcTemplateConference busiTemplateConference = new BusiMcuKdcTemplateConference();
                            busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                            busiTemplateConference.setBusinessFieldType(100);
                            busiTemplateConference.setType(2);
//                            checkType(busiTemplateConference);
                            busiConferenceAppointment.setStatus(2);
                            busiConferenceAppointment.setType(2);
                            busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                        }
                    }

                    checkResource(busiConferenceAppointment);
                    Map<String, Object> resultMap = busiMcuKdcConferenceAppointmentService.insertBusiMcuKdcConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                    Integer success = 0;
                    try {
                        success = (Integer) resultMap.get("rows");
                    } catch (Exception e) {
                    }
                    if (success > 0) {
                        Long conferenceNumber = null;
                        Long templateId = null;
                        String tenantId = "";
                        try {
                            conferenceNumber = (Long) resultMap.get("conferenceNumber");
                        } catch (Exception e) {

                        }
                        try {
                            templateId = (Long) resultMap.get("templateId");
                        } catch (Exception e) {

                        }
                        try {
                            tenantId = (String) resultMap.get("tenantId");
                        } catch (Exception e) {

                        }
                        if (type == 2) {
                            if (templateId != null) {
                                try {
                                    String contextKey = busiMcuKdcConferenceService.startConference(templateId);
                                    if (contextKey != null) {
                                        if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                            busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                            busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                            busiMcuKdcConferenceAppointmentService.updateBusiMcuKdcConferenceAppointment(busiConferenceAppointment, false);
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                        return RestResponse.success(busiMcuKdcConferenceService.buildTemplateConferenceContext(templateId));
                    }
                    return RestResponse.fail();
                }
            }
            case SMC3: {
                {
                    synchronized (MobileConferenceAppointmentController.class) {
                        setParticipants(mobileConferenceAppointmentRequest);
                        BusiMcuSmc3ConferenceAppointment busiConferenceAppointment = initDeptParamTemplateForSmc3();
                        if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
                            busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                        }
                        setRequestparams(mobileConferenceAppointmentRequest, busiConferenceAppointment);
                        createMobileConferenceParamTemplate(busiConferenceAppointment);
                        Boolean isMute = mobileConferenceAppointmentRequest.getIsMute();
                        Integer type = mobileConferenceAppointmentRequest.getType();

                        if (mobileConferenceAppointmentRequest.getType() != null) {
                            if (mobileConferenceAppointmentRequest.getType() == 1) {
                                busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                                busiConferenceAppointment.setType(1);
                                busiMcuSmc3ConferenceAppointmentService.insertBusiMcuSmc3ConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                                BusiMcuSmc3ConferenceAppointment conferenceAppointment = busiMcuSmc3ConferenceAppointmentMapper.selectBusiMcuSmc3ConferenceAppointmentById(busiConferenceAppointment.getId());
                                Smc3ConferenceContext conferenceContext = busiSmc3ConferenceService.buildTemplateConferenceContext(conferenceAppointment.getTemplateId());
                                conferenceContext.setConferenceAppointment(conferenceAppointment);
                                return RestResponse.success(conferenceContext);
                            } else if (mobileConferenceAppointmentRequest.getType() == 2) {
                                BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
                                busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                                busiTemplateConference.setBusinessFieldType(100);
                                busiTemplateConference.setType(2);
                                checkType(busiTemplateConference);
                                busiConferenceAppointment.setStatus(2);
                                busiConferenceAppointment.setType(2);
                                busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                            } else {
                                throw new CustomException("类型错误");
                            }
                        } else {
                            if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
                                // 没用传type的，如果开始时间在60秒内，直接看作即使会议
                                Date startTime = DateUtil.convertDateByString(mobileConferenceAppointmentRequest.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                                if (startTime.getTime() - System.currentTimeMillis() < 60000) {
                                    BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
                                    busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                                    busiTemplateConference.setBusinessFieldType(100);
                                    busiTemplateConference.setType(2);
                                    checkType(busiTemplateConference);
                                    busiConferenceAppointment.setStatus(2);
                                    busiConferenceAppointment.setType(2);
                                    busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss",new Date()));
                                } else {
                                    busiConferenceAppointment.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                                    busiConferenceAppointment.setType(1);
                                    busiMcuSmc3ConferenceAppointmentService.insertBusiMcuSmc3ConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                                    BusiMcuSmc3ConferenceAppointment conferenceAppointment = busiMcuSmc3ConferenceAppointmentMapper.selectBusiMcuSmc3ConferenceAppointmentById(busiConferenceAppointment.getId());
                                    Smc3ConferenceContext conferenceContext = busiSmc3ConferenceService.buildTemplateConferenceContext(conferenceAppointment.getTemplateId());
                                    conferenceContext.setConferenceAppointment(conferenceAppointment);
                                    return RestResponse.success(conferenceContext);
                                }
                            } else {
                                BusiMcuSmc3TemplateConference busiTemplateConference = new BusiMcuSmc3TemplateConference();
                                busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                                busiTemplateConference.setBusinessFieldType(100);
                                busiTemplateConference.setType(2);
                                checkType(busiTemplateConference);
                                busiConferenceAppointment.setStatus(2);
                                busiConferenceAppointment.setType(2);
                                busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                            }
                        }

                        Map<String, Object> resultMap = busiMcuSmc3ConferenceAppointmentService.insertBusiMcuSmc3ConferenceAppointmentIsMute(busiConferenceAppointment, isMute, loginUser.getUser().getUserId());
                        Integer success = 0;
                        try {
                            success = (Integer) resultMap.get("rows");
                        } catch (Exception e) {
                        }
                        if (success > 0) {
                            Long conferenceNumber = null;
                            Long templateId = null;
                            String tenantId = "";
                            try {
                                conferenceNumber = (Long) resultMap.get("conferenceNumber");
                            } catch (Exception e) {

                            }
                            try {
                                templateId = (Long) resultMap.get("templateId");
                            } catch (Exception e) {

                            }
                            try {
                                tenantId = (String) resultMap.get("tenantId");
                            } catch (Exception e) {

                            }
                            if (type == 2) {
                                if (templateId != null) {
                                    try {
                                        String contextKey = busiSmc3ConferenceService.startConference(templateId);
                                        if (contextKey != null) {
                                            if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                                busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                                busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                                busiMcuSmc3ConferenceAppointmentService.updateBusiMcuSmc3ConferenceAppointment(busiConferenceAppointment, false);
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                            return RestResponse.success(busiSmc3ConferenceService.buildTemplateConferenceContext(templateId));
                        }
                        return RestResponse.fail();
                    }
                }
            }
        }

        return RestResponse.fail();
    }


    @GetMapping("/endConference")
    @Operation(summary = "结束会议")
    public RestResponse endConference(@RequestParam("conferenceId") String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        Map<String, Object> recordFileInfo = new HashMap<>();
        switch (mcuType) {
            case FME: {
                if (!canControlConference(baseConferenceContext)) {
                    return fail(1, "没有权限控制会议！");
                }
                ConferenceContext conferenceContext = (ConferenceContext) baseConferenceContext;
                if (conferenceContext.isRecorded()) {
                    String coSpaceId = FmeDataCache.getCoSpaceByConferenceNumber(conferenceContext.getDeptId(), conferenceContext.getConferenceNumber()).getId();
                    recordFileInfo = busiRecordsService.getRecordFileInfo(conferenceContext.getDeptId(), coSpaceId, contextKey);
                }
                busiConferenceService.endConference(conferenceId, ConferenceEndType.COMMON.getValue(), EndReasonsType.ADMINISTRATOR_HANGS_UP);

                try {
                    ConferenceTakeSnapshotPdfTask conferenceTakeSnapshotPdfTask = new ConferenceTakeSnapshotPdfTask(conferenceContext.getId(), 5000,conferenceContext);
                    conferenceTakeSnapshotPdfTask.start();
                } catch (Exception e) {
                    logger.info("纪要生成 error" + e.getMessage());
                }
                break;
            }
            case MCU_ZJ: {
                McuZjConferenceContext mcuZjConferenceContext = (McuZjConferenceContext) baseConferenceContext;
                if (mcuZjConferenceContext != null) {
                    if (!canControlConference(mcuZjConferenceContext)) {
                        return fail(1, "没有权限控制会议！");
                    }
                    busiMcuZjConferenceService.endConference(conferenceId);
                } else {
                    BusiMcuZjTemplateConference busiMcuZjTemplateConferenceCon = new BusiMcuZjTemplateConference();
                    busiMcuZjTemplateConferenceCon.setId(id);
                    List<BusiMcuZjTemplateConference> busiMcuZjTemplateConferenceList = busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceList(busiMcuZjTemplateConferenceCon);
                    if (busiMcuZjTemplateConferenceList != null && busiMcuZjTemplateConferenceList.size() > 0) {
                        BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceList.get(0);
                        if (!canControlConference(busiMcuZjTemplateConference.getCreateUserId(), busiMcuZjTemplateConference.getPresenter())) {
                            return fail(1, "没有权限控制会议！");
                        }
                        List<BusiMcuZjConferenceAppointment> busiMcuZjConferenceAppointmentList = busiMcuZjConferenceAppointmentService.selectBusiMcuZjConferenceAppointmentByTemplateId(busiMcuZjTemplateConference.getId());
                        if (busiMcuZjConferenceAppointmentList != null && busiMcuZjConferenceAppointmentList.size() > 0) {
                            BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = busiMcuZjConferenceAppointmentList.get(0);
                            busiMcuZjConferenceAppointmentService.deleteBusiMcuZjConferenceAppointmentById(busiMcuZjConferenceAppointment.getId());
                        }
                        return success(recordFileInfo);
                    }
                }
                break;
            }
            case MCU_PLC: {
                McuPlcConferenceContext mcuPlcConferenceContext = (McuPlcConferenceContext) baseConferenceContext;
                if (mcuPlcConferenceContext != null) {
                    if (!canControlConference(mcuPlcConferenceContext)) {
                        return fail(1, "没有权限控制会议！");
                    }
                    busiMcuPlcConferenceService.endConference(conferenceId);
                    return success(recordFileInfo);
                } else {
                    BusiMcuPlcTemplateConference busiMcuPlcTemplateConferenceCon = new BusiMcuPlcTemplateConference();
                    busiMcuPlcTemplateConferenceCon.setId(id);
                    List<BusiMcuPlcTemplateConference> busiMcuPlcTemplateConferenceList = busiMcuPlcTemplateConferenceService.selectAllBusiMcuPlcTemplateConferenceList(busiMcuPlcTemplateConferenceCon);
                    if (busiMcuPlcTemplateConferenceList != null && busiMcuPlcTemplateConferenceList.size() > 0) {
                        BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceList.get(0);
                        if (!canControlConference(busiMcuPlcTemplateConference.getCreateUserId(), busiMcuPlcTemplateConference.getPresenter())) {
                            return fail(1, "没有权限控制会议！");
                        }
                        List<BusiMcuPlcConferenceAppointment> busiMcuPlcConferenceAppointmentList = busiMcuPlcConferenceAppointmentService.selectBusiMcuPlcConferenceAppointmentByTemplateId(busiMcuPlcTemplateConference.getId());
                        if (busiMcuPlcConferenceAppointmentList != null && busiMcuPlcConferenceAppointmentList.size() > 0) {
                            BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment = busiMcuPlcConferenceAppointmentList.get(0);
                            busiMcuPlcConferenceAppointmentService.deleteBusiMcuPlcConferenceAppointmentById(busiMcuPlcConferenceAppointment.getId());
                        }
                        return success(recordFileInfo);
                    }
                }
                break;
            }
            case MCU_KDC: {
                McuKdcConferenceContext mcuKdcConferenceContext = (McuKdcConferenceContext) baseConferenceContext;
                if (mcuKdcConferenceContext != null) {
                    if (!canControlConference(mcuKdcConferenceContext)) {
                        return fail(1, "没有权限控制会议！");
                    }
                    busiMcuKdcConferenceService.endConference(conferenceId);
                    return success(recordFileInfo);
                } else {
                    BusiMcuKdcTemplateConference busiMcuKdcTemplateConferenceCon = new BusiMcuKdcTemplateConference();
                    busiMcuKdcTemplateConferenceCon.setId(id);
                    List<BusiMcuKdcTemplateConference> busiMcuKdcTemplateConferenceList = busiMcuKdcTemplateConferenceService.selectAllBusiMcuKdcTemplateConferenceList(busiMcuKdcTemplateConferenceCon);
                    if (busiMcuKdcTemplateConferenceList != null && busiMcuKdcTemplateConferenceList.size() > 0) {
                        BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceList.get(0);
                        if (!canControlConference(busiMcuKdcTemplateConference.getCreateUserId(), busiMcuKdcTemplateConference.getPresenter())) {
                            return fail(1, "没有权限控制会议！");
                        }
                        List<BusiMcuKdcConferenceAppointment> busiMcuKdcConferenceAppointmentList = busiMcuKdcConferenceAppointmentService.selectBusiMcuKdcConferenceAppointmentByTemplateId(busiMcuKdcTemplateConference.getId());
                        if (busiMcuKdcConferenceAppointmentList != null && busiMcuKdcConferenceAppointmentList.size() > 0) {
                            BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment = busiMcuKdcConferenceAppointmentList.get(0);
                            busiMcuKdcConferenceAppointmentService.deleteBusiMcuKdcConferenceAppointmentById(busiMcuKdcConferenceAppointment.getId());
                        }
                        return success(recordFileInfo);
                    }
                }
                break;
            }
            case SMC3: {
                Smc3ConferenceContext smc3ConferenceContext = (Smc3ConferenceContext) baseConferenceContext;
                if (smc3ConferenceContext != null) {
                    if (!canControlConference(smc3ConferenceContext)) {
                        return fail(1, "没有权限控制会议！");
                    }
                    busiSmc3ConferenceService.endConference(conferenceId);
                } else {
                    BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConferenceCon = new BusiMcuSmc3TemplateConference();
                    busiMcuSmc3TemplateConferenceCon.setId(id);
                    List<BusiMcuSmc3TemplateConference> busiMcuSmc3TemplateConferenceList = busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceList(busiMcuSmc3TemplateConferenceCon);
                    if (busiMcuSmc3TemplateConferenceList != null && busiMcuSmc3TemplateConferenceList.size() > 0) {
                        BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceList.get(0);
                        Long presenter = null;
                        if (busiMcuSmc3TemplateConference.getPresenter() != null) {
                            presenter = busiMcuSmc3TemplateConference.getPresenter().longValue();
                        }
                        if (!canControlConference(busiMcuSmc3TemplateConference.getCreateUserId(), presenter)) {
                            return fail(1, "没有权限控制会议！");
                        }
                        List<BusiMcuSmc3ConferenceAppointment> busiMcuSmc3ConferenceAppointmentList = busiMcuSmc3ConferenceAppointmentService.selectBusiMcuSmc3ConferenceAppointmentByTemplateId(busiMcuSmc3TemplateConference.getId());
                        if (busiMcuSmc3ConferenceAppointmentList != null && busiMcuSmc3ConferenceAppointmentList.size() > 0) {
                            BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment = busiMcuSmc3ConferenceAppointmentList.get(0);
                            busiMcuSmc3ConferenceAppointmentService.deleteBusiMcuSmc3ConferenceAppointmentById(busiMcuSmc3ConferenceAppointment.getId());
                        }
                        return success(recordFileInfo);
                    }
                }
                break;
            }
        }

        return success(recordFileInfo);
    }

    private void setParticipants(MobileConferenceAppointmentRequest mobileConferenceAppointmentRequest) {
        List<BusiTemplateParticipant> templateParticipants = new ArrayList<>();
        List<Long> terminalIds = mobileConferenceAppointmentRequest.getTerminalIds();
        if (CollectionUtils.isNotEmpty(terminalIds)) {
            for (Long id : terminalIds) {
                covert(templateParticipants, id);
            }
        }
        mobileConferenceAppointmentRequest.setTemplateParticipants(templateParticipants);
    }

    private void covert(List<BusiTemplateParticipant> templateParticipants, Long id) {
        BusiTerminal busiTerminal = busiTerminalService.selectBusiTerminalById(id);
        BusiTemplateParticipant busiTemplateParticipant = new BusiTemplateParticipant();
        busiTemplateParticipant.setId(busiTerminal.getId());
        busiTemplateParticipant.setTerminalId(busiTerminal.getId());
        busiTemplateParticipant.setAttendType(busiTerminal.getAttendType());
        busiTemplateParticipant.setBusinessProperties(busiTerminal.getParams());
        busiTemplateParticipant.setCreateBy(busiTerminal.getCreateBy());
        busiTemplateParticipant.setCreateTime(busiTerminal.getCreateTime());
        busiTemplateParticipant.setWeight(1);
        templateParticipants.add(busiTemplateParticipant);
    }

    private void setRequestparams(MobileConferenceAppointmentRequest mobileConferenceAppointmentRequest, BusiConferenceAppointment busiConferenceAppointment) throws ParseException {
        if (mobileConferenceAppointmentRequest != null) {
            Map<String, Object> params = new HashMap<>(10);
            if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getConferenceName())) {
                params.put("conferenceName", mobileConferenceAppointmentRequest.getConferenceName());
            }
            if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getEndTime())) {
                params.put("endTime", mobileConferenceAppointmentRequest.getEndTime());
                busiConferenceAppointment.setEndTime(mobileConferenceAppointmentRequest.getEndTime());
            }

            if (mobileConferenceAppointmentRequest.getDurationOfMinutes() > 0) {
                if (!org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getEndTime())) {
                    Date sdate;
                    if (Objects.isNull(mobileConferenceAppointmentRequest.getStartTime())) {
                        sdate = org.apache.commons.lang3.time.DateUtils.parseDate(busiConferenceAppointment.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                    } else {
                        sdate = org.apache.commons.lang3.time.DateUtils.parseDate(mobileConferenceAppointmentRequest.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                    }
                    busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(org.apache.commons.lang3.time.DateUtils.addMinutes(sdate, mobileConferenceAppointmentRequest.getDurationOfMinutes()), null));
                } else {
                    try {
                        Date date = org.apache.commons.lang3.time.DateUtils.parseDate(mobileConferenceAppointmentRequest.getEndTime(), "yyyy-MM-dd HH:mm:ss");
                        busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(org.apache.commons.lang3.time.DateUtils.addMinutes(date, mobileConferenceAppointmentRequest.getDurationOfMinutes()), null));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
            if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getConferencePassword())) {
                params.put("conferencePassword", mobileConferenceAppointmentRequest.getConferencePassword());
            }
            if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getStreamUrl())) {
                params.put("streamingEnabled", 1);
                params.put("streamUrl", mobileConferenceAppointmentRequest.getStreamUrl());
            }
            if (mobileConferenceAppointmentRequest.getStreamingEnabled() != null) {
                params.put("streamingEnabled", mobileConferenceAppointmentRequest.getStreamingEnabled());
            }
            if (mobileConferenceAppointmentRequest.getRecordingEnabled() != null) {
                params.put("recordingEnabled", mobileConferenceAppointmentRequest.getRecordingEnabled());
            }
            if (org.springframework.util.StringUtils.hasText(mobileConferenceAppointmentRequest.getDefaultViewLayout())) {
                params.put("defaultViewLayout", mobileConferenceAppointmentRequest.getDefaultViewLayout());
            }
            if (mobileConferenceAppointmentRequest.getIsAutoCall() != null) {
                params.put("isAutoCall", mobileConferenceAppointmentRequest.getIsAutoCall());
            }
            params.put(DEFAULT_VIEW_IS_DISPLAY_SELF, mobileConferenceAppointmentRequest.getDefaultViewIsDisplaySelf() == 0 ? -1 : mobileConferenceAppointmentRequest.getDefaultViewIsDisplaySelf());
            params.put("defaultViewIsBroadcast", mobileConferenceAppointmentRequest.getDefaultViewIsBroadcast());
            params.put("defaultViewIsFill", mobileConferenceAppointmentRequest.getDefaultViewIsFill());
            if (!org.springframework.util.CollectionUtils.isEmpty(mobileConferenceAppointmentRequest.getTemplateParticipants())) {
                params.put("templateParticipants", mobileConferenceAppointmentRequest.getTemplateParticipants());
            }
            if (!Objects.isNull(mobileConferenceAppointmentRequest.getMasterTerminalId())) {
                params.put("masterTerminalId", mobileConferenceAppointmentRequest.getMasterTerminalId());
            }
            if (!Objects.isNull(mobileConferenceAppointmentRequest.getConferenceNumber())) {
                params.put("conferenceNumber", mobileConferenceAppointmentRequest.getConferenceNumber());
            }
            params.put("isAutoCreateStreamUrl", 1);
            params.put("conferencePassword", mobileConferenceAppointmentRequest.getConferencePassword());
            params.put("presenter", mobileConferenceAppointmentRequest.getPresenter());
            if (mobileConferenceAppointmentRequest.getBusinessProperties() != null) {
                params.put("businessProperties", mobileConferenceAppointmentRequest.getBusinessProperties());
            }
            busiConferenceAppointment.setParams(params);
        }
    }

    private void checkResource(BusiMcuKdcConferenceAppointment busiConferenceAppointment) {
        McuKdcBridge mcuKdcBridge = McuKdcBridgeCache.getInstance().getAvailableMcuKdcBridgesByDept(busiConferenceAppointment.getDeptId()).getMasterMcuKdcBridge();
        if (mcuKdcBridge != null) {
            if (mcuKdcBridge.getUsedResourceCount() >= mcuKdcBridge.getSystemResourceCount()) {
                if (busiConferenceAppointment.getType() == 2) {
                    throw new SystemException(1, "MCU资源已耗尽，请关闭一些会议后重试或者稍后再试。");
                } else if (busiConferenceAppointment.getType() == 1) {
                    String startTimeStr = busiConferenceAppointment.getStartTime();
                    Date startTime = DateUtil.convertDateByString(startTimeStr, "");
                    if (startTime.getTime() - new Date().getTime() < 7200000) {
                        throw new SystemException(1, "MCU资源已耗尽，创建2小时内的预约会议请先关闭一些会议后重试或者稍后再试。");
                    }
                }
            }
        }
    }

    private void checkResource(BusiMcuZjConferenceAppointment busiConferenceAppointment) {
        McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(busiConferenceAppointment.getDeptId()).getMasterMcuZjBridge();
        if (mcuZjBridge != null) {
            if (mcuZjBridge.getUsedResourceCount() >= mcuZjBridge.getSystemResourceCount()) {
                if (busiConferenceAppointment.getType() == 2) {
                    throw new SystemException(1, "MCU资源已耗尽，请关闭一些会议后重试或者稍后再试。");
                } else if (busiConferenceAppointment.getType() == 1) {
                    String startTimeStr = busiConferenceAppointment.getStartTime();
                    Date startTime = DateUtil.convertDateByString(startTimeStr, "");
                    if (startTime.getTime() - new Date().getTime() < 7200000) {
                        throw new SystemException(1, "MCU资源已耗尽，创建2小时内的预约会议请先关闭一些会议后重试或者稍后再试。");
                    }
                }
            } else {
                if (mcuZjBridge.getUsedResourceCount() > 0) {
                    SourceTemplate sourceTemplate = mcuZjBridge.getDefaultSourceTemplate();
                    Object resourceTemplateIdObj = busiConferenceAppointment.getParams().get("resourceTemplateId");
                    if (resourceTemplateIdObj != null) {
                        Integer resourceTemplateId = null;
                        try {
                            resourceTemplateId = (Integer) resourceTemplateIdObj;
                            SourceTemplate sourceTemplateT = mcuZjBridge.getSourceTemplateById(resourceTemplateId);
                            if (sourceTemplateT != null) {
                                sourceTemplate = sourceTemplateT;
                            }
                        } catch (Exception e) {
                        }
                    }
                    if (sourceTemplate.getEvaluationResourceCount() + mcuZjBridge.getUsedResourceCount() > mcuZjBridge.getSystemResourceCount()) {
                        throw new SystemException(1, "MCU资源不足，请关闭一些会议后重试。");
                    }
                }
            }
        }
    }

    private void checkType(BusiMcuKdcTemplateConference busiTemplateConference) {
        List<BusiMcuKdcTemplateConference> templateConferences = busiMcuKdcTemplateConferenceService.selectAllBusiMcuKdcTemplateConferenceList(busiTemplateConference);
        if (CollectionUtils.isNotEmpty(templateConferences)) {
            for (BusiMcuKdcTemplateConference templateConference : templateConferences) {
                List<BusiMcuKdcConferenceAppointment> busiConferenceAppointments = busiMcuKdcConferenceAppointmentService.selectBusiMcuKdcConferenceAppointmentByTemplateId(templateConference.getId());
                if (CollectionUtils.isNotEmpty(busiConferenceAppointments)) {
                    for (BusiMcuKdcConferenceAppointment conferenceAppointment : busiConferenceAppointments) {
                        if (conferenceAppointment.getType() == 2) {
                            throw new CustomException("你有正在开的会议不能创建");
                        }
                    }
                }
            }
        }
    }

    private void checkType(BusiMcuZjTemplateConference busiTemplateConference) {
        List<BusiMcuZjTemplateConference> templateConferences = busiMcuZjTemplateConferenceService.selectAllBusiMcuZjTemplateConferenceList(busiTemplateConference);
        if (CollectionUtils.isNotEmpty(templateConferences)) {
            for (BusiMcuZjTemplateConference templateConference : templateConferences) {
                List<BusiMcuZjConferenceAppointment> busiConferenceAppointments = busiMcuZjConferenceAppointmentService.selectBusiMcuZjConferenceAppointmentByTemplateId(templateConference.getId());
                if (CollectionUtils.isNotEmpty(busiConferenceAppointments)) {
                    for (BusiMcuZjConferenceAppointment conferenceAppointment : busiConferenceAppointments) {
                        if (conferenceAppointment.getType() == 2) {
                            throw new CustomException("你有正在开的会议不能创建");
                        }
                    }
                }
            }
        }
    }

    private void checkType(BusiTemplateConference busiTemplateConference) {
        int limitNumber=0;
        int count=0;
        SysConfigMapper sysConfigMapper = BeanFactory.getBean(SysConfigMapper.class);
        SysConfig sysConfig_q = new SysConfig();
        sysConfig_q.setConfigKey("mobile.number.limit");
        SysConfig sysConfig = sysConfigMapper.selectConfig(sysConfig_q);
        if (sysConfig != null) {
            String configValue = sysConfig.getConfigValue();
            if (Strings.isNotBlank(configValue)) {
                limitNumber = Integer.valueOf(configValue);
            }
        }

        List<BusiTemplateConference> templateConferences = busiTemplateConferenceService.selectAllBusiTemplateConferenceList(busiTemplateConference);
        if (CollectionUtils.isNotEmpty(templateConferences)) {
            for (BusiTemplateConference templateConference : templateConferences) {
                List<BusiConferenceAppointment> busiConferenceAppointments = busiConferenceAppointmentService.selectBusiConferenceAppointmentByTemplateId(templateConference.getId());
                if (CollectionUtils.isNotEmpty(busiConferenceAppointments)) {
                    for (BusiConferenceAppointment conferenceAppointment : busiConferenceAppointments) {
                        if (conferenceAppointment.getType() == 2) {
                            count++;

                        }
                    }
                }
            }
        }

        if(limitNumber>0){
            if(count>limitNumber){
                throw new CustomException("及时会议超过数量限制");
            }
        }
    }

    private void checkType(BusiMcuSmc3TemplateConference busiTemplateConference) {
        int limitNumber=0;
        int count=0;
        SysConfigMapper sysConfigMapper = BeanFactory.getBean(SysConfigMapper.class);
        SysConfig sysConfig_q = new SysConfig();
        sysConfig_q.setConfigKey("mobile.number.limit");
        SysConfig sysConfig = sysConfigMapper.selectConfig(sysConfig_q);
        if (sysConfig != null) {
            String configValue = sysConfig.getConfigValue();
            if (Strings.isNotBlank(configValue)) {
                limitNumber = Integer.valueOf(configValue);
            }
        }

        List<BusiMcuSmc3TemplateConference> templateConferences = busiMcuSmc3TemplateConferenceService.selectAllBusiTemplateConferenceList(busiTemplateConference);
        if (CollectionUtils.isNotEmpty(templateConferences)) {
            for (BusiMcuSmc3TemplateConference templateConference : templateConferences) {
                List<BusiMcuSmc3ConferenceAppointment> busiConferenceAppointments = busiMcuSmc3ConferenceAppointmentService.selectBusiMcuSmc3ConferenceAppointmentByTemplateId(templateConference.getId());
                if (CollectionUtils.isNotEmpty(busiConferenceAppointments)) {
                    for (BusiMcuSmc3ConferenceAppointment conferenceAppointment : busiConferenceAppointments) {
                        if (conferenceAppointment.getType() == 2) {
                            count++;
                        }
                    }
                }
            }
        }

        if(limitNumber>0) {
            if (count > limitNumber) {
                throw new CustomException("及时会议超过数量限制");
            }
        }
    }

    private BusiMcuPlcConferenceAppointment initDeptParamTemplateForPlc() {
        BusiMcuPlcConferenceAppointment busiConferenceAppointment = new BusiMcuPlcConferenceAppointment();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        String deptName = principal.getUser().getDept().getDeptName();
        busiConferenceAppointment.getParams().put("conferenceName", deptName + "-快速会议");
        Long deptId = principal.getUser().getDeptId();
        busiConferenceAppointment.setDeptId(deptId);
        busiConferenceAppointment.setIsAutoCreateTemplate(1);
        busiConferenceAppointment.setRepeatRate(1);
        Date date = new Date();
        busiConferenceAppointment.setStartTime(DateUtil.convertDateToString(date, null));
        busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(org.apache.commons.lang3.time.DateUtils.addHours(date, 2), null));
        busiConferenceAppointment.setStatus(1);
        return busiConferenceAppointment;
    }

    private BusiMcuKdcConferenceAppointment initDeptParamTemplateForKdc() {
        BusiMcuKdcConferenceAppointment busiConferenceAppointment = new BusiMcuKdcConferenceAppointment();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        String deptName = principal.getUser().getDept().getDeptName();
        busiConferenceAppointment.getParams().put("conferenceName", deptName + "-快速会议");
        Long deptId = principal.getUser().getDeptId();
        busiConferenceAppointment.setDeptId(deptId);
        busiConferenceAppointment.setIsAutoCreateTemplate(1);
        busiConferenceAppointment.setRepeatRate(1);
        Date date = new Date();
        busiConferenceAppointment.setStartTime(DateUtil.convertDateToString(date, null));
        busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(org.apache.commons.lang3.time.DateUtils.addHours(date, 2), null));
        busiConferenceAppointment.setStatus(1);
        return busiConferenceAppointment;
    }

    private BusiMcuZjConferenceAppointment initDeptParamTemplateForZj() {
        BusiMcuZjConferenceAppointment busiConferenceAppointment = new BusiMcuZjConferenceAppointment();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        String deptName = principal.getUser().getDept().getDeptName();
        busiConferenceAppointment.getParams().put("conferenceName", deptName + "-快速会议");
        Long deptId = principal.getUser().getDeptId();
        busiConferenceAppointment.setDeptId(deptId);
        busiConferenceAppointment.setIsAutoCreateTemplate(1);
        busiConferenceAppointment.setRepeatRate(1);
        Date date = new Date();
        busiConferenceAppointment.setStartTime(DateUtil.convertDateToString(date, null));
        busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(org.apache.commons.lang3.time.DateUtils.addHours(date, 2), null));
        busiConferenceAppointment.setStatus(1);
        return busiConferenceAppointment;
    }

    private BusiConferenceAppointment initDeptParamTemplate() {
        BusiConferenceAppointment busiConferenceAppointment = new BusiConferenceAppointment();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        String deptName = principal.getUser().getDept().getDeptName();
        busiConferenceAppointment.getParams().put("conferenceName", deptName + "-快速会议");
        Long deptId = principal.getUser().getDeptId();
        busiConferenceAppointment.setDeptId(deptId);
        busiConferenceAppointment.setIsAutoCreateTemplate(1);
        busiConferenceAppointment.setRepeatRate(1);
        Date date = new Date();
        busiConferenceAppointment.setStartTime(DateUtil.convertDateToString(date, null));
        busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(org.apache.commons.lang3.time.DateUtils.addHours(date, 2), null));
        busiConferenceAppointment.setStatus(1);
        return busiConferenceAppointment;
    }

    private BusiMcuSmc3ConferenceAppointment initDeptParamTemplateForSmc3() {
        BusiMcuSmc3ConferenceAppointment busiConferenceAppointment = new BusiMcuSmc3ConferenceAppointment();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        String deptName = principal.getUser().getDept().getDeptName();
        busiConferenceAppointment.getParams().put("conferenceName", deptName + "-快速会议");
        Long deptId = principal.getUser().getDeptId();
        busiConferenceAppointment.setDeptId(deptId);
        busiConferenceAppointment.setIsAutoCreateTemplate(1);
        busiConferenceAppointment.setRepeatRate(1);
        Date date = new Date();
        busiConferenceAppointment.setStartTime(DateUtil.convertDateToString(date, null));
        busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(org.apache.commons.lang3.time.DateUtils.addHours(date, 2), null));
        busiConferenceAppointment.setStatus(1);
        Map<String, Object> businessProperties = (Map<String, Object>) busiConferenceAppointment.getParams().get("businessProperties");
        if (businessProperties == null) {
            businessProperties = new HashMap<>();
            busiConferenceAppointment.getParams().put("businessProperties", businessProperties);
        }
        businessProperties.put("mainMcuId", "");
        businessProperties.put("mainMcuName", "");
        businessProperties.put("mainServiceZoneId", "");
        businessProperties.put("mainServiceZoneName", "");

        businessProperties.put("videoProtocol", "H264_BP");
        businessProperties.put("videoResolution", "MPI_1080P");
        businessProperties.put("audioProtocol", "AAC_LD_S");
//        businessProperties.put("streamService", null);
        return busiConferenceAppointment;
    }

    private void createMobileConferenceParamTemplate(BusiConferenceAppointment busiConferenceAppointment) {
        Map<String, Object> params = busiConferenceAppointment.getParams();
        if (params.get(IS_AUTO_CALL) == null) {
            params.put(IS_AUTO_CALL, 2);
        }

        if (Objects.isNull(params.get(CONFERENCE_NAME))) {
            params.put(CONFERENCE_NAME, "快速会议");
        }
        if (Objects.isNull(params.get(DEFAULT_VIEW_LAYOUT))) {
            params.put(DEFAULT_VIEW_LAYOUT, ALL_EQUAL);
        }
        if (Objects.isNull(params.get(DEFAULT_VIEW_IS_DISPLAY_SELF))) {
            params.put(DEFAULT_VIEW_IS_DISPLAY_SELF, -1);
        }
        if (Objects.isNull(params.get(RECORDING_ENABLED))) {
            params.put(RECORDING_ENABLED, 2);
        }
        if (Objects.isNull(params.get(STREAMING_ENABLED))) {
            params.put(STREAMING_ENABLED, 2);
        }
        if (Objects.isNull(params.get(DEFAULT_VIEW_IS_BROADCAST))) {
            params.put(DEFAULT_VIEW_IS_BROADCAST, 2);
        }
        if (Objects.isNull(params.get(DEFAULT_VIEW_IS_FILL))) {
            params.put(DEFAULT_VIEW_IS_FILL, 1);
        }
        if (Objects.isNull(params.get(BUSINESS_FIELD_TYPE))) {
            params.put(BUSINESS_FIELD_TYPE, 100);
        }

    }

    /**
     * 开启混音
     */
    @PutMapping("/openMixingOne")
    @Operation(summary = "开启混音")
    public RestResponse openMixingOne(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.openMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.openMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.openMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.openMixing(conferenceId, attendeeId);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.openMixing(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 关闭混音
     */
    @PutMapping("/closeMixingOne")
    @Operation(summary = "关闭混音")
    public RestResponse closeMixingOne(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.closeMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.closeMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.closeMixing(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.closeMixing(conferenceId, attendeeId);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.closeMixing(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 参会者页面上重呼
     */
    @PutMapping("/recall")
    @Operation(summary = "参会者页面上重呼")
    public RestResponse recall(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.recall(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.recall(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.recall(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.recall(conferenceId, attendeeId);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.recall(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 参会者页面上挂断
     */
    @PutMapping("/hangUp")
    @Operation(summary = "参会者页面上挂断")
    public RestResponse hangUp(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.hangUp(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.hangUp(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.hangUp(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.hangUp(conferenceId, attendeeId);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.hangUp(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 参会者页面上移除
     */
    @PutMapping("/remove")
    @Operation(summary = "参会者页面上移除")
    public RestResponse remove(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.remove(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.remove(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.remove(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.remove(conferenceId, attendeeId);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.remove(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

    /**
     * 主会场变更
     */
    @PutMapping("/changeMaster")
    @Operation(summary = "主会场变更")
    public RestResponse changeMaster(@Valid @RequestBody MobileAttendeeVo mobileAttendeeVo) {
        String attendeeId = mobileAttendeeVo.getAttendeeId();
        String conferenceId = mobileAttendeeVo.getConferenceId();

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        McuType mcuType = conferenceIdVo.getMcuType();
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (!canControlConference(baseConferenceContext)) {
            return fail(1, "没有权限控制会议！");
        }
        switch (mcuType) {
            case FME: {
                attendeeService.changeMaster(conferenceId, attendeeId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.changeMaster(conferenceId, attendeeId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.changeMaster(conferenceId, attendeeId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.changeMaster(conferenceId, attendeeId);
                break;
            }
            case SMC3: {
                attendeeSmc3Service.changeMaster(conferenceId, attendeeId);
                break;
            }
        }

        return success();
    }

}

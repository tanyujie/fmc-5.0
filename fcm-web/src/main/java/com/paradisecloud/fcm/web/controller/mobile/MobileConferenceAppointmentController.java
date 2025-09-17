package com.paradisecloud.fcm.web.controller.mobile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.ViewConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.McuTypeVo;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IBusiTemplatePollingSchemeService;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.*;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcBridgeCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.enumer.McuKdcLayoutTemplates;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.*;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.enumer.McuPlcLayoutTemplates;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.*;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.service.interfaces.IBusiLiveSettingService;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.fcm.web.model.mobile.AppointmentConferenceSyncExecutor;
import com.paradisecloud.fcm.web.model.mobile.MobileConferenceAppointmentRequest;
import com.paradisecloud.fcm.web.model.mobile.req.DefaultViewCellScreens;
import com.paradisecloud.fcm.web.model.mobile.vo.BatchInviteVo;
import com.paradisecloud.fcm.web.service.interfaces.IBusiAllMcuService;
import com.paradisecloud.fcm.web.utils.AuthenticationUtil;
import com.paradisecloud.system.dao.mapper.SysConfigMapper;
import com.paradisecloud.system.dao.model.SysConfig;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 移动端会议Controller
 *
 * @author nj
 * @date 2022-06-15
 */
@RestController
@RequestMapping("/mobile/conferenceAppointment")
@Tag(name = "移动端会议")
public class MobileConferenceAppointmentController extends MobileBaseController {


    public static final String SCHEME_NAME = "全局轮询";
    public static final String IS_AUTO_CALL = "isAutoCall";
    public static final String CONFERENCE_NAME = "conferenceName";
    public static final String DEFAULT_VIEW_LAYOUT = "defaultViewLayout";
    public static final String DEFAULT_VIEW_IS_DISPLAY_SELF = "defaultViewIsDisplaySelf";
    public static final String RECORDING_ENABLED = "recordingEnabled";
    public static final String STREAMING_ENABLED = "streamingEnabled";
    public static final String DEFAULT_VIEW_IS_BROADCAST = "defaultViewIsBroadcast";
    public static final String DEFAULT_VIEW_IS_FILL = "defaultViewIsFill";
    public static final String BUSINESS_FIELD_TYPE = "businessFieldType";
    public static final String ALL_EQUAL = "allEqual";
    public static final int INT4 = 4;
    public static final int INT8 = 8;
    @Resource
    private IBusiConferenceAppointmentService busiConferenceAppointmentService;

    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;

    @Resource
    private IBusiConferenceService busiConferenceService;

    @Resource
    private IDefaultAttendeeOperationPackageService defaultAttendeeOperationPackageService;

    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;

    @Resource
    private IBusiTemplatePollingSchemeService busiTemplatePollingSchemeService;

    @Resource
    private IAttendeeService attendeeService;

    @Resource
    private IBusiLiveSettingService iBusiLiveSettingService;

    @Resource
    private ICoSpaceService fmeCoSpaceService;

    @Resource
    private AppointmentConferenceSyncExecutor appointmentConferenceSyncExecutor;

    @Resource
    private BusiConferenceAppointmentMapper busiConferenceAppointmentMapper;


    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;

    @Resource
    private IBusiTerminalService busiTerminalService;

    @Resource
    private IBusiRecordsService busiRecordsService;

    @Resource
    private ViewConferenceAppointmentMapper viewConferenceAppointmentMapper;

    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;

    @Resource
    private IBusiMcuZjConferenceAppointmentService busiMcuZjConferenceAppointmentService;

    @Resource
    private IBusiMcuZjTemplateConferenceService busiMcuZjTemplateConferenceService;

    @Resource
    private IBusiMcuZjConferenceService busiMcuZjConferenceService;

    @Resource
    private IDefaultAttendeeOperationPackageForMcuZjService defaultAttendeeOperationPackageForMcuZjService;

    @Resource
    private IBusiMcuZjTemplatePollingSchemeService busiMcuZjTemplatePollingSchemeService;

    @Resource
    private IAttendeeForMcuZjService attendeeForMcuZjService;

    @Resource
    private IBusiRecordsForMcuZjService busiRecordsForMcuZjService;

    @Resource
    private IBusiMcuPlcConferenceAppointmentService busiMcuPlcConferenceAppointmentService;

    @Resource
    private IBusiMcuPlcTemplateConferenceService busiMcuPlcTemplateConferenceService;

    @Resource
    private IBusiMcuPlcConferenceService busiMcuPlcConferenceService;

    @Resource
    private IDefaultAttendeeOperationPackageForMcuPlcService defaultAttendeeOperationPackageForMcuPlcService;

    @Resource
    private IBusiMcuPlcTemplatePollingSchemeService busiMcuPlcTemplatePollingSchemeService;

    @Resource
    private IAttendeeForMcuPlcService attendeeForMcuPlcService;

    @Resource
    private IBusiRecordsForMcuPlcService busiRecordsForMcuPlcService;

    @Resource
    private IBusiMcuKdcConferenceAppointmentService busiMcuKdcConferenceAppointmentService;

    @Resource
    private IBusiMcuKdcTemplateConferenceService busiMcuKdcTemplateConferenceService;

    @Resource
    private IBusiMcuKdcConferenceService busiMcuKdcConferenceService;

    @Resource
    private IDefaultAttendeeOperationPackageForMcuKdcService defaultAttendeeOperationPackageForMcuKdcService;

    @Resource
    private IBusiMcuKdcTemplatePollingSchemeService busiMcuKdcTemplatePollingSchemeService;

    @Resource
    private IAttendeeForMcuKdcService attendeeForMcuKdcService;

    @Resource
    private IBusiRecordsForMcuKdcService busiRecordsForMcuKdcService;

    @Resource
    private IBusiAllMcuService busiAllMcuService;

    private static ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2,
            new BasicThreadFactory.Builder().namingPattern("MobileConferenceAppointment-schedule-pool-%d").daemon(true).build());


    /**
     * 获取布局等参数列表
     */
    @GetMapping("/source/template")
    @Operation(summary = "获取布局列表")
    public RestResponse getSourceTemplate() {
        String mcuTypeStr = "";
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = loginUser.getUser().getDeptId();
        McuTypeVo mcuTypeVo = busiAllMcuService.getDefaultMcuType(deptId);
        mcuTypeStr = mcuTypeVo.getCode();
        ModelBean data = new ModelBean();
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                List<ModelBean> list = new ArrayList<>();
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "一分屏");
                    modelBean.put("value", OneSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    list.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "四分屏");
                    modelBean.put("value", FourSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    list.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "九分屏");
                    modelBean.put("value", NineSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    list.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "自动");
                    modelBean.put("value", AutomaticSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    list.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "全等");
                    modelBean.put("value", AllEqualSplitScreen.LAYOUT);
                    modelBean.put("isDefault", true);
                    list.add(modelBean);
                }
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", "一大N小");
                    modelBean.put("value", OnePlusNSplitScreen.LAYOUT);
                    modelBean.put("isDefault", false);
                    list.add(modelBean);
                }
                data.put("splitScreenList", list);
            }
            case MCU_ZJ: {
                McuZjBridge mcuZjBridge = null;
                try {
                    mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(deptId).getMasterMcuZjBridge();
                } catch (Exception e) {
                }
                Assert.notNull(mcuZjBridge, "当前MCU不可用！");
                List<SourceTemplate> sourceTemplateList = mcuZjBridge.getSourceTemplateList();
                if (sourceTemplateList == null || sourceTemplateList.size() == 0) {
                    Assert.isTrue(false, "当前MCU不可用！");
                }
                SourceTemplate sourceTemplate = mcuZjBridge.getDefaultSourceTemplate();
                data.put("supportBroadcast", sourceTemplate.isSupportBroadcast());
                List<ModelBean> list;
                if (sourceTemplate.getSingle_view() == 0) {
                    list = sourceTemplate.getSpeakerSplitScreenList();
                } else {
                    list = sourceTemplate.getGuestSplitScreenList();
                }
                for (int i = 0; i < list.size(); i++) {
                    ModelBean modelBean = list.get(i);
                    if (i == 0) {
                        modelBean.put("isDefault", true);
                    } else {
                        modelBean.put("isDefault", false);
                    }
                }
                data.put("splitScreenList", list);
            }
            case MCU_PLC: {
                data.put("supportBroadcast", true);
                List<ModelBean> list = McuPlcLayoutTemplates.getLayoutTemplateScreenList();
                data.put("splitScreenList", list);
            }
            case MCU_KDC: {
                data.put("supportBroadcast", true);
                List<ModelBean> list = McuKdcLayoutTemplates.getLayoutTemplateScreenList();
                data.put("splitScreenList", list);
            }
        }

        return RestResponse.success(0, "查询成功", data);
    }

    /**
     * 查询会议预约记录列表
     */
    @GetMapping(value = "/list")
    @Operation(summary = "查询会议预约记录列表")
    public RestResponse list(@RequestParam(value = "isOnlyMine", required = false) boolean isOnlyMine) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        startPage();
        List<Object> list = new ArrayList<>();
        ViewConferenceAppointment viewConferenceAppointment = new ViewConferenceAppointment();
        viewConferenceAppointment.setDeptId(loginUser.getUser().getDeptId());
        HashMap<String, Object> paramZj = new HashMap<>(1);
        paramZj.put("businessFieldType", 100);
        if (isOnlyMine) {
            HashMap<String, Object> businessProperties = new HashMap<>();
            businessProperties.put("createUserId", loginUser.getUser().getUserId());
            paramZj.put("businessProperties", businessProperties);
        }
        viewConferenceAppointment.setParams(paramZj);
        List<ViewConferenceAppointment> viewConferenceAppointmentList = viewConferenceAppointmentMapper.selectViewConferenceAppointmentList(viewConferenceAppointment);
        viewConferenceAppointmentList.stream().forEach(p -> {
            ModelBean modelBean = null;
            McuType mcuType = McuType.convert(p.getMcuType());
            switch (mcuType) {
                case FME: {
                    modelBean = busiTemplateConferenceService.selectBusiTemplateConferenceById(p.getTemplateId());
                    break;
                }
                case MCU_ZJ: {
                    modelBean = busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceById(p.getTemplateId());
                    break;
                }
                case MCU_PLC: {
                    modelBean = busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceById(p.getTemplateId());
                    break;
                }
                case MCU_KDC: {
                    modelBean = busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceById(p.getTemplateId());
                    break;
                }
            }
            boolean isPresenter = false;
            boolean isMyConference = false;
            if (!Objects.isNull(modelBean)) {
                String conferenceId = p.getConferenceId();
                p.getParams().put("conferenceId", conferenceId);
                Object obj = modelBean.get("templateConference");
                String jsonString = JSON.toJSONString(obj);
                BusiTemplateConference templateConference = JSON.parseObject(jsonString, BusiTemplateConference.class);
                if (Objects.isNull(templateConference.getConferencePassword())) {
                    p.setPassword("DISABLED");
                } else {
                    p.setPassword("ENABLED");
                }
                if (templateConference.getPresenter() != null && templateConference.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                    isPresenter = true;
                }
                if (templateConference.getCreateUserId() != null && templateConference.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                    isMyConference = true;
                }
            }
            p.getParams().put("isPresenter", isPresenter);
            p.getParams().put("isMyConference", isMyConference);
            list.add(p);
        });
        return getDataTable(list);
    }


    /**
     * 通过关键字查询预约会议列表
     */
    @GetMapping("/list/searchkey")
    @Operation(summary = "通过关键字查询预约会议列表")
    public RestResponse listKeySearch(@RequestParam(value = "searchKey", required = false) String searchKey,
                                      @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = loginUser.getUser().getDeptId();
        PageHelper.startPage(pageIndex, pageSize);
        List<Object> list = new ArrayList<>();
        List<ViewConferenceAppointment> viewConferenceAppointmentList = viewConferenceAppointmentMapper.selectViewConferenceAppointmentListBykey(searchKey, deptId);
        viewConferenceAppointmentList.stream().forEach(p -> {
            ModelBean modelBean = null;
            McuType mcuType = McuType.convert(p.getMcuType());
            switch (mcuType) {
                case FME: {
                    modelBean = busiTemplateConferenceService.selectBusiTemplateConferenceById(p.getTemplateId());
                    break;
                }
                case MCU_ZJ: {
                    modelBean = busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceById(p.getTemplateId());
                    break;
                }
                case MCU_PLC: {
                    modelBean = busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceById(p.getTemplateId());
                    break;
                }
                case MCU_KDC: {
                    modelBean = busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceById(p.getTemplateId());
                    break;
                }
            }
            if (!Objects.isNull(modelBean)) {
                String conferenceId = EncryptIdUtil.generateConferenceId(Long.valueOf(modelBean.get("id").toString()), p.getMcuType());
                p.getParams().put("conferenceId", conferenceId);
                Object obj = modelBean.get("templateConference");
                String jsonString = JSON.toJSONString(obj);
                BusiTemplateConference templateConference = JSON.parseObject(jsonString, BusiTemplateConference.class);
                if (Objects.isNull(templateConference.getConferencePassword())) {
                    p.setPassword("DISABLED");
                } else {
                    p.setPassword("ENABLED");
                }
            }
            list.add(p);
        });
        return getDataTable(list);
    }

    public static Boolean judge(Date date1, Date date2, Date StartTime, Date EndDate) {
        long d1 = date1.getTime();
        long d2 = date2.getTime();
        long start = StartTime.getTime();
        long end = EndDate.getTime();
        if (((d1 - start) <= 0) && ((end - d2) <= 0) ||
                ((d2 - start) >= 0) && ((d1 - end) <= 0)
        ) {
            return true;
        }
        return false;
    }


    /**
     * 新增会议预约记录
     */
    @PostMapping
    @Operation(summary = "新增会议预约记录")
    public RestResponse add(@RequestBody JSONObject jsonObject) throws ParseException {
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
                        if (StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
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
                            if (StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
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
                    if (StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
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
                        if (StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
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
                    if (StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
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
                        if (StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
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
                    if (StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
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
                            checkType(busiTemplateConference);
                            busiConferenceAppointment.setStatus(2);
                            busiConferenceAppointment.setType(2);
                            busiConferenceAppointment.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                        } else {
                            throw new CustomException("类型错误");
                        }
                    } else {
                        if (StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
                            // 没用传type的，如果开始时间在60秒内，直接看作即使会议
                            Date startTime = DateUtil.convertDateByString(mobileConferenceAppointmentRequest.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                            if (startTime.getTime() - System.currentTimeMillis() < 60000) {
                                type = 2;
                                BusiMcuKdcTemplateConference busiTemplateConference = new BusiMcuKdcTemplateConference();
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
                            checkType(busiTemplateConference);
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
        }

        return RestResponse.fail();
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

    private LoginUser checkeTime(BusiConferenceAppointment busiConferenceAppointmentP) throws ParseException {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
        busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
        busiTemplateConference.setBusinessFieldType(100);
        List<BusiTemplateConference> templateConferences = busiTemplateConferenceService.selectAllBusiTemplateConferenceList(busiTemplateConference);

        if (CollectionUtils.isNotEmpty(templateConferences)) {
            for (BusiTemplateConference templateConference : templateConferences) {
                Long id = templateConference.getId();
                List<BusiConferenceAppointment> busiConferenceAppointments = busiConferenceAppointmentService.selectBusiConferenceAppointmentByTemplateId(id);
                if (CollectionUtils.isNotEmpty(busiConferenceAppointments)) {
                    for (BusiConferenceAppointment busiConferenceAppointment : busiConferenceAppointments) {

                        String startTime = busiConferenceAppointment.getStartTime();
                        String endTime = busiConferenceAppointment.getEndTime();
                        Date date1 = DateUtils.parseDate(startTime, "yyyy-MM-dd HH:mm:ss");
                        Date date2 = DateUtils.parseDate(endTime, "yyyy-MM-dd HH:mm:ss");

                        Date date3 = DateUtils.parseDate(busiConferenceAppointmentP.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                        Date date4 = DateUtils.parseDate(busiConferenceAppointmentP.getEndTime(), "yyyy-MM-dd HH:mm:ss");

                        Boolean judge = judge(date1, date2, date3, date4);
                        if (judge) {
                            throw new CustomException("同一时间段已有会议");
                        }
                    }
                }
            }
        }
        return loginUser;
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
            if (StringUtils.hasText(mobileConferenceAppointmentRequest.getConferenceName())) {
                params.put("conferenceName", mobileConferenceAppointmentRequest.getConferenceName());
            }
            if (StringUtils.hasText(mobileConferenceAppointmentRequest.getEndTime())) {
                params.put("endTime", mobileConferenceAppointmentRequest.getEndTime());
                busiConferenceAppointment.setEndTime(mobileConferenceAppointmentRequest.getEndTime());
            }

            if (mobileConferenceAppointmentRequest.getDurationOfMinutes() > 0) {
                if (!StringUtils.hasText(mobileConferenceAppointmentRequest.getEndTime())) {
                    Date sdate;
                    if (Objects.isNull(mobileConferenceAppointmentRequest.getStartTime())) {
                        sdate = DateUtils.parseDate(busiConferenceAppointment.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                    } else {
                        sdate = DateUtils.parseDate(mobileConferenceAppointmentRequest.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                    }
                    busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(DateUtils.addMinutes(sdate, mobileConferenceAppointmentRequest.getDurationOfMinutes()), null));
                } else {
                    try {
                        Date date = DateUtils.parseDate(mobileConferenceAppointmentRequest.getEndTime(), "yyyy-MM-dd HH:mm:ss");
                        busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(DateUtils.addMinutes(date, mobileConferenceAppointmentRequest.getDurationOfMinutes()), null));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
            if (StringUtils.hasText(mobileConferenceAppointmentRequest.getConferencePassword())) {
                params.put("conferencePassword", mobileConferenceAppointmentRequest.getConferencePassword());
            }
            if (StringUtils.hasText(mobileConferenceAppointmentRequest.getStreamUrl())) {
                params.put("streamingEnabled", 1);
                params.put("streamUrl", mobileConferenceAppointmentRequest.getStreamUrl());
            }
            if (StringUtils.hasText(mobileConferenceAppointmentRequest.getDefaultViewLayout())) {
                params.put("defaultViewLayout", mobileConferenceAppointmentRequest.getDefaultViewLayout());
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

    /**
     * 获取模版会议
     */
    @GetMapping(value = "/getTemplateConferenceInfo")
    @Operation(summary = "获取模版会议")
    public RestResponse getBusiTemplateConferenceInfo(@RequestParam("conferenceId") String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                return RestResponse.success(busiTemplateConferenceService.selectBusiTemplateConferenceById(id));
            }
            case MCU_ZJ: {
                return RestResponse.success(busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceById(id));
            }
            case MCU_PLC: {
                return RestResponse.success(busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceById(id));
            }
            case MCU_KDC: {
                return RestResponse.success(busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceById(id));
            }
        }
        return RestResponse.fail();
    }

    /**
     * 根据模版ID获取会议信息
     */
    @GetMapping(value = "/getCurrentConferenceInfo")
    @Operation(summary = "获取会议详细信息(模版ID)")
    public RestResponse getCurrentConferenceInfo(@RequestParam("conferenceId") String conferenceId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                ConferenceContext conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(id);
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
                json.put("defaultViewOperation", conferenceContext.getDefaultViewOperation());
                json.put("isPresenter", isPresenter);
                json.put("isMyConference", isMyConference);
                json.remove("supervisorPassword");
                List<ModelBean> splitScreenList = conferenceContext.getSpeakerSplitScreenList();
                json.put("splitScreenList", splitScreenList);
                return RestResponse.success(json);
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
        }

        return success(recordFileInfo);
    }

    @GetMapping("/defaultViewData")
    @Operation(summary = "显示布局数据")
    public RestResponse defaultViewData(@RequestParam("conferenceId") String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                return success(defaultAttendeeOperationPackageService.defaultViewData(conferenceId));
            }
            case MCU_ZJ: {
                return success(defaultAttendeeOperationPackageForMcuZjService.defaultViewData(conferenceId));
            }
            case MCU_PLC: {
                return success(defaultAttendeeOperationPackageForMcuPlcService.defaultViewData(conferenceId));
            }
            case MCU_KDC: {
                return success(defaultAttendeeOperationPackageForMcuKdcService.defaultViewData(conferenceId));
            }
        }
        return RestResponse.fail();
    }

    @PostMapping("/updateDefaultViewConfigInfo")
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
        }
        
        return success();
    }

    @PostMapping("/pre/updateDefaultViewConfigInfo")
    @Operation(summary = "修改布局")
    public RestResponse preUpdateDefaultViewConfigInfo(@RequestBody JSONObject jsonObject) {
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
        }

        return success();
    }

    @GetMapping("/discuss")
    @Operation(summary = "会议讨论")
    public RestResponse discuss(@RequestParam("conferenceId") String conferenceId) {
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
                busiConferenceService.discuss(conferenceId);
                break;
            }
            case MCU_ZJ: {
                busiMcuZjConferenceService.discuss(conferenceId);
                break;
            }
            case MCU_PLC: {
                busiMcuPlcConferenceService.discuss(conferenceId);
                break;
            }
            case MCU_KDC: {
                busiMcuKdcConferenceService.discuss(conferenceId);
                break;
            }
        }

        return success();
    }

    @GetMapping("/cancelDiscuss")
    @Operation(summary = "取消会议讨论")
    public RestResponse cancelDiscuss(@RequestParam("conferenceId") String conferenceId) {
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
                busiConferenceService.cancelDiscuss(conferenceId);
                break;
            }
            case MCU_ZJ: {
                busiMcuZjConferenceService.cancelDiscuss(conferenceId);
                break;
            }
            case MCU_PLC: {
                busiMcuPlcConferenceService.cancelDiscuss(conferenceId);
                break;
            }
            case MCU_KDC: {
                busiMcuKdcConferenceService.cancelDiscuss(conferenceId);
                break;
            }
        }

        return success();
    }

    @GetMapping("/lock")
    @Operation(summary = "会议锁定")
    public RestResponse lock(@RequestParam("conferenceId") String conferenceId, @RequestParam("locked") Boolean locked) {
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
                busiConferenceService.lock(conferenceId, locked);
                break;
            }
            case MCU_ZJ: {
                busiMcuZjConferenceService.lock(conferenceId, locked);
                break;
            }
            case MCU_PLC: {
                busiMcuPlcConferenceService.lock(conferenceId, locked);
                break;
            }
            case MCU_KDC: {
                busiMcuKdcConferenceService.lock(conferenceId, locked);
                break;
            }
        }

        return success();
    }


    /**
     * 获取全局轮询方案
     */
    @GetMapping("/defaultTemplatePollingScheme")
    @Operation(summary = "查询全局轮询方案")
    public RestResponse defaultTemplatePollingScheme(BusiTemplatePollingScheme busiTemplatePollingScheme) {
        String mcuTypeStr = "";
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = loginUser.getUser().getDeptId();
        McuTypeVo mcuTypeVo = busiAllMcuService.getDefaultMcuType(deptId);
        mcuTypeStr = mcuTypeVo.getCode();
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                busiTemplatePollingScheme.setSchemeName(SCHEME_NAME);
                busiTemplatePollingScheme.setEnableStatus(1);
                List<BusiTemplatePollingScheme> busiTemplatePollingSchemes = busiTemplatePollingSchemeService.selectBusiTemplatePollingSchemeList(busiTemplatePollingScheme);
                if (CollectionUtils.isNotEmpty(busiTemplatePollingSchemes)) {
                    return success(busiTemplatePollingSchemes.get(0));
                }
            }
            case MCU_ZJ: {
                busiTemplatePollingScheme.setSchemeName(SCHEME_NAME);
                busiTemplatePollingScheme.setEnableStatus(1);
                BusiMcuZjTemplatePollingScheme busiMcuZjTemplatePollingScheme = new BusiMcuZjTemplatePollingScheme();
                BeanUtils.copyBeanProp(busiMcuZjTemplatePollingScheme, busiTemplatePollingScheme);
                List<BusiMcuZjTemplatePollingScheme> busiTemplatePollingSchemes = busiMcuZjTemplatePollingSchemeService.selectBusiMcuZjTemplatePollingSchemeList(busiMcuZjTemplatePollingScheme);
                if (CollectionUtils.isNotEmpty(busiTemplatePollingSchemes)) {
                    return success(busiTemplatePollingSchemes.get(0));
                }
            }
            case MCU_PLC: {
                busiTemplatePollingScheme.setSchemeName(SCHEME_NAME);
                busiTemplatePollingScheme.setEnableStatus(1);
                BusiMcuPlcTemplatePollingScheme busiMcuPlcTemplatePollingScheme = new BusiMcuPlcTemplatePollingScheme();
                BeanUtils.copyBeanProp(busiMcuPlcTemplatePollingScheme, busiTemplatePollingScheme);
                List<BusiMcuPlcTemplatePollingScheme> busiTemplatePollingSchemes = busiMcuPlcTemplatePollingSchemeService.selectBusiMcuPlcTemplatePollingSchemeList(busiMcuPlcTemplatePollingScheme);
                if (CollectionUtils.isNotEmpty(busiTemplatePollingSchemes)) {
                    return success(busiTemplatePollingSchemes.get(0));
                }
            }
            case MCU_KDC: {
                busiTemplatePollingScheme.setSchemeName(SCHEME_NAME);
                busiTemplatePollingScheme.setEnableStatus(1);
                BusiMcuKdcTemplatePollingScheme busiMcuKdcTemplatePollingScheme = new BusiMcuKdcTemplatePollingScheme();
                BeanUtils.copyBeanProp(busiMcuKdcTemplatePollingScheme, busiTemplatePollingScheme);
                List<BusiMcuKdcTemplatePollingScheme> busiTemplatePollingSchemes = busiMcuKdcTemplatePollingSchemeService.selectBusiMcuKdcTemplatePollingSchemeList(busiMcuKdcTemplatePollingScheme);
                if (CollectionUtils.isNotEmpty(busiTemplatePollingSchemes)) {
                    return success(busiTemplatePollingSchemes.get(0));
                }
            }
        }


        return success(null);
    }

    /**
     * 修改轮询方案
     */
    @Log(title = "轮询方案", businessType = BusinessType.UPDATE)
    @PostMapping("/editTemplatePollingScheme")
    @Operation(summary = "修改轮询方案")
    public RestResponse edit(@RequestBody JSONObject jsonObj, @RequestParam("conferenceId") String conferenceId) {
        Long id = jsonObj.getLong("id");
        Assert.isTrue(id != null, "轮询id不能为空");
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                BusiTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiTemplatePollingScheme.class);
                if (templatePollingScheme == null) {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }
                templatePollingScheme.setId(id);
                templatePollingScheme.setPollingStrategy(1);
                List<BusiTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                // 部门顺序
                List<BusiTemplatePollingDept> templatePollingDepts = new ArrayList<>();

                return toAjax(busiTemplatePollingSchemeService.updateBusiTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case MCU_ZJ: {
                BusiMcuZjTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuZjTemplatePollingScheme.class);
                if (templatePollingScheme == null) {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }
                templatePollingScheme.setId(id);
                templatePollingScheme.setPollingStrategy(1);
                List<BusiMcuZjTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                // 部门顺序
                List<BusiMcuZjTemplatePollingDept> templatePollingDepts = new ArrayList<>();

                return toAjax(busiMcuZjTemplatePollingSchemeService.updateBusiMcuZjTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case MCU_PLC: {
                BusiMcuPlcTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuPlcTemplatePollingScheme.class);
                if (templatePollingScheme == null) {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }
                templatePollingScheme.setId(id);
                templatePollingScheme.setPollingStrategy(1);
                List<BusiMcuPlcTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                // 部门顺序
                List<BusiMcuPlcTemplatePollingDept> templatePollingDepts = new ArrayList<>();

                return toAjax(busiMcuPlcTemplatePollingSchemeService.updateBusiMcuPlcTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
            case MCU_KDC: {
                BusiMcuKdcTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiMcuKdcTemplatePollingScheme.class);
                if (templatePollingScheme == null) {
                    throw new SystemException(1110098, "轮询方案不能为空！");
                }
                templatePollingScheme.setId(id);
                templatePollingScheme.setPollingStrategy(1);
                List<BusiMcuKdcTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
                // 部门顺序
                List<BusiMcuKdcTemplatePollingDept> templatePollingDepts = new ArrayList<>();

                return toAjax(busiMcuKdcTemplatePollingSchemeService.updateBusiMcuKdcTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
            }
        }
        return null;
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
        busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(DateUtils.addHours(date, 2), null));
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
        busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(DateUtils.addHours(date, 2), null));
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
        busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(DateUtils.addHours(date, 2), null));
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
        busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(DateUtils.addHours(date, 2), null));
        busiConferenceAppointment.setStatus(1);
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
     * 消息发送
     */
    @PostMapping("/sendMessage")
    @Operation(summary = "消息发送")
    public RestResponse sendMessage(@RequestBody JSONObject jsonObject) {
        String conferenceId = jsonObject.getString("conferenceId");
        Assert.isTrue(conferenceId != null, "会议ID不能为空");
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.sendMessage(conferenceId, jsonObject);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.sendMessage(conferenceId, jsonObject);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.sendMessage(conferenceId, jsonObject);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.sendMessage(conferenceId, jsonObject);
                break;
            }
        }

        return success();
    }


    /**
     * 轮询
     */
    @GetMapping("/polling")
    @Operation(summary = "轮询")
    public RestResponse polling(@RequestParam("conferenceId") String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.polling(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.polling(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.polling(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.polling(conferenceId);
                break;
            }
        }

        return success();
    }

    /**
     * 轮询暂停
     *
     * @author sinhy
     * @since 2022-04-11 10:09  void
     */
    @GetMapping("/pollingPause")
    @Operation(summary = "轮询暂停")
    public RestResponse pollingPause(@RequestParam("conferenceId") String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.pollingPause(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.pollingPause(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.pollingPause(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.pollingPause(conferenceId);
                break;
            }
        }

        return success();
    }

    /**
     * 轮询恢复运行
     *
     * @author sinhy
     * @since 2022-04-11 10:09  void
     */
    @GetMapping("/pollingResume")
    @Operation(summary = "轮询恢复运行")
    public RestResponse pollingResume(@RequestParam("conferenceId") String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.pollingResume(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.pollingResume(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.pollingResume(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.pollingResume(conferenceId);
                break;
            }
        }

        return success();
    }

    /**
     * 取消轮询
     */
    @GetMapping("/cancelPolling")
    @Operation(summary = "取消轮询")
    public RestResponse cancelPolling(@RequestParam("conferenceId") String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.cancelCallTheRoll(conferenceId);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.cancelPolling(conferenceId);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.cancelPolling(conferenceId);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.cancelPolling(conferenceId);
                break;
            }
        }

        return success();
    }

    /**
     * 批量邀请
     */
    @PostMapping("/batchInvite")
    @Operation(summary = "批量邀请")
    public RestResponse invite(@Valid @RequestBody BatchInviteVo batchInviteVo) {
        String conferenceId = batchInviteVo.getConferenceId();
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                List<Long> terminalIds = batchInviteVo.getTerminalIds();
                attendeeService.invite(conferenceId, terminalIds);
                break;
            }
            case MCU_ZJ: {
                List<Long> terminalIds = batchInviteVo.getTerminalIds();
                attendeeForMcuZjService.invite(conferenceId, terminalIds);
                break;
            }
            case MCU_PLC: {
                List<Long> terminalIds = batchInviteVo.getTerminalIds();
                attendeeForMcuPlcService.invite(conferenceId, terminalIds);
                break;
            }
            case MCU_KDC: {
                List<Long> terminalIds = batchInviteVo.getTerminalIds();
                attendeeForMcuKdcService.invite(conferenceId, terminalIds);
                break;
            }
        }

        return success();
    }

    /**
     * uri邀请
     */
    @PostMapping("/inviteByUri")
    @Operation(summary = "uri邀请")
    public RestResponse invite(@RequestBody JSONObject jsonObj) {
        String conferenceId = jsonObj.getString("conferenceId");
        Assert.isTrue(conferenceId != null, "会议ID不能为空");
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                attendeeService.invite(conferenceId, jsonObj);
                break;
            }
            case MCU_ZJ: {
                attendeeForMcuZjService.invite(conferenceId, jsonObj);
                break;
            }
            case MCU_PLC: {
                attendeeForMcuPlcService.invite(conferenceId, jsonObj);
                break;
            }
            case MCU_KDC: {
                attendeeForMcuKdcService.invite(conferenceId, jsonObj);
                break;
            }
        }

        return success();
    }

    /**
     * 查询直播地址
     *
     * @return
     */
    @GetMapping("/liveSetting/list")
    @Operation(summary = "查询直播地址")
    public RestResponse list(BusiLiveSetting busiLiveSetting) {
        busiLiveSetting.setDeptId(AuthenticationUtil.getDeptId());
        List<BusiLiveSetting> liveSettingList = iBusiLiveSettingService.selectBusiLiveSettingList(busiLiveSetting);
        return RestResponse.success(liveSettingList);
    }

    /**
     * 开启或关闭会议录制功能
     *
     * @param conferenceId 会议ID
     * @return
     */
    @PostMapping("/changeRecordingStatus/{conferenceId}")
    @Operation(summary = "开启或关闭会议录制功能")
    public RestResponse changeRecordingStatus(@PathVariable String conferenceId, @RequestBody JSONObject jsonObject) {
        Map<String, Object> recordFileInfo = new HashMap<>();
        try {
            boolean flag = jsonObject.getBoolean("recording");
            String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
            ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
            Long id = conferenceIdVo.getId();
            McuType mcuType = conferenceIdVo.getMcuType();
            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
            switch (mcuType) {
                case FME: {
                    ConferenceContext conferenceContext = (ConferenceContext) baseConferenceContext;
                    if (conferenceContext.isRecorded()) {
                        String coSpaceId = FmeDataCache.getCoSpaceByConferenceNumber(conferenceContext.getDeptId(), conferenceContext.getConferenceNumber()).getId();
                        recordFileInfo = busiRecordsService.getRecordFileInfo(conferenceContext.getDeptId(), coSpaceId, contextKey);
                    }
                    busiRecordsService.updateBusiRecords(flag, conferenceContext.getContextKey());
                    break;
                }
                case MCU_ZJ: {
                    busiRecordsForMcuZjService.updateBusiRecords(flag, contextKey);
                    break;
                }
                case MCU_PLC: {
                    busiRecordsForMcuPlcService.updateBusiRecords(flag, contextKey);
                    break;
                }
                case MCU_KDC: {
                    busiRecordsForMcuKdcService.updateBusiRecords(flag, contextKey);
                    break;
                }
            }

        } catch (Exception e) {
            return RestResponse.fail(e.getMessage());
        }
        return RestResponse.success(recordFileInfo);
    }

    /**
     * 开启或关闭会议直播功能
     *
     * @param conferenceId 会议ID
     * @return
     */
    @PostMapping("/changeStreamingStatus/{conferenceId}")
    @Operation(summary = "开启或关闭会议直播功能")
    public RestResponse changeStreamingStatus(@PathVariable String conferenceId, @RequestBody JSONObject jsonObject) {
        try {
            boolean enable = jsonObject.getBoolean("streaming");
            String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
            ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
            Long id = conferenceIdVo.getId();
            McuType mcuType = conferenceIdVo.getMcuType();
            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
            String streamingUrl = baseConferenceContext.getStreamingUrl();
            if (enable && !StringUtils.hasText(streamingUrl)) {
                return RestResponse.fail("未设置直播地址，无法开启直播！");
            }
            switch (mcuType) {
                case FME: {
                    busiConferenceService.stream(conferenceId, enable, streamingUrl);
                    break;
                }
                case MCU_ZJ: {
                    busiMcuZjConferenceService.stream(conferenceId, enable, streamingUrl);
                    break;
                }
                case MCU_PLC: {
                    busiMcuPlcConferenceService.stream(conferenceId, enable, streamingUrl);
                    break;
                }
                case MCU_KDC: {
                    busiMcuKdcConferenceService.stream(conferenceId, enable, streamingUrl);
                    break;
                }
            }

        } catch (Exception e) {
            return RestResponse.fail(e.getMessage());
        }
        return RestResponse.success();
    }

    @PutMapping("/extendMinutes/{conferenceId}/{minutes}")
    @Operation(summary = "延长会议时间，单位（分钟）")
    public RestResponse extendMinutes(@PathVariable String conferenceId, @PathVariable int minutes) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                busiConferenceService.extendMinutes(conferenceId, minutes);
                break;
            }
            case MCU_ZJ: {
                busiMcuZjConferenceService.extendMinutes(conferenceId, minutes);
                break;
            }
            case MCU_PLC: {
                busiMcuPlcConferenceService.extendMinutes(conferenceId, minutes);
                break;
            }
            case MCU_KDC: {
                busiMcuKdcConferenceService.extendMinutes(conferenceId, minutes);
                break;
            }
        }

        return success();
    }


    /**
     * 删除会议预约记录
     */
    @DeleteMapping("/{apConferenceId}")
    @Operation(summary = "删除会议预约记录")
    public RestResponse remove(@PathVariable String apConferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(apConferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                return toAjax(busiConferenceAppointmentService.deleteBusiConferenceAppointmentById(id));
            }
            case MCU_ZJ: {
                return toAjax(busiMcuZjConferenceAppointmentService.deleteBusiMcuZjConferenceAppointmentById(id));
            }
            case MCU_PLC: {
                return toAjax(busiMcuPlcConferenceAppointmentService.deleteBusiMcuPlcConferenceAppointmentById(id));
            }
            case MCU_KDC: {
                return toAjax(busiMcuKdcConferenceAppointmentService.deleteBusiMcuKdcConferenceAppointmentById(id));
            }
        }
        return RestResponse.fail();
    }

    /**
     * 修改会议预约记录
     */
    @PutMapping(value = "/{apConferenceId}")
    @Operation(summary = "修改会议预约记录")
    public RestResponse edit(@PathVariable("apConferenceId") String apConferenceId, @RequestBody MobileConferenceAppointmentRequest mobileConferenceAppointmentRequest) throws ParseException {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(apConferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                synchronized (MobileConferenceAppointmentController.class) {
                    BusiConferenceAppointment busiConferenceAppointment1 = busiConferenceAppointmentService.selectBusiConferenceAppointmentById(id);
                    if (busiConferenceAppointment1 != null) {
                        Long templateId = busiConferenceAppointment1.getTemplateId();
                        BusiTemplateConference templateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(templateId);
                        templateConference.setName(mobileConferenceAppointmentRequest.getConferenceName());
                        templateConference.setPresenter(mobileConferenceAppointmentRequest.getPresenter());
                        setParticipants(mobileConferenceAppointmentRequest);
                        List<BusiTemplateParticipant> busiTemplateParticipants = mobileConferenceAppointmentRequest.getTemplateParticipants();
                        Long masterTerminalId = mobileConferenceAppointmentRequest.getMasterTerminalId();
                        List<BusiTemplateDept> templateDepts = new ArrayList<>();

                        setRequestparams(mobileConferenceAppointmentRequest, busiConferenceAppointment1);
                        if (StringUtils.hasText(mobileConferenceAppointmentRequest.getStartTime())) {
                            busiConferenceAppointment1.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                        }

                        if (mobileConferenceAppointmentRequest.getType() != null) {
                            if (mobileConferenceAppointmentRequest.getType() == 1) {
                                busiConferenceAppointment1.setStartTime(mobileConferenceAppointmentRequest.getStartTime());
                            } else if (mobileConferenceAppointmentRequest.getType() == 2) {
                                BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
                                busiTemplateConference.setCreateUserId(SecurityUtils.getLoginUser().getUser().getUserId());
                                busiTemplateConference.setBusinessFieldType(100);
                                busiTemplateConference.setType(2);
                                checkType(busiTemplateConference);
                                busiConferenceAppointment1.setStatus(2);
                                busiConferenceAppointment1.setStartTime(com.sinhy.utils.DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", new Date()));
                            } else {
                                throw new CustomException("类型错误");
                            }
                        }

                        createMobileConferenceParamTemplate(busiConferenceAppointment1);
                        Boolean isMute = mobileConferenceAppointmentRequest.getIsMute();
                        try {
                            CoSpace coSpace = fmeCoSpaceService.getCoSpaceByConferenceNumber(templateConference.getDeptId(), templateConference.getConferenceNumber().toString());
                            String callLegProfile = coSpace.getCallLegProfile();
                            if (Strings.isNotBlank(callLegProfile)) {
                                FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(templateConference.getDeptId());
                                CallLegProfile clp = fmeBridge.getDataCache().getCallLegProfile(callLegProfile);
                                Boolean rxAudioMute = clp.getRxAudioMute();
                                if (!Objects.equals(rxAudioMute, isMute)) {
                                    // 更新入会参数
                                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                                    nameValuePairs.add(new BasicNameValuePair("rxAudioMute", isMute ? "true" : "false"));
                                    BeanFactory.getBean(IBusiCallLegProfileService.class).updateCallLegProfile(fmeBridge, callLegProfile, nameValuePairs);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        busiConferenceAppointmentService.updateBusiConferenceAppointment(busiConferenceAppointment1, false);
                        templateConference.setDefaultViewIsBroadcast(mobileConferenceAppointmentRequest.getDefaultViewIsBroadcast());
                        templateConference.setDefaultViewIsFill(mobileConferenceAppointmentRequest.getDefaultViewIsFill());
                        templateConference.setConferencePassword(mobileConferenceAppointmentRequest.getConferencePassword());
                        if (Strings.isNotBlank(mobileConferenceAppointmentRequest.getDefaultViewLayout())) {
                            templateConference.setDefaultViewLayout(mobileConferenceAppointmentRequest.getDefaultViewLayout());
                        }
                        int c = busiTemplateConferenceService.updateBusiTemplateConference(templateConference, masterTerminalId, busiTemplateParticipants, templateDepts);

                    }
                }
            }
            return RestResponse.success();
        }
        return RestResponse.fail();
    }

    /**
     * 获取会议预约记录详细信息
     */
    @GetMapping(value = "/{apConferenceId}")
    @Operation(summary = "获取会议预约记录详细信息")
    public RestResponse getInfo(@PathVariable("apConferenceId") String apConferenceId)
    {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(apConferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                BusiConferenceAppointment busiConferenceAppointment = busiConferenceAppointmentService.selectBusiConferenceAppointmentById(id);
                String conferenceId = EncryptIdUtil.generateEncryptId(busiConferenceAppointment.getTemplateId(), McuType.FME.getCode());
                busiConferenceAppointment.getParams().put("apConferenceId", apConferenceId);
                busiConferenceAppointment.getParams().put("conferenceId", conferenceId);
                ModelBean modelBean = busiTemplateConferenceService.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                boolean isPresenter = false;
                boolean isMyConference = false;
                if (!Objects.isNull(modelBean)) {
                    Object obj = modelBean.get("templateConference");
                    String jsonString = JSON.toJSONString(obj);
                    BusiTemplateConference templateConference = JSON.parseObject(jsonString, BusiTemplateConference.class);
                    try {
                        CoSpace coSpace = fmeCoSpaceService.getCoSpaceByConferenceNumber(templateConference.getDeptId(), templateConference.getConferenceNumber().toString());
                        String callLegProfile = coSpace.getCallLegProfile();
                        if (Strings.isNotBlank(callLegProfile)) {
                            FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(templateConference.getDeptId());
                            CallLegProfile clp = fmeBridge.getDataCache().getCallLegProfile(callLegProfile);
                            if (clp == null) {
                                busiConferenceAppointment.getParams().put("isMute", false);
                            } else {
                                busiConferenceAppointment.getParams().put("isMute", clp.getRxAudioMute());
                            }
                            busiConferenceAppointment.getParams().put("isMute", clp.getRxAudioMute());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    busiConferenceAppointment.setPassword(templateConference.getConferencePassword());
                    if (templateConference.getPresenter() != null && templateConference.getPresenter().longValue() == loginUser.getUser().getUserId().longValue()) {
                        isPresenter = true;
                    }
                    if (templateConference.getCreateUserId() != null && templateConference.getCreateUserId().longValue() == loginUser.getUser().getUserId().longValue()) {
                        isMyConference = true;
                    }
                    busiConferenceAppointment.getParams().put("conferenceNumber", templateConference.getConferenceNumber());
                    busiConferenceAppointment.getParams().put("conferencePassword", templateConference.getConferencePassword());
                    busiConferenceAppointment.getParams().put("defaultViewIsDisplaySelf", templateConference.getDefaultViewIsDisplaySelf());
                    busiConferenceAppointment.getParams().put("defaultViewLayout", templateConference.getDefaultViewLayout());
                    busiConferenceAppointment.getParams().put("conferenceName", templateConference.getName());
                    busiConferenceAppointment.getParams().put("defaultViewIsBroadcast", templateConference.getDefaultViewIsBroadcast());
                    busiConferenceAppointment.getParams().put("defaultViewIsFill", templateConference.getDefaultViewIsFill());
                    busiConferenceAppointment.setCreateBy(templateConference.getCreateUserId() + "");
                    busiConferenceAppointment.getParams().put("createUserId", templateConference.getCreateUserId());
                    busiConferenceAppointment.getParams().put("createUserName", templateConference.getCreateUserName());

                    {
                        ConferenceContext conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(templateConference.getId());
                        JSONObject json = null;
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            json = JSONObject.parseObject(objectMapper.writeValueAsString(conferenceContext));
                        } catch (Exception e) {
                        }

                        busiConferenceAppointment.getParams().put("templateInfo", json);
                    }

                }
                busiConferenceAppointment.getParams().put("isPresenter", isPresenter);
                busiConferenceAppointment.getParams().put("isMyConference", isMyConference);

                return RestResponse.success(busiConferenceAppointment);
            }
        }
        return RestResponse.fail();
    }



}

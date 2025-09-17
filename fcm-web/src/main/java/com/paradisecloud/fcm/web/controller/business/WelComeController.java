/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WelComeController.java
 * Package     : com.paradisecloud.fcm.web.controller.business
 * @author lilinhai
 * @since 2021-06-02 11:27
 * @version  V1.0
 */
package com.paradisecloud.fcm.web.controller.business;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrReportResultService;
import com.paradisecloud.fcm.cdr.service.interfaces.report.IConferenceReportService;
import com.paradisecloud.fcm.cdr.service.interfaces.report.ITerminalReportService;
import com.paradisecloud.fcm.common.constant.ConfigConstant;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceApprovalMapper;
import com.paradisecloud.fcm.dao.mapper.ViewConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.fme.service.interfaces.IRecordingService;
import com.paradisecloud.fcm.mcu.kdc.cache.DeptMcuKdcMappingCache;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcDeptService;
import com.paradisecloud.fcm.mcu.plc.cache.DeptMcuPlcMappingCache;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcDeptService;
import com.paradisecloud.fcm.mcu.zj.cache.DeptMcuZjMappingCache;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjDeptService;
import com.paradisecloud.fcm.mqtt.cache.MqttBridgeCache;
import com.paradisecloud.fcm.mqtt.cache.MqttClusterCache;
import com.paradisecloud.fcm.mqtt.cache.MqttDeptMappingCache;
import com.paradisecloud.fcm.mqtt.enums.MqttType;
import com.paradisecloud.fcm.mqtt.model.MqttBridge;
import com.paradisecloud.fcm.mqtt.model.MqttBridgeCluster;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.utils.AllConferenceContextUtils;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberSectionService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.cache.ZjAccountCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmAccountCacheAndUtils;
import com.paradisecloud.fcm.web.service.interfaces.IWelcomeService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.service.ISysConfigService;
import com.paradisecloud.system.service.ISysDeptService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/busi/welcomepage")
@Tag(name = "首页控制器")
public class WelComeController extends BaseController {

    @Resource
    private IWelcomeService welcomeService;
    @Resource
    private ICdrReportResultService iCdrReportResultService;
    @Resource
    private IRecordingService iRecordingService;
    @Resource
    private ISysDeptService sysDeptService;
    @Resource
    private IBusiMcuZjDeptService busiMcuZjDeptService;
    @Resource
    private IBusiConferenceNumberSectionService busiConferenceNumberSectionService;
    @Resource
    private IBusiMcuPlcDeptService busiMcuPlcDeptService;
    @Resource
    private IBusiMcuKdcDeptService busiMcuKdcDeptService;
    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;
    @Resource
    private ViewConferenceAppointmentMapper viewConferenceAppointmentMapper;
    @Resource
    private BusiConferenceApprovalMapper busiConferenceApprovalMapper;
    @Resource
    private ISysConfigService sysConfigService;
    @Resource
    private IConferenceReportService conferenceReportService;
    @Resource
    private ITerminalReportService terminalReportService;

    /**
     * 查询会议号段列表
     */
    @GetMapping("/conferenceStat")
    @Operation(summary = "首页会议卡片统计")
    public RestResponse conferenceStat() {
        JSONObject json = new JSONObject();
        int activeConferenceCount = 0;
        int appointConferenceCount = 0;
        int deptTemplateCount = 0;
        int approvalCount = 0;

                // 全会议
        {
            Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();

            if (deptId != null) {
                ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                viewTemplateConferenceCon.setDeptId(deptId);
                deptTemplateCount += viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon).size();

                SysDept con3 = new SysDept();
                con3.setDeptId(deptId);
                List<SysDept> sds = sysDeptService.selectDeptList(con3);

                int c = 0;
                Collection<BaseConferenceContext> cc = AllConferenceContextCache.getInstance().values();
                for (Iterator<BaseConferenceContext> iterator = cc.iterator(); iterator.hasNext(); ) {
                    BaseConferenceContext conferenceContext = iterator.next();
                    for (SysDept sysDept : sds) {
                        if (conferenceContext.getDeptId().longValue() == sysDept.getDeptId().longValue()) {
                            c++;
                            break;
                        }
                    }
                }

                // 活跃会议室的数量
                activeConferenceCount += c;

                ViewConferenceAppointment viewConferenceAppointmentCon = new ViewConferenceAppointment();
                viewConferenceAppointmentCon.setDeptId(deptId);

                // 预约会议数
                appointConferenceCount += viewConferenceAppointmentMapper.selectViewConferenceAppointmentList(viewConferenceAppointmentCon).size();                // 待审批数
                BusiConferenceApproval busiConferenceApprovalCon = new BusiConferenceApproval();
                if (deptId > 100) {
                    busiConferenceApprovalCon.setDeptId(deptId);
                }
                busiConferenceApprovalCon.setApprovalStatus(0);
                approvalCount += busiConferenceApprovalMapper.selectBusiConferenceApprovalList(busiConferenceApprovalCon).size();
            } else {
                // 活跃会议室的数量
                activeConferenceCount += AllConferenceContextCache.getInstance().size();
                ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                deptTemplateCount += viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon).size();

                // 预约会议数
                ViewConferenceAppointment viewConferenceAppointmentCon = new ViewConferenceAppointment();
                appointConferenceCount += viewConferenceAppointmentMapper.selectViewConferenceAppointmentList(viewConferenceAppointmentCon).size();
                // 待审批数
                BusiConferenceApproval busiConferenceApprovalCon = new BusiConferenceApproval();
                busiConferenceApprovalCon.setApprovalStatus(0);
                approvalCount += busiConferenceApprovalMapper.selectBusiConferenceApprovalList(busiConferenceApprovalCon).size();
            }

            json.put("deptTemplateCount", deptTemplateCount);
            json.put("activeConferenceCount", activeConferenceCount);
            json.put("appointConferenceCount", appointConferenceCount);
            String conferenceApprovalEnable = sysConfigService.selectConfigByKey(ConfigConstant.CONFIG_KEY_CONFERENCE_APPROVAL_ENABLE);
            if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
                json.put("approvalCount", approvalCount);
            }
        }
        return RestResponse.success(json);
    }


    @GetMapping("/activeConferences")
    @Operation(summary = "首页活跃会议室列表")
    public RestResponse activeConferences() {
        List<JSONObject> jsons = new ArrayList<>();

        // 全会议
        {
            Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();

            Collection<BaseConferenceContext> cc = AllConferenceContextCache.getInstance().values();
            if (deptId != null) {
                SysDept con = new SysDept();
                con.setDeptId(deptId);
                List<SysDept> sds = sysDeptService.selectDeptList(con);
                for (Iterator<BaseConferenceContext> iterator = cc.iterator(); iterator.hasNext(); ) {
                    BaseConferenceContext conferenceContext = iterator.next();
                    for (SysDept sysDept : sds) {
                        if (conferenceContext.getDeptId().longValue() == sysDept.getDeptId().longValue()) {
                            ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(conferenceContext.getMcuType(), conferenceContext.getTemplateConferenceId());
                            if (viewTemplateConference != null) {
                                JSONObject json = toJson(conferenceContext);
                                if (!jsons.contains(json)) {
                                    jsons.add(json);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                for (Iterator<BaseConferenceContext> iterator = cc.iterator(); iterator.hasNext(); ) {
                    BaseConferenceContext conferenceContext = iterator.next();
                    ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(conferenceContext.getMcuType(), conferenceContext.getTemplateConferenceId());
                    if (viewTemplateConference != null) {
                        JSONObject json = toJson(conferenceContext);
                        if (!jsons.contains(json)) {
                            jsons.add(json);
                        }
                    }
                }
            }
        }
        return RestResponse.success(jsons);
    }

    private JSONObject toJson(BaseConferenceContext conferenceContext) {
        JSONObject json = new JSONObject();
        json.put("conferenceId", conferenceContext.getId());
        json.put("mcuType", conferenceContext.getMcuType());
        json.put("mcuTypeAlias", conferenceContext.getMcuTypeAlias());
        json.put("conferenceName", conferenceContext.getName());
        json.put("tenantId", conferenceContext.getTenantId());
        json.put("conferenceNumber", conferenceContext.getConferenceNumber());
        json.put("templateId", conferenceContext.getTemplateConferenceId());
        json.put("bindwidth", conferenceContext.getBandwidth());

        AtomicInteger as = new AtomicInteger();
        AtomicInteger inMeetings = new AtomicInteger();
        AllConferenceContextUtils.eachNonMcuAttendeeInConference(conferenceContext, (a) -> {
            as.incrementAndGet();
            if (a.isMeetingJoined()) {
                inMeetings.incrementAndGet();
            }
        });

        json.put("terminalCount", as.get());
        json.put("inMeetingTerminalCount", inMeetings.get());
        json.put("conferenceStartTime", conferenceContext.getStartTime());
        json.put("masterName", conferenceContext.getMasterAttendee() != null ? conferenceContext.getMasterAttendee().getName() : null);
        return json;
    }

    @GetMapping("/activeConferencesPages")
    @Operation(summary = "首页活跃会议室列表")
    public RestResponse activeConferencesPages(@RequestParam(value = "searchKey", required = false) String searchKey,
                                               @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
                                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<JSONObject> jsons = new ArrayList<>();
        PageHelper.startPage(pageIndex, pageSize);
        List<ViewTemplateConference> viewTemplateConferences = viewTemplateConferenceMapper.selectAllViewTemplateConferenceListByKey(searchKey, null);
        if (!CollectionUtils.isEmpty(viewTemplateConferences)) {
            for (ViewTemplateConference viewTemplateConference : viewTemplateConferences) {
                ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(viewTemplateConference.getConferenceId());
                if (conferenceContext != null) {
                    JSONObject json = toJson(conferenceContext);
                    jsons.add(json);
                }
            }
        }

        return RestResponse.success(0, "查询成功", new PageInfo<>(jsons));
    }

    @GetMapping("/terminalStat")
    @Operation(summary = "终端统计")
    public RestResponse terminalStat() {
        JSONObject json = new JSONObject();
        int total = 0;
        int onlineCount = 0;
        int meetingCount = 0;

        // 全会议
        {
            Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
            if (deptId != null)
            {
                Set<Long> deptIds = SysDeptCache.getInstance().getSubordinateDeptIds(deptId);
                Collection<BusiTerminal> bts = TerminalCache.getInstance().values();
                for (Iterator<BusiTerminal> iterator = bts.iterator(); iterator.hasNext();)
                {
                    BusiTerminal busiTerminal = iterator.next();
                    if (deptIds.contains(busiTerminal.getDeptId()))
                    {
                        total++;
                        if (TerminalOnlineStatus.convert(busiTerminal.getOnlineStatus()) == TerminalOnlineStatus.ONLINE)
                        {
                            onlineCount++;
                        }
                    }
                }

                Collection<BaseConferenceContext> cc = AllConferenceContextCache.getInstance().values();
                for (Iterator<BaseConferenceContext> iterator0 = cc.iterator(); iterator0.hasNext();)
                {
                    BaseConferenceContext conferenceContext = iterator0.next();
                    AtomicInteger ai = new AtomicInteger();
                    AllConferenceContextUtils.eachNonMcuAttendeeInConference(conferenceContext, (a)->{
                        if (a.getTerminalId() != null && deptIds.contains(a.getDeptId()))
                        {
                            ai.incrementAndGet();
                        }
                    });
                    meetingCount += ai.get();
                }
            }
            else
            {
                Collection<BusiTerminal> bts = TerminalCache.getInstance().values();
                for (Iterator<BusiTerminal> iterator = bts.iterator(); iterator.hasNext();)
                {
                    total++;
                    BusiTerminal busiTerminal = iterator.next();
                    if (TerminalOnlineStatus.convert(busiTerminal.getOnlineStatus()) == TerminalOnlineStatus.ONLINE)
                    {
                        onlineCount++;
                    }
                }

                Collection<BaseConferenceContext> cc = AllConferenceContextCache.getInstance().values();
                for (Iterator<BaseConferenceContext> iterator0 = cc.iterator(); iterator0.hasNext();)
                {
                    BaseConferenceContext conferenceContext = iterator0.next();
                    AtomicInteger ai = new AtomicInteger();
                    AllConferenceContextUtils.eachNonMcuAttendeeInConference(conferenceContext, (a)->{
                        if (a instanceof TerminalAttendee)
                        {
                            ai.incrementAndGet();
                        }
                    });
                    meetingCount += ai.get();
                }
            }

            json.put("total", total);
            json.put("onlineCount", onlineCount);
            json.put("meetingCount", meetingCount);
        }
        return RestResponse.success(json);
    }

    @GetMapping("/tenantResource")
    @Operation(summary = "租户资源信息")
    public RestResponse tenantResource() {
        JSONObject jsonObject = welcomeService.tenantResource();
        Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        if (deptId == null) {
            return RestResponse.success(jsonObject);
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

        // MCU-ZJ
        {
            BusiMcuZjDept bindMcu = DeptMcuZjMappingCache.getInstance().getBindMcu(deptId);
            if (bindMcu != null) {
                jsonObject.put("bindMcuZjInfo", busiMcuZjDeptService.toModelBean(bindMcu));
            } else {
                jsonObject.put("bindMcuZjInfo", null);
            }

            List<ModelBean> modelBeans = getBindZjBusiConferenceNumberSection(deptId);
            if (ObjectUtils.isEmpty(modelBeans)) {
                jsonObject.put("bindConferenceNumberSectionZjInfo", null);
            } else {
                jsonObject.put("bindConferenceNumberSectionZjInfo", modelBeans);
            }

            List<BusiZjNumberSection> busiZjNumberSectionList = ZjAccountCache.getInstance().getZjAccountByDeptId(deptId);
            if (busiZjNumberSectionList != null && busiZjNumberSectionList.size() > 0) {
                jsonObject.put("bindZjAccount", busiZjNumberSectionList);
            } else {
                jsonObject.put("bindZjAccount", null);
            }
        }

        // MCU-PLC
        {
            BusiMcuPlcDept bindMcu = DeptMcuPlcMappingCache.getInstance().getBindMcu(deptId);
            if (bindMcu != null) {
                jsonObject.put("bindMcuPlcInfo", busiMcuPlcDeptService.toModelBean(bindMcu));
            } else {
                jsonObject.put("bindMcuPlcInfo", null);
            }
        }

        // MCU-KDC
        {
            BusiMcuKdcDept bindMcu = DeptMcuKdcMappingCache.getInstance().getBindMcu(deptId);
            if (bindMcu != null) {
                jsonObject.put("bindMcuKdcInfo", busiMcuKdcDeptService.toModelBean(bindMcu));
            } else {
                jsonObject.put("bindMcuKdcInfo", null);
            }
        }

        return RestResponse.success(jsonObject);
    }

    /**
     * 首页会议报表统计(会议时长、终端总数、会议次数)
     */
    @GetMapping("/reportConferenceOfIndex")
    @Operation(summary = "首页会议报表统计(会议时长、终端总数、会议次数)")
    public RestResponse reportConferenceOfIndex(@RequestParam(required = false) Long deptId, @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime) {
        Map<String, Object> map = iCdrReportResultService.reportConferenceOfIndex(deptId, startTime, endTime);
        return RestResponse.success(map);
    }

    /**
     * 首页录制文件空间统计
     */
    @GetMapping("/reportRecordSpace")
    @Operation(summary = "首页录制文件空间统计")
    public RestResponse reportRecordSpace(@RequestParam(required = false) Long deptId) {
        Map<String, Object> map = iRecordingService.reportRecordSpace(deptId);
        return RestResponse.success(map);
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

//        List<MqttBridge> availableFcmBridges = busiMqttClusterById.getAvailableMqttBridges();
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

    public List<ModelBean> getBindZjBusiConferenceNumberSection(Long deptId) {
        BusiConferenceNumberSection con = new BusiConferenceNumberSection();
        con.setDeptId(deptId);
        List<ModelBean> modelBeans = busiConferenceNumberSectionService.selectBusiConferenceNumberSectionList(con);
        if (modelBeans.isEmpty() || modelBeans.size() == 0) {
            SysDept sysDept = SysDeptCache.getInstance().get(deptId);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0) {
                return getBindZjBusiConferenceNumberSection(sysDept.getParentId());
            } else {
                return null;
            }
        } else {
            return modelBeans;
        }
    }
    /**
     * 首页历史会议统计
     */
    @GetMapping("/reportHistory")
    @Operation(summary = "首页历史会议统计")
    public RestResponse reportHistoryConference()
    {
        Map<String, Object> historyMap = new HashMap<>();
        // 会议日报
        {
            Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
            ReportSearchVo reportSearchVo = new ReportSearchVo();
            if (deptId != null && deptId > 100) {
                reportSearchVo.setDeptId(deptId);
            }
            Date endTime = new Date();
            reportSearchVo.setEndTime(endTime);
            Date startTime = DateUtils.getDiffDate(-7, TimeUnit.DAYS);
            reportSearchVo.setStartTime(startTime);
            List<Map<String, Object>> list = conferenceReportService.selectConferenceNumOfDay(reportSearchVo, true);
            historyMap.put("conferenceDays", list);
        }
        // 会议月报
        {
            Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
            ReportSearchVo reportSearchVo = new ReportSearchVo();
            if (deptId != null && deptId > 100) {
                reportSearchVo.setDeptId(deptId);
            }
            Date endTime = new Date();
            reportSearchVo.setEndTime(endTime);
            Date startTime = DateUtils.getDiffDate(-180, TimeUnit.DAYS);
            reportSearchVo.setStartTime(startTime);
            List<Map<String, Object>> list = conferenceReportService.selectConferenceNumOfDay(reportSearchVo, 1, false);
            historyMap.put("conferenceMonths", list);
        }
        // 终端日报
        {
            Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
            ReportSearchVo reportSearchVo = new ReportSearchVo();
            if (deptId != null && deptId > 100) {
                reportSearchVo.setDeptId(deptId);
            }
            Date endTime = new Date();
            reportSearchVo.setEndTime(endTime);
            Date startTime = DateUtils.getDiffDate(-7, TimeUnit.DAYS);
            reportSearchVo.setStartTime(startTime);
            List<Map<String, Object>> list = terminalReportService.selectTerminalNumOfDay(reportSearchVo);
            historyMap.put("terminalDays", list);
        }
        // 终端月报
        {
            Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
            ReportSearchVo reportSearchVo = new ReportSearchVo();
            if (deptId != null && deptId > 100) {
                reportSearchVo.setDeptId(deptId);
            }
            Date endTime = new Date();
            reportSearchVo.setEndTime(endTime);
            Date startTime = DateUtils.getDiffDate(-180, TimeUnit.DAYS);
            reportSearchVo.setStartTime(startTime);
            List<Map<String, Object>> list = terminalReportService.selectTerminalNumOfDay(reportSearchVo, 1);
            historyMap.put("terminalMonths", list);
        }
        return RestResponse.success(historyMap);
    }
}

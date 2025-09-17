package com.paradisecloud.fcm.web.controller.mobile.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.ViewConferenceAppointmentVo;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiMcuDingConferenceAppointmentService;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiMcuDingTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceAppointmentService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudConferenceAppointmentService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudTemplateConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.StartTemplateConference;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcBridgeCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcTemplateConferenceService;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcBridgeCache;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridge;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcTemplateConferenceService;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjTemplateConferenceService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.task.StartDownCascadeConferenceTask;
import com.paradisecloud.fcm.service.task.NotifyTask;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomBookService;
import com.paradisecloud.fcm.smc2.cache.Smc2Bridge;
import com.paradisecloud.fcm.smc2.cache.Smc2BridgeCache;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2ConferenceAppointmentService;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2TemplateConferenceService;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiSmc2ConferenceService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentConferenceAppointmentService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentTemplateConferenceService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiTencentConferenceService;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteConferenceAppointmentService;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteConferenceService;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteTemplateConferenceService;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.model.request.TemplateNode;
import com.paradisecloud.smc3.model.request.TemplateNodeTemp;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3ConferenceAppointmentService;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3TemplateConferenceService;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3ConferenceService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 会议预约记录Controller
 *
 * @author lilinhai
 * @date 2021-05-24
 */
@RestController
@RequestMapping("/mobileWeb/mcu/all/conferenceAppointment")
@Tag(name = "会议预约记录")
public class MobileNewWebConferenceAppointmentController extends BaseController
{
    @Resource
    private ViewConferenceAppointmentMapper viewConferenceAppointmentMapper;
    @Resource
    private IBusiConferenceAppointmentService busiConferenceAppointmentService;
    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;
    @Resource
    private IBusiMcuZjConferenceAppointmentService busiMcuZjConferenceAppointmentService;
    @Resource
    private IBusiMcuZjConferenceService busiMcuZjConferenceService;
    @Resource
    private IBusiMcuPlcConferenceAppointmentService busiMcuPlcConferenceAppointmentService;
    @Resource
    private IBusiMcuZteConferenceAppointmentService busiMcuZteConferenceAppointmentService;
    @Resource
    private IBusiMcuPlcConferenceService busiMcuPlcConferenceService;

    @Resource
    private IBusiMcuZteConferenceService busiMcuZteConferenceService;
    @Resource
    private IBusiMcuKdcConferenceAppointmentService busiMcuKdcConferenceAppointmentService;
    @Resource
    private IBusiMcuDingConferenceAppointmentService busiMcuDingConferenceAppointmentService;
    @Resource
    private IBusiMcuKdcConferenceService busiMcuKdcConferenceService;
    @Resource
    private IBusiMcuSmc3ConferenceAppointmentService busiMcuSmc3ConferenceAppointmentService;
    @Resource
    private IBusiSmc3ConferenceService busiSmc3ConferenceService;
    @Resource
    private IBusiMcuSmc2ConferenceAppointmentService busiMcuSmc2ConferenceAppointmentService;
    @Resource
    private IBusiSmc2ConferenceService busiSmc2ConferenceService;
    @Resource
    private IBusiTencentConferenceService busiTencentConferenceService;
    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;
    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;
    @Resource
    private IBusiMcuZjTemplateConferenceService busiMcuZjTemplateConferenceService;
    @Resource
    private IBusiMcuPlcTemplateConferenceService busiMcuPlcTemplateConferenceService;
    @Resource
    private IBusiMcuZteTemplateConferenceService busiMcuZteTemplateConferenceService;
    @Resource
    private IBusiMcuKdcTemplateConferenceService busiMcuKdcTemplateConferenceService;
    @Resource
    private IBusiMcuSmc3TemplateConferenceService busiMcuSmc3TemplateConferenceService;
    @Resource
    private IBusiMcuSmc2TemplateConferenceService busiMcuSmc2TemplateConferenceService;
    @Resource
    private IBusiMcuTencentTemplateConferenceService busiMcuTencentTemplateConferenceService;
    @Resource
    private IBusiMcuDingTemplateConferenceService busiMcuDingTemplateConferenceService;
    @Resource
    private IBusiMcuHwcloudTemplateConferenceService busiMcuHwcloudTemplateConferenceService;
    @Resource
    private IBusiMcuHwcloudConferenceAppointmentService busiMcuHwcloudConferenceAppointmentService;
    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;
    @Resource
    private BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper;
    @Resource
    private BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper;
    @Resource
    private BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper;
    @Resource
    private BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper;
    @Resource
    private BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper;
    @Resource
    private BusiMcuSmc2TemplateConferenceMapper busiMcuSmc2TemplateConferenceMapper;
    @Resource
    private BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper;
    @Resource
    private BusiMcuDingTemplateConferenceMapper busiMcuDingTemplateConferenceMapper;
    @Resource
    private BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper;
    @Resource
    private BusiConferenceApprovalMapper busiConferenceApprovalMapper;
    @Resource
    private TaskService taskService;
    @Resource
    private ViewTemplateParticipantMapper viewTemplateParticipantMapper;

    @Resource
    private IBusiSmartRoomBookService busiSmartRoomBookService;

    @Resource
    private IBusiMcuTencentConferenceAppointmentService busiMcuTencentConferenceAppointmentService;
    public  static String regex = "^\\d{4,6}$";
    public  static String regex_chair = "^\\d{6}$";
    /**
     * 获取部门会议模板计数
     */
    @GetMapping(value = "/getDeptRecordCounts/{businessFieldType}")
    @Operation(summary = "获取部门预约会议计数")
    public RestResponse getDeptRecordCounts(@PathVariable("businessFieldType") Integer businessFieldType)
    {
        List<DeptRecordCount> deptRecordCountList = viewConferenceAppointmentMapper.getDeptRecordCounts(businessFieldType);
        Map<Long, Map<String, Long>> deptCountMap = new HashMap<>();
        for (DeptRecordCount deptRecordCountTemp : deptRecordCountList) {
            Long deptIdTemp = deptRecordCountTemp.getDeptId();
            Long count = deptRecordCountTemp.getCount().longValue();
            {
                Map<String, Long> deptMap = deptCountMap.get(deptIdTemp);
                if (deptMap == null) {
                    deptMap = new HashMap<>();
                    deptMap.put("deptId", deptIdTemp);
                    deptMap.put("count", count);
                    deptMap.put("totalCount", count);
                    deptCountMap.put(deptIdTemp, deptMap);
                } else {
                    Long countExist = deptMap.get("count");
                    Long totalCountExist = deptMap.get("totalCount");
                    deptMap.put("count", countExist + count);
                    deptMap.put("totalCount", totalCountExist + count);
                }
            }
            SysDept sysDept = SysDeptCache.getInstance().get(deptIdTemp);
            if (sysDept != null) {
                String ancestors = sysDept.getAncestors();
                if (StringUtils.isNotEmpty(ancestors)) {
                    String[] deptIdArr = ancestors.split(",");
                    for (String deptIdStr : deptIdArr) {
                        Long deptIdT = null;
                        try {
                            deptIdT = Long.valueOf(deptIdStr);
                        } catch (Exception e) {
                        }
                        if (deptIdT != null) {
                            if (deptIdT.longValue() == deptIdTemp.longValue()) {
                                continue;
                            }
                            Map<String, Long> deptMap = deptCountMap.get(deptIdT);
                            if (deptMap == null) {
                                deptMap = new HashMap<>();
                                deptMap.put("deptId", deptIdT);
                                if (deptIdT.longValue() == deptIdTemp.longValue()) {
                                    deptMap.put("count", count);
                                } else {
                                    deptMap.put("count", 0L);
                                }
                                deptMap.put("totalCount", count);
                                deptCountMap.put(deptIdT, deptMap);
                            } else {
                                Long countExist = deptMap.get("count");
                                Long totalCountExist = deptMap.get("totalCount");
                                if (deptIdT.longValue() == deptIdTemp.longValue()) {
                                    deptMap.put("count", countExist + count);
                                }
                                deptMap.put("totalCount", totalCountExist + count);
                            }
                        }
                    }
                }
            }
        }
        return RestResponse.success(deptCountMap.values());
    }

    /**
     * 查询会议预约记录列表
     */
    @PostMapping(value = "/list")
    @Operation(summary = "查询会议预约记录列表")
    public RestResponse list(@RequestBody ViewConferenceAppointment viewConferenceAppointment)
    {
        startPage();
        PaginationData<ViewConferenceAppointmentVo> paginationData = new PaginationData<>();
        List<ViewConferenceAppointment> list = viewConferenceAppointmentMapper.selectViewConferenceAppointmentList(viewConferenceAppointment);
        for (ViewConferenceAppointment viewConferenceAppointmentTemp : list) {
            ViewConferenceAppointmentVo viewConferenceAppointmentVo = new ViewConferenceAppointmentVo();
            BeanUtils.copyProperties(viewConferenceAppointmentTemp, viewConferenceAppointmentVo);
            if (viewConferenceAppointmentTemp.getApprovalId() != null) {
                BusiConferenceApproval busiConferenceApproval = busiConferenceApprovalMapper.selectBusiConferenceApprovalById(viewConferenceAppointmentTemp.getApprovalId());
                if (busiConferenceApproval != null) {
                    if (busiConferenceApproval.getApprovalStatus() == 0) {
                        viewConferenceAppointmentVo.setStatus(3);
                    } else if (busiConferenceApproval.getApprovalStatus() == 2) {
                        viewConferenceAppointmentVo.setStatus(4);
                    }
                    viewConferenceAppointmentVo.setApprovalFailReason(busiConferenceApproval.getApprovalFailReason());
                }
            }
            viewConferenceAppointmentVo.setMcuTypeAlias(McuType.convert(viewConferenceAppointmentVo.getMcuType()).getAlias());
            paginationData.addRecord(viewConferenceAppointmentVo);
        }
        PageInfo pageInfo = new PageInfo(list);
        paginationData.setSize(pageInfo.getSize());
        paginationData.setPage(pageInfo.getPageNum());
        paginationData.setTotal(pageInfo.getTotal());

        return RestResponse.success(paginationData);
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
                BusiConferenceAppointment busiConferenceAppointment = busiConferenceAppointmentService.selectBusiConferenceAppointmentById(id);
                if (busiConferenceAppointment != null) {
                    String generateEncryptId = EncryptIdUtil.generateEncryptId(busiConferenceAppointment.getTemplateId(), McuType.FME.getCode());
                    ModelBean templateInfo = getTemplateInfo(generateEncryptId);
                    busiConferenceAppointment.setParams(templateInfo);
                    return success(busiConferenceAppointment);
                }
            }
            case MCU_ZJ: {
                BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = busiMcuZjConferenceAppointmentService.selectBusiMcuZjConferenceAppointmentById(id);
                if (busiMcuZjConferenceAppointment != null) {
                    String generateEncryptId = EncryptIdUtil.generateEncryptId(busiMcuZjConferenceAppointment.getTemplateId(), McuType.MCU_ZJ.getCode());
                    ModelBean templateInfo = getTemplateInfo(generateEncryptId);
                    busiMcuZjConferenceAppointment.setParams(templateInfo);
                    return success(busiMcuZjConferenceAppointment);
                }
            }
            case MCU_PLC: {
                BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment = busiMcuPlcConferenceAppointmentService.selectBusiMcuPlcConferenceAppointmentById(id);
                if (busiMcuPlcConferenceAppointment != null ) {
                    String generateEncryptId = EncryptIdUtil.generateEncryptId(busiMcuPlcConferenceAppointment.getTemplateId(), McuType.MCU_PLC.getCode());
                    ModelBean templateInfo = getTemplateInfo(generateEncryptId);
                    busiMcuPlcConferenceAppointment.setParams(templateInfo);
                    return success(busiMcuPlcConferenceAppointment);
                }
            }
            case MCU_KDC: {
                BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment = busiMcuKdcConferenceAppointmentService.selectBusiMcuKdcConferenceAppointmentById(id);
                if (busiMcuKdcConferenceAppointment != null) {
                    String generateEncryptId = EncryptIdUtil.generateEncryptId(busiMcuKdcConferenceAppointment.getTemplateId(), McuType.MCU_KDC.getCode());
                    ModelBean templateInfo = getTemplateInfo(generateEncryptId);
                    busiMcuKdcConferenceAppointment.setParams(templateInfo);
                    return success(busiMcuKdcConferenceAppointment);
                }
            }
            case SMC3: {
                BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment = busiMcuSmc3ConferenceAppointmentService.selectBusiMcuSmc3ConferenceAppointmentById(id);
                if (busiMcuSmc3ConferenceAppointment != null) {
                    String generateEncryptId = EncryptIdUtil.generateEncryptId(busiMcuSmc3ConferenceAppointment.getTemplateId(), McuType.SMC3.getCode());
                    ModelBean templateInfo = getTemplateInfo(generateEncryptId);
                    busiMcuSmc3ConferenceAppointment.setParams(templateInfo);
                    return success(busiMcuSmc3ConferenceAppointment);
                }
            }
            case SMC2: {
                BusiMcuSmc2ConferenceAppointment busiMcuSmc2ConferenceAppointment = busiMcuSmc2ConferenceAppointmentService.selectBusiMcuSmc2ConferenceAppointmentById(id);
                if (busiMcuSmc2ConferenceAppointment != null) {
                    String generateEncryptId = EncryptIdUtil.generateEncryptId(busiMcuSmc2ConferenceAppointment.getTemplateId(), McuType.SMC2.getCode());
                    ModelBean templateInfo = getTemplateInfo(generateEncryptId);
                    busiMcuSmc2ConferenceAppointment.setParams(templateInfo);
                    return success(busiMcuSmc2ConferenceAppointment);
                }
            }

            case MCU_TENCENT: {
                BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = busiMcuTencentConferenceAppointmentService.selectBusiMcuTencentConferenceAppointmentById(id);
                if (busiMcuTencentConferenceAppointment != null) {
                    String generateEncryptId = EncryptIdUtil.generateEncryptId(busiMcuTencentConferenceAppointment.getTemplateId(), McuType.MCU_TENCENT.getCode());
                    ModelBean templateInfo = getTemplateInfo(generateEncryptId);
                    busiMcuTencentConferenceAppointment.setParams(templateInfo);
                    return success(busiMcuTencentConferenceAppointment);
                }
            }
            case MCU_DING: {
                BusiMcuDingConferenceAppointment busiMcuTencentConferenceAppointment = busiMcuDingConferenceAppointmentService.selectBusiMcuDingConferenceAppointmentById(id);
                if (busiMcuTencentConferenceAppointment != null) {
                    String generateEncryptId = EncryptIdUtil.generateEncryptId(busiMcuTencentConferenceAppointment.getTemplateId(), McuType.MCU_DING.getCode());
                    ModelBean templateInfo = getTemplateInfo(generateEncryptId);
                    busiMcuTencentConferenceAppointment.setParams(templateInfo);
                    return success(busiMcuTencentConferenceAppointment);
                }
            }

            case MCU_HWCLOUD: {
                BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment = busiMcuHwcloudConferenceAppointmentService.selectBusiMcuHwcloudConferenceAppointmentById(id);
                if (busiMcuHwcloudConferenceAppointment != null) {
                    String generateEncryptId = EncryptIdUtil.generateEncryptId(busiMcuHwcloudConferenceAppointment.getTemplateId(), McuType.MCU_HWCLOUD.getCode());
                    ModelBean templateInfo = getTemplateInfo(generateEncryptId);
                    busiMcuHwcloudConferenceAppointment.setParams(templateInfo);
                    return success(busiMcuHwcloudConferenceAppointment);
                }
            }
            case MCU_ZTE: {
                BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment = busiMcuZteConferenceAppointmentService.selectBusiMcuZteConferenceAppointmentById(id);
                if (busiMcuZteConferenceAppointment != null ) {
                    String generateEncryptId = EncryptIdUtil.generateEncryptId(busiMcuZteConferenceAppointment.getTemplateId(), McuType.MCU_ZTE.getCode());
                    ModelBean templateInfo = getTemplateInfo(generateEncryptId);
                    busiMcuZteConferenceAppointment.setParams(templateInfo);
                    return success(busiMcuZteConferenceAppointment);
                }
            }
        }
        return RestResponse.fail();
    }

    /**
     * 新增会议预约记录
     */
    @PostMapping
    @Operation(summary = "新增会议预约记录", description = "新增预约会议")
    public RestResponse add(@RequestBody JSONObject jsonObject)
    {
        BusiConferenceAppointment busiConferenceAppointment = null;
        String mcuTypeStr = jsonObject.getString("mcuType");
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiConferenceAppointment.class);
                break;
            }
            case MCU_ZJ: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuZjConferenceAppointment.class);
                break;
            }
            case MCU_PLC: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuPlcConferenceAppointment.class);
                break;
            }
            case MCU_KDC: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuKdcConferenceAppointment.class);
                break;
            }
            case SMC3: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuSmc3ConferenceAppointment.class);
                break;
            }
            case SMC2: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuSmc2ConferenceAppointment.class);
                break;
            }

            case MCU_TENCENT: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuTencentConferenceAppointment.class);
                break;
            }
            case MCU_DING: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuDingConferenceAppointment.class);
                break;
            }
            case MCU_HWCLOUD: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuHwcloudConferenceAppointment.class);
                break;
            }
            case MCU_ZTE: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuZteConferenceAppointment.class);
                break;
            }
        }
        if (busiConferenceAppointment == null) {
            return RestResponse.fail();
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        busiConferenceAppointment.setCreateBy(loginUser.getUsername());
        Integer type = busiConferenceAppointment.getType();
        try {
            if (type == null) {
                type = 1;
            }
            if (type != 1 && type != 3) {
                type = 2;
            }
            busiConferenceAppointment.setType(type);
            if (type == 2) {
                String startTimeStr = busiConferenceAppointment.getStartTime();
                String endTimeStr = busiConferenceAppointment.getEndTime();
                Date startTimeNew = new Date();
                String startTimeNewStr = DateUtil.convertDateToString(startTimeNew, "yyyy-MM-dd HH:mm:ss");
                Long diff = (Timestamp.valueOf(endTimeStr).getTime() - Timestamp.valueOf(startTimeStr).getTime()) / 1000;
                if (diff <= 0) {
                    diff = 60 * 120l;
                }
                Date endTimeNew = DateUtils.getDiffDate(startTimeNew, diff.intValue(), TimeUnit.SECONDS);
                String endTimeNewStr = DateUtil.convertDateToString(endTimeNew, "yyyy-MM-dd HH:mm:ss");
                busiConferenceAppointment.setStartTime(startTimeNewStr);
                busiConferenceAppointment.setEndTime(endTimeNewStr);
            }
            String endDate = null;
            if (type == 3) {
                Date startTimeNew = new Date();
                String startTimeNewStr = DateUtil.convertDateToString(startTimeNew, "yyyy-MM-dd HH:mm:ss");
                busiConferenceAppointment.setStartTime(startTimeNewStr);
                endDate = "9999-01-01 00:00:00";
                type = 2;
                busiConferenceAppointment.setEndTime(endDate);
            }
        } catch (Exception e) {
        }
        Map<String, Object> resultMap = null;
        switch (mcuType) {
            case FME: {
                resultMap = busiConferenceAppointmentService.insertBusiConferenceAppointment(busiConferenceAppointment);
                if (busiConferenceAppointment != null && busiConferenceAppointment.getIsAutoCreateTemplate() == 1) {
                    // 级联
                    try {
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(busiConferenceAppointment.getTemplateId(), McuType.FME.getCode());
                        JSONObject params = new JSONObject(busiConferenceAppointment.getParams());
                        addCascadeTemplateConference(params, generateEncryptId);
                        resultMap.put("conferenceId", generateEncryptId);
                    } catch (Exception e) {
                        try {
                            busiTemplateConferenceService.deleteBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                }
                break;
            }
            case MCU_ZJ: {
                BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = (BusiMcuZjConferenceAppointment) busiConferenceAppointment;
                checkResource(busiMcuZjConferenceAppointment);
                resultMap = busiMcuZjConferenceAppointmentService.insertBusiMcuZjConferenceAppointment(busiMcuZjConferenceAppointment);
                if (busiConferenceAppointment != null && busiConferenceAppointment.getIsAutoCreateTemplate() == 1) {
                    // 级联
                    try {
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(busiConferenceAppointment.getTemplateId(), McuType.MCU_ZJ.getCode());
                        JSONObject params = new JSONObject(busiConferenceAppointment.getParams());
                        addCascadeTemplateConference(params, generateEncryptId);
                        resultMap.put("conferenceId", generateEncryptId);
                    } catch (Exception e) {
                        try {
                            busiTemplateConferenceService.deleteBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                }
                break;
            }
            case MCU_PLC: {
                BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment = (BusiMcuPlcConferenceAppointment) busiConferenceAppointment;
                resultMap = busiMcuPlcConferenceAppointmentService.insertBusiMcuPlcConferenceAppointment(busiMcuPlcConferenceAppointment);
                if (busiConferenceAppointment != null && busiConferenceAppointment.getIsAutoCreateTemplate() == 1) {
                    // 级联
                    try {
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(busiConferenceAppointment.getTemplateId(), McuType.MCU_PLC.getCode());
                        JSONObject params = new JSONObject(busiConferenceAppointment.getParams());
                        addCascadeTemplateConference(params, generateEncryptId);
                        resultMap.put("conferenceId", generateEncryptId);
                    } catch (Exception e) {
                        try {
                            busiTemplateConferenceService.deleteBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                }
                break;
            }
            case MCU_KDC: {
                BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment = (BusiMcuKdcConferenceAppointment) busiConferenceAppointment;
                checkResource(busiMcuKdcConferenceAppointment);
                resultMap = busiMcuKdcConferenceAppointmentService.insertBusiMcuKdcConferenceAppointment(busiMcuKdcConferenceAppointment);
                if (busiConferenceAppointment != null && busiConferenceAppointment.getIsAutoCreateTemplate() == 1) {
                    // 级联
                    try {
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(busiConferenceAppointment.getTemplateId(), McuType.MCU_KDC.getCode());
                        JSONObject params = new JSONObject(busiConferenceAppointment.getParams());
                        addCascadeTemplateConference(params, generateEncryptId);
                        resultMap.put("conferenceId", generateEncryptId);
                    } catch (Exception e) {
                        try {
                            busiTemplateConferenceService.deleteBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                }
                break;
            }
            case SMC3: {
                BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment = (BusiMcuSmc3ConferenceAppointment) busiConferenceAppointment;
                // checkResource(busiMcuSmc3ConferenceAppointment);



                Map<String, Object> businessProperties = (Map<String, Object>) busiMcuSmc3ConferenceAppointment.getParams().get("businessProperties");
                if(businessProperties==null){
                    businessProperties=new HashMap<>();
                    busiMcuSmc3ConferenceAppointment.getParams().put("businessProperties",businessProperties);
                }
                businessProperties.put("mainMcuId",busiMcuSmc3ConferenceAppointment.getParams().get("mainMcuId"));
                businessProperties.put("mainMcuName",busiMcuSmc3ConferenceAppointment.getParams().get("mainMcuName"));
                businessProperties.put("mainServiceZoneId",busiMcuSmc3ConferenceAppointment.getParams().get("mainServiceZoneId"));
                businessProperties.put("mainServiceZoneName",busiMcuSmc3ConferenceAppointment.getParams().get("mainServiceZoneName"));

                businessProperties.put("videoProtocol",busiMcuSmc3ConferenceAppointment.getParams().get("videoProtocol"));
                businessProperties.put("videoResolution",busiMcuSmc3ConferenceAppointment.getParams().get("videoResolution"));
                businessProperties.put("audioProtocol",busiMcuSmc3ConferenceAppointment.getParams().get("audioProtocol"));
                businessProperties.put("streamService",busiMcuSmc3ConferenceAppointment.getParams().get("streamService"));
                resultMap = busiMcuSmc3ConferenceAppointmentService.insertBusiMcuSmc3ConferenceAppointment(busiMcuSmc3ConferenceAppointment);
                if (busiConferenceAppointment != null && busiConferenceAppointment.getIsAutoCreateTemplate() == 1) {
                    // 级联
                    try {
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(busiConferenceAppointment.getTemplateId(), McuType.SMC3.getCode());
                        JSONObject params = new JSONObject(busiConferenceAppointment.getParams());
                        addCascadeTemplateConference(params, generateEncryptId);
                        resultMap.put("conferenceId", generateEncryptId);
                    } catch (Exception e) {
                        try {
                            busiTemplateConferenceService.deleteBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                }
                break;
            }
            case SMC2: {
                BusiMcuSmc2ConferenceAppointment busiMcuSmc2ConferenceAppointment = (BusiMcuSmc2ConferenceAppointment) busiConferenceAppointment;
                resultMap = busiMcuSmc2ConferenceAppointmentService.insertBusiMcuSmc2ConferenceAppointment(busiMcuSmc2ConferenceAppointment);
                if (busiConferenceAppointment != null && busiConferenceAppointment.getIsAutoCreateTemplate() == 1) {
                    // 级联
                    try {
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(busiConferenceAppointment.getTemplateId(), McuType.SMC2.getCode());
                        JSONObject params = new JSONObject(busiConferenceAppointment.getParams());
                        addCascadeTemplateConference(params, generateEncryptId);
                    } catch (Exception e) {
                        try {
                            busiTemplateConferenceService.deleteBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                }
                break;
            }

            case MCU_TENCENT: {
                BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = (BusiMcuTencentConferenceAppointment) busiConferenceAppointment;
                Map<String, Object> businessProperties = (Map<String, Object>) busiMcuTencentConferenceAppointment.getParams().get("businessProperties");
                if(businessProperties==null){
                    businessProperties=new HashMap<>();
                    busiMcuTencentConferenceAppointment.getParams().put("businessProperties",businessProperties);
                }
                businessProperties.put("attendees",busiMcuTencentConferenceAppointment.getParams().get("templateParticipants"));
                resultMap = busiMcuTencentConferenceAppointmentService.insertBusiMcuTencentConferenceAppointment(busiMcuTencentConferenceAppointment);
                if (busiConferenceAppointment != null && busiConferenceAppointment.getIsAutoCreateTemplate() == 1) {
                    // 级联
                    try {
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(busiConferenceAppointment.getTemplateId(), McuType.MCU_TENCENT.getCode());
                        JSONObject params = new JSONObject(busiConferenceAppointment.getParams());
                        addCascadeTemplateConference(params, generateEncryptId);
                        resultMap.put("conferenceId", generateEncryptId);
                    } catch (Exception e) {
                        try {
                            busiTemplateConferenceService.deleteBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                }
                break;
            }
            case MCU_DING: {
                BusiMcuDingConferenceAppointment busiMcuDingConferenceAppointment = (BusiMcuDingConferenceAppointment) busiConferenceAppointment;

                resultMap = busiMcuDingConferenceAppointmentService.insertBusiMcuDingConferenceAppointment(busiMcuDingConferenceAppointment);
                if (busiConferenceAppointment != null && busiConferenceAppointment.getIsAutoCreateTemplate() == 1) {
                    // 级联
                    try {
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(busiConferenceAppointment.getTemplateId(), McuType.MCU_DING.getCode());
                        JSONObject params = new JSONObject(busiConferenceAppointment.getParams());
                        addCascadeTemplateConference(params, generateEncryptId);
                        resultMap.put("conferenceId", generateEncryptId);
                    } catch (Exception e) {
                        try {
                            busiTemplateConferenceService.deleteBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                }
                break;
            }
            case MCU_HWCLOUD: {
                BusiMcuHwcloudConferenceAppointment busiMcuhwcloudConferenceAppointment = (BusiMcuHwcloudConferenceAppointment) busiConferenceAppointment;


                Map<String, Object> businessProperties = (Map<String, Object>) busiMcuhwcloudConferenceAppointment.getParams().get("businessProperties");
                if(businessProperties==null){
                    businessProperties=new HashMap<>();
                    busiMcuhwcloudConferenceAppointment.getParams().put("businessProperties",businessProperties);
                }
                businessProperties.put("callInRestriction",busiMcuhwcloudConferenceAppointment.getParams().get("callInRestriction"));
                businessProperties.put("allowGuestStartConf",busiMcuhwcloudConferenceAppointment.getParams().get("allowGuestStartConf"));
                businessProperties.put("isSendCalendar",busiMcuhwcloudConferenceAppointment.getParams().get("isSendCalendar"));
                businessProperties.put("enableWaitingRoom",busiMcuhwcloudConferenceAppointment.getParams().get("enableWaitingRoom"));
                businessProperties.put("isSendNotify",busiMcuhwcloudConferenceAppointment.getParams().get("isSendNotify"));
                businessProperties.put("confPresetParam",busiMcuhwcloudConferenceAppointment.getParams().get("confPresetParam"));
                businessProperties.put("attendees",busiMcuhwcloudConferenceAppointment.getParams().get("templateParticipants"));


                resultMap = busiMcuHwcloudConferenceAppointmentService.insertBusiMcuHwcloudConferenceAppointment(busiMcuhwcloudConferenceAppointment);
                String generateEncryptId = EncryptIdUtil.generateEncryptId(busiMcuhwcloudConferenceAppointment.getTemplateId(), McuType.MCU_HWCLOUD.getCode());
                resultMap.put("conferenceId", generateEncryptId);
                break;
            }
            case MCU_ZTE: {
                BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment = (BusiMcuZteConferenceAppointment) busiConferenceAppointment;


                Map<String, Object> businessProperties = (Map<String, Object>) busiMcuZteConferenceAppointment.getParams().get("businessProperties");
                if(businessProperties==null){
                    businessProperties=new HashMap<>();
                    busiMcuZteConferenceAppointment.getParams().put("businessProperties",businessProperties);
                }
                businessProperties.put("maxParticipantNum", busiMcuZteConferenceAppointment.getParams().get("maxParticipantNum"));
                businessProperties.put("multiPicControl", busiMcuZteConferenceAppointment.getParams().get("multiPicControl"));
                businessProperties.put("multiViewNumber", busiMcuZteConferenceAppointment.getParams().get("multiViewNumber"));
                businessProperties.put("dynamicRes", busiMcuZteConferenceAppointment.getParams().get("dynamicRes"));
                businessProperties.put("inviteWithSDP", busiMcuZteConferenceAppointment.getParams().get("inviteWithSDP"));
                businessProperties.put("conferenceTemplet", busiMcuZteConferenceAppointment.getParams().get("conferenceTemplet"));

                businessProperties.put("confCascadeMode", busiMcuZteConferenceAppointment.getParams().get("confCascadeMode"));
                businessProperties.put("enableMcuTitle", busiMcuZteConferenceAppointment.getParams().get("enableMcuTitle"));
                businessProperties.put("enableMcuBanner", busiMcuZteConferenceAppointment.getParams().get("enableMcuBanner"));
                businessProperties.put("enableVoiceRecord", busiMcuZteConferenceAppointment.getParams().get("enableVoiceRecord"));

                businessProperties.put("enableAutoVoiceRecord", busiMcuZteConferenceAppointment.getParams().get("enableAutoVoiceRecord"));
                businessProperties.put("enableUpConf", busiMcuZteConferenceAppointment.getParams().get("enableUpConf"));
                businessProperties.put("sendMail", busiMcuZteConferenceAppointment.getParams().get("sendMail"));



                resultMap = busiMcuZteConferenceAppointmentService.insertBusiMcuZteConferenceAppointment(busiMcuZteConferenceAppointment);
                if (busiConferenceAppointment != null && busiConferenceAppointment.getIsAutoCreateTemplate() == 1) {

                    Object defaultViewPaticipants = busiMcuZteConferenceAppointment.getParams().get("defaultViewPaticipants");
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiMcuZteConferenceAppointment.getTemplateId(), mcuType.getCode());
                    // 分屏
                    if(defaultViewPaticipants!=null){
                        try {
                            JSONObject jsonObjectViewConfigInfo = new JSONObject();
                            jsonObjectViewConfigInfo.put("defaultViewPaticipants",defaultViewPaticipants);
                            jsonObjectViewConfigInfo.put("defaultViewLayout",busiMcuZteConferenceAppointment.getParams().get("defaultViewLayout"));
                            jsonObjectViewConfigInfo.put("defaultViewIsBroadcast",busiMcuZteConferenceAppointment.getParams().get("defaultViewIsBroadcast"));
                            jsonObjectViewConfigInfo.put("defaultViewDepts",busiMcuZteConferenceAppointment.getParams().get("defaultViewDepts"));
                            jsonObjectViewConfigInfo.put("defaultViewCellScreens",busiMcuZteConferenceAppointment.getParams().get("defaultViewCellScreens"));
                            jsonObjectViewConfigInfo.put("pollingInterval",busiMcuZteConferenceAppointment.getParams().get("pollingInterval"));
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }

                    // 级联
                    try {
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(busiConferenceAppointment.getTemplateId(), McuType.MCU_ZTE.getCode());
                        resultMap.put("conferenceId", generateEncryptId);
                        JSONObject params = new JSONObject(busiConferenceAppointment.getParams());
                        addCascadeTemplateConference(params, generateEncryptId);
                    } catch (Exception e) {
                        try {
                            busiTemplateConferenceService.deleteBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                }



                break;
            }
        }
        if (resultMap == null) {
            return RestResponse.fail();
        }
        Integer success = 0;
        try {
            success = (Integer) resultMap.get("rows");
        } catch (Exception e) {
        }
        if (success > 0) {
            Long conferenceNumber = null;
            Long templateId = null;
            String tenantId = "";
            Long appointmentId = null;
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
            try {
                appointmentId = (Long) resultMap.get("appointmentId");
            } catch (Exception e) {

            }
            if (type == 2) {
                if (templateId != null) {
                    try {
                        String contextKey = null;
                        switch (mcuType) {
                            case FME: {
                                contextKey = templateConferenceStartService.startTemplateConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiConferenceAppointmentService.updateBusiConferenceAppointment(busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }
                            case MCU_ZJ: {
                                contextKey = busiMcuZjConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuZjConferenceAppointmentService.updateBusiMcuZjConferenceAppointment((BusiMcuZjConferenceAppointment) busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }
                            case MCU_PLC: {
                                contextKey = busiMcuPlcConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuPlcConferenceAppointmentService.updateBusiMcuPlcConferenceAppointment((BusiMcuPlcConferenceAppointment) busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }
                            case MCU_KDC: {
                                contextKey = busiMcuKdcConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuKdcConferenceAppointmentService.updateBusiMcuKdcConferenceAppointment((BusiMcuKdcConferenceAppointment) busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }
                            case SMC3: {
                                contextKey = busiSmc3ConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuSmc3ConferenceAppointmentService.updateBusiMcuSmc3ConferenceAppointment((BusiMcuSmc3ConferenceAppointment) busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }
                            case SMC2: {
                                contextKey = busiSmc2ConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuSmc2ConferenceAppointmentService.updateBusiMcuSmc2ConferenceAppointment((BusiMcuSmc2ConferenceAppointment) busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }

                            case MCU_TENCENT: {
                                contextKey = busiTencentConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuTencentConferenceAppointmentService.updateBusiMcuTencentConferenceAppointment((BusiMcuTencentConferenceAppointment) busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }
                            case MCU_DING: {
                                contextKey = busiTencentConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuDingConferenceAppointmentService.updateBusiMcuDingConferenceAppointment((BusiMcuDingConferenceAppointment) busiConferenceAppointment);
                                    }
                                }
                                break;
                            }
                            case MCU_HWCLOUD: {
                                contextKey = busiTencentConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuHwcloudConferenceAppointmentService.updateBusiMcuHwcloudConferenceAppointment((BusiMcuHwcloudConferenceAppointment) busiConferenceAppointment,false);
                                    }
                                }
                                break;
                            }
                            case MCU_ZTE: {
                                contextKey = busiMcuZteConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuZteConferenceAppointmentService.updateBusiMcuZteConferenceAppointment((BusiMcuZteConferenceAppointment) busiConferenceAppointment,false);
                                    }
                                }
                                break;
                            }
                        }
                        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                        if (baseConferenceContext != null) {
                            StartDownCascadeConferenceTask startDownCascadeConferenceTask = new StartDownCascadeConferenceTask(baseConferenceContext.getId(), 0, baseConferenceContext);
                            BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask);
                            BusiConferenceAppointment conferenceAppointment = baseConferenceContext.getConferenceAppointment();
                            if (conferenceAppointment != null) {
                                NotifyTask notifyTask = new NotifyTask(baseConferenceContext.getId(), 10000, baseConferenceContext, conferenceAppointment.getId(), mcuTypeStr, "即时");
                                taskService.addTask(notifyTask);
                            }
                        }

                    } catch (Exception e) {
                    }
                }
            }
            NotifyTask notifyTask = new NotifyTask(appointmentId.toString(), 10000, null, appointmentId, mcuTypeStr, "预约");
            taskService.addTask(notifyTask);
        }
        int rows = 0;
        Object rowsObj = resultMap.get("rows");
        resultMap.put("mcuType", mcuType.getCode());

        if (rowsObj != null) {
            rows = (int) rowsObj;
            if (rows > 0) {
                return RestResponse.success(resultMap);
            }
        }
        return toAjax(rows);
    }

    /**
     * 修改会议预约记录
     */
    @PutMapping(value = "/{apConferenceId}")
    @Operation(summary = "修改会议预约记录", description = "修改预约会议")
    public RestResponse edit(@PathVariable("apConferenceId") String apConferenceId, @RequestBody JSONObject jsonObject)
    {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(apConferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType.getCode(), id);
        if (viewConferenceAppointment != null) {
            String contextKey = EncryptIdUtil.generateContextKey(viewConferenceAppointment.getTemplateId(), viewConferenceAppointment.getMcuType());
            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
            if (baseConferenceContext != null) {
                throw new SystemException("当前预约会议模板正在开会，不能修改！");
            }
        }
        switch (mcuType) {
            case FME: {
                BusiConferenceAppointment busiConferenceAppointment = jsonObject.toJavaObject(BusiConferenceAppointment.class);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                busiConferenceAppointment.setCreateBy(loginUser.getUsername());
                busiConferenceAppointment.setId(id);
                if (viewConferenceAppointment.getRoomBookId() != null) {
                    busiConferenceAppointment.setType(viewConferenceAppointment.getType());
                    busiConferenceAppointment.setStartTime(viewConferenceAppointment.getStartTime());
                    busiConferenceAppointment.setEndTime(viewConferenceAppointment.getEndTime());
                }
                // 级联
                try {
                    JSONObject params = new JSONObject(busiConferenceAppointment.getParams());
                    Long masterTerminalId = params.getLong("masterTerminalId");
                    BusiTemplateConference busiTemplateConference = params.toJavaObject(BusiTemplateConference.class);
                    busiTemplateConference.setId(busiConferenceAppointment.getTemplateId());
                    busiTemplateConference.setName(params.getString("conferenceName"));
                    JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                    List<BusiTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                    if (busiTemplateParticipantArr != null) {
                        for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                            BusiTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiTemplateParticipant.class);
                            p.setId(null);
                            Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                            Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                            Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                            busiTemplateParticipants.add(p);
                        }
                    }

                    // 部门顺序
                    JSONArray templateDeptArr = params.getJSONArray("templateDepts");
                    List<BusiTemplateDept> templateDepts = new ArrayList<>();
                    for (int i = 0; i < templateDeptArr.size(); i++) {
                        templateDepts.add(templateDeptArr.getObject(i, BusiTemplateDept.class));
                    }

                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                    int c = busiTemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                    if (c > 0) {
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(busiConferenceAppointment.getTemplateId(), McuType.FME.getCode());
                        // 分屏
                        try {
                            if (params.containsKey("viewConfigInfo")) {
                                JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                                updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, generateEncryptId);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }

                        editCascadeTemplateConference(params, generateEncryptId);
                    }
                } catch (Exception e) {
                    return RestResponse.fail();
                }
                int i = busiConferenceAppointmentService.updateBusiConferenceAppointment(busiConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiConferenceAppointment.getId().toString(), 1000, null, busiConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case MCU_ZJ: {
                BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = jsonObject.toJavaObject(BusiMcuZjConferenceAppointment.class);
                busiMcuZjConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                busiMcuZjConferenceAppointment.setCreateBy(loginUser.getUsername());
                if (viewConferenceAppointment.getRoomBookId() != null) {
                    busiMcuZjConferenceAppointment.setType(viewConferenceAppointment.getType());
                    busiMcuZjConferenceAppointment.setStartTime(viewConferenceAppointment.getStartTime());
                    busiMcuZjConferenceAppointment.setEndTime(viewConferenceAppointment.getEndTime());
                }
                // 级联
                try {
                    JSONObject params = new JSONObject(busiMcuZjConferenceAppointment.getParams());
                    Long masterTerminalId = params.getLong("masterTerminalId");
                    BusiMcuZjTemplateConference busiTemplateConference = params.toJavaObject(BusiMcuZjTemplateConference.class);
                    busiTemplateConference.setId(busiMcuZjConferenceAppointment.getTemplateId());

                    JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                    List<BusiMcuZjTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                    if (busiTemplateParticipantArr != null) {
                        for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                            BusiMcuZjTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuZjTemplateParticipant.class);
                            p.setId(null);
                            Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                            Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                            Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                            busiTemplateParticipants.add(p);
                        }
                    }

                    // 部门顺序
                    JSONArray templateDeptArr = params.getJSONArray("templateDepts");
                    List<BusiMcuZjTemplateDept> templateDepts = new ArrayList<>();
                    for (int i = 0; i < templateDeptArr.size(); i++) {
                        templateDepts.add(templateDeptArr.getObject(i, BusiMcuZjTemplateDept.class));
                    }

                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                    int c = busiMcuZjTemplateConferenceService.updateBusiMcuZjTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);

                    if (c > 0) {
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(busiMcuZjConferenceAppointment.getTemplateId(), McuType.MCU_ZJ.getCode());

                        // 分屏
                        try {
                            if (params.containsKey("viewConfigInfo")) {
                                JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                                updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, generateEncryptId);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }

                        editCascadeTemplateConference(params, generateEncryptId);
                    }
                } catch (Exception e) {
                    return RestResponse.fail();
                }
                int i = busiMcuZjConferenceAppointmentService.updateBusiMcuZjConferenceAppointment(busiMcuZjConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuZjConferenceAppointment.getId().toString(), 1000, null, busiMcuZjConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case MCU_PLC: {
                BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment = jsonObject.toJavaObject(BusiMcuPlcConferenceAppointment.class);
                busiMcuPlcConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                busiMcuPlcConferenceAppointment.setCreateBy(loginUser.getUsername());
                if (viewConferenceAppointment.getRoomBookId() != null) {
                    busiMcuPlcConferenceAppointment.setType(viewConferenceAppointment.getType());
                    busiMcuPlcConferenceAppointment.setStartTime(viewConferenceAppointment.getStartTime());
                    busiMcuPlcConferenceAppointment.setEndTime(viewConferenceAppointment.getEndTime());
                }

                JSONObject params = new JSONObject(busiMcuPlcConferenceAppointment.getParams());
                BusiMcuZjTemplateConference busiTemplateConference = params.toJavaObject(BusiMcuZjTemplateConference.class);
                busiTemplateConference.setId(busiMcuPlcConferenceAppointment.getTemplateId());
                JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                Long masterTerminalId = params.getLong("masterTerminalId");

                List<BusiMcuZjTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuZjTemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiMcuZjTemplateParticipant.class);
                        Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(busiTemplateParticipant.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(busiTemplateParticipant.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(busiTemplateParticipant);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = params.getJSONArray("templateDepts");
                List<BusiMcuZjTemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    BusiMcuZjTemplateDept busiTemplateDept = templateDeptArr.getObject(i, BusiMcuZjTemplateDept.class);
                    Assert.notNull(busiTemplateDept.getDeptId(), "部门ID不能为空");
                    Assert.notNull(busiTemplateDept.getWeight(), "部门weight顺序不能为空");
                    templateDepts.add(busiTemplateDept);
                }

                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }

                // 默认24小时
                busiTemplateConference.setDurationEnabled(1);// 开始会议时长限制
                busiTemplateConference.setDurationTime(1440);

                Integer muteType = busiTemplateConference.getMuteType();
                if (muteType == null || muteType != 0) {
                    muteType = 1;// 0 不静音 1 静音
                }
                busiTemplateConference.setMuteType(muteType);
                // 默认自动分屏
                busiTemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
                busiTemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
                busiTemplateConference.setDefaultViewIsFill(YesOrNo.YES.getValue());
                busiTemplateConference.setPollingInterval(10);
                busiTemplateConference.setDefaultViewIsDisplaySelf(-1);
                busiTemplateConference.setDefaultViewLayoutGuest(AutomaticSplitScreen.LAYOUT);
                busiTemplateConference.setDefaultViewIsFillGuest(YesOrNo.YES.getValue());
                busiTemplateConference.setPollingIntervalGuest(10);

                int c = busiMcuZjTemplateConferenceService.insertBusiMcuZjTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (params.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    try {
                        addCascadeTemplateConference(params, conferenceId);
                    } catch (Exception e) {
                        try {
                            busiMcuZjTemplateConferenceService.deleteBusiMcuZjTemplateConferenceById(busiTemplateConference.getId());
                        } catch (Exception e2) {
                            logger.error(e2.getMessage());
                        }
                        return RestResponse.fail(e.getMessage());
                    }
                } else {
                    return RestResponse.fail();
                }
                int i = busiMcuPlcConferenceAppointmentService.updateBusiMcuPlcConferenceAppointment(busiMcuPlcConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuPlcConferenceAppointment.getId().toString(), 1000, null, busiMcuPlcConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case MCU_KDC: {
                BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment = jsonObject.toJavaObject(BusiMcuKdcConferenceAppointment.class);
                busiMcuKdcConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                busiMcuKdcConferenceAppointment.setCreateBy(loginUser.getUsername());
                if (viewConferenceAppointment.getRoomBookId() != null) {
                    busiMcuKdcConferenceAppointment.setType(viewConferenceAppointment.getType());
                    busiMcuKdcConferenceAppointment.setStartTime(viewConferenceAppointment.getStartTime());
                    busiMcuKdcConferenceAppointment.setEndTime(viewConferenceAppointment.getEndTime());
                }

                JSONObject params = new JSONObject(busiMcuKdcConferenceAppointment.getParams());
                Long masterTerminalId = params.getLong("masterTerminalId");
                BusiMcuKdcTemplateConference busiTemplateConference = params.toJavaObject(BusiMcuKdcTemplateConference.class);

                busiTemplateConference.setId(id);

                JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                List<BusiMcuKdcTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuKdcTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuKdcTemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = params.getJSONArray("templateDepts");
                List<BusiMcuKdcTemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuKdcTemplateDept.class));
                }

                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                int c = busiMcuKdcTemplateConferenceService.updateBusiMcuKdcTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());

                    // 分屏
                    try {
                        if (params.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        editCascadeTemplateConference(params, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail();
                    }
                } else {
                    return RestResponse.fail();
                }
                int i = busiMcuKdcConferenceAppointmentService.updateBusiMcuKdcConferenceAppointment(busiMcuKdcConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuKdcConferenceAppointment.getId().toString(), 1000, null, busiMcuKdcConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case SMC3: {
                BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment = jsonObject.toJavaObject(BusiMcuSmc3ConferenceAppointment.class);
                busiMcuSmc3ConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                busiMcuSmc3ConferenceAppointment.setCreateBy(loginUser.getUsername());
                if (viewConferenceAppointment.getRoomBookId() != null) {
                    busiMcuSmc3ConferenceAppointment.setType(viewConferenceAppointment.getType());
                    busiMcuSmc3ConferenceAppointment.setStartTime(viewConferenceAppointment.getStartTime());
                    busiMcuSmc3ConferenceAppointment.setEndTime(viewConferenceAppointment.getEndTime());
                }

                JSONObject params = new JSONObject(busiMcuSmc3ConferenceAppointment.getParams());
                Long masterTerminalId = params.getLong("masterTerminalId");
                BusiMcuSmc3TemplateConference busiTemplateConference = params.toJavaObject(BusiMcuSmc3TemplateConference.class);

                busiTemplateConference.setId(busiMcuSmc3ConferenceAppointment.getTemplateId());
                JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                List<BusiMcuSmc3TemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuSmc3TemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuSmc3TemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = params.getJSONArray("templateDepts");
                List<BusiMcuSmc3TemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuSmc3TemplateDept.class));
                }



                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }
                if (busiTemplateConference.getIsAutoCreateStreamUrl() != null && busiTemplateConference.getIsAutoCreateStreamUrl() == 1) {
                    busiTemplateConference.setStreamUrl(null);
                }
                Object confPresetParam = busiMcuSmc3ConferenceAppointment.getParams().get("confPresetParam");
                if(confPresetParam!=null){
                    busiTemplateConference.setConfPresetParam(JSONObject.parseObject(JSONObject.toJSONString(confPresetParam)));
                }

                Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
                if(businessProperties==null){
                    businessProperties=new HashMap<>();
                }
                businessProperties.put("mainMcuId",busiMcuSmc3ConferenceAppointment.getParams().get("mainMcuId"));
                businessProperties.put("mainMcuName",busiMcuSmc3ConferenceAppointment.getParams().get("mainMcuName"));
                businessProperties.put("mainServiceZoneId",busiMcuSmc3ConferenceAppointment.getParams().get("mainServiceZoneId"));
                businessProperties.put("mainServiceZoneName",busiMcuSmc3ConferenceAppointment.getParams().get("mainServiceZoneName"));

                businessProperties.put("videoProtocol",busiMcuSmc3ConferenceAppointment.getParams().get("videoProtocol"));
                businessProperties.put("videoResolution",busiMcuSmc3ConferenceAppointment.getParams().get("videoResolution"));
                businessProperties.put("audioProtocol",busiMcuSmc3ConferenceAppointment.getParams().get("audioProtocol"));
                businessProperties.put("streamService",busiMcuSmc3ConferenceAppointment.getParams().get("streamService"));
                busiTemplateConference.setBusinessProperties(businessProperties);

//                Object cascadeNodes = busiMcuSmc3ConferenceAppointment.getParams().get("cascadeNodes");
//                if(cascadeNodes!=null){
//                    JSONObject jsonObject1 = new JSONObject();
//                    jsonObject1.put("cascadeNodes",cascadeNodes);
//                    JSONArray templateNodesJson = jsonObject1.getJSONArray("cascadeNodes");
//                    if(templateNodesJson!=null){
//                        busiTemplateConference.setCategory("CASCADE");
//                        busiTemplateConference.setIsAutoCreateConferenceNumber(1);
//                        busiTemplateConference.setBusinessFieldType(100);
//                        busiTemplateConference.setCreateType(2);
//                    }
//                    busiTemplateConference.setCascadeNodes(JSONArray.toJSONString(templateNodesJson));
//                }

                Object cascadeNodesTemp = busiMcuSmc3ConferenceAppointment.getParams().get("cascadeNodesTemp");
                if(cascadeNodesTemp!=null){
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("cascadeNodesTemp",cascadeNodesTemp);
                    JSONArray templateNodesTempJson = jsonObject1.getJSONArray("cascadeNodesTemp");
                    if(templateNodesTempJson!=null){
                        busiTemplateConference.setCategory("CASCADE");
                        busiTemplateConference.setIsAutoCreateConferenceNumber(1);
                        busiTemplateConference.setBusinessFieldType(100);
                        busiTemplateConference.setCreateType(2);
                    }
                    busiTemplateConference.setCascadeNodesTemp(JSONArray.toJSONString(templateNodesTempJson));
                }

                busiTemplateConference.setChairmanPassword((String)busiMcuSmc3ConferenceAppointment.getParams().get("chairmanPassword"));
                busiTemplateConference.setGuestPassword((String)busiMcuSmc3ConferenceAppointment.getParams().get("guestPassword"));
                if(busiMcuSmc3ConferenceAppointment.getParams().get("maxParticipantNum")==null){
                    busiTemplateConference.setMaxParticipantNum(500);
                }else {
                    busiTemplateConference.setMaxParticipantNum((Integer) busiMcuSmc3ConferenceAppointment.getParams().get("maxParticipantNum"));
                }
                busiTemplateConference.setName((String)busiMcuSmc3ConferenceAppointment.getParams().get("conferenceName"));
                busiTemplateConference.setMuteType((Integer) busiMcuSmc3ConferenceAppointment.getParams().get("muteType"));
                BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(busiMcuSmc3ConferenceAppointment.getTemplateId());
                busiTemplateConference.setSmcTemplateId(busiMcuSmc3TemplateConference.getSmcTemplateId());
                int c = busiMcuSmc3TemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {

                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());

                    // 分屏
                    try {
                        if (params.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        editCascadeTemplateConference(params, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail();
                    }
                } else {
                    return RestResponse.fail();
                }
                int i = busiMcuSmc3ConferenceAppointmentService.updateBusiMcuSmc3ConferenceAppointment(busiMcuSmc3ConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuSmc3ConferenceAppointment.getId().toString(), 1000, null, busiMcuSmc3ConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case SMC2: {
                BusiMcuSmc2ConferenceAppointment busiMcuSmc2ConferenceAppointment = jsonObject.toJavaObject(BusiMcuSmc2ConferenceAppointment.class);
                busiMcuSmc2ConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                busiMcuSmc2ConferenceAppointment.setCreateBy(loginUser.getUsername());
                if (viewConferenceAppointment.getRoomBookId() != null) {
                    busiMcuSmc2ConferenceAppointment.setType(viewConferenceAppointment.getType());
                    busiMcuSmc2ConferenceAppointment.setStartTime(viewConferenceAppointment.getStartTime());
                    busiMcuSmc2ConferenceAppointment.setEndTime(viewConferenceAppointment.getEndTime());
                }

                JSONObject params = new JSONObject(busiMcuSmc2ConferenceAppointment.getParams());
                Long masterTerminalId = params.getLong("masterTerminalId");
                BusiMcuSmc2TemplateConference busiTemplateConference = params.toJavaObject(BusiMcuSmc2TemplateConference.class);
                busiTemplateConference.setName((String) params.get("conferenceName"));
                busiTemplateConference.setId(id);
                JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                List<BusiMcuSmc2TemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuSmc2TemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuSmc2TemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = params.getJSONArray("templateDepts");
                List<BusiMcuSmc2TemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuSmc2TemplateDept.class));
                }

                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());


                busiTemplateConference.setChairmanPassword((String)busiMcuSmc2ConferenceAppointment.getParams().get("chairmanPassword"));
                busiTemplateConference.setGuestPassword((String)busiMcuSmc2ConferenceAppointment.getParams().get("guestPassword"));
                if(busiMcuSmc2ConferenceAppointment.getParams().get("maxParticipantNum")==null){
                    busiTemplateConference.setMaxParticipantNum(500);
                }else {
                    busiTemplateConference.setMaxParticipantNum((Integer) busiMcuSmc2ConferenceAppointment.getParams().get("maxParticipantNum"));
                }
                busiTemplateConference.setName((String)busiMcuSmc2ConferenceAppointment.getParams().get("conferenceName"));

                BusiMcuSmc2TemplateConference busiMcuSmc2TemplateConference = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(busiMcuSmc2ConferenceAppointment.getTemplateId());
                busiTemplateConference.setId(busiMcuSmc2TemplateConference.getId());
                busiTemplateConference.setGuestPassword((String)busiMcuSmc2ConferenceAppointment.getParams().get("password"));
                busiTemplateConference.setConferencePassword((String)busiMcuSmc2ConferenceAppointment.getParams().get("password"));
                busiTemplateConference.setMuteType(busiMcuSmc2TemplateConference.getParams().get("mutuType")==null?2:(Integer) busiMcuSmc2TemplateConference.getParams().get("mutuType"));
                int c = busiMcuSmc2TemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (params.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    // 级联
                    try {
                        editCascadeTemplateConference(params, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail();
                    }
                } else {
                    return RestResponse.fail();
                }
                int i = busiMcuSmc2ConferenceAppointmentService.updateBusiMcuSmc2ConferenceAppointment(busiMcuSmc2ConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuSmc2ConferenceAppointment.getId().toString(), 1000, null, busiMcuSmc2ConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case MCU_TENCENT: {
                BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = jsonObject.toJavaObject(BusiMcuTencentConferenceAppointment.class);
                busiMcuTencentConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                busiMcuTencentConferenceAppointment.setCreateBy(loginUser.getUsername());
                if (viewConferenceAppointment.getRoomBookId() != null) {
                    busiMcuTencentConferenceAppointment.setType(viewConferenceAppointment.getType());
                    busiMcuTencentConferenceAppointment.setStartTime(viewConferenceAppointment.getStartTime());
                    busiMcuTencentConferenceAppointment.setEndTime(viewConferenceAppointment.getEndTime());
                }

                JSONObject params = new JSONObject(busiMcuTencentConferenceAppointment.getParams());
                Long masterTerminalId = params.getLong("masterTerminalId");
                BusiMcuTencentTemplateConference busiTemplateConference = params.toJavaObject(BusiMcuTencentTemplateConference.class);
                busiTemplateConference.setId(id);
                List<BusiMcuTencentTemplateParticipant> busiTemplateParticipants = new ArrayList<>();

                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

                if(busiMcuTencentConferenceAppointment.getParams().get("chairmanPassword")!=null){
                    String passwordStr_chair = (String) busiMcuTencentConferenceAppointment.getParams().get("chairmanPassword");
                    Pattern pattern = Pattern.compile(regex_chair);
                    // 创建匹配器
                    Matcher matcher = pattern.matcher(passwordStr_chair);
                    if (!matcher.matches()) {
                        throw new CustomException("密码格式不正确,密码是6位的数字");
                    }
                    busiTemplateConference.setChairmanPassword(passwordStr_chair);
                }

                if(busiMcuTencentConferenceAppointment.getParams().get("password")!=null){
                    String passwordStr = (String) busiMcuTencentConferenceAppointment.getParams().get("password");
                    if(Strings.isNotBlank(passwordStr)){
                        Pattern pattern = Pattern.compile(regex);
                        // 创建匹配器
                        Matcher matcher = pattern.matcher(passwordStr);
                        if (!matcher.matches()) {
                            throw new CustomException("密码格式不正确,密码是4到6位的数字");
                        }
                        busiTemplateConference.setGuestPassword(passwordStr);
                        busiTemplateConference.setConferencePassword(passwordStr);
                    }
                }

                if(busiMcuTencentConferenceAppointment.getParams().get("supportLive")!=null){
                    busiTemplateConference.setRecordingEnabled((Integer) busiMcuTencentConferenceAppointment.getParams().get("supportLive"));
                }
                if(busiMcuTencentConferenceAppointment.getParams().get("supportRecord")!=null){
                    busiTemplateConference.setRecordingEnabled((Integer) busiMcuTencentConferenceAppointment.getParams().get("supportRecord"));
                }
                busiTemplateConference.setMuteType(busiMcuTencentConferenceAppointment.getParams().get("muteType")==null?2:(Integer) busiMcuTencentConferenceAppointment.getParams().get("muteType"));
                if(busiMcuTencentConferenceAppointment.getParams().get("maxParticipantNum")==null){
                    busiTemplateConference.setMaxParticipantNum(500);
                }else {
                    busiTemplateConference.setMaxParticipantNum((Integer) busiMcuTencentConferenceAppointment.getParams().get("maxParticipantNum"));
                }
                busiTemplateConference.setName((String)busiMcuTencentConferenceAppointment.getParams().get("conferenceName"));

                busiTemplateConference.setId(viewConferenceAppointment.getTemplateId());

                Map<String, Object> businessProperties = (Map<String, Object>) busiMcuTencentConferenceAppointment.getParams().get("businessProperties");
                if(businessProperties==null){
                    businessProperties=new HashMap<>();
                    busiMcuTencentConferenceAppointment.getParams().put("businessProperties",businessProperties);
                }
                businessProperties.put("attendees",busiMcuTencentConferenceAppointment.getParams().get("templateParticipants"));
                busiTemplateConference.setBusinessProperties(businessProperties);
                if(busiTemplateConference.getConferenceNumber()==null){
                    BusiMcuTencentTemplateConference tc= busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(busiTemplateConference.getId());
                    busiTemplateConference.setConferenceNumber(tc.getConferenceNumber());
                    busiTemplateConference.setConfId(tc.getConfId());
                }

                int c = busiMcuTencentTemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, null);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (params.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    //同步修改预约会议
                    new com.paradisecloud.fcm.tencent.templateConference.StartTemplateConference().editeTencentConference(busiTemplateConference.getId(),busiMcuTencentConferenceAppointment.getStartTime());
                    // 级联
                    try {
                        editCascadeTemplateConference(params, conferenceId);
                    } catch (Exception e) {
                        return RestResponse.fail();
                    }
                } else {
                    return RestResponse.fail();
                }
                int i = busiMcuTencentConferenceAppointmentService.updateBusiMcuTencentConferenceAppointment(busiMcuTencentConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuTencentConferenceAppointment.getId().toString(), 1000, null, busiMcuTencentConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case MCU_HWCLOUD: {
                BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment = jsonObject.toJavaObject(BusiMcuHwcloudConferenceAppointment.class);
                busiMcuHwcloudConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                busiMcuHwcloudConferenceAppointment.setCreateBy(loginUser.getUsername());
                if (viewConferenceAppointment.getRoomBookId() != null) {
                    busiMcuHwcloudConferenceAppointment.setType(viewConferenceAppointment.getType());
                    busiMcuHwcloudConferenceAppointment.setStartTime(viewConferenceAppointment.getStartTime());
                    busiMcuHwcloudConferenceAppointment.setEndTime(viewConferenceAppointment.getEndTime());
                }

                JSONObject params = new JSONObject(busiMcuHwcloudConferenceAppointment.getParams());
                Long masterTerminalId = params.getLong("masterTerminalId");
                BusiMcuHwcloudTemplateConference busiTemplateConference = params.toJavaObject(BusiMcuHwcloudTemplateConference.class);
                busiTemplateConference.setId(id);
                List<BusiMcuHwcloudTemplateParticipant> busiTemplateParticipants = new ArrayList<>();

                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

                if(busiMcuHwcloudConferenceAppointment.getParams().get("chairmanPassword")!=null){
                    String passwordStr_chair = (String) busiMcuHwcloudConferenceAppointment.getParams().get("chairmanPassword");
                    Pattern pattern = Pattern.compile(regex_chair);
                    // 创建匹配器
                    Matcher matcher = pattern.matcher(passwordStr_chair);
                    if (!matcher.matches()) {
                        throw new CustomException("密码格式不正确,密码是6位的数字");
                    }
                    busiTemplateConference.setChairmanPassword(passwordStr_chair);
                }

                if(busiMcuHwcloudConferenceAppointment.getParams().get("password")!=null){
                    String passwordStr = (String) busiMcuHwcloudConferenceAppointment.getParams().get("password");
                    if(Strings.isNotBlank(passwordStr)){
                        Pattern pattern = Pattern.compile(regex);
                        // 创建匹配器
                        Matcher matcher = pattern.matcher(passwordStr);
                        if (!matcher.matches()) {
                            throw new CustomException("密码格式不正确,密码是4到6位的数字");
                        }
                        busiTemplateConference.setGuestPassword(passwordStr);
                        busiTemplateConference.setConferencePassword(passwordStr);
                    }
                }

                if(busiMcuHwcloudConferenceAppointment.getParams().get("supportLive")!=null){
                    busiTemplateConference.setRecordingEnabled((Integer) busiMcuHwcloudConferenceAppointment.getParams().get("supportLive"));
                }
                if(busiMcuHwcloudConferenceAppointment.getParams().get("supportRecord")!=null){
                    busiTemplateConference.setRecordingEnabled((Integer) busiMcuHwcloudConferenceAppointment.getParams().get("supportRecord"));
                }
                busiTemplateConference.setMuteType(busiMcuHwcloudConferenceAppointment.getParams().get("muteType")==null?2:(Integer) busiMcuHwcloudConferenceAppointment.getParams().get("muteType"));
                if(busiMcuHwcloudConferenceAppointment.getParams().get("maxParticipantNum")==null){
                    busiTemplateConference.setMaxParticipantNum(500);
                }else {
                    busiTemplateConference.setMaxParticipantNum((Integer) busiMcuHwcloudConferenceAppointment.getParams().get("maxParticipantNum"));
                }
                busiTemplateConference.setName((String)busiMcuHwcloudConferenceAppointment.getParams().get("conferenceName"));

                Map<String, Object> businessProperties = (Map<String, Object>) busiMcuHwcloudConferenceAppointment.getParams().get("businessProperties");
                if(businessProperties==null){
                    businessProperties=new HashMap<>();
                    busiMcuHwcloudConferenceAppointment.getParams().put("businessProperties",businessProperties);
                }
                businessProperties.put("callInRestriction",busiMcuHwcloudConferenceAppointment.getParams().get("callInRestriction"));
                businessProperties.put("allowGuestStartConf",busiMcuHwcloudConferenceAppointment.getParams().get("allowGuestStartConf"));

                businessProperties.put("isSendCalendar",busiMcuHwcloudConferenceAppointment.getParams().get("isSendCalendar"));
                businessProperties.put("enableWaitingRoom",busiMcuHwcloudConferenceAppointment.getParams().get("enableWaitingRoom"));
                businessProperties.put("isSendNotify",busiMcuHwcloudConferenceAppointment.getParams().get("isSendNotify"));
                businessProperties.put("confPresetParam",busiMcuHwcloudConferenceAppointment.getParams().get("confPresetParam"));
                businessProperties.put("attendees",busiMcuHwcloudConferenceAppointment.getParams().get("templateParticipants"));

                busiTemplateConference.setBusinessProperties(businessProperties);
                BusiMcuHwcloudTemplateConference busiMcuHwcloudTemplateConference = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(viewConferenceAppointment.getTemplateId());
                busiTemplateConference.setId(busiMcuHwcloudTemplateConference.getId());
                busiTemplateConference.setConfId(busiMcuHwcloudTemplateConference.getConfId());
                busiTemplateConference.setConferenceNumber(busiMcuHwcloudTemplateConference.getConferenceNumber());
                busiMcuHwcloudConferenceAppointment.setTemplateId(busiMcuHwcloudTemplateConference.getId());
                int c = busiMcuHwcloudTemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, null);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (params.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    //同步修改预约会议
                    new StartTemplateConference().editConference(busiTemplateConference.getId(),busiMcuHwcloudConferenceAppointment.getStartTime());

                } else {
                    return RestResponse.fail();
                }
                int i = busiMcuHwcloudConferenceAppointmentService.updateBusiMcuHwcloudConferenceAppointment(busiMcuHwcloudConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuHwcloudConferenceAppointment.getId().toString(), 1000, null, busiMcuHwcloudConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case MCU_ZTE: {
                BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment = jsonObject.toJavaObject(BusiMcuZteConferenceAppointment.class);
                busiMcuZteConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                busiMcuZteConferenceAppointment.setCreateBy(loginUser.getUsername());
                if (viewConferenceAppointment.getRoomBookId() != null) {
                    busiMcuZteConferenceAppointment.setType(viewConferenceAppointment.getType());
                    busiMcuZteConferenceAppointment.setStartTime(viewConferenceAppointment.getStartTime());
                    busiMcuZteConferenceAppointment.setEndTime(viewConferenceAppointment.getEndTime());
                }

                JSONObject params = new JSONObject(busiMcuZteConferenceAppointment.getParams());
                Long masterTerminalId = params.getLong("masterTerminalId");
                BusiMcuZteTemplateConference busiTemplateConference = params.toJavaObject(BusiMcuZteTemplateConference.class);
                busiTemplateConference.setId(busiMcuZteConferenceAppointment.getTemplateId());

                JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                List<BusiMcuZteTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuZteTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuZteTemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = params.getJSONArray("templateDepts");
                List<BusiTemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiTemplateDept.class));
                }

                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());


                busiTemplateConference.setName((String)busiMcuZteConferenceAppointment.getParams().get("conferenceName"));

                Map<String, Object> businessProperties = (Map<String, Object>) busiMcuZteConferenceAppointment.getParams().get("businessProperties");
                if(businessProperties==null){
                    businessProperties=new HashMap<>();
                    busiMcuZteConferenceAppointment.getParams().put("businessProperties",businessProperties);
                }
                businessProperties.put("maxParticipantNum", busiMcuZteConferenceAppointment.getParams().get("maxParticipantNum"));
                businessProperties.put("multiPicControl", busiMcuZteConferenceAppointment.getParams().get("multiPicControl"));
                businessProperties.put("multiViewNumber", busiMcuZteConferenceAppointment.getParams().get("multiViewNumber"));
                businessProperties.put("dynamicRes", busiMcuZteConferenceAppointment.getParams().get("dynamicRes"));
                businessProperties.put("inviteWithSDP", busiMcuZteConferenceAppointment.getParams().get("inviteWithSDP"));
                businessProperties.put("conferenceTemplet", busiMcuZteConferenceAppointment.getParams().get("conferenceTemplet"));

                businessProperties.put("confCascadeMode", busiMcuZteConferenceAppointment.getParams().get("confCascadeMode"));
                businessProperties.put("enableMcuTitle", busiMcuZteConferenceAppointment.getParams().get("enableMcuTitle"));
                businessProperties.put("enableMcuBanner", busiMcuZteConferenceAppointment.getParams().get("enableMcuBanner"));
                businessProperties.put("enableVoiceRecord", busiMcuZteConferenceAppointment.getParams().get("enableVoiceRecord"));

                businessProperties.put("enableAutoVoiceRecord", busiMcuZteConferenceAppointment.getParams().get("enableAutoVoiceRecord"));
                businessProperties.put("enableUpConf", busiMcuZteConferenceAppointment.getParams().get("enableUpConf"));
                businessProperties.put("sendMail", busiMcuZteConferenceAppointment.getParams().get("sendMail"));

                busiTemplateConference.setBusinessProperties(businessProperties);
                BusiMcuZteTemplateConference busiMcuZteTemplateConference = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(viewConferenceAppointment.getTemplateId());
                busiTemplateConference.setId(busiMcuZteTemplateConference.getId());
                busiTemplateConference.setConfId(busiMcuZteTemplateConference.getConfId());
                busiTemplateConference.setConferenceNumber(busiMcuZteTemplateConference.getConferenceNumber());
                busiMcuZteConferenceAppointment.setTemplateId(busiMcuZteTemplateConference.getId());
                int c = busiMcuZteTemplateConferenceService.updateBusiMcuZteTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, null);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (params.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }else {
                            try {
                                JSONObject jsonObjectViewConfigInfo = new JSONObject();
                                jsonObjectViewConfigInfo.put("defaultViewPaticipants", params.get("defaultViewPaticipants"));
                                jsonObjectViewConfigInfo.put("defaultViewLayout",params.get("defaultViewLayout"));
                                jsonObjectViewConfigInfo.put("defaultViewIsBroadcast",params.get("defaultViewIsBroadcast"));
                                jsonObjectViewConfigInfo.put("defaultViewDepts",params.get("defaultViewDepts"));
                                jsonObjectViewConfigInfo.put("defaultViewCellScreens",params.get("defaultViewCellScreens"));
                                jsonObjectViewConfigInfo.put("pollingInterval",params.get("pollingInterval"));
                                updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                            }
                        }

                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    //同步修改预约会议
                   // new com.paradisecloud.fcm.zte.model.templateconference.StartTemplateConference().editConference(busiTemplateConference.getId(),busiMcuZteConferenceAppointment.getStartTime());

                } else {
                    return RestResponse.fail();
                }
                int i = busiMcuZteConferenceAppointmentService.updateBusiMcuZteConferenceAppointment(busiMcuZteConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuZteConferenceAppointment.getId().toString(), 1000, null, busiMcuZteConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
        }
        return RestResponse.fail();
    }

    /**
     * 删除会议预约记录
     */
    @DeleteMapping("/{apConferenceId}")
    @Operation(summary = "删除会议预约记录", description = "删除预约会议")
    public RestResponse remove(@PathVariable("apConferenceId") String apConferenceId)
    {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(apConferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();

        ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType.getCode(), id);
        ViewTemplateConference viewTemplateConference = null;
        List<ViewTemplateParticipant> viewTemplateParticipantList = null;
        if (viewConferenceAppointment != null) {
            String contextKey = EncryptIdUtil.generateContextKey(viewConferenceAppointment.getTemplateId(), viewConferenceAppointment.getMcuType());
            Long roomBookId = viewConferenceAppointment.getRoomBookId();
            if (roomBookId != null) {
                throw new SystemException("该会议为智慧办公预约的会议，不可删除！,请到智慧办公模块删除！");
            }
            viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType.getCode(), viewConferenceAppointment.getTemplateId());
            ViewTemplateParticipant viewTemplateParticipantTemp = new ViewTemplateParticipant();
            viewTemplateParticipantTemp.setMcuType(mcuType.getCode());
            viewTemplateParticipantTemp.setTemplateConferenceId(viewConferenceAppointment.getTemplateId());
            viewTemplateParticipantList = viewTemplateParticipantMapper.selectViewTemplateParticipantList(viewTemplateParticipantTemp);
            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
            if (baseConferenceContext != null) {
                throw new SystemException("当前预约会议模板正在开会，不能删除！");
            }
        }

        switch (mcuType) {
            case FME: {
                int i = busiConferenceAppointmentService.deleteBusiConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 1000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case MCU_ZJ: {
                int i = busiMcuZjConferenceAppointmentService.deleteBusiMcuZjConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 1000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case MCU_PLC: {
                int i = busiMcuPlcConferenceAppointmentService.deleteBusiMcuPlcConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 1000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case MCU_KDC: {
                int i = busiMcuKdcConferenceAppointmentService.deleteBusiMcuKdcConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 1000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case SMC3: {
                int i = busiMcuSmc3ConferenceAppointmentService.deleteBusiMcuSmc3ConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 1000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case SMC2: {
                int i = busiMcuSmc2ConferenceAppointmentService.deleteBusiMcuSmc2ConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 1000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case MCU_TENCENT: {
                int i = busiMcuTencentConferenceAppointmentService.deleteBusiMcuTencentConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 1000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case MCU_DING: {
                int i = busiMcuDingConferenceAppointmentService.deleteBusiMcuDingConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 1000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
            case MCU_HWCLOUD: {
                int i = busiMcuHwcloudConferenceAppointmentService.deleteBusiMcuHwcloudConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 1000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }

            case MCU_ZTE: {
                int i = busiMcuZteConferenceAppointmentService.deleteBusiMcuZteConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 1000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return toAjax(i);
            }
        }
        return RestResponse.fail();
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

    private void checkResource(BusiMcuSmc3ConferenceAppointment busiConferenceAppointment) {
        Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiConferenceAppointment.getDeptId());
        if (smc3Bridge != null) {
            if (smc3Bridge.getUsedResourceCount() >= smc3Bridge.getSystemResourceCount()) {
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

    private void checkResource(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment) {
        Smc2Bridge smc3Bridge = Smc2BridgeCache.getInstance().getBridgesByDept(busiConferenceAppointment.getDeptId());
        if (smc3Bridge != null) {
//            if (smc3Bridge.getUsedResourceCount() >= smc3Bridge.getSystemResourceCount()) {
//                if (busiConferenceAppointment.getType() == 2) {
//                    throw new SystemException(1, "MCU资源已耗尽，请关闭一些会议后重试或者稍后再试。");
//                } else if (busiConferenceAppointment.getType() == 1) {
//                    String startTimeStr = busiConferenceAppointment.getStartTime();
//                    Date startTime = DateUtil.convertDateByString(startTimeStr, "");
//                    if (startTime.getTime() - new Date().getTime() < 7200000) {
//                        throw new SystemException(1, "MCU资源已耗尽，创建2小时内的预约会议请先关闭一些会议后重试或者稍后再试。");
//                    }
//                }
//            }
        }
    }

    private ModelBean getTemplateInfo(String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                ModelBean modelBean = busiTemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case MCU_ZJ: {
                ModelBean modelBean = busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case MCU_PLC: {
                ModelBean modelBean = busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case MCU_KDC: {
                ModelBean modelBean = busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case SMC3: {
                ModelBean modelBean = busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                Object templateConference = modelBean.get("templateConference");
                BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = JSONObject.parseObject(JSONObject.toJSONString(templateConference), BusiMcuSmc3TemplateConference.class);
//                String cascadeNodes = busiMcuSmc3TemplateConference.getCascadeNodes();
//                if(Strings.isNotBlank(cascadeNodes)&&!Objects.equals("null",cascadeNodes)){
//                    modelBean.put("cascadeNodes",JSONArray.parseArray(cascadeNodes, TemplateNode.class));
//                }
                String cascadeNodesTemp = busiMcuSmc3TemplateConference.getCascadeNodesTemp();
                if (Strings.isNotBlank(cascadeNodesTemp) && !Objects.equals("null", cascadeNodesTemp)) {
                    modelBean.put("cascadeNodesTemp", JSONArray.parseArray(cascadeNodesTemp, TemplateNodeTemp.class));
                }
                Map<String, Object> businessProperties = busiMcuSmc3TemplateConference.getBusinessProperties();
                if(businessProperties!=null){
                    ModelBean tmb = new ModelBean(busiMcuSmc3TemplateConference);
                    tmb.put("videoProtocol", businessProperties.get("videoProtocol"));
                    tmb.put("audioProtocol", businessProperties.get("audioProtocol"));
                    tmb.put("mainMcuId", businessProperties.get("mainMcuId"));
                    tmb.put("mainServiceZoneId", businessProperties.get("mainServiceZoneId"));
                    tmb.put("mainServiceZoneName", businessProperties.get("mainServiceZoneName"));
                    tmb.put("videoResolution", businessProperties.get("videoResolution"));
                    tmb.put("mainMcuName", businessProperties.get("mainMcuName"));
                    tmb.put("streamService", businessProperties.get("streamService"));
                    modelBean.put("templateConference",tmb);
                }

                return modelBean;
            }
            case SMC2: {
                ModelBean modelBean = busiMcuSmc2TemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case MCU_TENCENT: {
                ModelBean modelBean = busiMcuTencentTemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                Object templateConference = modelBean.get("templateConference");
                BusiMcuTencentTemplateConference  busiMcuTencentTemplateConference = JSONObject.parseObject(JSONObject.toJSONString(templateConference), BusiMcuTencentTemplateConference.class);
                Map<String, Object> businessProperties = busiMcuTencentTemplateConference.getBusinessProperties();
                if (businessProperties != null) {
                    modelBean.put("templateParticipants", businessProperties.get("attendees"));
                }
                return modelBean;
            }

            case MCU_HWCLOUD: {
                ModelBean modelBean = busiMcuHwcloudTemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                Object templateConference = modelBean.get("templateConference");
                BusiMcuHwcloudTemplateConference busiMcuHwcloudTemplateConference = JSONObject.parseObject(JSONObject.toJSONString(templateConference), BusiMcuHwcloudTemplateConference.class);
                Map<String, Object> businessProperties = busiMcuHwcloudTemplateConference.getBusinessProperties();
                if (businessProperties != null) {
                    ModelBean tmb = new ModelBean(busiMcuHwcloudTemplateConference);
                    tmb.put("callInRestriction", businessProperties.get("callInRestriction"));
                    tmb.put("enableWaitingRoom", businessProperties.get("enableWaitingRoom"));
                    tmb.put("allowGuestStartConf", businessProperties.get("allowGuestStartConf"));
                    tmb.put("isSendNotify", businessProperties.get("isSendNotify"));
                    tmb.put("isSendCalendar", businessProperties.get("isSendCalendar"));
                    tmb.put("confPresetParam", businessProperties.get("confPresetParam"));
                    modelBean.put("templateConference", tmb);
                    modelBean.put("templateParticipants", businessProperties.get("attendees"));
                }

                return modelBean;
            }

            case MCU_ZTE: {
                ModelBean modelBean = busiMcuZteTemplateConferenceService.selectBusiMcuZteTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }

                Object templateConference = modelBean.get("templateConference");
                BusiMcuZteTemplateConference busiMcuZteTemplateConference = JSONObject.parseObject(JSONObject.toJSONString(templateConference), BusiMcuZteTemplateConference.class);
                Map<String, Object> businessProperties = busiMcuZteTemplateConference.getBusinessProperties();
                if (businessProperties != null) {
                    ModelBean tmb = new ModelBean(busiMcuZteTemplateConference);
                    tmb.put("maxParticipantNum", businessProperties.get("maxParticipantNum"));
                    tmb.put("multiPicControl", businessProperties.get("multiPicControl"));
                    tmb.put("multiViewNumber", businessProperties.get("multiViewNumber"));
                    tmb.put("dynamicRes", businessProperties.get("dynamicRes"));
                    tmb.put("inviteWithSDP", businessProperties.get("inviteWithSDP"));
                    tmb.put("conferenceTemplet", businessProperties.get("conferenceTemplet"));

                    tmb.put("confCascadeMode", businessProperties.get("confCascadeMode"));
                    tmb.put("enableMcuTitle", businessProperties.get("enableMcuTitle"));
                    tmb.put("enableMcuBanner", businessProperties.get("enableMcuBanner"));
                    tmb.put("enableVoiceRecord", businessProperties.get("enableVoiceRecord"));

                    tmb.put("enableAutoVoiceRecord", businessProperties.get("enableAutoVoiceRecord"));
                    tmb.put("enableUpConf", businessProperties.get("enableUpConf"));
                    tmb.put("sendMail", businessProperties.get("sendMail"));
                    modelBean.put("templateConference", tmb);
                    modelBean.put("templateParticipants", businessProperties.get("attendees"));
                }
                return modelBean;
            }
        }
        return null;
    }

    private void deleteCascadeTemplateConference(ViewTemplateConference viewTemplateConference) {
        Long templateId = viewTemplateConference.getId();
        String mcuTypeStr = viewTemplateConference.getMcuType();
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                busiTemplateConferenceMapper.deleteBusiTemplateConferenceById(templateId);
            }
            case MCU_ZJ: {
                busiMcuZjTemplateConferenceMapper.deleteBusiMcuZjTemplateConferenceById(templateId);
            }
            case MCU_PLC: {
                busiMcuPlcTemplateConferenceMapper.deleteBusiMcuPlcTemplateConferenceById(templateId);
            }
            case MCU_KDC: {
                busiMcuKdcTemplateConferenceMapper.deleteBusiMcuKdcTemplateConferenceById(templateId);
            }
            case SMC3: {
                busiMcuSmc3TemplateConferenceService.deleteBusiTemplateConferenceById(templateId);
            }
            case SMC2: {
                busiMcuSmc2TemplateConferenceService.deleteBusiTemplateConferenceById(templateId);
            }
            case MCU_TENCENT: {
                busiMcuTencentTemplateConferenceService.deleteBusiTemplateConferenceById(templateId);
            }
        }
    }

    private void updateCascadeTemplateConference(ViewTemplateConference viewTemplateConference, String upCascadeConferenceId, int upCascadeIndex) {
        updateCascadeTemplateConference(viewTemplateConference, upCascadeConferenceId, UpCascadeType.SELECT_TEMPLATE_OUT_MEETING, upCascadeIndex);
    }

    private void updateCascadeTemplateConference(ViewTemplateConference viewTemplateConference, String upCascadeConferenceId, UpCascadeType upCascadeType, int upCascadeIndex) {
        Long templateId = viewTemplateConference.getId();
        String mcuTypeStr = viewTemplateConference.getMcuType();
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                BusiTemplateConference busiTemplateConferenceUpdate = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_ZJ: {
                BusiMcuZjTemplateConference busiTemplateConferenceUpdate = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_PLC: {
                BusiMcuPlcTemplateConference busiTemplateConferenceUpdate = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_KDC: {
                BusiMcuKdcTemplateConference busiTemplateConferenceUpdate = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC3: {
                BusiMcuSmc3TemplateConference busiTemplateConferenceUpdate = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC2: {
                BusiMcuSmc2TemplateConference busiTemplateConferenceUpdate = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_TENCENT: {
                BusiMcuTencentTemplateConference busiTemplateConferenceUpdate = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }

            case MCU_DING: {
                BusiMcuDingTemplateConference busiTemplateConferenceUpdate = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }

        }
    }

    private void recoverUpdateCascadeTemplateConference(ViewTemplateConference viewTemplateConference) {
        Long templateId = viewTemplateConference.getId();
        String mcuTypeStr = viewTemplateConference.getMcuType();
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                BusiTemplateConference busiTemplateConferenceUpdate = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_ZJ: {
                BusiMcuZjTemplateConference busiTemplateConferenceUpdate = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_PLC: {
                BusiMcuPlcTemplateConference busiTemplateConferenceUpdate = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_KDC: {
                BusiMcuKdcTemplateConference busiTemplateConferenceUpdate = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC3: {
                BusiMcuSmc3TemplateConference busiTemplateConferenceUpdate = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC2: {
                BusiMcuSmc2TemplateConference busiTemplateConferenceUpdate = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }

            case MCU_TENCENT: {
                BusiMcuTencentTemplateConference busiTemplateConferenceUpdate = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }

            case MCU_DING: {
                BusiMcuDingTemplateConference busiTemplateConferenceUpdate = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
        }
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(viewTemplateConference.getConferenceId()));
        if (conferenceContext != null) {
            conferenceContext.setUpCascadeConferenceId(null);
            conferenceContext.setUpCascadeIndex(null);
            conferenceContext.setUpCascadeRemoteParty(null);
        }
    }

    private void addCascadeTemplateConference(JSONObject jsonObj, String upCascadeConferenceId) throws Exception {
        // 级联
        List<ViewTemplateConference> templateIdListAdd = new ArrayList();
        List<ViewTemplateConference> templateIdListUpdate = new ArrayList();
        try {
            if (jsonObj.containsKey("cascadeTemplateConferences")) {
                JSONArray jsonArrayCascadeTemplateConferences = jsonObj.getJSONArray("cascadeTemplateConferences");
                if (jsonArrayCascadeTemplateConferences != null && jsonArrayCascadeTemplateConferences.size() > 0) {
                    int index = 0;
                    for (Object jsonObjectTemplateConferenceObj : jsonArrayCascadeTemplateConferences) {
                        index = index + 1;
                        if (jsonObjectTemplateConferenceObj instanceof Map) {
                            Map jsonObjectTemplateConference = (Map) jsonObjectTemplateConferenceObj;
                            if (jsonObjectTemplateConference.containsKey("id")) {
                                Long templateId = Long.valueOf(jsonObjectTemplateConference.get("id").toString());
                                String mcuTypeStrCascade = String.valueOf(jsonObjectTemplateConference.get("mcuType"));
                                ViewTemplateConference busiTemplateConferenceCascade = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuTypeStrCascade, templateId);
                                if (busiTemplateConferenceCascade != null) {
                                    if (StringUtils.isNotEmpty(busiTemplateConferenceCascade.getUpCascadeConferenceId()) && !upCascadeConferenceId.equals(busiTemplateConferenceCascade.getUpCascadeConferenceId())) {
                                        throw new Exception("会议模板:" + busiTemplateConferenceCascade.getName() + ":已被其它会议级联！");
                                    }
                                    updateCascadeTemplateConference(busiTemplateConferenceCascade, upCascadeConferenceId, index);
                                    templateIdListUpdate.add(busiTemplateConferenceCascade);
                                } else {
                                    throw new Exception("找不到会议模板:templateId:" + templateId);
                                }
                            } else {
                                jsonObjectTemplateConference.put("upCascadeConferenceId", upCascadeConferenceId);
                                String mcuTypeStrCascade = jsonObjectTemplateConference.get("mcuType").toString();
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("templateConference", jsonObjectTemplateConference);
                                RestResponse restResponse = add(jsonObject);
                                ModelBean modelBean = (ModelBean) restResponse.getData();
                                ModelBean templateConferenceModelBean = (ModelBean) modelBean.get("templateConference");
                                Long templateId = (Long) templateConferenceModelBean.get("id");
                                ViewTemplateConference busiTemplateConferenceCascade = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuTypeStrCascade, templateId);
                                updateCascadeTemplateConference(busiTemplateConferenceCascade, upCascadeConferenceId, UpCascadeType.AUTO_CREATE, index);
                                templateIdListAdd.add(busiTemplateConferenceCascade);
                            }
                        }
                    }
                } else {
                    return;
                }
            }
        } catch (Exception e) {
            for (ViewTemplateConference viewTemplateConference : templateIdListAdd) {
                try {
                    deleteCascadeTemplateConference(viewTemplateConference);
                } catch (Exception e1) {
                    logger.error(e1.getMessage());
                }
            }
            for (ViewTemplateConference viewTemplateConference : templateIdListUpdate) {
                try {
                    recoverUpdateCascadeTemplateConference(viewTemplateConference);
                } catch (Exception e1) {
                    logger.error(e1.getMessage());
                }
            }
            throw e;
        }
    }

    private void editCascadeTemplateConference(JSONObject jsonObj, String upCascadeConferenceId) throws Exception {
        // 级联
        List<ViewTemplateConference> templateIdListAdd = new ArrayList();
        List<ViewTemplateConference> templateIdListUpdate = new ArrayList();
        Set<String> templateIdListCascade = new HashSet<>();
        try {
            if (jsonObj.containsKey("cascadeTemplateConferences")) {
                JSONArray jsonArrayCascadeTemplateConferences = jsonObj.getJSONArray("cascadeTemplateConferences");
                if (jsonArrayCascadeTemplateConferences != null && jsonArrayCascadeTemplateConferences.size() > 0) {
                    int index = 0;
                    for (Object jsonObjectTemplateConferenceObj : jsonArrayCascadeTemplateConferences) {
                        index = index + 1;
                        if (jsonObjectTemplateConferenceObj instanceof Map) {
                            Map jsonObjectTemplateConference = (Map) jsonObjectTemplateConferenceObj;
                            if (jsonObjectTemplateConference.containsKey("id")) {
                                Long templateId = Long.valueOf(jsonObjectTemplateConference.get("id").toString());
                                String mcuTypeStrCascade = String.valueOf(jsonObjectTemplateConference.get("mcuType"));
                                ViewTemplateConference busiTemplateConferenceCascade = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuTypeStrCascade, templateId);
                                if (busiTemplateConferenceCascade != null) {
                                    if (StringUtils.isNotEmpty(busiTemplateConferenceCascade.getUpCascadeConferenceId()) && !upCascadeConferenceId.equals(busiTemplateConferenceCascade.getUpCascadeConferenceId())) {
                                        throw new Exception("会议模板:" + busiTemplateConferenceCascade.getName() + ":已被其它会议级联！");
                                    }
                                    if (busiTemplateConferenceCascade.getUpCascadeId() != null) {
                                        if (busiTemplateConferenceCascade.getCreateType() == ConferenceTemplateCreateType.AUTO.getValue() && busiTemplateConferenceCascade.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
                                            String conferenceIdTemp = EncryptIdUtil.generateConferenceId(templateId, mcuTypeStrCascade);
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("templateConference", jsonObjectTemplateConference);
                                            edit(conferenceIdTemp, jsonObject);
                                            templateIdListUpdate.add(busiTemplateConferenceCascade);
                                            busiTemplateConferenceCascade = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuTypeStrCascade, templateId);
                                        }
                                    }
                                    updateCascadeTemplateConference(busiTemplateConferenceCascade, upCascadeConferenceId, index);
                                    templateIdListUpdate.add(busiTemplateConferenceCascade);
                                    templateIdListCascade.add(busiTemplateConferenceCascade.getConferenceId());
                                } else {
                                    throw new Exception("找不到会议模板:templateId:" + templateId);
                                }
                            } else {
                                jsonObjectTemplateConference.put("upCascadeConferenceId", upCascadeConferenceId);
                                String mcuTypeStrCascade = String.valueOf(jsonObjectTemplateConference.get("mcuType"));
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("templateConference", jsonObjectTemplateConference);
                                RestResponse restResponse = add(jsonObject);
                                ModelBean modelBean = (ModelBean) restResponse.getData();
                                ModelBean templateConferenceModelBean = (ModelBean) modelBean.get("templateConference");
                                Long templateId = (Long) templateConferenceModelBean.get("id");
                                ViewTemplateConference busiTemplateConferenceCascade = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuTypeStrCascade, templateId);
                                updateCascadeTemplateConference(busiTemplateConferenceCascade, upCascadeConferenceId, UpCascadeType.AUTO_CREATE, index);
                                templateIdListAdd.add(busiTemplateConferenceCascade);
                                templateIdListCascade.add(busiTemplateConferenceCascade.getConferenceId());
                            }
                        }
                    }
                }
            }
            ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
            Long id = conferenceIdVo.getId();
            McuType mcuType = conferenceIdVo.getMcuType();
            ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
            viewTemplateConferenceCascadeCon.setUpCascadeId(id);
            viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
            List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
            for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceList) {
                if (!templateIdListCascade.contains(viewTemplateConference.getConferenceId())) {
                    if (viewTemplateConference.getUpCascadeType() == UpCascadeType.AUTO_CREATE.getCode()) {
                        deleteCascadeTemplateConference(viewTemplateConference);
                    } else {
                        recoverUpdateCascadeTemplateConference(viewTemplateConference);
                    }
                }
            }
        } catch (Exception e) {
            for (ViewTemplateConference viewTemplateConference : templateIdListAdd) {
                try {
                    deleteCascadeTemplateConference(viewTemplateConference);
                } catch (Exception e1) {
                    logger.error(e1.getMessage());
                }
            }
            for (ViewTemplateConference viewTemplateConference : templateIdListUpdate) {
                try {
                    recoverUpdateCascadeTemplateConference(viewTemplateConference);
                } catch (Exception e1) {
                    logger.error(e1.getMessage());
                }
            }
            throw e;
        }
    }

    public RestResponse updateDefaultViewConfigInfo(JSONObject jsonObj, String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                busiTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
                return RestResponse.success();
            }
            case MCU_ZJ: {
                BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(id);
                if (busiMcuZjTemplateConference != null) {
                    McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(busiMcuZjTemplateConference.getDeptId()).getMasterMcuZjBridge();
                    if (mcuZjBridge != null) {
                        SourceTemplate sourceTemplate = mcuZjBridge.getSourceTemplateById(busiMcuZjTemplateConference.getResourceTemplateId());
                        if (sourceTemplate.getSingle_view() == 1) {
                            if (jsonObj.containsKey("guestDefaultViewData")) {
                                JSONObject jsonObjectGuest = jsonObj.getJSONObject("guestDefaultViewData");
                                busiMcuZjTemplateConferenceService.updateDefaultViewConfigInfoForGuest(jsonObjectGuest, id);
                            } else if (jsonObj.containsKey("speakerDefaultViewData")) {
                                JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                                busiMcuZjTemplateConferenceService.updateDefaultViewConfigInfoForGuest(jsonObjectSpeaker, id);
                            }
                        } else {
                            JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                            busiMcuZjTemplateConferenceService.updateDefaultViewConfigInfo(jsonObjectSpeaker, id);
                            if (jsonObj.containsKey("guestDefaultViewData")) {
                                JSONObject jsonObjectGuest = jsonObj.getJSONObject("guestDefaultViewData");
                                busiMcuZjTemplateConferenceService.updateDefaultViewConfigInfoForGuest(jsonObjectGuest, id);
                            }
                        }
                        return RestResponse.success();
                    }
                }
            }
            case MCU_PLC: {
                BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(id);
                if (busiMcuPlcTemplateConference != null) {
                    McuPlcBridge mcuPlcBridge = McuPlcBridgeCache.getInstance().getAvailableMcuPlcBridgesByDept(busiMcuPlcTemplateConference.getDeptId()).getMasterMcuPlcBridge();
                    if (mcuPlcBridge != null) {
                        JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                        busiMcuPlcTemplateConferenceService.updateDefaultViewConfigInfo(jsonObjectSpeaker, id);
                        return RestResponse.success();
                    }
                }
            }
            case MCU_KDC: {
                BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(id);
                if (busiMcuKdcTemplateConference != null) {
                    McuKdcBridge mcuKdcBridge = McuKdcBridgeCache.getInstance().getAvailableMcuKdcBridgesByDept(busiMcuKdcTemplateConference.getDeptId()).getMasterMcuKdcBridge();
                    if (mcuKdcBridge != null) {
                        JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                        busiMcuKdcTemplateConferenceService.updateDefaultViewConfigInfo(jsonObjectSpeaker, id);
                        return RestResponse.success();
                    }
                }
            }
            case SMC3: {
                busiMcuSmc3TemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
            }
            case SMC2: {
                busiMcuSmc2TemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
            }
            case MCU_TENCENT: {
                busiMcuTencentTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
            }
            case MCU_ZTE: {
                busiMcuZteTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
            }
        }
        return RestResponse.fail();
    }


}

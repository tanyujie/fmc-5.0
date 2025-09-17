package com.paradisecloud.fcm.web.controller.mcu.all;

import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.AppointmentConferenceStatus;
import com.paradisecloud.fcm.common.enumer.ConferenceTemplateCreateType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceApprovalExcludeVo;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceApprovalVo;
import com.paradisecloud.fcm.dao.model.vo.ConferenceApprovalSearchVo;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceAppointmentService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceAppointmentService;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2ConferenceAppointmentService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentConferenceAppointmentService;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3ConferenceAppointmentService;
import com.paradisecloud.system.dao.mapper.SysUserMapper;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 会议审批Controller
 *
 * @author lilinhai
 * @date 2021-05-24
 */
@RestController
@RequestMapping("/busi/mcu/all/conferenceApproval")
@Tag(name = "会议审批")
public class BusiAllConferenceApprovalController extends BaseController {

    @Resource
    private BusiConferenceApprovalMapper busiConferenceApprovalMapper;
    @Resource
    private IBusiConferenceAppointmentService busiConferenceAppointmentService;
    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;
    @Resource
    private IBusiMcuZjConferenceAppointmentService busiMcuZjConferenceAppointmentService;
    @Resource
    private BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper;
    @Resource
    private IBusiMcuPlcConferenceAppointmentService busiMcuPlcConferenceAppointmentService;
    @Resource
    private BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper;
    @Resource
    private IBusiMcuKdcConferenceAppointmentService busiMcuKdcConferenceAppointmentService;
    @Resource
    private IBusiMcuSmc3ConferenceAppointmentService busiMcuSmc3ConferenceAppointmentService;
    @Resource
    private IBusiMcuSmc2ConferenceAppointmentService busiMcuSmc2ConferenceAppointmentService;
    @Resource
    private IBusiMcuTencentConferenceAppointmentService busiMcuTencentConferenceAppointmentService;
    @Resource
    private IBusiMcuHwcloudConferenceAppointmentService busiMcuHwcloudConferenceAppointmentService;
    @Resource
    private BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper;
    @Resource
    private BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper;
    @Resource
    private BusiMcuSmc2TemplateConferenceMapper busiMcuSmc2TemplateConferenceMapper;
    @Resource
    private BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper;
    @Resource
    private BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper;
    @Resource
    private BusiConferenceApprovalExcludeMapper busiConferenceApprovalExcludeMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 查询审批记录列表
     */
    @PostMapping(value = "/list")
    @Operation(summary = "查询审批记录列表")
    public RestResponse list(@RequestBody(required = false) BusiConferenceApproval busiConferenceApproval)
    {
        startPage();
        PaginationData<BusiConferenceApprovalVo> pd = new PaginationData<>();
        List<BusiConferenceApproval> list = busiConferenceApprovalMapper.selectBusiConferenceApprovalList(busiConferenceApproval);
        for (BusiConferenceApproval busiConferenceApprovalTemp : list) {
            BusiConferenceApprovalVo busiConferenceApprovalVo = new BusiConferenceApprovalVo();
            BeanUtils.copyProperties(busiConferenceApprovalTemp, busiConferenceApprovalVo);
            String deptName = "";
            if (busiConferenceApprovalTemp.getDeptId() != null) {
                SysDept sysDept = SysDeptCache.getInstance().get(busiConferenceApprovalTemp.getDeptId());
                if (sysDept != null) {
                    deptName = sysDept.getDeptName();
                }
            }
            busiConferenceApprovalVo.setDeptName(deptName);
            pd.addRecord(busiConferenceApprovalVo);
        }
        PageInfo<?> pageInfo = new PageInfo<>(list);
        pd.setTotal(pageInfo.getTotal());
        pd.setSize(pageInfo.getSize());
        pd.setPage(pageInfo.getPageNum());
        return RestResponse.success(pd);
    }

    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiConferenceApprovalMapper.getDeptRecordCounts());
    }

    /**
     * 查询审批记录列表
     */
    @PostMapping(value = "/searchList")
    @Operation(summary = "查询审批记录列表")
    public RestResponse searchList(@RequestBody(required = false) ConferenceApprovalSearchVo conferenceApprovalSearchVo)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = loginUser.getUser().getDeptId();
        Long searchDeptId = conferenceApprovalSearchVo.getDeptId();
        startPage();
        PaginationData<BusiConferenceApprovalVo> pd = new PaginationData<>();
        if ((deptId == null || deptId <= 100) && searchDeptId != null && searchDeptId <= 100) {
            conferenceApprovalSearchVo.setDeptId(null);
        }
        if (deptId != null && deptId > 100 && (searchDeptId == null || searchDeptId <= 100)) {
            conferenceApprovalSearchVo.setDeptId(deptId);
        }
        List<BusiConferenceApproval> list = busiConferenceApprovalMapper.searchBusiConferenceApprovalList(conferenceApprovalSearchVo);
        for (BusiConferenceApproval busiConferenceApprovalTemp : list) {
            BusiConferenceApprovalVo busiConferenceApprovalVo = new BusiConferenceApprovalVo();
            BeanUtils.copyProperties(busiConferenceApprovalTemp, busiConferenceApprovalVo);
            String deptName = "";
            if (busiConferenceApprovalTemp.getDeptId() != null) {
                SysDept sysDept = SysDeptCache.getInstance().get(busiConferenceApprovalTemp.getDeptId());
                if (sysDept != null) {
                    deptName = sysDept.getDeptName();
                }
            }
            busiConferenceApprovalVo.setDeptName(deptName);
            pd.addRecord(busiConferenceApprovalVo);
        }
        PageInfo<?> pageInfo = new PageInfo<>(list);
        pd.setTotal(pageInfo.getTotal());
        pd.setSize(pageInfo.getSize());
        pd.setPage(pageInfo.getPageNum());
        return RestResponse.success(pd);
    }

    /**
     * 审批通过
     */
    @PutMapping(value = "/approve/{id}")
    @Operation(summary = "审批通过", description = "审批通过")
    public RestResponse approve(@PathVariable("id") Long id)
    {
        BusiConferenceApproval busiConferenceApprovalExist = busiConferenceApprovalMapper.selectBusiConferenceApprovalById(id);
        if (busiConferenceApprovalExist == null) {
            return RestResponse.fail("该会议审批申请不存在！");
        }
        if (busiConferenceApprovalExist.getApprovalStatus() != 0) {
            return RestResponse.fail("该会议审批已被其他人更新！请刷新后重试！");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long approvalUserId = loginUser.getUser().getUserId();
        String approvalBy = loginUser.getUser().getNickName();
        busiConferenceApprovalExist.setApprovalUserId(approvalUserId);
        busiConferenceApprovalExist.setApprovalBy(approvalBy);
        busiConferenceApprovalExist.setApprovalTime(new Date());
        busiConferenceApprovalExist.setApprovalStatus(1);
        int rows = busiConferenceApprovalMapper.updateBusiConferenceApproval(busiConferenceApprovalExist);
        if (rows > 0) {
            McuType mcuType = McuType.convert(busiConferenceApprovalExist.getMcuType());
            switch (mcuType) {
                case FME: {
                    BusiConferenceAppointment busiConferenceAppointment = busiConferenceAppointmentService.selectBusiConferenceAppointmentById(busiConferenceApprovalExist.getAppointmentConferenceId());
                    busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                    busiConferenceAppointmentService.updateBusiConferenceAppointment(busiConferenceAppointment, false);
                }
                case MCU_ZJ: {
                    BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = busiMcuZjConferenceAppointmentService.selectBusiMcuZjConferenceAppointmentById(busiConferenceApprovalExist.getAppointmentConferenceId());
                    busiMcuZjConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                    busiMcuZjConferenceAppointmentService.updateBusiMcuZjConferenceAppointment(busiMcuZjConferenceAppointment, false);
                }
                case MCU_PLC: {
                    BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment = busiMcuPlcConferenceAppointmentService.selectBusiMcuPlcConferenceAppointmentById(busiConferenceApprovalExist.getAppointmentConferenceId());
                    busiMcuPlcConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                    busiMcuPlcConferenceAppointmentService.updateBusiMcuPlcConferenceAppointment(busiMcuPlcConferenceAppointment, false);
                }
                case MCU_KDC: {
                    BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment = busiMcuKdcConferenceAppointmentService.selectBusiMcuKdcConferenceAppointmentById(busiConferenceApprovalExist.getAppointmentConferenceId());
                    busiMcuKdcConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                    busiMcuKdcConferenceAppointmentService.updateBusiMcuKdcConferenceAppointment(busiMcuKdcConferenceAppointment, false);
                }
                case SMC3: {
                    BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment = busiMcuSmc3ConferenceAppointmentService.selectBusiMcuSmc3ConferenceAppointmentById(busiConferenceApprovalExist.getAppointmentConferenceId());
                    busiMcuSmc3ConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                    busiMcuSmc3ConferenceAppointmentService.updateBusiMcuSmc3ConferenceAppointment(busiMcuSmc3ConferenceAppointment, false);
                }
                case SMC2: {
                    BusiMcuSmc2ConferenceAppointment busiMcuSmc2ConferenceAppointment = busiMcuSmc2ConferenceAppointmentService.selectBusiMcuSmc2ConferenceAppointmentById(busiConferenceApprovalExist.getAppointmentConferenceId());
                    busiMcuSmc2ConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                    busiMcuSmc2ConferenceAppointmentService.updateBusiMcuSmc2ConferenceAppointment(busiMcuSmc2ConferenceAppointment, false);
                }
                case MCU_TENCENT: {
                    BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = busiMcuTencentConferenceAppointmentService.selectBusiMcuTencentConferenceAppointmentById(busiConferenceApprovalExist.getAppointmentConferenceId());
                    busiMcuTencentConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                    busiMcuTencentConferenceAppointmentService.updateBusiMcuTencentConferenceAppointment(busiMcuTencentConferenceAppointment, false);
                }
                case MCU_DING: {
                }
                case MCU_HWCLOUD: {
                    BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment = busiMcuHwcloudConferenceAppointmentService.selectBusiMcuHwcloudConferenceAppointmentById(busiConferenceApprovalExist.getAppointmentConferenceId());
                    busiMcuHwcloudConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                    busiMcuHwcloudConferenceAppointmentService.updateBusiMcuHwcloudConferenceAppointment(busiMcuHwcloudConferenceAppointment, false);
                }
            }
        }
        return toAjax(rows);
    }

    /**
     * 审批不通过
     */
    @PutMapping(value = "/reject/{id}")
    @Operation(summary = "审批不通过", description = "审批不通过")
    public RestResponse reject(@PathVariable("id") Long id, @RequestBody BusiConferenceApproval busiConferenceApproval)
    {
        BusiConferenceApproval busiConferenceApprovalExist = busiConferenceApprovalMapper.selectBusiConferenceApprovalById(id);
        if (busiConferenceApprovalExist == null) {
            return RestResponse.fail("该会议审批申请不存在！");
        }
        if (busiConferenceApprovalExist.getApprovalStatus() != 0) {
            return RestResponse.fail("该会议审批已被其他人更新！请刷新后重试！");
        }
        if (StringUtils.isEmpty(busiConferenceApproval.getApprovalFailReason())) {
            return RestResponse.fail("请填写审批不通过原因！");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long approvalUserId = loginUser.getUser().getUserId();
        String approvalBy = loginUser.getUser().getNickName();
        busiConferenceApprovalExist.setApprovalUserId(approvalUserId);
        busiConferenceApprovalExist.setApprovalBy(approvalBy);
        busiConferenceApprovalExist.setApprovalTime(new Date());
        busiConferenceApprovalExist.setApprovalFailReason(busiConferenceApproval.getApprovalFailReason());
        busiConferenceApprovalExist.setApprovalStatus(2);
        int rows = busiConferenceApprovalMapper.updateBusiConferenceApproval(busiConferenceApprovalExist);
        if (rows > 0) {
            Long appointmentConferenceId = busiConferenceApprovalExist.getAppointmentConferenceId();
            McuType mcuType = McuType.convert(busiConferenceApprovalExist.getMcuType());
            switch (mcuType) {
                case FME: {
                    BusiConferenceAppointment busiConferenceAppointment = busiConferenceAppointmentService.selectBusiConferenceAppointmentById(appointmentConferenceId);
                    if (busiConferenceAppointment != null && ConferenceTemplateCreateType.convert(busiConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO) {
                        BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        if (busiTemplateConference != null && StringUtils.isNotEmpty(busiTemplateConference.getStreamUrl())) {
                            busiTemplateConference.setStreamUrl(null);
                            busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConference);
                        }
                    }
                }
                case MCU_ZJ: {
                    BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = busiMcuZjConferenceAppointmentService.selectBusiMcuZjConferenceAppointmentById(appointmentConferenceId);
                    if (busiMcuZjConferenceAppointment != null && ConferenceTemplateCreateType.convert(busiMcuZjConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO) {
                        BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(busiMcuZjConferenceAppointment.getTemplateId());
                        if (busiMcuZjTemplateConference != null && StringUtils.isNotEmpty(busiMcuZjTemplateConference.getStreamUrl())) {
                            busiMcuZjTemplateConference.setStreamUrl(null);
                            busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
                        }
                    }
                }
                case MCU_PLC: {
                    BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment = busiMcuPlcConferenceAppointmentService.selectBusiMcuPlcConferenceAppointmentById(appointmentConferenceId);
                    if (busiMcuPlcConferenceAppointment != null && ConferenceTemplateCreateType.convert(busiMcuPlcConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO) {
                        BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(busiMcuPlcConferenceAppointment.getTemplateId());
                        if (busiMcuPlcTemplateConference != null && StringUtils.isNotEmpty(busiMcuPlcTemplateConference.getStreamUrl())) {
                            busiMcuPlcTemplateConference.setStreamUrl(null);
                            busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiMcuPlcTemplateConference);
                        }
                    }
                }
                case MCU_KDC: {
                    BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment = busiMcuKdcConferenceAppointmentService.selectBusiMcuKdcConferenceAppointmentById(appointmentConferenceId);
                    if (busiMcuKdcConferenceAppointment != null && ConferenceTemplateCreateType.convert(busiMcuKdcConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO) {
                        BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiMcuKdcConferenceAppointment.getTemplateId());
                        if (busiMcuKdcTemplateConference != null && StringUtils.isNotEmpty(busiMcuKdcTemplateConference.getStreamUrl())) {
                            busiMcuKdcTemplateConference.setStreamUrl(null);
                            busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiMcuKdcTemplateConference);
                        }
                    }
                }
                case SMC3: {
                    BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment = busiMcuSmc3ConferenceAppointmentService.selectBusiMcuSmc3ConferenceAppointmentById(appointmentConferenceId);
                    if (busiMcuSmc3ConferenceAppointment != null && ConferenceTemplateCreateType.convert(busiMcuSmc3ConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO) {
                        BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(busiMcuSmc3ConferenceAppointment.getTemplateId());
                        if (busiMcuSmc3TemplateConference != null && StringUtils.isNotEmpty(busiMcuSmc3TemplateConference.getStreamUrl())) {
                            busiMcuSmc3TemplateConference.setStreamUrl(null);
                            busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConference);
                        }
                    }
                }
                case SMC2: {
                    BusiMcuSmc2ConferenceAppointment busiMcuSmc2ConferenceAppointment = busiMcuSmc2ConferenceAppointmentService.selectBusiMcuSmc2ConferenceAppointmentById(appointmentConferenceId);
                    if (busiMcuSmc2ConferenceAppointment != null && ConferenceTemplateCreateType.convert(busiMcuSmc2ConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO) {
                        BusiMcuSmc2TemplateConference busiMcuSmc2TemplateConference = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(busiMcuSmc2ConferenceAppointment.getTemplateId());
                        if (busiMcuSmc2TemplateConference != null && StringUtils.isNotEmpty(busiMcuSmc2TemplateConference.getStreamUrl())) {
                            busiMcuSmc2TemplateConference.setStreamUrl(null);
                            busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiMcuSmc2TemplateConference);
                        }
                    }
                }
                case MCU_TENCENT: {
                    BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = busiMcuTencentConferenceAppointmentService.selectBusiMcuTencentConferenceAppointmentById(appointmentConferenceId);
                    if (busiMcuTencentConferenceAppointment != null && ConferenceTemplateCreateType.convert(busiMcuTencentConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO) {
                        BusiMcuTencentTemplateConference busiMcuTencentTemplateConference = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(busiMcuTencentConferenceAppointment.getTemplateId());
                        if (busiMcuTencentTemplateConference != null && StringUtils.isNotEmpty(busiMcuTencentTemplateConference.getStreamUrl())) {
                            busiMcuTencentTemplateConference.setStreamUrl(null);
                            busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(busiMcuTencentTemplateConference);
                        }
                    }
                }
                case MCU_DING: {
                }
                case MCU_HWCLOUD: {
                    BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment = busiMcuHwcloudConferenceAppointmentService.selectBusiMcuHwcloudConferenceAppointmentById(appointmentConferenceId);
                    if (busiMcuHwcloudConferenceAppointment != null && ConferenceTemplateCreateType.convert(busiMcuHwcloudConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO) {
                        BusiMcuHwcloudTemplateConference busiMcuHwcloudTemplateConference = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(busiMcuHwcloudConferenceAppointment.getTemplateId());
                        if (busiMcuHwcloudTemplateConference != null && StringUtils.isNotEmpty(busiMcuHwcloudTemplateConference.getStreamUrl())) {
                            busiMcuHwcloudTemplateConference.setStreamUrl(null);
                            busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(busiMcuHwcloudTemplateConference);
                        }
                    }
                }
            }
        }
        return toAjax(rows);
    }

    /**
     * 查询会议审批排除列表
     */
    @GetMapping("/exclude/list")
    @Operation(summary = "查询会议审批排除列表")
    public RestResponse list(BusiConferenceApprovalExclude busiConferenceApprovalExclude)
    {
        startPage();
        PaginationData<BusiConferenceApprovalExcludeVo> pd = new PaginationData<>();
        List<BusiConferenceApprovalExclude> list = busiConferenceApprovalExcludeMapper.selectBusiConferenceApprovalExcludeList(busiConferenceApprovalExclude);
        for (BusiConferenceApprovalExclude busiConferenceApprovalExcludeTemp : list) {
            BusiConferenceApprovalExcludeVo busiConferenceApprovalExcludeVo = new BusiConferenceApprovalExcludeVo();
            BeanUtils.copyProperties(busiConferenceApprovalExcludeTemp, busiConferenceApprovalExcludeVo);
            String deptName = "";
            String userName = "";
            if (busiConferenceApprovalExcludeTemp.getExcludeId() != null) {
                if (busiConferenceApprovalExcludeTemp.getType() == 1) {
                    busiConferenceApprovalExcludeVo.setTypeName("用户");
                    SysUser sysUser = sysUserMapper.selectUserById(busiConferenceApprovalExcludeTemp.getExcludeId());
                    if (sysUser != null) {
                        userName = sysUser.getUserName();
                        SysDept sysDept = SysDeptCache.getInstance().get(sysUser.getDeptId());
                        if (sysDept != null) {
                            deptName = sysDept.getDeptName();
                        }
                    }
                } else {
                    busiConferenceApprovalExcludeVo.setTypeName("部门");
                    SysDept sysDept = SysDeptCache.getInstance().get(busiConferenceApprovalExcludeTemp.getExcludeId());
                    if (sysDept != null) {
                        deptName = sysDept.getDeptName();
                    }
                    busiConferenceApprovalExcludeVo.setRemark("该部门（不包含下级部门）的用户预约会议无需审批");
                }
            }
            busiConferenceApprovalExcludeVo.setDeptName(deptName);
            busiConferenceApprovalExcludeVo.setUserName(userName);
            pd.addRecord(busiConferenceApprovalExcludeVo);
        }
        PageInfo<?> pageInfo = new PageInfo<>(list);
        pd.setTotal(pageInfo.getTotal());
        pd.setSize(pageInfo.getSize());
        pd.setPage(pageInfo.getPageNum());
        return RestResponse.success(pd);
    }

    /**
     * 获取会议审批排除详细信息
     */
    @GetMapping(value = "/exclude/{id}")
    @Operation(summary = "获取会议审批排除详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        BusiConferenceApprovalExclude busiConferenceApprovalExcludeTemp = busiConferenceApprovalExcludeMapper.selectBusiConferenceApprovalExcludeById(id);
        BusiConferenceApprovalExcludeVo busiConferenceApprovalExcludeVo = new BusiConferenceApprovalExcludeVo();
        BeanUtils.copyProperties(busiConferenceApprovalExcludeTemp, busiConferenceApprovalExcludeVo);
        String deptName = "";
        String userName = "";
        if (busiConferenceApprovalExcludeTemp.getExcludeId() != null) {
            if (busiConferenceApprovalExcludeTemp.getType() == 1) {
                busiConferenceApprovalExcludeVo.setTypeName("用户");
                SysUser sysUser = sysUserMapper.selectUserById(busiConferenceApprovalExcludeTemp.getExcludeId());
                if (sysUser != null) {
                    userName = sysUser.getUserName();
                    SysDept sysDept = SysDeptCache.getInstance().get(sysUser.getDeptId());
                    if (sysDept != null) {
                        deptName = sysDept.getDeptName();
                    }
                }
            } else {
                busiConferenceApprovalExcludeVo.setTypeName("部门");
                SysDept sysDept = SysDeptCache.getInstance().get(busiConferenceApprovalExcludeTemp.getExcludeId());
                if (sysDept != null) {
                    deptName = sysDept.getDeptName();
                }
                busiConferenceApprovalExcludeVo.setRemark("该部门（不包含下级部门）的用户预约会议无需审批");
            }
        }
        busiConferenceApprovalExcludeVo.setDeptName(deptName);
        busiConferenceApprovalExcludeVo.setUserName(userName);
        return RestResponse.success(busiConferenceApprovalExcludeVo);
    }

    /**
     * 新增会议审批排除
     */
    @PostMapping(value = "/exclude")
    @Operation(summary = "新增会议审批排除", description = "新增会议审批排除")
    public RestResponse add(@RequestBody BusiConferenceApprovalExclude busiConferenceApprovalExclude)
    {
        return toAjax(busiConferenceApprovalExcludeMapper.insertBusiConferenceApprovalExclude(busiConferenceApprovalExclude));
    }

    /**
     * 修改会议审批排除
     */
    @PutMapping(value = "/exclude")
    @Operation(summary = "修改会议审批排除", description = "修改会议审批排除")
    public RestResponse edit(@RequestBody BusiConferenceApprovalExclude busiConferenceApprovalExclude)
    {
        return toAjax(busiConferenceApprovalExcludeMapper.updateBusiConferenceApprovalExclude(busiConferenceApprovalExclude));
    }

    /**
     * 删除会议审批排除
     */
    @DeleteMapping("/exclude/{ids}")
    @Operation(summary = "删除会议审批排除", description = "删除会议审批排除")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiConferenceApprovalExcludeMapper.deleteBusiConferenceApprovalExcludeByIds(ids));
    }
}

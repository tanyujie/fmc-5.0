package com.paradisecloud.fcm.web.controller.smartRoom;

import java.util.*;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.DeviceType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomBookVo;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiHwcloudConferenceService;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceService;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceService;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomDeviceCache;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomBookService;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceMapService;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomParticipantService;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiSmc2ConferenceService;
import com.paradisecloud.fcm.tencent.cache.TencentRoomsCache;
import com.paradisecloud.fcm.tencent.model.MeetingRoom;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.web.service.interfaces.IBusiAllConferenceAppointmentService;
import com.paradisecloud.fcm.web.service.interfaces.IBusiAllMcuService;
import com.paradisecloud.fcm.web.service.interfaces.IBusiUserService;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3ConferenceService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.paradisecloud.fcm.web.utils.AuthenticationUtil.getDeptId;

/**
 * 会议室预约Controller
 *
 * @author lilinhai
 * @date 2024-02-20
 */
@RestController
@RequestMapping("/busi/roomBook")
@Tag(name = "会议室预约")
public class BusiSmartRoomBookController extends BaseController {
    @Resource
    private IBusiSmartRoomBookService busiSmartRoomBookService;
    @Resource
    private IBusiUserService busiUserService;
    @Resource
    private IBusiAllConferenceAppointmentService busiAllConferenceAppointmentService;
    @Resource
    private IBusiAllMcuService busiAllMcuService;
    @Resource
    private IBusiSmartRoomParticipantService busiSmartRoomParticipantService;
    @Resource
    private ViewConferenceAppointmentMapper viewConferenceAppointmentMapper;
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
    private IBusiSmc2ConferenceService busiSmc2ConferenceService;
    @Resource
    private IBusiHwcloudConferenceService busiHwcloudConferenceService;


    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;
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
    private BusiMcuSmc2TemplateConferenceMapper busiMcuSmc2TemplateConferenceMapper;
    @Resource
    private BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper;
    @Resource
    private BusiMcuDingTemplateConferenceMapper busiMcuDingTemplateConferenceMapper;
    @Resource
    private BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper;

    @Resource
    private IBusiSmartRoomDeviceMapService busiSmartRoomDeviceMapService;


    /**
     * 查询会议室预约列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询会议室预约列表")
    public RestResponse list(BusiSmartRoomBookVo busiSmartRoomBookVo) {
        startPage();
        List<BusiSmartRoomBook> list = busiSmartRoomBookService.selectBusiSmartRoomBookList(busiSmartRoomBookVo);
        Map<String, SysUser> userMap = new HashMap<>();
        for (BusiSmartRoomBook busiSmartRoomBook : list) {
            String createByName = "";
            String createByDeptName = "";
            if (StringUtils.isNotEmpty(busiSmartRoomBook.getCreateBy())) {
                SysUser sysUser = userMap.get(busiSmartRoomBook.getCreateBy());
                if (sysUser == null) {
                    sysUser = busiUserService.selectUserByUserName(busiSmartRoomBook.getCreateBy());
                }
                if (sysUser != null) {
                    createByName = sysUser.getNickName();
                    if (sysUser.getDept() != null) {
                        createByDeptName = sysUser.getDept().getDeptName();
                    }
                }
            }
            BusiSmartRoomParticipant busiSmartRoomParticipant = new BusiSmartRoomParticipant();
            busiSmartRoomParticipant.setBookId(busiSmartRoomBook.getId());
            List<BusiSmartRoomParticipant> busiSmartRoomParticipantList = busiSmartRoomParticipantService.selectBusiSmartRoomParticipantList(busiSmartRoomParticipant);
            busiSmartRoomBook.getParams().put("participants", busiSmartRoomParticipantList);
            busiSmartRoomBook.getParams().put("createByName", createByName);
            busiSmartRoomBook.getParams().put("createByDeptName", createByDeptName);
        }
        return getDataTable(list);
    }

    /**
     * 获取会议室预约详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取会议室预约详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id) {
        BusiSmartRoomBook busiSmartRoomBook = busiSmartRoomBookService.selectBusiSmartRoomBookById(id);
        String createByName = "";
        String createByDeptName = "";
        if (StringUtils.isNotEmpty(busiSmartRoomBook.getCreateBy())) {
            SysUser sysUser = busiUserService.selectUserByUserName(busiSmartRoomBook.getCreateBy());
            if (sysUser != null) {
                createByName = sysUser.getNickName();
                if (sysUser.getDept() != null) {
                    createByDeptName = sysUser.getDept().getDeptName();
                }
            }
        }
        busiSmartRoomBook.getParams().put("createByName", createByName);
        busiSmartRoomBook.getParams().put("createByDeptName", createByDeptName);
        BusiSmartRoomParticipant busiSmartRoomParticipant = new BusiSmartRoomParticipant();
        busiSmartRoomParticipant.setBookId(busiSmartRoomBook.getId());
        List<BusiSmartRoomParticipant> busiSmartRoomParticipantList = busiSmartRoomParticipantService.selectBusiSmartRoomParticipantList(busiSmartRoomParticipant);
        busiSmartRoomBook.getParams().put("participants", busiSmartRoomParticipantList);
        return RestResponse.success(busiSmartRoomBook);
    }

    /**
     * 新增会议室预约
     */
    @PreAuthorize("@ss.hasPermi('busi:book:add')")
    @Log(title = "会议室预约", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增会议室预约")
    public RestResponse add(@RequestBody BusiSmartRoomBook busiSmartRoomBook) {
        int i = busiSmartRoomBookService.insertBusiSmartRoomBook(busiSmartRoomBook);
        try {
            Map<String, Object> params = busiSmartRoomBook.getParams();
            Boolean isAppointmenConfernce = false;
            if (params != null && params.size() > 0 && i > 0) {
                if (params.containsKey("isNeedAppointmentConference")) {
                    isAppointmenConfernce = (Boolean) params.get("isNeedAppointmentConference");
                }
                if (isAppointmenConfernce) {
                    LoginUser loginUser = SecurityUtils.getLoginUser();
                    Long deptId = 100L;
                    if (loginUser != null) {
                        SysDept dept = loginUser.getUser().getDept();
                        if (dept != null && dept.getDeptId() != null) {
                            deptId = dept.getDeptId();
                        }
                    }
                    if (!params.containsKey("businessFieldType")) {
                        params.put("businessFieldType", 100);
                    }
                    if (!params.containsKey("conferenceName")) {
                        params.put("conferenceName", busiSmartRoomBook.getBookName());
                    }
                    if (!params.containsKey("deptId")) {
                        params.put("deptId", deptId);
                    }
                    if (!params.containsKey("duration")) {
                        params.put("duration", 24);
                    }
                    if (!params.containsKey("endTime")) {
                        params.put("endTime", busiSmartRoomBook.getEndTime());
                    }
                    if (!params.containsKey("isAutoCreateTemplate")) {
                        params.put("isAutoCreateTemplate", 1);
                    }
                    if (!params.containsKey("muteType")) {
                        params.put("muteType", busiSmartRoomBook.getMcuType());
                    }
                    if (!params.containsKey("repeatValue")) {
                        params.put("repeatValue", 1);
                    }
                    if (!params.containsKey("startTime")) {
                        params.put("startTime", busiSmartRoomBook.getStartTime());
                    }
                    if (!params.containsKey("status")) {
                        params.put("status", 1);
                    }
                    if (!params.containsKey("supportLive")) {
                        params.put("supportLive", 2);
                    }
                    if (!params.containsKey("supportRecord")) {
                        params.put("supportRecord", 2);
                    }
                    if (!params.containsKey("type")) {
                        params.put("type", 1);
                    }
                    if (!params.containsKey("isAutoCall")) {
                        params.put("isAutoCall", 1);
                    }
                    if (!params.containsKey("conferenceName")) {
                        params.put("conferenceName", busiSmartRoomBook.getBookName());
                    }
                    if (!params.containsKey("defaultViewLayout")) {
                        params.put("defaultViewLayout", "allEqual");
                    }
                    if (!params.containsKey("defaultViewIsDisplaySelf")) {
                        params.put("defaultViewIsDisplaySelf", -1);
                    }
                    if (!params.containsKey("recordingEnabled")) {
                        params.put("recordingEnabled", 2);
                    }
                    if (!params.containsKey("streamingEnabled")) {
                        params.put("streamingEnabled", 2);
                    }
                    if (!params.containsKey("defaultViewIsBroadcast")) {
                        params.put("defaultViewIsBroadcast", 2);
                    }
                    if (!params.containsKey("defaultViewIsFill")) {
                        params.put("defaultViewIsFill", 1);
                    }
                    if (!params.containsKey("templateParticipants")) {
                        BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = new BusiSmartRoomDeviceMap();
                        busiSmartRoomDeviceMap.setRoomId(busiSmartRoomBook.getRoomId());
                        List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMap);
                        List<Map<String, Object>> mapList = new ArrayList<>();
                        if (busiSmartRoomDeviceMapList != null && busiSmartRoomDeviceMapList.size() > 0) {
                            Boolean isTencentMcu = false;
                            if (busiSmartRoomBook.getMcuType() == McuType.MCU_TENCENT.getCode()) {
                                isTencentMcu = true;
                            }
                            for (BusiSmartRoomDeviceMap smartRoomDeviceMap : busiSmartRoomDeviceMapList) {
                                BusiSmartRoomDevice busiSmartRoomDevice = SmartRoomDeviceCache.getInstance().get(smartRoomDeviceMap.getDeviceId());
                                if (busiSmartRoomDevice != null) {
                                    Integer deviceType = busiSmartRoomDevice.getDeviceType();
                                    if (DeviceType.isBindId(deviceType)) {
                                        if (deviceType == DeviceType.TERMINAL.getCode()) {
                                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(Long.valueOf(busiSmartRoomDevice.getBindId()));
                                            if (busiTerminal != null) {
                                                SysDept sysDept = SysDeptCache.getInstance().get(busiTerminal.getDeptId());
                                                if (sysDept != null) {
                                                    String ancestors = sysDept.getAncestors();
                                                    if (ancestors.contains(String.valueOf(deptId)) || deptId == busiTerminal.getDeptId()) {
                                                        Map<String, Object> map = new HashMap<>();
                                                        map.put("id", busiTerminal.getId());
                                                        map.put("terminalId", busiTerminal.getId());
                                                        map.put("attendType", 1);
                                                        map.put("weight", 1);
                                                        map.put("businessProperties", "");
                                                        mapList.add(map);
                                                    }
                                                }
                                            }
                                        }
                                        if (isTencentMcu) {
                                            if (deviceType == DeviceType.TENCENT_ROOMS.getCode()) {
                                                MeetingRoom meetingRoom = TencentRoomsCache.getInstance().getMeetingRoom(busiSmartRoomDevice.getBindId());
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("id", meetingRoom.getMeetingRoomId());
                                                map.put("terminalId", meetingRoom.getMeetingRoomId());
                                                map.put("attendType", 1);
                                                map.put("weight", 1);
                                                map.put("businessProperties", "");
                                                mapList.add(map);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        params.put("templateParticipants", mapList);
                    }


                    BusiConferenceAppointment busiConferenceAppointment = new BusiConferenceAppointment();
                    busiConferenceAppointment.setDeptId(Long.valueOf(deptId));
                    String endTimeStr = DateUtil.convertDateToString(busiSmartRoomBook.getEndTime(), null);
                    String startTimeStr = DateUtil.convertDateToString(busiSmartRoomBook.getStartTime(), null);
                    busiConferenceAppointment.setEndTime(endTimeStr);
                    busiConferenceAppointment.setStartTime(startTimeStr);
                    busiConferenceAppointment.setIsAutoCreateTemplate(1);
                    busiConferenceAppointment.setRepeatRate(1);
                    busiConferenceAppointment.setStatus(2);
                    busiConferenceAppointment.setType(1);
                    busiConferenceAppointment.setParams(params);
                    busiConferenceAppointment.setRoomBookId(busiSmartRoomBook.getId());
                    Map<String, Object> map = busiAllConferenceAppointmentService.addConferenceAppointment(busiConferenceAppointment, busiSmartRoomBook.getMcuType());
                    Long appointmentId = (Long) map.get("appointmentId");
                    if (appointmentId != null) {
                        busiSmartRoomBook.setAppointmentConferenceId(appointmentId);
                        busiSmartRoomBookService.updateBusiSmartRoomBookData(busiSmartRoomBook);
                        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
                        String contextKey = EncryptIdUtil.generateContextKey(appointmentId, busiSmartRoomBook.getMcuType());
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(contextKey);
                        objectObjectHashMap.put("apConferenceId", generateEncryptId);
                        busiSmartRoomBook.setParams(objectObjectHashMap);
                        return RestResponse.success(busiSmartRoomBook);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (busiSmartRoomBook.getId() != null) {
                busiSmartRoomBookService.cancelBusiSmartRoomBookById(busiSmartRoomBook.getId());
            }
            if (e instanceof CustomException) {
                return RestResponse.fail(e.getMessage());
            }
            return RestResponse.fail();
        }
        return toAjax(i);
    }

    /**
     * 修改会议室预约
     */
    @PreAuthorize("@ss.hasPermi('busi:book:edit')")
    @Log(title = "会议室预约", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改会议室预约")
    public RestResponse edit(@RequestBody BusiSmartRoomBook busiSmartRoomBook) {

        BusiSmartRoomBook busiSmartRoomBookTemp = busiSmartRoomBookService.selectBusiSmartRoomBookById(busiSmartRoomBook.getId());
        Long appointmentConferenceId = busiSmartRoomBookTemp.getAppointmentConferenceId();
        int i = busiSmartRoomBookService.updateBusiSmartRoomBook(busiSmartRoomBook);
        Map<String, Object> params = busiSmartRoomBook.getParams();
        Boolean isAppointmenConfernce = false;
        if (busiSmartRoomBook != null && params != null && params.size() > 0 && i > 0) {
            if (params.containsKey("isNeedAppointmentConference")) {
                isAppointmenConfernce = (Boolean) params.get("isNeedAppointmentConference");
            }
            if (isAppointmenConfernce) {

                LoginUser loginUser = SecurityUtils.getLoginUser();
                Long deptId = 100L;
                if (loginUser != null) {
                    SysDept dept = loginUser.getUser().getDept();
                    if (dept != null && dept.getDeptId() != null) {
                        deptId = dept.getDeptId();
                    }
                }
                if (!params.containsKey("businessFieldType")) {
                    params.put("businessFieldType", 100);
                }
                if (!params.containsKey("conferenceName")) {
                    params.put("conferenceName", busiSmartRoomBook.getBookName());
                }
                if (!params.containsKey("deptId")) {
                    params.put("deptId", getDeptId());
                }
                if (!params.containsKey("duration")) {
                    params.put("duration", 24);
                }
                if (!params.containsKey("endTime")) {
                    params.put("endTime", busiSmartRoomBook.getEndTime());
                }
                if (!params.containsKey("isAutoCreateTemplate")) {
                    params.put("isAutoCreateTemplate", 1);
                }
                if (!params.containsKey("muteType")) {
                    params.put("muteType", busiSmartRoomBook.getMcuType());
                }
                if (!params.containsKey("repeatValue")) {
                    params.put("repeatValue", 1);
                }
                if (!params.containsKey("startTime")) {
                    params.put("startTime", busiSmartRoomBook.getStartTime());
                }
                if (!params.containsKey("status")) {
                    params.put("status", 1);
                }
                if (!params.containsKey("supportLive")) {
                    params.put("supportLive", 2);
                }
                if (!params.containsKey("supportRecord")) {
                    params.put("supportRecord", 2);
                }
                if (!params.containsKey("type")) {
                    params.put("type", 1);
                }
                if (!params.containsKey("isAutoCall")) {
                    params.put("isAutoCall", 1);
                }
                if (!params.containsKey("conferenceName")) {
                    params.put("conferenceName", busiSmartRoomBook.getBookName());
                }
                if (!params.containsKey("defaultViewLayout")) {
                    params.put("defaultViewLayout", "allEqual");
                }
                if (!params.containsKey("defaultViewIsDisplaySelf")) {
                    params.put("defaultViewIsDisplaySelf", -1);
                }
                if (!params.containsKey("recordingEnabled")) {
                    params.put("recordingEnabled", 2);
                }
                if (!params.containsKey("streamingEnabled")) {
                    params.put("streamingEnabled", 2);
                }
                if (!params.containsKey("defaultViewIsBroadcast")) {
                    params.put("defaultViewIsBroadcast", 2);
                }
                if (!params.containsKey("defaultViewIsFill")) {
                    params.put("defaultViewIsFill", 1);
                }
                if (!params.containsKey("templateParticipants")) {
                    BusiSmartRoomDeviceMap busiSmartRoomDeviceMap = new BusiSmartRoomDeviceMap();
                    busiSmartRoomDeviceMap.setRoomId(busiSmartRoomBook.getRoomId());
                    List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList = busiSmartRoomDeviceMapService.selectBusiSmartRoomDeviceMapList(busiSmartRoomDeviceMap);
                    List<Map<String, Object>> mapList = new ArrayList<>();
                    if (busiSmartRoomDeviceMapList != null && busiSmartRoomDeviceMapList.size() > 0) {
                        Boolean isTencentMcu = false;
                        if (busiSmartRoomBook.getMcuType() == McuType.MCU_TENCENT.getCode()) {
                            isTencentMcu = true;
                        }
                        for (BusiSmartRoomDeviceMap smartRoomDeviceMap : busiSmartRoomDeviceMapList) {
                            BusiSmartRoomDevice busiSmartRoomDevice = SmartRoomDeviceCache.getInstance().get(smartRoomDeviceMap.getDeviceId());
                            if (busiSmartRoomDevice != null) {
                                Integer deviceType = busiSmartRoomDevice.getDeviceType();
                                if (DeviceType.isBindId(deviceType)) {
                                    if (deviceType == DeviceType.TERMINAL.getCode()) {
                                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(Long.valueOf(busiSmartRoomDevice.getBindId()));
                                        if (busiTerminal != null) {
                                            SysDept sysDept = SysDeptCache.getInstance().get(busiTerminal.getDeptId());
                                            if (sysDept != null) {
                                                String ancestors = sysDept.getAncestors();
                                                if (ancestors.contains(String.valueOf(deptId)) || deptId == busiTerminal.getDeptId()) {
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("id", busiTerminal.getId());
                                                    map.put("terminalId", busiTerminal.getId());
                                                    map.put("attendType", 1);
                                                    map.put("weight", 1);
                                                    map.put("businessProperties", "");
                                                    mapList.add(map);
                                                }
                                            }
                                        }
                                    }
                                    if (isTencentMcu) {
                                        if (deviceType == DeviceType.TENCENT_ROOMS.getCode()) {
                                            MeetingRoom meetingRoom = TencentRoomsCache.getInstance().getMeetingRoom(busiSmartRoomDevice.getBindId());
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("id", meetingRoom.getMeetingRoomId());
                                            map.put("terminalId", meetingRoom.getMeetingRoomId());
                                            map.put("attendType", 1);
                                            map.put("weight", 1);
                                            map.put("businessProperties", "");
                                            mapList.add(map);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    params.put("templateParticipants", mapList);
                }

                BusiConferenceAppointment busiConferenceAppointment = new BusiConferenceAppointment();
                busiConferenceAppointment.setDeptId(Long.valueOf(deptId));
                String endTimeStr = DateUtil.convertDateToString(busiSmartRoomBook.getEndTime(), null);
                String startTimeStr = DateUtil.convertDateToString(busiSmartRoomBook.getStartTime(), null);
                busiConferenceAppointment.setEndTime(endTimeStr);
                busiConferenceAppointment.setStartTime(startTimeStr);
                busiConferenceAppointment.setIsAutoCreateTemplate(1);
                busiConferenceAppointment.setRepeatRate(1);
                busiConferenceAppointment.setStatus(2);
                busiConferenceAppointment.setType(1);
                busiConferenceAppointment.setParams(params);
                busiConferenceAppointment.setRoomBookId(busiSmartRoomBook.getId());

                if (appointmentConferenceId == null) {
                    Map<String, Object> map = busiAllConferenceAppointmentService.addConferenceAppointment(busiConferenceAppointment, busiSmartRoomBook.getMcuType());
                    try {
                        Long appointmentId = (Long) map.get("appointmentId");
                        if (appointmentId != null) {
                            busiSmartRoomBook.setAppointmentConferenceId(appointmentId);
                            busiSmartRoomBookService.updateBusiSmartRoomBookData(busiSmartRoomBook);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String mcuTypeStr = busiSmartRoomBookTemp.getMcuType();

                    ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuTypeStr, busiSmartRoomBookTemp.getAppointmentConferenceId());
                    BusiConferenceAppointment busiConferenceAppointmentTemp = new BusiConferenceAppointment();
                    busiConferenceAppointmentTemp.setEndTime(endTimeStr);
                    busiConferenceAppointmentTemp.setStartTime(startTimeStr);
                    busiConferenceAppointmentTemp.setId(viewConferenceAppointment.getId());
                    int i1 = busiAllConferenceAppointmentService.onlyEditConferenceAppointment(busiConferenceAppointmentTemp, mcuTypeStr);
                    if (i1 > 0) {
                        if (viewConferenceAppointment != null) {
                            Long templateId = viewConferenceAppointment.getTemplateId();
                            String bookName = busiSmartRoomBook.getBookName();
                            McuType mcuType = McuType.convert(mcuTypeStr);
                            switch (mcuType) {
                                case FME: {
                                    i = busiTemplateConferenceService.updateBusiTemplateConferenceName(templateId, bookName);
                                    break;
                                }
                                case MCU_ZJ: {
                                    BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(templateId);
                                    busiMcuZjTemplateConference.setId(templateId);
                                    busiMcuZjTemplateConference.setName(bookName);
                                    i = busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
                                    break;
                                }
                                case MCU_PLC: {
                                    BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(templateId);
                                    busiMcuPlcTemplateConference.setId(templateId);
                                    busiMcuPlcTemplateConference.setName(bookName);
                                    i = busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiMcuPlcTemplateConference);
                                    break;
                                }
                                case MCU_KDC: {
                                    BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(templateId);
                                    busiMcuKdcTemplateConference.setId(templateId);
                                    busiMcuKdcTemplateConference.setName(bookName);
                                    i = busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiMcuKdcTemplateConference);
                                    break;
                                }
                                case SMC3: {
                                    BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateId);
                                    busiMcuSmc3TemplateConference.setId(templateId);
                                    busiMcuSmc3TemplateConference.setName(bookName);
                                    i = busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConference);
                                    break;
                                }
                                case SMC2: {
                                    BusiMcuSmc2TemplateConference busiMcuSmc2TemplateConference = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(templateId);
                                    busiMcuSmc2TemplateConference.setId(templateId);
                                    busiMcuSmc2TemplateConference.setName(bookName);
                                    i = busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiMcuSmc2TemplateConference);
                                    break;
                                }
                                case MCU_TENCENT: {
                                    BusiMcuTencentTemplateConference busiMcuTencentTemplateConference = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(templateId);
                                    busiMcuTencentTemplateConference.setId(templateId);
                                    busiMcuTencentTemplateConference.setName(bookName);
                                    i = busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(busiMcuTencentTemplateConference);
                                    break;
                                }
                                case MCU_DING: {
                                    BusiMcuDingTemplateConference busiMcuDingTemplateConference = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(templateId);
                                    busiMcuDingTemplateConference.setId(templateId);
                                    busiMcuDingTemplateConference.setName(bookName);
                                    i = busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiMcuDingTemplateConference);
                                    break;
                                }
                                case MCU_HWCLOUD: {
                                    BusiMcuHwcloudTemplateConference busiMcuHwcloudTemplateConference = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(templateId);
                                    busiMcuHwcloudTemplateConference.setId(templateId);
                                    busiMcuHwcloudTemplateConference.setName(bookName);
                                    i = busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(busiMcuHwcloudTemplateConference);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                if (appointmentConferenceId != null) {
                    String mcuType = busiSmartRoomBookTemp.getMcuType();
                    if (StringUtils.isNotEmpty(mcuType)) {
                        String contextKey = EncryptIdUtil.generateContextKey(appointmentConferenceId, mcuType);
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(contextKey);
                        if (StringUtils.isNotEmpty(generateEncryptId)) {
                            busiAllConferenceAppointmentService.removeConferenceAppointment(generateEncryptId);
                        }
                    }
                    try {
                        busiSmartRoomBook.setAppointmentConferenceId(null);
                        busiSmartRoomBook.setMcuType(null);
                        busiSmartRoomBookService.updateBusiSmartRoomBookData(busiSmartRoomBook);
                        return RestResponse.success(busiSmartRoomBook);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (busiSmartRoomBook.getAppointmentConferenceId() != null && busiSmartRoomBook.getMcuType() != null) {
            HashMap<String, Object> objectObjectHashMap = new HashMap<>();
            String contextKey = EncryptIdUtil.generateContextKey(busiSmartRoomBook.getAppointmentConferenceId(), busiSmartRoomBook.getMcuType());
            String generateEncryptId = EncryptIdUtil.generateEncryptId(contextKey);
            objectObjectHashMap.put("apConferenceId", generateEncryptId);
            busiSmartRoomBook.setParams(objectObjectHashMap);
        }
        return RestResponse.success(busiSmartRoomBook);
    }

    /**
     * 取消会议室预约
     */
    @PreAuthorize("@ss.hasPermi('busi:book:remove')")
    @Log(title = "会议室预约", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "取消会议室预约")
    public RestResponse remove(@PathVariable Long[] ids) {
        int i = 0;
        for (Long id : ids) {
            int i1 = busiSmartRoomBookService.cancelBusiSmartRoomBookById(id);
            i = i + i1;
            if (i1 > 0) {
                try {
                    BusiSmartRoomBook busiSmartRoomBook = busiSmartRoomBookService.selectBusiSmartRoomBookById(id);
                    if (busiSmartRoomBook != null) {
                        Long appointmentConferenceId = busiSmartRoomBook.getAppointmentConferenceId();
                        String mcuType = busiSmartRoomBook.getMcuType();
                        if (appointmentConferenceId != null && StringUtils.isNotEmpty(mcuType)) {
                            String contextKey = EncryptIdUtil.generateContextKey(appointmentConferenceId, mcuType);
                            String generateEncryptId = EncryptIdUtil.generateEncryptId(contextKey);
                            if (StringUtils.isNotEmpty(generateEncryptId)) {
                                busiAllConferenceAppointmentService.removeConferenceAppointment(generateEncryptId);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return toAjax(i);
    }

    /**
     * 结束使用会议室预约信息
     */
    @PreAuthorize("@ss.hasPermi('busi:book:end')")
    @Log(title = "结束使用会议室预约信息", businessType = BusinessType.DELETE)
    @PostMapping("/end/{id}")
    @Operation(summary = "结束使用会议室预约信息")
    public RestResponse end(@PathVariable Long id) {
        int i = busiSmartRoomBookService.endBusiSmartRoomBookById(id);
        if (i > 0) {
            BusiSmartRoomBook busiSmartRoomBook = busiSmartRoomBookService.selectBusiSmartRoomBookById(id);
            if (busiSmartRoomBook != null) {
                Long appointmentConferenceId = busiSmartRoomBook.getAppointmentConferenceId();
                String mcuType = busiSmartRoomBook.getMcuType();
                if (appointmentConferenceId != null && StringUtils.isNotEmpty(mcuType)) {
                    ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType, appointmentConferenceId);
                    if (viewConferenceAppointment != null) {
                        Long templateId = viewConferenceAppointment.getTemplateId();
                        if (templateId != null) {
                            String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
                            String generateEncryptId = EncryptIdUtil.generateEncryptId(contextKey);
                            if (StringUtils.isNotEmpty(generateEncryptId)) {
                                busiAllConferenceAppointmentService.endConference(generateEncryptId, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                            }
                        }
                    }
                }
            }
        }
        return toAjax(i);
    }

    /**
     * 查询会议室某月的预约列表
     */
    @GetMapping("/bookList/{roomId}/{dateMonth}")
    @Operation(summary = "查询会议室某月的预约列表")
    public RestResponse bookList(@PathVariable Long roomId, @PathVariable Date dateMonth) {
        Map<String, Object> data = new HashMap<>();
        data.put("dateMonth", dateMonth);
        List<Map<String, Object>> list = busiSmartRoomBookService.selectBusiSmartRoomBookListForRoomTerm(roomId, dateMonth);
        data.put("dateDayList", list);
        return RestResponse.success(data);
    }

    /**
     * 查询会议室某天的预约列表
     */
    @GetMapping("/bookListOfDay/{roomId}/{dateDay}")
    @Operation(summary = "查询会议室某天的预约列表")
    public RestResponse bookListByDay(@PathVariable Long roomId, @PathVariable Date dateDay) {
        List<BusiSmartRoomBook> list = busiSmartRoomBookService.selectBusiSmartRoomBookListForRoomDay(roomId, dateDay);
        Map<String, SysUser> userMap = new HashMap<>();
        for (BusiSmartRoomBook busiSmartRoomBook : list) {
            String createByName = "";
            String createByDeptName = "";
            if (StringUtils.isNotEmpty(busiSmartRoomBook.getCreateBy())) {
                SysUser sysUser = userMap.get(busiSmartRoomBook.getCreateBy());
                if (sysUser == null) {
                    sysUser = busiUserService.selectUserByUserName(busiSmartRoomBook.getCreateBy());
                }
                if (sysUser != null) {
                    createByName = sysUser.getNickName();
                    if (sysUser.getDept() != null) {
                        createByDeptName = sysUser.getDept().getDeptName();
                    }
                }
            }
            BusiSmartRoomParticipant busiSmartRoomParticipant = new BusiSmartRoomParticipant();
            busiSmartRoomParticipant.setBookId(busiSmartRoomBook.getId());
            List<BusiSmartRoomParticipant> busiSmartRoomParticipantList = busiSmartRoomParticipantService.selectBusiSmartRoomParticipantList(busiSmartRoomParticipant);
            busiSmartRoomBook.getParams().put("participants", busiSmartRoomParticipantList);
            busiSmartRoomBook.getParams().put("createByName", createByName);
            busiSmartRoomBook.getParams().put("createByDeptName", createByDeptName);
        }
        return RestResponse.success(list);
    }

    /**
     * 延长预约时间
     */
    @PreAuthorize("@ss.hasPermi('busi:book:extendMinutes')")
    @PostMapping("/extendMinutes/{id}/{minutes}")
    @Operation(summary = "延长预约时间")
    public RestResponse extendMinutes(@PathVariable Long id, @PathVariable Integer minutes) {
        BusiSmartRoomBook busiSmartRoomBook = busiSmartRoomBookService.selectBusiSmartRoomBookById(id);
        int i = 0;
        if (busiSmartRoomBook != null) {
            i = busiSmartRoomBookService.extendMinutes(id, minutes);
            if (i > 0) {
                Long appointmentConferenceId = busiSmartRoomBook.getAppointmentConferenceId();
                String mcuType = busiSmartRoomBook.getMcuType();
                if (appointmentConferenceId != null && StringUtils.isNotEmpty(mcuType)) {
                    ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType, appointmentConferenceId);
                    if (viewConferenceAppointment != null) {
                        Long templateId = viewConferenceAppointment.getTemplateId();
                        if (templateId != null) {
                            String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
                            String conferenceId = EncryptIdUtil.generateEncryptId(contextKey);
                            if (StringUtils.isNotEmpty(conferenceId)) {
                                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                                if (baseConferenceContext instanceof ConferenceContext) {
                                    busiConferenceService.extendMinutes(conferenceId, minutes);
                                } else if (baseConferenceContext instanceof McuZjConferenceContext) {
                                    busiMcuZjConferenceService.extendMinutes(conferenceId, minutes);
                                } else if (baseConferenceContext instanceof McuPlcConferenceContext) {
                                    busiMcuPlcConferenceService.extendMinutes(conferenceId, minutes);
                                } else if (baseConferenceContext instanceof McuKdcConferenceContext) {
                                    busiMcuKdcConferenceService.extendMinutes(conferenceId, minutes);
                                } else if (baseConferenceContext instanceof Smc3ConferenceContext) {
                                    busiSmc3ConferenceService.extendMinutes(conferenceId, minutes);
                                } else if (baseConferenceContext instanceof Smc2ConferenceContext) {
                                    busiSmc2ConferenceService.extendMinutes(conferenceId, minutes);
                                } else if (baseConferenceContext instanceof HwcloudConferenceContext) {
                                    busiHwcloudConferenceService.extendMinutes(conferenceId, minutes);
                                }
                            }
                        }
                    }
                }
            }
        }
        return toAjax(i);
    }
}

package com.paradisecloud.fcm.web.controller.mobile.web.smartRoom;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.DeptConstant;
import com.paradisecloud.fcm.dao.model.BusiSmartRoom;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomBook;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDevice;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDeviceMap;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomVo;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomCache;
import com.paradisecloud.fcm.smartroom.model.MeetingRoomInfo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * 智慧办公房间Controller
 *
 * @author lilinhai
 * @date 2024-02-19
 */
@RestController
@RequestMapping("/mobileWeb/room")
@Tag(name = "智慧办公房间")
public class MobileBusiSmartRoomController extends BaseController
{
    @Resource
    private IBusiSmartRoomService busiSmartRoomService;

    /**
     * 查询智慧办公房间列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询智慧办公房间列表")
    public RestResponse list(BusiSmartRoomVo busiSmartRoomVo)
    {
        Long deptId = busiSmartRoomVo.getDeptId();
        if (deptId == null) {
            busiSmartRoomVo.setDeptId(getDeptId());
        }
        startPage();
        List<BusiSmartRoom> list = busiSmartRoomService.selectBusiSmartRoomList(busiSmartRoomVo);
        return getDataTable(list);
    }

    /**
     * 获取智慧办公房间详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取智慧办公房间详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiSmartRoomService.selectBusiSmartRoomById(id));
    }

    /**
     * 新增智慧办公房间
     */
    @PreAuthorize("@ss.hasPermi('busi:room:add')")
    @Log(title = "智慧办公房间", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增智慧办公房间")
    public RestResponse add(@RequestBody BusiSmartRoom busiSmartRoom)
    {
        return toAjax(busiSmartRoomService.insertBusiSmartRoom(busiSmartRoom));
    }

    /**
     * 修改智慧办公房间
     */
    @PreAuthorize("@ss.hasPermi('busi:room:edit')")
    @Log(title = "智慧办公房间", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改智慧办公房间")
    public RestResponse edit(@RequestBody BusiSmartRoom busiSmartRoom)
    {
        return toAjax(busiSmartRoomService.updateBusiSmartRoom(busiSmartRoom));
    }

    /**
     * 删除智慧办公房间
     */
    @PreAuthorize("@ss.hasPermi('busi:room:remove')")
    @Log(title = "智慧办公房间", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除智慧办公房间")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiSmartRoomService.deleteBusiSmartRoomByIds(ids));
    }

    /**
     * 获取未绑定门牌智慧办公房间
     */
    @GetMapping(value = "/notBoundRoom")
    @Operation(summary = "获取未绑定门牌智慧办公房间")
    public RestResponse notBoundDoorplate(@RequestParam(value = "doorplateId", required = false) Long doorplateId)
    {
        return RestResponse.success(busiSmartRoomService.selectBusiSmartRoomListForDoorplateNotBound(doorplateId));
    }

    /**
     * 查询智慧办公房间预约列表通过部门
     */
    @GetMapping("/getSmartRoomBookList")
    @Operation(summary = "查询智慧办公房间预约列表")
    public RestResponse getSmartRoomBookListByDeptId(Long deptId)
    {
        startPage();
        List<MeetingRoomInfo> list = busiSmartRoomService.getSmartRoomBookListByDeptId(deptId);
        for (MeetingRoomInfo meetingRoomInfo : list) {
            if (meetingRoomInfo.getStatus() == 1) {
                long meetingRoomInfoId = meetingRoomInfo.getId();
                BusiSmartRoomBook terminalBook = SmartRoomCache.getInstance().getTerminalBook(meetingRoomInfoId);
                if (terminalBook != null) {
                    meetingRoomInfo.setCurrent(terminalBook);
                    meetingRoomInfo.setStatus(3);
                    List<BusiSmartRoomBook> allList = meetingRoomInfo.getAllList();
                    allList.add(terminalBook);
                }
            }
        }
        return getDataTable(list);
    }

    /**
     * 查询部门下智慧办公房间总数
     */
    @GetMapping("/getDeptSmartRoomCount/{deptId}")
    @Operation(summary = "查询部门下智慧办公房间总数")
    public RestResponse getDeptSmartRoomCount(@PathVariable Long deptId)
    {
        String topDeptAncestors = "";
        if (deptId != null) {
            SysDept sysDept = SysDeptCache.getInstance().get(deptId);
            if (sysDept != null) {
                if (sysDept.getAncestors() != null) {
                    topDeptAncestors = sysDept.getAncestors();
                }
            }
        }
        List<Map<String, Long>> allDeptMeetingRoomCount = SmartRoomCache.getInstance().getAllDeptMeetingRoomCount();
        List<Map<String, Long>> deptMeetingRoomCountNew = new ArrayList<>();
        if (StringUtils.isNotEmpty(topDeptAncestors)) {
            for (Map<String, Long> countMapTemp : allDeptMeetingRoomCount) {
                Long deptIdTemp = countMapTemp.get("deptId");
                if (deptIdTemp != null) {
                    SysDept sysDept = SysDeptCache.getInstance().get(deptIdTemp);
                    if (sysDept != null) {
                        if (sysDept.getAncestors() != null && sysDept.getAncestors().length() >= topDeptAncestors.length()) {
                            deptMeetingRoomCountNew.add(countMapTemp);
                        }
                    }
                }
            }
        }

        Collection<BusiSmartRoom> values = SmartRoomCache.getInstance().values();
        Map<String, Long> countMapTemp = new HashMap<>();
        countMapTemp.put("deptId", DeptConstant.SMART_ROOM_DEPT_ID);
        countMapTemp.put("count", Long.valueOf(values.size()));
        deptMeetingRoomCountNew.add(countMapTemp);

        return RestResponse.success(deptMeetingRoomCountNew);
    }

    /**
     * 查询会议室信息
     */
    @GetMapping("/getMeetingRoomInfo/{id}")
    @Operation(summary = "查询会议室信息")
    public RestResponse getMeetingRoomInfo(@PathVariable Long id)
    {
        MeetingRoomInfo meetingRoomInfo = SmartRoomCache.getInstance().getMeetingRoomInfoForWeb(id, new Date());
        return RestResponse.success(meetingRoomInfo);
    }

    /**
     * 查询房间下的设备列表
     */
    @GetMapping("/getBusiSmartRoomBoundDeviceInfo/{roomId}")
    @Operation(summary = "查询房间下的设备列表")
    public RestResponse getBusiSmartRoomBoundDeviceInfo(@PathVariable Long roomId)
    {
        List<BusiSmartRoomDevice> busiSmartRoomDeviceList = busiSmartRoomService.selectBusiSmartRoomBoundDevice(roomId);
        return RestResponse.success(busiSmartRoomDeviceList);
    }

    /**
     * 绑定设备到房间
     */
    @PreAuthorize("@ss.hasPermi('busi:room:bindDevice')")
    @Log(title = "智慧办公房间", businessType = BusinessType.INSERT)
    @PostMapping("/bindDevice")
    @Operation(summary = "绑定设备到房间")
    public RestResponse bindDevice(@RequestBody List<BusiSmartRoomDeviceMap> busiSmartRoomDeviceMapList)
    {
        return toAjax(busiSmartRoomService.bindDevice(busiSmartRoomDeviceMapList));
    }

    /**
     * 房间解除设备绑定
     */
    @PreAuthorize("@ss.hasPermi('busi:room:unBindDevice')")
    @Log(title = "智慧办公房间", businessType = BusinessType.INSERT)
    @PostMapping("/unBindDevice")
    @Operation(summary = "房间解除设备绑定")
    public RestResponse unBindDevice(@RequestBody BusiSmartRoomDeviceMap busiSmartRoomDeviceMap)
    {
        return toAjax(busiSmartRoomService.unBindDevice(busiSmartRoomDeviceMap));
    }

    private Long getDeptId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Long.valueOf(100);
        }
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        Long deptId = principal.getUser().getDeptId();
        return deptId;
    }
}

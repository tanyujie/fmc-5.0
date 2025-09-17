package com.paradisecloud.fcm.web.controller.smartRoom;

import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.DeptConstant;
import com.paradisecloud.fcm.common.enumer.DeviceType;
import com.paradisecloud.fcm.common.enumer.RoomLevel;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomVo;
import com.paradisecloud.fcm.dao.model.vo.PageVo;
import com.paradisecloud.fcm.dao.model.vo.TerminalSearchVo;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomCache;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomLotCache;
import com.paradisecloud.fcm.smartroom.model.MeetingRoomInfo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceService;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IAttendeeTencentService;
import com.paradisecloud.fcm.web.service.interfaces.IBusiTerminalWebService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/busi/room")
@Tag(name = "智慧办公房间")
public class BusiSmartRoomController extends BaseController
{
    @Resource
    private IBusiSmartRoomService busiSmartRoomService;
    @Resource
    private IBusiSmartRoomDeviceService busiSmartRoomDeviceService;
    @Resource
    private IBusiTerminalWebService busiTerminalWebService;
    @Resource
    private IAttendeeTencentService attendeeTencentService;

    /**
     * 查询智慧办公房间列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询智慧办公房间列表")
    public RestResponse list(BusiSmartRoomVo busiSmartRoomVo)
    {
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
    @GetMapping("/getSmartRoomBookList/{deptId}")
    @Operation(summary = "查询智慧办公房间预约列表")
    public RestResponse getSmartRoomBookListByDeptId(@PathVariable Long deptId)
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
    public RestResponse getDeptSmartRoomCount(@PathVariable Long deptId) {
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

    /**
     * 根据设备类型和部门查询设备列表
     *
     * @return
     */
    @GetMapping("/getDeviceList/{deviceTypeCode}/{deptId}")
    @Operation(summary = "根据设备类型和部门查询设备列表")
    public RestResponse getDeviceTypeList(@PathVariable Long deviceTypeCode, @PathVariable Long deptId, PageVo pageVo) {

        DeviceType convert = DeviceType.convert(Math.toIntExact(deviceTypeCode));
        Integer code = convert.getCode();
        if (code == DeviceType.LOT_DEVICE.getCode()) {
            List<BusiSmartRoomDevice> busiSmartRoomDeviceList = busiSmartRoomDeviceService.selectBusiSmartRoomDeviceListForUnbindLotDevice();

            for (BusiSmartRoomDevice busiSmartRoomDeviceTemp : busiSmartRoomDeviceList) {
                String deviceTypeName = "";
                DeviceType deviceType = DeviceType.convert(busiSmartRoomDeviceTemp.getDeviceType());
                if (deviceType != null) {
                    deviceTypeName = deviceType.getName();
                }
                busiSmartRoomDeviceTemp.getParams().put("deviceTypeName", deviceTypeName);
                if (busiSmartRoomDeviceTemp.getLotId() != null) {
                    String lotName = "";
                    BusiSmartRoomLot busiSmartRoomLot = SmartRoomLotCache.getInstance().get(busiSmartRoomDeviceTemp.getLotId());
                    if (busiSmartRoomLot != null) {
                        lotName = busiSmartRoomLot.getLotName();
                    }
                    busiSmartRoomDeviceTemp.getParams().put("lotName", lotName);
                }
                if (busiSmartRoomDeviceTemp.getDeviceClassify() != null) {
                    String deviceClassifyName = "";
                    busiSmartRoomDeviceTemp.getParams().put("deviceClassifyName", deviceClassifyName);
                }
            }
            Integer pageNum = pageVo.getPageNum();
            if (pageNum == null) {
                pageNum = 1;
            }
            Integer pageSize = pageVo.getPageSize();
            if (pageSize == null) {
                pageSize = 10;
            }

            //从第几条数据开始
            int firstIndex = (pageNum - 1) * pageSize;

            //到第几条数据结束
            int lastIndex = pageNum * pageSize;

            long total = new PageInfo<>(busiSmartRoomDeviceList).getTotal();
            PaginationData<Object> pd = new PaginationData<>();
            pd.setTotal(total);
            if(lastIndex > total) {
                lastIndex = (int) total;
            }
            List<BusiSmartRoomDevice> subList = busiSmartRoomDeviceList.subList(firstIndex, lastIndex);
            for (Object object : subList)
            {
                pd.addRecord(object);
            }

            return RestResponse.success(pd);
        } else
        if (code == DeviceType.TERMINAL.getCode()) {
            TerminalSearchVo busiTerminal = new TerminalSearchVo();
            busiTerminal.setDeptId(deptId);
            busiTerminal.setPageNum(pageVo.getPageNum());
            busiTerminal.setBusinessFieldType(100);
            busiTerminal.setPageSize(pageVo.getPageSize());
            PaginationData<ModelBean> modelBeanPaginationData = busiTerminalWebService.selectBusiTerminalList(busiTerminal);
            return RestResponse.success(modelBeanPaginationData);
        } else
        if (code == DeviceType.TENCENT_ROOMS.getCode()) {
            Object rooms = attendeeTencentService.rooms(deptId, pageVo.getPageNum(), pageVo.getPageSize(), null);
            return RestResponse.success(rooms);
        }
        if (code == DeviceType.OTHER_DEVICE.getCode()) {
            BusiSmartRoomDevice busiSmartRoomDevice = new BusiSmartRoomDevice();
            busiSmartRoomDevice.setDeviceType(DeviceType.OTHER_DEVICE.getCode());
            List<BusiSmartRoomDevice> busiSmartRoomDeviceList = busiSmartRoomDeviceService.selectBusiSmartRoomDeviceListForUnbind(busiSmartRoomDevice);

            for (BusiSmartRoomDevice busiSmartRoomDeviceTemp : busiSmartRoomDeviceList) {
                String deviceTypeName = "";
                DeviceType deviceType = DeviceType.convert(busiSmartRoomDeviceTemp.getDeviceType());
                if (deviceType != null) {
                    deviceTypeName = deviceType.getName();
                }
                busiSmartRoomDeviceTemp.getParams().put("deviceTypeName", deviceTypeName);
                if (busiSmartRoomDeviceTemp.getLotId() != null) {
                    String lotName = "";
                    BusiSmartRoomLot busiSmartRoomLot = SmartRoomLotCache.getInstance().get(busiSmartRoomDeviceTemp.getLotId());
                    if (busiSmartRoomLot != null) {
                        lotName = busiSmartRoomLot.getLotName();
                    }
                    busiSmartRoomDeviceTemp.getParams().put("lotName", lotName);
                }
                if (busiSmartRoomDeviceTemp.getDeviceClassify() != null) {
                    String deviceClassifyName = "";
                    busiSmartRoomDeviceTemp.getParams().put("deviceClassifyName", deviceClassifyName);
                }
            }
            Integer pageNum = pageVo.getPageNum();
            if (pageNum == null) {
                pageNum = 1;
            }
            Integer pageSize = pageVo.getPageSize();
            if (pageSize == null) {
                pageSize = 10;
            }

            //从第几条数据开始
            int firstIndex = (pageNum - 1) * pageSize;

            //到第几条数据结束
            int lastIndex = pageNum * pageSize;

            long total = new PageInfo<>(busiSmartRoomDeviceList).getTotal();
            PaginationData<Object> pd = new PaginationData<>();
            pd.setTotal(total);
            if(lastIndex > total) {
                lastIndex = (int) total;
            }
            List<BusiSmartRoomDevice> subList = busiSmartRoomDeviceList.subList(firstIndex, lastIndex);
            for (Object object : subList)
            {
                pd.addRecord(object);
            }

            return RestResponse.success(pd);
        }
        return RestResponse.success();
    }

    /**
     * 查询智慧办公房间等级
     */
    @GetMapping("/getRoomLevel")
    @Operation(summary = "查询智慧办公房间等级")
    public RestResponse getRoomLevel()
    {
        List<Map<String, Object>> roomLevelList = RoomLevel.getRoomLevelList();
        return RestResponse.success(roomLevelList);
    }

    /**
     * 查询的会议室
     */
    @GetMapping("/getSmartRoomList")
    @Operation(summary = "查询的会议室")
    public RestResponse getBoundDoorplateSmartRoomList() {
        return RestResponse.success(SmartRoomCache.getInstance().values());
    }

}

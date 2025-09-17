package com.paradisecloud.fcm.web.controller.smartRoom;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.common.enumer.DeviceType;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDevice;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomLot;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDeviceVo;
import com.paradisecloud.fcm.dao.model.vo.PageVo;
import com.paradisecloud.fcm.smartroom.cache.SmartRoomLotCache;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
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

/**
 * 房间设备Controller
 *
 * @author lilinhai
 * @date 2024-03-06
 */
@RestController
@RequestMapping("/busi/device")
@Tag(name = "房间设备")
public class BusiSmartRoomDeviceController extends BaseController
{
    @Resource
    private IBusiSmartRoomDeviceService busiSmartRoomDeviceService;

    /**
     * 查询设备类型列表
     *
     * @return
     */
    @GetMapping("/getDeviceTypeList")
    @Operation(summary = "查询设备类型列表")
    public RestResponse getDeviceTypeList() {
        List<Map<String, Object>> deviceTypeList = DeviceType.getDeviceTypeList();
        return RestResponse.success(deviceTypeList);
    }

    /**
     * 查询设备类型列表（部分）
     *
     * @return
     */
    @GetMapping("/getDeviceTypeListParty")
    @Operation(summary = "查询设备类型列表（部分）")
    public RestResponse getDeviceTypeListParty() {
        List<Map<String, Object>> deviceTypeList = DeviceType.getDeviceTypeListParty();
        return RestResponse.success(deviceTypeList);
    }

    /**
     * 查询设备类型列表（绑定）
     *
     * @return
     */
    @GetMapping("/getDeviceTypeListBind")
    @Operation(summary = "查询设备类型列表（部分）")
    public RestResponse getDeviceTypeListBind() {
        List<Map<String, Object>> deviceTypeList = DeviceType.getDeviceTypeListBind();
        return RestResponse.success(deviceTypeList);
    }

    /**
     * 查询设备列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询设备列表")
    public RestResponse list(BusiSmartRoomDeviceVo busiSmartRoomDevice)
    {
        startPage();
        List<BusiSmartRoomDevice> list = busiSmartRoomDeviceService.selectBusiSmartRoomDeviceList(busiSmartRoomDevice);
        for (BusiSmartRoomDevice busiSmartRoomDeviceTemp : list) {
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
        return getDataTable(list);
    }

    /**
     * 获取房间设备详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取房间设备详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        BusiSmartRoomDevice busiSmartRoomDevice = busiSmartRoomDeviceService.selectBusiSmartRoomDeviceById(id);
        String deviceTypeName = "";
        DeviceType deviceType = DeviceType.convert(busiSmartRoomDevice.getDeviceType());
        if (deviceType != null) {
            deviceTypeName = deviceType.getName();
        }
        busiSmartRoomDevice.getParams().put("deviceTypeName", deviceTypeName);
        if (busiSmartRoomDevice.getLotId() != null) {
            String lotName = "";
            BusiSmartRoomLot busiSmartRoomLot = SmartRoomLotCache.getInstance().get(busiSmartRoomDevice.getLotId());
            if (busiSmartRoomLot != null) {
                lotName = busiSmartRoomLot.getLotName();
            }
            busiSmartRoomDevice.getParams().put("lotName", lotName);
        }
        if (busiSmartRoomDevice.getDeviceClassify() != null) {
            String deviceClassifyName = "";
            busiSmartRoomDevice.getParams().put("deviceClassifyName", deviceClassifyName);
        }
        return RestResponse.success(busiSmartRoomDevice);
    }

    /**
     * 新增房间设备
     */
    @PreAuthorize("@ss.hasPermi('busi:device:add')")
    @Log(title = "房间设备", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增房间设备")
    public RestResponse add(@RequestBody BusiSmartRoomDevice busiSmartRoomDevice)
    {
        return toAjax(busiSmartRoomDeviceService.insertBusiSmartRoomDevice(busiSmartRoomDevice));
    }

    /**
     * 修改房间设备
     */
    @PreAuthorize("@ss.hasPermi('busi:device:edit')")
    @Log(title = "房间设备", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改房间设备")
    public RestResponse edit(@RequestBody BusiSmartRoomDevice busiSmartRoomDevice)
    {
        return toAjax(busiSmartRoomDeviceService.updateBusiSmartRoomDevice(busiSmartRoomDevice));
    }

    /**
     * 删除房间设备
     */
    @PreAuthorize("@ss.hasPermi('busi:device:remove')")
    @Log(title = "房间设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除房间设备")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiSmartRoomDeviceService.deleteBusiSmartRoomDeviceByIds(ids));
    }

    /**
     * 发送开机命令
     */
    @PreAuthorize("@ss.hasPermi('busi:device:cmd:power')")
    @Log(title = "发送开机命令")
    @PostMapping("/powerOn")
    @Operation(summary = "发送开机命令")
    public RestResponse powerOn(@RequestBody JSONObject jsonObject)
    {
        Long id = jsonObject.getLong("id");
        Integer channel = jsonObject.getInteger("channel");
        Assert.notNull(id, "设备id不能为空！");
        Assert.notNull(channel, "通道号不能为空");
        boolean success = busiSmartRoomDeviceService.powerOnChannel(id, channel);
        if (success) {
            return RestResponse.success("开机命令已经下发！");
        }
        return RestResponse.fail("开机命令下发失败！");
    }

    /**
     * 发送开机命令
     */
    @PreAuthorize("@ss.hasPermi('busi:device:cmd:power')")
    @Log(title = "发送关机命令")
    @PostMapping("/powerOff")
    @Operation(summary = "发送关机命令")
    public RestResponse powerOff(@RequestBody JSONObject jsonObject)
    {
        Long id = jsonObject.getLong("id");
        Integer channel = jsonObject.getInteger("channel");
        Assert.notNull(id, "设备id不能为空！");
        Assert.notNull(channel, "通道号不能为空");
        boolean success = busiSmartRoomDeviceService.powerOffChannel(id, channel);
        if (success) {
            return RestResponse.success("关机命令已经下发！");
        }
        return RestResponse.fail("关机命令下发失败！");
    }

    /**
     * 查询未绑定的设备列表
     *
     * @return
     */
    @GetMapping("/getBusiSmartRoomDeviceListForUnbind")
    @Operation(summary = "查询未绑定的设备列表")
    public RestResponse getBusiSmartRoomDeviceListForUnbind(PageVo pageVo) {
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
        return RestResponse.success(0, "查询成功", pd);
    }
}

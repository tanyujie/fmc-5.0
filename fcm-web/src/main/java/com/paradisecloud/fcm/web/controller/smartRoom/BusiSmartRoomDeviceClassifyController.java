package com.paradisecloud.fcm.web.controller.smartRoom;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomDeviceClassify;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDeviceClassifyVo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDeviceClassifyService;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * 会议室设备分类Controller
 *
 * @author lilinhai
 * @date 2024-02-27
 */
@RestController
@RequestMapping("/busi/deviceClassify")
@Tag(name = "会议室设备分类")
public class BusiSmartRoomDeviceClassifyController extends BaseController
{
    @Resource
    private IBusiSmartRoomDeviceClassifyService busiSmartRoomDeviceClassifyService;

    /**
     * 查询会议室设备分类列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询会议室设备分类列表")
    public RestResponse list(BusiSmartRoomDeviceClassifyVo busiSmartRoomDeviceClassify)
    {
        startPage();
        List<BusiSmartRoomDeviceClassify> list = busiSmartRoomDeviceClassifyService.selectBusiSmartRoomDeviceClassifyList(busiSmartRoomDeviceClassify);
        return getDataTable(list);
    }

    /**
     * 获取会议室设备分类详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取会议室设备分类详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiSmartRoomDeviceClassifyService.selectBusiSmartRoomDeviceClassifyById(id));
    }

    /**
     * 新增会议室设备分类
     */
    @PreAuthorize("@ss.hasPermi('busi:classify:add')")
    @Log(title = "会议室设备分类", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增会议室设备分类")
    public RestResponse add(@RequestBody BusiSmartRoomDeviceClassify busiSmartRoomDeviceClassify)
    {
        return toAjax(busiSmartRoomDeviceClassifyService.insertBusiSmartRoomDeviceClassify(busiSmartRoomDeviceClassify));
    }

    /**
     * 修改会议室设备分类
     */
    @PreAuthorize("@ss.hasPermi('busi:classify:edit')")
    @Log(title = "会议室设备分类", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改会议室设备分类")
    public RestResponse edit(@RequestBody BusiSmartRoomDeviceClassify busiSmartRoomDeviceClassify)
    {
        return toAjax(busiSmartRoomDeviceClassifyService.updateBusiSmartRoomDeviceClassify(busiSmartRoomDeviceClassify));
    }

    /**
     * 删除会议室设备分类
     */
    @PreAuthorize("@ss.hasPermi('busi:classify:remove')")
    @Log(title = "会议室设备分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除会议室设备分类")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiSmartRoomDeviceClassifyService.deleteBusiSmartRoomDeviceClassifyByIds(ids));
    }
}

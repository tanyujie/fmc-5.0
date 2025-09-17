package com.paradisecloud.fcm.web.controller.business;

import com.paradisecloud.fcm.dao.model.BusiFcmNumberSection;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberSectionService;
import com.paradisecloud.fcm.service.interfaces.IBusiFcmNumberSectionService;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * 会议号段Controller
 *
 * @author LiuXiLong
 * @date 2022-02-25
 */
@RestController
@RequestMapping("/busi/FcmCnsection")
@Tag(name = "会议号段")
public class BusiFcmNumberSectionController extends BaseController
{
    @Autowired
    private IBusiFcmNumberSectionService busiFcmNumberSectionService;

    @Autowired
    private IBusiConferenceNumberSectionService busiConferenceNumberSectionService;


    // 获取部门条目计数
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiFcmNumberSectionService.getDeptRecordCounts());
    }

    /**
     * 查询终端号段列表
     */
    //@PreAuthorize("@ss.hasPermi('system:section:list')")
    @GetMapping("/DeptId")
    @Operation(summary = "查询会议号段列表")
    public RestResponse list(BusiFcmNumberSection busiFcmNumberSection)
    {
        return RestResponse.success(busiFcmNumberSectionService.selectBusiFcmNumberSection(busiFcmNumberSection));
    }

    /**
     * 缓存中查询终端号段列表
     */
    //@PreAuthorize("@ss.hasPermi('system:section:list')")
    @GetMapping("/list")
    @Operation(summary = "查询会议号段列表")
    public RestResponse getByDeptId(BusiFcmNumberSection busiFcmNumberSection)
    {
        return RestResponse.success(busiFcmNumberSectionService.selectBusiFcmNumberSectionList(busiFcmNumberSection));
    }



    /**
     * 获取终端号段详细信息
     */
    //@PreAuthorize("@ss.hasPermi('system:section:query')")
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取会议号段详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiFcmNumberSectionService.selectBusiFcmNumberSectionById(id));
    }

    /**
     * 新增终端号段
     */
    //@PreAuthorize("@ss.hasPermi('system:section:add')")
    @Log(title = "会议号段", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增会议号段")
    public RestResponse add(@RequestBody BusiFcmNumberSection busiFcmNumberSection)
    {
        return toAjax(busiFcmNumberSectionService.insertBusiFcmNumberSection(busiFcmNumberSection));
    }

    /**
     * 修改终端号段
     */
    @PutMapping(value = "/{id}")
    @Operation(summary = "修改会议号段")
    public RestResponse edit(@PathVariable("id") Long id, @RequestBody BusiFcmNumberSection busiFcmNumberSection)
    {
        busiFcmNumberSection.setId(id);
        Assert.notNull(busiFcmNumberSection.getDeptId(), "部门不能为空");
        return toAjax(busiFcmNumberSectionService.updateBusiFcmNumberSection(busiFcmNumberSection));
    }

    /**
     * 删除终端号段
     */
    //@PreAuthorize("@ss.hasPermi('system:section:remove')")
    @Log(title = "会议号段", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除会议号段")
    public RestResponse remove(@PathVariable Long ids)
    {
        return toAjax(busiFcmNumberSectionService.deleteBusiFcmNumberSectionById(ids));
    }

}

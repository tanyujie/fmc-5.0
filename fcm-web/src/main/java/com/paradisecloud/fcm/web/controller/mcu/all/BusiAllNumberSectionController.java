package com.paradisecloud.fcm.web.controller.mcu.all;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumberSection;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberSectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Controller
 *
 * @author lilinhai
 * @date 2023-03-27
 */
@RestController
@RequestMapping("/busi/mcu/all/section")
@Tag(name = "【请填写功能名称】")
public class BusiAllNumberSectionController extends BaseController
{
    @Resource
    private IBusiConferenceNumberSectionService busiConferenceNumberSectionService;

    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiConferenceNumberSectionService.getDeptRecordCounts());
    }

    /**
     * 查询会议号段列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询会议号段列表")
    public RestResponse list(BusiConferenceNumberSection busiConferenceNumberSection)
    {
        return RestResponse.success(busiConferenceNumberSectionService.selectBusiConferenceNumberSectionList(busiConferenceNumberSection));
    }

    /**
     * 获取会议号段详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取会议号段详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiConferenceNumberSectionService.selectBusiConferenceNumberSectionById(id));
    }

    /**
     * 获取会议号段详细信息
     */
    @GetMapping(value = "/getConferenceNumberSections")
    @Operation(summary = "根据部门ID获取会议号段详细信息")
    public RestResponse getCnSections(Long deptId, String mcuType, @RequestParam(required = false) Integer sectionType)
    {
        return RestResponse.success(busiConferenceNumberSectionService.getConferenceNumberSections(deptId, mcuType, sectionType));
    }

    /**
     * 新增会议号段
     */
    @PostMapping
    @Operation(summary = "新增会议号段", description = "新增会议号段")
    public RestResponse add(@RequestBody BusiConferenceNumberSection busiConferenceNumberSection)
    {
        Assert.notNull(busiConferenceNumberSection.getDeptId(), "部门不能为空");
        return toAjax(busiConferenceNumberSectionService.insertBusiConferenceNumberSection(busiConferenceNumberSection));
    }

    /**
     * 修改会议号段
     */
    @PutMapping(value = "/{id}")
    @Operation(summary = "修改会议号段", description = "修改会议号段")
    public RestResponse edit(@PathVariable("id") Long id, @RequestBody BusiConferenceNumberSection busiConferenceNumberSection)
    {
        busiConferenceNumberSection.setId(id);
        Assert.notNull(busiConferenceNumberSection.getDeptId(), "部门不能为空");
        return toAjax(busiConferenceNumberSectionService.updateBusiConferenceNumberSection(busiConferenceNumberSection));
    }

    /**
     * 删除会议号段
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除会议号段", description = "删除会议号段")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiConferenceNumberSectionService.deleteBusiConferenceNumberSectionByIds(ids));
    }
}

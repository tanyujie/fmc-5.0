package com.paradisecloud.fcm.web.controller.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumberSection;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 会议号段Controller
 * 
 * @author lilinhai
 * @date 2021-05-19
 */
//@RestController
@RequestMapping("/busi/cnsection")
@Tag(name = "会议号段")
public class BusiConferenceNumberSectionController extends BaseController
{
//    @Autowired
//    private IBusiConferenceNumberSectionService busiConferenceNumberSectionService;
//
//    /**
//     * 获取部门条目计数
//     */
//    @GetMapping(value = "/getDeptRecordCounts")
//    @Operation(summary = "获取部门条目计数")
//    public RestResponse getDeptRecordCounts()
//    {
//        return RestResponse.success(busiConferenceNumberSectionService.getDeptRecordCounts());
//    }
//
//    /**
//     * 查询会议号段列表
//     */
//    @GetMapping("/list")
//    @Operation(summary = "查询会议号段列表")
//    public RestResponse list(BusiConferenceNumberSection busiConferenceNumberSection)
//    {
//        return RestResponse.success(busiConferenceNumberSectionService.selectBusiConferenceNumberSectionList(busiConferenceNumberSection));
//    }
//
//    /**
//     * 获取会议号段详细信息
//     */
//    @GetMapping(value = "/{id}")
//    @Operation(summary = "获取会议号段详细信息")
//    public RestResponse getInfo(@PathVariable("id") Long id)
//    {
//        return RestResponse.success(busiConferenceNumberSectionService.selectBusiConferenceNumberSectionById(id));
//    }
//
//    /**
//     * 获取会议号段详细信息
//     */
//    @GetMapping(value = "/getConferenceNumberSections/{id}")
//    @Operation(summary = "根据部门ID获取会议号段详细信息")
//    public RestResponse getCnSections(@PathVariable("id") Long deptId)
//    {
//        return RestResponse.success(busiConferenceNumberSectionService.getConferenceNumberSections(deptId));
//    }
//
//    /**
//     * 新增会议号段
//     */
//    @PostMapping
//    @Operation(summary = "新增会议号段")
//    public RestResponse add(@RequestBody BusiConferenceNumberSection busiConferenceNumberSection)
//    {
//        Assert.notNull(busiConferenceNumberSection.getDeptId(), "部门不能为空");
//        return toAjax(busiConferenceNumberSectionService.insertBusiConferenceNumberSection(busiConferenceNumberSection));
//    }
//
//    /**
//     * 修改会议号段
//     */
//    @PutMapping(value = "/{id}")
//    @Operation(summary = "修改会议号段")
//    public RestResponse edit(@PathVariable("id") Long id, @RequestBody BusiConferenceNumberSection busiConferenceNumberSection)
//    {
//        busiConferenceNumberSection.setId(id);
//        Assert.notNull(busiConferenceNumberSection.getDeptId(), "部门不能为空");
//        return toAjax(busiConferenceNumberSectionService.updateBusiConferenceNumberSection(busiConferenceNumberSection));
//    }
//
//    /**
//     * 删除会议号段
//     */
//	@DeleteMapping("/{ids}")
//	@Operation(summary = "删除会议号段")
//    public RestResponse remove(@PathVariable Long[] ids)
//    {
//        return toAjax(busiConferenceNumberSectionService.deleteBusiConferenceNumberSectionByIds(ids));
//    }
}

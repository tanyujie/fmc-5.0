package com.paradisecloud.fcm.web.controller.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.dao.model.BusiTemplateConferenceDefaultViewDept;
import com.paradisecloud.fcm.service.interfaces.IBusiTemplateConferenceDefaultViewDeptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 默认视图的部门显示顺序Controller
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
@RestController
@RequestMapping("/busi/defaultViewDept")
@Tag(name = "默认视图的部门显示顺序")
public class BusiTemplateConferenceDefaultViewDeptController extends BaseController
{
    @Autowired
    private IBusiTemplateConferenceDefaultViewDeptService busiTemplateConferenceDefaultViewDeptService;

    /**
     * 查询默认视图的部门显示顺序列表
     */
    @PreAuthorize("@ss.hasPermi('busi:defaultViewDept:list')")
    @GetMapping("/list")
    @Operation(summary = "查询默认视图的部门显示顺序列表")
    public RestResponse list(BusiTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept)
    {
        startPage();
        List<BusiTemplateConferenceDefaultViewDept> list = busiTemplateConferenceDefaultViewDeptService.selectBusiTemplateConferenceDefaultViewDeptList(busiTemplateConferenceDefaultViewDept);
        return getDataTable(list);
    }

    /**
     * 获取默认视图的部门显示顺序详细信息
     */
    @PreAuthorize("@ss.hasPermi('busi:defaultViewDept:query')")
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取默认视图的部门显示顺序详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiTemplateConferenceDefaultViewDeptService.selectBusiTemplateConferenceDefaultViewDeptById(id));
    }

    /**
     * 新增默认视图的部门显示顺序
     */
    @PreAuthorize("@ss.hasPermi('busi:defaultViewDept:add')")
    @Log(title = "默认视图的部门显示顺序", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增默认视图的部门显示顺序")
    public RestResponse add(@RequestBody BusiTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept)
    {
        return toAjax(busiTemplateConferenceDefaultViewDeptService.insertBusiTemplateConferenceDefaultViewDept(busiTemplateConferenceDefaultViewDept));
    }

    /**
     * 修改默认视图的部门显示顺序
     */
    @PreAuthorize("@ss.hasPermi('busi:defaultViewDept:edit')")
    @Log(title = "默认视图的部门显示顺序", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改默认视图的部门显示顺序")
    public RestResponse edit(@RequestBody BusiTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept)
    {
        return toAjax(busiTemplateConferenceDefaultViewDeptService.updateBusiTemplateConferenceDefaultViewDept(busiTemplateConferenceDefaultViewDept));
    }

    /**
     * 删除默认视图的部门显示顺序
     */
    @PreAuthorize("@ss.hasPermi('busi:defaultViewDept:remove')")
    @Log(title = "默认视图的部门显示顺序", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除默认视图的部门显示顺序")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiTemplateConferenceDefaultViewDeptService.deleteBusiTemplateConferenceDefaultViewDeptByIds(ids));
    }
}

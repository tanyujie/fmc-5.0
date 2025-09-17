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
import com.paradisecloud.fcm.dao.model.BusiTemplateConferenceDefaultViewPaticipant;
import com.paradisecloud.fcm.service.interfaces.IBusiTemplateConferenceDefaultViewPaticipantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 默认视图的参会者Controller
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
@RestController
@RequestMapping("/busi/DefaultViewPaticipant")
@Tag(name = "默认视图的参会者")
public class BusiTemplateConferenceDefaultViewPaticipantController extends BaseController
{
    @Autowired
    private IBusiTemplateConferenceDefaultViewPaticipantService busiTemplateConferenceDefaultViewPaticipantService;

    /**
     * 查询默认视图的参会者列表
     */
    @PreAuthorize("@ss.hasPermi('busi:DefaultViewPaticipant:list')")
    @GetMapping("/list")
    @Operation(summary = "查询默认视图的参会者列表")
    public RestResponse list(BusiTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant)
    {
        startPage();
        List<BusiTemplateConferenceDefaultViewPaticipant> list = busiTemplateConferenceDefaultViewPaticipantService.selectBusiTemplateConferenceDefaultViewPaticipantList(busiTemplateConferenceDefaultViewPaticipant);
        return getDataTable(list);
    }

    /**
     * 获取默认视图的参会者详细信息
     */
    @PreAuthorize("@ss.hasPermi('busi:DefaultViewPaticipant:query')")
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取默认视图的参会者详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiTemplateConferenceDefaultViewPaticipantService.selectBusiTemplateConferenceDefaultViewPaticipantById(id));
    }

    /**
     * 新增默认视图的参会者
     */
    @PreAuthorize("@ss.hasPermi('busi:DefaultViewPaticipant:add')")
    @Log(title = "默认视图的参会者", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增默认视图的参会者")
    public RestResponse add(@RequestBody BusiTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant)
    {
        return toAjax(busiTemplateConferenceDefaultViewPaticipantService.insertBusiTemplateConferenceDefaultViewPaticipant(busiTemplateConferenceDefaultViewPaticipant));
    }

    /**
     * 修改默认视图的参会者
     */
    @PreAuthorize("@ss.hasPermi('busi:DefaultViewPaticipant:edit')")
    @Log(title = "默认视图的参会者", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改默认视图的参会者")
    public RestResponse edit(@RequestBody BusiTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant)
    {
        return toAjax(busiTemplateConferenceDefaultViewPaticipantService.updateBusiTemplateConferenceDefaultViewPaticipant(busiTemplateConferenceDefaultViewPaticipant));
    }

    /**
     * 删除默认视图的参会者
     */
    @PreAuthorize("@ss.hasPermi('busi:DefaultViewPaticipant:remove')")
    @Log(title = "默认视图的参会者", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除默认视图的参会者")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiTemplateConferenceDefaultViewPaticipantService.deleteBusiTemplateConferenceDefaultViewPaticipantByIds(ids));
    }
}

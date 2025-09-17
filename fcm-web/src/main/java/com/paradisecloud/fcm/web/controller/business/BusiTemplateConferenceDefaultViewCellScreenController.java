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
import com.paradisecloud.fcm.dao.model.BusiTemplateConferenceDefaultViewCellScreen;
import com.paradisecloud.fcm.service.interfaces.IBusiTemplateConferenceDefaultViewCellScreenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 默认视图下指定的多分频单元格Controller
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
@RestController
@RequestMapping("/busi/defaultViewCellScreen")
@Tag(name = "默认视图下指定的多分频单元格")
public class BusiTemplateConferenceDefaultViewCellScreenController extends BaseController
{
    @Autowired
    private IBusiTemplateConferenceDefaultViewCellScreenService busiTemplateConferenceDefaultViewCellScreenService;

    /**
     * 查询默认视图下指定的多分频单元格列表
     */
    @PreAuthorize("@ss.hasPermi('busi:defaultViewCellScreen:list')")
    @GetMapping("/list")
    @Operation(summary = "查询默认视图下指定的多分频单元格列表")
    public RestResponse list(BusiTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen)
    {
        startPage();
        List<BusiTemplateConferenceDefaultViewCellScreen> list = busiTemplateConferenceDefaultViewCellScreenService.selectBusiTemplateConferenceDefaultViewCellScreenList(busiTemplateConferenceDefaultViewCellScreen);
        return getDataTable(list);
    }

    /**
     * 获取默认视图下指定的多分频单元格详细信息
     */
    @PreAuthorize("@ss.hasPermi('busi:defaultViewCellScreen:query')")
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取默认视图下指定的多分频单元格详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiTemplateConferenceDefaultViewCellScreenService.selectBusiTemplateConferenceDefaultViewCellScreenById(id));
    }

    /**
     * 新增默认视图下指定的多分频单元格
     */
    @PreAuthorize("@ss.hasPermi('busi:defaultViewCellScreen:add')")
    @Log(title = "默认视图下指定的多分频单元格", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增默认视图下指定的多分频单元格")
    public RestResponse add(@RequestBody BusiTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen)
    {
        return toAjax(busiTemplateConferenceDefaultViewCellScreenService.insertBusiTemplateConferenceDefaultViewCellScreen(busiTemplateConferenceDefaultViewCellScreen));
    }

    /**
     * 修改默认视图下指定的多分频单元格
     */
    @PreAuthorize("@ss.hasPermi('busi:defaultViewCellScreen:edit')")
    @Log(title = "默认视图下指定的多分频单元格", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改默认视图下指定的多分频单元格")
    public RestResponse edit(@RequestBody BusiTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen)
    {
        return toAjax(busiTemplateConferenceDefaultViewCellScreenService.updateBusiTemplateConferenceDefaultViewCellScreen(busiTemplateConferenceDefaultViewCellScreen));
    }

    /**
     * 删除默认视图下指定的多分频单元格
     */
    @PreAuthorize("@ss.hasPermi('busi:defaultViewCellScreen:remove')")
    @Log(title = "默认视图下指定的多分频单元格", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除默认视图下指定的多分频单元格")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiTemplateConferenceDefaultViewCellScreenService.deleteBusiTemplateConferenceDefaultViewCellScreenByIds(ids));
    }
}

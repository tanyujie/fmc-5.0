package com.paradisecloud.fcm.web.controller.fme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiTenantSettings;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiTenantSettingsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 租户设置Controller
 * 
 * @author lilinhai
 * @date 2021-08-04
 */
@RestController
@RequestMapping("/busi/tenantSettings")
@Tag(name = "租户设置")
public class BusiTenantSettingsController extends BaseController
{
    @Autowired
    private IBusiTenantSettingsService busiTenantSettingsService;

    /**
     * 查询租户设置列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询租户设置列表")
    public RestResponse list()
    {
        return RestResponse.success(busiTenantSettingsService.getAllTenants());
    }

    /**
     * 获取租户设置详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取租户设置详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiTenantSettingsService.selectBusiTenantSettingsById(id));
    }

    /**
     * 新增租户设置
     */
    @PostMapping
    @Operation(summary = "新增租户设置")
    public RestResponse add(@RequestBody BusiTenantSettings busiTenantSettings)
    {
        if (busiTenantSettingsService.insertBusiTenantSettings(busiTenantSettings) == 1)
        {
            return RestResponse.success(busiTenantSettings.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 修改租户设置
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改租户设置")
    public RestResponse edit(@RequestBody BusiTenantSettings busiTenantSettings, @PathVariable String id)
    {
        busiTenantSettings.getParams().put("id", id);
        if (busiTenantSettingsService.updateBusiTenantSettings(busiTenantSettings) == 1)
        {
            return RestResponse.success(busiTenantSettings.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 删除租户设置
     */
    @PostMapping("/delete")
	@Operation(summary = "删除租户设置")
    public RestResponse remove(@RequestBody BusiTenantSettings busiTenantSettings)
    {
        return busiTenantSettingsService.deleteBusiTenantSettingsById(busiTenantSettings);
    }
}

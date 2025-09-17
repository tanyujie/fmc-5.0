package com.paradisecloud.fcm.web.controller.fme;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiCallLegProfile;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 入会方案配置，控制参会者进入会议的方案Controller
 * 
 * @author lilinhai
 * @date 2021-01-26
 */
@RestController
@RequestMapping("/busi/callLegProfile")
@Tag(name = "入会方案配置，控制参会者进入会议的方案")
public class BusiCallLegProfileController extends BaseController
{
    @Autowired
    private IBusiCallLegProfileService busiCallLegProfileService;

    /**
     * 查询入会方案配置，控制参会者进入会议的方案列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询入会方案配置，控制参会者进入会议的方案列表")
    public RestResponse list(BusiCallLegProfile busiCallLegProfile)
    {
        List<ModelBean> list = busiCallLegProfileService.getAllCallLegProfiles(busiCallLegProfile.getDeptId());
        return RestResponse.success(list);
    }

    /**
     * 导出入会方案配置，控制参会者进入会议的方案列表
     */
    @Log(title = "入会方案配置，控制参会者进入会议的方案", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出入会方案配置，控制参会者进入会议的方案列表")
    public RestResponse export(BusiCallLegProfile busiCallLegProfile)
    {
        List<BusiCallLegProfile> list = busiCallLegProfileService.selectBusiCallLegProfileList(busiCallLegProfile);
        ExcelUtil<BusiCallLegProfile> util = new ExcelUtil<BusiCallLegProfile>(BusiCallLegProfile.class);
        return util.exportExcel(list, "callLegProfile");
    }

    /**
     * 获取入会方案配置，控制参会者进入会议的方案详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取入会方案配置，控制参会者进入会议的方案详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiCallLegProfileService.selectBusiCallLegProfileById(id));
    }

    /**
     * 新增入会方案配置，控制参会者进入会议的方案
     */
    @Log(title = "入会方案配置，控制参会者进入会议的方案", businessType = BusinessType.INSERT)
    @PostMapping("")
    @Operation(summary = "新增入会方案配置，控制参会者进入会议的方案")
    public RestResponse add(@RequestBody BusiCallLegProfile busiCallLegProfile)
    {
        if (busiCallLegProfileService.insertBusiCallLegProfile(busiCallLegProfile) == 1)
        {
            return RestResponse.success(busiCallLegProfile.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 修改入会方案配置，控制参会者进入会议的方案
     */
    @Log(title = "入会方案配置，控制参会者进入会议的方案", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "修改入会方案配置，控制参会者进入会议的方案")
    public RestResponse edit(@RequestBody BusiCallLegProfile busiCallLegProfile, @PathVariable String id)
    {
        busiCallLegProfile.getParams().put("id", id);
        if (busiCallLegProfileService.updateBusiCallLegProfile(busiCallLegProfile) == 1)
        {
            return RestResponse.success(busiCallLegProfile.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 删除入会方案配置，控制参会者进入会议的方案
     */
    @Log(title = "入会方案配置，控制参会者进入会议的方案", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
	@Operation(summary = "删除入会方案配置，控制参会者进入会议的方案")
    public RestResponse remove(@RequestBody BusiCallLegProfile busiCallLegProfile)
    {
        return busiCallLegProfileService.deleteBusiCallLegProfileById(busiCallLegProfile);
    }
    
    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiCallLegProfileService.getDeptRecordCounts());
    }
}

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

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiProfileCompatibility;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCompatibilityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 兼容性参数模板Controller
 * 
 * @author lilinhai
 * @date 2021-07-27
 */
@RestController
@RequestMapping("/fme/compatibilityProfile")
@Tag(name = "兼容性参数模板")
public class BusiProfileCompatibilityController extends BaseController
{
    @Autowired
    private IBusiProfileCompatibilityService busiProfileCompatibilityService;

    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiProfileCompatibilityService.getDeptRecordCounts());
    }
    
    /**
     * 查询兼容性参数模板列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询兼容性参数模板列表")
    public RestResponse list(BusiProfileCompatibility busiProfileCompatibility)
    {
        List<ModelBean> list = busiProfileCompatibilityService.getAllCompatibilityProfiles(busiProfileCompatibility.getDeptId());
        return RestResponse.success(list);
    }

    /**
     * 新增兼容性参数模板
     */
    @PostMapping
    @Operation(summary = "新增兼容性参数模板")
    public RestResponse add(@RequestBody BusiProfileCompatibility busiProfileCompatibility)
    {
        if (busiProfileCompatibilityService.insertBusiProfileCompatibility(busiProfileCompatibility) == 1)
        {
            return RestResponse.success(busiProfileCompatibility.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 修改兼容性参数模板
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改兼容性参数模板")
    public RestResponse edit(@RequestBody BusiProfileCompatibility busiProfileCompatibility, @PathVariable String id)
    {
        busiProfileCompatibility.getParams().put("id", id);
        if (busiProfileCompatibilityService.updateBusiProfileCompatibility(busiProfileCompatibility) == 1)
        {
            return RestResponse.success(busiProfileCompatibility.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 删除兼容性参数模板
     */
    @PostMapping("/delete")
    @Operation(summary = "删除删除兼容性参数模板")
    public RestResponse remove(@RequestBody BusiProfileCompatibility busiProfileCompatibility)
    {
        return busiProfileCompatibilityService.deleteBusiProfileCompatibilityById(busiProfileCompatibility);
    }
}

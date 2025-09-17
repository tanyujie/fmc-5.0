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
import com.paradisecloud.fcm.dao.model.BusiProfileDialInSecurity;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileDialInSecurityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 呼入安全模板Controller
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
@RestController
@RequestMapping("/fme/dialInSecurityProfile")
@Tag(name = "呼入安全模板")
public class BusiProfileDialInSecurityController extends BaseController
{
    @Autowired
    private IBusiProfileDialInSecurityService busiProfileDialInSecurityService;

    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiProfileDialInSecurityService.getDeptRecordCounts());
    }
    
    /**
     * 查询呼入安全模板列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询呼入安全模板列表")
    public RestResponse list(BusiProfileDialInSecurity busiProfileDialInSecurity)
    {
        List<ModelBean> list = busiProfileDialInSecurityService.getAllDialInSecurityProfiles(busiProfileDialInSecurity.getDeptId());
        return RestResponse.success(list);
    }

    /**
     * 新增呼入安全模板
     */
    @PostMapping
    @Operation(summary = "新增呼入安全模板")
    public RestResponse add(@RequestBody BusiProfileDialInSecurity busiProfileDialInSecurity)
    {
        if (busiProfileDialInSecurityService.insertBusiProfileDialInSecurity(busiProfileDialInSecurity) == 1)
        {
            return RestResponse.success(busiProfileDialInSecurity.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 修改呼入安全模板
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改呼入安全模板")
    public RestResponse edit(@RequestBody BusiProfileDialInSecurity busiProfileDialInSecurity, @PathVariable String id)
    {
        busiProfileDialInSecurity.getParams().put("id", id);
        if (busiProfileDialInSecurityService.updateBusiProfileDialInSecurity(busiProfileDialInSecurity) == 1)
        {
            return RestResponse.success(busiProfileDialInSecurity.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 删除呼入安全模板
     */
    @PostMapping("/delete")
    @Operation(summary = "删除呼入安全模板")
    public RestResponse remove(@RequestBody BusiProfileDialInSecurity busiProfileDialInSecurity)
    {
        return busiProfileDialInSecurityService.deleteBusiProfileDialInSecurityById(busiProfileDialInSecurity);
    }
}

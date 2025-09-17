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
import com.paradisecloud.fcm.dao.model.BusiProfileIvrBranding;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileIvrBrandingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * ivrBranding模板Controller
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
@RestController
@RequestMapping("/fme/ivrBranding")
@Tag(name = "ivrBranding模板")
public class BusiProfileIvrBrandingController extends BaseController
{
    @Autowired
    private IBusiProfileIvrBrandingService busiProfileIvrBrandingService;

    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiProfileIvrBrandingService.getDeptRecordCounts());
    }
    
    /**
     * 查询ivrBranding模板列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询ivrBranding模板列表")
    public RestResponse list(BusiProfileIvrBranding busiProfileIvrBranding)
    {
        List<ModelBean> list = busiProfileIvrBrandingService.getAllIvrBrandingProfiles(busiProfileIvrBranding.getDeptId());
        return RestResponse.success(list);
    }

    /**
     * 新增ivrBranding模板
     */
    @PostMapping
    @Operation(summary = "新增ivrBranding模板")
    public RestResponse add(@RequestBody BusiProfileIvrBranding busiProfileIvrBranding)
    {
        if (busiProfileIvrBrandingService.insertBusiProfileIvrBranding(busiProfileIvrBranding) == 1)
        {
            return RestResponse.success(busiProfileIvrBranding.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 修改ivrBranding模板
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改ivrBranding模板")
    public RestResponse edit(@RequestBody BusiProfileIvrBranding busiProfileIvrBranding, @PathVariable String id)
    {
        busiProfileIvrBranding.getParams().put("id", id);
        if (busiProfileIvrBrandingService.updateBusiProfileIvrBranding(busiProfileIvrBranding) == 1)
        {
            return RestResponse.success(busiProfileIvrBranding.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 删除ivrBranding模板
     */
    @PostMapping("/delete")
	@Operation(summary = "删除ivrBranding模板")
    public RestResponse remove(@RequestBody BusiProfileIvrBranding busiProfileIvrBranding)
    {
	    return busiProfileIvrBrandingService.deleteBusiProfileIvrBrandingById(busiProfileIvrBranding);
    }
}

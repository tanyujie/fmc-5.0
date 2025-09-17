package com.paradisecloud.fcm.web.controller.fme;

import java.io.IOException;
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
import com.paradisecloud.fcm.dao.model.BusiProfileCallBranding;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCallBrandingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * callBranding模板Controller
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
@RestController
@RequestMapping("/fme/callBrandingProfile")
@Tag(name = "callBranding模板")
public class BusiProfileCallBrandingController extends BaseController
{
    @Autowired
    private IBusiProfileCallBrandingService busiProfileCallBrandingService;

    /**
     * 查询callBranding模板列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询callBranding模板列表")
    public RestResponse list(BusiProfileCallBranding busiProfileCallBranding)
    {
        List<ModelBean> list = busiProfileCallBrandingService.getAllCallBrandingProfiles(busiProfileCallBranding.getDeptId());
        return RestResponse.success(list);
    }

    /**
     * 新增callBranding模板
     */
    @PostMapping
    @Operation(summary = "新增callBranding模板")
    public RestResponse add(@RequestBody BusiProfileCallBranding busiProfileCallBranding) throws IOException
    {
        if (busiProfileCallBrandingService.insertBusiProfileCallBranding(busiProfileCallBranding) == 1)
        {
            return RestResponse.success(busiProfileCallBranding.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 修改callBranding模板
     * @throws IOException 
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改callBranding模板")
    public RestResponse edit(@RequestBody BusiProfileCallBranding busiProfileCallBranding, @PathVariable String id) throws IOException
    {
        busiProfileCallBranding.getParams().put("id", id);
        if (busiProfileCallBrandingService.updateBusiProfileCallBranding(busiProfileCallBranding) == 1)
        {
            return RestResponse.success(busiProfileCallBranding.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }
    
    /**
     * 删除callBranding模板
     */
    @PostMapping("/delete")
    @Operation(summary = "删除callBranding模板")
    public RestResponse remove(@RequestBody BusiProfileCallBranding busiProfileCallBranding)
    {
        return busiProfileCallBrandingService.deleteBusiProfileCallBrandingById(busiProfileCallBranding);
    }
    
    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiProfileCallBrandingService.getDeptRecordCounts());
    }
}

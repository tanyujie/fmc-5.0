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
import com.paradisecloud.fcm.dao.model.BusiProfileCall;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCallService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * call模板Controller
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
@RestController
@RequestMapping("/fme/call_profile")
@Tag(name = "call模板")
public class BusiProfileCallController extends BaseController
{
    @Autowired
    private IBusiProfileCallService busiProfileCallService;

    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiProfileCallService.getDeptRecordCounts());
    }
    
    /**
     * 查询call模板列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询call模板列表")
    public RestResponse list(BusiProfileCall busiProfileCall)
    {
        List<ModelBean> list = busiProfileCallService.getAllCallProfiles(busiProfileCall.getDeptId());
        return RestResponse.success(list);
    }

    /**
     * 新增call模板
     */
    @PostMapping
    @Operation(summary = "新增call模板")
    public RestResponse add(@RequestBody BusiProfileCall busiProfileCall)
    {
        if (busiProfileCallService.insertBusiProfileCall(busiProfileCall) == 1)
        {
            return RestResponse.success(busiProfileCall.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 修改call模板
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改call模板")
    public RestResponse edit(@RequestBody BusiProfileCall busiProfileCall, @PathVariable String id)
    {
        busiProfileCall.getParams().put("id", id);
        if (busiProfileCallService.updateBusiProfileCall(busiProfileCall) == 1)
        {
            return RestResponse.success(busiProfileCall.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 删除call模板
     */
    @PostMapping("/delete")
	@Operation(summary = "删除call模板")
    public RestResponse remove(@RequestBody BusiProfileCall busiProfileCall)
    {
        return busiProfileCallService.deleteBusiProfileCallById(busiProfileCall);
    }
}

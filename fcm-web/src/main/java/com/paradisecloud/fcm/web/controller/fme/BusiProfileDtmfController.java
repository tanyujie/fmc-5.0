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
import com.paradisecloud.fcm.dao.model.BusiProfileDtmf;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileDtmfService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * DTMF模板Controller
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
@RestController
@RequestMapping("/fme/dtmfProfile")
@Tag(name = "DTMF模板")
public class BusiProfileDtmfController extends BaseController
{
    @Autowired
    private IBusiProfileDtmfService busiProfileDtmfService;

    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiProfileDtmfService.getDeptRecordCounts());
    }
    
    /**
     * 查询DTMF模板列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询DTMF模板列表")
    public RestResponse list(BusiProfileDtmf busiProfileDtmf)
    {
        List<ModelBean> list = busiProfileDtmfService.getAllDtmfProfiles(busiProfileDtmf.getDeptId());
        return RestResponse.success(list);
    }

    /**
     * 新增DTMF模板
     */
    @PostMapping
    @Operation(summary = "新增DTMF模板")
    public RestResponse add(@RequestBody BusiProfileDtmf busiProfileDtmf)
    {
        if (busiProfileDtmfService.insertBusiProfileDtmf(busiProfileDtmf) == 1)
        {
            return RestResponse.success(busiProfileDtmf.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 修改DTMF模板
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改DTMF模板")
    public RestResponse edit(@RequestBody BusiProfileDtmf busiProfileDtmf, @PathVariable String id)
    {
        busiProfileDtmf.getParams().put("id", id);
        if (busiProfileDtmfService.updateBusiProfileDtmf(busiProfileDtmf) == 1)
        {
            return RestResponse.success(busiProfileDtmf.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 删除DTMF模板
     */
    @PostMapping("/delete")
	@Operation(summary = "删除DTMF模板")
    public RestResponse remove(@RequestBody BusiProfileDtmf busiProfileDtmf)
    {
        return busiProfileDtmfService.deleteBusiProfileDtmfById(busiProfileDtmf);
    }
}

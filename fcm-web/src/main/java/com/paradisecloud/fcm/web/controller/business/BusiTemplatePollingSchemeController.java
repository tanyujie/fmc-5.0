package com.paradisecloud.fcm.web.controller.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.dao.model.BusiTemplatePollingDept;
import com.paradisecloud.fcm.dao.model.BusiTemplatePollingPaticipant;
import com.paradisecloud.fcm.dao.model.BusiTemplatePollingScheme;
import com.paradisecloud.fcm.fme.attendee.interfaces.IBusiTemplatePollingSchemeService;
import com.sinhy.exception.SystemException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 轮询方案Controller
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
//@RestController
@RequestMapping("/busi/templatePollingScheme")
@Tag(name = "轮询方案")
public class BusiTemplatePollingSchemeController extends BaseController
{
    @Autowired
    private IBusiTemplatePollingSchemeService busiTemplatePollingSchemeService;

    /**
     * 查询轮询方案列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询轮询方案列表")
    public RestResponse list(BusiTemplatePollingScheme busiTemplatePollingScheme)
    {
        return success(busiTemplatePollingSchemeService.selectBusiTemplatePollingSchemeList(busiTemplatePollingScheme));
    }

    /**
     * 获取轮询方案详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取轮询方案详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiTemplatePollingSchemeService.selectBusiTemplatePollingSchemeById(id));
    }

    /**
     * 新增轮询方案
     */
    @Log(title = "轮询方案", businessType = BusinessType.INSERT)
    @PostMapping("")
    @Operation(summary = "新增轮询方案")
    public RestResponse add(@RequestBody JSONObject jsonObj)
    {
        BusiTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiTemplatePollingScheme.class);
        if (templatePollingScheme == null)
        {
            throw new SystemException(1110098, "轮询方案不能为空！");
        }
        JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
        List<BusiTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
        for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
        {
            templatePollingPaticipants.add(busiTemplateParticipantArr.getObject(i, BusiTemplatePollingPaticipant.class));
        }
        
        // 部门顺序
        JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
        List<BusiTemplatePollingDept> templatePollingDepts = new ArrayList<>();
        for (int i = 0; i < templateDeptArr.size(); i++)
        {
            templatePollingDepts.add(templateDeptArr.getObject(i, BusiTemplatePollingDept.class));
        }
        return toAjax(busiTemplatePollingSchemeService.insertBusiTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
    }

    /**
     * 修改轮询方案
     */
    @Log(title = "轮询方案", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "修改轮询方案")
    public RestResponse edit(@RequestBody JSONObject jsonObj, @PathVariable("id") Long id)
    {
        BusiTemplatePollingScheme templatePollingScheme = jsonObj.getObject("pollingScheme", BusiTemplatePollingScheme.class);
        if (templatePollingScheme == null)
        {
            throw new SystemException(1110098, "轮询方案不能为空！");
        }
        
        templatePollingScheme.setId(id);
        
        JSONArray busiTemplateParticipantArr = jsonObj.getJSONArray("pollingParticipants");
        List<BusiTemplatePollingPaticipant> templatePollingPaticipants = new ArrayList<>();
        for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
        {
            templatePollingPaticipants.add(busiTemplateParticipantArr.getObject(i, BusiTemplatePollingPaticipant.class));
        }
        
        // 部门顺序
        JSONArray templateDeptArr = jsonObj.getJSONArray("pollingDepts");
        List<BusiTemplatePollingDept> templatePollingDepts = new ArrayList<>();
        for (int i = 0; i < templateDeptArr.size(); i++)
        {
            templatePollingDepts.add(templateDeptArr.getObject(i, BusiTemplatePollingDept.class));
        }
        return toAjax(busiTemplatePollingSchemeService.updateBusiTemplatePollingScheme(templatePollingScheme, templatePollingDepts, templatePollingPaticipants));
    }
    
    /**
     * 修改轮询方案
     */
    @Log(title = "轮询方案", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    @Operation(summary = "修改轮询方案")
    public RestResponse edit(@RequestBody List<BusiTemplatePollingScheme> templatePollingSchemes)
    {
        busiTemplatePollingSchemeService.updateBusiTemplatePollingSchemes(templatePollingSchemes);
        return RestResponse.success();
    }

    /**
     * 删除轮询方案
     */
    @Log(title = "轮询方案", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
	@Operation(summary = "删除轮询方案")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiTemplatePollingSchemeService.deleteBusiTemplatePollingSchemeById(id));
    }
}

package com.paradisecloud.fcm.web.controller.edu;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.model.BusiEduSectionScheme;
import com.paradisecloud.fcm.edu.interfaces.IBusiEduSectionSchemeService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 课程节次方案，每个季节都可能有不同的节次方案Controller
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@RestController
@RequestMapping("/busi/eduSectionScheme")
@Tag(name = "课程节次方案，每个季节都可能有不同的节次方案")
public class BusiEduSectionSchemeController extends BaseController
{
    @Autowired
    private IBusiEduSectionSchemeService busiEduSectionSchemeService;

    /**
     * 查询课程节次方案，每个季节都可能有不同的节次方案列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询课程节次方案，每个季节都可能有不同的节次方案列表")
    public RestResponse list(BusiEduSectionScheme busiEduSectionScheme)
    {
        startPage();
        List<BusiEduSectionScheme> list = busiEduSectionSchemeService.selectBusiEduSectionSchemeList(busiEduSectionScheme);
        return getDataTable(list);
    }
    
    /**
     * 查询课程节次方案，每个季节都可能有不同的节次方案列表
     */
    @GetMapping("/list/{deptId}")
    @Operation(summary = "根据部门ID查询节次方案列表")
    public RestResponse list(@PathVariable("deptId") Long deptId)
    {
        return RestResponse.success(busiEduSectionSchemeService.getEduSectionSchemeByDept(deptId));
    }
    
    /**
     * 查询课程节次方案，每个季节都可能有不同的节次方案列表
     */
    @GetMapping("/enabledScheme/{deptId}")
    @Operation(summary = "根据部门ID获取已启用的节次方案")
    public RestResponse enabledScheme(@PathVariable("deptId") Long deptId)
    {
        List<BusiEduSectionScheme> all = busiEduSectionSchemeService.getEduSectionSchemeByDept(deptId);
        BusiEduSectionScheme s = null;
        if (!ObjectUtils.isEmpty(all))
        {
            for (BusiEduSectionScheme busiEduSectionScheme : all)
            {
                if (YesOrNo.YES == YesOrNo.convert(busiEduSectionScheme.getEnableStatus()))
                {
                    s = busiEduSectionScheme;
                    break;
                }
            }
        }
        return RestResponse.success(s);
    }

    /**
     * 导出课程节次方案，每个季节都可能有不同的节次方案列表
     */
    @GetMapping("/export")
    @Operation(summary = "导出课程节次方案，每个季节都可能有不同的节次方案列表")
    public RestResponse export(BusiEduSectionScheme busiEduSectionScheme)
    {
        List<BusiEduSectionScheme> list = busiEduSectionSchemeService.selectBusiEduSectionSchemeList(busiEduSectionScheme);
        ExcelUtil<BusiEduSectionScheme> util = new ExcelUtil<BusiEduSectionScheme>(BusiEduSectionScheme.class);
        return util.exportExcel(list, "eduSectionScheme");
    }

    /**
     * 获取课程节次方案，每个季节都可能有不同的节次方案详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取课程节次方案，每个季节都可能有不同的节次方案详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiEduSectionSchemeService.selectBusiEduSectionSchemeById(id));
    }

    /**
     * 新增课程节次方案，每个季节都可能有不同的节次方案
     */
    @PostMapping
    @Operation(summary = "新增课程节次方案，每个季节都可能有不同的节次方案")
    public RestResponse add(@RequestBody BusiEduSectionScheme busiEduSectionScheme)
    {
        return toAjax(busiEduSectionSchemeService.insertBusiEduSectionScheme(busiEduSectionScheme));
    }

    /**
     * 修改课程节次方案，每个季节都可能有不同的节次方案
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改课程节次方案，每个季节都可能有不同的节次方案")
    public RestResponse edit(@RequestBody BusiEduSectionScheme busiEduSectionScheme, @PathVariable Long id)
    {
        busiEduSectionScheme.setId(id);
        return toAjax(busiEduSectionSchemeService.updateBusiEduSectionScheme(busiEduSectionScheme));
    }

    /**
     * 删除课程节次方案，每个季节都可能有不同的节次方案
     */
	@DeleteMapping("/{id}")
	@Operation(summary = "删除课程节次方案，每个季节都可能有不同的节次方案")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiEduSectionSchemeService.deleteBusiEduSectionSchemeById(id));
    }
	
    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiEduSectionSchemeService.getDeptRecordCounts());
    }
}

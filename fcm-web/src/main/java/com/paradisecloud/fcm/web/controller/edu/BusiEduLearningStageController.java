package com.paradisecloud.fcm.web.controller.edu;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.paradisecloud.fcm.dao.model.BusiEduLearningStage;
import com.paradisecloud.fcm.edu.cache.EduLearningStageCache;
import com.paradisecloud.fcm.edu.interfaces.IBusiEduLearningStageService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 学段信息，小学，初中，高中，大学等Controller
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@RestController
@RequestMapping("/busi/eduLearningStage")
@Tag(name = "学段信息，小学，初中，高中，大学等")
public class BusiEduLearningStageController extends BaseController
{
    @Autowired
    private IBusiEduLearningStageService busiEduLearningStageService;

    /**
     * 查询学段信息，小学，初中，高中，大学等列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询学段信息，小学，初中，高中，大学等列表")
    public RestResponse list(BusiEduLearningStage busiEduLearningStage)
    {
        startPage();
        List<BusiEduLearningStage> list = busiEduLearningStageService.selectBusiEduLearningStageList(busiEduLearningStage);
        return getDataTable(list);
    }
    
    /**
     * 查询学科信息列表
     */
    @GetMapping("/getItemList")
    @Operation(summary = "查询学段信息列表，当前部门不存在，则返回上级部门数据")
    public RestResponse getItemList(BusiEduLearningStage busiEduSubject)
    {
        Assert.notNull(busiEduSubject.getDeptId(), "部门ID不能为空！");
        Map<Long, BusiEduLearningStage> m = EduLearningStageCache.getInstance().getEduObjsByDeptId(busiEduSubject.getDeptId());
        return RestResponse.success(m != null ? m.values() : null);
    }

    /**
     * 导出学段信息，小学，初中，高中，大学等列表
     */
    @Log(title = "学段信息，小学，初中，高中，大学等", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出学段信息，小学，初中，高中，大学等列表")
    public RestResponse export(BusiEduLearningStage busiEduLearningStage)
    {
        List<BusiEduLearningStage> list = busiEduLearningStageService.selectBusiEduLearningStageList(busiEduLearningStage);
        ExcelUtil<BusiEduLearningStage> util = new ExcelUtil<BusiEduLearningStage>(BusiEduLearningStage.class);
        return util.exportExcel(list, "eduLearningStage");
    }

    /**
     * 获取学段信息，小学，初中，高中，大学等详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取学段信息，小学，初中，高中，大学等详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiEduLearningStageService.selectBusiEduLearningStageById(id));
    }

    /**
     * 新增学段信息，小学，初中，高中，大学等
     */
    @Log(title = "学段信息，小学，初中，高中，大学等", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增学段信息，小学，初中，高中，大学等")
    public RestResponse add(@RequestBody BusiEduLearningStage busiEduLearningStage)
    {
        return toAjax(busiEduLearningStageService.insertBusiEduLearningStage(busiEduLearningStage));
    }

    /**
     * 修改学段信息，小学，初中，高中，大学等
     */
    @Log(title = "学段信息，小学，初中，高中，大学等", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "修改学段信息，小学，初中，高中，大学等")
    public RestResponse edit(@RequestBody BusiEduLearningStage busiEduLearningStage, @PathVariable Long id)
    {
        busiEduLearningStage.setId(id);
        return toAjax(busiEduLearningStageService.updateBusiEduLearningStage(busiEduLearningStage));
    }

    /**
     * 删除学段信息，小学，初中，高中，大学等
     */
    @Log(title = "学段信息，小学，初中，高中，大学等", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
	@Operation(summary = "删除学段信息，小学，初中，高中，大学等")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiEduLearningStageService.deleteBusiEduLearningStageById(id));
    }
    
    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiEduLearningStageService.getDeptRecordCounts());
    }
}

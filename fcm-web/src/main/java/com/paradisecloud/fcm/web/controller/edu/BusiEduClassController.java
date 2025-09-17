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
import com.paradisecloud.fcm.dao.model.BusiEduClass;
import com.paradisecloud.fcm.edu.cache.EduClassCache;
import com.paradisecloud.fcm.edu.interfaces.IBusiEduClassService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 班级信息Controller
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@RestController
@RequestMapping("/busi/eduClass")
@Tag(name = "班级信息")
public class BusiEduClassController extends BaseController
{
    @Autowired
    private IBusiEduClassService busiEduClassService;

    /**
     * 查询班级信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询班级信息列表")
    public RestResponse list(BusiEduClass busiEduClass)
    {
        startPage();
        List<BusiEduClass> list = busiEduClassService.selectBusiEduClassList(busiEduClass);
        return getDataTable(list);
    }
    
    /**
     * 查询学科信息列表
     */
    @GetMapping("/getItemList")
    @Operation(summary = "查询班级信息列表，当前部门不存在，则返回上级部门数据")
    public RestResponse getItemList(BusiEduClass busiEduSubject)
    {
        Assert.notNull(busiEduSubject.getDeptId(), "部门ID不能为空！");
        Map<Long, BusiEduClass> m = EduClassCache.getInstance().getEduClassesByDeptId(busiEduSubject.getDeptId());
        return RestResponse.success(m != null ? m.values() : null);
    }

    /**
     * 导出班级信息列表
     */
    @Log(title = "班级信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出班级信息列表")
    public RestResponse export(BusiEduClass busiEduClass)
    {
        List<BusiEduClass> list = busiEduClassService.selectBusiEduClassList(busiEduClass);
        ExcelUtil<BusiEduClass> util = new ExcelUtil<BusiEduClass>(BusiEduClass.class);
        return util.exportExcel(list, "eduClass");
    }

    /**
     * 获取班级信息详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取班级信息详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiEduClassService.selectBusiEduClassById(id));
    }

    /**
     * 新增班级信息
     */
    @Log(title = "班级信息", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增班级信息")
    public RestResponse add(@RequestBody BusiEduClass busiEduClass)
    {
        return toAjax(busiEduClassService.insertBusiEduClass(busiEduClass));
    }

    /**
     * 修改班级信息
     */
    @Log(title = "班级信息", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "修改班级信息")
    public RestResponse edit(@RequestBody BusiEduClass busiEduClass, @PathVariable Long id)
    {
        busiEduClass.setId(id);
        return toAjax(busiEduClassService.updateBusiEduClass(busiEduClass));
    }

    /**
     * 删除班级信息
     */
    @Log(title = "班级信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
	@Operation(summary = "删除班级信息")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiEduClassService.deleteBusiEduClassById(id));
    }
    
    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiEduClassService.getDeptRecordCounts());
    }
}

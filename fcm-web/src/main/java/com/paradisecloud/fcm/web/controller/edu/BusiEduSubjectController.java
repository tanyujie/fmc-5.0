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
import com.paradisecloud.fcm.dao.model.BusiEduSubject;
import com.paradisecloud.fcm.edu.cache.EduSubjectCache;
import com.paradisecloud.fcm.edu.interfaces.IBusiEduSubjectService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 学科信息Controller
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@RestController
@RequestMapping("/busi/eduSubject")
@Tag(name = "学科信息")
public class BusiEduSubjectController extends BaseController
{
    @Autowired
    private IBusiEduSubjectService busiEduSubjectService;

    /**
     * 查询学科信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询学科信息列表")
    public RestResponse list(BusiEduSubject busiEduSubject)
    {
        startPage();
        List<BusiEduSubject> list = busiEduSubjectService.selectBusiEduSubjectList(busiEduSubject);
        return getDataTable(list);
    }
    
    /**
     * 查询学科信息列表
     */
    @GetMapping("/getItemList")
    @Operation(summary = "查询学科信息列表，当前部门不存在，则返回上级部门数据")
    public RestResponse getItemList(BusiEduSubject busiEduSubject)
    {
        Assert.notNull(busiEduSubject.getDeptId(), "部门ID不能为空！");
        Map<Long, BusiEduSubject> m = EduSubjectCache.getInstance().getEduObjsByDeptId(busiEduSubject.getDeptId());
        return RestResponse.success(m != null ? m.values() : null);
    }

    /**
     * 导出学科信息列表
     */
    @Log(title = "学科信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出学科信息列表")
    public RestResponse export(BusiEduSubject busiEduSubject)
    {
        List<BusiEduSubject> list = busiEduSubjectService.selectBusiEduSubjectList(busiEduSubject);
        ExcelUtil<BusiEduSubject> util = new ExcelUtil<BusiEduSubject>(BusiEduSubject.class);
        return util.exportExcel(list, "eduSubject");
    }

    /**
     * 获取学科信息详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取学科信息详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiEduSubjectService.selectBusiEduSubjectById(id));
    }

    /**
     * 新增学科信息
     */
    @Log(title = "学科信息", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增学科信息")
    public RestResponse add(@RequestBody BusiEduSubject busiEduSubject)
    {
        return toAjax(busiEduSubjectService.insertBusiEduSubject(busiEduSubject));
    }

    /**
     * 修改学科信息
     */
    @Log(title = "学科信息", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "修改学科信息")
    public RestResponse edit(@RequestBody BusiEduSubject busiEduSubject, @PathVariable Long id)
    {
        busiEduSubject.setId(id);
        return toAjax(busiEduSubjectService.updateBusiEduSubject(busiEduSubject));
    }

    /**
     * 删除学科信息
     */
    @Log(title = "学科信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
	@Operation(summary = "删除学科信息")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiEduSubjectService.deleteBusiEduSubjectById(id));
    }
    
    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiEduSubjectService.getDeptRecordCounts());
    }
}

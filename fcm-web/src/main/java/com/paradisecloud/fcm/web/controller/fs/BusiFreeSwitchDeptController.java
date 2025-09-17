package com.paradisecloud.fcm.web.controller.fs;

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

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.terminal.fs.interfaces.IBusiFreeSwitchDeptService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 租户绑定服务器资源Controller
 * 
 * @author zyz
 * @date 2021-09-02
 */
@RestController
@RequestMapping("/freeSwitch/dept")
@Tag(name = "租户绑定服务器资源")
public class BusiFreeSwitchDeptController extends BaseController
{
    @Autowired
    private IBusiFreeSwitchDeptService busiFreeSwitchDeptService;

    /**
     * 查询租户绑定服务器资源列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询租户绑定服务器资源列表")
    public RestResponse list(BusiFreeSwitchDept busiFreeSwitchDept)
    {
        startPage();
        List<BusiFreeSwitchDept> list = busiFreeSwitchDeptService.selectBusiFreeSwitchDeptList(busiFreeSwitchDept);
        return getDataTable(list);
    }
    
    /**
     * 查询租户绑定服务器资源列表
     */
    @GetMapping("/bindDept/list")
    @Operation(summary = "查询租户绑定服务器资源列表")
    public RestResponse deptList(BusiFreeSwitchDept busiFreeSwitchDept)
    {
    	List<ModelBean> modelBeans = busiFreeSwitchDeptService.selectBusiFreeSwitchDepts(busiFreeSwitchDept);
        return RestResponse.success(modelBeans);
    }

    /**
     * 导出租户绑定服务器资源列表
     */
    @GetMapping("/export")
    @Operation(summary = "导出租户绑定服务器资源列表")
    public RestResponse export(BusiFreeSwitchDept busiFreeSwitchDept)
    {
        List<BusiFreeSwitchDept> list = busiFreeSwitchDeptService.selectBusiFreeSwitchDeptList(busiFreeSwitchDept);
        ExcelUtil<BusiFreeSwitchDept> util = new ExcelUtil<BusiFreeSwitchDept>(BusiFreeSwitchDept.class);
        return util.exportExcel(list, "dept");
    }

    /**
     * 获取租户绑定服务器资源详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取租户绑定服务器资源详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiFreeSwitchDeptService.selectBusiFreeSwitchDeptById(id));
    }

    /**
     * 新增租户绑定服务器资源
     */
    @PostMapping
    @Operation(summary = "新增租户绑定服务器资源")
    public RestResponse add(@RequestBody BusiFreeSwitchDept busiFreeSwitchDept)
    {
        return toAjax(busiFreeSwitchDeptService.insertBusiFreeSwitchDept(busiFreeSwitchDept));
    }

    /**
     * 修改租户绑定服务器资源
     */
    @PutMapping
    @Operation(summary = "修改租户绑定服务器资源")
    public RestResponse edit(@RequestBody BusiFreeSwitchDept busiFreeSwitchDept)
    {
        return toAjax(busiFreeSwitchDeptService.updateBusiFreeSwitchDept(busiFreeSwitchDept));
    }

    /**
     * 删除租户绑定服务器资源
     */
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除租户绑定服务器资源")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiFreeSwitchDeptService.deleteBusiFreeSwitchDeptByIds(ids));
    }
}

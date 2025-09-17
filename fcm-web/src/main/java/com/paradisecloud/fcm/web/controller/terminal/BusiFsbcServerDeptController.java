package com.paradisecloud.fcm.web.controller.terminal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.dao.model.BusiFsbcServerDept;
import com.paradisecloud.fcm.terminal.fsbc.service.intefaces.IBusiFsbcServerDeptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * FSBC服务器-部门映射Controller
 * 
 * @author lilinhai
 * @date 2021-04-21
 */
@RestController
@RequestMapping("/busi/fsbcDept")
@Tag(name = "FSBC服务器-部门映射")
public class BusiFsbcServerDeptController extends BaseController
{
    @Autowired
    private IBusiFsbcServerDeptService busiFsbcServerDeptService;

    /**
     * 查询FSBC服务器-部门映射列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询FSBC服务器-部门映射列表")
    public RestResponse list(BusiFsbcServerDept busiFsbcServerDept)
    {
        return RestResponse.success(busiFsbcServerDeptService.selectBusiFsbcServerDeptList(busiFsbcServerDept));
    }

    /**
     * 获取FSBC服务器-部门映射详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取FSBC服务器-部门映射详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiFsbcServerDeptService.selectBusiFsbcServerDeptById(id));
    }

    /**
     * 新增FSBC服务器-部门映射
     */
    @Log(title = "FSBC服务器-部门映射", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增FSBC服务器-部门映射", description = "绑定FSBC")
    public RestResponse add(@RequestBody BusiFsbcServerDept busiFsbcServerDept)
    {
        return toAjax(busiFsbcServerDeptService.insertBusiFsbcServerDept(busiFsbcServerDept));
    }

    /**
     * 修改FSBC服务器-部门映射
     */
    /*@PreAuthorize("@ss.hasPermi('busi:fsbcDept:edit')")
    @Log(title = "FSBC服务器-部门映射", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/{id}")
    @Operation(summary = "修改FSBC服务器-部门映射")*/
    @Operation(summary = "", description = "修改FSBC绑定")
    public RestResponse edit(@RequestBody BusiFsbcServerDept busiFsbcServerDept, @PathVariable("id") Long id)
    {
        busiFsbcServerDept.setId(id);
        return toAjax(busiFsbcServerDeptService.updateBusiFsbcServerDept(busiFsbcServerDept));
    }

    /**
     * 删除FSBC服务器-部门映射
     */
    @Log(title = "FSBC服务器-部门映射", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
	@Operation(summary = "删除FSBC服务器-部门映射", description = "解绑FSBC")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiFsbcServerDeptService.deleteBusiFsbcServerDeptById(id));
    }
}

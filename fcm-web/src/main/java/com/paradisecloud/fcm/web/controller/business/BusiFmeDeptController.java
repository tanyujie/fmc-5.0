package com.paradisecloud.fcm.web.controller.business;

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
import com.paradisecloud.fcm.dao.model.BusiFmeDept;
import com.paradisecloud.fcm.fme.service.interfaces.IBusiFmeDeptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）Controller
 * 
 * @author lilinhai
 * @date 2021-01-28
 */
//@RestController
@RequestMapping("/busi/fmeDept")
@Tag(name = "租户-FME映射表（一个FME/FME集群可以分配给多个租户，一对多）")
public class BusiFmeDeptController extends BaseController
{
    @Autowired
    private IBusiFmeDeptService busiFmeGroupDeptService;

    /**
     * 查询FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询租户--FME集群分配记录")
    public RestResponse list(BusiFmeDept busiFmeDept)
    {
        return RestResponse.success(busiFmeGroupDeptService.selectBusiFmeDeptList(busiFmeDept));
    }

    /**
     * 获取FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取租户--FME集群分配记录详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiFmeGroupDeptService.selectBusiFmeDeptById(id));
    }

    /**
     * 新增FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     */
    @PostMapping("")
    @Operation(summary = "新增租户--FME集群分配记录")
    public RestResponse add(@RequestBody BusiFmeDept busiFmeDept)
    {
        return toAjax(busiFmeGroupDeptService.insertBusiFmeDept(busiFmeDept));
    }

    /**
     * 修改FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     */
    @PutMapping("{id}")
    @Operation(summary = "修改租户--FME集群分配记录")
    public RestResponse edit(@RequestBody BusiFmeDept busiFmeDept, @PathVariable Long id)
    {
        busiFmeDept.setId(id);
        return toAjax(busiFmeGroupDeptService.updateBusiFmeDept(busiFmeDept));
    }

    /**
     * 删除FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     */
	@DeleteMapping("/{id}")
	@Operation(summary = "删除租户--FME集群分配记录")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiFmeGroupDeptService.deleteBusiFmeDeptById(id));
    }
}

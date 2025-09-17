package com.paradisecloud.fcm.web.controller.smc3;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3Dept;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）Controller
 * 
 * @author lilinhai
 * @date 2021-01-28
 */
@RestController
@RequestMapping("/busi/mcu/Smc3/dept")
@Tag(name = "租户-MCU映射表（一个MCU/MCU集群可以分配给多个租户，一对多）")
public class BusiMcuSmc3DeptController extends BaseController
{
    @Resource
    private IBusiMcuSmc3DeptService busiMcuSmc3DeptService;

    /**
     * 查询MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询租户--MCU集群分配记录")
    public RestResponse list(BusiMcuSmc3Dept busiMcuSmc3Dept)
    {
        return RestResponse.success(busiMcuSmc3DeptService.selectBusiMcuSmc3DeptList(busiMcuSmc3Dept));
    }

    /**
     * 获取MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取租户--MCU集群分配记录详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiMcuSmc3DeptService.selectBusiMcuSmc3DeptById(id));
    }

    /**
     * 新增MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    @PostMapping("")
    @Operation(summary = "新增租户--MCU集群分配记录")
    public RestResponse add(@RequestBody BusiMcuSmc3Dept busiMcuSmc3Dept)
    {
        return toAjax(busiMcuSmc3DeptService.insertBusiMcuSmc3Dept(busiMcuSmc3Dept));
    }

    /**
     * 修改MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
    @PutMapping("{id}")
    @Operation(summary = "修改租户--MCU集群分配记录")
    public RestResponse edit(@RequestBody BusiMcuSmc3Dept busiMcuSmc3Dept, @PathVariable Long id)
    {
        busiMcuSmc3Dept.setId(id);
        return toAjax(busiMcuSmc3DeptService.updateBusiMcuSmc3Dept(busiMcuSmc3Dept));
    }

    /**
     * 删除MCU组分配租户的中间（一个MCU组可以分配给多个租户，一对多）
     */
	@DeleteMapping("/{id}")
	@Operation(summary = "删除租户--MCU集群分配记录")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiMcuSmc3DeptService.deleteBusiMcuSmc3DeptById(id));
    }
}

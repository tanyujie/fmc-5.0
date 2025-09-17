package com.paradisecloud.fcm.web.controller.live;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiLiveDept;
import com.paradisecloud.fcm.service.interfaces.IBusiLiveDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）Controller
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
@RestController
@RequestMapping("/busi/live/dept")
@Tag(name = "直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）")
public class BusiLiveDeptController extends BaseController
{
    @Resource
    private IBusiLiveDeptService busiLiveDeptService;

    /**
     * 查询直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）列表")
    public RestResponse list(BusiLiveDept busiLiveDept)
    {
        List<ModelBean> list = busiLiveDeptService.selectBusiLiveDeptList(busiLiveDept);
        return getDataTable(list);
    }

    /**
     * 获取直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiLiveDeptService.selectBusiLiveDeptById(id));
    }

    /**
     * 新增直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     */
    @Log(title = "直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）")
    public RestResponse add(@RequestBody BusiLiveDept busiLiveDept)
    {
        return toAjax(busiLiveDeptService.insertBusiLiveDept(busiLiveDept));
    }

    /**
     * 修改直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     */
    @Log(title = "直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）")
    public RestResponse edit(@RequestBody BusiLiveDept busiLiveDept)
    {
        return toAjax(busiLiveDeptService.updateBusiLiveDept(busiLiveDept));
    }

    /**
     * 删除直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）
     */
    @Log(title = "直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiLiveDeptService.deleteBusiLiveDeptByIds(ids));
    }
}

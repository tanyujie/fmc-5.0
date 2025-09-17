package com.paradisecloud.fcm.web.controller.ops;

import java.util.List;

import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.model.BusiOps;
import com.paradisecloud.fcm.dao.model.vo.BusiOpsVo;
import com.paradisecloud.fcm.mqtt.interfaces.IOpsActionService;
import com.paradisecloud.fcm.mqtt.task.OpsPushRegisterTask;
import com.paradisecloud.fcm.ops.cloud.interfaces.IBusiOpsService;
import com.paradisecloud.system.model.ExcelUtil;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;

/**
 * OPSController
 * 
 * @author lilinhai
 * @date 2024-07-26
 */
@RestController
@RequestMapping("/busi/ops")
@Tag(name = "OPS")
public class BusiOpsController extends BaseController
{
    @Resource
    private IBusiOpsService busiOpsService;
    @Resource
    private TaskService taskService;

    /**
     * 查询OPS列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询OPS列表")
    public RestResponse list(BusiOpsVo busiOps)
    {
        startPage();
        PaginationData<Object> paginationData = busiOpsService.selectBusiOpsList(busiOps);
        return RestResponse.success(0L, "查询成功", paginationData);
    }

    /**
     * 导出OPS列表
     */
//    @PreAuthorize("@ss.hasPermi('busi:ops:export')")
    @Log(title = "OPS", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出OPS列表")
    public RestResponse export(BusiOpsVo busiOps)
    {
        List list = busiOpsService.selectBusiOpsList(busiOps).getRecords();
        ExcelUtil<BusiOps> util = new ExcelUtil<BusiOps>(BusiOps.class);
        return util.exportExcel(list, "ops");
    }

    /**
     * 获取OPS详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取OPS详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiOpsService.selectBusiOpsById(id));
    }

    /**
     * 新增OPS
     */
//    @PreAuthorize("@ss.hasPermi('busi:ops:add')")
    @Log(title = "OPS", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增OPS")
    public RestResponse add(@RequestBody BusiOpsVo busiOps)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiOps.setCreateUserId(loginUser.getUser().getUserId());
        }
        return toAjax(busiOpsService.insertBusiOps(busiOps));
    }

    /**
     * 修改OPS
     */
//    @PreAuthorize("@ss.hasPermi('busi:ops:edit')")
    @Log(title = "OPS", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改OPS")
    public RestResponse edit(@RequestBody BusiOpsVo busiOps)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiOps.setUpdateUserId(loginUser.getUser().getUserId());
        }
        int row = busiOpsService.updateBusiOps(busiOps);
        if (row > 0) {
            OpsPushRegisterTask opsPushRegisterTask = new OpsPushRegisterTask(String.valueOf(busiOps.getId()), 100, busiOps.getId());
            taskService.addTask(opsPushRegisterTask);
        }
        return toAjax(row);
    }

    /**
     * 删除OPS
     */
//    @PreAuthorize("@ss.hasPermi('busi:ops:remove')")
    @Log(title = "OPS", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除OPS")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        if (true) {
            return RestResponse.fail("OPS不能删除！");
        }
        return toAjax(busiOpsService.deleteBusiOpsByIds(ids));
    }
}

package com.paradisecloud.fcm.web.controller.mqtt;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.paradisecloud.fcm.dao.model.BusiTerminalLog;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiTerminalLogService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 终端日志信息Controller
 * 
 * @author lilinhai
 * @date 2021-10-13
 */
@RestController
@RequestMapping("/busi/log")
@Tag(name = "终端日志信息")
public class BusiTerminalLogController extends BaseController
{
    @Autowired
    private IBusiTerminalLogService busiTerminalLogService;

    /**
     * 查询终端日志信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询终端日志信息列表")
    public RestResponse list(BusiTerminalLog busiTerminalLog)
    {
        startPage();
        List<BusiTerminalLog> list = busiTerminalLogService.selectBusiTerminalLogList(busiTerminalLog);
        PaginationData<ModelBean> pd = new PaginationData();
        pd.setTotal((new PageInfo(list)).getTotal());

        for (BusiTerminalLog busiTerminalLogTemp : list) {
            ModelBean modelBean = new ModelBean(busiTerminalLogTemp);
            modelBean.put("logSize", FileUtil.getFileSizeWithUnit(busiTerminalLogTemp.getLogSize()));
            pd.addRecord(modelBean);
        }

        return RestResponse.success(0L, "查询成功", pd);
    }

    /**
     * 导出终端日志信息列表
     */
    @Log(title = "终端日志信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出终端日志信息列表")
    public RestResponse export(BusiTerminalLog busiTerminalLog)
    {
        List<BusiTerminalLog> list = busiTerminalLogService.selectBusiTerminalLogList(busiTerminalLog);
        ExcelUtil<BusiTerminalLog> util = new ExcelUtil<BusiTerminalLog>(BusiTerminalLog.class);
        return util.exportExcel(list, "log");
    }

    /**
     * 获取终端日志信息详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取终端日志信息详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiTerminalLogService.selectBusiTerminalLogById(id));
    }

    /**
     * 新增终端日志信息
     */
    @Log(title = "终端日志信息", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增终端日志信息")
    public RestResponse add(@RequestBody BusiTerminalLog busiTerminalLog)
    {
        return toAjax(busiTerminalLogService.insertBusiTerminalLog(busiTerminalLog));
    }

    /**
     * 修改终端日志信息
     */
    @Log(title = "终端日志信息", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改终端日志信息")
    public RestResponse edit(@RequestBody BusiTerminalLog busiTerminalLog)
    {
        return toAjax(busiTerminalLogService.updateBusiTerminalLog(busiTerminalLog));
    }

    /**
     * 删除终端日志信息
     */
    @Log(title = "终端日志信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除终端日志信息")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiTerminalLogService.deleteBusiTerminalLogByIds(ids));
    }
}

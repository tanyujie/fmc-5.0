package com.paradisecloud.fcm.web.controller.mqtt;

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

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.dao.model.BusiTerminalSysInfo;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiTerminalSysInfoService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 终端系统信息Controller
 * 
 * @author zyz
 * @date 2021-10-11
 */
@RestController
@RequestMapping("/system/info")
@Tag(name = "终端系统信息")
public class BusiTerminalSysInfoController extends BaseController
{
    @Autowired
    private IBusiTerminalSysInfoService busiTerminalSysInfoService;

    /**
     * 查询终端系统信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询终端系统信息列表")
    public RestResponse list(BusiTerminalSysInfo busiTerminalSysInfo)
    {
        startPage();
        List<BusiTerminalSysInfo> list = busiTerminalSysInfoService.selectBusiTerminalSysInfoList(busiTerminalSysInfo);
        return getDataTable(list);
    }

    /**
     * 导出终端系统信息列表
     */
    @Log(title = "终端系统信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出终端系统信息列表")
    public RestResponse export(BusiTerminalSysInfo busiTerminalSysInfo)
    {
        List<BusiTerminalSysInfo> list = busiTerminalSysInfoService.selectBusiTerminalSysInfoList(busiTerminalSysInfo);
        ExcelUtil<BusiTerminalSysInfo> util = new ExcelUtil<BusiTerminalSysInfo>(BusiTerminalSysInfo.class);
        return util.exportExcel(list, "info");
    }

    /**
     * 获取终端系统信息详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取终端系统信息详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiTerminalSysInfoService.selectBusiTerminalSysInfoById(id));
    }
    
    /**
     * 获取终端的类型代码，用于升级
     */
    @GetMapping(value = "/terminalType")
    @Operation(summary = "获取终端的类型代码，用于升级")
    public RestResponse getTerminalType()
    {
        return RestResponse.success(busiTerminalSysInfoService.getTerminalTypeByGroup());
    }

    /**
     * 新增终端系统信息
     */
    @Log(title = "终端系统信息", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增终端系统信息")
    public RestResponse add(@RequestBody BusiTerminalSysInfo busiTerminalSysInfo)
    {
        return toAjax(busiTerminalSysInfoService.insertBusiTerminalSysInfo(busiTerminalSysInfo));
    }

    /**
     * 修改终端系统信息
     */
    @Log(title = "终端系统信息", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改终端系统信息")
    public RestResponse edit(@RequestBody BusiTerminalSysInfo busiTerminalSysInfo)
    {
        return toAjax(busiTerminalSysInfoService.updateBusiTerminalSysInfo(busiTerminalSysInfo));
    }

    /**
     * 删除终端系统信息
     */
    @Log(title = "终端系统信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除终端系统信息")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiTerminalSysInfoService.deleteBusiTerminalSysInfoByIds(ids));
    }
}

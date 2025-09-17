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
import com.paradisecloud.fcm.dao.model.BusiRegisterTerminal;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiRegisterTerminalService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 需绑定fs账号的终端Controller
 * 
 * @author zyz
 * @date 2021-11-04
 */
@RestController
@RequestMapping("/register/terminal")
@Tag(name = "需绑定fs账号的终端")
public class BusiRegisterTerminalController extends BaseController
{
    @Autowired
    private IBusiRegisterTerminalService busiRegisterTerminalService;

    /**
     * 查询需绑定fs账号的终端列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询需绑定fs账号的终端列表")
    public RestResponse list(BusiRegisterTerminal busiRegisterTerminal)
    {
        startPage();
        List<BusiRegisterTerminal> list = busiRegisterTerminalService.selectBusiRegisterTerminalList(busiRegisterTerminal);
        return getDataTable(list);
    }

    /**
     * 导出需绑定fs账号的终端列表
     */
    @Log(title = "需绑定fs账号的终端", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出需绑定fs账号的终端列表")
    public RestResponse export(BusiRegisterTerminal busiRegisterTerminal)
    {
        List<BusiRegisterTerminal> list = busiRegisterTerminalService.selectBusiRegisterTerminalList(busiRegisterTerminal);
        ExcelUtil<BusiRegisterTerminal> util = new ExcelUtil<BusiRegisterTerminal>(BusiRegisterTerminal.class);
        return util.exportExcel(list, "terminal");
    }

    /**
     * 获取需绑定fs账号的终端详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取需绑定fs账号的终端详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiRegisterTerminalService.selectBusiRegisterTerminalById(id));
    }

    /**
     * 新增需绑定fs账号的终端
     */
    @Log(title = "需绑定fs账号的终端", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增需绑定fs账号的终端")
    public RestResponse add(@RequestBody BusiRegisterTerminal busiRegisterTerminal)
    {
        return toAjax(busiRegisterTerminalService.insertBusiRegisterTerminal(busiRegisterTerminal));
    }

    /**
     * 修改需绑定fs账号的终端
     */
    @Log(title = "需绑定fs账号的终端", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改需绑定fs账号的终端")
    public RestResponse edit(@RequestBody BusiRegisterTerminal busiRegisterTerminal)
    {
        return toAjax(busiRegisterTerminalService.updateBusiRegisterTerminal(busiRegisterTerminal));
    }

    /**
     * 删除需绑定fs账号的终端
     */
    @Log(title = "需绑定fs账号的终端", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除需绑定fs账号的终端")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiRegisterTerminalService.deleteBusiRegisterTerminalByIds(ids));
    }
}

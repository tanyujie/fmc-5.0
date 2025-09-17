package com.paradisecloud.fcm.web.controller.terminal;

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
import com.paradisecloud.fcm.dao.model.BusiFsbcRegistrationServer;
import com.paradisecloud.fcm.terminal.fsbc.service.intefaces.IBusiFsbcRegistrationServerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 终端FSBC注册服务器Controller
 * 
 * @author lilinhai
 * @date 2021-04-21
 */
@RestController
@RequestMapping("/busi/fsbcServer")
@Tag(name = "终端FSBC注册服务器")
public class BusiFsbcRegistrationServerController extends BaseController
{
    @Autowired
    private IBusiFsbcRegistrationServerService busiFsbcRegistrationServerService;

    /**
     * 查询终端FSBC注册服务器列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询终端FSBC注册服务器列表")
    public RestResponse list(BusiFsbcRegistrationServer busiFsbcRegistrationServer)
    {
        return RestResponse.success(busiFsbcRegistrationServerService.selectBusiFsbcRegistrationServerList(busiFsbcRegistrationServer));
    }

    /**
     * 获取终端FSBC注册服务器详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取终端FSBC注册服务器详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiFsbcRegistrationServerService.selectBusiFsbcRegistrationServerById(id));
    }

    /**
     * 新增终端FSBC注册服务器
     */
    @Log(title = "终端FSBC注册服务器", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增终端FSBC注册服务器", description = "新增FSBC服务器")
    public RestResponse add(@RequestBody BusiFsbcRegistrationServer busiFsbcRegistrationServer)
    {
        return toAjax(busiFsbcRegistrationServerService.insertBusiFsbcRegistrationServer(busiFsbcRegistrationServer));
    }

    /**
     * 修改终端FSBC注册服务器
     */
    @Log(title = "终端FSBC注册服务器", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/{id}")
    @Operation(summary = "修改终端FSBC注册服务器", description = "修改FSBC服务器")
    public RestResponse edit(@RequestBody BusiFsbcRegistrationServer busiFsbcRegistrationServer, @PathVariable("id") Long id)
    {
        busiFsbcRegistrationServer.setId(id);
        return toAjax(busiFsbcRegistrationServerService.updateBusiFsbcRegistrationServer(busiFsbcRegistrationServer));
    }

    /**
     * 删除终端FSBC注册服务器
     */
    @Log(title = "终端FSBC注册服务器", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
	@Operation(summary = "删除终端FSBC注册服务器", description = "删除FSBC服务器")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiFsbcRegistrationServerService.deleteBusiFsbcRegistrationServerById(id));
    }
}

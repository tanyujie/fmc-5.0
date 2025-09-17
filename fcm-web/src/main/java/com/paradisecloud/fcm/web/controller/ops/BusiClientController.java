package com.paradisecloud.fcm.web.controller.ops;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.dao.model.BusiClient;
import com.paradisecloud.fcm.dao.model.vo.BusiClientVo;
import com.paradisecloud.fcm.mqtt.interfaces.IClientActionService;
import com.paradisecloud.fcm.mqtt.task.ClientPushRegisterTask;
import com.paradisecloud.fcm.ops.cloud.cache.ClientCache;
import com.paradisecloud.fcm.ops.cloud.interfaces.IBusiClientService;
import com.paradisecloud.system.model.ExcelUtil;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * BusiClientController
 * 
 * @author lilinhai
 * @date 2024-07-26
 */
@RestController
@RequestMapping("/busi/client")
@Tag(name = "客户端")
public class BusiClientController extends BaseController
{
    @Resource
    private IBusiClientService busiClientService;
    @Resource
    private IClientActionService clientActionService;
    @Resource
    private TaskService taskService;

    /**
     * 查询客户端列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询客户端列表")
    public RestResponse list(BusiClientVo busiClient)
    {
        startPage();
        PaginationData<Object> paginationData = busiClientService.selectBusiClientList(busiClient);
        return RestResponse.success(0L, "查询成功", paginationData);
    }

    /**
     * 导出客户端列表
     */
//    @PreAuthorize("@ss.hasPermi('busi:ops:export')")
    @Log(title = "客户端", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出客户端列表")
    public RestResponse export(BusiClientVo busiClient)
    {
        List list = busiClientService.selectBusiClientList(busiClient).getRecords();
        ExcelUtil<BusiClient> util = new ExcelUtil<BusiClient>(BusiClient.class);
        return util.exportExcel(list, "ops");
    }

    /**
     * 获取客户端详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取客户端详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiClientService.selectBusiClientById(id));
    }

    /**
     * 新增客户端
     */
//    @PreAuthorize("@ss.hasPermi('busi:ops:add')")
    @Log(title = "客户端", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增客户端")
    public RestResponse add(@RequestBody BusiClientVo busiClient)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiClient.setCreateUserId(loginUser.getUser().getUserId());
        }
        return toAjax(busiClientService.insertBusiClient(busiClient));
    }

    /**
     * 修改客户端
     */
//    @PreAuthorize("@ss.hasPermi('busi:ops:edit')")
    @Log(title = "客户端", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改客户端")
    public RestResponse edit(@RequestBody BusiClientVo busiClient)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiClient.setUpdateUserId(loginUser.getUser().getUserId());
        }
        int row = busiClientService.updateBusiClient(busiClient);
        if (row > 0) {
            ClientPushRegisterTask clientPushRegisterTask = new ClientPushRegisterTask(String.valueOf(busiClient.getId()), 100, busiClient.getId());
            taskService.addTask(clientPushRegisterTask);
        }
        return toAjax(row);
    }

    /**
     * 删除客户端
     */
//    @PreAuthorize("@ss.hasPermi('busi:ops:remove')")
    @Log(title = "客户端", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除客户端")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        if (true) {
            return RestResponse.fail("客户端不能删除！");
        }
        return toAjax(busiClientService.deleteBusiClientByIds(ids));
    }

    /**
     * 更新license授权
     */
//    @PreAuthorize("@ss.hasPermi('busi:ops:add')")
    @Log(title = "更新license授权", businessType = BusinessType.UPDATE)
    @PostMapping("/updateLicense")
    @Operation(summary = "更新license授权")
    public RestResponse updateLicense(@RequestBody JSONObject jsonObject)
    {
        Long id = jsonObject.getLong("id");
        String license = jsonObject.getString("license");
        BusiClient busiClient = ClientCache.getInstance().get(id);
        if (busiClient == null) {
            return RestResponse.fail("客户端不存在，请刷新页面后重试！");
        }
        if (busiClient.getMqttOnlineStatus() != TerminalOnlineStatus.ONLINE.getValue()) {
            return RestResponse.fail("客户端不在线，请客户端在线后重试！");
        }
        clientActionService.updateLicense(busiClient.getSn(), license);
        return RestResponse.success(0, "授权已下发客户端", null);
    }
}

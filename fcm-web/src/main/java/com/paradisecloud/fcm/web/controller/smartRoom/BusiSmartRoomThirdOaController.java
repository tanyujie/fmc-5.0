package com.paradisecloud.fcm.web.controller.smartRoom;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomThirdOa;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomThirdOaService;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * 智慧办公第三方OAController
 *
 * @author lilinhai
 * @date 2024-03-06
 */
@RestController
@RequestMapping("/busi/oa")
@Tag(name = "智慧办公第三方OA")
public class BusiSmartRoomThirdOaController extends BaseController
{
    @Resource
    private IBusiSmartRoomThirdOaService busiSmartRoomThirdOaService;

    /**
     * 查询智慧办公第三方OA列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询智慧办公第三方OA列表")
    public RestResponse list(BusiSmartRoomThirdOa busiSmartRoomThirdOa)
    {
        startPage();
        List<BusiSmartRoomThirdOa> list = busiSmartRoomThirdOaService.selectBusiSmartRoomThirdOaList(busiSmartRoomThirdOa);
        return getDataTable(list);
    }

    /**
     * 获取智慧办公第三方OA详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取智慧办公第三方OA详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiSmartRoomThirdOaService.selectBusiSmartRoomThirdOaById(id));
    }

    /**
     * 新增智慧办公第三方OA
     */
    @PreAuthorize("@ss.hasPermi('busi:oa:add')")
    @Log(title = "智慧办公第三方OA", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增智慧办公第三方OA")
    public RestResponse add(@RequestBody BusiSmartRoomThirdOa busiSmartRoomThirdOa)
    {
        return toAjax(busiSmartRoomThirdOaService.insertBusiSmartRoomThirdOa(busiSmartRoomThirdOa));
    }

    /**
     * 修改智慧办公第三方OA
     */
    @PreAuthorize("@ss.hasPermi('busi:oa:edit')")
    @Log(title = "智慧办公第三方OA", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改智慧办公第三方OA")
    public RestResponse edit(@RequestBody BusiSmartRoomThirdOa busiSmartRoomThirdOa)
    {
        return toAjax(busiSmartRoomThirdOaService.updateBusiSmartRoomThirdOa(busiSmartRoomThirdOa));
    }

    /**
     * 删除智慧办公第三方OA
     */
    @PreAuthorize("@ss.hasPermi('busi:oa:remove')")
    @Log(title = "智慧办公第三方OA", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除智慧办公第三方OA")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiSmartRoomThirdOaService.deleteBusiSmartRoomThirdOaByIds(ids));
    }
}

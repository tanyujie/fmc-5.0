package com.paradisecloud.fcm.web.controller.smartRoom;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDoorplateRegister;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomDoorplateRegisterVO;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomDoorplateRegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 智慧办公房间门牌注册Controller
 *
 * @author lilinhai
 * @date 2024-02-19
 */
@RestController
@RequestMapping("/busi/register")
@Tag(name = "智慧办公房间门牌注册")
public class BusiSmartRoomDoorplateRegisterController extends BaseController
{
    @Resource
    private IBusiSmartRoomDoorplateRegisterService busiSmartRoomDoorplateRegisterService;

    /**
     * 查询智慧办公房间门牌注册列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询智慧办公房间门牌注册列表")
    public RestResponse list(BusiSmartRoomDoorplateRegisterVO busiSmartRoomDoorplateRegister)
    {
        startPage();
        List<BusiSmartRoomDoorplateRegister> list = busiSmartRoomDoorplateRegisterService.selectBusiSmartRoomDoorplateRegisterList(busiSmartRoomDoorplateRegister);
        return getDataTable(list);
    }

    /**
     * 获取智慧办公房间门牌注册详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取智慧办公房间门牌注册详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiSmartRoomDoorplateRegisterService.selectBusiSmartRoomDoorplateRegisterById(id));
    }

    /**
     * 删除智慧办公房间门牌注册
     */
    @PreAuthorize("@ss.hasPermi('busi:register:remove')")
    @Log(title = "智慧办公房间门牌注册", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除智慧办公房间门牌注册")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiSmartRoomDoorplateRegisterService.deleteBusiSmartRoomDoorplateRegisterByIds(ids));
    }
}

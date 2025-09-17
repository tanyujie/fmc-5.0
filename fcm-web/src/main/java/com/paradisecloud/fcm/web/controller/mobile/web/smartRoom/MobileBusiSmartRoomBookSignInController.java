package com.paradisecloud.fcm.web.controller.mobile.web.smartRoom;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomParticipant;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomParticipantVo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 移动端  房间预约签到Controller
 *
 * @author lilinhai
 * @date 2024-03-22
 */
@RestController
@RequestMapping("/mobileWeb/signIn")
@Tag(name = "房间预约签到")
public class MobileBusiSmartRoomBookSignInController extends BaseController
{
    @Resource
    private IBusiSmartRoomParticipantService busiSmartRoomParticipantService;

    /**
     * 查询房间预约签到列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询房间预约签到列表")
    public RestResponse list(BusiSmartRoomParticipantVo busiSmartRoomParticipantVo)
    {
        startPage();
        List<BusiSmartRoomParticipant> list = busiSmartRoomParticipantService.selectBusiSmartRoomParticipantList(busiSmartRoomParticipantVo);
        return getDataTable(list);
    }

    /**
     * 导出房间预约签到列表
     */
//    @PreAuthorize("@ss.hasPermi('system:in:export')")
//    @Log(title = "房间预约签到", businessType = BusinessType.EXPORT)
//    @GetMapping("/export")
//    @Operation(summary = "导出房间预约签到列表")
//    public RestResponse export(BusiSmartRoomBookSignIn busiSmartRoomBookSignIn)
//    {
//        List<BusiSmartRoomBookSignIn> list = busiSmartRoomBookSignInService.selectBusiSmartRoomBookSignInList(busiSmartRoomBookSignIn);
//        ExcelUtil<BusiSmartRoomBookSignIn> util = new ExcelUtil<BusiSmartRoomBookSignIn>(BusiSmartRoomBookSignIn.class);
//        return util.exportExcel(list, "in");
//    }

    /**
     * 获取房间预约签到详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取房间预约签到详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiSmartRoomParticipantService.selectBusiSmartRoomParticipantById(id));
    }

    /**
     * 预约签到
     */
    @Log(title = "预约签到", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "预约签到")
    public RestResponse add(@RequestBody BusiSmartRoomParticipant busiSmartRoomParticipant)
    {
        return toAjax(busiSmartRoomParticipantService.signIn(busiSmartRoomParticipant));
    }

    /**
     * 修改房间预约签到
     */
//    @PreAuthorize("@ss.hasPermi('system:in:edit')")
//    @Log(title = "房间预约签到", businessType = BusinessType.UPDATE)
//    @PutMapping
//    @Operation(summary = "修改房间预约签到")
//    public RestResponse edit(@RequestBody BusiSmartRoomBookSignIn busiSmartRoomBookSignIn)
//    {
//        return toAjax(busiSmartRoomBookSignInService.updateBusiSmartRoomBookSignIn(busiSmartRoomBookSignIn));
//    }

    /**
     * 删除房间预约签到
     */
//    @PreAuthorize("@ss.hasPermi('system:in:remove')")
//    @Log(title = "房间预约签到", businessType = BusinessType.DELETE)
//    @DeleteMapping("/{ids}")
//    @Operation(summary = "删除房间预约签到")
//    public RestResponse remove(@PathVariable Long[] ids)
//    {
//        return toAjax(busiSmartRoomBookSignInService.deleteBusiSmartRoomBookSignInByIds(ids));
//    }
}


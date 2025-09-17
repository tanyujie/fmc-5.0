package com.paradisecloud.fcm.web.controller.smartRoom;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomBookSignIn;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomParticipant;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomBookSignInVo;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomParticipantVo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomBookSignInService;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomParticipantService;
import com.paradisecloud.system.dao.mapper.SysUserMapper;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.SysDeptCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
 * 房间预约签到Controller
 *
 * @author lilinhai
 * @date 2024-03-22
 */
@RestController
@RequestMapping("/busi/signIn")
@Tag(name = "房间预约签到")
public class BusiSmartRoomBookSignInController extends BaseController
{
    @Resource
    private IBusiSmartRoomParticipantService busiSmartRoomParticipantService;
    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 查询房间预约签到列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询房间预约签到列表")
    public RestResponse list(BusiSmartRoomParticipantVo busiSmartRoomParticipantVo)
    {
        startPage();
        List<BusiSmartRoomParticipant> list = busiSmartRoomParticipantService.selectBusiSmartRoomParticipantList(busiSmartRoomParticipantVo);
        for (BusiSmartRoomParticipant busiSmartRoomParticipant : list) {
            Long userId = busiSmartRoomParticipant.getUserId();
            SysUser sysUser = sysUserMapper.selectUserById(userId);
            if (sysUser != null) {
                SysDept dept = sysUser.getDept();
                if (dept != null) {
                    String deptName = dept.getDeptName();
                    busiSmartRoomParticipant.getParams().put("deptName", deptName);
                }
            }
        }
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

//    /**
//     * 新增房间预约签到
//     */
//    @Log(title = "房间预约签到", businessType = BusinessType.INSERT)
//    @PostMapping
//    @Operation(summary = "新增房间预约签到")
//    public RestResponse add(@RequestBody BusiSmartRoomBookSignIn busiSmartRoomBookSignIn)
//    {
//        return toAjax(busiSmartRoomBookSignInService.insertBusiSmartRoomBookSignIn(busiSmartRoomBookSignIn));
//    }

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

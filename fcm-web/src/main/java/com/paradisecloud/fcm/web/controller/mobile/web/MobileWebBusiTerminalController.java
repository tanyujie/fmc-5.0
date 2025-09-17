package com.paradisecloud.fcm.web.controller.mobile.web;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.cache.LicenseCache;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiMcuZjDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.vo.TerminalSearchVo;
import com.paradisecloud.fcm.mcu.zj.cache.DeptMcuZjMappingCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.tencent.model.MeetingRoom;
import com.paradisecloud.fcm.tencent.model.reponse.RoomResponse;
import com.paradisecloud.fcm.tencent.service2.interfaces.IAttendeeTencentService;
import com.paradisecloud.fcm.terminal.fs.db.FreeSwitchTransaction;
import com.paradisecloud.fcm.web.service.interfaces.IBusiTerminalWebService;
import com.paradisecloud.system.model.LoginUser;
import com.sinhy.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 终端信息Controller
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/mobileWeb/terminal")
@Tag(name = "终端信息")
public class MobileWebBusiTerminalController extends BaseController
{
    @Resource
    private IBusiTerminalWebService busiTerminalWebService;
    @Resource
    private IAttendeeTencentService attendeeTencentService;

    /**
     * 腾讯Rooms
     * @param deptId
     * @param pageIndex
     * @param pageSize
     * @param meetingRoomName
     * @return
     */
    @GetMapping("/rooms/{deptId}")
    @Operation(summary = "获取rooms列表")
    public RestResponse rooms(@PathVariable Long deptId, @RequestParam(required = false,defaultValue = "1") Integer pageIndex,@RequestParam(required = false,defaultValue = "20") Integer pageSize,@RequestParam(required = false) String meetingRoomName) {
        return RestResponse.success(attendeeTencentService.rooms(deptId,pageIndex,pageSize,meetingRoomName));
    }

    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts/{businessFieldType}")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts(@PathVariable("businessFieldType") Integer businessFieldType)
    {
        return RestResponse.success(busiTerminalWebService.getDeptRecordCounts(businessFieldType));
    }

    /**
     * 查询终端信息列表
     */
    @GetMapping("/getAllTerminalType")
    @Operation(summary = "获取所有终端类型")
    public RestResponse getAllTerminalType()
    {
        List<Map<String, Object>> all = new ArrayList<>();
        List<Integer> terminalTypeList = ExternalConfigCache.getInstance().getTerminalTypeList();
        String region = ExternalConfigCache.getInstance().getRegion();
        if(Objects.equals(region,"ops")){
            String terminalTypeStr = LicenseCache.getInstance().getTermianlType();
            if(Strings.isBlank(terminalTypeStr)){
                Map<String, Object> item = new HashMap<>();
                item.put("type", TerminalType.IP.getId());
                item.put("name", TerminalType.IP.getDisplayName());
                all.add(item);
                return RestResponse.success(all);
            }else {
                List<Integer> terminalTypeListops=new ArrayList<>();
                String[] terminalTypeArr = terminalTypeStr.split(",");
                for (String terminalTypeT : terminalTypeArr) {
                    if (terminalTypeT.trim().length() > 0) {
                        try {
                            Integer terminalType = Integer.valueOf(terminalTypeT);
                            terminalTypeListops.add(terminalType);
                        } catch (Exception e) {
                        }
                    }
                }
                if (terminalTypeListops!=null&&terminalTypeListops.size() > 0) {
                    for (Integer terminalTypeInt : terminalTypeListops) {
                        TerminalType terminalType = TerminalType.convert(terminalTypeInt, false);
                        if (terminalType != null) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("type", terminalType.getId());
                            item.put("name", terminalType.getDisplayName());
                            all.add(item);
                        }
                    }
                    return RestResponse.success(all);
                }
            }
        }
        if (terminalTypeList!=null&&terminalTypeList.size() > 0) {
            for (Integer terminalTypeInt : terminalTypeList) {
                TerminalType terminalType = TerminalType.convert(terminalTypeInt, false);
                if (terminalType != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("type", terminalType.getId());
                    item.put("name", terminalType.getDisplayName());
                    all.add(item);
                }
            }
        } else {
            for (TerminalType terminalType : TerminalType.values()) {
//                if(TerminalType.isSMCNUMBER(terminalType.getId())){
//                    continue;
//                }
                Map<String, Object> itam = new HashMap<>();
                itam.put("type", terminalType.getId());
                itam.put("name", terminalType.getDisplayName());
                all.add(itam);
            }
        }

        return RestResponse.success(all);
    }
    
    /**
     * 查询终端信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询终端信息列表")
    public RestResponse list(TerminalSearchVo busiTerminal)
    {
        busiTerminal.setDeptId(getDeptId());
        busiTerminal.setBusinessFieldType(100);
        PaginationData<ModelBean> list = busiTerminalWebService.selectBusiTerminalList(busiTerminal);
        return RestResponse.success(list);
    }

    /**
     * 查询终端信息列表（智慧办公）
     */
    @GetMapping("/smart/list")
    @Operation(summary = "查询终端信息列表（智慧办公）")
    public RestResponse listForSmart(TerminalSearchVo busiTerminal)
    {
        busiTerminal.setDeptId(getDeptId());
        busiTerminal.setBusinessFieldType(100);
        List<ModelBean> modelBeanList = new ArrayList<>();
        PaginationData<ModelBean> list = busiTerminalWebService.selectBusiTerminalList(busiTerminal);
        for (ModelBean record : list.getRecords()) {
            ModelBean modelBean = new ModelBean();
            modelBean.put("id", record.get("id"));
            modelBean.put("name", record.get("name"));
            modelBean.put("status", record.get("onlineStatus"));
            modelBeanList.add(modelBean);
        }
        Object rooms = attendeeTencentService.rooms(getDeptId(), busiTerminal.getPageNum(), busiTerminal.getPageSize(), null);
        if (rooms != null) {
            RoomResponse roomResponse = (RoomResponse)rooms;
            for (MeetingRoom meetingRoom : roomResponse.getMeetingRoomList()) {
                ModelBean modelBean = new ModelBean();
                modelBean.put("id", meetingRoom.getMeetingRoomId());
                modelBean.put("name", meetingRoom.getMeetingRoomName());
                modelBean.put("status", meetingRoom.getMeetingRoomStatus());
                modelBeanList.add(modelBean);
            }
        }
        return RestResponse.success(modelBeanList);
    }

    /**
     * 获取终端信息详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "根据ID获取终端信息详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiTerminalWebService.selectBusiTerminalById(id));
    }

    /**
     * 新增终端信息
     */
    @Log(title = "终端信息", businessType = BusinessType.INSERT)
    @PostMapping("")
    @Operation(summary = "新增终端信息", description = "新增终端")
    @FreeSwitchTransaction
    public RestResponse add(@RequestBody BusiTerminal busiTerminal)
    {
        int rows = busiTerminalWebService.insertBusiTerminal(busiTerminal);
        if (rows > 0) {
            if (TerminalType.isZJ(busiTerminal.getType())) {
                if (busiTerminal.getExpiredDate() != null) {
                    if (busiTerminal.getExpiredDate().getTime() < DateUtils.getDayStartTime(DateUtils.getDiffDate(new Date(), 1, TimeUnit.DAYS)).getTime()) {
                        return RestResponse.success(0, "添加成功。（设置今天以及今天以前的过期时间将会在第二天过期！）", busiTerminal);
                    }
                }
            }
            return RestResponse.success(busiTerminal);
        }
        return toAjax(rows);
    }

    /**
     * 修改终端信息
     */
    @Log(title = "终端信息", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "根据ID修改终端信息", description = "修改终端")
    @FreeSwitchTransaction
    public RestResponse edit(@RequestBody BusiTerminal busiTerminal, @PathVariable Long id)
    {
        busiTerminal.setId(id);
        int rows = busiTerminalWebService.updateBusiTerminal(busiTerminal);
        if (rows > 0) {
            if (TerminalType.isZJ(busiTerminal.getType())) {
                if (busiTerminal.getExpiredDate() != null) {
                    if (busiTerminal.getExpiredDate().getTime() < DateUtils.getDayStartTime(DateUtils.getDiffDate(new Date(), 1, TimeUnit.DAYS)).getTime()) {
                        return RestResponse.success(0, "修改成功。（设置今天以及今天以前的过期时间将会在第二天过期！）", null);
                    }
                }
            }
        }
        return toAjax(rows);
    }

    /**
     * 删除终端信息
     */
    @Log(title = "终端信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "根据ID删除终端信息（支持批量删除，传多个id，逗号隔开）", description = "删除终端")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        int c = 0;
        String msg = "";
        for (Long id : ids)
        {
            try {
                c += busiTerminalWebService.deleteBusiTerminalById(id);
            } catch (Exception e) {
                if (!msg.contains(e.getMessage())) {
                    msg += "【" + e.getMessage() + "】";
                }
            }
        }

        if (ids.length > 1) {
            if (c == ids.length) {
                return RestResponse.success();
            } else {
                if (c > 1) {
                    return RestResponse.success("删除成功" + c + "条，删除失败" + (ids.length - c) + "条:" + msg);
                } else {
                    return RestResponse.fail("删除成功" + c + "条，删除失败" + (ids.length - c) + "条:" + msg);
                }
            }
        } else {
            if (c == 0) {
                if (StringUtils.isNotEmpty(msg)) {
                    return RestResponse.fail(msg);
                } else {
                    return RestResponse.fail("删除失败！");
                }
            } else {
                return toAjax(c);
            }
        }
    }

    /**
     * 根据部门ID获取绑定Mcu信息
     * @param deptId
     * @return
     */
    @Log(title = "获取绑定Mcu信息")
    @GetMapping("/getBindMcuInfo")
    @Operation(summary = "根据部门ID获取绑定Mcu信息")
    public RestResponse getBindMcuInfo(Long deptId) {
        BusiMcuZjDept busiMcuZjDept = DeptMcuZjMappingCache.getInstance().getBindMcu(deptId);
        if (busiMcuZjDept != null) {
            McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(busiMcuZjDept.getMcuId());
            if (mcuZjBridge != null) {
                ModelBean mb = new ModelBean(mcuZjBridge.getBusiMcuZj());
                mb.remove("password");
                mb.remove("adminPassword");
                mb.remove("devToken");
                return RestResponse.success(mb);
            }
        }
        return RestResponse.success();
    }

    /**
     * 获取随机账号
     */
    @Log(title = "获取随机账号")
    @PostMapping("/randomAccount")
    @Operation(summary = "获取随机账号")
    @FreeSwitchTransaction
    public RestResponse getRandomAccount(@RequestBody BusiTerminal busiTerminal)
    {
        return RestResponse.success(busiTerminalWebService.getRandomAccount(busiTerminal));
    }

    private Long getDeptId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        Long deptId = principal.getUser().getDeptId();
        return deptId;
    }
}

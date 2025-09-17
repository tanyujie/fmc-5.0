package com.paradisecloud.fcm.web.controller.terminal;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.CommonConfigCache;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.cache.LicenseCache;
import com.paradisecloud.fcm.dao.model.BusiMcuZjDept;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.vo.TerminalSearchVo;
import com.paradisecloud.fcm.mcu.zj.cache.DeptMcuZjMappingCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.terminal.fs.db.FreeSwitchTransaction;
import com.paradisecloud.fcm.web.service.interfaces.IBusiTerminalWebService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.utils.DateUtils;
import org.apache.logging.log4j.util.Strings;
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
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiTerminal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Resource;

/**
 * 终端信息Controller
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/terminal")
@Tag(name = "终端信息")
public class BusiTerminalController extends BaseController
{
    @Resource
    private IBusiTerminalWebService busiTerminalWebService;

    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts/{businessFieldType}")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts(@PathVariable("businessFieldType") Integer businessFieldType)
    {
        List<DeptRecordCount> deptRecordCountList = busiTerminalWebService.getDeptRecordCounts(businessFieldType);
        Map<Long, Map<String, Long>> deptCountMap = new HashMap<>();
        for (DeptRecordCount deptRecordCountTemp : deptRecordCountList) {
            Long deptIdTemp = deptRecordCountTemp.getDeptId();
            Long count = deptRecordCountTemp.getCount().longValue();
            {
                Map<String, Long> deptMap = deptCountMap.get(deptIdTemp);
                if (deptMap == null) {
                    deptMap = new HashMap<>();
                    deptMap.put("deptId", deptIdTemp);
                    deptMap.put("count", count);
                    deptMap.put("totalCount", count);
                    deptCountMap.put(deptIdTemp, deptMap);
                } else {
                    Long countExist = deptMap.get("count");
                    Long totalCountExist = deptMap.get("totalCount");
                    deptMap.put("count", countExist + count);
                    deptMap.put("totalCount", totalCountExist + count);
                }
            }
            SysDept sysDept = SysDeptCache.getInstance().get(deptIdTemp);
            if (sysDept != null) {
                String ancestors = sysDept.getAncestors();
                if (StringUtils.isNotEmpty(ancestors)) {
                    String[] deptIdArr = ancestors.split(",");
                    for (String deptIdStr : deptIdArr) {
                        Long deptIdT = null;
                        try {
                            deptIdT = Long.valueOf(deptIdStr);
                        } catch (Exception e) {
                        }
                        if (deptIdT != null) {
                            if (deptIdT.longValue() == deptIdTemp.longValue()) {
                                continue;
                            }
                            Map<String, Long> deptMap = deptCountMap.get(deptIdT);
                            if (deptMap == null) {
                                deptMap = new HashMap<>();
                                deptMap.put("deptId", deptIdT);
                                if (deptIdT.longValue() == deptIdTemp.longValue()) {
                                    deptMap.put("count", count);
                                } else {
                                    deptMap.put("count", 0L);
                                }
                                deptMap.put("totalCount", count);
                                deptCountMap.put(deptIdT, deptMap);
                            } else {
                                Long countExist = deptMap.get("count");
                                Long totalCountExist = deptMap.get("totalCount");
                                if (deptIdT.longValue() == deptIdTemp.longValue()) {
                                    deptMap.put("count", countExist + count);
                                }
                                deptMap.put("totalCount", totalCountExist + count);
                            }
                        }
                    }
                }
            }
        }
        return RestResponse.success(deptCountMap.values());
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
        PaginationData<ModelBean> list = busiTerminalWebService.selectBusiTerminalList(busiTerminal);
        return RestResponse.success(list);
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

    /**
     * 查询信息发布终端信息列表
     */
    @GetMapping("/getInfoDisplayTerminal")
    @Operation(summary = "查询信息发布终端信息列表")
    public RestResponse getInfoDisplayTerminal(TerminalSearchVo busiTerminal)
    {
        PaginationData<ModelBean> list = busiTerminalWebService.getInfoDisplayTerminal(busiTerminal);
        return RestResponse.success(list);
    }
}

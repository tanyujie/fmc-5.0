package com.paradisecloud.fcm.web.controller.ops;

import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.PurchaseType;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.model.BusiOps;
import com.paradisecloud.fcm.dao.model.BusiOpsResource;
import com.paradisecloud.fcm.dao.model.vo.BusiOpsResourceVo;
import com.paradisecloud.fcm.ding.cache.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.*;
import com.paradisecloud.fcm.mqtt.task.OpsPushRegisterTask;
import com.paradisecloud.fcm.ops.cloud.cache.OpsCache;
import com.paradisecloud.fcm.ops.cloud.interfaces.IBusiOpsResourceService;
import com.paradisecloud.fcm.tencent.cache.*;
import com.paradisecloud.system.model.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * ops资源Controller
 *
 * @author lilinhai
 * @date 2024-07-31
 */
@RestController
@RequestMapping("/busi/resource")
@Tag(name = "ops资源")
public class BusiOpsResourceController extends BaseController {
    @Resource
    private IBusiOpsResourceService busiOpsResourceService;
    @Resource
    private TaskService taskService;

    /**
     * 获取购买列表
     */
    @Operation(summary = "获取购买列表（不分页）")
    @GetMapping("/purchaseTypeList")
    public RestResponse purchaseList() {
        return success(PurchaseType.getPurchaseTypeList());
    }

    /**
     * 获取MCU桥列表
     */
    @Operation(summary = "获取MCU桥列表（不分页）")
    @GetMapping("/mcuTypeList")
    public RestResponse mcuTypeList() {
        List<ModelBean> busiMcus = new ArrayList<>();

        //tencent
        {
            Map<Long, Long> callCountMap = new HashMap<>();
            Map<Long, Long> coSpaceCountMap = new HashMap<>();
            Map<Long, Long> participantCountMap = new HashMap<>();
            for (TencentConferenceContext conferenceContext : TencentConferenceContextCache.getInstance().values()) {
                Long mcuId = conferenceContext.getTencentBridge().getBusiTencent().getId();
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                callCount++;
                callCountMap.put(mcuId, callCount);
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                coSpaceCount++;
                coSpaceCountMap.put(mcuId, coSpaceCount);
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                participantCount += conferenceContext.getMasterAttendee() != null ? 1 : 0;
                participantCount += conferenceContext.getAttendees().size();
                participantCount += conferenceContext.getMasterAttendees().size();
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    participantCount += conferenceContext.getCascadeAttendeesMap().get(deptId).size();
                }
                participantCountMap.put(mcuId, participantCount);
            }
            List<TencentBridge> fbs = new ArrayList<>(TencentBridgeCache.getInstance().getTencentBridgeMap().values());
            for (TencentBridge fmeBridge : fbs) {
                ModelBean mb = new ModelBean(fmeBridge.getBusiTencent());
                Long mcuId = fmeBridge.getBusiTencent().getId();
                McuType mcuType = McuType.MCU_TENCENT;
                String mcuIdStr = EncryptIdUtil.generateEncryptId(fmeBridge.getBusiTencent().getId(), mcuType.getCode());
                mb.put("mcuId", mcuIdStr);
                mb.put("mcuType", mcuType.getCode());
                mb.put("mcuTypeAlias", mcuType.getAlias());
                mb.remove("secretKey");
                mb.put("bindDeptCount", DeptTencentMappingCache.getInstance().getBindDeptCount(FmeType.SINGLE_NODE, fmeBridge.getBusiTencent().getId()));


                mb.put("tencentStatus", fmeBridge.isAvailable());
                // 连接失败原因
                mb.put("connectionFailedReason", fmeBridge.getConnectionFailedReason());


                // 参会者数
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                mb.put("participantCount", participantCount);

                // 活跃会议数
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                mb.put("callCount", callCount);

                // 会议号数
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                mb.put("coSpaceCount", coSpaceCount);
                busiMcus.add(mb);
            }
        }
        //ding
        {
            Map<Long, Long> callCountMap = new HashMap<>();
            Map<Long, Long> coSpaceCountMap = new HashMap<>();
            Map<Long, Long> participantCountMap = new HashMap<>();
            for (DingConferenceContext conferenceContext : DingConferenceContextCache.getInstance().values()) {
                Long mcuId = conferenceContext.getDingBridge().getBusiDing().getId();
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                callCount++;
                callCountMap.put(mcuId, callCount);
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                coSpaceCount++;
                coSpaceCountMap.put(mcuId, coSpaceCount);
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                participantCount += conferenceContext.getMasterAttendee() != null ? 1 : 0;
                participantCount += conferenceContext.getAttendees().size();
                participantCount += conferenceContext.getMasterAttendees().size();
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    participantCount += conferenceContext.getCascadeAttendeesMap().get(deptId).size();
                }
                participantCountMap.put(mcuId, participantCount);
            }
            List<DingBridge> fbs = new ArrayList<>(DingBridgeCache.getInstance().getDingBridgeMap().values());
            for (DingBridge fmeBridge : fbs) {
                ModelBean mb = new ModelBean(fmeBridge.getBusiDing());
                Long mcuId = fmeBridge.getBusiDing().getId();
                McuType mcuType = McuType.MCU_DING;
                String mcuIdStr = EncryptIdUtil.generateEncryptId(fmeBridge.getBusiDing().getId(), mcuType.getCode());
                mb.put("mcuId", mcuIdStr);
                mb.put("mcuType", mcuType.getCode());
                mb.put("mcuTypeAlias", mcuType.getAlias());
                mb.remove("secretKey");
                mb.put("bindDeptCount", DeptDingMappingCache.getInstance().getBindDeptCount(FmeType.SINGLE_NODE, fmeBridge.getBusiDing().getId()));


                // 连接失败原因
                mb.put("connectionFailedReason", fmeBridge.getConnectionFailedReason());


                // 参会者数
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                mb.put("participantCount", participantCount);

                // 活跃会议数
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                mb.put("callCount", callCount);

                // 会议号数
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                mb.put("coSpaceCount", coSpaceCount);
                busiMcus.add(mb);
            }
        }
        //hwcloud
        {
            Map<Long, Long> callCountMap = new HashMap<>();
            Map<Long, Long> coSpaceCountMap = new HashMap<>();
            Map<Long, Long> participantCountMap = new HashMap<>();
            for (HwcloudConferenceContext conferenceContext : HwcloudConferenceContextCache.getInstance().values()) {
                Long mcuId = conferenceContext.getHwcloudBridge().getBusiHwcloud().getId();
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                callCount++;
                callCountMap.put(mcuId, callCount);
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                coSpaceCount++;
                coSpaceCountMap.put(mcuId, coSpaceCount);
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                participantCount += conferenceContext.getMasterAttendee() != null ? 1 : 0;
                participantCount += conferenceContext.getAttendees().size();
                participantCount += conferenceContext.getMasterAttendees().size();
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    participantCount += conferenceContext.getCascadeAttendeesMap().get(deptId).size();
                }
                participantCountMap.put(mcuId, participantCount);
            }
            List<HwcloudBridge> fbs = new ArrayList<>(HwcloudBridgeCache.getInstance().getHwcloudBridgeMap().values());
            for (HwcloudBridge fmeBridge : fbs) {
                ModelBean mb = new ModelBean(fmeBridge.getBusiHwcloud());
                Long mcuId = fmeBridge.getBusiHwcloud().getId();
                McuType mcuType = McuType.MCU_HWCLOUD;
                String mcuIdStr = EncryptIdUtil.generateEncryptId(fmeBridge.getBusiHwcloud().getId(), mcuType.getCode());
                mb.put("mcuId", mcuIdStr);
                mb.put("mcuType", mcuType.getCode());
                mb.put("mcuTypeAlias", mcuType.getAlias());
                mb.remove("appKey");
                mb.put("bindDeptCount", DeptHwcloudMappingCache.getInstance().getBindDeptCount(FmeType.SINGLE_NODE, fmeBridge.getBusiHwcloud().getId()));


                // 连接失败原因
                mb.put("connectionFailedReason", fmeBridge.getConnectionFailedReason());
                mb.put("cropResource", fmeBridge.getShowCorpResourceResponse());
                // 参会者数
                Long participantCount = participantCountMap.get(mcuId);
                if (participantCount == null) {
                    participantCount = 0L;
                }
                mb.put("participantCount", participantCount);

                // 活跃会议数
                Long callCount = callCountMap.get(mcuId);
                if (callCount == null) {
                    callCount = 0L;
                }
                mb.put("callCount", callCount);

                // 会议号数
                Long coSpaceCount = coSpaceCountMap.get(mcuId);
                if (coSpaceCount == null) {
                    coSpaceCount = 0L;
                }
                mb.put("coSpaceCount", coSpaceCount);
                busiMcus.add(mb);
            }
        }

        return success(busiMcus);
    }


    /**
     * 查询ops资源列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询ops资源列表")
    public RestResponse list(BusiOpsResourceVo busiOpsResource) {
        startPage();
        List<BusiOpsResource> list = busiOpsResourceService.selectBusiOpsResourceList(busiOpsResource);
        PaginationData<BusiOpsResourceVo> pd = new PaginationData();
        pd.setTotal((new PageInfo(list)).getTotal());
        for (BusiOpsResource resource : list) {
            BusiOpsResourceVo busiOpsResourceVo = new BusiOpsResourceVo();
            BeanUtils.copyProperties(resource, busiOpsResourceVo);
            if (resource.getSn() != null) {
                BusiOps busiOps = OpsCache.getInstance().getBySn(resource.getSn());
                if (busiOps != null) {
                    busiOpsResourceVo.setOpsId(busiOps.getId());
                    if (StringUtils.isNotEmpty(busiOpsResource.getPurchaseType())) {
                        PurchaseType purchaseType = PurchaseType.convert(busiOpsResource.getPurchaseType());
                        if (purchaseType != null) {
                            busiOpsResourceVo.setPurchaseTypeName(purchaseType.getName());
                            busiOpsResourceVo.setPurchaseTypeAlias(purchaseType.getName());
                        }
                    }
                    if (StringUtils.isNotEmpty(busiOpsResource.getMcuType())) {
                        McuType mcuType = McuType.convert(busiOpsResource.getMcuType());
                        if (mcuType != null) {
                            busiOpsResourceVo.setMcuTypeName(mcuType.getName());
                            busiOpsResourceVo.setMcuTypeAlias(mcuType.getAlias());
                        }
                    }
                }
            }
            pd.addRecord(busiOpsResourceVo);
        }

        return RestResponse.success(0L, "查询成功", pd);
    }

    /**
     * 导出ops资源列表
     */
    @Log(title = "ops资源", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出ops资源列表")
    public RestResponse export(BusiOpsResourceVo busiOpsResource) {
        List<BusiOpsResource> list = busiOpsResourceService.selectBusiOpsResourceList(busiOpsResource);
        ExcelUtil<BusiOpsResource> util = new ExcelUtil<BusiOpsResource>(BusiOpsResource.class);
        return util.exportExcel(list, "resource");
    }

    /**
     * 获取ops资源详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取ops资源详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id) {
        return RestResponse.success(busiOpsResourceService.selectBusiOpsResourceById(id));
    }

    /**
     * 新增ops资源
     */
    @Log(title = "ops资源", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增ops资源")
    public RestResponse add(@RequestBody BusiOpsResourceVo busiOpsResource) {
        Assert.notNull(busiOpsResource.getOpsId(), "OPS ID不能为空！");
        int i = busiOpsResourceService.insertBusiOpsResource(busiOpsResource);
        if (i > 0) {
            if (StringUtils.isNotEmpty(busiOpsResource.getSn())) {
                BusiOps busiOps = OpsCache.getInstance().getBySn(busiOpsResource.getSn());
                if (busiOps != null) {
                    OpsPushRegisterTask opsPushRegisterTask = new OpsPushRegisterTask(String.valueOf(busiOps.getId()), 5000, busiOps.getId());
                    taskService.addTask(opsPushRegisterTask);
                }
            }
        }
        return toAjax(i);
    }

    /**
     * 修改ops资源
     */
    @PutMapping
    @Operation(summary = "修改ops资源")
    public RestResponse edit(@RequestBody BusiOpsResourceVo busiOpsResource) {
        int i = busiOpsResourceService.updateBusiOpsResource(busiOpsResource);
        if (i > 0) {
            if (StringUtils.isNotEmpty(busiOpsResource.getSn())) {
                BusiOps busiOps = OpsCache.getInstance().getBySn(busiOpsResource.getSn());
                if (busiOps != null) {
                    OpsPushRegisterTask opsPushRegisterTask = new OpsPushRegisterTask(String.valueOf(busiOps.getId()), 5000, busiOps.getId());
                    taskService.addTask(opsPushRegisterTask);
                }
            }
        }
        return toAjax(i);
    }

    /**
     * 删除ops资源
     */
    @Log(title = "ops资源", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除ops资源")
    public RestResponse remove(@PathVariable Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            BusiOpsResource busiOpsResource = busiOpsResourceService.selectBusiOpsResourceById(id);
            int i = busiOpsResourceService.deleteBusiOpsResourceById(id);
            if (i > 0) {
                rows++;
                if (busiOpsResource != null) {
                    if (StringUtils.isNotEmpty(busiOpsResource.getSn())) {
                        BusiOps busiOps = OpsCache.getInstance().getBySn(busiOpsResource.getSn());
                        if (busiOps != null) {
                            OpsPushRegisterTask opsPushRegisterTask = new OpsPushRegisterTask(String.valueOf(busiOps.getId()), 5000, busiOps.getId());
                            taskService.addTask(opsPushRegisterTask);
                        }
                    }
                }
            }
        }
        return toAjax(rows);
    }
}

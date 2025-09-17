package com.paradisecloud.fcm.web.controller.livebroadcast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.dao.model.BusiLiveBroadcast;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.vo.BusiLiveBroadcastVo;
import com.paradisecloud.fcm.web.service.interfaces.IBusiLiveBroadcastService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
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
import javax.servlet.http.HttpServletRequest;

/**
 * 直播记录Controller
 *
 * @author lilinhai
 * @date 2024-05-07
 */
@RestController
@RequestMapping("/busi/liveBroadcast")
@Tag(name = "直播记录")
public class BusiLiveBroadcastController extends BaseController
{
    @Resource
    private IBusiLiveBroadcastService busiLiveBroadcastService;

    /**
     * 获取部门直播计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门预约会议计数")
    public RestResponse getDeptRecordCounts()
    {
        List<DeptRecordCount> deptRecordCountList = busiLiveBroadcastService.getDeptRecordCounts();
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
     * 查询直播记录列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询直播记录列表")
    public RestResponse list(HttpServletRequest request, BusiLiveBroadcastVo busiLiveBroadcast)
    {
        startPage();
        List<BusiLiveBroadcast> list = busiLiveBroadcastService.selectBusiLiveBroadcastList(busiLiveBroadcast);
        String urlTemp = ExternalConfigCache.getInstance().getFmcRootUrl();
        String fmcRootUrlExternal = ExternalConfigCache.getInstance().getFmcRootUrlExternal();
        if (StringUtils.isNotEmpty(fmcRootUrlExternal)) {
            try {
                String referer = request.getHeader("referer");
                String ip = referer.replace("http://", "").replace("https://", "");
                if (ip.indexOf(":") > 0) {
                    ip.substring(0, ip.indexOf(":"));
                }
                if (ip.indexOf("/") > 0) {
                    ip = ip.substring(0, ip.indexOf("/"));
                }
                String externalIp = fmcRootUrlExternal.replace("http://", "").replace("https://", "");
                if (externalIp.indexOf(":") > 0) {
                    externalIp.substring(0, externalIp.indexOf(":"));
                }
                if (externalIp.indexOf("/") > 0) {
                    externalIp = externalIp.substring(0, externalIp.indexOf("/"));
                }
                if (externalIp.equals(ip)) {
                    urlTemp = fmcRootUrlExternal;
                }
            } catch (Exception e) {
            }
        }
        for (BusiLiveBroadcast busiLiveBroadcastTemp : list) {
            String introduce = busiLiveBroadcastTemp.getIntroduce();
            if (StringUtils.isNotEmpty(introduce)) {
                String url = urlTemp;
                introduce = introduce.replace("{url}", url);
                busiLiveBroadcastTemp.setIntroduce(introduce);
            }
        }
        return getDataTable(list);
    }

    /**
     * 获取直播记录详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取直播记录详细信息")
    public RestResponse getInfo(HttpServletRequest request, @PathVariable("id") Long id)
    {
        BusiLiveBroadcast busiLiveBroadcastTemp = busiLiveBroadcastService.selectBusiLiveBroadcastById(id);
        String urlTemp = ExternalConfigCache.getInstance().getFmcRootUrl();
        String fmcRootUrlExternal = ExternalConfigCache.getInstance().getFmcRootUrlExternal();
        if (StringUtils.isNotEmpty(fmcRootUrlExternal)) {
            try {
                String referer = request.getHeader("referer");
                String ip = referer.replace("http://", "").replace("https://", "");
                if (ip.indexOf(":") > 0) {
                    ip.substring(0, ip.indexOf(":"));
                }
                if (ip.indexOf("/") > 0) {
                    ip = ip.substring(0, ip.indexOf("/"));
                }
                String externalIp = fmcRootUrlExternal.replace("http://", "").replace("https://", "");
                if (externalIp.indexOf(":") > 0) {
                    externalIp.substring(0, externalIp.indexOf(":"));
                }
                if (externalIp.indexOf("/") > 0) {
                    externalIp = externalIp.substring(0, externalIp.indexOf("/"));
                }
                if (externalIp.equals(ip)) {
                    urlTemp = fmcRootUrlExternal;
                }
            } catch (Exception e) {
            }
        }
        String introduce = busiLiveBroadcastTemp.getIntroduce();
        if (StringUtils.isNotEmpty(introduce)) {
            String url = urlTemp;
            introduce = introduce.replace("{url}", url);
            busiLiveBroadcastTemp.setIntroduce(introduce);
        }
        return RestResponse.success(busiLiveBroadcastTemp);
    }

    /**
     * 新增直播记录
     */
    @PreAuthorize("@ss.hasPermi('busi:broadcast:add')")
    @Log(title = "直播记录", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增直播记录")
    public RestResponse add(@RequestBody BusiLiveBroadcast busiLiveBroadcast)
    {
        String introduce = busiLiveBroadcast.getIntroduce();
        if (StringUtils.isNotEmpty(introduce)) {
            if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getFmcRootUrl())) {
                introduce = introduce.replace(ExternalConfigCache.getInstance().getFmcRootUrl(), "{url}");
            }
            if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getFmcRootUrlExternal())) {
                introduce = introduce.replace(ExternalConfigCache.getInstance().getFmcRootUrlExternal(), "{url}");
            }
            busiLiveBroadcast.setIntroduce(introduce);
        }
        return toAjax(busiLiveBroadcastService.insertBusiLiveBroadcast(busiLiveBroadcast));
    }

    /**
     * 修改直播记录
     */
    @PreAuthorize("@ss.hasPermi('busi:broadcast:edit')")
    @Log(title = "直播记录", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改直播记录")
    public RestResponse edit(@RequestBody BusiLiveBroadcast busiLiveBroadcast)
    {
        String introduce = busiLiveBroadcast.getIntroduce();
        if (StringUtils.isNotEmpty(introduce)) {
            if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getFmcRootUrl())) {
                introduce = introduce.replace(ExternalConfigCache.getInstance().getFmcRootUrl(), "{url}");
            }
            if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getFmcRootUrlExternal())) {
                introduce = introduce.replace(ExternalConfigCache.getInstance().getFmcRootUrlExternal(), "{url}");
            }
            busiLiveBroadcast.setIntroduce(introduce);
        }
        return toAjax(busiLiveBroadcastService.updateBusiLiveBroadcast(busiLiveBroadcast));
    }

    /**
     * 删除直播记录
     */
    @PreAuthorize("@ss.hasPermi('busi:broadcast:remove')")
    @Log(title = "直播记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除直播记录")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiLiveBroadcastService.deleteBusiLiveBroadcastByIds(ids));
    }

    /**
     * 结束直播
     */
    @PreAuthorize("@ss.hasPermi('busi:broadcast:end')")
    @Log(title = "结束直播", businessType = BusinessType.INSERT)
    @PostMapping("/endLive/{id}")
    @Operation(summary = "结束直播")
    public RestResponse endLive(@PathVariable Long id)
    {
        return RestResponse.success(busiLiveBroadcastService.endLive(id, EndReasonsType.ADMINISTRATOR_HANGS_UP));
    }
}

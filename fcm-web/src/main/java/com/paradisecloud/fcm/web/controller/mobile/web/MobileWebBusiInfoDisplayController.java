package com.paradisecloud.fcm.web.controller.mobile.web;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.dao.model.BusiInfoDisplay;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.vo.BusiInfoDisplayVO;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiInfoDisplayService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 信息展示Controller
 * 
 * @author lilinhai
 * @date 2024-05-13
 */
@RestController
@RequestMapping("/mobileWeb/infodisplay")
@Tag(name = "信息展示")
public class MobileWebBusiInfoDisplayController extends BaseController
{
    @Resource
    private IBusiInfoDisplayService busiInfoDisplayService;

    /**
     * 获取部门直播计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门预约会议计数")
    public RestResponse getDeptRecordCounts()
    {
        List<DeptRecordCount> deptRecordCountList = busiInfoDisplayService.getDeptRecordCounts();
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
     * 查询信息展示列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询信息展示列表")
    public RestResponse list(HttpServletRequest request, BusiInfoDisplayVO busiInfoDisplay)
    {
        startPage();
        List<BusiInfoDisplay> list = busiInfoDisplayService.selectBusiInfoDisplayList(busiInfoDisplay);
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
                if (referer.indexOf(":8898") > -1) {
                    urlTemp = urlTemp.replace(":8899", ":8898");
                }
            } catch (Exception e) {
            }
        } else {
            try {
                String host = request.getHeader("Host");
                if (host.indexOf(":8898") > 0) {
                    urlTemp = urlTemp.replace(":8899", ":8898").replace("https://", "http://");
                }
            } catch (Exception e) {
            }
        }
        for (BusiInfoDisplay busiInfoDisplayTemp : list) {
            String introduce = busiInfoDisplayTemp.getUrlData();
            if (StringUtils.isNotEmpty(introduce)) {
                String url = urlTemp;
                introduce = introduce.replace("{url}", url);
                busiInfoDisplayTemp.setUrlData(introduce);
            }
        }

        return getDataTable(list);
    }

    /**
     * 获取信息展示详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取信息展示详细信息")
    public RestResponse getInfo(HttpServletRequest request, @PathVariable("id") Long id) {
        BusiInfoDisplay busiInfoDisplay = busiInfoDisplayService.selectBusiInfoDisplayById(id);
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
                if (referer.indexOf(":8898") > -1) {
                    urlTemp = urlTemp.replace(":8899", ":8898");
                }
            } catch (Exception e) {
            }
        } else {
            try {
                String host = request.getHeader("Host");
                if (host.indexOf(":8898") > 0) {
                    urlTemp = urlTemp.replace(":8899", ":8898").replace("https://", "http://");
                }
            } catch (Exception e) {
            }
        }

        String introduce = busiInfoDisplay.getUrlData();
        if (StringUtils.isNotEmpty(introduce)) {
            String url = urlTemp;
            introduce = introduce.replace("{url}", url);
            busiInfoDisplay.setUrlData(introduce);
        }

        return RestResponse.success(busiInfoDisplay);
    }

    /**
     * 新增信息展示
     */
    @PreAuthorize("@ss.hasPermi('busi:infodisplay:add')")
    @Log(title = "信息展示", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增信息展示")
    public RestResponse add(HttpServletRequest request, @RequestBody BusiInfoDisplay busiInfoDisplay)
    {
        String urlData = busiInfoDisplay.getUrlData();
        if (StringUtils.isNotEmpty(urlData)) {
            if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getFmcRootUrl())) {
                urlData = urlData.replace(ExternalConfigCache.getInstance().getFmcRootUrl(), "{url}");
            }
            if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getFmcRootUrlExternal())) {
                urlData = urlData.replace(ExternalConfigCache.getInstance().getFmcRootUrlExternal(), "{url}");
            }
            try {
                String host = request.getHeader("Host");
                if (host.indexOf(":8898") > 0) {
                    if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getFmcRootUrl())) {
                        String fmcRootUrl = ExternalConfigCache.getInstance().getFmcRootUrl();
                        fmcRootUrl = fmcRootUrl.replace(":8899", ":8898").replace("https://", "http://");
                        urlData = urlData.replace(fmcRootUrl, "{url}");
                    }
                }
            } catch (Exception e) {
            }
            busiInfoDisplay.setUrlData(urlData);
        }
        if (busiInfoDisplay.getDurationTime() == null) {
            busiInfoDisplay.setDurationTime(0);
        }
        return toAjax(busiInfoDisplayService.insertBusiInfoDisplay(busiInfoDisplay));
    }

    /**
     * 修改信息展示
     */
    @PreAuthorize("@ss.hasPermi('busi:infodisplay:edit')")
    @Log(title = "信息展示", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改信息展示")
    public RestResponse edit(HttpServletRequest request, @RequestBody BusiInfoDisplay busiInfoDisplay)
    {
        String urlData = busiInfoDisplay.getUrlData();
        if (StringUtils.isNotEmpty(urlData)) {
            if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getFmcRootUrl())) {
                urlData = urlData.replace(ExternalConfigCache.getInstance().getFmcRootUrl(), "{url}");
            }
            if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getFmcRootUrlExternal())) {
                urlData = urlData.replace(ExternalConfigCache.getInstance().getFmcRootUrlExternal(), "{url}");
            }
            busiInfoDisplay.setUrlData(urlData);
        }
        if (busiInfoDisplay.getDurationTime() == null) {
            busiInfoDisplay.setDurationTime(0);
        }
        return toAjax(busiInfoDisplayService.updateBusiInfoDisplay(busiInfoDisplay));
    }

    /**
     * 删除信息展示
     */
    @PreAuthorize("@ss.hasPermi('busi:infodisplay:remove')")
    @Log(title = "信息展示", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除信息展示")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiInfoDisplayService.deleteBusiInfoDisplayByIds(ids));
    }

    /**
     * 推送信息展示
     */
    @PreAuthorize("@ss.hasPermi('busi:infodisplay:push')")
    @Log(title = "信息展示", businessType = BusinessType.INSERT)
    @PostMapping("/push/{id}")
    @Operation(summary = "推送信息展示")
    public RestResponse push(@PathVariable Long id)
    {
        return toAjax(busiInfoDisplayService.push(id));
    }

    /**
     * 修改状态
     */
    @PreAuthorize("@ss.hasPermi('busi:infodisplay:edit')")
    @Log(title = "信息展示", businessType = BusinessType.INSERT)
    @PostMapping("/status")
    @Operation(summary = "修改状态")
    public RestResponse status(@RequestBody BusiInfoDisplay busiInfoDisplay)
    {
        return toAjax(busiInfoDisplayService.status(busiInfoDisplay));
    }
}

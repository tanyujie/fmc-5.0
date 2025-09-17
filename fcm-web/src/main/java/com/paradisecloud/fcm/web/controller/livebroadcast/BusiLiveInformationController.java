package com.paradisecloud.fcm.web.controller.livebroadcast;

import java.util.List;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.dao.model.BusiInfoDisplay;
import com.paradisecloud.fcm.dao.model.BusiLiveInformation;
import com.paradisecloud.fcm.web.service.interfaces.IBusiLiveInformationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.servlet.http.HttpServletRequest;

/**
 * 直播资料Controller
 *
 * @author lilinhai
 * @date 2024-05-07
 */
@RestController
@RequestMapping("/busi/information")
@Tag(name = "直播资料")
public class BusiLiveInformationController extends BaseController
{
    @Autowired
    private IBusiLiveInformationService busiLiveInformationService;

    /**
     * 查询直播资料列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询直播资料列表")
    public RestResponse list(HttpServletRequest request, BusiLiveInformation busiLiveInformation)
    {
        startPage();
        List<BusiLiveInformation> list = busiLiveInformationService.selectBusiLiveInformationList(busiLiveInformation);

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
        for (BusiLiveInformation busiInfoDisplayTemp : list) {
            String introduce = busiInfoDisplayTemp.getInformationUrl();
            if (StringUtils.isNotEmpty(introduce)) {
                String url = urlTemp;
                introduce = introduce.replace("{url}", url);
                busiInfoDisplayTemp.setInformationUrl(introduce);
            }
        }

        return getDataTable(list);
    }

    /**
     * 获取直播资料详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取直播资料详细信息")
    public RestResponse getInfo(HttpServletRequest request, @PathVariable("id") Long id)
    {
        BusiLiveInformation busiLiveInformation = busiLiveInformationService.selectBusiLiveInformationById(id);
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
        String introduce = busiLiveInformation.getInformationUrl();
        if (StringUtils.isNotEmpty(introduce)) {
            String url = urlTemp;
            introduce = introduce.replace("{url}", url);
            busiLiveInformation.setInformationUrl(introduce);
        }
        return RestResponse.success(busiLiveInformation);
    }

    /**
     * 新增直播资料
     */
    @PreAuthorize("@ss.hasPermi('busi:information:add')")
    @Log(title = "直播资料", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增直播资料")
    public RestResponse add(@RequestBody BusiLiveInformation busiLiveInformation)
    {
        String urlData = busiLiveInformation.getInformationUrl();
        if (StringUtils.isNotEmpty(urlData)) {
            if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getFmcRootUrl())) {
                urlData = urlData.replace(ExternalConfigCache.getInstance().getFmcRootUrl(), "{url}");
            }
            if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getFmcRootUrlExternal())) {
                urlData.replace(ExternalConfigCache.getInstance().getFmcRootUrlExternal(), "{url}");
            }
            busiLiveInformation.setInformationUrl(urlData);
        }
        return toAjax(busiLiveInformationService.insertBusiLiveInformation(busiLiveInformation));
    }

    /**
     * 修改直播资料
     */
    @PreAuthorize("@ss.hasPermi('busi:information:edit')")
    @Log(title = "直播资料", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改直播资料")
    public RestResponse edit(@RequestBody BusiLiveInformation busiLiveInformation)
    {
        String urlData = busiLiveInformation.getInformationUrl();
        if (StringUtils.isNotEmpty(urlData)) {
            if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getFmcRootUrl())) {
                urlData = urlData.replace(ExternalConfigCache.getInstance().getFmcRootUrl(), "{url}");
            }
            if (StringUtils.isNotEmpty(ExternalConfigCache.getInstance().getFmcRootUrlExternal())) {
                urlData.replace(ExternalConfigCache.getInstance().getFmcRootUrlExternal(), "{url}");
            }
            busiLiveInformation.setInformationUrl(urlData);
        }
        return toAjax(busiLiveInformationService.updateBusiLiveInformation(busiLiveInformation));
    }

    /**
     * 删除直播资料
     */
    @PreAuthorize("@ss.hasPermi('busi:information:remove')")
    @Log(title = "直播资料", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除直播资料")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiLiveInformationService.deleteBusiLiveInformationByIds(ids));
    }
}

package com.paradisecloud.fcm.web.controller.live;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.dao.model.BusiLive;
import com.paradisecloud.fcm.service.interfaces.IBusiLiveService;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.paradisecloud.system.model.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播服务器信息Controller
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
@RestController
@RequestMapping("/busi/live")
@Tag(name = "直播服务器信息")
public class BusiLiveController extends BaseController
{
    @Autowired
    private IBusiLiveService busiLiveService;

    /**
     * 查询直播服务器信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询直播服务器信息列表")
    public RestResponse list(BusiLive busiLive)
    {
        startPage();
        List<BusiLive> list = busiLiveService.selectBusiLiveList(busiLive);
        List<ModelBean> busiLives = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (BusiLive live : list) {
                ModelBean mb = new ModelBean();
                mb.put("bindDeptCount", LiveDeptCache.getInstance().getBindDeptCount(FcmType.SINGLE_NODE, live.getId()));
                mb.put("createTime", live.getCreateTime());
                mb.put("updateTime", live.getUpdateTime());
                mb.put("id", live.getId());
                mb.put("name", live.getName());
                mb.put("ip", live.getIp());
                mb.put("status", live.getStatus());
                mb.put("uriPath", live.getUriPath());
                mb.put("protocolType", live.getProtocolType());
                mb.put("port", live.getPort());
                mb.put("domainName", live.getDomainName());
                busiLives.add(mb);
            }
        }
        return getDataTable(busiLives);
    }

    /**
     * 导出直播服务器信息列表
     */
    @Log(title = "直播服务器信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出直播服务器信息列表")
    public RestResponse export(BusiLive busiLive)
    {
        List<BusiLive> list = busiLiveService.selectBusiLiveList(busiLive);
        ExcelUtil<BusiLive> util = new ExcelUtil<BusiLive>(BusiLive.class);
        return util.exportExcel(list, "live");
    }

    /**
     * 获取直播服务器信息详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取直播服务器信息详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiLiveService.selectBusiLiveById(id));
    }

    /**
     * 新增直播服务器信息
     */
    @Log(title = "直播服务器信息", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增直播服务器信息")
    public RestResponse add(@RequestBody BusiLive busiLive)
    {
        return toAjax(busiLiveService.insertBusiLive(busiLive));
    }

    /**
     * 修改直播服务器信息
     */
    @Log(title = "直播服务器信息", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改直播服务器信息")
    public RestResponse edit(@RequestBody BusiLive busiLive)
    {
        return toAjax(busiLiveService.updateBusiLive(busiLive));
    }

    /**
     * 删除直播服务器信息
     */
    @Log(title = "直播服务器信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除直播服务器信息")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiLiveService.deleteBusiLiveByIds(ids));
    }
}

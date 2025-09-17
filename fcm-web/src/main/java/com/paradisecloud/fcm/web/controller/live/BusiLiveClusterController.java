package com.paradisecloud.fcm.web.controller.live;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.dao.model.BusiLiveCluster;
import com.paradisecloud.fcm.service.interfaces.IBusiLiveClusterService;
import com.paradisecloud.fcm.terminal.fs.cache.LiveClusterCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.paradisecloud.system.model.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播服务器集群Controller
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
@RestController
@RequestMapping("/busi/live/cluster")
@Tag(name = "直播服务器集群")
public class BusiLiveClusterController extends BaseController
{
    @Autowired
    private IBusiLiveClusterService busiLiveClusterService;

    /**
     * 查询直播服务器集群列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询直播服务器集群列表")
    public RestResponse list(BusiLiveCluster busiLiveCluster)
    {
        startPage();
//        List<BusiLiveCluster> list = busiLiveClusterService.selectBusiLiveClusterList(busiLiveCluster);

        List<BusiLiveCluster> gs = new ArrayList<>(LiveClusterCache.getInstance().values());
        List<ModelBean> ms = new ArrayList<>();
        for (BusiLiveCluster busiLiveCluster1 : gs)
        {
            ModelBean m = new ModelBean(busiLiveCluster1);
            m.put("bindDeptCount", LiveDeptCache.getInstance().getBindDeptCount(FcmType.CLUSTER, busiLiveCluster1.getId()));
            ms.add(m);
        }

        return getDataTable(ms);
    }

    /**
     * 导出直播服务器集群列表
     */
    @Log(title = "直播服务器集群", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出直播服务器集群列表")
    public RestResponse export(BusiLiveCluster busiLiveCluster)
    {
        List<BusiLiveCluster> list = busiLiveClusterService.selectBusiLiveClusterList(busiLiveCluster);
        ExcelUtil<BusiLiveCluster> util = new ExcelUtil<BusiLiveCluster>(BusiLiveCluster.class);
        return util.exportExcel(list, "cluster");
    }

    /**
     * 获取直播服务器集群详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取直播服务器集群详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiLiveClusterService.selectBusiLiveClusterById(id));
    }

    /**
     * 新增直播服务器集群
     */
    @Log(title = "直播服务器集群", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增直播服务器集群")
    public RestResponse add(@RequestBody BusiLiveCluster busiLiveCluster)
    {
        return toAjax(busiLiveClusterService.insertBusiLiveCluster(busiLiveCluster));
    }

    /**
     * 修改直播服务器集群
     */
    @Log(title = "直播服务器集群", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改直播服务器集群")
    public RestResponse edit(@RequestBody BusiLiveCluster busiLiveCluster)
    {
        return toAjax(busiLiveClusterService.updateBusiLiveCluster(busiLiveCluster));
    }

    /**
     * 删除直播服务器集群
     */
    @Log(title = "直播服务器集群", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除直播服务器集群")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiLiveClusterService.deleteBusiLiveClusterByIds(ids));
    }
}

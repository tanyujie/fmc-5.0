package com.paradisecloud.fcm.web.controller.live;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiLiveClusterMap;
import com.paradisecloud.fcm.service.interfaces.IBusiLiveClusterMapService;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.system.model.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播服务器-直播集群组中间（多对多）Controller
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
@RestController
@RequestMapping("/busi/live/map")
@Tag(name = "直播服务器-直播集群组中间（多对多）")
public class BusiLiveClusterMapController extends BaseController
{
    @Autowired
    private IBusiLiveClusterMapService busiLiveClusterMapService;

    /**
     * 查询直播服务器-直播集群组中间（多对多）列表
     */
    @GetMapping("/getAllLiveCluster")
    @Operation(summary = "查询直播服务器-直播集群组中间（多对多）列表")
    public RestResponse list(BusiLiveClusterMap busiLiveClusterMap) {
        startPage();
//        List<BusiLiveClusterMap> list1 = busiLiveClusterMapService.selectBusiLiveClusterMapList(busiLiveClusterMap);

        List<ModelBean> mbs = new ArrayList<>();
        List<BusiLiveClusterMap> list = busiLiveClusterMapService.selectBusiLiveClusterMapList(busiLiveClusterMap);
        for (int i = 0; i < list.size(); i++) {
            {
                ModelBean mb = new ModelBean(list.get(i));
                ModelBean mb0 = new ModelBean(LiveBridgeCache.getInstance().get(list.get(i).getLiveId()).getBusiLive());
                mb0.put("status", LiveBridgeCache.getInstance().get(list.get(i).getLiveId()).getBusiLive().getStatus());
                mb0.remove("id");
                mb0.remove("createTime");
                mb0.remove("updateTime");
                mb0.remove("password");
                mb0.remove("username");
                mb.putAll(mb0);
                mbs.add(mb);
            }
//        return getDataTable(list);
        }
        return success(mbs);
    }

    /**
     * 导出直播服务器-直播集群组中间（多对多）列表
     */
    @Log(title = "直播服务器-直播集群组中间（多对多）", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出直播服务器-直播集群组中间（多对多）列表")
    public RestResponse export(BusiLiveClusterMap busiLiveClusterMap)
    {
        List<BusiLiveClusterMap> list = busiLiveClusterMapService.selectBusiLiveClusterMapList(busiLiveClusterMap);
        ExcelUtil<BusiLiveClusterMap> util = new ExcelUtil<BusiLiveClusterMap>(BusiLiveClusterMap.class);
        return util.exportExcel(list, "map");
    }

    /**
     * 获取直播服务器-直播集群组中间（多对多）详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取直播服务器-直播集群组中间（多对多）详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiLiveClusterMapService.selectBusiLiveClusterMapById(id));
    }

    /**
     * 新增直播服务器-直播集群组中间（多对多）
     */
    @Log(title = "直播服务器-直播集群组中间（多对多）", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增直播服务器-直播集群组中间（多对多）")
    public RestResponse add(@RequestBody BusiLiveClusterMap busiLiveClusterMap)
    {
        return toAjax(busiLiveClusterMapService.insertBusiLiveClusterMap(busiLiveClusterMap));
    }

    /**
     * 修改直播服务器-直播集群组中间（多对多）
     */
    @Log(title = "直播服务器-直播集群组中间（多对多）", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改直播服务器-直播集群组中间（多对多）")
    public RestResponse edit(@RequestBody BusiLiveClusterMap busiLiveClusterMap)
    {
        return toAjax(busiLiveClusterMapService.updateBusiLiveClusterMap(busiLiveClusterMap));
    }

    /**
     * 删除直播服务器-直播集群组中间（多对多）
     */
    @Log(title = "直播服务器-直播集群组中间（多对多）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除直播服务器-直播集群组中间（多对多）")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiLiveClusterMapService.deleteBusiLiveClusterMapByIds(ids));
    }
}

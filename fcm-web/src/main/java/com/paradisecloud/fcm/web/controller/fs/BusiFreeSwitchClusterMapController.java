package com.paradisecloud.fcm.web.controller.fs;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchClusterMap;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.interfaces.IBusiFreeSwitchClusterMapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * FS-终端组中间（多对多）Controller
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
@RestController
@RequestMapping("/freeSwitch/fsClusterMap")
@Tag(name = "FS-集群映射控制层")
public class BusiFreeSwitchClusterMapController extends BaseController
{
    @Resource
    private IBusiFreeSwitchClusterMapService busiFreeSwitchClusterMapService;

    /**
     * 查询FS-终端组中间（多对多）列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询FS-终端组中间（多对多）列表")
    public RestResponse list(BusiFreeSwitchClusterMap busiFreeSwitchClusterMap)
    {
        List<ModelBean> mbs = new ArrayList<>();
        List<BusiFreeSwitchClusterMap> list = busiFreeSwitchClusterMapService.selectBusiFreeSwitchClusterMapList(busiFreeSwitchClusterMap);
        for (BusiFreeSwitchClusterMap busiFreeSwitchClusterMap2 : list)
        {
            ModelBean mb = new ModelBean(busiFreeSwitchClusterMap2);
            ModelBean mb0 = new ModelBean(FcmBridgeCache.getInstance().get(busiFreeSwitchClusterMap2.getFreeSwitchId()).getBusiFreeSwitch());
            int status = FcmBridgeCache.getInstance().get(busiFreeSwitchClusterMap2.getFreeSwitchId()).isAvailable() ? 1 : 2;
            mb0.put("status", status);
            mb0.remove("id");
            mb0.remove("createTime");
            mb0.remove("updateTime");
            mb0.remove("password");
            mb0.remove("username");
            mb.putAll(mb0);
            mbs.add(mb);
        }
        return success(mbs);
    }

    /**
     * 获取FS-终端组中间（多对多）详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取FS-终端组中间（多对多）详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiFreeSwitchClusterMapService.selectBusiFreeSwitchClusterMapById(id));
    }

    /**
     * 新增FS-终端组中间（多对多）
     */
    @Log(title = "FS-终端组中间（多对多）", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增FS-终端组中间（多对多）")
    public RestResponse add(@RequestBody BusiFreeSwitchClusterMap busiFreeSwitchClusterMap)
    {
        return toAjax(busiFreeSwitchClusterMapService.insertBusiFreeSwitchClusterMap(busiFreeSwitchClusterMap));
    }
    
    /**
     * 新增FS-终端组中间（多对多）
     */
    @Log(title = "FreeSwitch-终端组中间（多对多）", businessType = BusinessType.INSERT)
    @PutMapping("/{id}")
    @Operation(summary = "修改FS-终端组中间（多对多）")
    public RestResponse update(@RequestBody BusiFreeSwitchClusterMap busiFreeSwitchClusterMap, @PathVariable Long id)
    {
        busiFreeSwitchClusterMap.setId(id);
        return toAjax(busiFreeSwitchClusterMapService.updateBusiFreeSwitchClusterMap(busiFreeSwitchClusterMap));
    }

    /**
     * 删除FS-终端组中间（多对多）
     */
    @Log(title = "FS-终端组中间（多对多）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
	@Operation(summary = "删除FreeSwitch-终端组中间（多对多）")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiFreeSwitchClusterMapService.deleteBusiFreeSwitchClusterMapById(id));
    }
}

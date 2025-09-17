package com.paradisecloud.fcm.web.controller.fme;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.paradisecloud.fcm.dao.model.BusiFmeClusterMap;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.service.interfaces.IBusiFmeClusterMapService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * FME-终端组中间（多对多）Controller
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
@RestController
@RequestMapping("/busi/fmeClusterMap")
@Tag(name = "FME-集群映射控制层")
public class BusiFmeClusterMapController extends BaseController
{
    @Autowired
    private IBusiFmeClusterMapService busiFmeClusterMapService;

    /**
     * 查询FME-终端组中间（多对多）列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询FME-终端组中间（多对多）列表")
    public RestResponse list(BusiFmeClusterMap busiFmeClusterMap)
    {
        List<ModelBean> mbs = new ArrayList<>();
        List<BusiFmeClusterMap> list = busiFmeClusterMapService.selectBusiFmeClusterMapList(busiFmeClusterMap);
        for (BusiFmeClusterMap busiFmeClusterMap2 : list)
        {
            ModelBean mb = new ModelBean(busiFmeClusterMap2);
            ModelBean mb0 = new ModelBean(FmeBridgeCache.getInstance().get(busiFmeClusterMap2.getFmeId()).getBusiFme());
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
     * 获取FME-终端组中间（多对多）详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取FME-终端组中间（多对多）详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiFmeClusterMapService.selectBusiFmeClusterMapById(id));
    }

    /**
     * 新增FME-终端组中间（多对多）
     */
    @Log(title = "FME-终端组中间（多对多）", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增FME-终端组中间（多对多）")
    public RestResponse add(@RequestBody BusiFmeClusterMap busiFmeClusterMap)
    {
        return toAjax(busiFmeClusterMapService.insertBusiFmeClusterMap(busiFmeClusterMap));
    }
    
    /**
     * 新增FME-终端组中间（多对多）
     */
    @Log(title = "FME-终端组中间（多对多）", businessType = BusinessType.INSERT)
    @PutMapping("/{id}")
    @Operation(summary = "修改FME-终端组中间（多对多）")
    public RestResponse update(@RequestBody BusiFmeClusterMap busiFmeClusterMap, @PathVariable Long id)
    {
        busiFmeClusterMap.setId(id);
        return toAjax(busiFmeClusterMapService.updateBusiFmeClusterMap(busiFmeClusterMap));
    }

    /**
     * 删除FME-终端组中间（多对多）
     */
    @Log(title = "FME-终端组中间（多对多）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
	@Operation(summary = "删除FME-终端组中间（多对多）")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiFmeClusterMapService.deleteBusiFmeClusterMapById(id));
    }
}

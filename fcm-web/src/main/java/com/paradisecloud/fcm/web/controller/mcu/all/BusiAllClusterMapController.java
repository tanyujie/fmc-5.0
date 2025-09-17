package com.paradisecloud.fcm.web.controller.mcu.all;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.EncryptIdVo;
import com.paradisecloud.fcm.dao.model.BusiFmeClusterMap;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.service.interfaces.IBusiFmeClusterMapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * MCU-终端组中间（多对多）Controller
 * 
 * @author lilinhai
 * @date 2021-03-17
 */
@RestController
@RequestMapping("/busi/mcu/all/clusterMap")
@Tag(name = "FME-集群映射控制层")
public class BusiAllClusterMapController extends BaseController
{
    @Resource
    private IBusiFmeClusterMapService busiFmeClusterMapService;

    /**
     * 查询MCU-终端组中间（多对多）列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询MCU-终端组中间（多对多）列表")
    public RestResponse list(String mcuClusterId)
    {
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuClusterId);
        Long clusterId = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                BusiFmeClusterMap busiFmeClusterMap = new BusiFmeClusterMap();
                busiFmeClusterMap.setClusterId(clusterId);
                List<ModelBean> mbs = new ArrayList<>();
                List<BusiFmeClusterMap> list = busiFmeClusterMapService.selectBusiFmeClusterMapList(busiFmeClusterMap);
                for (BusiFmeClusterMap busiFmeClusterMap2 : list)
                {
                    ModelBean mb = new ModelBean(busiFmeClusterMap2);
                    Long id = (Long) mb.get("id");
                    String mcuClusterMapId = EncryptIdUtil.generateEncryptId(id, McuType.FME.getCode());
                    mb.put("mcuClusterMapId", mcuClusterMapId);
                    mb.put("mcuClusterId", mcuClusterId);
                    mb.put("mcuId", busiFmeClusterMap2.getFmeId());
                    mb.remove("fmeId");
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
        }
        return RestResponse.fail();
    }

    /**
     * 获取MCU-终端组中间（多对多）详细信息
     */
    @GetMapping(value = "/{mcuClusterMapId}")
    @Operation(summary = "获取MCU-终端组中间（多对多）详细信息")
    public RestResponse getInfo(@PathVariable("mcuClusterMapId") String mcuClusterMapId)
    {
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuClusterMapId);
        Long clusterId = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                return RestResponse.success(busiFmeClusterMapService.selectBusiFmeClusterMapById(clusterId));
            }
        }
        return RestResponse.fail();
    }

    /**
     * 新增MCU-终端组中间（多对多）
     */
    @Log(title = "MCU-终端组中间（多对多）", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增MCU-终端组中间（多对多）", description = "添加集群")
    public RestResponse add(@RequestBody JSONObject jsonObject)
    {
        String mcuClusterId = jsonObject.getString("mcuClusterId");
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuClusterId);
        Long clusterId = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        String mcuIdStr = jsonObject.getString("mcuId");
        EncryptIdVo encryptIdVoMcu = EncryptIdUtil.parasEncryptId(mcuIdStr);
        Long mcuId = encryptIdVoMcu.getId();
        switch (mcuType) {
            case FME: {
                BusiFmeClusterMap busiFmeClusterMap = jsonObject.toJavaObject(BusiFmeClusterMap.class);
                busiFmeClusterMap.setClusterId(clusterId);
                busiFmeClusterMap.setFmeId(mcuId);
                return toAjax(busiFmeClusterMapService.insertBusiFmeClusterMap(busiFmeClusterMap));
            }
        }
        if (mcuType != McuType.FME) {
            return RestResponse.fail("该MCU不支持集群！");
        }
        return RestResponse.fail();
    }
    
    /**
     * 更新MCU-终端组中间（多对多）
     */
    @Log(title = "MCU-终端组中间（多对多）", businessType = BusinessType.INSERT)
    @PutMapping("/{mcuClusterMapId}")
    @Operation(summary = "修改MCU-终端组中间（多对多）", description = "修改集群")
    public RestResponse update(@RequestBody JSONObject jsonObject, @PathVariable("mcuClusterMapId") String mcuClusterMapId)
    {
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuClusterMapId);
        Long clusterMapId = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        Long mcuId = jsonObject.getLong("mcuId");
        switch (mcuType) {
            case FME: {
                BusiFmeClusterMap busiFmeClusterMap = jsonObject.toJavaObject(BusiFmeClusterMap.class);
                busiFmeClusterMap.setId(clusterMapId);
                busiFmeClusterMap.setFmeId(mcuId);
                return toAjax(busiFmeClusterMapService.updateBusiFmeClusterMap(busiFmeClusterMap));
            }
        }
        return RestResponse.fail();
    }

    /**
     * 删除MCU-终端组中间（多对多）
     */
    @Log(title = "MCU-终端组中间（多对多）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{mcuClusterMapId}")
	@Operation(summary = "删除MCU-终端组中间（多对多）", description = "清除集群")
    public RestResponse remove(@PathVariable("mcuClusterMapId") String mcuClusterMapId)
    {
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuClusterMapId);
        Long clusterMapId = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                return toAjax(busiFmeClusterMapService.deleteBusiFmeClusterMapById(clusterMapId));
            }
        }
        return RestResponse.fail();
    }
}

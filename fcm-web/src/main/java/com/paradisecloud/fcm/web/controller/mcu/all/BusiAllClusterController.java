package com.paradisecloud.fcm.web.controller.mcu.all;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.EncryptIdVo;
import com.paradisecloud.fcm.dao.model.BusiFme;
import com.paradisecloud.fcm.dao.model.BusiFmeCluster;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.FmeBridgeCluster;
import com.paradisecloud.fcm.fme.websocket.interfaces.IFmeCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * MCU终端组Controller
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/mcu/all/cluster")
@Tag(name = "MCU集群控制层")
public class BusiAllClusterController extends BaseController
{
    
    @Resource
    private IFmeCacheService fmeCacheService;
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键ID删除一个实体</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键ID删除单个记录：id放到rest地址上占位符处", description = "删除集群")
    @DeleteMapping("/{mcuClusterId}")
    public RestResponse deleteMcuGroup(@PathVariable String mcuClusterId)
    {
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuClusterId);
        Long clusterId = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                fmeCacheService.deleteBusiFmeCluster(clusterId);
                break;
            }
        }
        return success("Delete Entity successfully, id: " + clusterId);
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键修改实体属性</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键修改单个会议桥组记录：id放到rest地址上占位符处，修改的字段和字段值放到请求body中封装为json格式", description = "修改集群")
    @PutMapping("/{mcuClusterId}")
    public RestResponse update(@RequestBody JSONObject jsonObject, @PathVariable String mcuClusterId)
    {
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuClusterId);
        Long clusterId = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                BusiFmeCluster busiFmeCluster = jsonObject.toJavaObject(BusiFmeCluster.class);
                busiFmeCluster.setId(clusterId);
                fmeCacheService.updateBusiFmeCluster(busiFmeCluster);
                break;
            }
        }
        return success(jsonObject);
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">创建会议桥组[bridgeHostGroup]</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "单个会议桥组记录新增：记录的属性和属性值放到请求body中封装为json格式", description = "创建集群")
    @PostMapping("")
    public RestResponse save(@RequestBody JSONObject jsonObject)
    {
        String mcuTypeStr = jsonObject.getString("mcuType");
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                BusiFmeCluster busiFmeCluster = jsonObject.toJavaObject(BusiFmeCluster.class);
                fmeCacheService.addBusiFmeCluster(busiFmeCluster);
                break;
            }
        }
        if (mcuType != McuType.FME) {
            return RestResponse.fail("该MCU不支持集群！");
        }
        return success(jsonObject);
    }
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键查找单个记录</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据MCU集群ID获取集群下所有会议桥")
    @GetMapping("/getMcusByCluster/{mcuClusterId}")
    public RestResponse getAllMcuByGroupId(@PathVariable String mcuClusterId)
    {
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuClusterId);
        Long clusterId = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                FmeBridgeCluster fmeHttpInvokers = FmeBridgeCache.getInstance().getByFmeClusterId(clusterId);
                if (fmeHttpInvokers != null) {
                    List<BusiFme> bhs = new ArrayList<>();
                    for (Iterator<FmeBridge> iterator = fmeHttpInvokers.getFmeBridges().iterator(); iterator.hasNext(); ) {
                        FmeBridge fmeHttpInvoker = (FmeBridge) iterator.next();
                        bhs.add(fmeHttpInvoker.getBusiFme());
                    }
                    return success(bhs);
                }
            }
        }
        return success(new ArrayList<>());
    }
    
    /**
     * <pre>获取当前部门下所有主用MCU组</pre>
     * @author lilinhai
     * @since 2021-01-21 15:54 
     * @return List<BusiFmeGroup>
     */
    @Operation(summary = "获取所有FME集群")
    @GetMapping("/getAllMcuCluster")
    public RestResponse getAllMcuCluster()
    {
        List<ModelBean> list = new ArrayList<>();
        //fme
        {
            McuType mcuType = McuType.FME;
            List<ModelBean> gs = fmeCacheService.getAllBusiFmeCluster();
            for (ModelBean modelBean : gs) {
                Long id = (Long) modelBean.get("id");
                String mcuClusterId = EncryptIdUtil.generateEncryptId(id, McuType.FME.getCode());
                modelBean.put("mcuClusterId", mcuClusterId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
            }
            list.addAll(gs);
        }
        return success(list);
    }
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键查找单个记录</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键查询单个MCU集群信息")
    @GetMapping("/{mcuClusterId}")
    public RestResponse getMcuClusterById(@PathVariable String mcuClusterId)
    {
        EncryptIdVo encryptIdVo = EncryptIdUtil.parasEncryptId(mcuClusterId);
        Long clusterId = encryptIdVo.getId();
        McuType mcuType = encryptIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                BusiFmeCluster busiFmeCluster = fmeCacheService.getFmeClusterById(clusterId);
                ModelBean modelBean = new ModelBean(busiFmeCluster);
                modelBean.put("mcuClusterId", mcuClusterId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                return success(modelBean);
            }
        }
        return RestResponse.fail();
    }
}

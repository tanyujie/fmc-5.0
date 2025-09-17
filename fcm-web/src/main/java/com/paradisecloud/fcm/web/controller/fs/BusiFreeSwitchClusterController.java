package com.paradisecloud.fcm.web.controller.fs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.interfaces.IBusiFreeSwitchClusterService;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchCluster;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Resource;

/**
 * FreeSwitch终端组Controller
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/freeSwitch/fsCluster")
@Tag(name = "FreeSwitch集群控制层")
public class BusiFreeSwitchClusterController extends BaseController
{
    @Resource
    private IBusiFreeSwitchClusterService busiFreeSwitchClusterService;

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键ID删除一个实体</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键ID删除单个记录：id放到rest地址上占位符处")
    @DeleteMapping("/{id}")
    public RestResponse deleteBusiFreeSwitchGroup(@PathVariable Integer id)
    {
        busiFreeSwitchClusterService.deleteBusiFreeSwitchCluster(id);
        return success("Delete Entity successfully, id: " + id);
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键修改实体属性</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键修改单个会议桥组记录：id放到rest地址上占位符处，修改的字段和字段值放到请求body中封装为json格式")
    @PutMapping("/{id}")
    public RestResponse update(@RequestBody BusiFreeSwitchCluster busiFreeSwitchCluster, @PathVariable Long id)
    {
        busiFreeSwitchCluster.setId(id);
        busiFreeSwitchClusterService.updateBusiFreeSwitchCluster(busiFreeSwitchCluster);
        return success(busiFreeSwitchCluster);
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">创建会议桥组[bridgeHostGroup]</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "单个会议桥组记录新增：记录的属性和属性值放到请求body中封装为json格式")
    @PostMapping("")
    public RestResponse save(@RequestBody BusiFreeSwitchCluster busiFreeSwitchCluster)
    {
        busiFreeSwitchClusterService.addBusiFreeSwitchCluster(busiFreeSwitchCluster);
        return success(busiFreeSwitchCluster);
    }
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键查找单个记录</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据FreeSwitch集群ID获取集群下所有会议桥")
    @GetMapping("/getByFcmCluster/{clusterId}")
    public RestResponse getAllFreeSwitchByGroupId(@PathVariable Long clusterId)
    {
        FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(clusterId);
        if (fcmBridgeCluster != null)
        {
            List<BusiFreeSwitch> bhs = new ArrayList<>();
            for (Iterator<FcmBridge> iterator = fcmBridgeCluster.getFcmBridges().iterator(); iterator.hasNext();)
            {
                FcmBridge fcmBridge = iterator.next();
                bhs.add(fcmBridge.getBusiFreeSwitch());
            }
            return success(bhs);
        }
        return success(null);
    }
    
    /**
     * <pre>获取当前部门下所有主用FreeSwitch组</pre>
     * @author lilinhai
     * @since 2021-01-21 15:54 
     * @return List<BusiFreeSwitchGroup>
     */
    @Operation(summary = "获取所有FreeSwitch集群")
    @GetMapping("/getAllFcmCluster")
    public RestResponse getAllFreeSwitchCluster()
    {
        List<ModelBean> gs = busiFreeSwitchClusterService.getAllBusiFreeSwitchCluster();
        return success(gs);
    }
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键查找单个记录</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键查询单个FreeSwitch集群信息")
    @GetMapping("/{id}")
    public RestResponse getBusiFreeSwitchClusterById(@PathVariable Long id)
    {
        return success(busiFreeSwitchClusterService.getFreeSwitchClusterById(id));
    }
}

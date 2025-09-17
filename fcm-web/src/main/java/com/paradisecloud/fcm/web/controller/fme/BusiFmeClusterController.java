package com.paradisecloud.fcm.web.controller.fme;

import java.util.ArrayList;
import java.util.Iterator;
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

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiFme;
import com.paradisecloud.fcm.dao.model.BusiFmeCluster;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.FmeBridgeCluster;
import com.paradisecloud.fcm.fme.websocket.interfaces.IFmeCacheService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * FME终端组Controller
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/fmeCluster")
@Tag(name = "FME集群控制层")
public class BusiFmeClusterController extends BaseController
{
    
    @Autowired
    private IFmeCacheService fmeCacheService;
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键ID删除一个实体</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键ID删除单个记录：id放到rest地址上占位符处")
    @DeleteMapping("/{id}")
    public RestResponse deleteBusiFmeGroup(@PathVariable Integer id)
    {
        fmeCacheService.deleteBusiFmeCluster(id);
        return success("Delete Entity successfully, id: " + id);
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键修改实体属性</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键修改单个会议桥组记录：id放到rest地址上占位符处，修改的字段和字段值放到请求body中封装为json格式")
    @PutMapping("/{id}")
    public RestResponse update(@RequestBody BusiFmeCluster busiFmeCluster, @PathVariable Long id)
    {
        busiFmeCluster.setId(id);
        fmeCacheService.updateBusiFmeCluster(busiFmeCluster);
        return success(busiFmeCluster);
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">创建会议桥组[bridgeHostGroup]</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "单个会议桥组记录新增：记录的属性和属性值放到请求body中封装为json格式")
    @PostMapping("")
    public RestResponse save(@RequestBody BusiFmeCluster busiFmeCluster)
    {
        fmeCacheService.addBusiFmeCluster(busiFmeCluster);
        return success(busiFmeCluster);
    }
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键查找单个记录</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据FME集群ID获取集群下所有会议桥")
    @GetMapping("/getFmesByCluster/{clusterId}")
    public RestResponse getAllFmeByGroupId(@PathVariable Long clusterId)
    {
        FmeBridgeCluster fmeHttpInvokers = FmeBridgeCache.getInstance().getByFmeClusterId(clusterId);
        if (fmeHttpInvokers != null)
        {
            List<BusiFme> bhs = new ArrayList<>();
            for (Iterator<FmeBridge> iterator = fmeHttpInvokers.getFmeBridges().iterator(); iterator.hasNext();)
            {
                FmeBridge fmeHttpInvoker = (FmeBridge) iterator.next();
                bhs.add(fmeHttpInvoker.getBusiFme());
            }
            return success(bhs);
        }
        return success(null);
    }
    
    /**
     * <pre>获取当前部门下所有主用FME组</pre>
     * @author lilinhai
     * @since 2021-01-21 15:54 
     * @return List<BusiFmeGroup>
     */
    @Operation(summary = "获取所有FME集群")
    @GetMapping("/getAllFmeCluster")
    public RestResponse getAllFmeCluster()
    {
        List<ModelBean> gs = fmeCacheService.getAllBusiFmeCluster();
        return success(gs);
    }
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键查找单个记录</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键查询单个FME集群信息")
    @GetMapping("/{id}")
    public RestResponse getBusiFmeClusterById(@PathVariable Long id)
    {
        return success(fmeCacheService.getFmeClusterById(id));
    }
}

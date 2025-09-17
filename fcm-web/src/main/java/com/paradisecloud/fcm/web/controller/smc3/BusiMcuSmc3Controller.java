package com.paradisecloud.fcm.web.controller.smc3;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.fme.cache.DeptFmeMappingCache;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3;
import com.paradisecloud.smc3.service.interfaces.IMcuSmc3CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Smc3终端信息Controller
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/mcu/smc3")
@Tag(name = "Smc3基础配置控制层")
public class BusiMcuSmc3Controller extends BaseController
{

    @Autowired
    private IMcuSmc3CacheService smc3CacheService;
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键查找单个记录</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "获取FME桥列表（不分页）")
    @GetMapping("/list")
    public RestResponse list()
    {
        List<ModelBean> busiFmes = new ArrayList<>();
        List<Smc3Bridge> fbs = new ArrayList<>(Smc3BridgeCache.getInstance().getIdToTeleBridgeMap().values());
        for (Smc3Bridge fmeBridge : fbs)
        {
            ModelBean mb = new ModelBean(fmeBridge.getBusiSMC());
            mb.remove("password");
            mb.remove("adminPassword");
            mb.put("bindDeptCount", DeptFmeMappingCache.getInstance().getBindDeptCount(FmeType.SINGLE_NODE, fmeBridge.getBusiSMC().getId()));
            
            // websocket断开次数
            mb.put("webSocketBreakCount", fmeBridge.getWebSocketBreakCount());
            
            // 连接创建成功的数量，理论只能存在一个
            mb.put("websocketConnections", fmeBridge.getWsAuthTokens());
            

            
            // 自上次断开后的重试次数
            mb.put("websocketConnectionTryTimesSinceLastDisconnected", fmeBridge.getWebsocketConnectionTryTimesSinceLastDisconnected());
            
            // 首次建立连接时间
            mb.put("firstConnectedTime", fmeBridge.getFirstConnectedTime());
            
            // 最后一次建立连接时间
            mb.put("lastConnectedTime", fmeBridge.getLastConnectedTime());
            
            // 最后一次连接断开时间
            mb.put("lastDisConnectedTime", fmeBridge.getLastDisConnectedTime());

            
            // 连接失败原因
            mb.put("connectionFailedReason", fmeBridge.getConnectionFailedReason());
            busiFmes.add(mb);
        }
        return success(busiFmes);
    }
    


    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">创建会议桥[bridgeHost]</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "单个会议桥记录新增：记录的属性和属性值放到请求body中封装为json格式")
    @PostMapping("")
    public RestResponse saveSmc3(@RequestBody BusiMcuSmc3 busiMcuSmc3)
    {
        smc3CacheService.addMcuSmc3(busiMcuSmc3);
        return success(busiMcuSmc3);
    }
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键ID删除一个实体</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键ID删除单个记录：id放到rest地址上占位符处")
    @DeleteMapping("/{id}")
    public RestResponse deleteSmc3(@PathVariable Long id)
    {
        smc3CacheService.deleteMcuSmc3(id);
        return success("Delete Entity successfully, id: " + id);
    }

    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键修改实体属性</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键修改单个会议桥记录：id放到rest地址上占位符处，修改的字段和字段值放到请求body中封装为json格式")
    @PutMapping("/{id}")
    public RestResponse updateSmc3(@RequestBody BusiMcuSmc3 busiMcuSmc3, @PathVariable Long id)
    {
        busiMcuSmc3.setId(id);
        smc3CacheService.updateMcuSmc3(busiMcuSmc3);
        return success(busiMcuSmc3);
    }
    
    /**
     * <pre style="color:blue;font-weight:bold;font-size:16px">根据主键查找单个记录</pre>
     * @author Nature Coding Robot
     * @version V2.0
     */
    @Operation(summary = "根据主键查询单个会议桥记录：id放到rest地址上占位符处")
    @GetMapping("/{id}")
    public RestResponse getFme(@PathVariable Long id)
    {
        Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getIdToTeleBridgeMap().get(id);
        if (smc3Bridge != null)
        {
            return success(smc3Bridge.getBusiSMC());
        }
        return success(null);
    }
}

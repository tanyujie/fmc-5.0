/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : FmeHttpInvokerCache.java
 * Package : com.paradisecloud.sync.cache
 * 
 * @author lilinhai
 * 
 * @since 2020-12-16 12:19
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.zte.cache;


import com.paradisecloud.fcm.common.enumer.McuZteType;
import com.paradisecloud.fcm.dao.model.BusiMcuZteDept;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridge;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridgeCollection;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>会议桥api工具对象缓存</pre>
 * 
 * @author lilinhai
 * @since 2020-12-16 12:19
 * @version V1.0
 */
public abstract class McuZteBridgeCache
{
    
    private static final McuZteBridgeCache INSTANCE = new McuZteBridgeCache()
    {
    };
    
    /**
     * 正常可用的，属于启用类别
     */
    private Map<String, McuZteBridge> ipToMcuZteBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 所有fme调用器缓存，key：会议桥的id
     */
    private Map<Long, McuZteBridge> mcuZteBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author lilinhai
     * @since 2020-12-16 12:19
     */
    private McuZteBridgeCache()
    {
    }
    
    /**
     * <pre>更新到全集缓存中</pre>
     * @author lilinhai
     * @since 2020-12-29 17:44 
     * @param mcuZteBridge void
     */
    public synchronized void update(McuZteBridge mcuZteBridge)
    {
        
        // 添加ID映射
        mcuZteBridgeMap.put(mcuZteBridge.getBusiMcuZte().getId(), mcuZteBridge);
        
        // 添加IP映射
        ipToMcuZteBridgeMap.put(mcuZteBridge.getBridgeAddress(), mcuZteBridge);
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean containsKey(Long id) {
        return mcuZteBridgeMap.containsKey(id);
    }
    
    /**
     * <pre>根据IP和端口号会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:29 
     * @param bridgeAddress IP:PORT
     * @return List<FmeHttpInvoker>
     */
    public McuZteBridge getMcuZteBridgeByBridgeAddress(String bridgeAddress)
    {
        return ipToMcuZteBridgeMap.get(bridgeAddress);
    }

    /**
     * <pre>根据IP获取会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:29
     * @param ipAddress
     * @return List<FmeHttpInvoker>
     */
    public McuZteBridge getMcuZteBridgeByBridgeAddressOnly(String ipAddress)
    {
        for (McuZteBridge fmeBridge : ipToMcuZteBridgeMap.values()) {
            if (fmeBridge.getBridgeAddress().startsWith(ipAddress + ":")) {
                return fmeBridge;
            }
        }
        return null;
    }
    
    /**
     * <pre>根据数据库id获取对象</pre>
     * @author lilinhai
     * @since 2020-12-29 18:05 
     * @param busiMcuZteId
     * @return FmeHttpInvoker
     */
    public McuZteBridge get(long busiMcuZteId)
    {
        return mcuZteBridgeMap.get(busiMcuZteId);
    }
    
    /**
     * 删除fme桥
     * @author lilinhai
     * @since 2021-03-19 15:59 
     * @param mcuZteBridge void
     */
    public void delete(McuZteBridge mcuZteBridge)
    {
        if (mcuZteBridge != null)
        {
            mcuZteBridge.setDeleted(true);
            
            // 销毁会议桥对象
            mcuZteBridge.destroy();
            McuZteBridge removed = mcuZteBridgeMap.remove(mcuZteBridge.getBusiMcuZte().getId());
            if (removed != null)
            {
                removed.getMcuZteLogger().logInfo("从invokerMap缓存移除本会议桥成功, 移除原因，会议桥被删除");
            }
            
            removed = ipToMcuZteBridgeMap.remove(mcuZteBridge.getBridgeAddress());
            if (removed != null)
            {
                removed.getMcuZteLogger().logInfo("从ipToInvokerMap缓存移除本会议桥成功, 移除原因，会议桥被删除!");
            }
        }
    }
    
    /**
     * <pre>获取所有的MCU桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:27 
     * @return List<McuZteBridge>
     */
    public List<McuZteBridge> getMcuZteBridges()
    {
        return new ArrayList<>(mcuZteBridgeMap.values());
    }
    
    public List<McuZteBridge> getMcuZteBridgesByDept(long deptId)
    {
        BusiMcuZteDept busiMcuZteDept = DeptMcuZteMappingCache.getInstance().getBindMcu(deptId);
        if (busiMcuZteDept != null)
        {
            if (busiMcuZteDept.getMcuType().intValue() == McuZteType.SINGLE_NODE.getValue())
            {
                McuZteBridge fmeBridge = mcuZteBridgeMap.get(busiMcuZteDept.getMcuId());
                return valid(Arrays.asList(fmeBridge));
            }
        }
        String errInfo = "很抱歉，【" + SysDeptCache.getInstance().get(deptId).getDeptName() + "】当前无可用的MCU，请联系管理员配置您的MCU！";
        LoggerFactory.getLogger(getClass()).error(errInfo);
        throw new SystemException(1001002, errInfo);
    }
    
    /**
     * <pre>根据部门ID获取主用的所有MCU，按权重最大倒叙排列</pre>
     * @author lilinhai
     * @since 2021-01-26 15:21 
     * @param deptId
     * @return List<FmeHttpInvoker>
     */
    public McuZteBridgeCollection getAvailableMcuZteBridgesByDept(long deptId)
    {
        McuZteBridgeCollection mcuXdBridgeCollection = new McuZteBridgeCollection();
        BusiMcuZteDept busiMcuZteDept = DeptMcuZteMappingCache.getInstance().getBindMcu(deptId);
        if (busiMcuZteDept == null)
        {
            return null;
        }
        
        // 如果是单节点
        if (busiMcuZteDept.getMcuType().intValue() == McuZteType.SINGLE_NODE.getValue())
        {
            McuZteBridge mcuXdBridge = mcuZteBridgeMap.get(busiMcuZteDept.getMcuId());
            
            // 可用则直接添加，不可用则转备用
            if (mcuXdBridge.isAvailable())
            {
                mcuXdBridgeCollection.addMcuZteBridge(mcuXdBridge);
                mcuXdBridgeCollection.setMasterMcuZteBridge(mcuXdBridge);
            }
        }
        
        return ObjectUtils.isEmpty(mcuXdBridgeCollection.getMcuZteBridges()) ? null : mcuXdBridgeCollection;
    }

    public McuZteBridgeCollection getUnAvailableMcuZteBridgesByDept(long deptId)
    {
        McuZteBridgeCollection mcuXdBridgeCollection = new McuZteBridgeCollection();
        BusiMcuZteDept busiMcuZteDept = DeptMcuZteMappingCache.getInstance().getBindMcu(deptId);
        if (busiMcuZteDept == null)
        {
            return null;
        }

        // 如果是单节点
        if (busiMcuZteDept.getMcuType().intValue() == McuZteType.SINGLE_NODE.getValue())
        {
            McuZteBridge mcuXdBridge = mcuZteBridgeMap.get(busiMcuZteDept.getMcuId());

            // 可用则直接添加，不可用则转备用
            if (!mcuXdBridge.isAvailable())
            {
                mcuXdBridgeCollection.addMcuZteBridge(mcuXdBridge);
                mcuXdBridgeCollection.setMasterMcuZteBridge(mcuXdBridge);
            }
        }

        return ObjectUtils.isEmpty(mcuXdBridgeCollection.getMcuZteBridges()) ? null : mcuXdBridgeCollection;
    }
    
    /**
     * <pre>获取可用的会议桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:27 
     * @return List<BridgeApiUtil>
     */
    public List<McuZteBridge> getAvailableMcuZteBridges()
    {
        return getMcuZteBridgesByStatus(true);
    }
    
    /**
     * <pre>获取不可用的会议桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:26 
     * @return List<BridgeApiUtil>
     */
    public List<McuZteBridge> getUnAvailableMcuZteBridges()
    {
        return getMcuZteBridgesByStatus(false);
    }
    

    
    /**
     * 根据可用状态获取桥集合
     * @author lilinhai
     * @since 2021-03-22 17:38 
     * @param isAvailable
     * @return List<McuZteBridge>
     */
    private List<McuZteBridge> getMcuZteBridgesByStatus(boolean isAvailable)
    {
        List<McuZteBridge> mcuZteBridgeList = new ArrayList<>();
        Collection<McuZteBridge> fhis = mcuZteBridgeMap.values();
        for (Iterator<McuZteBridge> iterator = fhis.iterator(); iterator.hasNext();)
        {
            McuZteBridge mcuZteBridge = iterator.next();
            if (mcuZteBridge.isAvailable() == isAvailable)
            {
                mcuZteBridgeList.add(mcuZteBridge);
            }
        }
        return mcuZteBridgeList;
    }
    

    
    private List<McuZteBridge> valid(List<McuZteBridge> mbs)
    {
        for (McuZteBridge fb : mbs)
        {
            if (fb.isInitializing())
            {
                throw new SystemException(1009485, "MCU" + (mbs.size() > 1 ? "集群" : "单") + "【" + fb.getBusiMcuZte().getIp() + "】节点初始化中，即将就绪，请稍后...");
            }
        }
        return mbs;
    }
    
    public static McuZteBridgeCache getInstance()
    {
        return INSTANCE;
    }
    
}

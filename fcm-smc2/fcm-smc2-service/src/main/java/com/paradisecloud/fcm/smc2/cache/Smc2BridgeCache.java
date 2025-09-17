package com.paradisecloud.fcm.smc2.cache;

import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2Dept;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2022/8/15 16:52
 */
public class Smc2BridgeCache {

    private static final Smc2BridgeCache INSTANCE = new Smc2BridgeCache() {
    };

    private Map<Long, Smc2BridgeCluster> smc2BridgeClusterMap = new ConcurrentHashMap<>();
    private Map<String, Smc2Bridge> ipToSmc2BridgeMap = new ConcurrentHashMap<>();

    private Map<Long, Smc2Bridge> smc2BridgeMap = new ConcurrentHashMap<>();

    public static Smc2BridgeCache getInstance() {
        return INSTANCE;
    }

    public synchronized void update(Smc2Bridge smc2Bridge) {
        // 添加ID映射
        smc2BridgeMap.put(smc2Bridge.getBusiSmc2().getId(), smc2Bridge);
        // 添加IP映射
        ipToSmc2BridgeMap.put(smc2Bridge.getBusiSmc2().getIp(), smc2Bridge);
    }

    public synchronized void init(Smc2Bridge smc2Bridge) {
        // 添加ID映射
        smc2BridgeMap.put(smc2Bridge.getBusiSmc2().getId(), smc2Bridge);
        // 添加IP映射
        ipToSmc2BridgeMap.put(smc2Bridge.getBusiSmc2().getIp(), smc2Bridge);

    }

    public synchronized void removeSmc2(Smc2Bridge smc2Bridge) {
        if (smc2Bridge != null) {
            smc2Bridge.setDeleted(true);
        }
        // 添加ID映射
        boolean remove = smc2BridgeMap.remove(smc2Bridge.getBusiSmc2().getId(), smc2Bridge);
        if (remove) {
            smc2Bridge.getLogger().info("Smc2会议桥移除成功,移除原因，会议桥被删除" + smc2Bridge.getRootUrl());
        }
        remove = ipToSmc2BridgeMap.remove(smc2Bridge.getBusiSmc2().getIp(), smc2Bridge);
        if (remove) {
            smc2Bridge.getLogger().info("Smc2会议桥移除成功,移除原因，会议桥被删除" + smc2Bridge.getRootUrl());
        }

    }


    public Smc2Bridge getAvailableBridgesByDept(Integer deptId) {
        BusiMcuSmc2Dept busiSmc2Dept = DeptSmc2MappingCache.getInstance().getBindSmc(deptId.longValue());
        if (busiSmc2Dept == null) {
            return null;
        }
        Smc2Bridge smc2Bridge = smc2BridgeMap.get(busiSmc2Dept.getMcuId());
        if(smc2Bridge==null){
            return null;
        }
        // 可用则直接添加，不可用则转备用
        if (smc2Bridge.isAvailable()) {
            return smc2Bridge;
        }
        return null;
    }

    public Smc2Bridge getBridgesByDept(Long deptId) {
        BusiMcuSmc2Dept busiSmc2Dept = DeptSmc2MappingCache.getInstance().getBindSmc(deptId.longValue());
        if (busiSmc2Dept == null) {
            return null;
        }
        Smc2Bridge smc2Bridge = smc2BridgeMap.get(busiSmc2Dept.getMcuId());
        return smc2Bridge;
    }

    public Map<Long, Smc2Bridge> getSmc2BridgeMap() {
        return smc2BridgeMap;
    }

    /**
     * <pre>根据部门ID获取主用的所有FME，按权重最大倒叙排列</pre>
     * @author lilinhai
     * @since 2021-01-26 15:21
     * @param deptId
     * @return List<FmeHttpInvoker>
     */
    public Smc2BridgeCollection getAvailableSmc2BridgesByDept(long deptId)
    {
        Smc2BridgeCollection fmeBridgeCollection = new Smc2BridgeCollection();
        BusiMcuSmc2Dept busiMcuSmc2Dept = DeptSmc2MappingCache.getInstance().getBindSmc(deptId);
        if (busiMcuSmc2Dept == null)
        {
            return null;
        }

        // 如果是单节点
        if (busiMcuSmc2Dept.getMcuType().intValue() == FmeType.SINGLE_NODE.getValue())
        {
            Smc2Bridge smc2Bridge = smc2BridgeMap.get(busiMcuSmc2Dept.getMcuId());
            if(smc2Bridge==null){
                return null;
            }

            // 可用则直接添加，不可用则转备用
            if (smc2Bridge.isAvailable())
            {
                fmeBridgeCollection.addSmc2Bridge(smc2Bridge);
            }
            // 转备用
            else
            {
                smc2Bridge = getSpareFmeBridge(smc2Bridge);
                if (smc2Bridge != null)
                {
                    fmeBridgeCollection.addSmc2Bridge(smc2Bridge);
                }
            }
        }
        else
        {
            // 根据集群ID获取集群下可用的fme集合
            this.addAvailableFmeBridge(fmeBridgeCollection, busiMcuSmc2Dept.getMcuId());
        }

        return ObjectUtils.isEmpty(fmeBridgeCollection.getSmc2Bridges()) ? null : fmeBridgeCollection;
    }


    public Smc2Bridge getSpareFmeBridge(Smc2Bridge smc2Bridge)
    {
        if (smc2Bridge.getBusiSmc2().getSpareMcuId() == null)
        {
            return null;
        }

        return getSpareFmeBridge(smc2Bridge.getBusiSmc2().getSpareMcuId());
    }


    public Smc2Bridge getSpareFmeBridge(long spareSmc2Id)
    {
        Smc2Bridge spareSmc2Bridge = smc2BridgeMap.get(spareSmc2Id);
        return spareSmc2Bridge.isAvailable() ? spareSmc2Bridge : getSpareFmeBridge(spareSmc2Bridge);
    }

    private void addAvailableFmeBridge(Smc2BridgeCollection smc2BridgeCollection, long clusterId)
    {
        // 根据集群ID获取集群下可用的fme集合
        Smc2BridgeCluster fmeBridgeCluster = getByFmeClusterId(clusterId);
        if (fmeBridgeCluster == null)
        {
            return;
        }

        List<Smc2Bridge> availableFmeBridges0 = fmeBridgeCluster.getAvailableFmeBridges();
        if (!ObjectUtils.isEmpty(availableFmeBridges0))
        {
            smc2BridgeCollection.setMasterSmc2Bridge(fmeBridgeCluster.getMasterFmeBridge());
            for (Smc2Bridge fmeBridge : availableFmeBridges0)
            {
                smc2BridgeCollection.addSmc2Bridge(fmeBridge);
            }
        }

        if (ObjectUtils.isEmpty(smc2BridgeCollection.getSmc2Bridges()))
        {
            if (fmeBridgeCluster.getSpareFmeType() != null)
            {
                if (fmeBridgeCluster.getSpareFmeType() == FmeType.CLUSTER)
                {
                    this.addAvailableFmeBridge(smc2BridgeCollection, fmeBridgeCluster.getSpareFmeId());
                }
                else
                {
                    Smc2Bridge fmeBridge = getSpareFmeBridge(fmeBridgeCluster.getSpareFmeId());
                    if (fmeBridge != null)
                    {
                        smc2BridgeCollection.addSmc2Bridge(fmeBridge);
                    }
                }
            }
        }
    }

    public Smc2BridgeCluster getByFmeClusterId(long clusterId)
    {
        return smc2BridgeClusterMap.get(clusterId);
    }


    public Map<String, Smc2Bridge> getIpToSmc2BridgeMap() {
        return ipToSmc2BridgeMap;
    }

    public Smc2Bridge get(Long mcuId) {
        return smc2BridgeMap.get(mcuId);
    }
}

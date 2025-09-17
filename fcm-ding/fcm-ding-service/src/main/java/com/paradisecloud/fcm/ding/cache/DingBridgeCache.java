package com.paradisecloud.fcm.ding.cache;

import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.model.BusiMcuDingDept;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2022/8/15 16:53
 */
public class DingBridgeCache {

    private static final DingBridgeCache INSTANCE = new DingBridgeCache() {
    };
    private Map<Long, DingBridgeCluster> DingBridgeClusterMap = new ConcurrentHashMap<>();

    private Map<Long, DingBridge> DingBridgeMap = new ConcurrentHashMap<>();

    public static DingBridgeCache getInstance() {
        return INSTANCE;
    }

    public synchronized void update(DingBridge dingBridge) {
        // 添加ID映射
        DingBridgeMap.put(dingBridge.getBusiDing().getId(), dingBridge);

    }

    public synchronized void init(DingBridge dingBridge) {
        // 添加ID映射
        DingBridgeMap.put(dingBridge.getBusiDing().getId(), dingBridge);


    }

    public synchronized void removeDing(DingBridge dingBridge) {
        if (dingBridge != null) {
            dingBridge.setDeleted(true);
        }
        // 添加ID映射
        boolean remove = DingBridgeMap.remove(dingBridge.getBusiDing().getId(), dingBridge);
        if (remove) {
            dingBridge.getLogger().info("Ding会议桥移除成功,移除原因，会议桥被删除" + dingBridge.getBusiDing().getSdkId());
        }
    }


    public DingBridge getAvailableBridgesByDept(Long deptId) {
        BusiMcuDingDept DingDept = DeptDingMappingCache.getInstance().getBindSmc(deptId);
        if (DingDept == null) {
            return null;
        }
        DingBridge dingBridge = DingBridgeMap.get(DingDept.getMcuId());
        if(dingBridge ==null){
            return null;
        }
        // 可用则直接添加，不可用则转备用
        if (dingBridge.isAvailable()) {
            return dingBridge;
        }
        return null;
    }

    public DingBridge getBridgesByDept(Integer deptId) {
        BusiMcuDingDept DingDept = DeptDingMappingCache.getInstance().getBindSmc(deptId.longValue());
        if (DingDept == null) {
            return null;
        }
        DingBridge dingBridge = DingBridgeMap.get(DingDept.getMcuId());
        return dingBridge;
    }

    public Map<Long, DingBridge> getDingBridgeMap() {
        return DingBridgeMap;
    }

    public DingBridgeCollection getAvailableDingBridgesByDept(long deptId)
    {
        DingBridgeCollection bridgeCollection = new DingBridgeCollection();
        BusiMcuDingDept busiDingDept = DeptDingMappingCache.getInstance().getBindSmc(deptId);
        if (busiDingDept == null)
        {
            return null;
        }

        // 如果是单节点
        if (busiDingDept.getMcuType().intValue() == FmeType.SINGLE_NODE.getValue())
        {
            DingBridge DingBridge = DingBridgeMap.get(busiDingDept.getMcuId());

            // 可用则直接添加，不可用则转备用
            if (DingBridge.isAvailable())
            {
                bridgeCollection.addDingBridge(DingBridge);
            }
            // 转备用
            else
            {
                DingBridge = getSpareDingBridge(DingBridge);
                if (DingBridge != null)
                {
                    bridgeCollection.addDingBridge(DingBridge);
                }
            }
        }
        else
        {
            // 根据集群ID获取集群下可用的Ding集合
            this.addAvailableDingBridge(bridgeCollection, busiDingDept.getMcuId());
        }

        return ObjectUtils.isEmpty(bridgeCollection.getDingBridges()) ? null : bridgeCollection;
    }


    private void addAvailableDingBridge(DingBridgeCollection DingBridgeCollection, long clusterId)
    {
        // 根据集群ID获取集群下可用的fme集合
        DingBridgeCluster DingBridgeCluster = getByDingClusterId(clusterId);
        if (DingBridgeCluster == null)
        {
            return;
        }

        List<DingBridge> availableFmeBridges0 = DingBridgeCluster.getAvailableFmeBridges();
        if (!ObjectUtils.isEmpty(availableFmeBridges0))
        {
            DingBridgeCollection.setMasterDingBridge(DingBridgeCluster.getMasterFmeBridge());
            for (DingBridge fmeBridge : availableFmeBridges0)
            {
                DingBridgeCollection.addDingBridge(fmeBridge);
            }
        }

        if (ObjectUtils.isEmpty(DingBridgeCollection.getDingBridges()))
        {
            if (DingBridgeCluster.getSpareFmeType() != null)
            {
                if (DingBridgeCluster.getSpareFmeType() == FmeType.CLUSTER)
                {
                    this.addAvailableDingBridge(DingBridgeCollection, DingBridgeCluster.getSpareFmeId());
                }
                else
                {
                    DingBridge fmeBridge = getSpareDingBridge(DingBridgeCluster.getSpareFmeId());
                    if (fmeBridge != null)
                    {
                        DingBridgeCollection.addDingBridge(fmeBridge);
                    }
                }
            }
        }
    }


    public DingBridgeCluster getByDingClusterId(long clusterId)
    {
        return DingBridgeClusterMap.get(clusterId);
    }


    public DingBridge getSpareDingBridge(long spareDingId)
    {
        DingBridge spareDingBridge = DingBridgeMap.get(spareDingId);
        return spareDingBridge.isAvailable() ? spareDingBridge : getSpareDingBridge(spareDingBridge);
    }

    public DingBridge getSpareDingBridge(DingBridge bridge)
    {
        if (bridge.getBusiDing().getSpareSmcId() == null)
        {
            return null;
        }

        return getSpareDingBridge(bridge.getBusiDing().getSpareSmcId());
    }


    public DingBridge get(Long mcuId) {
        return DingBridgeMap.get(mcuId);
    }

}

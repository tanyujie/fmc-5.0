package com.paradisecloud.fcm.tencent.cache;

import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentDept;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2022/8/15 16:53
 */
public class TencentBridgeCache {

    private static final TencentBridgeCache INSTANCE = new TencentBridgeCache() {
    };
    private Map<Long, TencentBridgeCluster> tencentBridgeClusterMap = new ConcurrentHashMap<>();

    private Map<Long, TencentBridge> tencentBridgeMap = new ConcurrentHashMap<>();

    public static TencentBridgeCache getInstance() {
        return INSTANCE;
    }

    public synchronized void update(TencentBridge tencentBridge) {
        // 添加ID映射
        tencentBridgeMap.put(tencentBridge.getBusiTencent().getId(), tencentBridge);

    }

    public synchronized void init(TencentBridge tencentBridge) {
        // 添加ID映射
        tencentBridgeMap.put(tencentBridge.getBusiTencent().getId(), tencentBridge);


    }

    public synchronized void removeTencent(TencentBridge tencentBridge) {
        if (tencentBridge != null) {
            tencentBridge.setDeleted(true);
        }
        // 添加ID映射
        boolean remove = tencentBridgeMap.remove(tencentBridge.getBusiTencent().getId(), tencentBridge);
        if (remove) {
            tencentBridge.getLogger().info("tencent会议桥移除成功,移除原因，会议桥被删除" +tencentBridge.getBusiTencent().getSdkId());
        }
    }


    public TencentBridge getAvailableBridgesByDept(Long deptId) {
        BusiMcuTencentDept tencentDept = DeptTencentMappingCache.getInstance().getBindSmc(deptId);
        if (tencentDept == null) {
            return null;
        }
        TencentBridge tencentBridge = tencentBridgeMap.get(tencentDept.getMcuId());
        if(tencentBridge==null){
            return null;
        }
        // 可用则直接添加，不可用则转备用
        if (tencentBridge.isAvailable()) {
            return tencentBridge;
        }
        return null;
    }

    public TencentBridge getBridgesByDept(Integer deptId) {
        BusiMcuTencentDept tencentDept = DeptTencentMappingCache.getInstance().getBindSmc(deptId.longValue());
        if (tencentDept == null) {
            return null;
        }
        TencentBridge tencentBridge = tencentBridgeMap.get(tencentDept.getMcuId());
        return tencentBridge;
    }

    public Map<Long, TencentBridge> getTencentBridgeMap() {
        return tencentBridgeMap;
    }

    public TencentBridgeCollection getAvailableTencentBridgesByDept(long deptId)
    {
        TencentBridgeCollection bridgeCollection = new TencentBridgeCollection();
        BusiMcuTencentDept busiTencentDept = DeptTencentMappingCache.getInstance().getBindSmc(deptId);
        if (busiTencentDept == null)
        {
            return null;
        }

        // 如果是单节点
        if (busiTencentDept.getMcuType().intValue() == FmeType.SINGLE_NODE.getValue())
        {
            TencentBridge tencentBridge = tencentBridgeMap.get(busiTencentDept.getMcuId());
            if(tencentBridge==null){
                return null;
            }

            // 可用则直接添加，不可用则转备用
            if (tencentBridge.isAvailable())
            {
                bridgeCollection.addTencentBridge(tencentBridge);
            }
            // 转备用
            else
            {
                tencentBridge = getSpareTencentBridge(tencentBridge);
                if (tencentBridge != null)
                {
                    bridgeCollection.addTencentBridge(tencentBridge);
                }
            }
        }
        else
        {
            // 根据集群ID获取集群下可用的Tencent集合
            this.addAvailableTencentBridge(bridgeCollection, busiTencentDept.getMcuId());
        }

        return ObjectUtils.isEmpty(bridgeCollection.getTencentBridges()) ? null : bridgeCollection;
    }


    private void addAvailableTencentBridge(TencentBridgeCollection TencentBridgeCollection, long clusterId)
    {
        // 根据集群ID获取集群下可用的fme集合
        TencentBridgeCluster tencentBridgeCluster = getByTencentClusterId(clusterId);
        if (tencentBridgeCluster == null)
        {
            return;
        }

        List<TencentBridge> availableFmeBridges0 = tencentBridgeCluster.getAvailableFmeBridges();
        if (!ObjectUtils.isEmpty(availableFmeBridges0))
        {
            TencentBridgeCollection.setMasterTencentBridge(tencentBridgeCluster.getMasterFmeBridge());
            for (TencentBridge fmeBridge : availableFmeBridges0)
            {
                TencentBridgeCollection.addTencentBridge(fmeBridge);
            }
        }

        if (ObjectUtils.isEmpty(TencentBridgeCollection.getTencentBridges()))
        {
            if (tencentBridgeCluster.getSpareFmeType() != null)
            {
                if (tencentBridgeCluster.getSpareFmeType() == FmeType.CLUSTER)
                {
                    this.addAvailableTencentBridge(TencentBridgeCollection, tencentBridgeCluster.getSpareFmeId());
                }
                else
                {
                    TencentBridge fmeBridge = getSpareTencentBridge(tencentBridgeCluster.getSpareFmeId());
                    if (fmeBridge != null)
                    {
                        TencentBridgeCollection.addTencentBridge(fmeBridge);
                    }
                }
            }
        }
    }


    public TencentBridgeCluster getByTencentClusterId(long clusterId)
    {
        return tencentBridgeClusterMap.get(clusterId);
    }


    public TencentBridge getSpareTencentBridge(long spareTencentId)
    {
        TencentBridge spareTencentBridge = tencentBridgeMap.get(spareTencentId);
        return spareTencentBridge.isAvailable() ? spareTencentBridge : getSpareTencentBridge(spareTencentBridge);
    }

    public TencentBridge getSpareTencentBridge(TencentBridge bridge)
    {
        if (bridge.getBusiTencent().getSpareSmcId() == null)
        {
            return null;
        }

        return getSpareTencentBridge(bridge.getBusiTencent().getSpareSmcId());
    }


    public TencentBridge get(Long mcuId) {
        return tencentBridgeMap.get(mcuId);
    }

}

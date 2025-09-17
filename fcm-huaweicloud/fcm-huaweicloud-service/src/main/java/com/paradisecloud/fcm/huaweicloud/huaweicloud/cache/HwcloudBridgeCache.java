package com.paradisecloud.fcm.huaweicloud.huaweicloud.cache;

import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudDept;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2022/8/15 16:53
 */
public class HwcloudBridgeCache {

    private static final HwcloudBridgeCache INSTANCE = new HwcloudBridgeCache() {
    };
    private Map<Long, HwcloudBridgeCluster> HwcloudBridgeClusterMap = new ConcurrentHashMap<>();

    private Map<Long, HwcloudBridge> HwcloudBridgeMap = new ConcurrentHashMap<>();

    private Map<String, HwcloudBridge> appIdHwcloudBridgeMap = new ConcurrentHashMap<>();

    public static HwcloudBridgeCache getInstance() {
        return INSTANCE;
    }

    public synchronized void update(HwcloudBridge hwcloudBridge) {
        // 添加ID映射
        HwcloudBridgeMap.put(hwcloudBridge.getBusiHwcloud().getId(), hwcloudBridge);
        appIdHwcloudBridgeMap.put(hwcloudBridge.getBusiHwcloud().getAppId(), hwcloudBridge);
    }

    public synchronized void init(HwcloudBridge hwcloudBridge) {
        // 添加ID映射
        HwcloudBridgeMap.put(hwcloudBridge.getBusiHwcloud().getId(), hwcloudBridge);
        // 添加ID映射
        appIdHwcloudBridgeMap.put(hwcloudBridge.getBusiHwcloud().getAppId(), hwcloudBridge);

    }

    public synchronized void removeHwcloud(HwcloudBridge hwcloudBridge) {
        if (hwcloudBridge != null) {
            hwcloudBridge.setDeleted(true);
        }
        // 添加ID映射
        boolean remove = HwcloudBridgeMap.remove(hwcloudBridge.getBusiHwcloud().getId(), hwcloudBridge);
        if (remove) {
            hwcloudBridge.getLogger().info("Hwcloud会议桥移除成功,移除原因，会议桥被删除" + hwcloudBridge.getBusiHwcloud().getAppId());
        }

        // 添加ID映射
        boolean removeApp = appIdHwcloudBridgeMap.remove(hwcloudBridge.getBusiHwcloud().getAppKey(), hwcloudBridge);
        if (removeApp) {
            hwcloudBridge.getLogger().info("Hwcloud会议桥移除成功,移除原因，会议桥被删除" + hwcloudBridge.getBusiHwcloud().getAppId());
        }
    }

    public HwcloudBridge getBridgesByAppId(String appId){
       return appIdHwcloudBridgeMap.get(appId);
    }


    public HwcloudBridge getAvailableBridgesByDept(Long deptId) {
        BusiMcuHwcloudDept HwcloudDept = DeptHwcloudMappingCache.getInstance().getBindSmc(deptId);
        if (HwcloudDept == null) {
            return null;
        }
        HwcloudBridge hwcloudBridge = HwcloudBridgeMap.get(HwcloudDept.getMcuId());
        if(hwcloudBridge ==null){
            return null;
        }
        // 可用则直接添加，不可用则转备用
        if (hwcloudBridge.isAvailable()) {
            return hwcloudBridge;
        }
        return null;
    }

    public HwcloudBridge getBridgesByDept(Long deptId) {
        BusiMcuHwcloudDept HwcloudDept = DeptHwcloudMappingCache.getInstance().getBindSmc(deptId);
        if (HwcloudDept == null) {
            return null;
        }
        HwcloudBridge hwcloudBridge = HwcloudBridgeMap.get(HwcloudDept.getMcuId());
        return hwcloudBridge;
    }

    public Map<Long, HwcloudBridge> getHwcloudBridgeMap() {
        return HwcloudBridgeMap;
    }

    public HwcloudBridgeCollection getAvailableHwcloudBridgesByDept(long deptId)
    {
        HwcloudBridgeCollection bridgeCollection = new HwcloudBridgeCollection();
        BusiMcuHwcloudDept busiHwcloudDept = DeptHwcloudMappingCache.getInstance().getBindSmc(deptId);
        if (busiHwcloudDept == null)
        {
            return null;
        }

        // 如果是单节点
        if (busiHwcloudDept.getMcuType().intValue() == FmeType.SINGLE_NODE.getValue())
        {
            HwcloudBridge HwcloudBridge = HwcloudBridgeMap.get(busiHwcloudDept.getMcuId());

            // 可用则直接添加，不可用则转备用
            if (HwcloudBridge.isAvailable())
            {
                bridgeCollection.addHwcloudBridge(HwcloudBridge);
            }
            // 转备用
            else
            {
                HwcloudBridge = getSpareHwcloudBridge(HwcloudBridge);
                if (HwcloudBridge != null)
                {
                    bridgeCollection.addHwcloudBridge(HwcloudBridge);
                }
            }
        }
        else
        {
            // 根据集群ID获取集群下可用的Hwcloud集合
            this.addAvailableHwcloudBridge(bridgeCollection, busiHwcloudDept.getMcuId());
        }

        return ObjectUtils.isEmpty(bridgeCollection.getHwcloudBridges()) ? null : bridgeCollection;
    }


    private void addAvailableHwcloudBridge(HwcloudBridgeCollection HwcloudBridgeCollection, long clusterId)
    {
        // 根据集群ID获取集群下可用的fme集合
        HwcloudBridgeCluster HwcloudBridgeCluster = getByHwcloudClusterId(clusterId);
        if (HwcloudBridgeCluster == null)
        {
            return;
        }

        List<HwcloudBridge> availableFmeBridges0 = HwcloudBridgeCluster.getAvailableFmeBridges();
        if (!ObjectUtils.isEmpty(availableFmeBridges0))
        {
            HwcloudBridgeCollection.setMasterHwcloudBridge(HwcloudBridgeCluster.getMasterFmeBridge());
            for (HwcloudBridge fmeBridge : availableFmeBridges0)
            {
                HwcloudBridgeCollection.addHwcloudBridge(fmeBridge);
            }
        }

        if (ObjectUtils.isEmpty(HwcloudBridgeCollection.getHwcloudBridges()))
        {
            if (HwcloudBridgeCluster.getSpareFmeType() != null)
            {
                if (HwcloudBridgeCluster.getSpareFmeType() == FmeType.CLUSTER)
                {
                    this.addAvailableHwcloudBridge(HwcloudBridgeCollection, HwcloudBridgeCluster.getSpareFmeId());
                }
                else
                {
                    HwcloudBridge fmeBridge = getSpareHwcloudBridge(HwcloudBridgeCluster.getSpareFmeId());
                    if (fmeBridge != null)
                    {
                        HwcloudBridgeCollection.addHwcloudBridge(fmeBridge);
                    }
                }
            }
        }
    }


    public HwcloudBridgeCluster getByHwcloudClusterId(long clusterId)
    {
        return HwcloudBridgeClusterMap.get(clusterId);
    }


    public HwcloudBridge getSpareHwcloudBridge(long spareHwcloudId)
    {
        HwcloudBridge spareHwcloudBridge = HwcloudBridgeMap.get(spareHwcloudId);
        return spareHwcloudBridge.isAvailable() ? spareHwcloudBridge : getSpareHwcloudBridge(spareHwcloudBridge);
    }

    public HwcloudBridge getSpareHwcloudBridge(HwcloudBridge bridge)
    {
        if (bridge.getBusiHwcloud().getSpareSmcId() == null)
        {
            return null;
        }

        return getSpareHwcloudBridge(bridge.getBusiHwcloud().getSpareSmcId());
    }


    public HwcloudBridge get(Long mcuId) {
        return HwcloudBridgeMap.get(mcuId);
    }


    public Map<String, HwcloudBridge> getAppIdHwcloudBridgeMap() {
        return appIdHwcloudBridgeMap;
    }

    public void setAppIdHwcloudBridgeMap(Map<String, HwcloudBridge> appIdHwcloudBridgeMap) {
        this.appIdHwcloudBridgeMap = appIdHwcloudBridgeMap;
    }
}

package com.paradisecloud.smc3.busi.cache;

import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3ClusterMap;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3Dept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2022/10/14 15:25
 */
public class Smc3BridgeCache {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final Smc3BridgeCache INSTANCE = new Smc3BridgeCache() {
    };


    private Map<String, Smc3Bridge> ipToTeleBridgeMap = new ConcurrentHashMap<>();

    private Map<Long, Smc3Bridge> idToTeleBridgeMap = new ConcurrentHashMap<>();

    /**
     * 一个组对应的FME集合映射，key：groupId
     */
    private Map<Long, Smc3BridgeCluster> smc3BridgeClusterMap = new ConcurrentHashMap<>();

    public synchronized void update(Smc3Bridge smc3Bridge) {
        // 添加ID映射
        idToTeleBridgeMap.put(smc3Bridge.getBusiSMC().getId(), smc3Bridge);
        // 添加IP映射
        ipToTeleBridgeMap.put(smc3Bridge.getBusiSMC().getIp(), smc3Bridge);
    }

    public static Smc3BridgeCache getInstance() {
        return INSTANCE;
    }

    public Map<String, Smc3Bridge> getIpToTeleBridgeMap() {
        return ipToTeleBridgeMap;
    }

    public Map<Long, Smc3Bridge> getIdToTeleBridgeMap() {
        return idToTeleBridgeMap;
    }

    public synchronized void remove(Smc3Bridge smc3Bridge) {

        // 添加ID映射
        boolean remove = idToTeleBridgeMap.remove(smc3Bridge.getBusiSMC().getId(), smc3Bridge);
        if (remove) {
            logger.info("Smc3会议桥移除成功,移除原因，会议桥被删除" + smc3Bridge.getRootUrl());
        }
        remove = ipToTeleBridgeMap.remove(smc3Bridge.getBusiSMC().getIp(), smc3Bridge);
        if (remove) {
            logger.info("Smc3会议桥移除成功,移除原因，会议桥被删除" + smc3Bridge.getRootUrl());
        }

        smc3BridgeClusterMap.forEach((k, v) -> {
            if (!ObjectUtils.isEmpty(v))
            {
                v.remove(smc3Bridge);
            }
        });

    }



    public Smc3Bridge getBridgesByDept(Long deptId){
        if (deptId == null) {

            if (!CollectionUtils.isEmpty(idToTeleBridgeMap)) {
                for (Smc3Bridge value : idToTeleBridgeMap.values()) {
                    return value;
                }
            }
        }
        BusiMcuSmc3Dept busiTeleDept = DeptSmc3MappingCache.getInstance().getBindSmc(deptId);
        if (busiTeleDept == null) {
            return null;
        }
        Smc3Bridge smc3Bridge = idToTeleBridgeMap.get(busiTeleDept.getMcuId());
        return smc3Bridge;
    }


    /**
     * <pre>根据部门ID获取主用的所有FME，按权重最大倒叙排列</pre>
     * @author lilinhai
     * @since 2021-01-26 15:21
     * @param deptId
     * @return List<FmeHttpInvoker>
     */
    public Smc3BridgeCollection getAvailableSmc3BridgesByDept(long deptId)
    {
        Smc3BridgeCollection fmeBridgeCollection = new Smc3BridgeCollection();
        BusiMcuSmc3Dept busiMcuSmc3Dept = DeptSmc3MappingCache.getInstance().getBindSmc3(deptId);
        if (busiMcuSmc3Dept == null)
        {
            return null;
        }

        // 如果是单节点
        if (busiMcuSmc3Dept.getMcuType().intValue() == FmeType.SINGLE_NODE.getValue())
        {
            Smc3Bridge fmeBridge = idToTeleBridgeMap.get(busiMcuSmc3Dept.getMcuId());

            // 可用则直接添加，不可用则转备用
            if (fmeBridge.isAvailable())
            {
                fmeBridgeCollection.addSmc3Bridge(fmeBridge);
            }
            // 转备用
            else
            {
                fmeBridge = getSpareFmeBridge(fmeBridge);
                if (fmeBridge != null)
                {
                    fmeBridgeCollection.addSmc3Bridge(fmeBridge);
                }
            }
        }
        else
        {
            // 根据集群ID获取集群下可用的fme集合
            this.addAvailableFmeBridge(fmeBridgeCollection, busiMcuSmc3Dept.getMcuId());
        }

        return ObjectUtils.isEmpty(fmeBridgeCollection.getSmc3Bridges()) ? null : fmeBridgeCollection;
    }


    public Smc3Bridge getSpareFmeBridge(Smc3Bridge smc3Bridge)
    {
        if (smc3Bridge.getBusiSMC().getSpareMcuId() == null)
        {
            return null;
        }

        return getSpareFmeBridge(smc3Bridge.getBusiSMC().getSpareMcuId());
    }

    public Smc3Bridge getSpareFmeBridge(long spareFmeId)
    {
        Smc3Bridge spareFmeBridge = idToTeleBridgeMap.get(spareFmeId);
        return spareFmeBridge.isAvailable() ? spareFmeBridge : getSpareFmeBridge(spareFmeBridge);
    }


    private void addAvailableFmeBridge(Smc3BridgeCollection smc3BridgeCollection, long clusterId)
    {
        // 根据集群ID获取集群下可用的fme集合
        Smc3BridgeCluster fmeBridgeCluster = getByFmeClusterId(clusterId);
        if (fmeBridgeCluster == null)
        {
            return;
        }

        List<Smc3Bridge> availableFmeBridges0 = fmeBridgeCluster.getAvailableFmeBridges();
        if (!ObjectUtils.isEmpty(availableFmeBridges0))
        {
            smc3BridgeCollection.setMasterSmc3Bridge(fmeBridgeCluster.getMasterFmeBridge());
            for (Smc3Bridge fmeBridge : availableFmeBridges0)
            {
                smc3BridgeCollection.addSmc3Bridge(fmeBridge);
            }
        }

        if (ObjectUtils.isEmpty(smc3BridgeCollection.getSmc3Bridges()))
        {
            if (fmeBridgeCluster.getSpareFmeType() != null)
            {
                if (fmeBridgeCluster.getSpareFmeType() == FmeType.CLUSTER)
                {
                    this.addAvailableFmeBridge(smc3BridgeCollection, fmeBridgeCluster.getSpareFmeId());
                }
                else
                {
                    Smc3Bridge fmeBridge = getSpareFmeBridge(fmeBridgeCluster.getSpareFmeId());
                    if (fmeBridge != null)
                    {
                        smc3BridgeCollection.addSmc3Bridge(fmeBridge);
                    }
                }
            }
        }
    }

    public Smc3BridgeCluster getByFmeClusterId(long clusterId)
    {
        return smc3BridgeClusterMap.get(clusterId);
    }

    public synchronized void update(BusiMcuSmc3ClusterMap fmeClusterMap)
    {
        Smc3Bridge fmeBridge = idToTeleBridgeMap.get(fmeClusterMap.getMcuId());
        Assert.notNull(fmeBridge, "SMC3绑定smc3集群失败，smc3Bridge桥找不到：" + fmeClusterMap.getMcuId());
        fmeBridge.setWeight(fmeClusterMap.getWeight());
        Smc3BridgeCluster fmeBridgeCluster = getByFmeClusterId(fmeClusterMap.getClusterId());
        if (fmeBridgeCluster == null)
        {
            fmeBridgeCluster = new Smc3BridgeCluster();
            smc3BridgeClusterMap.put(fmeClusterMap.getClusterId(), fmeBridgeCluster);
        }
        fmeBridgeCluster.addFmeBridge(fmeBridge);
    }


    public Smc3Bridge get(Long mcuId) {
        return idToTeleBridgeMap.get(mcuId);
    }
}

package com.paradisecloud.fcm.terminal.fs.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.paradisecloud.fcm.dao.model.BusiFreeSwitchClusterMap;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


/**  
 * <pre>Fcm桥缓存</pre>
 * @author zyz
 * @since 2021-11-09 14:35
 * @version V1.0  
 */
public abstract class FcmBridgeCache
{
    
    private static final FcmBridgeCache INSTANCE = new FcmBridgeCache()
    {
        
    };
    
    /**
     * 所有FcmBridge缓存，key：FCM桥的IP
     */
    private Map<String, FcmBridge> ipToFcmBridgeMap = new ConcurrentHashMap<>();

    /**
     * 所有FcmBridge缓存，key：FCM桥的域名
     */
    private Map<String, FcmBridge> domainNameToFcmBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 所有FcmBridge缓存，key：Fcm桥的id
     */
    private Map<Long, FcmBridge> fcmBridgeMap = new ConcurrentHashMap<>();

    /**
     * 一个组对应的FreeSwitch集合映射，key：groupId
     */
    private Map<Long, FcmBridgeCluster> fcmBridgeClusterMap = new ConcurrentHashMap<>();

    /**
     * 一个FCM对应多个FCM集群 key为fcmId
     */
    private Map<Long, ConcurrentHashMap<Long, FcmBridgeCluster>> fcmInClusterMap = new ConcurrentHashMap<>();

    /**
     * <pre>构造方法</pre>
     * @author zyz
     * @since 2021-11-09 14:35  
     */
    private FcmBridgeCache()
    {
        
    }
    
    public void each(FcmBridgeProcessor fcmBridgeProcessor)
    {
    	fcmBridgeMap.forEach((k, v)->{
    		fcmBridgeProcessor.process(v);
        });
    }
    
    /**
     * 更新桥缓存
     * @author zyz
     * @since 2021-11-09 14:49 
     * @param fcmBridge void
     */
    public void update(FcmBridge fcmBridge)
    {
    	FcmBridge oldFcmBridge = fcmBridgeMap.put(fcmBridge.getBusiFreeSwitch().getId(), fcmBridge);
        if (oldFcmBridge != null)
        {
        	ipToFcmBridgeMap.remove(oldFcmBridge.getBusiFreeSwitch().getIp());
        	if (StringUtils.hasText(oldFcmBridge.getBusiFreeSwitch().getDomainName())) {
        	    domainNameToFcmBridgeMap.remove(oldFcmBridge.getBusiFreeSwitch().getDomainName());
            }
        }
        
        ipToFcmBridgeMap.put(fcmBridge.getBusiFreeSwitch().getIp(), fcmBridge);
        if (StringUtils.hasText(fcmBridge.getBusiFreeSwitch().getDomainName())) {
            domainNameToFcmBridgeMap.put(fcmBridge.getBusiFreeSwitch().getDomainName(), fcmBridge);
        }
    }
    
    /**
     * 根据ID移除FCM服务器配置
     * @author zyz
     * @since 2021-11-09 17:52 
     * @param id
     * @return FcmBridge
     */
    public FcmBridge remove(Long id)
    {
    	FcmBridge fcmBridge = fcmBridgeMap.remove(id);
        if (fcmBridge != null)
        {
        	ipToFcmBridgeMap.remove(fcmBridge.getBusiFreeSwitch().getIp());
            if (StringUtils.hasText(fcmBridge.getBusiFreeSwitch().getDomainName())) {
                domainNameToFcmBridgeMap.remove(fcmBridge.getBusiFreeSwitch().getDomainName());
            }
        }
        return fcmBridge;
    }
    
    /**
     * 根据ID获取fcm桥
     * @author zyz
     * @since 2021-11-09 16:59 
     * @param fcmId
     * @return FcmBridge
     */
    public FcmBridge getById(long fcmId)
    {
        return fcmBridgeMap.get(fcmId);
    }
    
    
    /**
     * 根据IP获取fcm桥
     * @author zyz
     * @since 2021-04-21 16:59 
     * @param ip
     * @return FsbcBridge
     */
    public FcmBridge getByIp(String ip)
    {
        return ipToFcmBridgeMap.get(ip);
    }

    /**
     * 根据域名获取fcm桥
     * @author zyz
     * @since 2021-04-21 16:59
     * @param domainName
     * @return FsbcBridge
     */
    public FcmBridge getByDomainName(String domainName)
    {
        return domainNameToFcmBridgeMap.get(domainName);
    }
    
    /**
     * <p>Get Method   :   fcmBridgeMap Map<Long,FcmBridge></p>
     * @return fcmBridgeMap
     */
    public Map<Long, FcmBridge> getFcmBridgeMap()
    {
        return fcmBridgeMap;
    }

    /**
     * <p>Get Method   :   INSTANCE FcmBridgeCache</p>
     * @return instance
     */
    public static FcmBridgeCache getInstance()
    {
        return INSTANCE;
    }
    
    public static interface FcmBridgeProcessor
    {
        void process(FcmBridge fcmBridge);
    }

    /**
     * 将freeSwitch添加到对应的freeSwitch组中（一个freeSwitch可以添加到多个freeSwitch组中，一个组可以添加多个freeSwitch，即多对多的关系）
     * @author lilinhai
     * @since 2021-03-17 15:27
     */
    public synchronized void update(BusiFreeSwitchClusterMap freeSwitchClusterMap)
    {
        FcmBridge fcmBridge = fcmBridgeMap.get(freeSwitchClusterMap.getFreeSwitchId());
        Assert.notNull(fcmBridge, "fme绑定fme集群失败，fmeBridge桥找不到：" + freeSwitchClusterMap.getFreeSwitchId());
        fcmBridge.setWeight(freeSwitchClusterMap.getWeight());
        FcmBridgeCluster fcmBridgeCluster = getByFcmClusterId(freeSwitchClusterMap.getClusterId());
        if (fcmBridgeCluster == null)
        {
            fcmBridgeCluster = new FcmBridgeCluster();
            fcmBridgeClusterMap.put(freeSwitchClusterMap.getClusterId(), fcmBridgeCluster);
        }
        fcmBridgeCluster.addFcmBridge(fcmBridge);
        ConcurrentHashMap<Long, FcmBridgeCluster> fcmInClusters = fcmInClusterMap.get(freeSwitchClusterMap.getFreeSwitchId());
        if (fcmInClusters == null) {
            fcmInClusters = new ConcurrentHashMap<>();
            fcmInClusterMap.put(freeSwitchClusterMap.getFreeSwitchId(), fcmInClusters);
        }
        fcmInClusters.put(freeSwitchClusterMap.getClusterId(), fcmBridgeCluster);
    }

    /**
     * 从freeswitch集群删除fme节点
     * @author lilinhai
     * @since 2021-03-19 18:34
     * @param freeSwitchClusterMap
     */
    public synchronized void removeFcmFromCluster(BusiFreeSwitchClusterMap freeSwitchClusterMap)
    {
        FcmBridgeCluster fcmBridgeCluster = getByFcmClusterId(freeSwitchClusterMap.getClusterId());
        if (fcmBridgeCluster != null)
        {
            FcmBridge fmeBridge = fcmBridgeMap.get(freeSwitchClusterMap.getFreeSwitchId());
            if (fmeBridge != null)
            {
                fcmBridgeCluster.remove(fmeBridge);
            }
            ConcurrentHashMap<Long, FcmBridgeCluster> fcmInClusters = fcmInClusterMap.get(freeSwitchClusterMap.getFreeSwitchId());
            if (fcmInClusters != null) {
                if (fcmInClusters.size() == 0) {
                    fcmInClusterMap.remove(freeSwitchClusterMap.getFreeSwitchId());
                }
            }
        }
    }

    /**
     * <pre>根据数据库id获取对象</pre>
     * @author lilinhai
     * @since 2020-12-29 18:05
     * @param busiFcmId
     * @return FmeHttpInvoker
     */
    public FcmBridge get(long busiFcmId)
    {
        return fcmBridgeMap.get(busiFcmId);
    }

    /**
     * <pre>根据组ID获取FreeSwitch调用器集合</pre>
     * @author lilinhai
     * @since 2021-01-28 15:25 
     * @param clusterId
     * @return FcmBridgeCluster
     */
    public FcmBridgeCluster getByFcmClusterId(long clusterId)
    {
        return fcmBridgeClusterMap.get(clusterId);
    }

    /**
     * <pre>根据ID获取会议桥组对象</pre>
     * @author lilinhai
     * @since 2021-01-04 14:31 
     * @param clusterId
     * @return 
     */
    public boolean isInUse(long clusterId)
    {
        FcmBridgeCluster fbc = getByFcmClusterId(clusterId);
        return fbc != null && !ObjectUtils.isEmpty(fbc.getFcmBridges());
    }

    /**
     * <pre>根据ID获取所在集群列表</pre>
     *
     * @param busiFcmId
     * @return
     */
    public Map<Long, FcmBridgeCluster> getFcmBridgeClusterMapByFcmId(long busiFcmId) {
        return fcmInClusterMap.get(busiFcmId);
    }
}

package com.paradisecloud.fcm.terminal.fs.cache;

import com.paradisecloud.fcm.dao.model.BusiLiveClusterMap;
import com.paradisecloud.fcm.terminal.fs.model.LiveBridge;
import com.paradisecloud.fcm.terminal.fs.model.LiveBridgeCluster;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LiveBridgeCache {

    private static final LiveBridgeCache INSTANCE = new LiveBridgeCache()
    {

    };

    /**
     * 所有liveBridge缓存，key：live桥的IP
     */
    private Map<String, LiveBridge> ipToLiveBridgeMap = new ConcurrentHashMap<>();

    /**
     * 所有liveBridge缓存，key：live桥的id
     */
    private Map<Long, LiveBridge> liveBridgeMap = new ConcurrentHashMap<>();

    /**
     * 一个组对应的Live集合映射，key：groupId
     */
    private Map<Long, LiveBridgeCluster> liveBridgeClusterMap = new ConcurrentHashMap<>();

    /**
     * 一个live对应多个live集群 key为liveId
     */
    private Map<Long, ConcurrentHashMap<Long, LiveBridgeCluster>> liveInClusterMap = new ConcurrentHashMap<>();

    /**
     * 此直播url的观看终端数
     */
    private Map<String, Integer> liveUrlCount = new ConcurrentHashMap<>();

    /**
     * 通过会议ID存储该会议直播的观看终端数
     */
    private Map<String, Integer> liveConferenceIdCount = new ConcurrentHashMap<>();

    /**
     * <pre>构造方法</pre>
     * @author zyz
     * @since 2021-11-09 14:35
     */
    private LiveBridgeCache()
    {

    }

    public void each(LiveBridgeProcessor liveBridgeProcessor)
    {
        liveBridgeMap.forEach((k, v)->{
            liveBridgeProcessor.process(v);
        });
    }

    /**
     * 更新桥缓存
     * @author zyz
     * @since 2021-11-09 14:49
     * @param liveBridge void
     */
    public void update(LiveBridge liveBridge)
    {
        LiveBridge oldliveBridge = liveBridgeMap.put(liveBridge.getBusiLive().getId(), liveBridge);
        LiveCache.getInstance().put(liveBridge.getBusiLive().getId(), liveBridge.getBusiLive());
        if (oldliveBridge != null)
        {
            ipToLiveBridgeMap.remove(oldliveBridge.getBusiLive().getIp());
        }

        ipToLiveBridgeMap.put(liveBridge.getBusiLive().getIp(), liveBridge);
    }

    /**
     * 根据ID移除live服务器配置
     * @author zyz
     * @since 2021-11-09 17:52
     * @param id
     * @return liveBridge
     */
    public LiveBridge remove(Long id)
    {
        LiveBridge liveBridge = liveBridgeMap.remove(id);
        if (liveBridge != null)
        {
            ipToLiveBridgeMap.remove(liveBridge.getBusiLive().getIp());
        }
        return liveBridge;
    }

    /**
     * 根据ID获取live桥
     * @author zyz
     * @since 2021-11-09 16:59
     * @param liveId
     * @return liveBridge
     */
    public LiveBridge getById(long liveId)
    {
        return liveBridgeMap.get(liveId);
    }


    /**
     * 根据IP获取live桥
     * @author zyz
     * @since 2021-04-21 16:59
     * @param ip
     * @return FsbcBridge
     */
    public LiveBridge getByIp(String ip)
    {
        return ipToLiveBridgeMap.get(ip);
    }

    /**
     * <p>Get Method   :   liveBridgeMap Map<Long,liveBridge></p>
     * @return liveBridgeMap
     */
    public Map<Long, LiveBridge> getLiveBridgeMap()
    {
        return liveBridgeMap;
    }

    /**
     * <p>Get Method   :   INSTANCE liveBridgeCache</p>
     * @return instance
     */
    public static LiveBridgeCache getInstance()
    {
        return INSTANCE;
    }

    public static interface LiveBridgeProcessor
    {
        void process(LiveBridge liveBridge);
    }

    /**
     * 将freeSwitch添加到对应的freeSwitch组中（一个freeSwitch可以添加到多个freeSwitch组中，一个组可以添加多个freeSwitch，即多对多的关系）
     * @author lilinhai
     * @since 2021-03-17 15:27
     */
    public synchronized void update(BusiLiveClusterMap freeSwitchClusterMap)
    {
        LiveBridge liveBridge = liveBridgeMap.get(freeSwitchClusterMap.getLiveId());
        Assert.notNull(liveBridge, "fme绑定fme集群失败，fmeBridge桥找不到：" + freeSwitchClusterMap.getLiveId());
//        liveBridge.setWeight(freeSwitchClusterMap.getWeight());
        LiveBridgeCluster liveBridgeCluster = getByliveClusterId(freeSwitchClusterMap.getClusterId());
        if (liveBridgeCluster == null)
        {
            liveBridgeCluster = new LiveBridgeCluster();
            liveBridgeClusterMap.put(freeSwitchClusterMap.getClusterId(), liveBridgeCluster);
        }
        liveBridgeCluster.addLiveBridge(liveBridge);
        ConcurrentHashMap<Long, LiveBridgeCluster> liveInClusters = liveInClusterMap.get(freeSwitchClusterMap.getLiveId());
        if (liveInClusters == null) {
            liveInClusters = new ConcurrentHashMap<>();
            liveInClusterMap.put(freeSwitchClusterMap.getLiveId(), liveInClusters);
        }
        liveInClusters.put(freeSwitchClusterMap.getClusterId(), liveBridgeCluster);
    }

    /**
     * 从freeswitch集群删除fme节点
     * @author lilinhai
     * @since 2021-03-19 18:34
     * @param freeSwitchClusterMap
     */
    public synchronized void removeliveFromCluster(BusiLiveClusterMap freeSwitchClusterMap)
    {
        LiveBridgeCluster liveBridgeCluster = getByliveClusterId(freeSwitchClusterMap.getClusterId());
        if (liveBridgeCluster != null)
        {
            LiveBridge fmeBridge = liveBridgeMap.get(freeSwitchClusterMap.getLiveId());
            if (fmeBridge != null)
            {
                liveBridgeCluster.remove(fmeBridge);
            }
            ConcurrentHashMap<Long, LiveBridgeCluster> liveInClusters = liveInClusterMap.get(freeSwitchClusterMap.getLiveId());
            if (liveInClusters != null) {
                if (liveInClusters.size() == 0) {
                    liveInClusterMap.remove(freeSwitchClusterMap.getLiveId());
                }
            }
        }
    }

    /**
     * <pre>根据数据库id获取对象</pre>
     * @author lilinhai
     * @since 2020-12-29 18:05
     * @param busiliveId
     * @return FmeHttpInvoker
     */
    public LiveBridge get(long busiliveId)
    {
        return liveBridgeMap.get(busiliveId);
    }

    /**
     * <pre>根据组ID获取FreeSwitch调用器集合</pre>
     * @author lilinhai
     * @since 2021-01-28 15:25
     * @param clusterId
     * @return liveBridgeCluster
     */
    public LiveBridgeCluster getByliveClusterId(long clusterId)
    {
        return liveBridgeClusterMap.get(clusterId);
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
        LiveBridgeCluster fbc = getByliveClusterId(clusterId);
        return fbc != null && !ObjectUtils.isEmpty(fbc.getLiveBridges());
    }

    /**
     * <pre>根据ID获取所在集群列表</pre>
     *
     * @param busiliveId
     * @return
     */
    public Map<Long, LiveBridgeCluster> getliveBridgeClusterMapByliveId(long busiliveId) {
        return liveInClusterMap.get(busiliveId);
    }

    public void setLiveUrlTerminalCount(String url, Integer count) {
        liveUrlCount.put(url, count);
    }

    public Integer getLiveUrlTerminalCount(String url) {
        int i = 0;
        if (liveUrlCount.containsKey(url)) {
            i = liveUrlCount.get(url);
        }
        return i;
    }

    public void clearLiveUrlTerminalCount() {
        liveUrlCount.clear();
    }

    public void setLiveConferenceTerminalCount(String conferenceId, Integer count) {
        liveConferenceIdCount.put(conferenceId, count);
    }

    public Integer getLiveConferenceTerminalCount(String conferenceId) {
        int i = 0;
        if (liveConferenceIdCount.containsKey(conferenceId)) {
            i = liveConferenceIdCount.get(conferenceId);
        }
        return i;
    }

    public void clearLiveConferenceTerminalCount() {
        liveConferenceIdCount.clear();
    }
}

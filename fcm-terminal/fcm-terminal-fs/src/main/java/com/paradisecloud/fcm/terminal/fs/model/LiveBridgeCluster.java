/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : LiveBridgeCluster.java
 * Package     : com.paradisecloud.Live.terminal.fs.mode
 * @author lilinhai 
 * @since 2021-03-19 14:04
 * @version  V1.0
 */
package com.paradisecloud.fcm.terminal.fs.model;

import com.paradisecloud.fcm.terminal.fs.model.LiveBridge;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>FreeSwitch桥集群</pre>
 * @author lilinhai
 * @since 2021-03-19 14:04
 * @version V1.0  
 */
public class LiveBridgeCluster
{
    
    private volatile List<LiveBridge> LiveBridges = new ArrayList<>();
    private Map<String, LiveBridge> callBridgeLiveBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 添加FME到集群
     * @author lilinhai
     * @since 2021-03-19 14:06 
     * @param LiveBridge void
     */
    public synchronized void addLiveBridge(LiveBridge LiveBridge)
    {
        if (!LiveBridges.contains(LiveBridge))
        {
            this.LiveBridges.add(LiveBridge);
            LiveBridge.setLiveBridgeCluster(this);
        }
    }

    /**
     * <p>Get Method   :   masterLiveBridge LiveBridge</p>
     * @return masterLiveBridge
     */
    public LiveBridge getMasterLiveBridge()
    {

        return LiveBridges.get(0);
    }
    
    public LiveBridge getByCallBridge(String callBridgeId)
    {
        return callBridgeLiveBridgeMap.get(callBridgeId);
    }
    
    public synchronized boolean remove(LiveBridge LiveBridge)
    {
        boolean flag = LiveBridges.remove(LiveBridge);
        return flag;
    }
    
    /**
     * <p>Get Method   :   LiveBridges List<LiveBridge></p>
     * @return LiveBridges
     */
    public List<LiveBridge> getLiveBridges()
    {
        return LiveBridges;
    }
    
    public List<LiveBridge> getAvailableLiveBridges()
    {
        List<LiveBridge> availableLiveBridges = new ArrayList<>();
        for (Iterator<LiveBridge> iterator = LiveBridges.iterator(); iterator.hasNext();)
        {
            LiveBridge LiveBridge = iterator.next();

                availableLiveBridges.add(LiveBridge);

        }
        return availableLiveBridges;
    }

}

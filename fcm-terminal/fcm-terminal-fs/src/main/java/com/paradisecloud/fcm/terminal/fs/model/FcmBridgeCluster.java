/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FcmBridgeCluster.java
 * Package     : com.paradisecloud.fcm.terminal.fs.mode
 * @author lilinhai 
 * @since 2021-03-19 14:04
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.fs.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>FreeSwitch桥集群</pre>
 * @author lilinhai
 * @since 2021-03-19 14:04
 * @version V1.0  
 */
public class FcmBridgeCluster
{
    
    private volatile List<FcmBridge> fcmBridges = new ArrayList<>();
    private Map<String, FcmBridge> callBridgeFcmBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 添加FME到集群
     * @author lilinhai
     * @since 2021-03-19 14:06 
     * @param fcmBridge void
     */
    public synchronized void addFcmBridge(FcmBridge fcmBridge)
    {
        if (!fcmBridges.contains(fcmBridge))
        {
            this.fcmBridges.add(fcmBridge);
            fcmBridge.setFcmBridgeCluster(this);
        }
    }

    /**
     * <p>Get Method   :   masterFcmBridge FcmBridge</p>
     * @return masterFcmBridge
     */
    public FcmBridge getMasterFcmBridge()
    {
        Collections.sort(getAvailableFcmBridges(), new Comparator<FcmBridge>()
        {
            @Override
            public int compare(FcmBridge o1, FcmBridge o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        
        return fcmBridges.get(0);
    }
    
    public FcmBridge getByCallBridge(String callBridgeId)
    {
        return callBridgeFcmBridgeMap.get(callBridgeId);
    }
    
    public synchronized boolean remove(FcmBridge fcmBridge)
    {
        boolean flag = fcmBridges.remove(fcmBridge);
        return flag;
    }
    
    /**
     * <p>Get Method   :   fcmBridges List<FcmBridge></p>
     * @return fcmBridges
     */
    public List<FcmBridge> getFcmBridges()
    {
        return fcmBridges;
    }
    
    public List<FcmBridge> getAvailableFcmBridges()
    {
        List<FcmBridge> availableFcmBridges = new ArrayList<>();
        for (Iterator<FcmBridge> iterator = fcmBridges.iterator(); iterator.hasNext();)
        {
            FcmBridge fcmBridge = iterator.next();
            if (fcmBridge.isAvailable())
            {
                availableFcmBridges.add(fcmBridge);
            }
        }
        return availableFcmBridges;
    }

}

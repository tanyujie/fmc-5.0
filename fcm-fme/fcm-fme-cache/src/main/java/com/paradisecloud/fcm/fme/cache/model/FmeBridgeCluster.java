/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCluster.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author lilinhai 
 * @since 2021-03-19 14:04
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;

import com.paradisecloud.fcm.common.enumer.FmeType;

/**  
 * <pre>fme桥集群</pre>
 * @author lilinhai
 * @since 2021-03-19 14:04
 * @version V1.0  
 */
public class FmeBridgeCluster
{
    
    private volatile List<FmeBridge> fmeBridges = new ArrayList<>();
    private FmeType spareFmeType;
    private Long spareFmeId;
    private Map<String, FmeBridge> callBridgeFmeBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 添加FME到集群
     * @author lilinhai
     * @since 2021-03-19 14:06 
     * @param fmeBridge void
     */
    public synchronized void addFmeBridge(FmeBridge fmeBridge)
    {
        if (!fmeBridges.contains(fmeBridge))
        {
            this.fmeBridges.add(fmeBridge);
            fmeBridge.setFmeBridgeCluster(this);
            registerCallBridge(fmeBridge);
        }
    }
    
    public synchronized void registerCallBridge(FmeBridge fmeBridge)
    {
        if (fmeBridge.getCallBridgeId() != null)
        {
            Assert.isTrue(fmeBridges.contains(fmeBridge), "非法集群节点【" + fmeBridge.getBridgeAddress() + "】，请检查！");
            FmeBridge old = callBridgeFmeBridgeMap.put(fmeBridge.getCallBridgeId(), fmeBridge);
            if (!(old == null || old == fmeBridge))
            {
                fmeBridge.getFmeLogger().logInfo("websocket断开过，重复注册CallBridgeId【" + fmeBridge.getCallBridgeId() + "】的FME节点：" + old + "----" + fmeBridge, true, true);
            }
            else
            {
                fmeBridge.getFmeLogger().logInfo("注册FME集群CallBridge信息成功: " + fmeBridge.getCallBridgeId(), true, false);
            }
        }
    }

    /**
     * <p>Get Method   :   masterFmeBridge FmeBridge</p>
     * @return masterFmeBridge
     */
    public FmeBridge getMasterFmeBridge()
    {
        Collections.sort(getAvailableFmeBridges(), new Comparator<FmeBridge>()
        {
            @Override
            public int compare(FmeBridge o1, FmeBridge o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        
        return fmeBridges.get(0);
    }
    
    public FmeBridge getByCallBridge(String callBridgeId)
    {
        return callBridgeFmeBridgeMap.get(callBridgeId);
    }
    
    public synchronized boolean remove(FmeBridge fmeBridge)
    {
        boolean flag = fmeBridges.remove(fmeBridge);
        if (flag)
        {
            fmeBridge.setFmeBridgeCluster(null);
            if (fmeBridge.getCallBridgeId() != null)
            {
                callBridgeFmeBridgeMap.remove(fmeBridge.getCallBridgeId());
            }
        }
        return flag;
    }
    
    /**
     * <p>Get Method   :   fmeBridges List<FmeBridge></p>
     * @return fmeBridges
     */
    public List<FmeBridge> getFmeBridges()
    {
        return fmeBridges;
    }
    
    public List<FmeBridge> getAvailableFmeBridges()
    {
        List<FmeBridge> availableFmeBridges = new ArrayList<>();
        for (Iterator<FmeBridge> iterator = fmeBridges.iterator(); iterator.hasNext();)
        {
            FmeBridge fmeBridge = iterator.next();
            if (fmeBridge.isAvailable())
            {
                availableFmeBridges.add(fmeBridge);
            }
        }
        return availableFmeBridges;
    } 

    /**
     * <p>Get Method   :   spareFmeType FmeType</p>
     * @return spareFmeType
     */
    public FmeType getSpareFmeType()
    {
        return spareFmeType;
    }

    /**
     * <p>Set Method   :   spareFmeType FmeType</p>
     * @param spareFmeType
     */
    public void setSpareFmeType(FmeType spareFmeType)
    {
        this.spareFmeType = spareFmeType;
    }

    /**
     * <p>Get Method   :   spareFmeId Long</p>
     * @return spareFmeId
     */
    public Long getSpareFmeId()
    {
        return spareFmeId;
    }

    /**
     * <p>Set Method   :   spareFmeId Long</p>
     * @param spareFmeId
     */
    public void setSpareFmeId(Long spareFmeId)
    {
        this.spareFmeId = spareFmeId;
    }
}

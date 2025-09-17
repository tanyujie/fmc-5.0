/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCluster.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author lilinhai 
 * @since 2021-02-19 14:04
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.tencent.cache;

import com.paradisecloud.fcm.common.enumer.FmeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>fme桥集群</pre>
 * @author lilinhai
 * @since 2021-02-19 14:04
 * @version V1.0  
 */
@Slf4j
public class TencentBridgeCluster
{
    
    private volatile List<TencentBridge> fmeBridges = new ArrayList<>();
    private FmeType spareFmeType;
    private Long spareFmeId;
    private Map<String, TencentBridge> callBridgeFmeBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 添加FME到集群
     * @author lilinhai
     * @since 2021-02-19 14:06 
     * @param fmeBridge void
     */
    public synchronized void addFmeBridge(TencentBridge fmeBridge)
    {
        if (!fmeBridges.contains(fmeBridge))
        {
            this.fmeBridges.add(fmeBridge);
            fmeBridge.setTencentBridgeCluster(this);
            registerCallBridge(fmeBridge);
        }
    }
    
    public synchronized void registerCallBridge(TencentBridge fmeBridge)
    {
        Assert.isTrue(fmeBridges.contains(fmeBridge), "非法集群节点【" + fmeBridge.getBusiTencent().getAppId() + "】，请检查！");
        TencentBridge old = callBridgeFmeBridgeMap.put(fmeBridge.getBusiTencent().getAppId()+"", fmeBridge);
        if (!(old == null || old == fmeBridge))
        {
           log.info("websocket断开过，重复注册CallBridgeId【" + fmeBridge.getBusiTencent().getAppId() + "】的Tencent节点：" + old + "----" + fmeBridge, true, true);
        }
        else
        {
            log.info("注册Tencent集群CallBridge信息成功: " +fmeBridge.getBusiTencent().getAppId(), true, false);
        }
    }

    /**
     * <p>Get Method   :   masterFmeBridge FmeBridge</p>
     * @return masterFmeBridge
     */
    public TencentBridge getMasterFmeBridge()
    {
        Collections.sort(getAvailableFmeBridges(), new Comparator<TencentBridge>()
        {
            @Override
            public int compare(TencentBridge o1, TencentBridge o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        
        return fmeBridges.get(0);
    }
    
    public TencentBridge getByCallBridge(String callBridgeId)
    {
        return callBridgeFmeBridgeMap.get(callBridgeId);
    }
    
    public synchronized boolean remove(TencentBridge fmeBridge)
    {
        boolean flag = fmeBridges.remove(fmeBridge);
        if (flag)
        {
            fmeBridge.setTencentBridgeCluster(null);
            callBridgeFmeBridgeMap.remove(fmeBridge.getBusiTencent().getAppId());
        }
        return flag;
    }
    
    /**
     * <p>Get Method   :   fmeBridges List<FmeBridge></p>
     * @return fmeBridges
     */
    public List<TencentBridge> getFmeBridges()
    {
        return fmeBridges;
    }
    
    public List<TencentBridge> getAvailableFmeBridges()
    {
        List<TencentBridge> availableFmeBridges = new ArrayList<>();
        for (Iterator<TencentBridge> iterator = fmeBridges.iterator(); iterator.hasNext();)
        {
            TencentBridge fmeBridge = iterator.next();
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

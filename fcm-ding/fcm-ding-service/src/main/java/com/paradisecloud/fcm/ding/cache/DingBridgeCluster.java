/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCluster.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author lilinhai 
 * @since 2021-02-19 14:04
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.ding.cache;

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
public class DingBridgeCluster
{
    
    private volatile List<DingBridge> fmeBridges = new ArrayList<>();
    private FmeType spareFmeType;
    private Long spareFmeId;
    private Map<String, DingBridge> callBridgeFmeBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 添加FME到集群
     * @author lilinhai
     * @since 2021-02-19 14:06 
     * @param fmeBridge void
     */
    public synchronized void addFmeBridge(DingBridge fmeBridge)
    {
        if (!fmeBridges.contains(fmeBridge))
        {
            this.fmeBridges.add(fmeBridge);
            fmeBridge.setDingBridgeCluster(this);
            registerCallBridge(fmeBridge);
        }
    }
    
    public synchronized void registerCallBridge(DingBridge fmeBridge)
    {
        Assert.isTrue(fmeBridges.contains(fmeBridge), "非法集群节点【" + fmeBridge.getBusiDing().getAppId() + "】，请检查！");
        DingBridge old = callBridgeFmeBridgeMap.put(fmeBridge.getBusiDing().getAppId()+"", fmeBridge);
        if (!(old == null || old == fmeBridge))
        {
           log.info("websocket断开过，重复注册CallBridgeId【" + fmeBridge.getBusiDing().getAppId() + "】的Ding节点：" + old + "----" + fmeBridge, true, true);
        }
        else
        {
            log.info("注册Ding集群CallBridge信息成功: " +fmeBridge.getBusiDing().getAppId(), true, false);
        }
    }

    /**
     * <p>Get Method   :   masterFmeBridge FmeBridge</p>
     * @return masterFmeBridge
     */
    public DingBridge getMasterFmeBridge()
    {
        Collections.sort(getAvailableFmeBridges(), new Comparator<DingBridge>()
        {
            @Override
            public int compare(DingBridge o1, DingBridge o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        
        return fmeBridges.get(0);
    }
    
    public DingBridge getByCallBridge(String callBridgeId)
    {
        return callBridgeFmeBridgeMap.get(callBridgeId);
    }
    
    public synchronized boolean remove(DingBridge fmeBridge)
    {
        boolean flag = fmeBridges.remove(fmeBridge);
        if (flag)
        {
            fmeBridge.setDingBridgeCluster(null);
            callBridgeFmeBridgeMap.remove(fmeBridge.getBusiDing().getAppId());
        }
        return flag;
    }
    
    /**
     * <p>Get Method   :   fmeBridges List<FmeBridge></p>
     * @return fmeBridges
     */
    public List<DingBridge> getFmeBridges()
    {
        return fmeBridges;
    }
    
    public List<DingBridge> getAvailableFmeBridges()
    {
        List<DingBridge> availableFmeBridges = new ArrayList<>();
        for (Iterator<DingBridge> iterator = fmeBridges.iterator(); iterator.hasNext();)
        {
            DingBridge fmeBridge = iterator.next();
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

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCluster.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author lilinhai 
 * @since 2021-02-19 14:04
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smc2.cache;

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
public class Smc2BridgeCluster
{
    
    private volatile List<Smc2Bridge> fmeBridges = new ArrayList<>();
    private FmeType spareFmeType;
    private Long spareFmeId;
    private Map<String, Smc2Bridge> callBridgeFmeBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 添加FME到集群
     * @author lilinhai
     * @since 2021-02-19 14:06 
     * @param fmeBridge void
     */
    public synchronized void addFmeBridge(Smc2Bridge fmeBridge)
    {
        if (!fmeBridges.contains(fmeBridge))
        {
            this.fmeBridges.add(fmeBridge);
            fmeBridge.setSmc2BridgeCluster(this);
            registerCallBridge(fmeBridge);
        }
    }
    
    public synchronized void registerCallBridge(Smc2Bridge fmeBridge)
    {
        Assert.isTrue(fmeBridges.contains(fmeBridge), "非法集群节点【" + fmeBridge.getBusiSmc2().getIp() + "】，请检查！");
        Smc2Bridge old = callBridgeFmeBridgeMap.put(fmeBridge.getBusiSmc2().getIp()+"", fmeBridge);
        if (!(old == null || old == fmeBridge))
        {
           log.info("websocket断开过，重复注册CallBridgeId【" + fmeBridge.getBusiSmc2().getIp() + "】的smc2节点：" + old + "----" + fmeBridge, true, true);
        }
        else
        {
            log.info("注册SMC2集群CallBridge信息成功: " +fmeBridge.getBusiSmc2().getIp(), true, false);
        }
    }

    /**
     * <p>Get Method   :   masterFmeBridge FmeBridge</p>
     * @return masterFmeBridge
     */
    public Smc2Bridge getMasterFmeBridge()
    {
        Collections.sort(getAvailableFmeBridges(), new Comparator<Smc2Bridge>()
        {
            @Override
            public int compare(Smc2Bridge o1, Smc2Bridge o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        
        return fmeBridges.get(0);
    }
    
    public Smc2Bridge getByCallBridge(String callBridgeId)
    {
        return callBridgeFmeBridgeMap.get(callBridgeId);
    }
    
    public synchronized boolean remove(Smc2Bridge fmeBridge)
    {
        boolean flag = fmeBridges.remove(fmeBridge);
        if (flag)
        {
            fmeBridge.setSmc2BridgeCluster(null);
            callBridgeFmeBridgeMap.remove(fmeBridge.getBusiSmc2().getIp());
        }
        return flag;
    }
    
    /**
     * <p>Get Method   :   fmeBridges List<FmeBridge></p>
     * @return fmeBridges
     */
    public List<Smc2Bridge> getFmeBridges()
    {
        return fmeBridges;
    }
    
    public List<Smc2Bridge> getAvailableFmeBridges()
    {
        List<Smc2Bridge> availableFmeBridges = new ArrayList<>();
        for (Iterator<Smc2Bridge> iterator = fmeBridges.iterator(); iterator.hasNext();)
        {
            Smc2Bridge fmeBridge = iterator.next();
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

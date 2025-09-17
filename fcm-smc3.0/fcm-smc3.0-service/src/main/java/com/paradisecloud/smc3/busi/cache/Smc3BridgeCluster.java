/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCluster.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author lilinhai 
 * @since 2021-03-19 14:04
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.cache;

import com.paradisecloud.fcm.common.enumer.FmeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>fme桥集群</pre>
 * @author lilinhai
 * @since 2021-03-19 14:04
 * @version V1.0  
 */
@Slf4j
public class Smc3BridgeCluster
{
    
    private volatile List<Smc3Bridge> fmeBridges = new ArrayList<>();
    private FmeType spareFmeType;
    private Long spareFmeId;
    private Map<String, Smc3Bridge> callBridgeFmeBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 添加FME到集群
     * @author lilinhai
     * @since 2021-03-19 14:06 
     * @param fmeBridge void
     */
    public synchronized void addFmeBridge(Smc3Bridge fmeBridge)
    {
        if (!fmeBridges.contains(fmeBridge))
        {
            this.fmeBridges.add(fmeBridge);
            fmeBridge.setSmc3BridgeCluster(this);
            registerCallBridge(fmeBridge);
        }
    }
    
    public synchronized void registerCallBridge(Smc3Bridge fmeBridge)
    {
        Assert.isTrue(fmeBridges.contains(fmeBridge), "非法集群节点【" + fmeBridge.getBusiSMC().getIp() + "】，请检查！");
        Smc3Bridge old = callBridgeFmeBridgeMap.put(fmeBridge.getBusiSMC().getIp()+"", fmeBridge);
        if (!(old == null || old == fmeBridge))
        {
           log.info("websocket断开过，重复注册CallBridgeId【" + fmeBridge.getBusiSMC().getIp() + "】的smc3节点：" + old + "----" + fmeBridge, true, true);
        }
        else
        {
            log.info("注册SMC3集群CallBridge信息成功: " +fmeBridge.getBusiSMC().getIp(), true, false);
        }
    }

    /**
     * <p>Get Method   :   masterFmeBridge FmeBridge</p>
     * @return masterFmeBridge
     */
    public Smc3Bridge getMasterFmeBridge()
    {
        Collections.sort(getAvailableFmeBridges(), new Comparator<Smc3Bridge>()
        {
            @Override
            public int compare(Smc3Bridge o1, Smc3Bridge o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        
        return fmeBridges.get(0);
    }
    
    public Smc3Bridge getByCallBridge(String callBridgeId)
    {
        return callBridgeFmeBridgeMap.get(callBridgeId);
    }
    
    public synchronized boolean remove(Smc3Bridge fmeBridge)
    {
        boolean flag = fmeBridges.remove(fmeBridge);
        if (flag)
        {
            fmeBridge.setSmc3BridgeCluster(null);
            callBridgeFmeBridgeMap.remove(fmeBridge.getBusiSMC().getIp());
        }
        return flag;
    }
    
    /**
     * <p>Get Method   :   fmeBridges List<FmeBridge></p>
     * @return fmeBridges
     */
    public List<Smc3Bridge> getFmeBridges()
    {
        return fmeBridges;
    }
    
    public List<Smc3Bridge> getAvailableFmeBridges()
    {
        List<Smc3Bridge> availableFmeBridges = new ArrayList<>();
        for (Iterator<Smc3Bridge> iterator = fmeBridges.iterator(); iterator.hasNext();)
        {
            Smc3Bridge fmeBridge = iterator.next();
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

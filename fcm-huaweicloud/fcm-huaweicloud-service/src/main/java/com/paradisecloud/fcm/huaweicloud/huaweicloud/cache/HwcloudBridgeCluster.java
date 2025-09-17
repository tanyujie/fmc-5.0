/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCluster.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author lilinhai 
 * @since 2021-02-19 14:04
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.huaweicloud.huaweicloud.cache;

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
public class HwcloudBridgeCluster
{
    
    private volatile List<HwcloudBridge> fmeBridges = new ArrayList<>();
    private FmeType spareFmeType;
    private Long spareFmeId;
    private Map<String, HwcloudBridge> callBridgeFmeBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 添加FME到集群
     * @author lilinhai
     * @since 2021-02-19 14:06 
     * @param fmeBridge void
     */
    public synchronized void addFmeBridge(HwcloudBridge fmeBridge)
    {
        if (!fmeBridges.contains(fmeBridge))
        {
            this.fmeBridges.add(fmeBridge);
            fmeBridge.setHwcloudBridgeCluster(this);
            registerCallBridge(fmeBridge);
        }
    }
    
    public synchronized void registerCallBridge(HwcloudBridge fmeBridge)
    {
        Assert.isTrue(fmeBridges.contains(fmeBridge), "非法集群节点【" + fmeBridge.getBusiHwcloud().getAppId() + "】，请检查！");
        HwcloudBridge old = callBridgeFmeBridgeMap.put(fmeBridge.getBusiHwcloud().getAppId()+"", fmeBridge);
        if (!(old == null || old == fmeBridge))
        {
           log.info("websocket断开过，重复注册CallBridgeId【" + fmeBridge.getBusiHwcloud().getAppId() + "】的Hwcloud节点：" + old + "----" + fmeBridge, true, true);
        }
        else
        {
            log.info("注册Hwcloud集群CallBridge信息成功: " +fmeBridge.getBusiHwcloud().getAppId(), true, false);
        }
    }

    /**
     * <p>Get Method   :   masterFmeBridge FmeBridge</p>
     * @return masterFmeBridge
     */
    public HwcloudBridge getMasterFmeBridge()
    {
        Collections.sort(getAvailableFmeBridges(), new Comparator<HwcloudBridge>()
        {
            @Override
            public int compare(HwcloudBridge o1, HwcloudBridge o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        
        return fmeBridges.get(0);
    }
    
    public HwcloudBridge getByCallBridge(String callBridgeId)
    {
        return callBridgeFmeBridgeMap.get(callBridgeId);
    }
    
    public synchronized boolean remove(HwcloudBridge fmeBridge)
    {
        boolean flag = fmeBridges.remove(fmeBridge);
        if (flag)
        {
            fmeBridge.setHwcloudBridgeCluster(null);
            callBridgeFmeBridgeMap.remove(fmeBridge.getBusiHwcloud().getAppId());
        }
        return flag;
    }
    
    /**
     * <p>Get Method   :   fmeBridges List<FmeBridge></p>
     * @return fmeBridges
     */
    public List<HwcloudBridge> getFmeBridges()
    {
        return fmeBridges;
    }
    
    public List<HwcloudBridge> getAvailableFmeBridges()
    {
        List<HwcloudBridge> availableFmeBridges = new ArrayList<>();
        for (Iterator<HwcloudBridge> iterator = fmeBridges.iterator(); iterator.hasNext();)
        {
            HwcloudBridge fmeBridge = iterator.next();
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

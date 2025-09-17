/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FsbcBridgeCache.java
 * Package     : com.paradisecloud.fcm.terminal.fsbc.model
 * @author lilinhai 
 * @since 2021-04-21 14:35
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.fsbc.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import org.springframework.util.StringUtils;

/**  
 * <pre>FSBC桥缓存</pre>
 * @author lilinhai
 * @since 2021-04-21 14:35
 * @version V1.0  
 */
public abstract class FsbcBridgeCache
{
    
    private static final FsbcBridgeCache INSTANCE = new FsbcBridgeCache()
    {
        
    };
    
    /**
     * 所有FsbcBridge缓存，key：FSBC桥的IP
     */
    private Map<String, FsbcBridge> ipToFsbcBridgeMap = new ConcurrentHashMap<>();

    /**
     * 所有FsbcBridge缓存，key：FSBC桥的域名
     */
    private Map<String, FsbcBridge> domainNameToFsbcBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 所有FsbcBridge缓存，key：FSBC桥的id
     */
    private Map<Long, FsbcBridge> fsbcBridgeMap = new ConcurrentHashMap<>();

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-04-21 14:35  
     */
    private FsbcBridgeCache()
    {
        
    }
    
    public void each(FsbcBridgeProcessor fsbcBridgeProcessor)
    {
        fsbcBridgeMap.forEach((k, v)->{
            fsbcBridgeProcessor.process(v);
        });
    }
    
    /**
     * 更新桥缓存
     * @author lilinhai
     * @since 2021-04-21 14:49 
     * @param fsbcBridge void
     */
    public void update(FsbcBridge fsbcBridge)
    {
        FsbcBridge oldFsbcBridge = fsbcBridgeMap.put(fsbcBridge.getBusiFsbcRegistrationServer().getId(), fsbcBridge);
        if (oldFsbcBridge != null)
        {
            ipToFsbcBridgeMap.remove(oldFsbcBridge.getBusiFsbcRegistrationServer().getCallIp());
            if (StringUtils.hasText(oldFsbcBridge.getBusiFsbcRegistrationServer().getDomainName())) {
                domainNameToFsbcBridgeMap.remove(oldFsbcBridge.getBusiFsbcRegistrationServer().getDomainName());
            }
        }
        
        ipToFsbcBridgeMap.put(fsbcBridge.getBusiFsbcRegistrationServer().getCallIp(), fsbcBridge);
        if (StringUtils.hasText(fsbcBridge.getBusiFsbcRegistrationServer().getDomainName())) {
            domainNameToFsbcBridgeMap.put(fsbcBridge.getBusiFsbcRegistrationServer().getDomainName(), fsbcBridge);
        }
    }
    
    /**
     * 根据ID移除FSBC服务器配置
     * @author sinhy
     * @since 2021-06-07 17:52 
     * @param id
     * @return FsbcBridge
     */
    public FsbcBridge remove(Long id)
    {
        FsbcBridge fsbcBridge = fsbcBridgeMap.remove(id);
        if (fsbcBridge != null)
        {
            ipToFsbcBridgeMap.remove(fsbcBridge.getBusiFsbcRegistrationServer().getCallIp());
            if (StringUtils.hasText(fsbcBridge.getBusiFsbcRegistrationServer().getDomainName())) {
                domainNameToFsbcBridgeMap.remove(fsbcBridge.getBusiFsbcRegistrationServer().getDomainName());
            }
        }
        return fsbcBridge;
    }
    
    /**
     * 根据ID获取fsbc桥
     * @author lilinhai
     * @since 2021-04-21 16:59 
     * @param fsbcId
     * @return FsbcBridge
     */
    public FsbcBridge getById(long fsbcId)
    {
        return fsbcBridgeMap.get(fsbcId);
    }
    
    /**
     * 根据IP获取fsbc桥
     * @author lilinhai
     * @since 2021-04-21 16:59 
     * @param fsbcIp
     * @return FsbcBridge
     */
    public FsbcBridge getByIp(String fsbcIp)
    {
        return ipToFsbcBridgeMap.get(fsbcIp);
    }

    /**
     * 根据IP获取fsbc桥
     * @author lilinhai
     * @since 2021-04-21 16:59
     * @param domainName
     * @return FsbcBridge
     */
    public FsbcBridge getByDomainName(String domainName)
    {
        return domainNameToFsbcBridgeMap.get(domainName);
    }
    
    /**
     * <p>Get Method   :   fsbcBridgeMap Map<Long,FsbcBridge></p>
     * @return fsbcBridgeMap
     */
    public Map<Long, FsbcBridge> getFsbcBridgeMap()
    {
        return fsbcBridgeMap;
    }

    /**
     * <p>Get Method   :   INSTANCE FsbcBridgeCache</p>
     * @return instance
     */
    public static FsbcBridgeCache getInstance()
    {
        return INSTANCE;
    }
    
    public static interface FsbcBridgeProcessor
    {
        void process(FsbcBridge fsbcBridge);
    }
}

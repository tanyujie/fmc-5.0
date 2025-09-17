package com.paradisecloud.fcm.telep.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2022/10/14 15:25
 */
public class TeleBridgeCache {

    private static final TeleBridgeCache INSTANCE = new TeleBridgeCache() {
    };


    private Map<String, TelepBridge> ipToTeleBridgeMap = new ConcurrentHashMap<>();

    private Map<Long, TelepBridge> idToTeleBridgeMap = new ConcurrentHashMap<>();

    public synchronized void update(TelepBridge telepBridge) {
        // 添加ID映射
        idToTeleBridgeMap.put(telepBridge.getBusiTele().getId(), telepBridge);
        // 添加IP映射
        ipToTeleBridgeMap.put(telepBridge.getBusiTele().getIp(), telepBridge);
    }

    public static TeleBridgeCache getInstance() {
        return INSTANCE;
    }

    public Map<String, TelepBridge> getIpToTeleBridgeMap() {
        return ipToTeleBridgeMap;
    }

    public Map<Long, TelepBridge> getIdToTeleBridgeMap() {
        return idToTeleBridgeMap;
    }
}

package com.paradisecloud.fcm.wvp.gb28181;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class WvpBridgeCache {

    private static final WvpBridgeCache INSTANCE = new WvpBridgeCache() {
    };
    private final Map<String, WvpBridge> wvpBridgeMap = new ConcurrentHashMap<>();

    private WvpBridgeCache() {
    }

    public static WvpBridgeCache getInstance() {
        return INSTANCE;
    }

    public synchronized void update(WvpBridge wvpBridge) {
        wvpBridgeMap.put("wvp", wvpBridge);
    }
    public WvpBridge  get() {
       return wvpBridgeMap.get("wvp");
    }

}

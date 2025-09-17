package com.paradisecloud.smc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2023/3/8 9:28
 */
public class SmcWebsocketContext {

    private static Map<String, SMCWebsocketClient> smcWebsocketClientMap = new ConcurrentHashMap<>();

    private static final SmcWebsocketContext INSTANC = new SmcWebsocketContext();

    public void put(String smcBridgeIp, SMCWebsocketClient smcWebsocketClient) {
        smcWebsocketClientMap.put(smcBridgeIp, smcWebsocketClient);
    }

    public static SmcWebsocketContext getInstance(){
        return INSTANC;
    }

    public static Map<String, SMCWebsocketClient> getSmcWebsocketClientMap() {
        return smcWebsocketClientMap;
    }
}

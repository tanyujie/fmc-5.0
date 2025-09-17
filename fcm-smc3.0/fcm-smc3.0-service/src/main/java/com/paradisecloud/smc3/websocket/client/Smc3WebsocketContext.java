package com.paradisecloud.smc3.websocket.client;

import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2023/3/8 9:28
 */
public class Smc3WebsocketContext {

    private static Map<String, SMC3WebsocketClient> smcWebsocketClientMap = new ConcurrentHashMap<>();

    private static Map<String, String> conferenceTokenId = new ConcurrentHashMap<>();

    private static final Smc3WebsocketContext INSTANC = new Smc3WebsocketContext();

    public void put(String smcBridgeIp, SMC3WebsocketClient smc3WebsocketClient) {
        smcWebsocketClientMap.put(smcBridgeIp, smc3WebsocketClient);
    }



    public static Smc3WebsocketContext getInstance(){
        return INSTANC;
    }

    public static Map<String, SMC3WebsocketClient> getSmcWebsocketClientMap() {
        return smcWebsocketClientMap;
    }

    public static String getConferenceTokenId(String conferenceId) {
        return conferenceTokenId.get(conferenceId);
    }

    public static void setConferenceTokenId(String conferenceId,String conferenceToken) {
        if(Strings.isBlank(conferenceId)||Strings.isBlank(conferenceToken)){
            return;
        }
        conferenceTokenId.put(conferenceId,conferenceToken);
    }
}

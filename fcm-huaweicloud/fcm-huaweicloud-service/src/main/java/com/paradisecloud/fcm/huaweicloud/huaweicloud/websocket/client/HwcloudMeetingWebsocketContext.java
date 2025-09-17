package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2023/3/8 9:28
 */
public class HwcloudMeetingWebsocketContext {

    private static Map<String, HwcloudMeetingWebsocketClient> smcWebsocketClientMap = new ConcurrentHashMap<>();


    private static final HwcloudMeetingWebsocketContext INSTANC = new HwcloudMeetingWebsocketContext();

    public void put(String confId, HwcloudMeetingWebsocketClient hwcloudMeetingWebsocketClient) {
        smcWebsocketClientMap.put(confId, hwcloudMeetingWebsocketClient);
    }



    public static HwcloudMeetingWebsocketContext getInstance(){
        return INSTANC;
    }

    public static Map<String, HwcloudMeetingWebsocketClient> getSmcWebsocketClientMap() {
        return smcWebsocketClientMap;
    }


}

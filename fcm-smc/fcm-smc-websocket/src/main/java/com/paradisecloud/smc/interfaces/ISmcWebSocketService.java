package com.paradisecloud.smc.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.smc.SmcWebSocketProcessor;

/**
 * @author nj
 * @date 2023/3/1 14:41
 */
public interface ISmcWebSocketService {

    void process(JSONObject json, SmcBridge smcBridge);
    void process(JSONObject json, SmcBridge smcBridge, SmcWebSocketProcessor smcWebSocketProcessor);
}
